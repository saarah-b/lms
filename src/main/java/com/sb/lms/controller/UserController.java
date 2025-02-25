package com.sb.lms.controller;

import com.sb.lms.service.UserService;
import com.sb.lms.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contains Controller methods of User CRUD
 * @author Saarah Bedekar
 */

@RestController // Marks this class as a REST controller.
@RequestMapping("/lms/v1/users") // Base url mapping
@Slf4j  // logging
public class UserController {

    @Autowired
    private UserService userService;

    /**
    * There is no practical use case for retrieving all users in the system.
    * This query can potentially break the system as the dataset can be very large
    @GetMapping
    public List<User> getAllUsers() {
        log.info("Started UserController::getAllUsers");
        return userService.getAllUsers();
    }*/

    /**
     * Handles GET requests to get/retrieve an existing User.
     * @param userId the ID of the User to be retrieved
     * @return the requested User entity
     */
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Integer userId) {
        log.info("Started UserController::getUserById. userId = " + userId);
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(userId));
    }

    /**
     * Handles GET requests to get/retrieve existing Users.
     * @param name the name pattern of the Users to be retrieved
     * @return a list of all requested Users
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<List<User>> getUserByName(@PathVariable String name) {
        log.info("Started UserController::getUserByName. name = " + name);
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserByName(name));
    }

    /**
     * Handles POST requests to save a new User.
     * @param user the User entity to be saved
     * @return the saved User entity
     */
    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody User user) {
        log.info("Started UserController::addUser. user name = " + user.getFirstName() + " " + user.getLastName());
        //return userService.addUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(user));
    }

    /**
     * Handles PUT requests to update an existing User.
     * @param userId the ID of the User to be updated
     * @param user the User entity with updated information
     * @return the updated User entity
     */
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Integer userId, @RequestBody User user) {
        log.info("Started UserController::updateUser. userId = " + userId);
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(userId, user));
    }

    /**
     * Handles DELETE requests to remove a User by ID.
     * @param userId the ID of the User to be deleted
     * @return a success/failure message
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable Integer userId) {
        log.info("Started UserController::deleteUserById. userId = " + userId);
        return ResponseEntity.status(HttpStatus.OK).body(userService.deleteUserById(userId));
    }
}