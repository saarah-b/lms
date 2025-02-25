package com.sb.lms.service;

import com.sb.lms.model.ReportInfo;
import com.sb.lms.utils.Utils;
import lombok.extern.slf4j.Slf4j;

/**
 * Contains Entity mapping utility for updates of ReportInfo
 * @author Saarah Bedekar
 */
@Slf4j
class ReportInfoUtils {

    public ReportInfoUtils() {};

    /**
     * Handles the mapping of Non Null values in the ReportInfo to be updated to the DB
     * @param reportInfoFromClient the ReportInfo entity sent by the UI
     * @param reportInfoDB the ReportInfo Object with updated values
     * @return the updated ReportInfo entity to be saved to Database
     */
    public static ReportInfo nonNullMapper(ReportInfo reportInfoFromClient, ReportInfo reportInfoDB) {
        log.info("Started ReportInfoUtils::nonNullMapper");
        if (!Utils.isStringBlank(reportInfoFromClient.getSqlStatement())) {
            reportInfoDB.setSqlStatement(reportInfoFromClient.getSqlStatement());
        }
        if (!Utils.isStringBlank(reportInfoFromClient.getTimeToGenerate())) {
            reportInfoDB.setTimeToGenerate(reportInfoFromClient.getTimeToGenerate());
        }
        log.info("Returning ReportInfoUtils::nonNullMapper");
        return reportInfoDB;
    }
}
