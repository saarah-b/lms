package com.sb.lms.repository;

import com.sb.lms.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Address entity.
 * Provides CRUD operations and custom query methods through JpaRepository.
 * @author Saarah Bedekar *
 */
@Repository // Indicates that this interface is a Spring Data repository.
public interface AddressRepository extends JpaRepository<Address, Integer> {
    //  No Custom methods
}
