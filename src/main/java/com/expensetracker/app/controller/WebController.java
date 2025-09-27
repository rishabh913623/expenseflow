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
     * Redirect root path to login page
     *
     * @return redirect to login page
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/login.html";
    }

    /**
     * Redirect dashboard to main app page for authenticated users
     *
     * @return redirect to main app page
     */
    @GetMapping("/dashboard")
    public String dashboard() {
        return "redirect:/index.html";
    }

    /**
     * Redirect login to login page
     *
     * @return redirect to login page
     */
    @GetMapping("/login")
    public String login() {
        return "redirect:/login.html";
    }
}
