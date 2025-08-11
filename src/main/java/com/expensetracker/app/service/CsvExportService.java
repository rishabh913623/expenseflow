package com.expensetracker.app.service;

import com.expensetracker.app.model.Expense;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service class for exporting expense data to CSV format.
 * 
 * @author Expense Tracker Team
 * @version 1.0
 */
@Service
public class CsvExportService {
    
    private static final Logger logger = LoggerFactory.getLogger(CsvExportService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Exports a list of expenses to CSV format
     * 
     * @param expenses the list of expenses to export
     * @return CSV content as string
     */
    public String exportExpensesToCsv(List<Expense> expenses) {
        logger.debug("Exporting {} expenses to CSV", expenses.size());
        
        StringWriter stringWriter = new StringWriter();
        
        try (CSVWriter csvWriter = new CSVWriter(stringWriter)) {
            // Write header
            String[] header = {
                "ID", "Amount", "Category", "Expense Date", "Payment Method",
                "Cash Amount", "UPI Amount", "UPI VPA", "Transaction ID", 
                "Payer Name", "Notes", "Created At", "Updated At"
            };
            csvWriter.writeNext(header);
            
            // Write data rows
            for (Expense expense : expenses) {
                String[] row = {
                    expense.getId() != null ? expense.getId().toString() : "",
                    expense.getAmount() != null ? expense.getAmount().toString() : "",
                    expense.getCategory() != null ? expense.getCategory() : "",
                    expense.getExpenseDate() != null ? expense.getExpenseDate().format(DATE_FORMATTER) : "",
                    expense.getPaymentMethod() != null ? expense.getPaymentMethod().toString() : "",
                    expense.getCashAmount() != null ? expense.getCashAmount().toString() : "0.00",
                    expense.getUpiAmount() != null ? expense.getUpiAmount().toString() : "0.00",
                    expense.getUpiVpa() != null ? expense.getUpiVpa() : "",
                    expense.getTransactionId() != null ? expense.getTransactionId() : "",
                    expense.getPayerName() != null ? expense.getPayerName() : "",
                    expense.getNotes() != null ? expense.getNotes() : "",
                    expense.getCreatedAt() != null ? expense.getCreatedAt().format(DATETIME_FORMATTER) : "",
                    expense.getUpdatedAt() != null ? expense.getUpdatedAt().format(DATETIME_FORMATTER) : ""
                };
                csvWriter.writeNext(row);
            }
            
            csvWriter.flush();
            
        } catch (Exception e) {
            logger.error("Error exporting expenses to CSV", e);
            throw new RuntimeException("Failed to export expenses to CSV", e);
        }
        
        String csvContent = stringWriter.toString();
        logger.info("Successfully exported {} expenses to CSV", expenses.size());
        
        return csvContent;
    }
    
    /**
     * Gets the CSV filename with current timestamp
     * 
     * @return filename for CSV export
     */
    public String getCsvFilename() {
        return "expenses_" + java.time.LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
    }
}
