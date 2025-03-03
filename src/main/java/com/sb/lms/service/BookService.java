package com.sb.lms.service;

import com.sb.lms.model.*;
import com.sb.lms.repository.BookInfoRepository;
import com.sb.lms.repository.BookRepository;
import com.sb.lms.repository.TransactionRepository;
import com.sb.lms.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Contains Service methods of Book CRUD
 * @author Saarah Bedekar
 */
@Service
@Slf4j
public class BookService {

    private static final String BOOK_INFO_NOT_FOUND = "Associated BookInfo Not Found";
    private static final String BOOK_NOT_FOUND = "Associated Book(s) Not Found";
    private static final String BOOKINFO_BASE_URL = "http://localhost:8080/lms/v1/bookinfos/";

    @Autowired
    private final BookRepository bookRepository;

    @Autowired
    private final BookInfoRepository bookInfoRepository;

    @Autowired
    TransactionRepository transactionRepository; // Injects the TransactionRepository dependency.

    private final RestTemplate restTemplate;

    // Constructor to initialise this Object with restTemplate
    public BookService(RestTemplate restTemplate, BookRepository bookRepository,
                       BookInfoRepository bookInfoRepository, TransactionRepository transactionRepository) {
        this.restTemplate = restTemplate;
        this.bookRepository = bookRepository;
        this.bookInfoRepository = bookInfoRepository;
        this.transactionRepository = transactionRepository;
    }

    /*
    Will not be practically used as there is no use to know all Book instances in the system
    Users may want to see all BookInfos

    public List<Book> getAllBooks() {
        log.info("Started BookService::getAllBooks");
        return bookRepository.findAll();
    }/*

    /**
     * Handles requests to get/retrieve all existing Books of a BookInfo (i.e Title).
     * @param bookInfoId the Id of bookInfoId for which all books are to be retrieved
     * @return the requested list of Book entities
     */
    public List<Book> getAllBooksOfBookInfo(Integer bookInfoId) {
        log.info("Started BookService::getAllBooksOfBookInfo. bookInfoId = " + bookInfoId);

        // Gets the bookinfo entity by its ID.
        // select * from bookinfo where bookinfo_id=?
        BookInfo bookInfoDB = bookInfoRepository.findById(bookInfoId).orElse(null);

        // Create Fault Object to give details on UI
        if (bookInfoDB == null) {
            LmsFault lmsFault = new LmsFault("Resource Not Found", "404",
                    BOOK_INFO_NOT_FOUND,"/lms/v1/bookinfos/" + bookInfoId);
            return Utils.createFaultyBookInList(lmsFault);
        }

        // Gets the Book entity by its BookInfo ID.
        // SELECT * FROM BOOK WHERE bookinfo_id =?
        List<Book> bookDBList = bookRepository.findAllBooksByBookInfo(bookInfoId);

        // Create Fault Object to give details on UI
        if (bookDBList == null || bookDBList.isEmpty()) {
            LmsFault lmsFault = new LmsFault("Resource Not Found", "404",
                    BOOK_NOT_FOUND,"/lms/v1/books/all/" + bookInfoId);
            return Utils.createFaultyBookInList(lmsFault);
        }

        log.info("Returning BookService::getAllBooksOfBookInfo");
        return bookDBList;
    }

    /**
     * Handles requests to get/retrieve all existing available Books (i.e not borrowed) of a BookInfo (i.e Title).
     * @param bookInfoId the Id of bookInfoId for which all books are to be retrieved
     * @return the requested list of Book entities
     */
    public List<Book> getAvailableBooksByBookInfo(Integer bookInfoId) {
        log.info("Started BookService::getAvailableBooksByBookInfo. bookInfoId = " + bookInfoId);

        // SELECT * FROM BOOK WHERE bookinfo_id =? and available=true
        return bookRepository.findAvailableBooksByBookInfo(bookInfoId);
    }

    /**
     * Handles requests to get/retrieve an existing Book.
     * @param bookId the Id of Book to be retrieved
     * @return the requested Book entity
     */
    public Book getBookById(Integer bookId) {
        log.info("Started BookService::getBookById. bookId = " + bookId);

        // Gets the bookinfo entity by its ID.
        // select * from book b left join bookinfo bi on bi.bookinfo_id=b.bookinfo_id where b.book_id=?
        Book bookDB = bookRepository.findById(bookId).orElse(null);

        // Create Fault Object to give details on UI
        if (bookDB == null) {
            LmsFault lmsFault = new LmsFault("Resource Not Found", "404",
                    "Book Not Found","/lms/v1/books/" + bookId);
            return Utils.createFaultyBook(lmsFault);
        }
        log.info("Returning BookService::getBookById");
        return bookDB;
    }

