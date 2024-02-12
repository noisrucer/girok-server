package com.girok.girokserver.domain.category.controller.response;

import com.girok.girokserver.global.enums.CategoryColor;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GetCategoryResponse {

    @NotNull
    private CategoryColor color;
}
