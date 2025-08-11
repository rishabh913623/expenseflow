package com.expensetracker.app.dto;

import com.expensetracker.app.model.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

/**
 * Data Transfer Object for expense filtering criteria.
 * Used to encapsulate filter parameters for expense queries.
 * 
 * @author Expense Tracker Team
 * @version 1.0
 */
public class ExpenseFilterDTO {
    
    private String category;
    private PaymentMethod paymentMethod;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    private String upiVpa;
    private String transactionId;
    
    /**
     * Default constructor
     */
    public ExpenseFilterDTO() {
    }
    
    /**
     * Constructor with all fields
     */
    public ExpenseFilterDTO(String category, PaymentMethod paymentMethod, 
                           LocalDate startDate, LocalDate endDate, 
                           String upiVpa, String transactionId) {
        this.category = category;
        this.paymentMethod = paymentMethod;
        this.startDate = startDate;
        this.endDate = endDate;
        this.upiVpa = upiVpa;
        this.transactionId = transactionId;
    }
    
    // Getters and Setters
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public String getUpiVpa() {
        return upiVpa;
    }
    
    public void setUpiVpa(String upiVpa) {
        this.upiVpa = upiVpa;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    /**
     * Checks if any filter criteria is specified
     * 
     * @return true if any filter is applied, false otherwise
     */
    public boolean hasFilters() {
        return category != null || paymentMethod != null || 
               startDate != null || endDate != null || 
               upiVpa != null || transactionId != null;
    }
    
    @Override
    public String toString() {
        return "ExpenseFilterDTO{" +
                "category='" + category + '\'' +
                ", paymentMethod=" + paymentMethod +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", upiVpa='" + upiVpa + '\'' +
                ", transactionId='" + transactionId + '\'' +
                '}';
    }
}
