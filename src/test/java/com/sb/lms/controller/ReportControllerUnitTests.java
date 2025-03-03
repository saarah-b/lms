package com.sb.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sb.lms.model.Book;
import com.sb.lms.model.BookInfo;
import com.sb.lms.model.Report;
import com.sb.lms.model.ReportInfo;
import com.sb.lms.service.ReportService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReportControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @Autowired
    private ObjectMapper objectMapper;

    Report report;
    ReportInfo reportInfo;

    @BeforeEach
    public void setup() throws ParseException {
        reportInfo = ReportInfo.builder().reportInfoId(1).name("USERS_RPT").timeToGenerate("11:30").build();
        report = Report.builder().reportId(1).
                downloadLink("\\lms\\report_files\\1.html").reportInfo(reportInfo).build();
    }

    @Test
    @DisplayName("Test 1:Verify Report By Id Retrieval Check")
    @Order(1)
    public void ctrTestGetReportById() throws Exception {
        // arrange
        when(reportService.getReportById(1)).thenReturn(report);

        // action
        ResultActions response = mockMvc.perform(get("/lms/v1/reports/{reportId}", 1));
        response.andExpect(status().isOk()) // Expect HTTP 200
                .andDo(print())
                .andExpect(jsonPath("$.reportId").value(report.getReportId()))
                .andExpect(jsonPath("$.downloadLink").value(report.getDownloadLink()))
                .andExpect(jsonPath("$.reportInfo.reportInfoId").value(report.getReportInfo().getReportInfoId()));
    }

    @Test
    @DisplayName("Test 2:Verify Report By Generated Date Retrieval Check")
    @Order(2)
    public void ctrTestGetReportByGeneratedDate() throws Exception {
        String date = "2025-02-20";
        List<Report> reportList = new ArrayList<>();
        reportList.add(report);
        Report report2 = Report.builder().reportId(3).
                downloadLink("\\lms\\report_files\\3.html").reportInfo(reportInfo).build();
        reportList.add(report2);

        // arrange
        when(reportService.getReportsByGeneratedDate(date)).thenReturn(reportList);

        // action
        ResultActions response = mockMvc.perform(get("/lms/v1/reports/date/{date}", date));
        response.andExpect(status().isOk()) // Expect HTTP 200
                .andDo(print())
                .andExpect(jsonPath("$[0].reportId").value(report.getReportId()))
                .andExpect(jsonPath("$[0].downloadLink").value(report.getDownloadLink()))
                .andExpect(jsonPath("$[0].reportInfo.reportInfoId").value(report.getReportInfo().getReportInfoId()))
                .andExpect(jsonPath("$[1].reportId").value(report2.getReportId()))
                .andExpect(jsonPath("$[1].downloadLink").value(report2.getDownloadLink()))
                .andExpect(jsonPath("$[1].reportInfo.reportInfoId").value(report2.getReportInfo().getReportInfoId()));
    }

    //Post Controller
    @Test
    @DisplayName("Test 5:Verify Add Report Check")
    @Order(5)
    public void ctrTestAddReportSuccess() throws Exception {
        // arrange
        when(reportService.addReportForReportInfo(report.getReportInfo().getReportInfoId())).thenReturn(report);

        // action
        ResultActions response = mockMvc.perform(post("/lms/v1/reports/reportinfo/{reportInfoId}",1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(report)));

        // verify
        response.andDo(print()).
            andExpect(status().isCreated()) // Expect HTTP 201
            .andExpect(jsonPath("$.reportId").value(report.getReportId()))
            .andExpect(jsonPath("$.downloadLink").value(report.getDownloadLink()))
            .andExpect(jsonPath("$.reportInfo.reportInfoId").value(report.getReportInfo().getReportInfoId()));
    }
}