package com.sb.lms.utils;

import com.sb.lms.model.Book;
import com.sb.lms.model.LmsFault;
import com.sb.lms.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UtilsUnitTests {

    @Test
    @DisplayName("Test 1:Verify Null String Check")
    @Order(1)
    public void stringNullTest(){
        String str = null;
        //Verify
        Assertions.assertThat(Utils.isStringBlank(str)).isTrue();
    }

    @Test
    @DisplayName("Test 2:Verify Blank String Check")
    @Order(2)
    public void stringBlankTest(){
        String str = "";
        //Verify
        Assertions.assertThat(Utils.isStringBlank(str)).isTrue();
    }

    @Test
    @DisplayName("Test 3:Verify Just Blank Spaces String Check")
    @Order(2)
    public void stringBlankSpacesTest(){
        String str = "    ";
        //Verify
        Assertions.assertThat(Utils.isStringBlank(str)).isTrue();
    }

    @Test
    @DisplayName("Test 4:Verify Not Null and not Blank String Check")
    @Order(4)
    public void stringNotNullNotBlankTest(){
        String str = "LMS";
        //Verify
        Assertions.assertThat(Utils.isStringBlank(str)).isFalse();
    }

    @Test
    @DisplayName("Test 5:Verify Timstamp close to Current Time Check")
    @Order(5)
    public void nowTimeTest(){
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp1 = Timestamp.valueOf(now);
        Timestamp timestamp2 = Utils.convertNowToSQLTS();

        // Convert Timestamps to Instant
        Duration duration = Duration.between(timestamp1.toInstant(), timestamp2.toInstant());

        //Verify
        // Assert that the difference is within 1 second
        Assertions.assertThat(duration.abs().getSeconds()).isLessThanOrEqualTo(1);

    }

    @Test
    @DisplayName("Test 6:Verify Timstamp close to Current Time's Year Month Date Check")
    @Order(6)
    public void nowTimeYearMonthDateTest(){
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp1 = Timestamp.valueOf(now);
        Timestamp timestamp2 = Utils.convertNowToSQLTS();

        //Verify
        // Assert that the year, month, and day of both timestamps are equal
        Assertions.assertThat(timestamp1.toLocalDateTime().getYear())
                .isEqualTo(timestamp2.toLocalDateTime().getYear());
        Assertions.assertThat(timestamp1.toLocalDateTime().getMonthValue())
                .isEqualTo(timestamp2.toLocalDateTime().getMonthValue());
        Assertions.assertThat(timestamp1.toLocalDateTime().getDayOfMonth())
                .isEqualTo(timestamp2.toLocalDateTime().getDayOfMonth());

    }

    @Test
    @DisplayName("Test 7:Verify Faulty User Object Check")
    @Order(7)
    public void faultyUserTest(){
        final String USER_ADDRESS_NOT_FOUND = "Invalid Address reference provided to User";

        LmsFault lmsFault = new LmsFault("Resource Not Found", "404", USER_ADDRESS_NOT_FOUND,"/lms/v1/users");
        User userObject = Utils.createFaultyUser(lmsFault);

        //Verify
        Assertions.assertThat(userObject).isInstanceOf(User.class);
        Assertions.assertThat(userObject.getFault()).isNotNull();
        Assertions.assertThat(userObject.getFault().getHttp()).isEqualTo("Resource Not Found");
        Assertions.assertThat(userObject.getFault().getCode()).isEqualTo("404");
        Assertions.assertThat(userObject.getFault().getMessage()).isEqualTo(USER_ADDRESS_NOT_FOUND);
        Assertions.assertThat(userObject.getFault().getPath()).isEqualTo("/lms/v1/users");

        Assertions.assertThat(userObject.getFirstName()).isNull();
        Assertions.assertThat(userObject.getLastName()).isNull();
        }

    @Test
    @DisplayName("Test 8:Verify Just Blank Spaces String Check")
    @Order(8)
    public void faultyUserInListTest(){

        List<User> userList = new ArrayList<User>();

        //Verify
        Assertions.assertThat(userList).isEmpty();

        LmsFault lmsFault = new LmsFault("Resource Not Found", "404", "User Not Found","/lms/v1/users/name/churchill");
        User userObject = Utils.createFaultyUser(lmsFault);

        userList = Utils.createFaultyUserInList(lmsFault);
        User userInsideList = userList.get(0);

        Assertions.assertThat(userList).isNotNull();
        Assertions.assertThat(userList).isNotEmpty();
        Assertions.assertThat(userList.size()).isEqualTo(1);

        //Verify
        Assertions.assertThat(userList).isInstanceOf(ArrayList.class);
        Assertions.assertThat(userList).isNotInstanceOf(Map.class);
        Assertions.assertThat(userInsideList).isInstanceOf(User.class);
        Assertions.assertThat(userInsideList).isNotInstanceOf(Book.class);

        Assertions.assertThat(userInsideList.getFault()).isNotNull();
        Assertions.assertThat(userInsideList.getFault().getHttp()).isEqualTo("Resource Not Found");
        Assertions.assertThat(userInsideList.getFault().getCode()).isEqualTo("404");
        Assertions.assertThat(userInsideList.getFault().getMessage()).isEqualTo("User Not Found");
        Assertions.assertThat(userInsideList.getFault().getPath()).isEqualTo("/lms/v1/users/name/churchill");

        Assertions.assertThat(userInsideList.getFirstName()).isNull();
        Assertions.assertThat(userInsideList.getLastName()).isNull();
    }
}