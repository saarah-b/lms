package com.sb.lms.service;

import com.sb.lms.model.LmsFault;
import com.sb.lms.model.ReportInfo;
import com.sb.lms.repository.ReportInfoRepository;
import com.sb.lms.repository.ReportRepository;
import com.sb.lms.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Contains Service methods of ReportInfo CRUD
 * @author Saarah Bedekar
 */
@Service
@Slf4j
public class ReportInfoService {

    @Autowired
    private ReportInfoRepository reportInfoRepository; // Injects the ReportInfoRepository dependency.

    @Autowired
    private ReportRepository reportRepository; // Injects the ReportInfoRepository dependency.

    /**
     * Handles requests to get/retrieve all ReportInfos (Full Report Catalogue).
     * @return the requested list of ReportInfos
     */
    public List<ReportInfo> getAllReportInfos() {
        log.info("Started ReportInfoService::getAllReportInfos");

        // select * from reportinfo
        return reportInfoRepository.findAll();
    }

    /**
     * Handles requests to get/retrieve an existing ReportInfo.
     * @param reportInfoId the Id of ReportInfo to be retrieved
     * @return the requested ReportInfo entity
     */
    public ReportInfo getReportInfoById(Integer reportInfoId) {
        log.info("Started ReportInfoService::getReportInfoById. reportInfoId = " + reportInfoId);

        // Gets the reportinfo entity by its ID.
        // select * from reportinfo where reportinfo_id=?
        ReportInfo reportInfoDB = reportInfoRepository.findById(reportInfoId).orElse(null);

        // Create Fault Object to give details on UI
        if (reportInfoDB == null) {
            LmsFault lmsFault = new LmsFault("Resource Not Found", "404",
                    "ReportInfo Not Found","/lms/v1/reportinfos/" + reportInfoId);
            return Utils.createFaultyReportInfo(lmsFault);
        }
        log.info("Returning ReportInfoService::getReportInfoById");
        return reportInfoDB;
    }

    /**
     * Handles requests to get/retrieve existing ReportInfos by their title pattern.
     * @param name the title pattern of the ReportInfos to be retrieved
     * @return a list of all requested ReportInfos
     */
    public List<ReportInfo> getReportInfoByName(String name) {
        log.info("Started ReportInfoService::getReportInfoByName. name = " + name);

        // Gets the ReportInfo entity by its name pattern.
        // SELECT * FROM REPORTINFO WHERE name LIKE %?%;
        List<ReportInfo> reportInfoDBList = reportInfoRepository.findReportInfoByName(name);

        // Create Fault Object to give details on UI
        if (reportInfoDBList == null || reportInfoDBList.isEmpty()) {
            LmsFault lmsFault = new LmsFault("Resource Not Found", "404",
                    "ReportInfo Not Found","/lms/v1/reportinfos/name/" + name);
            return Utils.createFaultyReportInfoInList(lmsFault);
        }
        log.info("Returning ReportInfoService::getReportInfoByName. name = " + name);
        return reportInfoDBList;
    }

    /**
     * Handles requests to save a new ReportInfo.
     * @param reportInfo the ReportInfo entity to be saved
     * @return the saved ReportInfo entity
     */
    public ReportInfo addReportInfo(ReportInfo reportInfo) {
        log.info("Started ReportInfoService::addReportInfo. reportInfo = " + reportInfo);

        // insert into reportinfo (name,sql_statement,time_to_generate) values (?,?,?)
        return reportInfoRepository.save(reportInfo);
    }

    /**
     * Handles requests to save an existing ReportInfo.
     * @param reportInfoId the ID of the ReportInfo to be updated
     * @param reportInfoFromClient the ReportInfo Object with updated values
     * @return the updated ReportInfo entity
     */
    public ReportInfo updateReportInfo(Integer reportInfoId, ReportInfo reportInfoFromClient) {
        log.info("Started ReportInfoService::updateReportInfo. reportInfoId = " + reportInfoId);

        // Finds the existing ReportInfo by ID.
        ReportInfo reportInfoDB = this.getReportInfoById(reportInfoId);

        // Create Fault Object to give details on UI
        if (reportInfoDB == null) {
            LmsFault lmsFault = new LmsFault("Resource Not Found", "404",
                    "ReportInfo Not Found","/lms/v1/reportinfos/" + reportInfoId);
            return Utils.createFaultyReportInfo(lmsFault);
        }

        // Map all non null values
        ReportInfo reportInfoUpdate = ReportInfoUtils.nonNullMapper(reportInfoFromClient, reportInfoDB);

        // Saves and returns the updated entity.
        log.info("Returning ReportInfoService::updateReportInfo");

        // update reportinfo set time_to_generate=? where reportinfo_id=?
        return reportInfoRepository.save(reportInfoUpdate);
    }

    /**
     * Handles requests to delete a ReportInfo by ID.
     * @param reportInfoId the ID of the ReportInfo to be deleted
     * @return a success/failure message of deletion
     */
    public String deleteReportInfoById(Integer reportInfoId) {
        log.info("Started ReportInfoService::deleteReportInfoById. reportInfoId = " + reportInfoId);

        // Finds the existing ReportInfo by ID.
        ReportInfo reportInfo = this.getReportInfoById(reportInfoId);

        // Check if the ReportInfo has any associated reports
        // SELECT COUNT(*) FROM REPORT WHERE reportinfo_id =?
        int reportCounts = reportRepository.findTotalReportCountsByReportInfo(reportInfoId);
        if (reportCounts > 0 ) {
            log.info("Returning ReportInfoService::deleteReportInfoById");
            return "error:ReportInfo " + reportInfoId + " cannot be deleted. It has " +
                    reportCounts + " associated report(s).";
        }

        // Deletes the ReportInfo entity by its ID.
        // delete from reportinfo where reportinfo_id=?
        reportInfoRepository.deleteById(reportInfoId);
        log.info("Returning ReportInfoService::deleteReportInfoById");
        return "success:ReportInfo " + reportInfoId + " successfully deleted";
    }
}