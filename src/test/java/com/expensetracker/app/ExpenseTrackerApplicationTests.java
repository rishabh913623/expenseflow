package com.expensetracker.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Basic application context test to ensure the application starts correctly.
 */
@SpringBootTest
@ActiveProfiles("test")
class ExpenseTrackerApplicationTests {

    @Test
    void contextLoads() {
        // This test ensures that the Spring application context loads successfully
        // If there are any configuration issues, this test will fail
    }
}
