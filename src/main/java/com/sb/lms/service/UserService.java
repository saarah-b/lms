package com.sb.lms.service;

import com.sb.lms.model.*;
import com.sb.lms.repository.TransactionRepository;
import com.sb.lms.repository.UserRepository;
import com.sb.lms.repository.AddressRepository;
import com.sb.lms.security.BCryptPasswordHasher;
import com.sb.lms.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Contains Service methods of User CRUD
 * @author Saarah Bedekar
 */
@Service
@Slf4j
public class UserService {

    private static final String USER_ADDRESS_NOT_FOUND = "Invalid Address reference provided to User";
    private static final String DEFAULT_PASSWORD = "Qwerty";

    @Autowired
    private UserRepository userRepository; // Injects the UserRepository dependency.

    @Autowired
    private AddressRepository addressRepository; // Injects the AddressRepository dependency.

    @Autowired
    TransactionRepository transactionRepository; // Injects the TransactionRepository dependency.

    /**
     * Handles the one time authentication of the user to the LMS UI application
     * @param userId the userId of the User to be autheticated
     * @param rawPassword the password of the User to be autheticated
     * @return the state of successful/failure authentication of the User
     */
    public String authenticate(Integer userId, String rawPassword) {
        log.info("Started UserService::authenticate. userId = " + userId);
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User userDB = userOptional.get();

            // Build the display profile of the User
            String userInfo = userDB.getFirstName().charAt(0) + "" +
                    userDB.getLastName().charAt(0) + "-" + userDB.getUserId() + "-" + userDB.getType();

            // Hash the raw password
            String hashedPassword = BCryptPasswordHasher.hashPassword(rawPassword);
            //log.info("BCrypt Hashed Password: " + hashedPassword);

            // Verify password match
            boolean isMatch = BCryptPasswordHasher.verifyPassword(userDB.getPassword(), hashedPassword);
            //log.info("Password match: " + isMatch);

            // return the state of authetication whether success or failure
            if (isMatch) {
                log.info("Returning UserService::authenticate. success - userId = " + userId);
                return "success-" + userInfo;
            }
            log.info("Returning UserService::authenticate. invalidPwd - userId = " + userId);
            return "invalid_password-";
        }
        log.info("Returning UserService::authenticate. UserNotFound - userId = " + userId);
        return "user_not_found-";
    }

    /*
    Will not be practically used as there is no use to know all Users in the system

    public List<User> getAllUsers() { // SELECT * FROM USER
        log.info("Started UserService::getAllUsers");
        return userRepository.findAll();
    }
    */

    /**
     * Handles requests to get/retrieve an existing User.
     * @param userId the ID of the User to be retrieved
     * @return the requested User entity
     */
    public User getUserById(Integer userId) { // SELECT * FROM USER WHERE USER_ID=?;
        log.info("Started UserService::getUserById. userId = " + userId);

        // Gets the user entity by its ID.
        // select * from user u left join address a on a.address_id=u.address_id where user_id=?
        User userDB = userRepository.findById(userId).orElse(null);

        // Create Fault Object to give details on UI
        if (userDB == null) {
            LmsFault lmsFault = new LmsFault("Resource Not Found", "404", "User Not Found","/lms/v1/users/" + userId);
            return Utils.createFaultyUser(lmsFault);
        }
        log.info("Returning UserService::getUserById");
        return userDB;
    }

    /**
     * Handles requests to get/retrieve existing Users by their name pattern.
     * @param name the name pattern of the Users to be retrieved
     * @return a list of all requested Users
     */
    public List<User> getUserByName(String name) {
        log.info("Started UserService::getUserByName. name = " + name);

        // Gets the user entity by its name pattern.
        // select * from user where first_name like %firstName% or first_name like %lastName%
        List<User> userDBList = userRepository.findUserByName(name);

        // Create Fault Object to give details on UI
        if (userDBList == null || userDBList.isEmpty()) {
            LmsFault lmsFault = new LmsFault("Resource Not Found", "404", "User Not Found","/lms/v1/users/name/" + name);
            return Utils.createFaultyUserInList(lmsFault);
        }
        log.info("Returning UserService::getUserByName");
        return userDBList;
    }

    /**
     * Handles requests to save a new User.
     * @param user the User entity to be saved
     * @return the saved User entity
     */
    public User addUser(User user) {
        log.info("Started UserService::addUser. user name = " + user.getFirstName() + " " + user.getLastName());

        // Gets the Address entity by its associated ID.
        // select * from address where address_id=?
        Address addressDB = addressRepository.findById(user.getAddress().getAddressId()).orElse(null);

        // Create Fault Object to give details on UI
        if (addressDB == null) {
            LmsFault lmsFault = new LmsFault("Resource Not Found", "404", USER_ADDRESS_NOT_FOUND,"/lms/v1/users");
            return Utils.createFaultyUser(lmsFault);
        }
        //log.info("Address (door#) = " + addressDB.getDoorNumber());
        user.setAddress(addressDB);

        // System assigns this default password
        user.setPassword(DEFAULT_PASSWORD);

        log.info("Returning UserService::addUser userId = " + user.getUserId());
        //insert into user (address_id,birth,email,first_name,last_login,last_name,
        //     *             middle_name,mobile_number,password,type) values (?,?,?,?,?,?,?,?,?,?)
        return userRepository.save(user);
    }

    /**
     * Handles requests to save an existing User.
     * @param userId the ID of the User to be updated
     * @param userFromClient the User Object with updated values
     * @return the updated User entity
     */
    public User updateUser(Integer userId, User userFromClient) {
        log.info("Started UserService::updateUser. userId = " + userId);

        // Finds the existing User by ID.
        User userDB = this.getUserById(userId);

        // Map all non null values
        User userUpdated = UserUtils.nonNullMapper(userFromClient, userDB);

        // Saves and returns the updated entity.
        log.info("Returning UserService::updateUser userId = " + userId);

        // update user set email=?,mobile_number=?,password=? where user_id=?
        return userRepository.save(userUpdated);
    }

    /**
     * Handles requests to delete a User by ID.
     * @param userId the ID of the User to be deleted
     * @return a success/failure message of deletion
     */
    public String deleteUserById(Integer userId) {
        log.info("Started UserService::deleteUserById. userId = " + userId);

        // Finds the existing User by ID.
        User userDB = this.getUserById(userId);

        // Check if the User has any book(s) issued
        // SELECT COUNT(*) FROM TRANSACTION WHERE user_id =? and returned=false
        int userBorrows = transactionRepository.findCountOfOpenTransactionsByUser(userId);

        // User cannot be deleted if they have borrowed books and not yet returned
        boolean userHasBorrowedBooks = userBorrows > 0;
        if (userHasBorrowedBooks) {
            log.info("User " + userId + " cannot be deleted. User has " + userBorrows + " book(s) issued");
            log.info("Returning UserService::deleteUserById. userId = " + userId);
            return "error:User " + userId + " cannot be deleted. User has " + userBorrows + " book(s) issued";
        }

        // Check if the User has any past transactions
        // SELECT COUNT(*) FROM TRANSACTION WHERE user_id =?
        int userHistoryEntries = transactionRepository.findCountOfAllTransactionsByUser(userId);

        // User cannot be deleted if they have a record of any past transactions because the transaction will
        // first have to be deleted from the system to protect referential integrity of the Database
        boolean userHasHistoricalTransactions = userHistoryEntries > 0;
        if (userHasHistoricalTransactions) {
            log.info("User " + userId + " cannot be deleted. User has " +
                    userHistoryEntries + " historical transaction(s)");
            String strError = "error:User " + userId + " cannot be deleted. " +
                    "User has " + userHistoryEntries + " historical transaction(s)";
            strError += "<br>Details: SQL Error: 1451, SQLState: 23000 Cannot delete or " +
                    "update a parent row: a foreign key constraint fails";
            strError += "<br>Please escalate to technical support";

            log.info("Returning UserService::deleteUserById. userId = " + userId);
            return strError;
        }

        // Deletes the user entity by its ID.
        // delete from user where user_id=?
        userRepository.deleteById(userId);
        log.info("Returning UserService::deleteUserById. userId = " + userId);
        return "success:User " + userId + " successfully deleted";
    }
}