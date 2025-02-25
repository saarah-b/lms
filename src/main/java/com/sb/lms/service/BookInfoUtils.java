package com.sb.lms.service;

import com.sb.lms.model.BookInfo;
import com.sb.lms.utils.Utils;
import lombok.extern.slf4j.Slf4j;

/**
 * Contains Entity mapping utility for updates of BookInfo
 * @author Saarah Bedekar
 */
@Slf4j
class BookInfoUtils {

    public BookInfoUtils() {};

    /**
     * Handles the mapping of Non Null values in the BookInfo to be updated to the DB
     * @param bookInfoFromClient the BookInfo entity sent by the UI
     * @param bookInfoDB the BookInfo Object with updated values
     * @return the updated BookInfo entity to be saved to Database
     */
    public static BookInfo nonNullMapper(BookInfo bookInfoFromClient, BookInfo bookInfoDB) {
        log.info("Started BookInfoUtils::nonNullMapper");
        if (!Utils.isStringBlank(bookInfoFromClient.getTitle())) {
            bookInfoDB.setTitle(bookInfoFromClient.getTitle());
        }
        if (!Utils.isStringBlank(bookInfoFromClient.getAuthor())) {
            bookInfoDB.setAuthor(bookInfoFromClient.getAuthor());
        }
        if (!Utils.isStringBlank(bookInfoFromClient.getGenre())) {
            bookInfoDB.setGenre(bookInfoFromClient.getGenre());
        }
        if (!Utils.isStringBlank(bookInfoFromClient.getCategory())) {
            bookInfoDB.setCategory(bookInfoFromClient.getCategory());
        }
        if (!Utils.isStringBlank(bookInfoFromClient.getIsbn())) {
            bookInfoDB.setIsbn(bookInfoFromClient.getIsbn());
        }
        if (!Utils.isStringBlank(bookInfoFromClient.getPublisher())) {
            bookInfoDB.setPublisher(bookInfoFromClient.getPublisher());
        }
        if (bookInfoFromClient.getPrice() != null) {
            bookInfoDB.setPrice(bookInfoFromClient.getPrice());
        }
        if (bookInfoFromClient.getTotalQuantity() != null) {
            bookInfoDB.setTotalQuantity(bookInfoFromClient.getTotalQuantity());
        }
        log.info("Returning BookInfoUtils::nonNullMapper");
        return bookInfoDB;
    }
}
