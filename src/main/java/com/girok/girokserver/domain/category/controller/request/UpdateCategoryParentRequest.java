package com.girok.girokserver.domain.category.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UpdateCategoryParentRequest {

    @Schema(example = "3")
    private Long newParentId;
}
