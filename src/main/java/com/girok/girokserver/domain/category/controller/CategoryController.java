package com.girok.girokserver.domain.category.controller;

import com.girok.girokserver.core.exception.CustomException;
import com.girok.girokserver.core.exception.ErrorInfo;
import com.girok.girokserver.core.security.jwt.JwtTokenProvider;
import com.girok.girokserver.core.security.jwt.dto.JwtUserInfo;
import com.girok.girokserver.domain.category.controller.dto.CategoryResponseDto;
import com.girok.girokserver.domain.category.controller.mapper.CategoryMapper;
import com.girok.girokserver.domain.category.controller.request.CreateCategoryByIdRequest;
import com.girok.girokserver.domain.category.controller.request.CreateCategoryByPathRequest;
import com.girok.girokserver.domain.category.controller.request.UpdateCategoryInfoRequest;
import com.girok.girokserver.domain.category.controller.request.UpdateCategoryParentRequest;
import com.girok.girokserver.domain.category.controller.response.CreateCategoryByIdResponse;
import com.girok.girokserver.domain.category.controller.response.CreateCategoryByPathResponse;
import com.girok.girokserver.domain.category.controller.response.GetCategoriesResponse;
import com.girok.girokserver.domain.category.controller.response.GetCategoryIdByPathResponse;
import com.girok.girokserver.domain.category.entity.Category;
import com.girok.girokserver.domain.category.facade.CategoryFacade;
import com.girok.girokserver.domain.category.facade.dto.CategoryUpdateDto;
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

