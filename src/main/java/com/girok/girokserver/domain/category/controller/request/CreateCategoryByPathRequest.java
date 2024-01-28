package com.girok.girokserver.domain.category.controller.request;

import com.girok.girokserver.global.enums.CategoryColor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateCategoryByPathRequest {

    @NotNull
    @Schema(example = "BEIGE")
    private CategoryColor color;

    @NotEmpty
    @Schema(example = "[\"Career\", \"Work\", \"Interview\"]")
    private List<String> path;

}
