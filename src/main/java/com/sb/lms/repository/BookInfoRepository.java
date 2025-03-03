package com.sb.lms.repository;

import com.sb.lms.model.BookInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for BookInfo entity.
 * Provides CRUD operations and custom query methods through JpaRepository.
 * @author Saarah Bedekar
 */
@Repository // Indicates that this interface is a Spring Data repository.
public interface BookInfoRepository extends JpaRepository<BookInfo, Integer> {

    /**
     * Handles the DB call to get/retrieve all BookInfos by a title pattern.
     * @param title the title pattern of the BookInfos to be retrieved
     * @return a list of the requested BookInfo entities
     */
    @Query(
            value = "SELECT * FROM BOOKINFO WHERE title LIKE %?1%",
            nativeQuery = true)
    List<BookInfo> findBookInfoByTitle(String title);
}