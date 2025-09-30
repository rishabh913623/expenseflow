package com.expensetracker.app.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expensetracker.app.dto.ExpenseFilterDTO;
import com.expensetracker.app.dto.ExpenseSummaryDTO;
import com.expensetracker.app.model.Expense;
import com.expensetracker.app.model.PaymentMethod;
import com.expensetracker.app.model.User;
import com.expensetracker.app.repository.ExpenseRepository;

/**
 * Service class for managing expense operations.
 * Provides business logic for CRUD operations, filtering, and reporting.
 * 
 * @author Expense Tracker Team
 * @version 1.0
 */
@Service
@Transactional
public class ExpenseService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExpenseService.class);
    
    private final ExpenseRepository expenseRepository;
    
    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }
    
    /**
     * Creates a new expense
     *
     * @param expense the expense to create
     * @param user the user who owns the expense
     * @return the created expense
     * @throws IllegalArgumentException if expense data is invalid
     */
    public Expense createExpense(Expense expense, User user) {
        logger.debug("Creating new expense for user {}: {}", user.getUsername(), expense);

        expense.setUser(user);
        validateExpense(expense);

        Expense savedExpense = expenseRepository.save(expense);
        logger.info("Created expense with ID: {} for user: {}", savedExpense.getId(), user.getUsername());

        return savedExpense;
    }
    
    /**
     * Updates an existing expense
     *
     * @param id the ID of the expense to update
     * @param expense the updated expense data
     * @param user the user who owns the expense
     * @return the updated expense
     * @throws IllegalArgumentException if expense data is invalid or expense not found
     */
    public Expense updateExpense(Long id, Expense expense, User user) {
        logger.debug("Updating expense with ID: {} for user: {}", id, user.getUsername());

        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found with ID: " + id));

        // Check if the expense belongs to the user
        if (!existingExpense.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Expense does not belong to the user");
        }

        // Update fields
        existingExpense.setAmount(expense.getAmount());
        existingExpense.setCategory(expense.getCategory());
        existingExpense.setExpenseDate(expense.getExpenseDate());
        existingExpense.setPaymentMethod(expense.getPaymentMethod());
        existingExpense.setUpiVpa(expense.getUpiVpa());
        existingExpense.setTransactionId(expense.getTransactionId());
        existingExpense.setPayerName(expense.getPayerName());
        existingExpense.setNotes(expense.getNotes());

        validateExpense(existingExpense);

        Expense updatedExpense = expenseRepository.save(existingExpense);
        logger.info("Updated expense with ID: {} for user: {}", updatedExpense.getId(), user.getUsername());

        return updatedExpense;
    }
    
    /**
     * Deletes an expense by ID
     *
     * @param id the ID of the expense to delete
     * @param user the user who owns the expense
     * @throws IllegalArgumentException if expense not found or doesn't belong to user
     */
    public void deleteExpense(Long id, User user) {
        logger.debug("Deleting expense with ID: {} for user: {}", id, user.getUsername());

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found with ID: " + id));

        // Check if the expense belongs to the user
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Expense does not belong to the user");
        }

        expenseRepository.deleteById(id);
        logger.info("Deleted expense with ID: {} for user: {}", id, user.getUsername());
    }
    
    /**
     * Retrieves an expense by ID
     *
     * @param id the ID of the expense
     * @param user the user who owns the expense
     * @return the expense
     * @throws IllegalArgumentException if expense not found or doesn't belong to user
     */
    @Transactional(readOnly = true)
    public Expense getExpenseById(Long id, User user) {
        logger.debug("Retrieving expense with ID: {} for user: {}", id, user.getUsername());

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found with ID: " + id));

        // Check if the expense belongs to the user
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Expense does not belong to the user");
        }

        return expense;
    }
    
    /**
     * Retrieves all expenses for a user
     *
     * @param user the user to filter by
     * @return list of all expenses for the user
     */
    @Transactional(readOnly = true)
    public List<Expense> getAllExpenses(User user) {
        logger.debug("Retrieving all expenses for user: {}", user.getUsername());
        return expenseRepository.findByUserOrderByExpenseDateDesc(user);
    }
    
    /**
     * Retrieves expenses based on filter criteria for a user
     *
     * @param filter the filter criteria
     * @param user the user to filter by
     * @return list of filtered expenses
     */
    @Transactional(readOnly = true)
    public List<Expense> getFilteredExpenses(ExpenseFilterDTO filter, User user) {
        logger.debug("Retrieving filtered expenses for user {}: {}", user.getUsername(), filter);

        if (!filter.hasFilters()) {
            return getAllExpenses(user);
        }

        List<Expense> expenses = new ArrayList<>();

        // Apply filters based on available criteria
        if (filter.getStartDate() != null && filter.getEndDate() != null) {
            if (filter.getCategory() != null && filter.getPaymentMethod() != null) {
                expenses = expenseRepository.findByUserAndCategoryAndPaymentMethodAndExpenseDateBetween(
                        user, filter.getCategory(), filter.getPaymentMethod(),
                        filter.getStartDate(), filter.getEndDate());
            } else if (filter.getCategory() != null) {
                expenses = expenseRepository.findByUserAndCategoryAndExpenseDateBetween(
                        user, filter.getCategory(), filter.getStartDate(), filter.getEndDate());
            } else if (filter.getPaymentMethod() != null) {
                expenses = expenseRepository.findByUserAndPaymentMethodAndExpenseDateBetween(
                        user, filter.getPaymentMethod(), filter.getStartDate(), filter.getEndDate());
            } else {
                expenses = expenseRepository.findByUserAndExpenseDateBetween(
                        user, filter.getStartDate(), filter.getEndDate());
            }
        } else if (filter.getCategory() != null && filter.getPaymentMethod() != null) {
            expenses = expenseRepository.findByUserAndCategory(user, filter.getCategory())
                    .stream()
                    .filter(e -> e.getPaymentMethod().equals(filter.getPaymentMethod()))
                    .collect(Collectors.toList());
        } else if (filter.getCategory() != null) {
            expenses = expenseRepository.findByUserAndCategory(user, filter.getCategory());
        } else if (filter.getPaymentMethod() != null) {
            expenses = expenseRepository.findByUserAndPaymentMethod(user, filter.getPaymentMethod());
        } else {
            expenses = getAllExpenses(user);
        }

        // Apply additional filters
        if (filter.getUpiVpa() != null && !filter.getUpiVpa().trim().isEmpty()) {
            expenses = expenses.stream()
                    .filter(e -> e.getUpiVpa() != null &&
                            e.getUpiVpa().toLowerCase().contains(filter.getUpiVpa().toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (filter.getTransactionId() != null && !filter.getTransactionId().trim().isEmpty()) {
            expenses = expenses.stream()
                    .filter(e -> e.getTransactionId() != null &&
                            e.getTransactionId().toLowerCase().contains(filter.getTransactionId().toLowerCase()))
                    .collect(Collectors.toList());
        }

        return expenses;
    }
    
    /**
     * Gets expense summary with totals and breakdowns (legacy method for backward compatibility)
     *
     * @return expense summary DTO
     */
    @Transactional(readOnly = true)
    public ExpenseSummaryDTO getExpenseSummary() {
        logger.debug("Generating expense summary (legacy method)");

        List<Expense> allExpenses = expenseRepository.findAll();

        BigDecimal totalAmount = allExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCashAmount = allExpenses.stream()
                .map(Expense::getCashAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalUpiAmount = allExpenses.stream()
                .map(Expense::getUpiAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalTransactions = allExpenses.size();

        // Category totals
        Map<String, BigDecimal> categoryTotals = allExpenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
                ));

        // Payment method totals
        Map<String, BigDecimal> paymentMethodTotals = allExpenses.stream()
                .collect(Collectors.groupingBy(
                        expense -> expense.getPaymentMethod().toString(),
                        Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
                ));

        ExpenseSummaryDTO summary = new ExpenseSummaryDTO(
                totalAmount, totalCashAmount, totalUpiAmount, totalTransactions);
        summary.setCategoryTotals(categoryTotals);
        summary.setPaymentMethodTotals(paymentMethodTotals);
        // Budget fields will be null for legacy method

        return summary;
    }

    /**
     * Gets expense summary with totals and breakdowns
     *
     * @param user the user for whom to generate the summary
     * @return expense summary DTO
     */
    @Transactional(readOnly = true)
    public ExpenseSummaryDTO getExpenseSummary(User user) {
        logger.debug("Generating expense summary for user: {}", user.getUsername());

        List<Expense> allExpenses = expenseRepository.findByUser(user);

        BigDecimal totalAmount = allExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCashAmount = allExpenses.stream()
                .map(Expense::getCashAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalUpiAmount = allExpenses.stream()
                .map(Expense::getUpiAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalTransactions = allExpenses.size();

        // Category totals
        Map<String, BigDecimal> categoryTotals = allExpenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
                ));

        // Payment method totals
        Map<String, BigDecimal> paymentMethodTotals = allExpenses.stream()
                .collect(Collectors.groupingBy(
                        expense -> expense.getPaymentMethod().toString(),
                        Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
                ));

        // Budget and remaining budget
        BigDecimal budget = user.getBudget() != null ? user.getBudget() : BigDecimal.ZERO;
        BigDecimal remainingBudget = budget.subtract(totalAmount);

        ExpenseSummaryDTO summary = new ExpenseSummaryDTO(
                totalAmount, totalCashAmount, totalUpiAmount, totalTransactions);
        summary.setCategoryTotals(categoryTotals);
        summary.setPaymentMethodTotals(paymentMethodTotals);
        summary.setBudget(budget);
        summary.setRemainingBudget(remainingBudget);

        return summary;
    }
    
    /**
     * Gets all distinct categories for a user
     *
     * @param user the user to filter by
     * @return list of distinct categories
     */
    @Transactional(readOnly = true)
    public List<String> getDistinctCategories(User user) {
        return expenseRepository.findDistinctCategoriesByUser(user);
    }
    
    /**
     * Validates expense data
     * 
     * @param expense the expense to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateExpense(Expense expense) {
        if (expense.getAmount() == null || expense.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        
        if (expense.getCategory() == null || expense.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Category is required");
        }
        
        if (expense.getExpenseDate() == null) {
            throw new IllegalArgumentException("Expense date is required");
        }
        
        if (expense.getPaymentMethod() == null) {
            throw new IllegalArgumentException("Payment method is required");
        }
        
        // UPI specific validations
        if (expense.getPaymentMethod() == PaymentMethod.UPI) {
            if (expense.getUpiVpa() == null || expense.getUpiVpa().trim().isEmpty()) {
                throw new IllegalArgumentException("UPI VPA is required for UPI payments");
            }
            if (expense.getTransactionId() == null || expense.getTransactionId().trim().isEmpty()) {
                throw new IllegalArgumentException("Transaction ID is required for UPI payments");
            }
        }
    }
}
