package com.expensetracker.controller;

import com.expensetracker.dto.request.CategoryRequest;
import com.expensetracker.dto.response.CategoryResponse;
import com.expensetracker.enums.TransactionType;
import com.expensetracker.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * контроллер для работы с категориями
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "управление категориями")
@SecurityRequirement(name = "Bearer Authentication")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * создать категорию
     */
    @PostMapping
    @Operation(summary = "создать новую категорию")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCategory(request));
    }

    /**
     * получить все категории
     */
    @GetMapping
    @Operation(summary = "получить список категорий с фильтром по типу")
    public ResponseEntity<List<CategoryResponse>> getAllCategories(
            @RequestParam(required = false) TransactionType type
    ) {
        return ResponseEntity.ok(categoryService.getAllCategories(type));
    }

    /**
     * получить категорию по id
     */
    @GetMapping("/{id}")
    @Operation(summary = "получить категорию по id")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    /**
     * обновить категорию
     */
    @PutMapping("/{id}")
    @Operation(summary = "обновить категорию")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request
    ) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    /**
     * удалить категорию
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "удалить категорию")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
