package com.uspray.uspray.controller;

import com.uspray.uspray.DTO.ApiResponseDto;
import com.uspray.uspray.DTO.category.CategoryRequestDto;
import com.uspray.uspray.DTO.category.CategoryResponseDto;
import com.uspray.uspray.exception.SuccessStatus;
import com.uspray.uspray.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")
@Tag(name = "Category", description = "Category 관련 API")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT Auth")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "카테고리 조회")
    @ApiResponse(
        responseCode = "200",
        description = "카테고리 조회",
        content = @Content(schema = @Schema(implementation = CategoryResponseDto.class)))
    @GetMapping("/{categoryId}")
    public ApiResponseDto<CategoryResponseDto> getCategory(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @Parameter(description = "카테고리 ID", required = true) @PathVariable("categoryId") Long categoryId
    ) {
        return ApiResponseDto.success(SuccessStatus.GET_CATEGORY_SUCCESS,
            categoryService.getCategory(user.getUsername(), categoryId));
    }

    @Operation(summary = "카테고리 목록 조회")
    @ApiResponse(
        responseCode = "200",
        description = "카테고리 목록 조회",
        content = @Content(schema = @Schema(implementation = CategoryResponseDto.class)))
    @GetMapping
    public ApiResponseDto<List<CategoryResponseDto>> getCategoryList(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @Parameter(description = "카테고리 종류(personal, shared)", required = true, example = "personal") String categoryType
    ) {
        return ApiResponseDto.success(SuccessStatus.GET_CATEGORY_LIST_SUCCESS,
            categoryService.getCategoryList(user.getUsername(), categoryType));
    }

    @Operation(summary = "카테고리 생성")
    @ApiResponse(
        responseCode = "201",
        description = "카테고리 생성",
        content = @Content(schema = @Schema(implementation = CategoryResponseDto.class)))
    @PostMapping()
    public ApiResponseDto<CategoryResponseDto> createCategory(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @RequestBody @Valid CategoryRequestDto categoryRequestDto
    ) {
        return ApiResponseDto.success(SuccessStatus.CREATE_CATEGORY_SUCCESS,
            categoryService.createCategory(user.getUsername(), categoryRequestDto));
    }

    @DeleteMapping("/{categoryId}")
    public ApiResponseDto<CategoryResponseDto> deleteCategory(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @Parameter(description = "카테고리 ID", required = true) @PathVariable("categoryId") Long categoryId
    ) {
        return ApiResponseDto.success(SuccessStatus.DELETE_CATEGORY_SUCCESS,
            categoryService.deleteCategory(user.getUsername(), categoryId));
    }

    @PutMapping("/{categoryId}")
    @ApiResponse(
        responseCode = "200",
        description = "카테고리 수정",
        content = @Content(schema = @Schema(implementation = CategoryResponseDto.class)))
    @Operation(summary = "카테고리 수정")
    public ApiResponseDto<CategoryResponseDto> updatePray(
        @Parameter(description = "카테고리 ID", required = true) @PathVariable("categoryId") Long categoryId,
        @RequestBody @Valid CategoryRequestDto categoryRequestDto,
        @Parameter(hidden = true) @AuthenticationPrincipal User user
    ) {
        return ApiResponseDto.success(SuccessStatus.UPDATE_CATEGORY_SUCCESS,
            categoryService.updateCategory(user.getUsername(), categoryId, categoryRequestDto));
    }


    @PutMapping("/{categoryId}/order/{index}")
    @ApiResponse(
        responseCode = "200",
        description = "카테고리 순서 수정",
        content = @Content(schema = @Schema(implementation = CategoryResponseDto.class)))
    @Operation(summary = "카테고리 순서 수정")
    public ApiResponseDto<CategoryResponseDto> updatePrayOrder(
        @Parameter(description = "카테고리 ID", required = true) @PathVariable("categoryId") Long categoryId,
        @Parameter(description = "카테고리 순서", required = true) @PathVariable("index") int index,
        @Parameter(hidden = true) @AuthenticationPrincipal User user
    ) {
        return ApiResponseDto.success(SuccessStatus.UPDATE_CATEGORY_SUCCESS,
            categoryService.updateCategoryOrder(user.getUsername(), categoryId, index));
    }
}
