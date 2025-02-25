package com.sb.lms.repository;

import com.sb.lms.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Transaction entity.
 * Provides CRUD operations and custom query methods through JpaRepository.
 * @author Saarah Bedekar
 */
@Repository // Indicates that this interface is a Spring Data repository.
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    /**
     * Handles the DB call to get/retrieve count of existing open Transactions by it's User Id.
     * @param userId the Id of the User for which open transactions will be searched for
     * @return the count of existing open Transactions by it's User Id
     */
    @Query(
            value = "SELECT COUNT(*) FROM TRANSACTION WHERE user_id =?1 and returned=false",
            nativeQuery = true)
    Integer findCountOfOpenTransactionsByUser(Integer userId);

    /**
     * Handles the DB call to get/retrieve count of all existing Transactions by it's user Id.
     * @param userId the name pattern of the Transaction to be retrieved
     * @return the requested User entity
     */
    @Query(
            value = "SELECT COUNT(*) FROM TRANSACTION WHERE user_id =?1",
            nativeQuery = true)
    Integer findCountOfAllTransactionsByUser(Integer userId);

    /**
     * Handles the DB call to get/retrieve count of existing open Transactions by it's Book Id.
     * @param bookId the name pattern of the Book to be retrieved
     * @return the requested User entity
     */
    @Query(
            value = "SELECT COUNT(*) FROM TRANSACTION WHERE book_id =?1",
            nativeQuery = true)
    Integer findCountOfAllTransactionsByBook(Integer bookId);

    /**
     * Handles the DB call to get/retrieve all open Transactions by its userId
     * @param userId the userId for which all associated open Transactions to be retrieved
     * @return a list of the requested open Transaction entities
     */
    @Query(
            value = "SELECT * FROM TRANSACTION WHERE user_id =?1 and returned=false",
            nativeQuery = true)
    List<Transaction> findOpenTransactionsByUser(Integer userId);

    /**
     * Handles the DB call to get/retrieve a Transaction by its bookId
     * @param bookId the bookId for which the associated Transaction is to be retrieved
     * @return the requested Transaction entity
     */
    @Query(
            value = "SELECT * FROM TRANSACTION WHERE book_id =?1 and returned=false",
            nativeQuery = true)
    Transaction findOpenTransactionByBook(Integer bookId);

    /**
     * Handles the DB call to get/retrieve all Transactions by its userId
     * @param userId the userId for which all associated Transactions to be retrieved
     * @return a list of the requested Transaction entities
     */
    @Query(
            value = "SELECT * FROM TRANSACTION WHERE user_id =?1",
            nativeQuery = true)
    List<Transaction> findAllTransactionsByUser(Integer userId);

    /**
     * Handles the DB call to get/retrieve all Transactions by its bookId
     * @param bookId the bookId for which all associated Transactions to be retrieved
     * @return a list of the requested Transaction entities
     */
    @Query(
            value = "SELECT * FROM TRANSACTION WHERE book_id =?1",
            nativeQuery = true)
    List<Transaction> findAllTransactionsByBook(Integer bookId);

}
