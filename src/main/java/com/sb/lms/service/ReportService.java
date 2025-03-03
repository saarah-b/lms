package com.sb.lms.service;

import com.sb.lms.model.LmsFault;
import com.sb.lms.model.Report;
import com.sb.lms.model.ReportInfo;
import com.sb.lms.repository.*;
import com.sb.lms.utils.Utils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

/**
 * Contains Service methods of Report CRUD
 * Modelled it to use the old classic way of JDBC connection using Datasource, ResultSet to show I know those concepts
 * @author Saarah Bedekar
 */
@Service
@Slf4j
@Data
public class ReportService {

    @Value("${spring.datasource.url}")
    String url;

    @Value("${spring.datasource.username}")
    String username;

    @Value("${spring.datasource.password}")
    String password;

    @Autowired
    private ReportRepository reportRepository; // Injects the ReportRepository dependency.

    @Autowired
    private ReportInfoRepository reportInfoRepository; // Injects the ReportRepository dependency.

    @Autowired
    private DataSource datasource;

    /*
    Will not be practically used as there is no use to know all Reports in the system
    public List<Report> getAllReports() {
        log.info("Started ReportService::getAllReports");
        return reportRepository.findAll();
    }
    */

    /**
     * Handles requests to get/retrieve an existing Report.
     * @param reportId the Id of Report to be retrieved
     * @return the requested Report entity
     */
    public Report getReportById(Integer reportId) {
        log.info("Started ReportService::getReportById. reportId = " + reportId);

        // Gets the reportentity by its ID.
        // select * from report where report_id=?
        Report reportDB = reportRepository.findById(reportId).orElse(null);

        // Create Fault Object to give details on UI
        if (reportDB == null) {
            LmsFault lmsFault = new LmsFault("Resource Not Found", "404",
                    "Report Not Found","/lms/v1/reports/" + reportId);
            return Utils.createFaultyReport(lmsFault);
        }
        log.info("Returning ReportService::getReportById");
        return reportDB;
    }

    /**
     * Handles requests to get/retrieve existing Reports by their dateString pattern
     * @param dateString the dateString pattern (YYYY-MM-DD) of the Reports to be retrieved
     * @return a list of all requested Reports
     */
    public List<Report> getReportsByGeneratedDate(String dateString) {
        log.info("Started ReportService::getReportsByGeneratedDate");

        // Gets the report entity by its dateString pattern.
        // SELECT * FROM REPORT WHERE generated_date LIKE %?1%
        List<Report> reportDBList = reportRepository.findReportsByGeneratedDate(dateString);

        // Create Fault Object to give details on UI
        if (reportDBList == null || reportDBList.isEmpty()) {
            LmsFault lmsFault = new LmsFault("Resource Not Found", "404",
                    "Report Not Found","/lms/v1/reports/date/" + dateString);
            return Utils.createFaultyReportInList(lmsFault);
        }
        log.info("Returning ReportService::getReportsByGeneratedDate");
        return reportDBList;
    }

    /**
     * Handles requests to save a new Report for a ReportInfo.
     * @param reportInfoId the Id of the ReportInfo for which a report is to be saved
     * @return the saved Report entity
     */
    public Report addReportForReportInfo(Integer reportInfoId) {
        log.info("Started ReportService::addReportForReportInfo. reportInfoId = " + reportInfoId);
        Report report = null;
        String reportContentDB = null;

        // Gets the reportinfo entity by its ID.
        // select * from reportinfo where reportinfo_id=?
        ReportInfo reportInfoDB = reportInfoRepository.findById(reportInfoId).orElse(null);

        // Create Fault Object to give details on UI
        if (reportInfoDB == null) {
            LmsFault lmsFault = new LmsFault("Resource Not Found", "404",
                    "ReportInfo Not Found","/lms/v1/reportinfos/" + reportInfoId);
            return Utils.createFaultyReport(lmsFault);
        }

        // Get the JDBC connection and generate the report
        try {
            reportContentDB = jdbcCall(reportInfoId);

        } catch (Exception ex) {
            log.info("Exception in addReportForReportInfo " + ex.getMessage());
            ex.printStackTrace();
            LmsFault lmsFault = new LmsFault("Resource Not Found", "404",
                    "Report Content Not Found", "/lms/v1/reports");
            return Utils.createFaultyReport(lmsFault);
        }

        // Create the Report Object
        report = new Report();
        report.setContent(reportContentDB);
        report.setReportInfo(reportInfoDB);
        report.setGeneratedDate(Utils.convertNowToSQLTS());// today's date
        log.info("Returning ReportService::addReportForReportInfo. reportInfoId = " + reportInfoId);

        // When the report is created, the downlink cannot have the report ID because by this time
        // the DB has not yet assigned any Id to the report. Hence an immediate update also is necessary
        // as part of Report model's @PostPersist
        // insert into report (download_link,generated_date,reportinfo_id) values (?,?,?)
        // update report set download_link=? where report_id=?
        return reportRepository.save(report);
    }

