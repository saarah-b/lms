package com.sb.lms.repository;

import com.sb.lms.model.ReportInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for ReportInfo entity.
 * Provides CRUD operations and custom query methods through JpaRepository.
 * @author Saarah Bedekar
 */
@Repository // Indicates that this interface is a Spring Data repository.
public interface ReportInfoRepository extends JpaRepository<ReportInfo, Integer> {

    /**
     * Handles the DB call to get/retrieve all ReportInfos by its name pattern
     * @param name the name pattern of all associated ReportInfos to be retrieved
     * @return a list of the requested ReportInfo entities
     */
    @Query(
            value = "SELECT * FROM REPORTINFO WHERE name LIKE %?1%",
            nativeQuery = true)
    List<ReportInfo> findReportInfoByName(String name);

}