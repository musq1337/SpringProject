package com.expensetracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * ответ с данными для главной панели
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {
    
    private BigDecimal totalIncome;
    
    private BigDecimal totalExpense;
    
    private BigDecimal balance;
    
    private List<CategoryExpense> categoryExpenses;
    
    private List<MonthSummary> monthSummaries;
    
    private List<BalancePoint> balanceTrend;
    
    private String period;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryExpense {
        private String categoryName;
        private String categoryColor;
        private BigDecimal amount;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthSummary {
        private String month;
        private Integer year;
        private BigDecimal income;
        private BigDecimal expense;
        private BigDecimal balance;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BalancePoint {
        private LocalDate date;
        private BigDecimal balance;
    }
}
