package com.expensetracker.app.model;

/**
 * Enumeration representing the different payment methods available for expenses.
 * 
 * @author Expense Tracker Team
 * @version 1.0
 */
public enum PaymentMethod {
    /**
     * Cash payment method
     */
    CASH("Cash"),
    
    /**
     * UPI (Unified Payments Interface) payment method
     */
    UPI("UPI");
    
    private final String displayName;
    
    /**
     * Constructor for PaymentMethod enum.
     * 
     * @param displayName the display name for the payment method
     */
    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Gets the display name of the payment method.
     * 
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Returns the string representation of the payment method.
     * 
     * @return the display name
     */
    @Override
    public String toString() {
        return displayName;
    }
}
