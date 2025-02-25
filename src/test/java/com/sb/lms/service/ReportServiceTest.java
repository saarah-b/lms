package com.sb.lms.service;

import com.sb.lms.model.LmsFault;
import com.sb.lms.model.Report;
import com.sb.lms.model.ReportInfo;
import com.sb.lms.model.User;
import com.sb.lms.repository.ReportInfoRepository;
import com.sb.lms.repository.ReportRepository;
import com.sb.lms.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.sql.*;

import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j


public class ReportServiceTest {
    @Captor
    ArgumentCaptor<Report> reportCaptor;

    @Mock
    private ReportInfoRepository reportInfoRepository;

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportService reportService;

    @Mock
    private DataSource ds;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @Mock
    private ResultSetMetaData mockResultSetMetaData;

    Report report;
    ReportInfo reportInfo;

    @BeforeEach
    public void setup() throws Exception {
        reportInfo = ReportInfo.builder().reportInfoId(1).name("USERS_RPT").timeToGenerate("11:30").build();
        report = Report.builder().reportId(1).
                downloadLink("\\lms\\report_files\\1.html").reportInfo(reportInfo).build();
    }

    @Test
    @DisplayName("Test 1:Verify Report By Id Retrieval Check")
    @Order(1)
    public void srvTestGetReportById() {

        when(reportRepository.findById(report.getReportId())).thenReturn(Optional.of(report));

        Report reportDB = reportService.getReportById(report.getReportId());

        Assertions.assertThat(reportDB).isNotNull();
        Assertions.assertThat(reportDB).isInstanceOf(Report.class);
        Assertions.assertThat(reportDB).isNotInstanceOf(ReportInfo.class);
        Assertions.assertThat(report.getDownloadLink()).isEqualTo(reportDB.getDownloadLink());
        Assertions.assertThat(report.getGeneratedDate()).isEqualTo(reportDB.getGeneratedDate());
        Assertions.assertThat(reportDB.getFault()).isNull();
        verify(reportRepository, times(1)).findById(report.getReportId());

    }

    @Test
    @DisplayName("Test 2:Verify Report Fault By Id Retrieval Check")
    @Order(2)
    public void srvTestGetReportByIdFault() {

        LmsFault lmsFault = new LmsFault("Resource Not Found", "404", "Report Not Found","/lms/v1/reports/9999");
        Report faultyReportInfo = Utils.createFaultyReport(lmsFault);

        Report reportDB = reportService.getReportById(9999);

        //Verify
        Assertions.assertThat(reportDB).isNotNull();
        Assertions.assertThat(reportDB).isInstanceOf(Report.class);
        Assertions.assertThat(reportDB.getFault()).isNotNull();
        Assertions.assertThat(reportDB.getFault().getHttp()).isEqualTo(faultyReportInfo.getFault().getHttp());
        Assertions.assertThat(reportDB.getFault().getCode()).isEqualTo(faultyReportInfo.getFault().getCode());
        Assertions.assertThat(reportDB.getFault().getMessage()).isEqualTo(faultyReportInfo.getFault().getMessage());
        Assertions.assertThat(reportDB.getFault().getPath()).isEqualTo(faultyReportInfo.getFault().getPath());
        Assertions.assertThat(reportDB.getFault()).isNotNull();
        Assertions.assertThat(reportDB.getDownloadLink()).isNull();
        Assertions.assertThat(reportDB.getGeneratedDate()).isNull();
    }

