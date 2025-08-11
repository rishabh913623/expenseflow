-- Expense Tracker Database Schema
-- This script creates the database schema for the expense tracker application

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS expense_tracker;
USE expense_tracker;

-- Create expenses table
CREATE TABLE IF NOT EXISTS expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(10, 2) NOT NULL CHECK (amount > 0),
    category VARCHAR(50) NOT NULL,
    expense_date DATE NOT NULL,
    payment_method ENUM('CASH', 'UPI') NOT NULL,
    
    -- Cash specific fields
    cash_amount DECIMAL(10, 2) DEFAULT 0.00,
    
    -- UPI specific fields
    upi_amount DECIMAL(10, 2) DEFAULT 0.00,
    upi_vpa VARCHAR(100),
    transaction_id VARCHAR(100),
    payer_name VARCHAR(100),
    
    -- Common fields
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Indexes for better query performance
    INDEX idx_expense_date (expense_date),
    INDEX idx_category (category),
    INDEX idx_payment_method (payment_method),
    INDEX idx_created_at (created_at),
    
    -- Constraints
    CONSTRAINT chk_payment_amounts CHECK (
        (payment_method = 'CASH' AND cash_amount > 0 AND upi_amount = 0) OR
        (payment_method = 'UPI' AND upi_amount > 0 AND cash_amount = 0)
    ),
    CONSTRAINT chk_upi_fields CHECK (
        (payment_method = 'UPI' AND upi_vpa IS NOT NULL AND transaction_id IS NOT NULL) OR
        (payment_method = 'CASH')
    )
);

-- Create categories table for reference (optional enhancement)
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert default categories
INSERT IGNORE INTO categories (name, description) VALUES
('Food', 'Food and dining expenses'),
('Travel', 'Transportation and travel expenses'),
('Utilities', 'Utility bills and services'),
('Entertainment', 'Entertainment and leisure activities'),
('Healthcare', 'Medical and healthcare expenses'),
('Shopping', 'Shopping and retail purchases'),
('Education', 'Educational expenses'),
('Miscellaneous', 'Other miscellaneous expenses');

-- Create a view for expense summaries
CREATE OR REPLACE VIEW expense_summary AS
SELECT 
    category,
    payment_method,
    COUNT(*) as transaction_count,
    SUM(amount) as total_amount,
    AVG(amount) as average_amount,
    MIN(expense_date) as earliest_date,
    MAX(expense_date) as latest_date
FROM expenses
GROUP BY category, payment_method;

-- Create a view for monthly summaries
CREATE OR REPLACE VIEW monthly_expense_summary AS
SELECT 
    YEAR(expense_date) as year,
    MONTH(expense_date) as month,
    MONTHNAME(expense_date) as month_name,
    category,
    payment_method,
    COUNT(*) as transaction_count,
    SUM(amount) as total_amount
FROM expenses
GROUP BY YEAR(expense_date), MONTH(expense_date), category, payment_method
ORDER BY year DESC, month DESC;
