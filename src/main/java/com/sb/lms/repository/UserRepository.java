package com.sb.lms.repository;

import com.sb.lms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for User entity.
 * Provides CRUD operations and custom query methods through JpaRepository.
 * @author Saarah Bedekar
 */
@Repository // Indicates that this interface is a Spring Data repository.
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Handles the DB call to get/retrieve existing Users by a name pattern.
     * @param name the name pattern of the Users to be retrieved
     * @return a list of the requested User entities
     */
    @Query(
            value = "SELECT * FROM User WHERE first_name LIKE %?1% or last_name LIKE %?1%",
            nativeQuery = true)
    List<User> findUserByName(String name);
}