    /**
     * Handles requests to save a new Book.
     * @param book the Book entity to be saved
     * @return the saved Book entity
     */
    public Book addBook(Book book) {
        log.info("Started BookService::addBook. book info id = " + book.getBookInfo().getBookInfoId());

        // Finds the existing bookInfo by ID from another rest service.
        // Equivalent of BookInfo bookInfoDB = bookInfoRepository.findById(
        //                  book.getBookInfo().getBookInfoId()).orElse(null);
        // select * from bookinfo where bookinfo_id=?
        String url = BOOKINFO_BASE_URL + book.getBookInfo().getBookInfoId();
        BookInfo bookInfoDB =  restTemplate.getForObject(url, BookInfo.class);
        //log.info("bookInfoDB = " + bookInfoDB.getTitle() + " -- " + bookInfoDB.getAuthor());

        // Create Fault Object to give details on UI
        if (bookInfoDB == null) {
            LmsFault lmsFault = new LmsFault("Resource Not Found", "404",
                    BOOK_INFO_NOT_FOUND,"/lms/v1/books");
            return Utils.createFaultyBook(lmsFault);
        }
        //log.info("Title = " + bookInfoDB.getTitle());

        // Increment the count of total Books in the associated BookInfo entity
        bookInfoDB.incrementTotalQuantity();

        // save the updated BookInfo entity
        // update bookinfo set total_quantity=? where bookinfo_id=?
        bookInfoDB = bookInfoRepository.save(bookInfoDB);

        //book.setAvailable(true); // already set as default in Model class
        // set the updated BookInfo to the current Book
        book.setBookInfo(bookInfoDB);

        log.info("Returning BookService::addBook");

        // insert into book (available,bookinfo_id,edition,location,shelf_reference) values (?,?,?,?,?)
        return bookRepository.save(book);
    }

    /**
     * Handles requests to save an existing Book.
     * @param bookId the ID of the Book to be updated
     * @param bookFromClient the Book Object with updated values
     * @return the updated Book entity
     */
    public Book updateBook(Integer bookId, Book bookFromClient) {
        log.info("Started BookService::updateBook. bookId = " + bookId);

        // Finds the existing Book by ID.
        Book bookDB = this.getBookById(bookId);

        // Create Fault Object to give details on UI
        if (bookDB == null) {
            LmsFault lmsFault = new LmsFault("Resource Not Found", "404", "Book Not Found","/lms/v1/books/" + bookId);
            return Utils.createFaultyBook(lmsFault);
        }

        // Map all non null values
        Book bookUpdated = BookUtils.nonNullMapper(bookFromClient, bookDB);

        // Saves and returns the updated entity.
        log.info("Returning BookService::updateBook");

        // update book set edition=?,location=?,shelf_reference=? where book_id=?
        return bookRepository.save(bookUpdated);
    }

    /**
     * Handles requests to delete a Book by ID.
     * @param bookId the ID of the Book to be deleted
     * @return a success/failure message of deletion
     */
    public String deleteBookById(Integer bookId) {
        log.info("Started BookService::deleteBookById. bookId = " + bookId);

        // Finds the existing Book by ID.
        Book book = this.getBookById(bookId);
        //log.info("book.getAvailable() = " + book.getAvailable());

        // Book cannot be deleted if it is a currently borrowed books and not yet returned
        boolean bookCurrentlyBorrowed = !book.getAvailable();
        if (bookCurrentlyBorrowed) {
            log.info("Book " + bookId + " cannot be deleted. It is currently issued");
            log.info("Returning BookService::deleteBookById");
            return "error:Book " + bookId + " cannot be deleted. It is currently issued";
        }

        // Check if the User has any past transactions
        // SELECT COUNT(*) FROM TRANSACTION WHERE book_id =?
        int bookHistoryEntries = transactionRepository.findCountOfAllTransactionsByBook(bookId);

        // Book cannot be deleted if they have a record of any past transactions because the transaction will
        // first have to be deleted from the system to protect referential integrity of the Database
        boolean bookHasHistoricalTransactions = bookHistoryEntries > 0;
        if (bookHasHistoricalTransactions) {
            log.info("Book " + bookId + " cannot be deleted. Book has " + bookHistoryEntries +
                    " historical transaction(s)");
            String strError = "error:Book " + bookId + " cannot be deleted. Book has " +
                    bookHistoryEntries + " historical transaction(s)";
            strError += "<br>Details: SQL Error: 1451, SQLState: 23000 Cannot delete or " +
                    "update a parent row: a foreign key constraint fails";
            strError += "<br>Please escalate to technical support";

            log.info("Returning BookService::deleteBookById");
            return strError;
        }

        // Get the associated BookInfo entity by its book.
        BookInfo bookInfo = book.getBookInfo();

        // Decrement the count of Total Books in the associated BookInfo entity
        bookInfo.decrementTotalQuantity();

        // save the updated BookInfo entity
        // update bookinfo set total_quantity=? where bookinfo_id=?
        bookInfoRepository.save(bookInfo);

        // Deletes the book entity by its ID.
        // delete from book where book_id=?
        bookRepository.deleteById(bookId);
        log.info("Returning BookService::deleteBookById");
        return "success:Book " + bookId + " successfully deleted";
    }
}