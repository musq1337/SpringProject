package com.expensetracker.controller;

import com.expensetracker.dto.request.BudgetRequest;
import com.expensetracker.dto.response.BudgetResponse;
import com.expensetracker.service.BudgetService;
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
 * контроллер для работы с бюджетами
 */
@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Tag(name = "Budgets", description = "управление бюджетами")
@SecurityRequirement(name = "Bearer Authentication")
public class BudgetController {

    private final BudgetService budgetService;

    /**
     * создать бюджет
     */
    @PostMapping
    @Operation(summary = "создать новый бюджет")
    public ResponseEntity<BudgetResponse> createBudget(@Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(budgetService.createBudget(request));
    }

    /**
     * получить все бюджеты
     */
    @GetMapping
    @Operation(summary = "получить все бюджеты пользователя")
    public ResponseEntity<List<BudgetResponse>> getAllBudgets() {
        return ResponseEntity.ok(budgetService.getAllBudgets());
    }

    /**
     * получить активные бюджеты
     */
    @GetMapping("/active")
    @Operation(summary = "получить активные бюджеты пользователя")
    public ResponseEntity<List<BudgetResponse>> getAllActiveBudgets() {
        return ResponseEntity.ok(budgetService.getAllActiveBudgets());
    }

    /**
     * получить бюджет по id
     */
    @GetMapping("/{id}")
    @Operation(summary = "получить бюджет по id")
    public ResponseEntity<BudgetResponse> getBudgetById(@PathVariable Long id) {
        return ResponseEntity.ok(budgetService.getBudgetById(id));
    }

    /**
     * обновить бюджет
     */
    @PutMapping("/{id}")
    @Operation(summary = "обновить бюджет")
    public ResponseEntity<BudgetResponse> updateBudget(
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequest request
    ) {
        return ResponseEntity.ok(budgetService.updateBudget(id, request));
    }

    /**
     * удалить бюджет
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "удалить бюджет")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }
}
