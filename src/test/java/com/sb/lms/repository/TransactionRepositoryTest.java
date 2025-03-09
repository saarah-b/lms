package com.sb.lms.repository;

import com.sb.lms.model.*;
import com.sb.lms.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
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
public class TransactionRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private TransactionRepository transactionRepository;

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
                .edition("2.13").available(false).bookInfo(bookInfo).build();
        transaction = Transaction.builder().transactionId(1).user(user).book(book).build();
    }

    @Test
    @DisplayName("Test 1:Verify Transaction By Id Retrieval Check")
    @Order(1)
    public void repTestGetTransactionById() {

        transaction.setIssueDate(Utils.convertStringToTs("2025-02-19"));
        transaction.setReturnDate(Utils.generateReturnSQLTS(transaction.getIssueDate()));
        transaction.setReturned(false);

        when(transactionRepository.findById(transaction.getTransactionId())).thenReturn(Optional.of(transaction));

        Transaction transactionDB = transactionRepository.findById(transaction.getTransactionId()).orElse(null);

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
    @DisplayName("Test 2:Verify Counts of Open Transactions By User Check")
    @Order(2)
    public void repTestFindCountOfOpenTransactionsByUser() {
        // Arrange
        when(transactionRepository.findCountOfOpenTransactionsByUser(
                transaction.getUser().getUserId())).thenReturn(2);

        // Act
        int count = transactionRepository.findCountOfOpenTransactionsByUser(transaction.getUser().getUserId());

        // Assert
        Assertions.assertThat(count).isEqualTo(2);

        // Verify that save was called
        verify(transactionRepository, times(1)).
                findCountOfOpenTransactionsByUser(transaction.getUser().getUserId());
    }

    @Test
    @DisplayName("Test 3:Verify Counts of All Transactions By User Check")
    @Order(3)
    public void repTestFindCountOfAllTransactionsByUser() {
        // Arrange
        when(transactionRepository.findCountOfAllTransactionsByUser(
                transaction.getUser().getUserId())).thenReturn(10);

        // Act
        int count = transactionRepository.findCountOfAllTransactionsByUser(transaction.getUser().getUserId());

        // Assert
        Assertions.assertThat(count).isEqualTo(10);

        // Verify that save was called
        verify(transactionRepository, times(1)).
                findCountOfAllTransactionsByUser(transaction.getUser().getUserId());
    }

    @Test
    @DisplayName("Test 4:Verify Counts of All Transactions By Book Check")
    @Order(4)
    public void repTestFindCountOfAllTransactionsByBook() {
        // Arrange
        when(transactionRepository.findCountOfAllTransactionsByBook(
                transaction.getBook().getBookId())).thenReturn(10);

        // Act
        int count = transactionRepository.findCountOfAllTransactionsByBook(transaction.getBook().getBookId());

        // Assert
        Assertions.assertThat(count).isEqualTo(10);

        // Verify that save was called
        verify(transactionRepository, times(1)).
                findCountOfAllTransactionsByBook(transaction.getBook().getBookId());
    }

    @Test
    @DisplayName("Test 5:Verify Add Transaction Check")
    @Order(5)
    public void repTestAddTransactionSuccess() {

        when(transactionRepository.save(transaction)).thenReturn(transaction);

        // Act
        Transaction transactionDB = transactionRepository.save(transaction);

        // Assert
        Assertions.assertThat(transactionDB).isNotNull(); // Assert the User returned is not null
        Assertions.assertThat(transactionDB).isInstanceOf(Transaction.class); // Assert the method returns User instance
        Assertions.assertThat(transactionDB.getTransactionId()).isEqualTo(transaction.getTransactionId());

        // Verify that save was called
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    @DisplayName("Test 6:Verify Update User Check")
    @Order(6)
    public void repTestUpdateTransactionSuccess() {

        when(transactionRepository.save(Mockito.any(Transaction.class))).thenReturn(transaction);

        // Act
        Transaction transactionDB = transactionRepository.save(transaction);

        // Assert
        Assertions.assertThat(transactionDB).isNotNull();
        Assertions.assertThat(transactionDB).isInstanceOf(Transaction.class);
        Assertions.assertThat(transactionDB.getUser()).isEqualTo(transaction.getUser());
        Assertions.assertThat(transactionDB.getBook()).isEqualTo(transaction.getBook());

        // Verify that save was called
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    @DisplayName("Test 7:Verify Open Transactions By User Retrieval Check")
    @Order(7)
    public void repTestFindOpenTransactionsByUser() {
        Book book2 = Book.builder().bookId(2).shelfReference("Shelf2").location("Whitechapel")
                .edition("4.13").available(false).bookInfo(bookInfo).build();

        String title = "ede";
        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transaction);
        Transaction transaction2 = Transaction.builder().transactionId(1).user(user).book(book2).build();
        transactionList.add(transaction2);

        // Mock the behavior of the BookInfoRepository and BookRepository
        Mockito.when(transactionRepository.findOpenTransactionsByUser(transaction.getUser().getUserId())).
                thenReturn(transactionList);  // Mock finding Books by BookInfo

        List<Transaction> transactionDBList = transactionRepository.
                findOpenTransactionsByUser(transaction.getUser().getUserId());
        Transaction transactionInsideList = transactionDBList.get(0);

        //Verify
        Assertions.assertThat(transactionDBList).isInstanceOf(ArrayList.class);
        Assertions.assertThat(transactionDBList).isNotInstanceOf(Map.class);
        Assertions.assertThat(transactionInsideList).isInstanceOf(Transaction.class);
        Assertions.assertThat(transactionInsideList.getFault()).isNull();
        Assertions.assertThat(transaction.getUser()).isEqualTo(transactionDBList.get(0).getUser());
        Assertions.assertThat(transaction.getBook()).isEqualTo(transactionDBList.get(0).getBook());
        Assertions.assertThat(transaction2.getUser()).isEqualTo(transactionDBList.get(1).getUser());
        Assertions.assertThat(transaction2.getBook()).isEqualTo(transactionDBList.get(1).getBook());
        Assertions.assertThat(transactionDBList.size()).isGreaterThan(0);
        verify(transactionRepository, times(1)).findOpenTransactionsByUser(transaction.getUser().getUserId());
    }

    @Test
    @DisplayName("Test 8:Verify All Transactions By User Retrieval Check")
    @Order(8)
    public void repTestFindAllTransactionsByUser() {
        Book book2 = Book.builder().bookId(2).shelfReference("Shelf2").location("Whitechapel")
                .edition("4.13").available(true).bookInfo(bookInfo).build();

        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transaction);
        Transaction transaction2 = Transaction.builder().transactionId(2).user(user).book(book2).build();
        transactionList.add(transaction2);

        // Mock the behavior of the BookInfoRepository and BookRepository
        Mockito.when(transactionRepository.findAllTransactionsByUser(transaction.getUser().getUserId())).
                thenReturn(transactionList);  // Mock finding Books by BookInfo

        List<Transaction> transactionDBList = transactionRepository.
                findAllTransactionsByUser(transaction.getUser().getUserId());
        Transaction transactionInsideList = transactionDBList.get(0);

        //Verify
        Assertions.assertThat(transactionDBList).isInstanceOf(ArrayList.class);
        Assertions.assertThat(transactionDBList).isNotInstanceOf(Map.class);
        Assertions.assertThat(transactionInsideList).isInstanceOf(Transaction.class);
        Assertions.assertThat(transactionInsideList.getFault()).isNull();
        Assertions.assertThat(transaction.getUser()).isEqualTo(transactionDBList.get(0).getUser());
        Assertions.assertThat(transaction.getBook()).isEqualTo(transactionDBList.get(0).getBook());
        Assertions.assertThat(transaction2.getUser()).isEqualTo(transactionDBList.get(1).getUser());
        Assertions.assertThat(transaction2.getBook()).isEqualTo(transactionDBList.get(1).getBook());
        Assertions.assertThat(transactionDBList.size()).isGreaterThan(0);
        verify(transactionRepository, times(1)).findAllTransactionsByUser(transaction.getUser().getUserId());
    }

    @Test
    @DisplayName("Test 9:Verify All Transactions By Book Retrieval Check")
    @Order(9)
    public void repTestFindAllTransactionsByBook() {
        User user2 = User.builder().userId(2).firstName("Sarah").lastName("Joe")
                .email("s.j@c.com").mobileNumber("07004444555").build();

        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transaction);
        Transaction transaction2 = Transaction.builder().transactionId(2).user(user2).book(book).build();
        transactionList.add(transaction2);

        // Mock the behavior of the BookInfoRepository and BookRepository
        Mockito.when(transactionRepository.findAllTransactionsByBook(transaction.getBook().getBookId())).
                thenReturn(transactionList);

        List<Transaction> transactionDBList = transactionRepository.
                findAllTransactionsByBook(transaction.getBook().getBookId());
        Transaction transactionInsideList = transactionDBList.get(0);

        //Verify
        Assertions.assertThat(transactionDBList).isInstanceOf(ArrayList.class);
        Assertions.assertThat(transactionDBList).isNotInstanceOf(Map.class);
        Assertions.assertThat(transactionInsideList).isInstanceOf(Transaction.class);
        Assertions.assertThat(transactionInsideList.getFault()).isNull();
        Assertions.assertThat(transaction.getUser()).isEqualTo(transactionDBList.get(0).getUser());
        Assertions.assertThat(transaction.getBook()).isEqualTo(transactionDBList.get(0).getBook());
        Assertions.assertThat(transaction2.getUser()).isEqualTo(transactionDBList.get(1).getUser());
        Assertions.assertThat(transaction2.getBook()).isEqualTo(transactionDBList.get(1).getBook());
        Assertions.assertThat(transaction.getBook()).isEqualTo(transaction2.getBook());
        Assertions.assertThat(transactionDBList.size()).isGreaterThan(0);
        verify(transactionRepository, times(1)).findAllTransactionsByBook(transaction.getBook().getBookId());
    }
}