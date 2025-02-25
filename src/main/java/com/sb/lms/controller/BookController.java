package com.sb.lms.controller;

import com.sb.lms.model.Book;
import com.sb.lms.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Contains Controller methods of Book CRUD
 * @author Saarah Bedekar
 */
@RestController // Marks this class as a REST controller.
@RequestMapping("/lms/v1/books") // Base url mapping
@Slf4j // logging
public class BookController {

    @Autowired
    private BookService bookService;

    /*
    * There is no practical use case for retrieving all books in the system.
    * This query can potentially break the system as the dataset can be very large
    @GetMapping
    public List<Book> getAllBooks() {
        log.info("Started BookController::getAllBooks");
        return bookService.getAllBooks();
    }
    */

    /**
     * Handles GET requests to get/retrieve all Books of a particular BookInfo
     * @param bookInfoId the ID of the BookInfo associated to Books to be retrieved
     * @return a list of all associated Books
     */
    @GetMapping("/all/{bookInfoId}")
    public ResponseEntity<List<Book>> getAllBooksOfBookInfo(@PathVariable Integer bookInfoId) {
        log.info("Started BookController::getAllBooksOfBookInfo. bookInfoId = " + bookInfoId);
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getAllBooksOfBookInfo(bookInfoId));
    }

    /**
     * Handles GET requests to get/retrieve all available Books (i.e not currently borrowed by anyone).
     * @param bookInfoId the ID of the BookInfo associated to available Books to be retrieved
     * @return a list of all associated available Books
     */
    @GetMapping("/available/{bookInfoId}")
    public ResponseEntity<List<Book>> getAvailableBooksByBookInfo(@PathVariable Integer bookInfoId) {
        log.info("Started BookController::getAvailableBooksByBookInfo. bookInfoId = " + bookInfoId);
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getAvailableBooksByBookInfo(bookInfoId));
    }

    /**
     * Handles GET requests to get/retrieve an existing Book.
     * @param bookId the ID of the Book to be retrieved
     * @return the requested Book entity
     */
    @GetMapping("/{bookId}")
    public ResponseEntity<Book> getBookById(@PathVariable Integer bookId) {
        log.info("Started BookController::getBookById. bookId = " + bookId);
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getBookById(bookId));
    }

    /**
     * Handles POST requests to save a new Book.
     * @param book the Book entity to be saved
     * @return the saved Book entity
     */
    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        log.info("Started BookController::addBook. bookId = " + book.getBookId());
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.addBook(book));
    }

    /**
     * Handles PUT requests to update an existing Book.
     * @param bookId the ID of the book to be updated
     * @param book the Book entity with updated information
     * @return the updated Book entity
     */
    @PutMapping("/{bookId}")
    public ResponseEntity<Book> updateBook(@PathVariable Integer bookId, @RequestBody Book book) {
        log.info("Started BookController::updateBook. bookId = " + bookId);
        return ResponseEntity.status(HttpStatus.OK).body(bookService.updateBook(bookId, book));
    }

    /**
     * Handles DELETE requests to remove a Book by ID.
     * @param bookId the ID of the Book to be deleted
     * @return a success/failure message
     */
    @DeleteMapping("/{bookId}")
    public ResponseEntity<String> deleteBookById(@PathVariable Integer bookId) {
        log.info("Started BookController::deleteBookById. bookId = " + bookId);
        return ResponseEntity.status(HttpStatus.OK).body(bookService.deleteBookById(bookId));
    }
}