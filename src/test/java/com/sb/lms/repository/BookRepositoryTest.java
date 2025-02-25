package com.sb.lms.repository;

import com.sb.lms.model.Book;
import com.sb.lms.model.BookInfo;
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
import org.springframework.web.client.RestTemplate;

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
public class BookRepositoryTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookInfoRepository bookInfoRepository;

    @Mock
    private TransactionRepository transactionRepository;

    Book book;
    BookInfo bookInfo;

    private static final String BOOK_INFO_NOT_FOUND = "Associated BookInfo Not Found";
    private static final String BOOK_NOT_FOUND = "Associated Book(s) Not Found";

    @BeforeEach
    public void setup() {
        bookInfo = BookInfo.builder().bookInfoId(1).title("The Jungle Book").price(2.99).totalQuantity(2).build();
        book = Book.builder().bookId(1).shelfReference("Shelf1").location("Whitechapel")
                .edition("2.13").available(true).bookInfo(bookInfo).build();
    }

    @Test
    @DisplayName("Test 1:Verify Book By Id Retrieval Check")
    @Order(1)
    public void repTestGetBookById() {

        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.of(book));

        Book bookDB = bookRepository.findById(book.getBookId()).orElse(null);

        Assertions.assertThat(bookDB).isNotNull();
        Assertions.assertThat(bookDB).isInstanceOf(Book.class);
        Assertions.assertThat(bookDB).isNotInstanceOf(BookInfo.class);
        Assertions.assertThat(book.getShelfReference()).isEqualTo(bookDB.getShelfReference());
        Assertions.assertThat(book.getLocation()).isEqualTo(bookDB.getLocation());
        Assertions.assertThat(book.getEdition()).isEqualTo(bookDB.getEdition());
        Assertions.assertThat(bookDB.getFault()).isNull();
        verify(bookRepository, times(1)).findById(book.getBookId());

    }

    @Test
    @DisplayName("Test 3:Verify All Book of Book Retrieval Check")
    @Order(3)
    public void repTestGetAllBookOfBookInfo() {
        String title = "ede";
        List<Book> bookList = new ArrayList<>();
        bookList.add(book);
        Book book2 = Book.builder().bookId(1).shelfReference("Shelf2").location("Whitechapel")
                .edition("2.14").available(true).bookInfo(bookInfo).build();
        bookList.add(book2);

        // Mock the behavior of the BookInfoRepository and BookRepository
        Mockito.when(bookRepository.findAllBooksByBookInfo(bookInfo.getBookInfoId())).thenReturn(bookList);  // Mock finding Books by BookInfo

        List<Book> bookDBList = bookRepository.findAllBooksByBookInfo(bookInfo.getBookInfoId());
        Book bookInsideList = bookDBList.get(0);

        //Verify
        Assertions.assertThat(bookDBList).isInstanceOf(ArrayList.class);
        Assertions.assertThat(bookDBList).isNotInstanceOf(Map.class);
        Assertions.assertThat(bookInsideList).isInstanceOf(Book.class);
        Assertions.assertThat(bookInsideList).isNotInstanceOf(BookInfo.class);
        Assertions.assertThat(bookInsideList.getFault()).isNull();
        Assertions.assertThat(book.getShelfReference()).isEqualTo(bookDBList.get(0).getShelfReference());
        Assertions.assertThat(book.getLocation()).isEqualTo(bookDBList.get(0).getLocation());
        Assertions.assertThat(book2.getShelfReference()).isEqualTo(bookDBList.get(1).getShelfReference());
        Assertions.assertThat(book2.getLocation()).isEqualTo(bookDBList.get(1).getLocation());
        Assertions.assertThat(bookDBList.size()).isGreaterThan(0);
        verify(bookRepository, times(1)).findAllBooksByBookInfo(bookInfo.getBookInfoId());
    }

    @Test
    @DisplayName("Test 5:Verify Add Book Check")
    @Order(5)
    public void repTestAddBookSuccess() {

        // Mock the behavior of the BookInfoRepository and BookRepository
        when(bookRepository.save(Mockito.any(Book.class))).thenReturn(book);  // Mock save of Book

        // Act
        Book bookDB = bookRepository.save(book);

        // Assert
        Assertions.assertThat(bookDB).isNotNull(); // Assert the Book returned is not null
        Assertions.assertThat(bookDB).isInstanceOf(Book.class); // Assert the method returns Book instance
        Assertions.assertThat(bookDB.getShelfReference()).isEqualTo(book.getShelfReference());
        Assertions.assertThat(bookDB.getLocation()).isEqualTo(book.getLocation());

        // Verify the interactions
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    @DisplayName("Test 6:Verify Update Book Check")
    @Order(6)
    public void repTestUpdateBookSuccess() {
        // Arrange
        Book book2 = Book.builder().bookId(1).shelfReference("Shelf2").location("Whitechapel")
                .edition("2.14").available(true).bookInfo(bookInfo).build();

        when(bookRepository.save(Mockito.any(Book.class))).thenReturn(book2);
        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.of(book2));

        // Act
        Book bookDB = bookRepository.save(book2);

        // Assert
        Assertions.assertThat(bookDB).isNotNull(); // Assert the Book returned is not null
        Assertions.assertThat(bookDB).isInstanceOf(Book.class); // Assert the method returns Book instance
        Assertions.assertThat(bookDB.getShelfReference()).isEqualTo(book2.getShelfReference());
        Assertions.assertThat(bookDB.getLocation()).isEqualTo(book2.getLocation());

        // Verify that save was called
        verify(bookRepository, times(1)).save(book2);
    }

    @Test
    @DisplayName("Test 7:Verify Delete Book Success Check")
    @Order(7)
    public void repTestDeleteBookByIdSuccess() {
        Assertions.assertThat(bookInfo.getTotalQuantity()).isEqualTo(2);

        // Simulate a successful delete scenario
        doNothing().when(bookRepository).deleteById(book.getBookId());

        // Call the method under test
        bookRepository.deleteById(book.getBookId());

        // Verify interactions with repositories
        verify(bookRepository, times(1)).deleteById(book.getBookId());

    }
}