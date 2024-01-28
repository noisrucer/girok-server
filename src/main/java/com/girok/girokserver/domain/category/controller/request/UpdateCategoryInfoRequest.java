package com.girok.girokserver.domain.category.controller.request;

import com.girok.girokserver.global.enums.CategoryColor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UpdateCategoryInfoRequest {

    @Schema(example = "SomeNewName")
    private String newName;

    @Schema(example = "BEIGE")
    private CategoryColor color;
}
