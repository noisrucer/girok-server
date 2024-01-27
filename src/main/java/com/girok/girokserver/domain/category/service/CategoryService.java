package com.girok.girokserver.domain.category.service;

import com.girok.girokserver.core.exception.CustomException;
import com.girok.girokserver.core.exception.ErrorInfo;
import com.girok.girokserver.domain.category.entity.Category;
import com.girok.girokserver.domain.category.repository.CategoryRepository;
import com.girok.girokserver.domain.category.vo.CategoryPath;
import com.girok.girokserver.domain.member.entity.Member;
import com.girok.girokserver.global.enums.CategoryColor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.girok.girokserver.core.exception.ErrorInfo.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public Category createCategory(Member member, CategoryColor color, CategoryPath categoryPath) {
        // ["A", "B", "C"]
        CategoryPath parentCategoryPath = categoryPath.getParentCategoryPath(); // ["A", "B"]
        String newCategoryName = categoryPath.getLastCategoryName(); // "C"

        // Get parent category. ex. ["A", "B"]
        Category parentCategory = getCategoryByPath(parentCategoryPath);

        // Ensure parent category does not have the duplicating child category
        ensureNoDuplicateChildren(parentCategory, newCategoryName);

        Category category = Category.createCategory(member, parentCategory, newCategoryName, color);
        categoryRepository.save(category);
        return category;
    }

    /**
     * Get category by category path.
     * ex) If the category path is ["A", "B", "C"], it will retrieve the category "A/B/C"
     * @param categoryPath Category path.
     * @return The target category. It can be nullable.
     */
    public Category getCategoryByPath(CategoryPath categoryPath) {
        // ["A", "B", "C"]
        Category parentCategory = null;
        StringBuilder parentCategoryPathStringBuilder = new StringBuilder("/");
        for (String categoryName : categoryPath.getPath()) {
            Category category = categoryRepository.findByParentAndName(parentCategory, categoryName)
                    .orElseThrow(() -> new CustomException(
                            PARENT_CATEGORY_NOT_EXIST,
                            "Parent category " + "'" + parentCategoryPathStringBuilder.toString() + "'" + " does not have child category " + "'" + categoryName + "'"
                    ));

            parentCategory = category;
            parentCategoryPathStringBuilder.append(category.getName()).append("/");
        }

        return parentCategory; // last category ("C")
    }

    public void ensureNoDuplicateChildren(Category parentCategory, String name) {
        Optional<Category> optionalCategory = categoryRepository.findByParentAndName(parentCategory, name);
        if (optionalCategory.isPresent()) {
            throw new CustomException(DUPLICATE_CATEGORY);
        }
    }
}
