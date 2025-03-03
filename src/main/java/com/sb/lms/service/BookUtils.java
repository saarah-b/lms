package com.sb.lms.service;

import com.sb.lms.model.Book;
import com.sb.lms.utils.Utils;
import lombok.extern.slf4j.Slf4j;

/**
 * Contains Entity mapping utility for updates of Book
 * @author Saarah Bedekar
 */
@Slf4j
class BookUtils {

    public BookUtils() {};

    /**
     * Handles the mapping of Non Null values in the Book to be updated to the DB
     * @param bookFromClient the Book entity sent by the UI
     * @param bookDB the Book Object with updated values
     * @return the updated Book entity to be saved to Database
     */
    public static Book nonNullMapper(Book bookFromClient, Book bookDB) {
        log.info("Started BookUtils::nonNullMapper");
        if (!Utils.isStringBlank(bookFromClient.getShelfReference())) {
            bookDB.setShelfReference(bookFromClient.getShelfReference());
        }
        if (!Utils.isStringBlank(bookFromClient.getLocation())) {
            bookDB.setLocation(bookFromClient.getLocation());
        }
        if (!Utils.isStringBlank(bookFromClient.getEdition())) {
            bookDB.setEdition(bookFromClient.getEdition());
        }
        log.info("Returning BookUtils::nonNullMapper");
        return bookDB;
    }
}
