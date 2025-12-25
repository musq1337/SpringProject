package com.expensetracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * ответ с данными для главной панели
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {
    
    private BigDecimal totalBalance;
    
    private BigDecimal currentMonthIncome;
    
    private BigDecimal currentMonthExpense;
    
    private BigDecimal currentMonthBalance;
    
    private Double incomeChangePercent;
    
    private Double expenseChangePercent;
    
    private CategorySummary topExpenseCategory;
    
    private BigDecimal averageDailyExpense;
    
    private Long transactionCount;
    
    private List<CategoryExpense> expensesByCategory;
    
    private List<MonthSummary> incomeVsExpenseByMonth;
    
    private List<BalancePoint> balanceTrend;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategorySummary {
        private String name;
        private BigDecimal amount;
        private Double percent;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryExpense {
        private String categoryName;
        private BigDecimal amount;
        private Double percent;
        private String color;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthSummary {
        private String month;
        private BigDecimal income;
        private BigDecimal expense;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BalancePoint {
        private String date;
        private BigDecimal balance;
    }
}
