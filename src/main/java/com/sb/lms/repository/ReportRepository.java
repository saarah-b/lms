package com.sb.lms.repository;

import com.sb.lms.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for Report entity.
 * Provides CRUD operations and custom query methods through JpaRepository.
 * @author Saarah Bedekar
 */
@Repository // Indicates that this interface is a Spring Data repository.
public interface ReportRepository extends JpaRepository<Report, Integer> {

    /**
     * Handles the DB call to get/retrieve count of all Reports by it's reportInfoId.
     * @param reportInfoId the reportInfoId for which all Reports will be searched for
     * @return the count of all requested Reports
     */
    @Query(
            value = "SELECT COUNT(*) FROM REPORT WHERE reportinfo_id =?1",
            nativeQuery = true)
    Integer findTotalReportCountsByReportInfo(Integer reportInfoId);

    /**
     * Handles the DB call to get/retrieve all Reports by a reportInfoId.
     * @param reportInfoId the reportInfoId of all the Reports to be retrieved
     * @return a list of the requested Report entities
     */
    @Query(
            value = "SELECT * FROM REPORT WHERE reportinfo_id =?1",
            nativeQuery = true)
    List<Report> findAllReportsByReportInfo(Integer reportInfoId);

    /**
     * Handles the DB call to get/retrieve all Reports by its date of generation
     * @param generatedDate the date of report creation for all associated Reports to be retrieved
     * @return a list of the requested Report entities
     */
    @Query(
            value = "SELECT * FROM REPORT WHERE generated_date LIKE %?1% ",
            nativeQuery = true)
    List<Report> findReportsByGeneratedDate(String generatedDate);

}
