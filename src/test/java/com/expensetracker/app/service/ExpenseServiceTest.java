package com.expensetracker.app.service;

import com.expensetracker.app.dto.ExpenseFilterDTO;
import com.expensetracker.app.dto.ExpenseSummaryDTO;
import com.expensetracker.app.model.Expense;
import com.expensetracker.app.model.PaymentMethod;
import com.expensetracker.app.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ExpenseService
 */
@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {
    
    @Mock
    private ExpenseRepository expenseRepository;
    
    @InjectMocks
    private ExpenseService expenseService;
    
    private Expense testExpense;
    
    @BeforeEach
    void setUp() {
        testExpense = new Expense();
        testExpense.setId(1L);
        testExpense.setAmount(new BigDecimal("100.00"));
        testExpense.setCategory("Food");
        testExpense.setExpenseDate(LocalDate.now());
        testExpense.setPaymentMethod(PaymentMethod.CASH);
        testExpense.setNotes("Test expense");
    }
    
    @Test
    void testCreateExpense_Success() {
        // Given
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);
        
        // When
        Expense result = expenseService.createExpense(testExpense);
        
        // Then
        assertNotNull(result);
        assertEquals(testExpense.getId(), result.getId());
        assertEquals(testExpense.getAmount(), result.getAmount());
        verify(expenseRepository, times(1)).save(testExpense);
    }
    
    @Test
    void testCreateExpense_InvalidAmount() {
        // Given
        testExpense.setAmount(new BigDecimal("-10.00"));
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            expenseService.createExpense(testExpense);
        });
        
        verify(expenseRepository, never()).save(any(Expense.class));
    }
    
    @Test
    void testCreateExpense_MissingCategory() {
        // Given
        testExpense.setCategory(null);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            expenseService.createExpense(testExpense);
        });
        
        verify(expenseRepository, never()).save(any(Expense.class));
    }
    
    @Test
    void testCreateExpense_UpiWithoutVpa() {
        // Given
        testExpense.setPaymentMethod(PaymentMethod.UPI);
        testExpense.setUpiVpa(null);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            expenseService.createExpense(testExpense);
        });
        
        verify(expenseRepository, never()).save(any(Expense.class));
    }
    
    @Test
    void testUpdateExpense_Success() {
        // Given
        Expense updatedExpense = new Expense();
        updatedExpense.setAmount(new BigDecimal("150.00"));
        updatedExpense.setCategory("Travel");
        updatedExpense.setExpenseDate(LocalDate.now());
        updatedExpense.setPaymentMethod(PaymentMethod.UPI);
        updatedExpense.setUpiVpa("test@upi");
        updatedExpense.setTransactionId("TXN123");
        
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);
        
        // When
        Expense result = expenseService.updateExpense(1L, updatedExpense);
        
        // Then
        assertNotNull(result);
        verify(expenseRepository, times(1)).findById(1L);
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }
    
    @Test
    void testUpdateExpense_NotFound() {
        // Given
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            expenseService.updateExpense(1L, testExpense);
        });
        
        verify(expenseRepository, times(1)).findById(1L);
        verify(expenseRepository, never()).save(any(Expense.class));
    }
    
    @Test
    void testDeleteExpense_Success() {
        // Given
        when(expenseRepository.existsById(1L)).thenReturn(true);
        
        // When
        expenseService.deleteExpense(1L);
        
        // Then
        verify(expenseRepository, times(1)).existsById(1L);
        verify(expenseRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void testDeleteExpense_NotFound() {
        // Given
        when(expenseRepository.existsById(1L)).thenReturn(false);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            expenseService.deleteExpense(1L);
        });
        
        verify(expenseRepository, times(1)).existsById(1L);
        verify(expenseRepository, never()).deleteById(any(Long.class));
    }
    
    @Test
    void testGetExpenseById_Success() {
        // Given
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense));
        
        // When
        Expense result = expenseService.getExpenseById(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(testExpense.getId(), result.getId());
        verify(expenseRepository, times(1)).findById(1L);
    }
    
    @Test
    void testGetExpenseById_NotFound() {
        // Given
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            expenseService.getExpenseById(1L);
        });
        
        verify(expenseRepository, times(1)).findById(1L);
    }
    
    @Test
    void testGetAllExpenses() {
        // Given
        List<Expense> expenses = Arrays.asList(testExpense);
        when(expenseRepository.findAllByOrderByExpenseDateDesc()).thenReturn(expenses);
        
        // When
        List<Expense> result = expenseService.getAllExpenses();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testExpense.getId(), result.get(0).getId());
        verify(expenseRepository, times(1)).findAllByOrderByExpenseDateDesc();
    }
    
    @Test
    void testGetFilteredExpenses_NoFilters() {
        // Given
        ExpenseFilterDTO filter = new ExpenseFilterDTO();
        List<Expense> expenses = Arrays.asList(testExpense);
        when(expenseRepository.findAllByOrderByExpenseDateDesc()).thenReturn(expenses);
        
        // When
        List<Expense> result = expenseService.getFilteredExpenses(filter);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(expenseRepository, times(1)).findAllByOrderByExpenseDateDesc();
    }
    
    @Test
    void testGetFilteredExpenses_WithCategory() {
        // Given
        ExpenseFilterDTO filter = new ExpenseFilterDTO();
        filter.setCategory("Food");
        List<Expense> expenses = Arrays.asList(testExpense);
        when(expenseRepository.findByCategory("Food")).thenReturn(expenses);
        
        // When
        List<Expense> result = expenseService.getFilteredExpenses(filter);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(expenseRepository, times(1)).findByCategory("Food");
    }
    
    @Test
    void testGetExpenseSummary() {
        // Given
        List<Expense> expenses = Arrays.asList(testExpense);
        when(expenseRepository.findAll()).thenReturn(expenses);
        
        // When
        ExpenseSummaryDTO result = expenseService.getExpenseSummary();
        
        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("100.00"), result.getTotalAmount());
        assertEquals(1L, result.getTotalTransactions());
        assertNotNull(result.getCategoryTotals());
        assertNotNull(result.getPaymentMethodTotals());
        verify(expenseRepository, times(1)).findAll();
    }
    
    @Test
    void testGetDistinctCategories() {
        // Given
        List<String> categories = Arrays.asList("Food", "Travel", "Utilities");
        when(expenseRepository.findDistinctCategories()).thenReturn(categories);
        
        // When
        List<String> result = expenseService.getDistinctCategories();
        
        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("Food"));
        assertTrue(result.contains("Travel"));
        assertTrue(result.contains("Utilities"));
        verify(expenseRepository, times(1)).findDistinctCategories();
    }
}
