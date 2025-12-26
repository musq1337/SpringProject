package com.expensetracker.service;

import com.expensetracker.dto.response.DashboardResponse;
import com.expensetracker.entity.User;
import com.expensetracker.enums.TransactionType;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.TransactionRepository;
import com.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * сервис для аналитики и отчетов
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    /**
     * получить данные для dashboard
     */
    public DashboardResponse getDashboard(LocalDate startDate, LocalDate endDate) {
        User currentUser = getCurrentUser();
        
        // текущий месяц если даты не указаны
        if (startDate == null || endDate == null) {
            YearMonth currentMonth = YearMonth.now();
            startDate = currentMonth.atDay(1);
            endDate = currentMonth.atEndOfMonth();
        }
        
        // общие суммы
        BigDecimal totalIncome = transactionRepository.sumByUserIdAndTypeAndDateBetween(
                currentUser.getId(), TransactionType.INCOME, startDate, endDate);
        BigDecimal totalExpense = transactionRepository.sumByUserIdAndTypeAndDateBetween(
                currentUser.getId(), TransactionType.EXPENSE, startDate, endDate);
        
        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;
        
        BigDecimal balance = totalIncome.subtract(totalExpense);
        
        // расходы по категориям
        List<Object[]> expensesByCategory = transactionRepository
                .findExpensesByCategoryGrouped(currentUser.getId(), startDate, endDate);
        
        List<DashboardResponse.CategoryExpense> categoryExpenses = new ArrayList<>();
        for (Object[] row : expensesByCategory) {
            String categoryName = (String) row[0];
            String categoryColor = (String) row[1];
            BigDecimal amount = (BigDecimal) row[2];
            
            categoryExpenses.add(DashboardResponse.CategoryExpense.builder()
                    .categoryName(categoryName)
                    .categoryColor(categoryColor)
                    .amount(amount)
                    .build());
        }
        
        // итоги по месяцам (последние 6 месяцев)
        List<DashboardResponse.MonthSummary> monthSummaries = new ArrayList<>();
        LocalDate monthStart = YearMonth.now().minusMonths(5).atDay(1);
        
        for (int i = 0; i < 6; i++) {
            YearMonth month = YearMonth.from(monthStart).plusMonths(i);
            LocalDate start = month.atDay(1);
            LocalDate end = month.atEndOfMonth();
            
            BigDecimal income = transactionRepository.sumByUserIdAndTypeAndDateBetween(
                    currentUser.getId(), TransactionType.INCOME, start, end);
            BigDecimal expense = transactionRepository.sumByUserIdAndTypeAndDateBetween(
                    currentUser.getId(), TransactionType.EXPENSE, start, end);
            
            if (income == null) income = BigDecimal.ZERO;
            if (expense == null) expense = BigDecimal.ZERO;
            
            monthSummaries.add(DashboardResponse.MonthSummary.builder()
                    .month(month.getMonth().toString())
                    .year(month.getYear())
                    .income(income)
                    .expense(expense)
                    .balance(income.subtract(expense))
                    .build());
        }
        
        // динамика баланса (последние 30 дней)
        List<DashboardResponse.BalancePoint> balanceTrend = new ArrayList<>();
        BigDecimal runningBalance = BigDecimal.ZERO;
        
        for (int i = 29; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            
            BigDecimal dayIncome = transactionRepository.sumByUserIdAndTypeAndDateBetween(
                    currentUser.getId(), TransactionType.INCOME, date, date);
            BigDecimal dayExpense = transactionRepository.sumByUserIdAndTypeAndDateBetween(
                    currentUser.getId(), TransactionType.EXPENSE, date, date);
            
            if (dayIncome == null) dayIncome = BigDecimal.ZERO;
            if (dayExpense == null) dayExpense = BigDecimal.ZERO;
            
            runningBalance = runningBalance.add(dayIncome).subtract(dayExpense);
            
            balanceTrend.add(DashboardResponse.BalancePoint.builder()
                    .date(date)
                    .balance(runningBalance)
                    .build());
        }
        
        return DashboardResponse.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .balance(balance)
                .categoryExpenses(categoryExpenses)
                .monthSummaries(monthSummaries)
                .balanceTrend(balanceTrend)
                .period(startDate + " - " + endDate)
                .build();
    }

    /**
     * получить текущего пользователя
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("пользователь не найден"));
    }
}
