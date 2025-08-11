package com.expensetracker.app.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.expensetracker.app.model.Expense;
import com.expensetracker.app.model.PaymentMethod;
import com.expensetracker.app.repository.ExpenseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Integration tests for ExpenseController
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class ExpenseControllerIntegrationTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        expenseRepository.deleteAll();
    }
    
    @Test
    void testCreateExpense_Success() throws Exception {
        // Given
        Expense expense = new Expense();
        expense.setAmount(new BigDecimal("100.00"));
        expense.setCategory("Food");
        expense.setExpenseDate(LocalDate.now());
        expense.setPaymentMethod(PaymentMethod.CASH);
        expense.setNotes("Test expense");
        
        // When & Then
        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expense)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount", is(100.0)))
                .andExpect(jsonPath("$.category", is("Food")))
                .andExpect(jsonPath("$.paymentMethod", is("CASH")))
                .andExpect(jsonPath("$.notes", is("Test expense")));
    }
    
    @Test
    void testCreateExpense_UpiSuccess() throws Exception {
        // Given
        Expense expense = new Expense();
        expense.setAmount(new BigDecimal("150.00"));
        expense.setCategory("Travel");
        expense.setExpenseDate(LocalDate.now());
        expense.setPaymentMethod(PaymentMethod.UPI);
        expense.setUpiVpa("test@upi");
        expense.setTransactionId("TXN123");
        expense.setPayerName("John Doe");
        expense.setNotes("UPI payment test");
        
        // When & Then
        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expense)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount", is(150.0)))
                .andExpect(jsonPath("$.category", is("Travel")))
                .andExpect(jsonPath("$.paymentMethod", is("UPI")))
                .andExpect(jsonPath("$.upiVpa", is("test@upi")))
                .andExpect(jsonPath("$.transactionId", is("TXN123")))
                .andExpect(jsonPath("$.payerName", is("John Doe")));
    }
    
    @Test
    void testCreateExpense_InvalidAmount() throws Exception {
        // Given
        Expense expense = new Expense();
        expense.setAmount(new BigDecimal("-10.00"));
        expense.setCategory("Food");
        expense.setExpenseDate(LocalDate.now());
        expense.setPaymentMethod(PaymentMethod.CASH);
        
        // When & Then
        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expense)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testGetAllExpenses() throws Exception {
        // Given
        Expense expense1 = createTestExpense("Food", PaymentMethod.CASH, new BigDecimal("50.00"));
        Expense expense2 = createTestExpense("Travel", PaymentMethod.UPI, new BigDecimal("100.00"));
        expenseRepository.save(expense1);
        expenseRepository.save(expense2);
        
        // When & Then
        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].category", containsInAnyOrder("Food", "Travel")))
                .andExpect(jsonPath("$[*].paymentMethod", containsInAnyOrder("CASH", "UPI")));
    }
    
    @Test
    void testGetExpenseById_Success() throws Exception {
        // Given
        Expense expense = createTestExpense("Food", PaymentMethod.CASH, new BigDecimal("75.00"));
        Expense savedExpense = expenseRepository.save(expense);
        
        // When & Then
        mockMvc.perform(get("/api/expenses/{id}", savedExpense.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedExpense.getId().intValue())))
                .andExpect(jsonPath("$.category", is("Food")))
                .andExpect(jsonPath("$.amount", is(75.0)));
    }
    
    @Test
    void testGetExpenseById_NotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/expenses/{id}", 999L))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testUpdateExpense_Success() throws Exception {
        // Given
        Expense expense = createTestExpense("Food", PaymentMethod.CASH, new BigDecimal("50.00"));
        Expense savedExpense = expenseRepository.save(expense);
        
        savedExpense.setAmount(new BigDecimal("75.00"));
        savedExpense.setCategory("Travel");
        
        // When & Then
        mockMvc.perform(put("/api/expenses/{id}", savedExpense.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(savedExpense)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(75.0)))
                .andExpect(jsonPath("$.category", is("Travel")));
    }
    
    @Test
    void testDeleteExpense_Success() throws Exception {
        // Given
        Expense expense = createTestExpense("Food", PaymentMethod.CASH, new BigDecimal("50.00"));
        Expense savedExpense = expenseRepository.save(expense);
        
        // When & Then
        mockMvc.perform(delete("/api/expenses/{id}", savedExpense.getId()))
                .andExpect(status().isNoContent());
        
        // Verify deletion
        mockMvc.perform(get("/api/expenses/{id}", savedExpense.getId()))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testGetExpensesWithFilters() throws Exception {
        // Given
        Expense expense1 = createTestExpense("Food", PaymentMethod.CASH, new BigDecimal("50.00"));
        Expense expense2 = createTestExpense("Travel", PaymentMethod.UPI, new BigDecimal("100.00"));
        Expense expense3 = createTestExpense("Food", PaymentMethod.UPI, new BigDecimal("75.00"));
        
        expenseRepository.save(expense1);
        expenseRepository.save(expense2);
        expenseRepository.save(expense3);
        
        // When & Then - Filter by category
        mockMvc.perform(get("/api/expenses")
                .param("category", "Food"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].category", everyItem(is("Food"))));
        
        // Filter by payment method
        mockMvc.perform(get("/api/expenses")
                .param("paymentMethod", "UPI"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].paymentMethod", everyItem(is("UPI"))));
    }
    
    @Test
    void testGetExpenseSummary() throws Exception {
        // Given
        Expense expense1 = createTestExpense("Food", PaymentMethod.CASH, new BigDecimal("50.00"));
        Expense expense2 = createTestExpense("Travel", PaymentMethod.UPI, new BigDecimal("100.00"));
        expenseRepository.save(expense1);
        expenseRepository.save(expense2);
        
        // When & Then
        mockMvc.perform(get("/api/expenses/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount", is(150.0)))
                .andExpect(jsonPath("$.totalTransactions", is(2)))
                .andExpect(jsonPath("$.totalCashAmount", is(50.0)))
                .andExpect(jsonPath("$.totalUpiAmount", is(100.0)))
                .andExpect(jsonPath("$.categoryTotals").exists())
                .andExpect(jsonPath("$.paymentMethodTotals").exists());
    }
    
    @Test
    void testGetCategories() throws Exception {
        // Given
        Expense expense1 = createTestExpense("Food", PaymentMethod.CASH, new BigDecimal("50.00"));
        Expense expense2 = createTestExpense("Travel", PaymentMethod.UPI, new BigDecimal("100.00"));
        expenseRepository.save(expense1);
        expenseRepository.save(expense2);
        
        // When & Then
        mockMvc.perform(get("/api/expenses/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$", containsInAnyOrder("Food", "Travel")));
    }
    
    @Test
    void testExportToCsv() throws Exception {
        // Given
        Expense expense = createTestExpense("Food", PaymentMethod.CASH, new BigDecimal("50.00"));
        expenseRepository.save(expense);
        
        // When & Then
        mockMvc.perform(get("/api/expenses/export/csv"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/csv"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().string(containsString("ID,Amount,Category")))
                .andExpect(content().string(containsString("Food")))
                .andExpect(content().string(containsString("50.00")));
    }
    
    private Expense createTestExpense(String category, PaymentMethod paymentMethod, BigDecimal amount) {
        Expense expense = new Expense();
        expense.setAmount(amount);
        expense.setCategory(category);
        expense.setExpenseDate(LocalDate.now());
        expense.setPaymentMethod(paymentMethod);
        
        if (paymentMethod == PaymentMethod.UPI) {
            expense.setUpiVpa("test@upi");
            expense.setTransactionId("TXN" + System.currentTimeMillis());
            expense.setPayerName("Test User");
        }
        
        expense.setNotes("Test expense for " + category);
        return expense;
    }
}
