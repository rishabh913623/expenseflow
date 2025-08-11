package com.expensetracker.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Web controller for serving the frontend application.
 * 
 * @author Expense Tracker Team
 * @version 1.0
 */
@Controller
public class WebController {
    
    /**
     * Serves the main application page
     *
     * @return the index page
     */
    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    /**
     * Serves the main application page for any path (SPA support)
     *
     * @return the index page
     */
    @GetMapping("/{path:[^\\.]*}")
    public String forward() {
        return "forward:/index.html";
    }
}
