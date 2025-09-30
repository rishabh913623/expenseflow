package com.expensetracker.app.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.expensetracker.app.model.Expense;
import com.expensetracker.app.model.PaymentMethod;
import com.expensetracker.app.model.User;

/**
 * Repository interface for Expense entity operations.
 * Provides CRUD operations and custom query methods for expense management.
 * 
 * @author Expense Tracker Team
 * @version 1.0
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    /**
     * Find expenses by user
     *
     * @param user the user to filter by
     * @return list of expenses for the specified user
     */
    List<Expense> findByUser(User user);

    /**
     * Find expenses by user and category
     *
     * @param user the user to filter by
     * @param category the category to filter by
     * @return list of expenses for the specified user and category
     */
    List<Expense> findByUserAndCategory(User user, String category);

    /**
     * Find expenses by user and payment method
     *
     * @param user the user to filter by
     * @param paymentMethod the payment method to filter by
     * @return list of expenses for the specified user and payment method
     */
    List<Expense> findByUserAndPaymentMethod(User user, PaymentMethod paymentMethod);

    /**
     * Find expenses by user within a date range
     *
     * @param user the user to filter by
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of expenses for the specified user within the date range
     */
    List<Expense> findByUserAndExpenseDateBetween(User user, LocalDate startDate, LocalDate endDate);

    /**
     * Find expenses by user, category and date range
     *
     * @param user the user to filter by
     * @param category the category to filter by
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of expenses matching the criteria
     */
    List<Expense> findByUserAndCategoryAndExpenseDateBetween(User user, String category, LocalDate startDate, LocalDate endDate);

    /**
     * Find expenses by user, payment method and date range
     *
     * @param user the user to filter by
     * @param paymentMethod the payment method to filter by
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of expenses matching the criteria
     */
    List<Expense> findByUserAndPaymentMethodAndExpenseDateBetween(User user, PaymentMethod paymentMethod, LocalDate startDate, LocalDate endDate);

    /**
     * Find expenses by user, category, payment method and date range
     *
     * @param user the user to filter by
     * @param category the category to filter by
     * @param paymentMethod the payment method to filter by
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of expenses matching all criteria
     */
    List<Expense> findByUserAndCategoryAndPaymentMethodAndExpenseDateBetween(
            User user, String category, PaymentMethod paymentMethod, LocalDate startDate, LocalDate endDate);

    /**
     * Find expenses by user ordered by date (most recent first)
     *
     * @param user the user to filter by
     * @return list of expenses for the specified user ordered by expense date descending
     */
    List<Expense> findByUserOrderByExpenseDateDesc(User user);

    /**
     * Find expenses by user and UPI VPA
     *
     * @param user the user to filter by
     * @param upiVpa the UPI VPA to search for
     * @return list of expenses for the specified user with the specified UPI VPA
     */
    List<Expense> findByUserAndUpiVpaContainingIgnoreCase(User user, String upiVpa);

    /**
     * Find expenses by user and transaction ID
     *
     * @param user the user to filter by
     * @param transactionId the transaction ID to search for
     * @return list of expenses for the specified user with the specified transaction ID
     */
    List<Expense> findByUserAndTransactionIdContainingIgnoreCase(User user, String transactionId);

    /**
     * Get distinct categories for a user
     *
     * @param user the user to filter by
     * @return list of distinct categories for the specified user
     */
    @Query("SELECT DISTINCT e.category FROM Expense e WHERE e.user = :user ORDER BY e.category")
    List<String> findDistinctCategoriesByUser(@Param("user") User user);

    /**
     * Find expenses by category
     *
     * @param category the category to filter by
     * @return list of expenses in the specified category
     */
    List<Expense> findByCategory(String category);
    
    /**
     * Find expenses by payment method
     * 
     * @param paymentMethod the payment method to filter by
     * @return list of expenses with the specified payment method
     */
    List<Expense> findByPaymentMethod(PaymentMethod paymentMethod);
    
    /**
     * Find expenses within a date range
     * 
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of expenses within the date range
     */
    List<Expense> findByExpenseDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find expenses by category and date range
     * 
     * @param category the category to filter by
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of expenses matching the criteria
     */
    List<Expense> findByCategoryAndExpenseDateBetween(String category, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find expenses by payment method and date range
     * 
     * @param paymentMethod the payment method to filter by
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of expenses matching the criteria
     */
    List<Expense> findByPaymentMethodAndExpenseDateBetween(PaymentMethod paymentMethod, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find expenses by category, payment method, and date range
     * 
     * @param category the category to filter by
     * @param paymentMethod the payment method to filter by
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of expenses matching all criteria
     */
    List<Expense> findByCategoryAndPaymentMethodAndExpenseDateBetween(
            String category, PaymentMethod paymentMethod, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get total amount by category
     * 
     * @return list of objects containing category and total amount
     */
    @Query("SELECT e.category as category, SUM(e.amount) as totalAmount " +
           "FROM Expense e GROUP BY e.category ORDER BY totalAmount DESC")
    List<Object[]> getTotalAmountByCategory();
    
    /**
     * Get total amount by payment method
     * 
     * @return list of objects containing payment method and total amount
     */
    @Query("SELECT e.paymentMethod as paymentMethod, SUM(e.amount) as totalAmount " +
           "FROM Expense e GROUP BY e.paymentMethod ORDER BY totalAmount DESC")
    List<Object[]> getTotalAmountByPaymentMethod();
    
    /**
     * Get monthly expense summary
     * 
     * @return list of objects containing year, month, and total amount
     */
    @Query("SELECT YEAR(e.expenseDate) as year, MONTH(e.expenseDate) as month, " +
           "SUM(e.amount) as totalAmount, COUNT(e) as transactionCount " +
           "FROM Expense e GROUP BY YEAR(e.expenseDate), MONTH(e.expenseDate) " +
           "ORDER BY year DESC, month DESC")
    List<Object[]> getMonthlyExpenseSummary();
    
    /**
     * Get category-wise monthly summary
     * 
     * @param year the year to filter by
     * @param month the month to filter by
     * @return list of objects containing category and total amount for the specified month
     */
    @Query("SELECT e.category as category, SUM(e.amount) as totalAmount, COUNT(e) as transactionCount " +
           "FROM Expense e WHERE YEAR(e.expenseDate) = :year AND MONTH(e.expenseDate) = :month " +
           "GROUP BY e.category ORDER BY totalAmount DESC")
    List<Object[]> getCategoryWiseMonthlySummary(@Param("year") int year, @Param("month") int month);
    
    /**
     * Get total cash and UPI amounts
     * 
     * @return list containing total cash amount and total UPI amount
     */
    @Query("SELECT SUM(e.cashAmount) as totalCash, SUM(e.upiAmount) as totalUpi FROM Expense e")
    Object[] getTotalCashAndUpiAmounts();
    
    /**
     * Find expenses by UPI VPA
     * 
     * @param upiVpa the UPI VPA to search for
     * @return list of expenses with the specified UPI VPA
     */
    List<Expense> findByUpiVpaContainingIgnoreCase(String upiVpa);
    
    /**
     * Find expenses by transaction ID
     * 
     * @param transactionId the transaction ID to search for
     * @return list of expenses with the specified transaction ID
     */
    List<Expense> findByTransactionIdContainingIgnoreCase(String transactionId);
    
    /**
     * Get all distinct categories
     * 
     * @return list of distinct categories
     */
    @Query("SELECT DISTINCT e.category FROM Expense e ORDER BY e.category")
    List<String> findDistinctCategories();
    
    /**
     * Get expenses ordered by date (most recent first)
     * 
     * @return list of expenses ordered by expense date descending
     */
    List<Expense> findAllByOrderByExpenseDateDesc();
    
    /**
     * Get expenses for a specific date
     * 
     * @param date the specific date
     * @return list of expenses for the specified date
     */
    List<Expense> findByExpenseDate(LocalDate date);
}