@Tag(name = "2. Category")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CategoryController {

    private final CategoryFacade categoryFacade;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all categories", description = "Get all categories in a **tree** shape.")
    public ResponseEntity<GetCategoriesResponse> getCategories() {
        JwtUserInfo jwtUserInfo = jwtTokenProvider.getCurrentUserInfo();
        Long memberId = jwtUserInfo.getMemberId();

        List<Category> categories = categoryFacade.getCategoriesAsTree(memberId);
        List<CategoryResponseDto> categoryResponseDtos = CategoryMapper.mapCategoriesToCategoryDtos(categories);

        return ResponseEntity.ok().body(new GetCategoriesResponse(categoryResponseDtos));
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Create a new category with parent id",
            description =
                    "[1] Pass `parentId = null` in order to make it a **top-level category**\n\n" +
                            "[2] `color` must be one of the colors defined in the color palette."
    )
    public ResponseEntity<CreateCategoryByIdResponse> createCategoryById(@Valid @RequestBody CreateCategoryByIdRequest request) {
        JwtUserInfo jwtUserInfo = jwtTokenProvider.getCurrentUserInfo();
        Long memberId = jwtUserInfo.getMemberId();

        CategoryColor color = request.getColor();
        Long parentId = request.getParentId();
        String name = request.getName();

        if (parentId == null && color == null) {
            throw new CustomException(ErrorInfo.TOP_LEVEL_CATEGORY_COLOR_NOT_EXIST);
        }

        Long categoryId = categoryFacade.createCategoryById(memberId, color, parentId, name);
        return ResponseEntity.ok().body(new CreateCategoryByIdResponse(categoryId));
    }

    @PostMapping("/categories/path")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Create a new category by path",
            description =
                    "[1] Suppose `path` is 0-indexed array. Then, this operation will create a new category with the name `path[N-1]` under the category defined by the path `path[0 ~ N-2]`, inclusive.\n\n" +
                            "[2] ex) If `path = [\"A\", \"B\", \"C\"]`, then this operation will create a new category `C` under `A/B`"
    )
    public ResponseEntity<CreateCategoryByPathResponse> createCategoryByPath(@Valid @RequestBody CreateCategoryByPathRequest request) {
        JwtUserInfo jwtUserInfo = jwtTokenProvider.getCurrentUserInfo();
        Long memberId = jwtUserInfo.getMemberId();

        CategoryColor color = request.getColor();
        CategoryPath path = new CategoryPath(request.getPath());

        Long categoryId = categoryFacade.createCategoryByPath(memberId, color, path);
        return ResponseEntity.ok().body(new CreateCategoryByPathResponse(categoryId));
    }

    @DeleteMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a category by ID",
            description =
                    "[1] This operation follows **idempotency** (멱등성). If you send a request to delete a same category **twice**, you will get **success** for both. This means that if you try to delete a **non-existent category**, you will still get success unless you don't have authorization to access the resource (i.e. you're not an owner of the cateogry).\n\n" +
                            "[2] This operation will **propagate** to all its descendants. This means if you delete a category, **all its descendants will be deleted**."
    )
    public void deleteCategoryById(@PathVariable(name = "id") Long categoryId) {
        JwtUserInfo jwtUserInfo = jwtTokenProvider.getCurrentUserInfo();
        Long memberId = jwtUserInfo.getMemberId();

        categoryFacade.deleteCategoryById(memberId, categoryId);
    }

    @PatchMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update category information",
            description =
                    "[1] This endpoint is concerned with a category's **static attributes** such as `color` and `name`.\n\n" +
                            "[2] This endpoint is **NOT** concerned with the **relationship entities** such as updating `parent`. In order to update the parent category, utilize `PATCH /categories/{id}/parent` endpoint.\n\n" +
                            "[3] Updating `color` attribute of **non-top level** categories is **NOT** allowed. This is due to the domain rule that all the child categories inherit their top-level category's color.\n\n" +
                            "[4] When updating `color` attribute of a top level category, the color update will **propagate** to **all its descendants in the subtree**."
    )
    public void updateCategoryInfo(@PathVariable(name = "id") Long categoryId, @Valid @RequestBody UpdateCategoryInfoRequest request) {
        JwtUserInfo jwtUserInfo = jwtTokenProvider.getCurrentUserInfo();
        Long memberId = jwtUserInfo.getMemberId();

        CategoryUpdateDto categoryUpdateDto = new CategoryUpdateDto(request.getNewName(), request.getColor());
        categoryFacade.updateCategoryInfo(memberId, categoryId, categoryUpdateDto);
    }

    @PatchMapping("/categories/{id}/parent")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update category parent with parent ID",
            description =
                    "[1] `newParentId = null` refers to changing the category to a **top level category**.\n\n" +
                            "[2] In case `newParentId = null` is passed, the color of the category and all its descendants is **preserved**.\n\n" +
                            "[3] In case `newParent Id = not null` is passed, the color of the category and all its descendants will be **updated to that of the top-level category of the new parent**. This is due to the domain rule that each category group **MUST** have a single color only."
    )
    public void updateCategoryParent(@PathVariable(name = "id") Long categoryId, @Valid @RequestBody UpdateCategoryParentRequest request) {
        JwtUserInfo jwtUserInfo = jwtTokenProvider.getCurrentUserInfo();
        Long memberId = jwtUserInfo.getMemberId();

        categoryFacade.updateCategoryParent(memberId, categoryId, request.getNewParentId());
    }

    @GetMapping("/categories/id-by-path")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a category ID by path",
            description =
                    "[1] ex) When `path = [\"A\", \"B\", \"C\"]` is passed, the category id of `C` (A/B/C) will be returned"
    )
    public ResponseEntity<GetCategoryIdByPathResponse> getCategoryIdByPath(@RequestParam(name = "path") List<String> path) {
        JwtUserInfo jwtUserInfo = jwtTokenProvider.getCurrentUserInfo();
        Long memberId = jwtUserInfo.getMemberId();

        CategoryPath categoryPath = new CategoryPath(path);
        Long categoryId = categoryFacade.getCategoryIdByPath(memberId, categoryPath);
        return ResponseEntity.ok().body(new GetCategoryIdByPathResponse(categoryId));
    }

}

