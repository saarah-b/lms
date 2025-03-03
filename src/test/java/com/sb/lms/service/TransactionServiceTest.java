package com.sb.lms.service;

import com.sb.lms.model.*;
import com.sb.lms.repository.AddressRepository;
import com.sb.lms.repository.BookRepository;
import com.sb.lms.repository.TransactionRepository;
import com.sb.lms.repository.UserRepository;
import com.sb.lms.security.BCryptPasswordHasher;
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

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
public class TransactionServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    Transaction transaction;
    User user;
    Book book;
    BookInfo bookInfo;

    @BeforeEach
    public void setup() {
        user = User.builder().userId(1).firstName("Saarah").lastName("Bedekar")
                .email("a.b@c.com").mobileNumber("07894444555").build();
        bookInfo = BookInfo.builder().bookInfoId(1).title("The Jungle Book").price(2.99).totalQuantity(2).build();
        book = Book.builder().bookId(1).shelfReference("Shelf1").location("Whitechapel")
                .edition("2.13").available(true).bookInfo(bookInfo).build();
        transaction = Transaction.builder().transactionId(1).user(user).book(book).build();
    }

    @Test
    @DisplayName("Test 1:Verify Transaction By Id Retrieval Check")
    @Order(1)
    public void srvTestGetTransactionById() {

        transaction.setIssueDate(Utils.convertStringToTs("2025-02-19"));
        transaction.setReturnDate(Utils.generateReturnSQLTS(transaction.getIssueDate()));
        transaction.setReturned(false);

        when(transactionRepository.findById(transaction.getTransactionId())).thenReturn(Optional.of(transaction));

        Transaction transactionDB = transactionService.getTransactionById(transaction.getTransactionId());

        Assertions.assertThat(transactionDB).isNotNull(); // Assert the User returned is not null
        Assertions.assertThat(transactionDB).isInstanceOf(Transaction.class); // Assert the method returns User instance
        Assertions.assertThat(transactionDB).isNotInstanceOf(Book.class);
        Assertions.assertThat(Utils.convertTsToString(transactionDB.getIssueDate()))
                .isEqualTo(Utils.convertTsToString(transaction.getIssueDate()));
        Assertions.assertThat(Utils.convertTsToString(transactionDB.getReturnDate()))
                .isEqualTo(Utils.convertTsToString(transaction.getReturnDate()));
        Assertions.assertThat(transactionDB.getTransactionId()).isEqualTo(transaction.getTransactionId());
        Assertions.assertThat(transactionDB.getFault()).isNull();
        verify(transactionRepository, times(1)).findById(transaction.getTransactionId());

    }

    @Test
    @DisplayName("Test 2:Verify Transaction Fault By Id Retrieval Check")
    @Order(2)
    public void srvTestGetTransactionByIdFault() {

        LmsFault lmsFault = new LmsFault("Resource Not Found", "404", "Transaction Not Found","/lms/v1/transactions/9999");
        Transaction faultyTransaction = Utils.createFaultyTransaction(lmsFault);

        Transaction transactionDB = transactionService.getTransactionById(9999);

        //Verify
        Assertions.assertThat(transactionDB).isNotNull();
        Assertions.assertThat(transactionDB).isInstanceOf(Transaction.class);
        Assertions.assertThat(transactionDB.getFault()).isNotNull();
        Assertions.assertThat(transactionDB.getFault().getHttp()).isEqualTo(faultyTransaction.getFault().getHttp());
        Assertions.assertThat(transactionDB.getFault().getCode()).isEqualTo(faultyTransaction.getFault().getCode());
        Assertions.assertThat(transactionDB.getFault().getMessage()).isEqualTo(faultyTransaction.getFault().getMessage());
        Assertions.assertThat(transactionDB.getFault().getPath()).isEqualTo(faultyTransaction.getFault().getPath());
        Assertions.assertThat(transactionDB.getFault()).isNotNull();
        Assertions.assertThat(transactionDB.getUser()).isNull();
        Assertions.assertThat(transactionDB.getBook()).isNull();
    }

    @Test
    @DisplayName("Test 5:Verify Add Transaction Check")
    @Order(5)
    public void srvTestAddTransactionSuccess() {
        transaction.setIssueDate(Utils.convertNowToSQLTS());
        transaction.setReturnDate(Utils.generateReturnSQLTS(transaction.getIssueDate()));

        when(transactionRepository.save(transaction)).thenReturn(transaction);
        when(userRepository.findById(transaction.getUser().getUserId())).thenReturn(Optional.of(user));
        when(bookRepository.findById(transaction.getBook().getBookId())).thenReturn(Optional.of(book));
        when(transactionRepository.findCountOfOpenTransactionsByUser(transaction.getUser().getUserId()))
                .thenReturn(2);

        // Act
        Transaction transactionDB = transactionService.addTransaction(transaction);

        // Assert
        Assertions.assertThat(transactionDB).isNotNull(); // Assert the User returned is not null
        Assertions.assertThat(transactionDB).isInstanceOf(Transaction.class); // Assert the method returns User instance
        Assertions.assertThat(Utils.convertTsToString(transactionDB.getIssueDate()))
                .isEqualTo(Utils.convertTsToString(transaction.getIssueDate()));
        Assertions.assertThat(Utils.convertTsToString(transactionDB.getReturnDate()))
                .isEqualTo(Utils.convertTsToString(transaction.getReturnDate()));
        Assertions.assertThat(transactionDB.getTransactionId()).isEqualTo(transaction.getTransactionId());

        // Verify that save was called
        verify(bookRepository, times(1)).save(book);
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    @DisplayName("Test 5:Verify Add Transaction OpenTransactionsFailure Check")
    @Order(5)
    public void srvTestAddTransactionOpenTransactionsFailure() {
        final int MAX_BOOKS_PER_USER = 3;
        final String MAX_BOOKS_PER_USER_LIMIT_USED = "User has a max limit of borrowing only "
                + MAX_BOOKS_PER_USER + " books at any time";

        when(transactionRepository.save(transaction)).thenReturn(transaction);
        when(userRepository.findById(transaction.getUser().getUserId())).thenReturn(Optional.of(user));
        when(bookRepository.findById(transaction.getBook().getBookId())).thenReturn(Optional.of(book));
        when(transactionRepository.findCountOfOpenTransactionsByUser(transaction.getUser().getUserId()))
                .thenReturn(3);

        // Act
        Transaction transactionDB = transactionService.addTransaction(transaction);

        LmsFault lmsFault = new LmsFault("Application Constraint Error", "210", MAX_BOOKS_PER_USER_LIMIT_USED, "/lms/v1/transactions");
        Transaction faultyTransaction = Utils.createFaultyTransaction(lmsFault);

        //Verify
        Assertions.assertThat(transactionDB).isNotNull();
        Assertions.assertThat(transactionDB).isInstanceOf(Transaction.class);
        Assertions.assertThat(transactionDB.getFault()).isNotNull();
        Assertions.assertThat(transactionDB.getFault().getHttp()).isEqualTo(faultyTransaction.getFault().getHttp());
        Assertions.assertThat(transactionDB.getFault().getCode()).isEqualTo(faultyTransaction.getFault().getCode());
        Assertions.assertThat(transactionDB.getFault().getMessage()).isEqualTo(faultyTransaction.getFault().getMessage());
        Assertions.assertThat(transactionDB.getFault().getPath()).isEqualTo(faultyTransaction.getFault().getPath());
        Assertions.assertThat(transactionDB.getFault()).isNotNull();
        Assertions.assertThat(transactionDB.getUser()).isNull();
        Assertions.assertThat(transactionDB.getBook()).isNull();

        // Verify that save was called
        verify(transactionRepository, times(0)).save(transaction);
    }

    @Test
    @DisplayName("Test 5:Verify Add Transaction BookBorrowedFailure Check")
    @Order(5)
    public void srvTestAddTransactionBookBorrowedFailure() {
        final String BOOKS_ALREADY_BORROWED = "Book is already borrowed by another User";
        book.setAvailable(false);

        when(transactionRepository.save(transaction)).thenReturn(transaction);
        when(userRepository.findById(transaction.getUser().getUserId())).thenReturn(Optional.of(user));
        when(bookRepository.findById(transaction.getBook().getBookId())).thenReturn(Optional.of(book));

        // Act
        Transaction transactionDB = transactionService.addTransaction(transaction);

        LmsFault lmsFault = new LmsFault("Application Constraint Error", "210", BOOKS_ALREADY_BORROWED, "/lms/v1/transactions");
        Transaction faultyTransaction = Utils.createFaultyTransaction(lmsFault);

        //Verify
        Assertions.assertThat(transactionDB).isNotNull();
        Assertions.assertThat(transactionDB).isInstanceOf(Transaction.class);
        Assertions.assertThat(transactionDB.getFault()).isNotNull();
        Assertions.assertThat(transactionDB.getFault().getHttp()).isEqualTo(faultyTransaction.getFault().getHttp());
        Assertions.assertThat(transactionDB.getFault().getCode()).isEqualTo(faultyTransaction.getFault().getCode());
        Assertions.assertThat(transactionDB.getFault().getMessage()).isEqualTo(faultyTransaction.getFault().getMessage());
        Assertions.assertThat(transactionDB.getFault().getPath()).isEqualTo(faultyTransaction.getFault().getPath());
        Assertions.assertThat(transactionDB.getFault()).isNotNull();
        Assertions.assertThat(transactionDB.getUser()).isNull();
        Assertions.assertThat(transactionDB.getBook()).isNull();

        // Verify that save was called
        verify(transactionRepository, times(0)).save(transaction);
    }

    @Test
    @DisplayName("Test 6:Verify Update Transaction Check")
    @Order(6)
    public void srvTestUpdateTransactionSuccess() {
        // Arrange
        //Transaction transaction2 = Transaction.builder().transactionId(1).user(user).book(book).build();

        book.setAvailable(true);
        transaction.setIssueDate(Utils.convertNowToSQLTS());
        transaction.setReturnDate(Utils.generateReturnSQLTS(transaction.getIssueDate()));
        transaction.setReturned(true);
        when(transactionRepository.save(Mockito.any(Transaction.class))).thenReturn(transaction);
        when(transactionRepository.findById(1)).thenReturn(Optional.of(transaction));
        when(bookRepository.findById(transaction.getBook().getBookId())).thenReturn(Optional.of(book));
        when(transactionRepository.findOpenTransactionByBook(book.getBookId())).thenReturn(transaction);

        // Act
        Transaction transactionDB = transactionService.updateTransaction(1);


        // Assert
        Assertions.assertThat(transactionDB).isNotNull(); // Assert the User returned is not null
        Assertions.assertThat(transactionDB).isInstanceOf(Transaction.class); // Assert the method returns User instance
        Assertions.assertThat(transactionDB.getUser()).isEqualTo(transaction.getUser());
        Assertions.assertThat(transactionDB.getBook()).isEqualTo(transaction.getBook());
        Assertions.assertThat(transactionDB.getBook().getAvailable()).isTrue();
        Assertions.assertThat(transactionDB.getReturned()).isTrue();
        Assertions.assertThat(transactionDB.getFine()).isZero();
        Assertions.assertThat(Utils.convertTsToString(transactionDB.getActualReturnDate())).
                isEqualTo(Utils.convertTsToString(Utils.convertNowToSQLTS()));

        // Verify that save was called
        verify(transactionRepository, times(1)).save(transaction);
    }
}