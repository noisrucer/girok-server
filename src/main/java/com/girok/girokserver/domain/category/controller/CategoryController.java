package com.girok.girokserver.domain.category.controller;

import com.girok.girokserver.core.security.jwt.JwtTokenProvider;
import com.girok.girokserver.core.security.jwt.dto.JwtUserInfo;
import com.girok.girokserver.domain.category.controller.request.CreateCategoryRequest;
import com.girok.girokserver.domain.category.controller.response.CreateCategoryResponse;
import com.girok.girokserver.domain.category.facade.CategoryFacade;
import com.girok.girokserver.domain.category.vo.CategoryPath;
import com.girok.girokserver.global.enums.CategoryColor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Category")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CategoryController {

    private final CategoryFacade categoryFacade;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Create a new category")
    public ResponseEntity<CreateCategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        JwtUserInfo jwtUserInfo = jwtTokenProvider.getCurrentUserInfo();
        Long userId = jwtUserInfo.getUserId();

        CategoryColor color = request.getColor();
        CategoryPath path = new CategoryPath(request.getPath());

        Long categoryId = categoryFacade.createCategory(userId, color, path);
        return ResponseEntity.ok().body(new CreateCategoryResponse(categoryId));
    }
}