    @Test
    @DisplayName("Test 3:Verify Report By Generated Date Retrieval Check")
    @Order(3)
    public void srvTestGetReportByGeneratedDate() {
        String date = "2025-02-20";
        List<Report> reportList = new ArrayList<>();
        reportList.add(report);
        Report report2 = Report.builder().reportId(2).
                downloadLink("\\lms\\report_files\\2.html").reportInfo(reportInfo).build(); // .generatedDate("2025-02-20")
        reportList.add(report2);

        when(reportRepository.findReportsByGeneratedDate(date)).thenReturn(reportList);

        List<Report> reportDBList = reportService.getReportsByGeneratedDate(date);
        Report reportInsideList = reportDBList.get(0);

        //Verify
        Assertions.assertThat(reportDBList).isInstanceOf(ArrayList.class);
        Assertions.assertThat(reportDBList).isNotInstanceOf(Map.class);
        Assertions.assertThat(reportInsideList).isInstanceOf(Report.class);
        Assertions.assertThat(reportInsideList).isNotInstanceOf(ReportInfo.class);
        Assertions.assertThat(reportInsideList.getFault()).isNull();
        Assertions.assertThat(report.getDownloadLink()).isEqualTo(reportDBList.get(0).getDownloadLink());
        Assertions.assertThat(report.getGeneratedDate()).isEqualTo(reportDBList.get(0).getGeneratedDate());
        Assertions.assertThat(report2.getDownloadLink()).isEqualTo(reportDBList.get(1).getDownloadLink());
        Assertions.assertThat(report2.getGeneratedDate()).isEqualTo(reportDBList.get(1).getGeneratedDate());
        Assertions.assertThat(reportDBList.size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Test 4:Verify ReportInfo Fault By Name Retrieval Check")
    @Order(4)
    public void srvTestGetReportInfoByGeneratedDateFault() {
        String date = "2025-02-20";
        LmsFault lmsFault = new LmsFault("Resource Not Found", "404",
                "Report Not Found","/lms/v1/reports/date/" + date);
        List<Report> faultyReportList = Utils.createFaultyReportInList(lmsFault);

        when(reportRepository.findReportsByGeneratedDate(date)).thenReturn(faultyReportList);

        List<Report> reportDBList = reportService.getReportsByGeneratedDate(date);
        Report reportDBInsideList = reportDBList.get(0);

        //Verify
        Assertions.assertThat(reportDBList).isNotNull();
        Assertions.assertThat(reportDBList.size()).isEqualTo(1);
        Assertions.assertThat(reportDBInsideList).isInstanceOf(Report.class);
        Assertions.assertThat(reportDBInsideList.getFault()).isNotNull();
        Assertions.assertThat(reportDBInsideList.getFault().getHttp()).isEqualTo(lmsFault.getHttp());
        Assertions.assertThat(reportDBInsideList.getFault().getCode()).isEqualTo(lmsFault.getCode());
        Assertions.assertThat(reportDBInsideList.getFault().getMessage()).isEqualTo(lmsFault.getMessage());
        Assertions.assertThat(reportDBInsideList.getFault().getPath()).isEqualTo(lmsFault.getPath());
        Assertions.assertThat(reportDBInsideList.getFault()).isNotNull();
        Assertions.assertThat(reportDBInsideList.getDownloadLink()).isNull();
        Assertions.assertThat(reportDBInsideList.getGeneratedDate()).isNull();
    }

    @Test
    @DisplayName("Test 5:Verify Add Report For ReportInfo Check")
    @Order(5)
    public void srvTestAddReportForReportInfoSuccess() throws SQLException {
        specialAddSetup();

        // Mock repository behavior
        when(reportInfoRepository.findById(report.getReportInfo().getReportInfoId())).thenReturn(Optional.of(reportInfo));
        when(reportRepository.save(Mockito.any(Report.class))).thenReturn(report);

        // Act
        Report reportDB = reportService.addReportForReportInfo(report.getReportInfo().getReportInfoId());

        // Verify
        Assertions.assertThat(reportDB).isNotNull();
        Assertions.assertThat(reportDB).isInstanceOf(Report.class);
        Assertions.assertThat(reportDB.getReportId()).isEqualTo(report.getReportId());
        Assertions.assertThat(reportDB.getDownloadLink()).isEqualTo(report.getDownloadLink());
        verify(reportRepository, times(1)).save(reportCaptor.capture());
    }

    private void specialAddSetup() throws SQLException {
        // Mock DataSource to return a mock Connection
        when(ds.getConnection()).thenReturn(mockConnection);

        // Mock Connection to return a mock PreparedStatement
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Mock PreparedStatement to return a mock ResultSet
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Mock ResultSet to return expected values
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.next()).thenReturn(true)  // 1st row
                .thenReturn(true)  // 2nd row
                .thenReturn(false); // End of results
        when(mockResultSet.getString("sql_statement")).thenReturn("select * from book where available=1");

        // Mock the ResultSetMetaData
        when(mockResultSet.getMetaData()).thenReturn(mockResultSetMetaData);

        // Mock the ResultSetMetaData to return the number of columns (e.g., 1 column)
        when(mockResultSetMetaData.getColumnCount()).thenReturn(5);

        // Mock that the ResultSetMetaData returns the column name
        when(mockResultSetMetaData.getColumnName(3)).thenReturn("download_link");
    }
}