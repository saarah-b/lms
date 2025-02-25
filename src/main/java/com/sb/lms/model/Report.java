package com.sb.lms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * The Data / Model / Value Object to store Report Entity
 * @author Saarah Bedekar
 */

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "Report")
@Data // Generates getters, setters, toString, equals, and hashCode methods.
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Report {
    @Id
    @Column(name = "report_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reportId;

    @Column(name = "generated_date")
    private Timestamp generatedDate;

    @Column(name = "download_link")
    private String downloadLink;

    @ManyToOne
    @JoinColumn(name = "reportinfo_id")
    private ReportInfo reportInfo;

    @Transient
    private String content;

    @Transient
    private LmsFault fault;


    // Method to save file and set downloadLink just before persisting
    @PostPersist
    public void onPrePersist() throws Exception {
        log.info("Started Report::onPrePersist. reportId = " + reportId);
        // Set the idAsString field to the string value of the auto-increment ID
        if (reportId != null) {
            //log.info("reportId = " + reportId + ", toString = " + this.toString());
            String formattedDate = new SimpleDateFormat("yyyyMMdd").format(generatedDate);
            String fileName = "src/main/resources/static/lms/report_files/";
            downloadLink = reportInfo.getReportInfoId() + "_" + reportInfo.getName() + "_" + reportId + "_" + formattedDate + ".html";
            FileWriter csvWriter = new FileWriter(fileName + downloadLink);
            csvWriter.write(content);
            csvWriter.close();
            downloadLink = "../../lms/report_files/" + downloadLink;
            log.info("File Download link created for " + downloadLink);
        }
        log.info("Returning Report::onPrePersist. reportId = " + reportId);
    }
}