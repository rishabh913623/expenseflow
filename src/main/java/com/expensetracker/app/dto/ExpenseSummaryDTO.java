package com.expensetracker.app.dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Data Transfer Object for expense summary information.
 * Used to encapsulate aggregated expense data for reporting.
 * 
 * @author Expense Tracker Team
 * @version 1.0
 */
public class ExpenseSummaryDTO {
    
    private BigDecimal totalAmount;
    private BigDecimal totalCashAmount;
    private BigDecimal totalUpiAmount;
    private long totalTransactions;
    private Map<String, BigDecimal> categoryTotals;
    private Map<String, BigDecimal> paymentMethodTotals;
    
    /**
     * Default constructor
     */
    public ExpenseSummaryDTO() {
    }
    
    /**
     * Constructor with basic totals
     */
    public ExpenseSummaryDTO(BigDecimal totalAmount, BigDecimal totalCashAmount, 
                            BigDecimal totalUpiAmount, long totalTransactions) {
        this.totalAmount = totalAmount;
        this.totalCashAmount = totalCashAmount;
        this.totalUpiAmount = totalUpiAmount;
        this.totalTransactions = totalTransactions;
    }
    
    // Getters and Setters
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public BigDecimal getTotalCashAmount() {
        return totalCashAmount;
    }
    
    public void setTotalCashAmount(BigDecimal totalCashAmount) {
        this.totalCashAmount = totalCashAmount;
    }
    
    public BigDecimal getTotalUpiAmount() {
        return totalUpiAmount;
    }
    
    public void setTotalUpiAmount(BigDecimal totalUpiAmount) {
        this.totalUpiAmount = totalUpiAmount;
    }
    
    public long getTotalTransactions() {
        return totalTransactions;
    }
    
    public void setTotalTransactions(long totalTransactions) {
        this.totalTransactions = totalTransactions;
    }
    
    public Map<String, BigDecimal> getCategoryTotals() {
        return categoryTotals;
    }
    
    public void setCategoryTotals(Map<String, BigDecimal> categoryTotals) {
        this.categoryTotals = categoryTotals;
    }
    
    public Map<String, BigDecimal> getPaymentMethodTotals() {
        return paymentMethodTotals;
    }
    
    public void setPaymentMethodTotals(Map<String, BigDecimal> paymentMethodTotals) {
        this.paymentMethodTotals = paymentMethodTotals;
    }
    
    @Override
    public String toString() {
        return "ExpenseSummaryDTO{" +
                "totalAmount=" + totalAmount +
                ", totalCashAmount=" + totalCashAmount +
                ", totalUpiAmount=" + totalUpiAmount +
                ", totalTransactions=" + totalTransactions +
                ", categoryTotals=" + categoryTotals +
                ", paymentMethodTotals=" + paymentMethodTotals +
                '}';
    }
}
