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
 * Handles all requests to map url paths to their respective BookInfo controllers methods
 * @author Saarah Bedekar
 */
public class BookInfoMappingController {

    private static final String baseUrl = "lms/bookinfo/";

    /**
     * Handles GET requests to map url path to its respective Base BookInfo Menu page
     * @return the next mapped url path
     */
    @GetMapping("/bookinfo")
    public String showBookInfoPage(HttpSession session, Map<String, Object> model) {
        return Utils.handleSession(session, model, baseUrl + "bookinfo");
    }

    /**
     * Handles GET requests to map url path to its respective Add BookInfo controller method
     * @return the next mapped url path
     */
    @GetMapping("/bookinfoa")
    public String showBookInfoAPage(HttpSession session, Map<String, Object> model) {
        return Utils.handleSession(session, model, baseUrl + "bookinfoa");
    }

    /**
     * Handles GET requests to map url path to its respective Search BookInfo controller method
     * @return the next mapped url path
     */
    @GetMapping("/bookinfor")
    public String showBookInfoRPage(HttpSession session, Map<String, Object> model) {
        log.info("Started BookInfoMappingController::showBookInfoRPage. model = " + model.values().toString());
        return Utils.handleSession(session, model, baseUrl + "bookinfor");
    }

    /**
     * Handles GET requests to map url path to its respective Update BookInfo controller method
     * @return the next mapped url path
     */
    @GetMapping("/bookinfou")
    public String showBookInfoUPage(HttpSession session, Map<String, Object> model) {
        return Utils.handleSession(session, model, baseUrl + "bookinfou");
    }

    /**
     * Handles GET requests to map url path to its respective Delete BookInfo controller method
     * @return the next mapped url path
     */
    @GetMapping("/bookinfod")
    public String showBookInfoDPage(HttpSession session, Map<String, Object> model) {
        return Utils.handleSession(session, model, baseUrl + "bookinfod");
    }
}