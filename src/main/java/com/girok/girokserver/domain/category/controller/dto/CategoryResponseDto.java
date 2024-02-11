package com.girok.girokserver.domain.category.controller.dto;

import com.girok.girokserver.global.enums.CategoryColor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class CategoryResponseDto {

    private Long id;
    private String name;
    private CategoryColor color;
    private List<CategoryResponseDto> children;
}
