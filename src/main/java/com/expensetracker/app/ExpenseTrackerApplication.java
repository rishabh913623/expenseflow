package com.expensetracker.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Expense Tracker application.
 * This class serves as the entry point for the Spring Boot application.
 * 
 * @author Expense Tracker Team
 * @version 1.0
 */
@SpringBootApplication
public class ExpenseTrackerApplication {

    /**
     * Main method to start the Spring Boot application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ExpenseTrackerApplication.class, args);
    }
}
