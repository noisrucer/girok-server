package com.girok.girokserver.domain.category.controller.request;

import com.girok.girokserver.global.enums.CategoryColor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateCategoryByIdRequest {

    @Schema(example = "GREYISH_YELLOW")
    private CategoryColor color;

    @Schema(example = "1")
    private Long parentId;

    @NotBlank
    @Schema(example = "Work")
    private String name;
}
