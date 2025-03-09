package com.sb.lms.model;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class TransactionTest {

    @Mock
    Transaction transaction;
    User user;
    Book book;
    BookInfo bookInfo;

    @BeforeEach
    public void setup() {
        user = User.builder().userId(1).firstName("Saarah").lastName("Bedekar")
                .email("a.b@c.com").mobileNumber("07894444555").build();
        book = Book.builder().bookId(1).shelfReference("Shelf1").location("Whitechapel")
                .edition("2.13").available(true).bookInfo(bookInfo).build();
    }

    @Test
    @DisplayName("Test 1:Verify Issue Book Check")
    @Order(1)
    public void mdlTestIssueBook() {

        //when(transaction.issueBook(user,book)).thenReturn(Optional.of(bookInfo));
        doNothing().when(transaction).issueBook(user, book);
        transaction.issueBook(user, book);

        verify(transaction, times(1)).issueBook(user, book);
    }
}