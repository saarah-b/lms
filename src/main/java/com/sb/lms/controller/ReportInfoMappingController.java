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
 * Handles all requests to map url paths to their respective ReportInfo controllers methods
 * @return the next mapped url path
 */
public class ReportInfoMappingController {
    private static final String baseUrl = "lms/reportinfo/";

    /**
     * Handles GET requests to map url path to its respective Base ReportInfo Menu page
     * @return the next mapped url path
     */
    @GetMapping("/reportinfo")
    public String showReportInfoPage(HttpSession session, Map<String, Object> model) {
        return Utils.handleSession(session, model, baseUrl + "reportinfo");
    }

    /**
     * Handles GET requests to map url path to its respective Add ReportInfo controller method
     * @return the next mapped url path
     */
    @GetMapping("/reportinfoa")
    public String showReportInfoAPage(HttpSession session, Map<String, Object> model) {
        // No .html needed, Thymeleaf will find login.html in templates
        return Utils.handleSession(session, model,  baseUrl + "reportinfoa");
    }

    /**
     * Handles GET requests to map url path to its respective Search ReportInfo controller method
     * @return the next mapped url path
     */
    @GetMapping("/reportinfor")
    public String showReportInfoRPage(HttpSession session, Map<String, Object> model) {
        return Utils.handleSession(session, model, baseUrl + "reportinfor");
    }

    /**
     * Handles GET requests to map url path to its respective Update ReportInfo controller method
     * @return the next mapped url path
     */
    @GetMapping("/reportinfou")
    public String showReportInfoUPage(HttpSession session, Map<String, Object> model) {
        return Utils.handleSession(session, model, baseUrl + "reportinfou");
    }

    /**
     * Handles GET requests to map url path to its respective Delete ReportInfo controller method
     * @return the next mapped url path
     */
    @GetMapping("/reportinfod")
    public String showReportInfoDPage(HttpSession session, Map<String, Object> model) {
        return Utils.handleSession(session, model, baseUrl + "reportinfod");
    }
}