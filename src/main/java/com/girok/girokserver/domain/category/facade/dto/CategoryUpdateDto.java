package com.girok.girokserver.domain.category.facade.dto;

import com.girok.girokserver.global.enums.CategoryColor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryUpdateDto {
    private String newName;
    private CategoryColor color;
}
