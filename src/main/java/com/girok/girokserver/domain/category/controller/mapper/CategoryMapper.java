package com.girok.girokserver.domain.category.controller.mapper;


import com.girok.girokserver.domain.category.controller.dto.CategoryResponseDto;
import com.girok.girokserver.domain.category.entity.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryMapper {

    public static List<CategoryResponseDto> mapCategoriesToCategoryDtos(List<Category> categories) {
        // Base case
        if (categories.isEmpty()) return new ArrayList<>();

        List<CategoryResponseDto> categoryDtos = new ArrayList<>();

        for (Category category : categories) {
            CategoryResponseDto categoryDto = CategoryResponseDto.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .color(category.getColor())
                    .children(mapCategoriesToCategoryDtos(category.getChildren()))
                    .build();

            categoryDtos.add(categoryDto);
        }

        return categoryDtos;
    }
}
