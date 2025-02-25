package com.sb.lms.utils;

import com.sb.lms.model.*;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Contains utility methods required throughout the LMS application
 * @author Saarah Bedekar
 */
@Slf4j
public final class Utils {

    /**
     * Checks if a String value is null or blank
     * @return the boolean check flag
     */
    public static boolean isStringBlank(String stringInput) {
        return stringInput == null || stringInput.trim().isBlank();
    }

    /**
     * Creates a BookInfo entity object with just the LmsFault property populated
     * @param lmsFault  Has information about the Fault's http message, code, message and path of its occurrence
     * @return the BookInfo entity as an element of a List, with only LMSFault information
     */
    public static List<BookInfo> createFaultyBookInfoInList(LmsFault lmsFault) {
        BookInfo faultybookInfo = new BookInfo();
        faultybookInfo.setFault(lmsFault);
        List<BookInfo> bookInfoList = new ArrayList<BookInfo>();
        bookInfoList.add(faultybookInfo);
        return bookInfoList;
    }

    /**
     * Creates a BookInfo entity object with just the LmsFault property populated
     * @param lmsFault  Has information about the Fault's http message, code, message and path of its occurrence
     * @return the BookInfo entity with only LMSFault information
     */
    public static BookInfo createFaultyBookInfo(LmsFault lmsFault) {
        BookInfo faultyBookInfo = new BookInfo();
        faultyBookInfo.setFault(lmsFault);
        return faultyBookInfo;
    }

    /**
     * Creates a Book entity object with just the LmsFault property populated
     * @param lmsFault  Has information about the Fault's http message, code, message and path of its occurrence
     * @return the Book entity as an element of a List, with only LMSFault information
     */
    public static List<Book> createFaultyBookInList(LmsFault lmsFault) {
        Book faultyBook = new Book();
        faultyBook.setFault(lmsFault);
        List<Book> bookList = new ArrayList<Book>();
        bookList.add(faultyBook);
        return bookList;
    }

    /**
     * Creates a Book entity object with just the LmsFault property populated
     * @param lmsFault  Has information about the Fault's http message, code, message and path of its occurrence
     * @return the Book entity with only LMSFault information
     */
    public static Book createFaultyBook(LmsFault lmsFault) {
        Book faultyBook = new Book();
        faultyBook.setFault(lmsFault);
        return faultyBook;
    }

    /**
     * Creates a ReportInfo entity object with just the LmsFault property populated
     * @param lmsFault  Has information about the Fault's http message, code, message and path of its occurrence
     * @return the ReportInfo entity as an element of a List, with only LMSFault information
     */
    public static List<ReportInfo> createFaultyReportInfoInList(LmsFault lmsFault) {
        ReportInfo faultyReportInfo = new ReportInfo();
        faultyReportInfo.setFault(lmsFault);
        List<ReportInfo> reportInfoList = new ArrayList<ReportInfo>();
        reportInfoList.add(faultyReportInfo);
        return reportInfoList;
    }

    /**
     * Creates a ReportInfo entity object with just the LmsFault property populated
     * @param lmsFault  Has information about the Fault's http message, code, message and path of its occurrence
     * @return the ReportInfo entity with only LMSFault information
     */
    public static ReportInfo createFaultyReportInfo(LmsFault lmsFault) {
        ReportInfo faultyReportInfo = new ReportInfo();
        faultyReportInfo.setFault(lmsFault);
        return faultyReportInfo;
    }

    /**
     * Creates a Report entity object with just the LmsFault property populated
     * @param lmsFault  Has information about the Fault's http message, code, message and path of its occurrence
     * @return the Report entity as an element of a List, with only LMSFault information
     */
    public static List<Report> createFaultyReportInList(LmsFault lmsFault) {
        Report faultyReport = new Report();
        faultyReport.setFault(lmsFault);
        List<Report> reportList = new ArrayList<Report>();
        reportList.add(faultyReport);
        return reportList;
    }

    /**
     * Creates a Report entity object with just the LmsFault property populated
     * @param lmsFault  Has information about the Fault's http message, code, message and path of its occurrence
     * @return the Report entity with only LMSFault information
     */
    public static Report createFaultyReport(LmsFault lmsFault) {
        Report faultyReport = new Report();
        faultyReport.setFault(lmsFault);
        return faultyReport;
    }

    /**
     * Creates a Transaction entity object with just the LmsFault property populated
     * @param lmsFault  Has information about the Fault's http message, code, message and path of its occurrence
     * @return the Transaction entity as an element of a List, with only LMSFault information
     */
    public static List<Transaction> createFaultyTransactionInList(LmsFault lmsFault) {
        Transaction faultyTransaction = new Transaction();
        faultyTransaction.setFault(lmsFault);
        List<Transaction> transactionList = new ArrayList<Transaction>();
        transactionList.add(faultyTransaction);
        return transactionList;
    }

