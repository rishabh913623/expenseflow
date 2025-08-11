package com.expensetracker.app.service;

import com.expensetracker.app.dto.ExpenseFilterDTO;
import com.expensetracker.app.dto.ExpenseSummaryDTO;
import com.expensetracker.app.model.Expense;
import com.expensetracker.app.model.PaymentMethod;
import com.expensetracker.app.repository.ExpenseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
     * @return the created expense
     * @throws IllegalArgumentException if expense data is invalid
     */
    public Expense createExpense(Expense expense) {
        logger.debug("Creating new expense: {}", expense);
        
        validateExpense(expense);
        
        Expense savedExpense = expenseRepository.save(expense);
        logger.info("Created expense with ID: {}", savedExpense.getId());
        
        return savedExpense;
    }
    
    /**
     * Updates an existing expense
     * 
     * @param id the ID of the expense to update
     * @param expense the updated expense data
     * @return the updated expense
     * @throws IllegalArgumentException if expense data is invalid or expense not found
     */
    public Expense updateExpense(Long id, Expense expense) {
        logger.debug("Updating expense with ID: {}", id);
        
        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found with ID: " + id));
        
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
        logger.info("Updated expense with ID: {}", updatedExpense.getId());
        
        return updatedExpense;
    }
    
    /**
     * Deletes an expense by ID
     * 
     * @param id the ID of the expense to delete
     * @throws IllegalArgumentException if expense not found
     */
    public void deleteExpense(Long id) {
        logger.debug("Deleting expense with ID: {}", id);
        
        if (!expenseRepository.existsById(id)) {
            throw new IllegalArgumentException("Expense not found with ID: " + id);
        }
        
        expenseRepository.deleteById(id);
        logger.info("Deleted expense with ID: {}", id);
    }
    
    /**
     * Retrieves an expense by ID
     * 
     * @param id the ID of the expense
     * @return the expense
     * @throws IllegalArgumentException if expense not found
     */
    @Transactional(readOnly = true)
    public Expense getExpenseById(Long id) {
        logger.debug("Retrieving expense with ID: {}", id);
        
        return expenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found with ID: " + id));
    }
    
    /**
     * Retrieves all expenses
     * 
     * @return list of all expenses
     */
    @Transactional(readOnly = true)
    public List<Expense> getAllExpenses() {
        logger.debug("Retrieving all expenses");
        return expenseRepository.findAllByOrderByExpenseDateDesc();
    }
    
    /**
     * Retrieves expenses based on filter criteria
     * 
     * @param filter the filter criteria
     * @return list of filtered expenses
     */
    @Transactional(readOnly = true)
    public List<Expense> getFilteredExpenses(ExpenseFilterDTO filter) {
        logger.debug("Retrieving filtered expenses: {}", filter);
        
        if (!filter.hasFilters()) {
            return getAllExpenses();
        }
        
        List<Expense> expenses = new ArrayList<>();
        
        // Apply filters based on available criteria
        if (filter.getStartDate() != null && filter.getEndDate() != null) {
            if (filter.getCategory() != null && filter.getPaymentMethod() != null) {
                expenses = expenseRepository.findByCategoryAndPaymentMethodAndExpenseDateBetween(
                        filter.getCategory(), filter.getPaymentMethod(), 
                        filter.getStartDate(), filter.getEndDate());
            } else if (filter.getCategory() != null) {
                expenses = expenseRepository.findByCategoryAndExpenseDateBetween(
                        filter.getCategory(), filter.getStartDate(), filter.getEndDate());
            } else if (filter.getPaymentMethod() != null) {
                expenses = expenseRepository.findByPaymentMethodAndExpenseDateBetween(
                        filter.getPaymentMethod(), filter.getStartDate(), filter.getEndDate());
            } else {
                expenses = expenseRepository.findByExpenseDateBetween(
                        filter.getStartDate(), filter.getEndDate());
            }
        } else if (filter.getCategory() != null && filter.getPaymentMethod() != null) {
            expenses = expenseRepository.findByCategory(filter.getCategory())
                    .stream()
                    .filter(e -> e.getPaymentMethod().equals(filter.getPaymentMethod()))
                    .collect(Collectors.toList());
        } else if (filter.getCategory() != null) {
            expenses = expenseRepository.findByCategory(filter.getCategory());
        } else if (filter.getPaymentMethod() != null) {
            expenses = expenseRepository.findByPaymentMethod(filter.getPaymentMethod());
        } else {
            expenses = getAllExpenses();
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
     * Gets expense summary with totals and breakdowns
     * 
     * @return expense summary DTO
     */
    @Transactional(readOnly = true)
    public ExpenseSummaryDTO getExpenseSummary() {
        logger.debug("Generating expense summary");
        
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
        
        return summary;
    }
    
    /**
     * Gets all distinct categories
     * 
     * @return list of distinct categories
     */
    @Transactional(readOnly = true)
    public List<String> getDistinctCategories() {
        return expenseRepository.findDistinctCategories();
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
