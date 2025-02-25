package com.sb.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sb.lms.model.ReportInfo;
import com.sb.lms.service.ReportInfoService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportInfoController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReportInfoControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportInfoService reportInfoService;

    @Autowired
    private ObjectMapper objectMapper;

    ReportInfo reportInfo;

    @BeforeEach
    public void setup() {
        reportInfo = ReportInfo.builder().reportInfoId(1).name("USER_RPT").timeToGenerate("11:30").build();
    }

    @Test
    @DisplayName("Test 1:Verify ReportInfo By Id Retrieval Check")
    @Order(1)
    public void ctrTestGetReportInfoById() throws Exception {
        // arrange
        when(reportInfoService.getReportInfoById(1)).thenReturn(reportInfo);

        // action
        ResultActions response = mockMvc.perform(get("/lms/v1/reportinfos/{reportInfoId}", 1));
        response.andExpect(status().isOk()) // Expect HTTP 200
                .andDo(print())
                .andExpect(jsonPath("$.reportInfoId").value(reportInfo.getReportInfoId()))
                .andExpect(jsonPath("$.name").value(reportInfo.getName()))
                .andExpect(jsonPath("$.timeToGenerate").value(reportInfo.getTimeToGenerate()));
    }

    @Test
    @DisplayName("Test 3:Verify ReportInfo By Name Retrieval Check")
    @Order(3)
    public void ctrTestGetReportInfoByTitle() throws Exception {

        // arrange
        String name = "USERS";
        List<ReportInfo> reportInfoList = new ArrayList<>();
        reportInfoList.add(reportInfo);
        ReportInfo reportInfo2 = ReportInfo.builder().reportInfoId(2).name("BOOKS_RPT").
                timeToGenerate("12:45").build();
        reportInfoList.add(reportInfo2);

        when(reportInfoService.getReportInfoByName(name)).thenReturn(reportInfoList);

        // action
        ResultActions response = mockMvc.perform(get("/lms/v1/reportinfos/name/{name}", name)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportInfo)));

        // verify the output
        response.andExpect(status().isOk()) // Expect HTTP 200
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(reportInfoList.size())))
                .andExpect(jsonPath("$[0].reportInfoId").value(reportInfo.getReportInfoId()))
                .andExpect(jsonPath("$[0].name").value(reportInfo.getName()))
                .andExpect(jsonPath("$[0].timeToGenerate").value(reportInfo.getTimeToGenerate()))
                .andExpect(jsonPath("$[1].reportInfoId").value(reportInfo2.getReportInfoId()))
                .andExpect(jsonPath("$[1].name").value(reportInfo2.getName()))
                .andExpect(jsonPath("$[1].timeToGenerate").value(reportInfo2.getTimeToGenerate()))  ;
    }

    //Post Controller
    @Test
    @DisplayName("Test 5:Verify Add ReportInfo Check")
    @Order(5)
    public void ctrTestAddReportInfoSuccess() throws Exception {
        // arrange
        when(reportInfoService.addReportInfo(any(ReportInfo.class))).thenReturn(reportInfo);

        // action
        ResultActions response = mockMvc.perform(post("/lms/v1/reportinfos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportInfo)));

        // verify
        response.andDo(print()).
                andExpect(status().isCreated()) // Expect HTTP 201
                .andExpect(jsonPath("$.reportInfoId").value(reportInfo.getReportInfoId()))
                .andExpect(jsonPath("$.name").value(reportInfo.getName()))
                .andExpect(jsonPath("$.timeToGenerate").value(reportInfo.getTimeToGenerate()));
    }

    //Update employee
    @Test
    @DisplayName("Test 6:Verify Update ReportInfo Check")
    @Order(6)
    public void ctrTestUpdateReportInfoSuccess() throws Exception{
        // arrange
        reportInfo.setTimeToGenerate("00:45");

        when(reportInfoService.updateReportInfo(1,reportInfo)).thenReturn(reportInfo);

        // action
        ResultActions response = mockMvc.perform(put("/lms/v1/reportinfos/{reportInfoId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportInfo)));

        // verify
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.timeToGenerate").value(reportInfo.getTimeToGenerate()));
    }

    @Test
    @DisplayName("Test 7:Verify Delete ReportInfo Check")
    @Order(7)
    public void ctrTestDeleteReportInfoById() throws Exception {
        // arrange
        when(reportInfoService.deleteReportInfoById(1)).thenReturn("success:ReportInfo");

        // Act: Perform the DELETE request
        ResultActions response = mockMvc.perform(delete("/lms/v1/reportinfos/{reportInfoId}",
                reportInfo.getReportInfoId()));
        response.andExpect(content().string(containsString("success")));

    }

    @Test
    @DisplayName("Test 8:Verify Delete ReportInfo Error Check")
    @Order(8)
    public void ctrTestDeleteReportInfoByIdError() throws Exception {
        // arrange
        when(reportInfoService.deleteReportInfoById(1)).thenReturn("error:ReportInfo");

        // Act: Perform the DELETE request
        ResultActions response = mockMvc.perform(delete("/lms/v1/reportinfos/{reportInfoId}",
                reportInfo.getReportInfoId()));
        response.andExpect(content().string(containsString("error")));

    }
}