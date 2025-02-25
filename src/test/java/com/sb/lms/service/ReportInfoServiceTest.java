package com.sb.lms.service;

import com.sb.lms.model.*;
import com.sb.lms.repository.ReportInfoRepository;
import com.sb.lms.repository.ReportRepository;
import com.sb.lms.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class ReportInfoServiceTest {

    @Mock
    private ReportInfoRepository reportInfoRepository;

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportInfoService reportInfoService;

    ReportInfo reportInfo;

    @BeforeEach
    public void setup() {
        reportInfo = ReportInfo.builder().reportInfoId(1)
                .name("SCH_All_Books_With_Title").timeToGenerate("23:30").build();
    }

    @Test
    @DisplayName("Test 1:Verify ReportInfo By Id Retrieval Check")
    @Order(1)
    public void srvTestGetReportInfoById() {

        when(reportInfoRepository.findById(reportInfo.getReportInfoId())).thenReturn(Optional.of(reportInfo));

        ReportInfo reportInfoDB = reportInfoService.getReportInfoById(reportInfo.getReportInfoId());

        Assertions.assertThat(reportInfoDB).isNotNull();
        Assertions.assertThat(reportInfoDB).isInstanceOf(ReportInfo.class);
        Assertions.assertThat(reportInfoDB).isNotInstanceOf(Report.class);
        Assertions.assertThat(reportInfo.getName()).isEqualTo(reportInfoDB.getName());
        Assertions.assertThat(reportInfo.getTimeToGenerate()).isEqualTo(reportInfoDB.getTimeToGenerate());
        Assertions.assertThat(reportInfoDB.getFault()).isNull();
        verify(reportInfoRepository, times(1)).findById(reportInfo.getReportInfoId());

    }

    @Test
    @DisplayName("Test 2:Verify ReportInfo Fault By Id Retrieval Check")
    @Order(2)
    public void srvTestGetReportInfoByIdFault() {

        LmsFault lmsFault = new LmsFault("Resource Not Found", "404", "ReportInfo Not Found","/lms/v1/reportinfos/9999");
        ReportInfo faultyReportInfo = Utils.createFaultyReportInfo(lmsFault);

        ReportInfo reportInfoDB = reportInfoService.getReportInfoById(9999);

        //Verify
        Assertions.assertThat(reportInfoDB).isNotNull();
        Assertions.assertThat(reportInfoDB).isInstanceOf(ReportInfo.class);
        Assertions.assertThat(reportInfoDB.getFault()).isNotNull();
        Assertions.assertThat(reportInfoDB.getFault().getHttp()).isEqualTo(faultyReportInfo.getFault().getHttp());
        Assertions.assertThat(reportInfoDB.getFault().getCode()).isEqualTo(faultyReportInfo.getFault().getCode());
        Assertions.assertThat(reportInfoDB.getFault().getMessage()).isEqualTo(faultyReportInfo.getFault().getMessage());
        Assertions.assertThat(reportInfoDB.getFault().getPath()).isEqualTo(faultyReportInfo.getFault().getPath());
        Assertions.assertThat(reportInfoDB.getFault()).isNotNull();
        Assertions.assertThat(reportInfoDB.getName()).isNull();
        Assertions.assertThat(reportInfoDB.getTimeToGenerate()).isNull();
    }

    @Test
    @DisplayName("Test 3:Verify ReportInfo By Title Retrieval Check")
    @Order(3)
    public void srvTestGetReportInfoByName() {
        String name = "SCH";
        List<ReportInfo> reportInfoList = new ArrayList<>();
        reportInfoList.add(reportInfo);
        ReportInfo reportInfo2 = ReportInfo.builder().reportInfoId(1)
                .name("SCH_ALL_BOOKS").timeToGenerate("10:30").build();
        reportInfoList.add(reportInfo2);

        when(reportInfoRepository.findReportInfoByName(name)).thenReturn(reportInfoList);

        List<ReportInfo> reportInfoDBList = reportInfoService.getReportInfoByName(name);
        ReportInfo reportInfoInsideList = reportInfoDBList.get(0);

        //Verify
        Assertions.assertThat(reportInfoDBList).isInstanceOf(ArrayList.class);
        Assertions.assertThat(reportInfoDBList).isNotInstanceOf(Map.class);
        Assertions.assertThat(reportInfoInsideList).isInstanceOf(ReportInfo.class);
        Assertions.assertThat(reportInfoInsideList).isNotInstanceOf(Report.class);
        Assertions.assertThat(reportInfoInsideList.getFault()).isNull();
        Assertions.assertThat(reportInfo.getName()).isEqualTo(reportInfoDBList.get(0).getName());
        Assertions.assertThat(reportInfo.getTimeToGenerate()).isEqualTo(reportInfoDBList.get(0).getTimeToGenerate());
        Assertions.assertThat(reportInfo2.getName()).isEqualTo(reportInfoDBList.get(1).getName());
        Assertions.assertThat(reportInfo2.getTimeToGenerate()).isEqualTo(reportInfoDBList.get(1).getTimeToGenerate());
        Assertions.assertThat(reportInfoDBList.size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Test 4:Verify ReportInfo Fault By Name Retrieval Check")
    @Order(4)
    public void srvTestGetReportInfoByNameFault() {
        String name = "xyz";
        LmsFault lmsFault = new LmsFault("Resource Not Found", "404",
                "ReportInfo Not Found","/lms/v1/reportinfos/name/" + name);
        List<ReportInfo> faultyReportInfoList = Utils.createFaultyReportInfoInList(lmsFault);

        when(reportInfoRepository.findReportInfoByName(name)).thenReturn(faultyReportInfoList);

        List<ReportInfo> reportInfoDBList = reportInfoService.getReportInfoByName(name);
        ReportInfo reportInfoDBInsideList = reportInfoDBList.get(0);

        //Verify
        Assertions.assertThat(reportInfoDBList).isNotNull();
        Assertions.assertThat(reportInfoDBList.size()).isEqualTo(1);
        Assertions.assertThat(reportInfoDBInsideList).isInstanceOf(ReportInfo.class);
        Assertions.assertThat(reportInfoDBInsideList.getFault()).isNotNull();
        Assertions.assertThat(reportInfoDBInsideList.getFault().getHttp()).isEqualTo(lmsFault.getHttp());
        Assertions.assertThat(reportInfoDBInsideList.getFault().getCode()).isEqualTo(lmsFault.getCode());
        Assertions.assertThat(reportInfoDBInsideList.getFault().getMessage()).isEqualTo(lmsFault.getMessage());
        Assertions.assertThat(reportInfoDBInsideList.getFault().getPath()).isEqualTo(lmsFault.getPath());
        Assertions.assertThat(reportInfoDBInsideList.getFault()).isNotNull();
        Assertions.assertThat(reportInfoDBInsideList.getName()).isNull();
        Assertions.assertThat(reportInfoDBInsideList.getTimeToGenerate()).isNull();
    }

    @Test
    @DisplayName("Test 5:Verify Add ReportInfo Check")
    @Order(5)
    public void srvTestAddReportInfoSuccess() {

        when(reportInfoRepository.save(Mockito.any(ReportInfo.class))).thenReturn(reportInfo);

        // Act
        ReportInfo reportInfoDB = reportInfoService.addReportInfo(reportInfo);

        // Assert
        Assertions.assertThat(reportInfoDB).isNotNull(); // Assert the ReportInfo returned is not null
        Assertions.assertThat(reportInfoDB).isInstanceOf(ReportInfo.class); // Assert the method returns ReportInfo instance
        Assertions.assertThat(reportInfoDB.getName()).isEqualTo(reportInfo.getName());
        Assertions.assertThat(reportInfoDB.getTimeToGenerate()).isEqualTo(reportInfo.getTimeToGenerate());

        // Verify that save was called
        verify(reportInfoRepository, times(1)).save(reportInfo);
    }

    @Test
    @DisplayName("Test 6:Verify Update ReportInfo Check")
    @Order(6)
    public void srvTestUpdateReportInfoSuccess() {
        // Arrange
        ReportInfo reportInfo2 = ReportInfo.builder().timeToGenerate("00:45").build();

        when(reportInfoRepository.save(Mockito.any(ReportInfo.class))).thenReturn(reportInfo2);
        when(reportInfoRepository.findById(reportInfo.getReportInfoId())).thenReturn(Optional.of(reportInfo2));

        // Act
        ReportInfo reportInfoDB = reportInfoService.updateReportInfo(reportInfo.getReportInfoId(), reportInfo2);

        // Assert
        Assertions.assertThat(reportInfoDB).isNotNull(); // Assert the ReportInfo returned is not null
        Assertions.assertThat(reportInfoDB).isInstanceOf(ReportInfo.class); // Assert the method returns ReportInfo instance
        Assertions.assertThat(reportInfoDB.getName()).isEqualTo(reportInfo2.getName());
        Assertions.assertThat(reportInfoDB.getTimeToGenerate()).isEqualTo(reportInfo2.getTimeToGenerate());

        // Verify that save was called
        verify(reportInfoRepository, times(1)).save(reportInfo2);
    }

    @Test
    @DisplayName("Test 7:Verify Delete ReportInfo Success Check")
    @Order(7)
    public void srvTestDeleteReportInfoByIdSuccess() {
        when(reportRepository.findTotalReportCountsByReportInfo(reportInfo.getReportInfoId())).thenReturn(0);
        reportInfoService.deleteReportInfoById(reportInfo.getReportInfoId());

        verify(reportInfoRepository, times(1)).deleteById(reportInfo.getReportInfoId());
    }

    @Test
    @DisplayName("Test 8:Verify Delete ReportInfo Error Check")
    @Order(8)
    public void srvTestDeleteReportInfoByIdError() {
        when(reportRepository.findTotalReportCountsByReportInfo(reportInfo.getReportInfoId())).thenReturn(1);
        reportInfoService.deleteReportInfoById(reportInfo.getReportInfoId());

        verify(reportInfoRepository, times(0)).deleteById(reportInfo.getReportInfoId());
    }
}