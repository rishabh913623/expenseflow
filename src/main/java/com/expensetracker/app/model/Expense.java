package com.expensetracker.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity class representing an expense in the expense tracker application.
 * This class maps to the 'expenses' table in the database.
 * 
 * @author Expense Tracker Team
 * @version 1.0
 */
@Entity
@Table(name = "expenses")
public class Expense {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @NotBlank(message = "Category is required")
    @Size(max = 50, message = "Category must not exceed 50 characters")
    @Column(name = "category", nullable = false, length = 50)
    private String category;
    
    @NotNull(message = "Expense date is required")
    @Column(name = "expense_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expenseDate;
    
    @NotNull(message = "Payment method is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;
    
    // Cash specific fields
    @Column(name = "cash_amount", precision = 10, scale = 2)
    private BigDecimal cashAmount = BigDecimal.ZERO;
    
    // UPI specific fields
    @Column(name = "upi_amount", precision = 10, scale = 2)
    private BigDecimal upiAmount = BigDecimal.ZERO;
    
    @Size(max = 100, message = "UPI VPA must not exceed 100 characters")
    @Column(name = "upi_vpa", length = 100)
    private String upiVpa;
    
    @Size(max = 100, message = "Transaction ID must not exceed 100 characters")
    @Column(name = "transaction_id", length = 100)
    private String transactionId;
    
    @Size(max = 100, message = "Payer name must not exceed 100 characters")
    @Column(name = "payer_name", length = 100)
    private String payerName;
    
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Default constructor
     */
    public Expense() {
    }
    
    /**
     * Constructor with basic fields
     */
    public Expense(BigDecimal amount, String category, LocalDate expenseDate, PaymentMethod paymentMethod) {
        this.amount = amount;
        this.category = category;
        this.expenseDate = expenseDate;
        this.paymentMethod = paymentMethod;
        updateAmountFields();
    }
    
    /**
     * Sets timestamps before persisting
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        updateAmountFields();
    }
    
    /**
     * Updates timestamp before updating
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        updateAmountFields();
    }
    
    /**
     * Updates cash and UPI amount fields based on payment method
     */
    private void updateAmountFields() {
        if (paymentMethod == PaymentMethod.CASH) {
            this.cashAmount = this.amount;
            this.upiAmount = BigDecimal.ZERO;
        } else if (paymentMethod == PaymentMethod.UPI) {
            this.upiAmount = this.amount;
            this.cashAmount = BigDecimal.ZERO;
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
        updateAmountFields();
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public LocalDate getExpenseDate() {
        return expenseDate;
    }
    
    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }
    
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        updateAmountFields();
    }
    
    public BigDecimal getCashAmount() {
        return cashAmount;
    }
    
    public void setCashAmount(BigDecimal cashAmount) {
        this.cashAmount = cashAmount;
    }
    
    public BigDecimal getUpiAmount() {
        return upiAmount;
    }
    
    public void setUpiAmount(BigDecimal upiAmount) {
        this.upiAmount = upiAmount;
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
    
    public String getPayerName() {
        return payerName;
    }
    
    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", expenseDate=" + expenseDate +
                ", paymentMethod=" + paymentMethod +
                ", notes='" + notes + '\'' +
                '}';
    }
}
