package com.sb.lms.service;

import com.sb.lms.model.Book;
import com.sb.lms.model.LmsFault;
import com.sb.lms.model.Transaction;
import com.sb.lms.model.User;
import com.sb.lms.repository.BookRepository;
import com.sb.lms.repository.TransactionRepository;
import com.sb.lms.repository.UserRepository;
import com.sb.lms.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Contains Service methods of Transaction CRUD
 * @author Saarah Bedekar
 */
@Service
@Slf4j
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository; // Injects the UserRepository dependency.

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    private static final int MAX_BOOKS_PER_USER = 3;
    private static final String MAX_BOOKS_PER_USER_LIMIT_USED = "User has a max limit of borrowing only "
            + MAX_BOOKS_PER_USER + " books at any time";
    private static final String BOOKS_ALREADY_BORROWED = "Book is already borrowed by another User";

    /*
    Will not be practically used as there is no use to know all Transactions in the system
    public List<Transaction> getAllTransactions() {
        log.info("Started TransactionService::getAllTransactions");
        return transactionRepository.findAll();
    }
    */

    /**
     * Handles requests to get/retrieve all existing Transactions of a user
     * @param userId the Id of User for which all Transactions are to be retrieved
     * @return the requested list of Transaction entities
     */
    public List<Transaction> getAllTransactionsByUser(Integer userId) {
        log.info("Started TransactionService::getAllTransactionsByUser");

        // Gets the Transaction entity list by its associated User
        // SELECT * FROM TRANSACTION WHERE user_id =?
        List<Transaction> transactionDBList = transactionRepository.findAllTransactionsByUser(userId);

        // Create Fault Object to give details on UI
        if (transactionDBList == null || transactionDBList.isEmpty()) {
            LmsFault lmsFault = new LmsFault("Resource Not Found", "404",
                "Transaction(s) Not Found", "/lms/v1/transactions/all/user/" + userId);
            return Utils.createFaultyTransactionInList(lmsFault);
        }
        log.info("Returning TransactionService::getAllTransactionsByUser");
        // Prepares every Transaction in the list with its calculated fine
        return prepareTransactionList (transactionDBList);
    }

    /**
     * Handles requests to get/retrieve all existing Transactions of a Book
     * @param bookId the Id of Book for which all Transactions are to be retrieved
     * @return the requested list of Transaction entities
     */
    public List<Transaction> getAllTransactionsByBook(Integer bookId) {
        log.info("Started TransactionService::getAllTransactionsByBook");

        // Gets the Transaction entity list by its associated User
        // SELECT * FROM TRANSACTION WHERE book_id =?
        // select * from user u left join address a on a.address_id=u.address_id where user_id=?
        List<Transaction> transactionDBList = transactionRepository.findAllTransactionsByBook(bookId);

        // Create Fault Object to give details on UI
        if (transactionDBList == null || transactionDBList.isEmpty()) {
            LmsFault lmsFault = new LmsFault("Resource Not Found", "404",
                    "Transaction(s) Not Found", "/lms/v1/transactions/all/book/" + bookId);
            return Utils.createFaultyTransactionInList(lmsFault);
        }
        log.info("Returning TransactionService::getAllTransactionsByBook");

        // Prepares every Transaction in the list with its calculated fine
        return prepareTransactionList (transactionDBList);
    }

    /**
     * Handles requests to get/retrieve existing open Transactions of a User
     * @param userId the Id of User for which open Transactions are to be retrieved
     * @return the requested list of Transaction entities
     */
    public List<Transaction> getOpenTransactionByUser(Integer userId) {
        log.info("Started TransactionService::getOpenTransactionByUser. userId = " + userId);
        LmsFault lmsFault;

        // Gets the user entity by its ID.
        // select * from user u left join address a on a.address_id=u.address_id where user_id=?
        User userDB = userRepository.findById(userId).orElse(null);

        // Create Fault Object to give details on UI
        if (userDB == null) {
            lmsFault = new LmsFault("Resource Not Found", "404",
                    "User Not Found", "/lms/v1/users/" + userId);
            return Utils.createFaultyTransactionInList(lmsFault);
        }

        // SELECT * FROM TRANSACTION WHERE user_id =? and returned=false
        // select * from book b left join bookinfo bi on bi.bookinfo_id=b.bookinfo_id where b.book_id=?
        List<Transaction> transactionDBList = transactionRepository.findOpenTransactionsByUser(userId);

        // Create Fault Object to give details on UI
        if (transactionDBList == null || transactionDBList.isEmpty()) {
            lmsFault = new LmsFault("Resource Not Found", "404",
                    "(Open) Transaction(s) Not Found", "/lms/v1/transactions/available/user/" + userId);
            return Utils.createFaultyTransactionInList(lmsFault);
        }
        log.info("Returning TransactionService::getOpenTransactionByUser. userId = " + userId);

        // Prepares every Transaction in the list with its calculated fine
        return prepareTransactionList (transactionDBList);
    }

    /**
     * Handles requests to get/retrieve an existing open Transaction of a Book
     * @param bookId the Id of Book for which an open Transaction is to be retrieved
     * @return the requested Transaction entity
     */
    public Transaction getOpenTransactionByBook(Integer bookId) {
        log.info("Started TransactionService::getOpenTransactionByBook. bookId = " + bookId);

        LmsFault lmsFault;

        // Gets the book entity by its ID.
        // select * from book b left join bookinfo bi on bi.bookinfo_id=b.bookinfo_id where b.book_id=?
        Book bookDB = bookRepository.findById(bookId).orElse(null);

        // Create Fault Object to give details on UI
        if (bookDB == null) {
            lmsFault = new LmsFault("Resource Not Found", "404",
                    "Book Not Found", "/lms/v1/books/" + bookId);
            return Utils.createFaultyTransaction(lmsFault);
        }

        // SELECT * FROM TRANSACTION WHERE book_id =? and returned=false
        // select * from user u left join address a on a.address_id=u.address_id where user_id=?
        Transaction transactionDB = transactionRepository.findOpenTransactionByBook(bookId);

        // Create Fault Object to give details on UI
        if (transactionDB == null) {
            lmsFault = new LmsFault("Resource Not Found", "404",
                    "(Open) Transaction Not Found", "/lms/v1/transactions/available/book/" + bookId);
            return Utils.createFaultyTransaction(lmsFault);
        }
        log.info("Returning TransactionService::getOpenTransactionByBook. bookId = " + bookId);

        // Prepares every Transaction in the list with its calculated fine
        return prepareTransaction (transactionDB);
    }

    public Transaction getTransactionById(Integer transactionId) {
        log.info("Started TransactionService::getTransactionById. transactionId = " + transactionId);
        // Gets the Transaction entity by its ID.
        Transaction transactionDB = transactionRepository.findById(transactionId).orElse(null);

        if (transactionDB == null) {
            LmsFault lmsFault = new LmsFault("Resource Not Found", "404",
                    "Transaction Not Found", "/lms/v1/transactions/" + transactionId);
            return Utils.createFaultyTransaction(lmsFault);
        }
        // Prepares the Transaction with its calculated fine
        return prepareTransaction (transactionDB);
    }

    /**
     * Handles requests to save a new Transaction (i.e Issue a Book To a User).
     * @param transaction the Transaction entity to be saved
     * @return the saved Transaction entity
     */
    public Transaction addTransaction(Transaction transaction) {
        log.info("Started TransactionService::addTransaction. userId = "
                + transaction.getUser().getUserId() + ", bookId = " + transaction.getBook().getBookId());

        LmsFault lmsFault;

        // Gets the user entity by its ID.
        // select * from user u left join address a on a.address_id=u.address_id where user_id=?
        User userDB = userRepository.findById(transaction.getUser().getUserId()).orElse(null);

        // Create Fault Object to give details on UI
        if (userDB == null) {
            lmsFault = new LmsFault("Resource Not Found", "404",
                    "User Not Found", "/lms/v1/users/" + transaction.getUser().getUserId());
            return Utils.createFaultyTransaction(lmsFault);
        }

        // Gets the book entity by its ID.
        // select * from book b left join bookinfo bi on bi.bookinfo_id=b.bookinfo_id where b.book_id=?
        Book bookDB = bookRepository.findById(transaction.getBook().getBookId()).orElse(null);

        // Create Fault Object to give details on UI
        if (bookDB == null) {
            lmsFault = new LmsFault("Resource Not Found", "404",
                    "Book Not Found", "/lms/v1/books/" + transaction.getBook().getBookId());
            return Utils.createFaultyTransaction(lmsFault);
        }
        // Get the count of book(s) currently issued by the User to check the borrowing limit
        // SELECT COUNT(*) FROM TRANSACTION WHERE user_id =? and returned=false
        int userBookCount = transactionRepository.findCountOfOpenTransactionsByUser(transaction.getUser().getUserId());

        // User cannot borrow more than 3 books at any point in time
        if (userBookCount == MAX_BOOKS_PER_USER) {
            log.info("User already has 3 books borrowed");
            lmsFault = new LmsFault("Application Constraint Error", "210",
                    MAX_BOOKS_PER_USER_LIMIT_USED, "/lms/v1/transactions");
            return Utils.createFaultyTransaction(lmsFault);
        }

        // Book to be borrowed cannot be currently borrowed by a User
        if (!bookDB.getAvailable()) {
            log.info("Book is already borrowed");
            lmsFault = new LmsFault("Application Constraint Error", "210",
                    BOOKS_ALREADY_BORROWED, "/lms/v1/transactions");
            return Utils.createFaultyTransaction(lmsFault);
        }
        // The user and book entities get associated to the transaction
        transaction.issueBook(transaction.getUser(), transaction.getBook()); //adds user and book the Transaction

        // Issue date is auto set as today's date
        transaction.setIssueDate(Utils.convertNowToSQLTS());

        // Return date is auto set as 2 weeks from issue date
        transaction.setReturnDate(Utils.generateReturnSQLTS(transaction.getIssueDate()));

        // Mark the book as unavailable for borrowing until returned
        bookDB.setAvailable(false);

        // update book set available=?,bookinfo_id=?,edition=?,location=?,shelf_reference=? where book_id=?
        bookRepository.save(bookDB);

        // Associate the updated book back to the Transaction
        transaction.setBook(bookDB);
        log.info("Returning TransactionService::addTransaction");

        // insert into transaction (actual_return_date_date,book_id,fine,issue_date,return_date,
        //              returned,user_id) values (?,?,?,?,?,?,?)
        return transactionRepository.save(transaction);
    }

    /**
     * Handles requests to update an existing Transaction (i.e Return a Book From a User).
     * @param transactionId the ID of the Transaction to be updated
     * @return the updated Transaction entity
     */
    public Transaction updateTransaction(Integer transactionId) {
        log.info("Started TransactionService::updateTransaction. transactionId = " + transactionId);

        // Finds the existing Transaction by ID.
        Transaction transactionDB = this.getTransactionById(transactionId);

        // Create Fault Object to give details on UI
        if (transactionDB == null) {
            LmsFault lmsFault = new LmsFault("Resource Not Found", "404",
                    "Transaction Not Found", "/lms/v1/transactions/" + transactionId);
            return Utils.createFaultyTransaction(lmsFault);
        }
        // Process the marking of returning the book
        Transaction validTransaction = returnBook(transactionDB.getBook().getBookId());

        log.info("Returning TransactionService::updateTransaction");
        return validTransaction;
    }

    /**
     * Handles processing of returning a book
     * @param bookId the ID of the Book to be marked as returned
     * @return the updated Transaction entity
     */
    public Transaction returnBook(Integer bookId) {
        log.info("Started TransactionService::returnBook bookId = " + bookId);

        // Finds the existing open Transaction by its Book ID.
        Transaction transactionDB = this.getOpenTransactionByBook(bookId);

        // Set Book to be available
        transactionDB.getBook().setAvailable(true);

        // insert into book (available,bookinfo_id,edition,location,shelf_reference) values (?,?,?,?,?)
        bookRepository.save(transactionDB.getBook());

        // Set Transaction to be returned
        // set today's date to the Actual Returned date
        transactionDB.setActualReturnDate(Utils.convertNowToSQLTS());

        // calculate fine with respect to any late returns
        transactionDB.setFine(transactionDB.calculateFine()); // fine calculation

        // Mark the Transaction as returned and complete
        transactionDB.setReturned(true);

        log.info("Returning TransactionService::returnBook(bookId)");

        // insert into transaction (actual_return_date_date,book_id,fine,issue_date,return_date,
        //              returned,user_id) values (?,?,?,?,?,?,?)
        return transactionRepository.save(transactionDB);
    }

    /**
     * Handles processing of returning a book by its Transaction Object
     * @param transaction the Transaction entity to be marked as returned
     * @return the updated Transaction entity
     */
    public Transaction returnBook(Transaction transaction) {
        log.info("Started TransactionService::returnBook Transaction = " + transaction.getTransactionId());
        Transaction validTransaction = returnBook(transaction.getBook().getBookId());

        log.info("Returning TransactionService::returnBook(transaction)");
        return validTransaction;
    }

    /**
     * Handles the preparation of a Transaction with its calculated fine
     * @param transaction the Transaction entity to be updated with fine calculation
     * @return the prepared Transaction entity
     */
    private Transaction prepareTransaction (Transaction transaction) {
        log.info("Started TransactionService::prepareTransaction Transaction = " + transaction.getTransactionId());

        // Calculate fine only if a transaction is still open and not yet returned
        if (!transaction.getReturned())
            //calculate fine as per business rules
            transaction.calculateFine();

        log.info("Returning TransactionService::prepareTransaction");
        return transaction;
    }

    /**
     * Handles the preparation of all Transaction in the list with their calculated fine
     * @param transactionList the list of Transactions in which every Transaction to be updated with fine calculation
     * @return the prepared Transaction entity
     */
    private List<Transaction> prepareTransactionList (List<Transaction> transactionList) {
        log.info("Started TransactionService::prepareTransaction");
        if (!(transactionList == null || transactionList.isEmpty())) {
            for (Transaction transaction : transactionList) { // max 3 iterations because of user limit
                // Calculate fine only if a transaction is still open and not yet returned
                if (!transaction.getReturned())
                    //calculate fine as per business rules
                    transaction.calculateFine();
            }
        }
        log.info("Returning TransactionService::prepareTransactionList");
        return transactionList;
    }
}