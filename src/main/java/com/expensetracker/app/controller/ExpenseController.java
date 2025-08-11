package com.expensetracker.app.controller;

import com.expensetracker.app.dto.ExpenseFilterDTO;
import com.expensetracker.app.dto.ExpenseSummaryDTO;
import com.expensetracker.app.model.Expense;
import com.expensetracker.app.model.PaymentMethod;
import com.expensetracker.app.service.CsvExportService;
import com.expensetracker.app.service.ExpenseService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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
    
    @Autowired
    public ExpenseController(ExpenseService expenseService, CsvExportService csvExportService) {
        this.expenseService = expenseService;
        this.csvExportService = csvExportService;
    }
    
    /**
     * Get all expenses or filtered expenses
     * 
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
            @RequestParam(required = false) String category,
            @RequestParam(required = false) PaymentMethod paymentMethod,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String upiVpa,
            @RequestParam(required = false) String transactionId) {
        
        logger.debug("GET /api/expenses - category: {}, paymentMethod: {}, startDate: {}, endDate: {}", 
                    category, paymentMethod, startDate, endDate);
        
        try {
            ExpenseFilterDTO filter = new ExpenseFilterDTO(category, paymentMethod, startDate, endDate, upiVpa, transactionId);
            List<Expense> expenses = expenseService.getFilteredExpenses(filter);
            
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            logger.error("Error retrieving expenses", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get expense by ID
     * 
     * @param id the expense ID
     * @return the expense
     */
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {
        logger.debug("GET /api/expenses/{}", id);
        
        try {
            Expense expense = expenseService.getExpenseById(id);
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
     * Create a new expense
     * 
     * @param expense the expense to create
     * @return the created expense
     */
    @PostMapping
    public ResponseEntity<Expense> createExpense(@Valid @RequestBody Expense expense) {
        logger.debug("POST /api/expenses - {}", expense);
        
        try {
            Expense createdExpense = expenseService.createExpense(expense);
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
     * Update an existing expense
     * 
     * @param id the expense ID
     * @param expense the updated expense data
     * @return the updated expense
     */
    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @Valid @RequestBody Expense expense) {
        logger.debug("PUT /api/expenses/{} - {}", id, expense);
        
        try {
            Expense updatedExpense = expenseService.updateExpense(id, expense);
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
     * Delete an expense
     * 
     * @param id the expense ID
     * @return no content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        logger.debug("DELETE /api/expenses/{}", id);
        
        try {
            expenseService.deleteExpense(id);
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
    public ResponseEntity<ExpenseSummaryDTO> getExpenseSummary() {
        logger.debug("GET /api/expenses/summary");
        
        try {
            ExpenseSummaryDTO summary = expenseService.getExpenseSummary();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            logger.error("Error generating expense summary", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get all distinct categories
     * 
     * @return list of categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        logger.debug("GET /api/expenses/categories");
        
        try {
            List<String> categories = expenseService.getDistinctCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            logger.error("Error retrieving categories", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Export expenses to CSV
     * 
     * @param category optional category filter
     * @param paymentMethod optional payment method filter
     * @param startDate optional start date filter
     * @param endDate optional end date filter
     * @return CSV file as response
     */
    @GetMapping("/export/csv")
    public ResponseEntity<String> exportExpensesToCsv(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) PaymentMethod paymentMethod,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.debug("GET /api/expenses/export/csv");
        
        try {
            ExpenseFilterDTO filter = new ExpenseFilterDTO(category, paymentMethod, startDate, endDate, null, null);
            List<Expense> expenses = expenseService.getFilteredExpenses(filter);
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
