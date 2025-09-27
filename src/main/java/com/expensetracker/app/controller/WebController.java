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
     * Serve the login page for root path
     *
     * @return the login page
     */
    @GetMapping("/")
    public String index() {
        return "login";
    }

    /**
     * Serve the dashboard page for authenticated users
     *
     * @return the dashboard page
     */
    @GetMapping("/dashboard")
    public String dashboard() {
        return "index";
    }

    /**
     * Serve login page for any other unmatched paths
     *
     * @return the login page
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
