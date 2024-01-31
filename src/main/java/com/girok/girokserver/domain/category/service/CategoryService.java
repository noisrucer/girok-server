package com.girok.girokserver.domain.category.service;

import com.girok.girokserver.core.exception.CustomException;
import com.girok.girokserver.domain.category.entity.Category;
import com.girok.girokserver.domain.category.facade.dto.CategoryUpdateDto;
import com.girok.girokserver.domain.category.repository.CategoryRepository;
import com.girok.girokserver.domain.category.vo.CategoryPath;
import com.girok.girokserver.domain.member.entity.Member;
import com.girok.girokserver.global.enums.CategoryColor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.girok.girokserver.core.exception.ErrorInfo.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category getCategoryByMemberAndId(Member member, Long id) {
        return categoryRepository.findByMemberAndId(member, id)
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND));
    }

    public List<Category> getCategoriesAsTree(Member member) {
        // TODO: FETCH JOIN 안쓰면 mapper에서 query가 나간다. Self reference기 때문에 fetch join도 낭비라고 생각하는데(hahsmap 직접 만들고 직접 assignChildren 해주기 때문), 왜 service 밖을 벗어나면 lazy-loading이 다시 일어날까?
        List<Category> allCategories = categoryRepository.findAllWithChildrenByMember(member);

        /** [1] Construct children map
         * Null -> [Category 1, Category 2]
         * 1 -> [Category 3, Category 4]
         * 2 -> [Category 5]
         * 5 -> [Category 6]
         * **/
        Map<Long, List<Category>> childrenMap = new HashMap<>();
        for (Category category : allCategories) {
            childrenMap.computeIfAbsent(category.getParentId(), k -> new ArrayList<>())
                    .add(category);
        }

        // [2] Set Children. This will build a recursive tree.
        for (Category category : allCategories) {
            List<Category> children = childrenMap.get(category.getId());
            if (children != null) {
                category.assignChildren(children);
            }
        }

        // [3] Return top level categories.
        return allCategories.stream()
                .filter(c -> c.getParent() == null)
                .toList();
    }

    @Transactional
    public Category createCategoryByPath(Member member, CategoryColor color, CategoryPath categoryPath) {
        // ["A", "B", "C"]
        CategoryPath parentCategoryPath = categoryPath.getParentCategoryPath(); // ["A", "B"]
        String newCategoryName = categoryPath.getLastCategoryName(); // "C"

        // Get parent category. ex. ["A", "B"]
        Category parentCategory = getCategoryByPath(member, parentCategoryPath);

        // Ensure parent category does not have the duplicating child category
        ensureNoDuplicateChildren(member, parentCategory, newCategoryName);

        Category category = Category.createCategory(member, parentCategory, newCategoryName, color);
        categoryRepository.save(category);
        return category;
    }

    @Transactional
    public Category createCategoryById(Member member, CategoryColor color, Long parentId, String categoryName) {
        // Check duplicating category
        Optional<Category> optionalCategory = categoryRepository.findByMemberAndParent_IdAndName(member, parentId, categoryName);
        if (optionalCategory.isPresent()) {
            throw new CustomException(DUPLICATE_CATEGORY);
        }

        Category parentCategory = null;
        if (parentId != null) {
            Optional<Category> optionalParentCategory = categoryRepository.findById(parentId);
            if (optionalParentCategory.isEmpty()) {
                throw new CustomException(PARENT_CATEGORY_NOT_FOUND);
            }
            parentCategory = optionalParentCategory.get();
        }

        Category category = Category.createCategory(member, parentCategory, categoryName, color);
        categoryRepository.save(category);
        return category;
    }

    @Transactional
    public void deleteCategoryById(Long memberId, Long categoryId) {
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (optionalCategory.isEmpty()) {
            return;
        }

        Category category = optionalCategory.get();
        if (!Objects.equals(category.getMember().getId(), memberId)) {
            throw new CustomException(UNAUTHORIZED_OPERATION_EXCEPTION);
        }

        categoryRepository.delete(category);
    }

    public void updateCategoryInfo(Member member, Long categoryId, CategoryUpdateDto categoryUpdateDto) {
        Category category = categoryRepository.findByMemberAndId(member, categoryId)
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND, "Category with id " + categoryId + " is not found."));

        String newName = categoryUpdateDto.getNewName();
        CategoryColor newColor = categoryUpdateDto.getColor();

        if (newName != null) {
            if (newName.isEmpty()) {
                throw new CustomException(EMPTY_CATEGORY_NAME);
            }
            category.updateName(newName);
        }

        if (newColor != null) {
            // Only top level category can update color
            if (category.getParent() != null) {
                throw new CustomException(NON_TOP_LEVEL_CATEGORY_COLOR_UPDATE_ATTEMPT_EXCEPTION);
            }

            // Recursively change itself and all its descendants color
            updateSubTreeCategoryColor(category, newColor);
        }
    }

    private void updateSubTreeCategoryColor(Category category, CategoryColor color) {
        category.updateColor(color);
        for (Category childCategory : category.getChildren()) {
            childCategory.updateColor(color);
            updateSubTreeCategoryColor(childCategory, color);
        }
    }

    public void updateCategoryParent(Member member, Long categoryId, Long newParentCategoryId) {
        Category category = categoryRepository.findByMemberAndId(member, categoryId)
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND, "Category with id " + categoryId + " is not found."));

        Category parentCategory = null;
        if (newParentCategoryId != null) {
            parentCategory = categoryRepository.findByMemberAndId(member, newParentCategoryId)
                    .orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND, "Parent category with id " + newParentCategoryId + " is not found."));
        }

        // Update all subtree color if not top-level category
        category.assignParent(parentCategory);
        if (parentCategory != null) {
            updateSubTreeCategoryColor(parentCategory, parentCategory.getColor());
        }
    }

    /**
     * Get category by category path.
     * ex) If the category path is ["A", "B", "C"], it will retrieve the category "A/B/C"
     * @param categoryPath Category path.
     * @return The target category. It can be nullable.
     */
    public Category getCategoryByPath(Member member, CategoryPath categoryPath) {
        // ["A", "B", "C"]
        Category parentCategory = null;
        StringBuilder parentCategoryPathStringBuilder = new StringBuilder("/");
        for (String categoryName : categoryPath.getPath()) {
            Category category = categoryRepository.findByMemberAndParentAndName(member, parentCategory, categoryName)
                    .orElseThrow(() -> new CustomException(
                            PARENT_CATEGORY_NOT_FOUND,
                            "Parent category " + "'" + parentCategoryPathStringBuilder.toString() + "'" + " does not have child category " + "'" + categoryName + "'"
                    ));

            parentCategory = category;
            parentCategoryPathStringBuilder.append(category.getName()).append("/");
        }

        return parentCategory; // last category ("C")
    }

    public void ensureNoDuplicateChildren(Member member, Category parentCategory, String name) {
        Optional<Category> optionalCategory = categoryRepository.findByMemberAndParentAndName(member, parentCategory, name);
        if (optionalCategory.isPresent()) {
            throw new CustomException(DUPLICATE_CATEGORY);
        }
    }
}
