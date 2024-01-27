package com.girok.girokserver.domain.category.controller.request;

import com.girok.girokserver.domain.category.vo.CategoryPath;
import com.girok.girokserver.global.enums.CategoryColor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateCategoryRequest {

    @NotNull
    @Schema(example = "YELLOW")
    private CategoryColor color;

    @NotEmpty
    @Schema(example = "[\"Career\", \"Work\", \"Interview\"]")
    private List<String> path;

}