    /**
     * Handles the connection to the Database, executes the query and gives the resultSet in a report format
     * @param reportInfoId the Id of the ReportInfo for which a report is to be saved
     * @return the report content as a String
     */
    public String jdbcCall(Integer reportInfoId) throws Exception {
        log.info("Starting ReportService::jdbc. reportInfoId = " + reportInfoId);
        String infoQuery = "select * from reportinfo where reportinfo_id =" +  reportInfoId; // query to be run
        Class.forName("com.mysql.cj.jdbc.Driver"); // Driver name

        // Using the datasource configured in the application.properties, establish the DB connection
        Connection con = datasource.getConnection();
        log.info("DB Connection Established successfully");

        // Create a statement to be executed on the DB to get the details of the ReportInfo
        PreparedStatement pst = con.prepareStatement(infoQuery);
        ResultSet resultSet = pst.executeQuery(); // Execute query
        resultSet.next(); // Shift the resultSet pointer to the first element in the resultSet (Result Table)

        // Get the preset sql statement from the ReportInfo to be executed for Report Generation
        String sqlStatement = resultSet.getString("sql_statement"); // Retrieve sql to be executed
        pst = con.prepareStatement(sqlStatement);

        // Execute the query on the DB
        resultSet = pst.executeQuery(); // Execute the sql on DB

        // Get metadata (i.e Column count and names) of the query being executed
        final ResultSetMetaData metaData = resultSet.getMetaData();
        final int columnCount = metaData.getColumnCount();

        // Formatted to html report but can be a csv file as well if needed
        StringBuilder stringBuilder = new StringBuilder("<table border=1><tr><th>");
        // Write column names as header line
        for (int i = 1; i <= columnCount; i++) {
            stringBuilder.append(metaData.getColumnName(i));
            if (i < columnCount) {
                //sb.append(",");
                stringBuilder.append("<th>");
            }
        }

        // Write data rows
        while (resultSet.next()) {
            stringBuilder.append("<tr><td>");
            for (int i = 1; i <= columnCount; i++) {
                stringBuilder.append(resultSet.getString(i));
                if (i < columnCount) {
                    stringBuilder.append("<td>");
                }
            }
        }
        //log.info(sb.toString());
        // Make sure you close the Statament, ResultSet and Connection to avoid Memory Leakage
        pst.close(); // close statement
        resultSet.close(); // close resultSet
        con.close(); // close connection

        log.info("Connection Closed....");
        log.info("Returning ReportService::jdbc. reportInfoId = " + reportInfoId);
        // return the final html string
        return stringBuilder.toString();
    }

    /*
    * A report once generated cannot be updated for audit reasons
    * /
    public Report updateReportInfo(Integer reportId, ReportInfo reportDBFromClient) {
        log.info("Started ReportService::updateReportInfo. reportInfoId = " + reportId);
        Report reportDB = reportRepository.findById(reportId).orElse(null);

        // Map all non null values
        reportDB = ReportUtils.nonNullMapper(reportDBFromClient, reportDB);

        return reportRepository.save(reportDB);
    }

    /*
    * A report once generated cannot be deleted for audit reasons
    * /
    public void deleteReportById(Integer reportId) {
        log.info("Started ReportService::deleteReportById. reportId = " + reportId);
        // Deletes the Report entity by its ID.
        reportRepository.deleteById(reportId);
    }
    */
}