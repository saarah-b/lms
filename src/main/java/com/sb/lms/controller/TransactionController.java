package com.sb.lms.controller;

import com.sb.lms.model.Transaction;
import com.sb.lms.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contains Controller methods of Transaction CRUD
 * @author Saarah Bedekar
 */
@RestController // Marks this class as a REST controller.
@RequestMapping("/lms/v1/transactions")
@Slf4j  // logging
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    /*
    * There is no practical use case for retrieving all transactions in the system.
    * This query can potentially break the system as the dataset can be very large
    public List<Transaction> getAllTransactions() {
        log.info("Started TransactionController::getAllTransactions");
        return transactionService.getAllTransactions();
    }*/

    /**
     * Handles GET requests to get/retrieve an existing Transaction
     * @param transactionId the ID of the Transaction to be retrieved
     * @return the requested Transaction entity
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Integer transactionId) {
        log.info("Started TransactionController::getTransactionById. transactionId = " + transactionId);
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.getTransactionById(transactionId));
    }

    /**
     * Handles GET requests to get/retrieve all Transactions (whether returned or not books).
     * @param userId the ID of the User associated to the Transactions to be retrieved
     * @return a list of all Transactions
     */
    @GetMapping("/all/user/{userId}")
    public ResponseEntity<List<Transaction>> getAllTransactionsByUser(@PathVariable Integer userId) {
        log.info("Started TransactionController::getAllTransactionsByUser");
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.getAllTransactionsByUser(userId));
    }

    /**
     * Handles GET requests to get/retrieve all Transactions (whether returned or not books).
     * @param bookId the ID of the Book associated to the Transactions to be retrieved
     * @return a list of all Transactions
     */
    @GetMapping("/all/book/{bookId}")
    public ResponseEntity<List<Transaction>> getAllTransactionsByBook(@PathVariable Integer bookId) {
        log.info("Started TransactionController::getAllTransactionsByBook");
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.getAllTransactionsByBook(bookId));
    }

    /**
     * Handles GET requests to get/retrieve an existing open Transaction (i.e non returned books).
     * @param userId the ID of the User associated to the open Transaction to be retrieved
     * @return a list of all associated open Transactions
     */
    @GetMapping("/available/user/{userId}")
    public ResponseEntity<List<Transaction>> getOpenTransactionByUser(@PathVariable Integer userId) {
        log.info("Started TransactionController::getOpenTransactionByUser. userId = " + userId);
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.getOpenTransactionByUser(userId));
    }

    /**
     * Handles GET requests to get/retrieve an existing open Transaction (i.e non returned books).
     * @param bookId the ID of the Book associated to the open Transaction to be retrieved
     * @return a list of all associated open Transactions
     */
    @GetMapping("/available/book/{bookId}")
    public ResponseEntity<Transaction> getOpenTransactionByBook(@PathVariable Integer bookId) {
        log.info("Started TransactionController::getOpenTransactionByBook. bookId = " + bookId);
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.getOpenTransactionByBook(bookId));
    }

    /**
     * Handles POST requests to save (issue/borrow a book) a new Transaction.
     * @param transaction the Transaction entity to be saved
      * @return the saved Transaction entity
     */
    @PostMapping
    public ResponseEntity<Transaction> addTransaction(@RequestBody Transaction transaction) {
        log.info("Started TransactionController::addTransaction. " +
                "userId = " + transaction.getUser().getUserId() +
                ", bookId = " + transaction.getBook().getBookId());
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.addTransaction(transaction));
    }

    /**
     * Handles PUT requests to update (return a book) an existing Transaction.
     * @param transactionId the Transaction entity with updated information
     * @return the updated Transaction entity
     */
    @PutMapping("/{transactionId}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Integer transactionId) {
        log.info("Started TransactionController::updateTransaction. transactionId = " + transactionId);
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.updateTransaction(transactionId));
    }

    /**
     * Handles PUT requests to update (return a book) an existing Transaction.
     * @param bookId the ID of the Book for which the Transaction has to be updated
     * @return the updated Transaction entity
     */
    @PutMapping("/book/{bookId}")
    public ResponseEntity<Transaction> returnBook(@PathVariable Integer bookId) {
        log.info("Started TransactionController::returnBook. bookId = " + bookId);
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.returnBook(bookId));
    }

    /**
     * Handles PUT requests to update (return a book) an existing Transaction.
     * @param transaction the Transaction entity with updated information
     * @return the updated Transaction entity
     */
    @PutMapping("/book/transaction")
    public ResponseEntity<Transaction> returnBook(@RequestBody Transaction transaction) {
        log.info("Started TransactionController::returnBook transaction");
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.returnBook(transaction));
    }

    /**
     * A Transaction once created cannot be deleted for audit reasons.
     * But if there is a need to delete it for DB referential integrity reasons,
     * then the support will delete it directly on DB
    @DeleteMapping("/{userId}")
    public String deleteUserById(@PathVariable Integer userId) {
        log.info("Started UserController::deleteUserById. userId = " + userId);
        transactionService.deleteUserById(userId);
        return "Deleted Successfully";
    }
    */
}
