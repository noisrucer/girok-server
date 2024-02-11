package com.girok.girokserver.domain.category.controller.response;

import com.girok.girokserver.domain.category.controller.dto.CategoryResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class GetCategoriesResponse {

    @NotNull
    @Schema(example = "[" +
            "{" +
            "   \"id\": 1," +
            "   \"name\": \"Work\"," +
            "   \"color\": \"LAVENDER\"," +
            "   \"children\": [" +
            "       {" +
            "           \"id\": 2," +
            "           \"name\": \"Meeting\"," +
            "           \"color\": \"LAVENDER\"," +
            "           \"children\": []" +
            "       }" +
            "   ]" +
            "}," +
            "{" +
            "   \"id\": 3," +
            "   \"name\": \"Project\"," +
            "   \"color\": \"LAVENDER\"," +
            "   \"children\": [" +
            "       {" +
            "           \"id\": 4," +
            "           \"name\": \"Meeting\"," +
            "           \"color\": \"LAVENDER\"," +
            "           \"children\": []" +
            "       }," +
            "       {" +
            "           \"id\": 5," +
            "           \"name\": \"Meeting\"," +
            "           \"color\": \"LAVENDER\"," +
            "           \"children\": []" +
            "       }" +
            "   ]" +
            "}" +
            "]")
    private List<CategoryResponseDto> rootCategories;
}
