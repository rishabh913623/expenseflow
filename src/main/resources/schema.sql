-- Expense Tracker Database Schema
-- This script creates the database schema for the expense tracker application

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    budget DECIMAL(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for users table
CREATE INDEX IF NOT EXISTS idx_username ON users (username);
CREATE INDEX IF NOT EXISTS idx_email ON users (email);
CREATE INDEX IF NOT EXISTS idx_created_at ON users (created_at);

-- Create expenses table
CREATE TABLE IF NOT EXISTS expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL CHECK (amount > 0),
    category VARCHAR(50) NOT NULL,
    expense_date DATE NOT NULL,
    payment_method VARCHAR(10) NOT NULL CHECK (payment_method IN ('CASH', 'UPI')),

    -- Cash specific fields
    cash_amount DECIMAL(10, 2) DEFAULT 0.00,

    -- UPI specific fields
    upi_amount DECIMAL(10, 2) DEFAULT 0.00,
    upi_vpa VARCHAR(100),
    transaction_id VARCHAR(100),
    payer_name VARCHAR(100),

    -- Common fields
    notes CLOB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Foreign key constraint
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for expenses table
CREATE INDEX IF NOT EXISTS idx_expense_date ON expenses (expense_date);
CREATE INDEX IF NOT EXISTS idx_category ON expenses (category);
CREATE INDEX IF NOT EXISTS idx_payment_method ON expenses (payment_method);
CREATE INDEX IF NOT EXISTS idx_created_at ON expenses (created_at);

-- Create categories table for reference (optional enhancement)
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert default categories
INSERT INTO categories (name, description) VALUES
('Food', 'Food and dining expenses'),
('Travel', 'Transportation and travel expenses'),
('Utilities', 'Utility bills and services'),
('Entertainment', 'Entertainment and leisure activities'),
('Healthcare', 'Medical and healthcare expenses'),
('Shopping', 'Shopping and retail purchases'),
('Education', 'Educational expenses'),
('Miscellaneous', 'Other miscellaneous expenses');

-- Insert demo user (password: demo)
INSERT INTO users (username, email, password, budget) VALUES
('demo', 'demo@example.com', '$2a$10$8K2LZ5fHvqQ3Jc8QyQX5Ue4oKoEa3Ro9llC/.og/at2.uheWG/igi', 5000.00);

-- Note: Views removed for H2 compatibility
-- They can be added back when using a different database
