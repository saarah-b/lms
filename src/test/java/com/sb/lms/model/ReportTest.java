package com.sb.lms.model;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.SimpleDateFormat;
import java.sql.Timestamp;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class ReportTest {

    private Report reportSpy;

    @BeforeEach
    public void setup() throws Exception {
        reportSpy = Mockito.spy(new Report());
    }

    @Test
    @DisplayName("Test 1:Verify BookInfo By Id Retrieval Check")
    @Order(1)
    public void repTestGetBookInfoById() throws Exception {

        // Mock necessary values
        ReportInfo reportInfoMock = mock(ReportInfo.class);

        when(reportInfoMock.getReportInfoId()).thenReturn(12);
        when(reportInfoMock.getName()).thenReturn("USERS_RPT");

        Timestamp generatedDate = Timestamp.valueOf("2025-03-03 10:10:00");
        // Set dependencies
        reportSpy.setReportId(4);
        reportSpy.setReportInfo(reportInfoMock);
        reportSpy.setGeneratedDate(generatedDate);
        reportSpy.setContent("Dummy Report Content");

        // Call the method
        reportSpy.onPrePersist();

        // Validate expected result
        String strGeneratedDate = new SimpleDateFormat("yyyyMMdd").format(reportSpy.getGeneratedDate());
        String generatedDownloadLink = "../../lms/report_files/12_USERS_RPT_4_" + strGeneratedDate + ".html";

        Assertions.assertThat(generatedDownloadLink).isEqualTo(reportSpy.getDownloadLink());
    }
}