package com.sb.lms.controller;

import com.sb.lms.model.Report;
import com.sb.lms.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Contains Controller methods of Report CRUD
 * @author Saarah Bedekar
 */
@RestController // Marks this class as a REST controller.
@RequestMapping("/lms/v1/reports") // Base url mapping
@Slf4j  // logging
public class ReportController {

    @Autowired
    private ReportService reportService;

    /*
    * There is no practical use case for retrieving all report in the system.
    * This query can potentially break the system as the dataset can be very large
    @GetMapping
    public List<Report> getAllReports() {
        log.info("Started ReportController::getAllReports");
        return reportService.getAllReports();
    }*/

    /**
     * Handles GET requests to get/retrieve an existing Report.
     * @param reportId the ID of the Report to be retrieved
     * @return the requested Report entity
     */
    @GetMapping("/{reportId}")
    public ResponseEntity<Report> getReportById(@PathVariable Integer reportId) {
        log.info("Started ReportController::getReportById. reportId = " + reportId);
        return ResponseEntity.status(HttpStatus.OK).body(reportService.getReportById(reportId));
    }

    /**
     * Handles GET requests to get/retrieve existing Reports.
     * @param generatedDate the generatedDate of the Reports to be retrieved
     * @return a list of all requested Reports
     */
    @GetMapping("/date/{generatedDate}")
    public ResponseEntity<List<Report>> getReportsByGeneratedDate(@PathVariable String generatedDate) {
        log.info("Started ReportController::getReportsByGeneratedDate. generatedDate = " + generatedDate);
        return ResponseEntity.status(HttpStatus.OK).body(reportService.getReportsByGeneratedDate(generatedDate));
    }

    /**
     *
     * Handles POST requests to save a new Report.
     * @param reportInfoId the ID of the ReportInfo for which a report has to be generated and saved
     * @return the saved Report entity
     */
    @PostMapping("/reportinfo/{reportInfoId}")
    public ResponseEntity<Report> addReportForReportInfo(@PathVariable Integer reportInfoId) {
        log.info("Started ReportController::addReport. reportInfoId = " + reportInfoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(reportService.addReportForReportInfo(reportInfoId));
    }

    /**
     * A Report once created cannot be updated for audit reasons.
     *
    @PutMapping("/{reportId}")
    public Report updateReport(@PathVariable Integer reportId, @RequestBody Report report) {
        log.info("Started ReportController::updateReport");
        return reportService.updateReport(reportId, report);
    }

    /**
     * A Report once created cannot be deleted for audit reasons.
     * It can only be purged once the retention period (currently 10 yrs) is over
     *
    @DeleteMapping("/{reportId}")
    public Report deleteReportById(@PathVariable Integer reportId) {
        log.info("Started ReportController::deleteReportById. reportId = " + reportId);
        reportService.deleteReportById(reportId);
        return "Deleted Successfully";
    }
    */
}