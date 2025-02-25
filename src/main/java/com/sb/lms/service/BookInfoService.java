package com.sb.lms.service;

import com.sb.lms.model.BookInfo;
import com.sb.lms.model.LmsFault;
import com.sb.lms.repository.BookInfoRepository;
import com.sb.lms.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Contains Service methods of BookInfo CRUD
 * @author Saarah Bedekar
 */
@Service
@Slf4j
public class BookInfoService {

    @Autowired
    private BookInfoRepository bookInfoRepository; // Injects the BookInfoRepository dependency.

    /**
     * Handles requests to get/retrieve all BookInfos (Full Library Book Catalogue).
     * @return the requested list of BookInfos
     */
    public List<BookInfo> getAllBookInfos() {
        log.info("Started BookInfoService::getAllBookInfos");

        // select * from bookinfo
        return bookInfoRepository.findAll();
    }

    /**
     * Handles requests to get/retrieve an existing BookInfo.
     * @param bookInfoId the Id of BookInfo to be retrieved
     * @return the requested BookInfo entity
     */
    public BookInfo getBookInfoById(Integer bookInfoId) {
        log.info("Started BookInfoService::getBookInfoById. bookInfoId = " + bookInfoId);

        // Gets the bookinfo entity by its ID.
        // select * from bookinfo where bookinfo_id=?
        BookInfo bookInfoDB = bookInfoRepository.findById(bookInfoId).orElse(null);

        // Create Fault Object to give details on UI
        if (bookInfoDB == null) {
            LmsFault lmsFault = new LmsFault("Resource Not Found", "404",
                    "BookInfo Not Found","/lms/v1/bookinfos/" + bookInfoId);
            return Utils.createFaultyBookInfo(lmsFault);
        }
        log.info("Returning BookInfoService::getBookInfoById");
        return bookInfoDB;
    }

    /**
     * Handles requests to get/retrieve existing BookInfos by their name pattern.
     * @param title the title pattern of the BookInfos to be retrieved
     * @return a list of all requested BookInfos
     */
    public List<BookInfo> getBookInfoByTitle(String title) {
        log.info("Started BookInfoService::getBookInfoByTitle. title = " + title);

        // Gets the BookInfo entity list by its title pattern.
        // SELECT * FROM BOOKINFO WHERE title LIKE %?%
        List<BookInfo> bookInfoDBList = bookInfoRepository.findBookInfoByTitle(title);

        // Create Fault Object to give details on UI
        if (bookInfoDBList == null || bookInfoDBList.isEmpty()) {
            LmsFault lmsFault = new LmsFault("Resource Not Found", "404",
                    "BookInfo Not Found","/lms/v1/bookinfos/title/" + title);
            return Utils.createFaultyBookInfoInList(lmsFault);
        }
        log.info("Returning BookInfoService::getBookInfoByTitle");
        return bookInfoDBList;
    }

    /**
     * Handles requests to save a new BookInfo.
     * @param bookInfo the BookInfo entity to be saved
     * @return the saved BookInfo entity
     */
    public BookInfo addBookInfo(BookInfo bookInfo) {
        log.info("Started BookInfoService::addBookInfo. BookInfo Title = " + bookInfo.getTitle());

        // insert into bookinfo (author,category,genre,isbn,price,publisher,
        //      title,total_quantity) values (?,?,?,?,?,?,?,?)
        return bookInfoRepository.save(bookInfo);
    }

    /**
     * Handles requests to save an existing BookInfo.
     * @param bookInfoId the ID of the BookInfo to be updated
     * @param bookInfoFromClient the BookInfo Object with updated values
     * @return the updated BookInfo entity
     */
    public BookInfo updateBookInfo(Integer bookInfoId, BookInfo bookInfoFromClient) {
        log.info("Started BookInfoService::updateBookInfo. bookInfoId = " + bookInfoId);

        // Finds the existing User by ID.
        BookInfo bookInfoDB = this.getBookInfoById(bookInfoId);

        // Create Fault Object to give details on UI
        if (bookInfoDB == null) {
            LmsFault lmsFault = new LmsFault("Resource Not Found", "404",
                    "BookInfo Not Found","/lms/v1/bookinfos/" + bookInfoId);
            return Utils.createFaultyBookInfo(lmsFault);
        }

        // Map all non null values
        BookInfo bookInfoUpdate = BookInfoUtils.nonNullMapper(bookInfoFromClient, bookInfoDB);

        // Saves and returns the updated entity.
        log.info("Returning BookInfoService::updateBookInfo");

        // update bookinfo set author=?,category=?,genre=?,isbn=?,price=?,publisher=?,title=? where bookinfo_id=?
        return bookInfoRepository.save(bookInfoUpdate);
    }

    /**
     * Handles requests to delete a BookInfo by ID.
     * @param bookInfoId the ID of the BookInfo to be deleted
     * @return a success/failure message of deletion
     */
    public String deleteBookInfoById(Integer bookInfoId) {
        log.info("Started BookInfoService::deleteBookInfoById. bookInfoId = " + bookInfoId);

        // Finds the existing BookInfo by ID.
        BookInfo bookInfo = this.getBookInfoById(bookInfoId);

        // Check if the BookInfo has any associated books
        if (bookInfo.getTotalQuantity() > 0 ) {
            log.info("error:BookInfo " + bookInfoId + " cannot be deleted. It has " +
                    bookInfo.getTotalQuantity() + " associated book(s).");
            log.info("Returning BookInfoService::deleteBookInfoById");
            return "error:BookInfo " + bookInfoId + " cannot be deleted. It has " +
                    bookInfo.getTotalQuantity() + " associated book(s).";
        }

        // Deletes the BookInfo entity by its ID.
        // delete from bookinfo where bookinfo_id=?
        bookInfoRepository.deleteById(bookInfoId);
        log.info("Returning BookInfoService::deleteBookInfoById");
        return "success:BookInfo " + bookInfoId + " successfully deleted";
    }
}
