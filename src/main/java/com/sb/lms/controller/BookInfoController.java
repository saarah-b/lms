package com.sb.lms.controller;

import com.sb.lms.model.BookInfo;
import com.sb.lms.service.BookInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contains Controller methods of BookInfo CRUD
 * @author Saarah Bedekar
 */
@RestController // Marks this class as a REST controller.
@RequestMapping("/lms/v1/bookinfos") // Base url mapping
@Slf4j // logging
public class BookInfoController {

    @Autowired
    private BookInfoService bookInfoService;

    /**
     * Handles GET requests of the full catalog of all existing BookInfos.
     * @return a list of all BookInfos
     */
    @GetMapping
    public ResponseEntity<List<BookInfo>> getAllBookInfos() {
        log.info("Started BookInfoController::getAllBookInfos");
        return ResponseEntity.status(HttpStatus.OK).body(bookInfoService.getAllBookInfos());
    }

    /**
     * Handles GET requests to get/retrieve an existing BookInfo.
     * @param bookInfoId the ID of the BookInfo to be retrieved
     * @return the requested BookInfo entity
     */
    @GetMapping("/{bookInfoId}")
    public ResponseEntity<BookInfo> getBookInfoById(@PathVariable Integer bookInfoId) {
        log.info("Started BookInfoController::getBookInfoById. bookInfoId = " + bookInfoId);
        return ResponseEntity.status(HttpStatus.OK).body(bookInfoService.getBookInfoById(bookInfoId));
    }

    /**
     * Handles GET requests to get/retrieve existing BookInfos.
     * @param title the title pattern of the BookInfos to be retrieved
     * @return a list of all requested BookInfos
     */
    @GetMapping("/title/{title}")
    public ResponseEntity<List<BookInfo>> getBookInfoByTitle(@PathVariable String title) {
        log.info("Started BookInfoController::getBookInfoByTitle. title = " + title);
        return ResponseEntity.status(HttpStatus.OK).body(bookInfoService.getBookInfoByTitle(title));
    }

    /**
     * Handles POST requests to save a new BookInfo.
     * @param bookInfo the BookInfo entity to be saved
     * @return the saved BookInfo entity
     */
    @PostMapping
    public ResponseEntity<BookInfo> addBookInfo(@RequestBody BookInfo bookInfo) {
        log.info("Started BookInfoController::addBookInfo. bookInfoId = " + bookInfo.getBookInfoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(bookInfoService.addBookInfo(bookInfo));
    }

    /**
     * Handles PUT requests to update an existing BookInfo.
     * @param bookInfoId the ID of the BookInfo to be updated
     * @param bookInfo the BookInfo entity with updated information
     * @return the updated BookInfo entity
     */
    @PutMapping("/{bookInfoId}")
    public ResponseEntity<BookInfo> updateBookInfo(@PathVariable Integer bookInfoId, @RequestBody BookInfo bookInfo) {
        log.info("Started BookInfoController::updateBookInfo. bookInfoId = " + bookInfoId);
        return ResponseEntity.status(HttpStatus.OK).body(bookInfoService.updateBookInfo(bookInfoId, bookInfo));
    }

    /**
     * Handles DELETE requests to remove a BookInfo by ID.
     * @param bookInfoId the ID of the BookInfo to be deleted
     * @return a success/failure message
     */
    @DeleteMapping("/{bookInfoId}")
    public ResponseEntity<String> deleteBookInfo(@PathVariable Integer bookInfoId) {
        log.info("Started BookInfoController::deleteBookInfo. bookInfoId = " + bookInfoId);
        return ResponseEntity.status(HttpStatus.OK).body(bookInfoService.deleteBookInfoById(bookInfoId));
    }
}