package com.sb.lms.service;

import com.sb.lms.model.User;
import com.sb.lms.repository.AddressRepository;
import com.sb.lms.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Contains Entity mapping utility for updates of User
 * @author Saarah Bedekar
 */
@Slf4j
class UserUtils {

    @Autowired
    private static AddressRepository addressRepository; // Injects the UserRepository dependency.

    public UserUtils() {};

    /**
     * Handles the mapping of Non Null values in the User to be updated to the DB
     * @param userFromClient the User entity sent by the UI
     * @param userDB the User Object with updated values
     * @return the updated User entity to be saved to Database
     */
    public static User nonNullMapper(User userFromClient, User userDB) {
        log.info("Started UserUtils::nonNullMapper");
        if (!Utils.isStringBlank(userFromClient.getPassword())) {
            userDB.setPassword(userFromClient.getPassword());
        }
        /*
        if (!Utils.isStringBlank(userFromClient.getFirstName())) {
            userDB.setFirstName(userFromClient.getFirstName());
        }
        if (!Utils.isStringBlank(userFromClient.getMiddleName())) {
            userDB.setMiddleName(userFromClient.getMiddleName());
        }
        if (!Utils.isStringBlank(userFromClient.getLastName())) {
            userDB.setLastName(userFromClient.getLastName());
        }*/
        if (!Utils.isStringBlank(userFromClient.getEmail())) {
            userDB.setEmail(userFromClient.getEmail());
        }
        if (!Utils.isStringBlank(userFromClient.getMobileNumber())) {
            userDB.setMobileNumber(userFromClient.getMobileNumber());
        }
        /*
        if (userFromClient.getBirth() != null) {
            log.info("userFromClient.getBirth() = " + userFromClient.getBirth());
            userDB.setBirth(userFromClient.getBirth());
        }
        if (userFromClient.getType() != null ) {
            userDB.setType(userFromClient.getType());
        }*/
        if (userFromClient.getLastLogin() != null) {
            userDB.setLastLogin(userFromClient.getLastLogin());
        }
        if (userFromClient.getAddress() != null) {
            userDB.setAddress(userFromClient.getAddress());
        }
        log.info("Returning UserUtils::nonNullMapper");
        return userDB;
    }
}
