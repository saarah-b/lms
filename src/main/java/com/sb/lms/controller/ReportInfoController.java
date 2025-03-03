package com.sb.lms.controller;

import com.sb.lms.model.ReportInfo;
import com.sb.lms.service.ReportInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contains Controller methods of ReportInfo CRUD
 * @author Saarah Bedekar
 */
@RestController // Marks this class as a REST controller.
@RequestMapping("/lms/v1/reportinfos") // Base url mapping
@Slf4j
public class ReportInfoController {

    @Autowired
    private ReportInfoService reportInfoService;

    /**
     * Handles GET requests of the full catalog of all existing ReportInfos.
     * @return a list of all ReportInfos
     */
    @GetMapping
    public ResponseEntity<List<ReportInfo>> getAllReportInfos() {
        log.info("Started ReportInfoController::getAllReportInfos");
        return ResponseEntity.status(HttpStatus.OK).body(reportInfoService.getAllReportInfos());
    }

    /*
    @GetMapping("/report/{reportId}")
    public ReportInfo getReportInfoByReport(@PathVariable Integer reportId) {
        log.info("Started ReportInfoController::getReportInfoByReport. reportId = " + reportId);
        return reportInfoService.getReportInfoByReport(reportId);
    }*/

    /**
     * Handles GET requests to get/retrieve an existing ReportInfo.
     * @param reportInfoId the ID of the ReportInfo to be retrieved
     * @return the requested ReportInfo entity
     */
    @GetMapping("/{reportInfoId}")
    public ResponseEntity<ReportInfo> getReportInfoById(@PathVariable Integer reportInfoId) {
        log.info("Started ReportInfoController::getBookById. bookInfoId = " + reportInfoId);
        return ResponseEntity.status(HttpStatus.OK).body(reportInfoService.getReportInfoById(reportInfoId));
    }

    /**
     * Handles GET requests to get/retrieve existing ReportInfos.
     * @param name the name pattern of the ReportInfos to be retrieved
     * @return a list of all requested ReportInfos
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<List<ReportInfo>> getReportInfoByName(@PathVariable String name) {
        log.info("Started ReportInfoController::getReportInfoByName. name = " + name);
        return ResponseEntity.status(HttpStatus.OK).body(reportInfoService.getReportInfoByName(name));
    }

    /**
     * Handles POST requests to save a new ReportInfo.
     * @param reportInfo the ReportInfo entity to be saved
     * @return the saved ReportInfo entity
     */
    @PostMapping
    public ResponseEntity<ReportInfo> addReportInfo(@RequestBody ReportInfo reportInfo) {
        log.info("Started ReportInfoController::addReportInfo. bookInfoId = " + reportInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(reportInfoService.addReportInfo(reportInfo));
    }

    /**
     * Handles PUT requests to update an existing ReportInfo.
     * @param reportInfoId the ID of the ReportInfo to be updated
     * @param reportInfo the ReportInfo entity with updated information
     * @return the updated ReportInfo entity
     */
    @PutMapping("/{reportInfoId}")
    public ResponseEntity<ReportInfo> updateReportInfo(@PathVariable Integer reportInfoId, @RequestBody ReportInfo reportInfo) {
        log.info("Started ReportInfoController::updateReportInfo. reportInfoId = " + reportInfoId);
        return ResponseEntity.status(HttpStatus.OK).body(reportInfoService.updateReportInfo(reportInfoId, reportInfo));
    }

    /**
     * Handles DELETE requests to remove a ReportInfo by ID.
     * @param reportInfoId the ID of the ReportInfo to be deleted
     * @return a success/failure message
     */
    @DeleteMapping("/{reportInfoId}")
    public ResponseEntity<String> deleteReportInfoById(@PathVariable Integer reportInfoId) {
        log.info("Started ReportInfoController::deleteReportInfoById. reportInfoId = " + reportInfoId);
        return ResponseEntity.status(HttpStatus.OK).body(reportInfoService.deleteReportInfoById(reportInfoId));
    }
}