package com.sb.lms.controller;

import com.sb.lms.service.UserService;
import com.sb.lms.utils.Utils;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * Handles all login requests to map url paths to their respective login methods
 * @author Saarah Bedekar
 */
@Controller // Marks this class as a RESTful controller.
@Slf4j  // logging
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * Handles GET requests to map url path to the login page to show login options
     * @return the next mapped url path
     */
    @GetMapping("/")
    public String showLoginPage() {
        return "login";  // No .html needed, Thymeleaf will find login.html in templates
    }

    /**
     * Handles GET requests to map url path to the Login page taking user's login credentials
     * @return the next mapped url path
     */
    @PostMapping("/login")
    public String login(@RequestParam Integer userid,
                              @RequestParam String password,
                              HttpSession session,
                              Map<String, Object> model) {
        log.info("Starting LoginController::login. userid = " + userid);
        String userState = userService.authenticate(userid, password);

        String result = userState.split("-")[0];

        if ("success".equals(result)) {
            String userInfo = userState.split("-")[1];
            String userId = userState.split("-")[2];
            String userType = userState.split("-")[3];
            session.setAttribute("userInfo", userInfo + "-" + userId + "-" + userType);
            session.setAttribute("userId", userId);
            session.setAttribute("userType", userType);

            log.info("Returning LoginController::login. success - userid = " + userid);
            return "redirect:/menu";
        } else if ("invalid_password".equals(result)) {
            model.put("userId",userid);
            model.put("error", "Invalid password.");
            log.info("Returning LoginController::login. invalid password - userid = " + userid);
            return "login";  // Return to login.html on error
        } else {
            model.put("error", "User not found.");
            model.put("userId",userid);
            log.info("Returning LoginController::login. User not found - userid = " + userid);
            return "login";  // Return to login.html on error
        }
    }

    /**
     * Handles GET requests to map url path to the Menu page
     * @return the next mapped url path
     */
    @GetMapping("/menu")
    public String showMenu(HttpSession session, Map<String, Object> model) {
        return Utils.handleSession(session, model, "menu");
    }

    /**
     * Handles GET requests to map url path to the Login page having logged out the existing user
     * @return the next mapped url path
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "login";  // Return to login.html on error
    }
}