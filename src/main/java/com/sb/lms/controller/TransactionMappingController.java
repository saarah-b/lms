package com.sb.lms.controller;

import com.sb.lms.utils.Utils;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Map;

@Controller // Marks this class as a web controller.
@Slf4j  // logging

/**
 * Handles all requests to map url paths to their respective Transaction controllers methods
 * @return the next mapped url path
 */
public class TransactionMappingController {
    private static final String baseUrl = "lms/transaction/";

    /**
     * Handles GET requests to map url path to its respective Base Transaction Menu page
     * @return the next mapped url path
     */
    @GetMapping("/transaction")
    public String showTransactionPage(HttpSession session, Map<String, Object> model) {
        // No .html needed, Thymeleaf will find login.html in templates
        return Utils.handleSession(session, model, baseUrl + "transaction");
    }

    /**
     * Handles GET requests to map url path to its respective Add Transaction controller method
     * @return the next mapped url path
     */
    @GetMapping("/transactiona")
    public String showTransactionAPage(HttpSession session, Map<String, Object> model) {
        // No .html needed, Thymeleaf will find login.html in templates
        return Utils.handleSession(session, model, baseUrl + "transactiona");
    }

    /**
     * Handles GET requests to map url path to its respective Search Transaction controller method
     * @return the next mapped url path
     */
    @GetMapping("/transactionr")
    public String showTransactionRPage(HttpSession session, Map<String, Object> model) {
        return Utils.handleSession(session, model, baseUrl + "transactionr");
    }

    /**
     * Handles GET requests to map url path to its respective Update Transaction controller method
     * @return the next mapped url path
     */
    @GetMapping("/transactionu")
    public String showTransactionUPage(HttpSession session, Map<String, Object> model) {
        return Utils.handleSession(session, model, baseUrl + "transactionu");
    }

    /**
     * Handles GET requests to map url path to its respective Search Self only Transaction controller method
     * @return the next mapped url path
     */
    @GetMapping("/transactionrm")
    public String showTransactionRMPage(HttpSession session, Map<String, Object> model) {
        return Utils.handleSession(session, model, baseUrl + "transactionrm");
    }
}