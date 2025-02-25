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
 * Handles all requests to map url paths to their respective Book controllers methods
 * @author Saarah Bedekar
 */
public class BookMappingController {
    private static final String baseUrl = "lms/book/";

    @GetMapping("/book")
    public String showBookPage(HttpSession session, Map<String, Object> model) {
        return Utils.handleSession(session, model, baseUrl + "book");
    }

    /**
     * Handles GET requests to map url path to its respective Add Book controller method
     * @return the next mapped url path
     */
    @GetMapping("/booka")
    public String showBookAPage(HttpSession session, Map<String, Object> model) {
        return Utils.handleSession(session, model, baseUrl + "booka");
    }

    /**
     * Handles GET requests to map url path to its respective Search Book controller method
     * @return the next mapped url path
     */
    @GetMapping("/bookr")
    public String showBookRPage(HttpSession session, Map<String, Object> model) {
        return Utils.handleSession(session, model, baseUrl + "bookr");
    }

    /**
     * Handles GET requests to map url path to its respective Update Book controller method
     * @return the next mapped url path
     */
    @GetMapping("/booku")
    public String showBookUPage(HttpSession session, Map<String, Object> model) {
        return Utils.handleSession(session, model, baseUrl + "booku");
    }

    /**
     * Handles GET requests to map url path to its respective Update Book controller method
     * @return the next mapped url path
     */
    @GetMapping("/bookd")
    public String showBookDPage(HttpSession session, Map<String, Object> model) {
        return Utils.handleSession(session, model, baseUrl + "bookd");
    }
}