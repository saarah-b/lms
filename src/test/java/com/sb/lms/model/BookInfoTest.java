package com.sb.lms.model;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class BookInfoTest {

    BookInfo bookInfo;

    @BeforeEach
    public void setup() {
        bookInfo = BookInfo.builder().bookInfoId(1).title("The Jungle Book").totalQuantity(2).build();
    }

    @Test
    @DisplayName("Test 1:Verify Increment count Check")
    @Order(1)
    public void mdlTestIncrementCount() {
        Assertions.assertThat(bookInfo.getTotalQuantity()).isEqualTo(2);

        bookInfo.incrementTotalQuantity();
        Assertions.assertThat(bookInfo.getTotalQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("Test 2:Verify Decrement count Check")
    @Order(2)
    public void mdlTestDecrementCount() {
        Assertions.assertThat(bookInfo.getTotalQuantity()).isEqualTo(2);

        bookInfo.decrementTotalQuantity();
        Assertions.assertThat(bookInfo.getTotalQuantity()).isEqualTo(1);
    }
}