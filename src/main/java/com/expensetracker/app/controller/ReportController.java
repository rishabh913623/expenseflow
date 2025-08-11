package com.expensetracker.app.controller;

import com.expensetracker.app.service.ExpenseService;
import com.expensetracker.app.repository.ExpenseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for reporting and analytics operations.
 * Provides endpoints for various expense reports and analytics.
 * 
 * @author Expense Tracker Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReportController {
    
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    
    private final ExpenseRepository expenseRepository;
    private final ExpenseService expenseService;
    
    @Autowired
    public ReportController(ExpenseRepository expenseRepository, ExpenseService expenseService) {
        this.expenseRepository = expenseRepository;
        this.expenseService = expenseService;
    }
    
    /**
     * Get monthly expense summary
     * 
     * @return monthly summary data
     */
    @GetMapping("/monthly-summary")
    public ResponseEntity<List<Object[]>> getMonthlySummary() {
        logger.debug("GET /api/reports/monthly-summary");
        
        try {
            List<Object[]> monthlySummary = expenseRepository.getMonthlyExpenseSummary();
            return ResponseEntity.ok(monthlySummary);
        } catch (Exception e) {
            logger.error("Error generating monthly summary", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get category-wise totals
     * 
     * @return category totals
     */
    @GetMapping("/category-totals")
    public ResponseEntity<List<Object[]>> getCategoryTotals() {
        logger.debug("GET /api/reports/category-totals");
        
        try {
            List<Object[]> categoryTotals = expenseRepository.getTotalAmountByCategory();
            return ResponseEntity.ok(categoryTotals);
        } catch (Exception e) {
            logger.error("Error generating category totals", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get payment method totals
     * 
     * @return payment method totals
     */
    @GetMapping("/payment-method-totals")
    public ResponseEntity<List<Object[]>> getPaymentMethodTotals() {
        logger.debug("GET /api/reports/payment-method-totals");
        
        try {
            List<Object[]> paymentMethodTotals = expenseRepository.getTotalAmountByPaymentMethod();
            return ResponseEntity.ok(paymentMethodTotals);
        } catch (Exception e) {
            logger.error("Error generating payment method totals", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get category-wise monthly summary for a specific month
     * 
     * @param year the year
     * @param month the month (1-12)
     * @return category-wise monthly summary
     */
    @GetMapping("/monthly-category-summary")
    public ResponseEntity<List<Object[]>> getMonthlyCategorySummary(
            @RequestParam int year,
            @RequestParam int month) {
        
        logger.debug("GET /api/reports/monthly-category-summary - year: {}, month: {}", year, month);
        
        try {
            List<Object[]> monthlyCategorySummary = expenseRepository.getCategoryWiseMonthlySummary(year, month);
            return ResponseEntity.ok(monthlyCategorySummary);
        } catch (Exception e) {
            logger.error("Error generating monthly category summary", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get cash vs UPI totals
     * 
     * @return cash and UPI totals
     */
    @GetMapping("/cash-upi-totals")
    public ResponseEntity<Map<String, Object>> getCashUpiTotals() {
        logger.debug("GET /api/reports/cash-upi-totals");
        
        try {
            Object[] totals = expenseRepository.getTotalCashAndUpiAmounts();
            
            Map<String, Object> result = new HashMap<>();
            result.put("totalCash", totals[0]);
            result.put("totalUpi", totals[1]);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error generating cash/UPI totals", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get comprehensive dashboard data
     * 
     * @return dashboard data with various metrics
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        logger.debug("GET /api/reports/dashboard");
        
        try {
            Map<String, Object> dashboard = new HashMap<>();
            
            // Get summary
            dashboard.put("summary", expenseService.getExpenseSummary());
            
            // Get monthly summary
            dashboard.put("monthlySummary", expenseRepository.getMonthlyExpenseSummary());
            
            // Get category totals
            dashboard.put("categoryTotals", expenseRepository.getTotalAmountByCategory());
            
            // Get payment method totals
            dashboard.put("paymentMethodTotals", expenseRepository.getTotalAmountByPaymentMethod());
            
            // Get cash/UPI totals
            Object[] cashUpiTotals = expenseRepository.getTotalCashAndUpiAmounts();
            Map<String, Object> cashUpi = new HashMap<>();
            cashUpi.put("totalCash", cashUpiTotals[0]);
            cashUpi.put("totalUpi", cashUpiTotals[1]);
            dashboard.put("cashUpiTotals", cashUpi);
            
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            logger.error("Error generating dashboard data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
