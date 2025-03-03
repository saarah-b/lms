package com.sb.lms.repository;

import com.sb.lms.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Book entity.
 * Provides CRUD operations and custom query methods through JpaRepository.
 * @author Saarah Bedekar
 */
@Repository // Indicates that this interface is a Spring Data repository.
public interface BookRepository extends JpaRepository<Book, Integer> {

    /**
     * Handles the DB call to get/retrieve the count of all Books by a bookinfoId.
     * @param bookinfoId the bookinfoId of all the Books for which the count is to be retrieved
     * @return the count of all requested Books
     */
    @Query(
            value = "SELECT COUNT(*) FROM BOOK WHERE bookinfo_id =?1",
            nativeQuery = true)
    Integer findTotalBookCountsByBookInfo(Integer bookinfoId);

    /**
     * Handles the DB call to get/retrieve the count of all available Books by a bookinfoId.
     * @param bookinfoId the bookinfoId of all available Books for which the count is to be retrieved
     * @return the count of all requested available Books
     */
    @Query(
            value = "SELECT COUNT(*) FROM BOOK WHERE bookinfo_id =?1 and available=true",
            nativeQuery = true)
    Integer findAvailableBookCountsByBookInfo(Integer bookinfoId);

    /**
     * Handles the DB call to get/retrieve all Books by a bookinfoId.
     * @param bookinfoId the bookinfoId of all the Books to be retrieved
     * @return a list of the requested Book entities
     */
    @Query(
            value = "SELECT * FROM BOOK WHERE bookinfo_id =?1",
            nativeQuery = true)
    List<Book> findAllBooksByBookInfo(Integer bookinfoId);

    /**
     * Handles the DB call to get/retrieve all available Books by a bookinfoId.
     * @param bookinfoId the bookinfoId of all the available Books to be retrieved
     * @return a list of the requested Book entities
     */
    @Query(
            value = "SELECT * FROM BOOK WHERE bookinfo_id =?1 and available=true",
            nativeQuery = true)
    List<Book> findAvailableBooksByBookInfo(Integer bookinfoId);

}
