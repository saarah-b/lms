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
 * Handles all requests to map url paths to their respective Report controllers methods
 * @return the next mapped url path
 */
public class ReportMappingController {
    private static final String baseUrl = "lms/report/";

    /**
     * Handles GET requests to map url path to its respective Base Report Menu page
     * @return the next mapped url path
     */
    @GetMapping("/report")
    public String showReportPage(HttpSession session, Map<String, Object> model) {
        // No .html needed, Thymeleaf will find login.html in templates
        /*
        String userType = (String) session.getAttribute("userType");
        if (!userType.equalsIgnoreCase("A"))
            return "redirect:/menu";
        */
        return Utils.handleSession(session, model, baseUrl+ "report");
    }

    /**
     * Handles GET requests to map url path to its respective Add Report controller method
     * @return the next mapped url path
     */
    @GetMapping("/reporta")
    public String showReportAPage(HttpSession session, Map<String, Object> model) {
        // No .html needed, Thymeleaf will find login.html in templates
        return Utils.handleSession(session, model, baseUrl+ "reporta");
    }

    /**
     * Handles GET requests to map url path to its respective Search Report controller method
     * @return the next mapped url path
     */
    @GetMapping("/reportr")
    public String showReportRPage(HttpSession session, Map<String, Object> model) {
        return Utils.handleSession(session, model, baseUrl + "reportr");
    }
}