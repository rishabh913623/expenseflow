package com.expensetracker.app.controller;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expensetracker.app.dto.ExpenseFilterDTO;
import com.expensetracker.app.dto.ExpenseSummaryDTO;
import com.expensetracker.app.model.Expense;
import com.expensetracker.app.model.PaymentMethod;
import com.expensetracker.app.model.User;
import com.expensetracker.app.service.AuthService;
import com.expensetracker.app.service.CsvExportService;
import com.expensetracker.app.service.ExpenseService;

import jakarta.validation.Valid;

/**
 * REST Controller for expense management operations.
 * Provides endpoints for CRUD operations, filtering, and reporting.
 * 
 * @author Expense Tracker Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ExpenseController {
    
    private static final Logger logger = LoggerFactory.getLogger(ExpenseController.class);
    
    private final ExpenseService expenseService;
    private final CsvExportService csvExportService;
    private final AuthService authService;

    @Autowired
    public ExpenseController(ExpenseService expenseService, CsvExportService csvExportService, AuthService authService) {
        this.expenseService = expenseService;
        this.csvExportService = csvExportService;
        this.authService = authService;
    }

    /**
     * Helper method to get authenticated user from token
     *
     * @param token the JWT token
     * @return the authenticated user
     */
    private User getAuthenticatedUser(String token) {
        String username = authService.validateTokenAndGetUsername(token.replace("Bearer ", ""));
        return authService.getUserByUsername(username);
    }
    
    /**
     * Get all expenses or filtered expenses for authenticated user
     *
     * @param token the JWT token
     * @param category optional category filter
     * @param paymentMethod optional payment method filter
     * @param startDate optional start date filter (yyyy-MM-dd)
     * @param endDate optional end date filter (yyyy-MM-dd)
     * @param upiVpa optional UPI VPA filter
     * @param transactionId optional transaction ID filter
     * @return list of expenses
     */
    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpenses(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) PaymentMethod paymentMethod,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String upiVpa,
            @RequestParam(required = false) String transactionId) {

        logger.debug("GET /api/expenses - category: {}, paymentMethod: {}, startDate: {}, endDate: {}",
                    category, paymentMethod, startDate, endDate);

        try {
            User user = getAuthenticatedUser(token);
            ExpenseFilterDTO filter = new ExpenseFilterDTO(category, paymentMethod, startDate, endDate, upiVpa, transactionId);
            List<Expense> expenses = expenseService.getFilteredExpenses(filter, user);

            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            logger.error("Error retrieving expenses", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get expense by ID for authenticated user
     *
     * @param id the expense ID
     * @param token the JWT token
     * @return the expense
     */
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        logger.debug("GET /api/expenses/{}", id);

        try {
            User user = getAuthenticatedUser(token);
            Expense expense = expenseService.getExpenseById(id, user);
            return ResponseEntity.ok(expense);
        } catch (IllegalArgumentException e) {
            logger.warn("Expense not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error retrieving expense with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Create a new expense for authenticated user
     *
     * @param expense the expense to create
     * @param token the JWT token
     * @return the created expense
     */
    @PostMapping
    public ResponseEntity<Expense> createExpense(@Valid @RequestBody Expense expense, @RequestHeader("Authorization") String token) {
        logger.debug("POST /api/expenses - {}", expense);

        try {
            User user = getAuthenticatedUser(token);
            Expense createdExpense = expenseService.createExpense(expense, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdExpense);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid expense data: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error creating expense", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Update an existing expense for authenticated user
     *
     * @param id the expense ID
     * @param expense the updated expense data
     * @param token the JWT token
     * @return the updated expense
     */
    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @Valid @RequestBody Expense expense, @RequestHeader("Authorization") String token) {
        logger.debug("PUT /api/expenses/{} - {}", id, expense);

        try {
            User user = getAuthenticatedUser(token);
            Expense updatedExpense = expenseService.updateExpense(id, expense, user);
            return ResponseEntity.ok(updatedExpense);
        } catch (IllegalArgumentException e) {
            logger.warn("Error updating expense: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error updating expense with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Delete an expense for authenticated user
     *
     * @param id the expense ID
     * @param token the JWT token
     * @return no content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        logger.debug("DELETE /api/expenses/{}", id);

        try {
            User user = getAuthenticatedUser(token);
            expenseService.deleteExpense(id, user);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Expense not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting expense with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get expense summary with totals and breakdowns
     *
     * @return expense summary
     */
    @GetMapping("/summary")
    public ResponseEntity<ExpenseSummaryDTO> getExpenseSummary(@RequestHeader("Authorization") String token) {
        logger.debug("GET /api/expenses/summary");

        try {
            String username = authService.validateTokenAndGetUsername(token.replace("Bearer ", ""));
            User user = authService.getUserByUsername(username);
            ExpenseSummaryDTO summary = expenseService.getExpenseSummary(user);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            logger.error("Error generating expense summary", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get user budget
     *
     * @return user budget
     */
    @GetMapping("/budget")
    public ResponseEntity<java.math.BigDecimal> getBudget(@RequestHeader("Authorization") String token) {
        logger.debug("GET /api/expenses/budget");

        try {
            String username = authService.validateTokenAndGetUsername(token.replace("Bearer ", ""));
            User user = authService.getUserByUsername(username);
            return ResponseEntity.ok(user.getBudget() != null ? user.getBudget() : java.math.BigDecimal.ZERO);
        } catch (Exception e) {
            logger.error("Error retrieving budget", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update user budget
     *
     * @param budget the new budget amount
     * @return updated budget
     */
    @PutMapping("/budget")
    public ResponseEntity<java.math.BigDecimal> updateBudget(@RequestHeader("Authorization") String token,
                                                              @RequestBody String budgetString) {
        logger.debug("PUT /api/expenses/budget - {}", budgetString);

        try {
            String username = authService.validateTokenAndGetUsername(token.replace("Bearer ", ""));
            User user = authService.getUserByUsername(username);

            // Parse the budget amount from string to BigDecimal
            java.math.BigDecimal budget = new java.math.BigDecimal(budgetString.trim());

            // Ensure budget is not negative
            if (budget.compareTo(java.math.BigDecimal.ZERO) < 0) {
                return ResponseEntity.badRequest().build();
            }

            user.setBudget(budget);
            authService.updateUser(user);
            return ResponseEntity.ok(budget);
        } catch (NumberFormatException e) {
            logger.error("Invalid budget format: {}", budgetString, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error updating budget", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get all distinct categories for authenticated user
     *
     * @param token the JWT token
     * @return list of categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories(@RequestHeader("Authorization") String token) {
        logger.debug("GET /api/expenses/categories");

        try {
            User user = getAuthenticatedUser(token);
            List<String> categories = expenseService.getDistinctCategories(user);
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            logger.error("Error retrieving categories", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Export expenses to CSV for authenticated user
     *
     * @param token the JWT token
     * @param category optional category filter
     * @param paymentMethod optional payment method filter
     * @param startDate optional start date filter
     * @param endDate optional end date filter
     * @return CSV file as response
     */
    @GetMapping("/export/csv")
    public ResponseEntity<String> exportExpensesToCsv(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) PaymentMethod paymentMethod,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        logger.debug("GET /api/expenses/export/csv");

        try {
            User user = getAuthenticatedUser(token);
            ExpenseFilterDTO filter = new ExpenseFilterDTO(category, paymentMethod, startDate, endDate, null, null);
            List<Expense> expenses = expenseService.getFilteredExpenses(filter, user);
            String csvContent = csvExportService.exportExpensesToCsv(expenses);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", csvExportService.getCsvFilename());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvContent);
        } catch (Exception e) {
            logger.error("Error exporting expenses to CSV", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