    /**
     * Creates a Transaction entity object with just the LmsFault property populated
     * @param lmsFault  Has information about the Fault's http message, code, message and path of its occurrence
     * @return the Transaction entity with only LMSFault information
     */
    public static Transaction createFaultyTransaction(LmsFault lmsFault) {
        Transaction faultyTransaction = new Transaction();
        faultyTransaction.setFault(lmsFault);
        return faultyTransaction;
    }

    /**
     * Creates a User entity object with just the LmsFault property populated
     * @param lmsFault  Has information about the Fault's http message, code, message and path of its occurrence
     * @return the User entity as an element of a List, with only LMSFault information
     */
    public static List<User> createFaultyUserInList(LmsFault lmsFault) {
        User faultyUser = new User();
        faultyUser.setFault(lmsFault);
        List<User> userList = new ArrayList<User>();
        userList.add(faultyUser);
        return userList;
    }

    /**
     * Creates a User entity object with just the LmsFault property populated
     * @param lmsFault  Has information about the Fault's http message, code, message and path of its occurrence
     * @return the User entity with only LMSFault information
     */
    public static User createFaultyUser(LmsFault lmsFault) {
        User faultyUser = new User();
        faultyUser.setFault(lmsFault);
        return faultyUser;
    }

    /**
     * Converts the current date into SQL Timestamp
     * @return the converted SQL timestamp
     */
    public static Timestamp convertNowToSQLTS() {
        log.info("Started Utils::convertNowToSQLTS");
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);

        log.info("Returning Utils::convertNowToSQLTS");
        return timestamp;
    }

    /**
     * Calculates the future date of exactly 15 days
     * @return the calculated SQL timestamp to mark the default return date of the Book
     */
    public static Timestamp generateReturnSQLTS(Timestamp timestamp) {
        log.info("Started Utils::generateReturnSQLTS. timestamp = {}", timestamp);

        LocalDateTime future;

        //Add 15 days to the timestamp passed as parameter
        future = timestamp.toLocalDateTime().plus(15, ChronoUnit.DAYS);

        log.info("Returning Utils::generateReturnSQLTS. timestamp = {}", timestamp);
        return Timestamp.valueOf(future);
    }

    /**
     * Converts a Timestamp to a dateString (YYYY-MM-DD)
     * @return the converted dateString
     */
    public static String convertTsToString(Timestamp timestamp) {
        log.info("Started Utils::convertStringToTs. timestamp = {}", timestamp);
        // Convert Timestamp to LocalDate
        LocalDate localDate = timestamp.toLocalDateTime().toLocalDate();

        log.info("Returning Utils::convertStringToTs. timestamp = {}", timestamp);
        // Format as "YYYY-MM-DD"
        return localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * Converts a dateString (YYYY-MM-DD) to a Timestamp
     * @return the converted Timestamp
     */
    public static Timestamp convertStringToTs(String dateString) {
        log.info("Started Utils::convertStringToTs. dateString = {}", dateString);
        // Define a DateTimeFormatter for the custom format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Parse the string into a LocalDate
        LocalDate localDate = LocalDate.parse(dateString, formatter);

        log.info("Returning Utils::convertStringToTs. dateString = {}", dateString);
        // Convert LocalDate to Timestamp (at start of the day)
        return Timestamp.valueOf(localDate.atStartOfDay());
    }

    /**
     * Handles the mapping between the user session and the GUI's model
     * @param session the user's Http Session
     * @param model the model which lets the thymleaf GUI get model information from server
     * @param returnPath the url path to its respective controller method
     * @return the returnPath as received
     */
    public static String handleSession(HttpSession session, Map<String, Object> model, String returnPath) {
        log.info("Starting Utils::authenticate. model = {}, returnPath = {}", model.values().toString(), returnPath);
        String  userInfo = (String) session.getAttribute("userInfo");
        String  userId = (String) session.getAttribute("userId");
        String  userType = (String) session.getAttribute("userType");
        //log.info("userInfo = " + userInfo + ", userId = " + userId + ", userType = " + userType);
        if (userInfo == null) {
            //log.info("userInfo is NULL here");
            return "redirect:/";
        }
        model.put("userInfo", userInfo);
        model.put("userId", userId);
        model.put("userType", userType);
        log.info("Returning Utils::authenticate. model = {}", model.values().toString());
        return returnPath;
    }
}
