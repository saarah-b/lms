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
 * Handles all requests to map url paths to their respective User controllers methods
 * @author Saarah Bedekar
 */
public class UserMappingController {
    private static final String baseUrl = "lms/user/";

    /**
     * Handles GET requests to map url path to its respective Base User Menu page
     * @return the next mapped url path
     */
    @GetMapping("/user")
    public String showUserPage(HttpSession session, Map<String, Object> model) {
        // No .html needed, Thymeleaf will find login.html in templates
        return Utils.handleSession(session, model, baseUrl + "user");
    }

    /**
     * Handles GET requests to map url path to its respective Add User controller method
     * @return the next mapped url path
     */
    @GetMapping("/usera")
    public String showUserAPage(HttpSession session, Map<String, Object> model) {
        return Utils.handleSession(session, model, baseUrl + "usera");
    }

    /**
     * Handles GET requests to map url path to its respective Search User controller method
     * @return the next mapped url path
     */
    @GetMapping("/userr")
    public String showUserRPage(HttpSession session, Map<String, Object> model) {
        return Utils.handleSession(session, model, baseUrl + "userr");
    }

    /**
     * Handles GET requests to map url path to its respective Update User controller method
     * @return the next mapped url path
     */
    @GetMapping("/useru")
    public String showUserUPage(HttpSession session, Map<String, Object> model) {
        return Utils.handleSession(session, model, baseUrl + "useru");
    }

    /**
     * Handles GET requests to map url path to its respective Delete User controller method
     * @return the next mapped url path
     */
    @GetMapping("/userd")
    public String showUserDPage(HttpSession session, Map<String, Object> model) {
        return Utils.handleSession(session, model, baseUrl + "userd");
    }

    /**
     * Handles GET requests to map url path to its respective Update Self Only User controller method
     * @return the next mapped url path
     */
    @GetMapping("/userum")
    public String showUserUMPage(HttpSession session, Map<String, Object> model) {
        return Utils.handleSession(session, model, baseUrl + "userum");
    }
}