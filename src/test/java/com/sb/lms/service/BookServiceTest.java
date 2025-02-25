package com.sb.lms.service;

import com.sb.lms.model.Book;
import com.sb.lms.model.BookInfo;
import com.sb.lms.model.LmsFault;
import com.sb.lms.repository.*;
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
public class BookServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookInfoRepository bookInfoRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private BookService bookService;

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
    public void srvTestGetBookById() {

        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.of(book));

        Book bookDB = bookService.getBookById(book.getBookId());

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
    @DisplayName("Test 2:Verify Book Fault By Id Retrieval Check")
    @Order(2)
    public void srvTestGetBookByIdFault() {

        LmsFault lmsFault = new LmsFault("Resource Not Found", "404", "Book Not Found","/lms/v1/books/9999");
        Book faultyBook = Utils.createFaultyBook(lmsFault);

        Book bookDB = bookService.getBookById(9999);

        //Verify
        Assertions.assertThat(bookDB).isNotNull();
        Assertions.assertThat(bookDB).isInstanceOf(Book.class);
        Assertions.assertThat(bookDB.getFault()).isNotNull();
        Assertions.assertThat(bookDB.getFault().getHttp()).isEqualTo(faultyBook.getFault().getHttp());
        Assertions.assertThat(bookDB.getFault().getCode()).isEqualTo(faultyBook.getFault().getCode());
        Assertions.assertThat(bookDB.getFault().getMessage()).isEqualTo(faultyBook.getFault().getMessage());
        Assertions.assertThat(bookDB.getFault().getPath()).isEqualTo(faultyBook.getFault().getPath());
        Assertions.assertThat(bookDB.getFault()).isNotNull();
        Assertions.assertThat(bookDB.getShelfReference()).isNull();
        Assertions.assertThat(bookDB.getLocation()).isNull();
    }

    @Test
    @DisplayName("Test 3:Verify All Book of Book Retrieval Check")
    @Order(3)
    public void srvTestGetAllBookOfBookInfo() {
        String title = "ede";
        List<Book> bookList = new ArrayList<>();
        bookList.add(book);
        Book book2 = Book.builder().bookId(1).shelfReference("Shelf2").location("Whitechapel")
                .edition("2.14").available(true).bookInfo(bookInfo).build();
        bookList.add(book2);

        // Mock the behavior of the BookInfoRepository and BookRepository
        Mockito.when(bookInfoRepository.findById(bookInfo.getBookInfoId())).thenReturn(Optional.of(bookInfo));  // Mock finding BookInfo
        Mockito.when(bookRepository.findAllBooksByBookInfo(bookInfo.getBookInfoId())).thenReturn(bookList);  // Mock finding Books by BookInfo

        List<Book> bookDBList = bookService.getAllBooksOfBookInfo(bookInfo.getBookInfoId());
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
        verify(bookInfoRepository, times(1)).findById(bookInfo.getBookInfoId());
        verify(bookRepository, times(1)).findAllBooksByBookInfo(bookInfo.getBookInfoId());
    }

    @Test
    @DisplayName("Test 4:Verify All Book of Book Fault Retrieval Check")
    @Order(4)
    public void srvTestGetAllBookOfBookInfoFault() {

        LmsFault lmsFault = new LmsFault("Resource Not Found", "404",
                BOOK_INFO_NOT_FOUND,"/lms/v1/bookinfos/" + bookInfo.getBookInfoId());
        List<Book> faultyBookList = Utils.createFaultyBookInList(lmsFault);

        when(bookRepository.findAllBooksByBookInfo(bookInfo.getBookInfoId())).thenReturn(faultyBookList);

        List<Book> bookDBList = bookService.getAllBooksOfBookInfo(bookInfo.getBookInfoId());
        Book bookDBInsideList = bookDBList.get(0);

        //Verify
        Assertions.assertThat(bookDBList).isNotNull();
        Assertions.assertThat(bookDBList.size()).isEqualTo(1);
        Assertions.assertThat(bookDBInsideList).isInstanceOf(Book.class);
        Assertions.assertThat(bookDBInsideList.getFault()).isNotNull();
        Assertions.assertThat(bookDBInsideList.getFault().getHttp()).isEqualTo(lmsFault.getHttp());
        Assertions.assertThat(bookDBInsideList.getFault().getCode()).isEqualTo(lmsFault.getCode());
        Assertions.assertThat(bookDBInsideList.getFault().getMessage()).isEqualTo(lmsFault.getMessage());
        Assertions.assertThat(bookDBInsideList.getFault().getPath()).isEqualTo(lmsFault.getPath());
        Assertions.assertThat(bookDBInsideList.getFault()).isNotNull();
        Assertions.assertThat(bookDBInsideList.getShelfReference()).isNull();
        Assertions.assertThat(bookDBInsideList.getLocation()).isNull();
    }

    @Test
    @DisplayName("Test 5:Verify Add Book Check")
    @Order(5)
    public void srvTestAddBookSuccess() {
        Assertions.assertThat(bookInfo.getTotalQuantity()).isEqualTo(2);
        // Mock the RestTemplate to return the BookInfo when the URL is called
        String url = "http://localhost:8080/lms/v1/bookinfos/" + bookInfo.getBookInfoId();
        Mockito.when(restTemplate.getForObject(url, BookInfo.class)).thenReturn(bookInfo);

        // Mock the behavior of the BookInfoRepository and BookRepository
        when(bookInfoRepository.save(Mockito.any(BookInfo.class))).thenReturn(bookInfo);  // Mock save of BookInfo
        when(bookRepository.save(Mockito.any(Book.class))).thenReturn(book);  // Mock save of Book


        // Act
        Book bookDB = bookService.addBook(book);

        // Assert
        Assertions.assertThat(bookDB).isNotNull(); // Assert the Book returned is not null
        Assertions.assertThat(bookDB).isInstanceOf(Book.class); // Assert the method returns Book instance
        Assertions.assertThat(bookDB.getShelfReference()).isEqualTo(book.getShelfReference());
        Assertions.assertThat(bookDB.getLocation()).isEqualTo(book.getLocation());

        // Verify the interactions
        verify(restTemplate, times(1)).getForObject(url, BookInfo.class);
        verify(bookInfoRepository, times(1)).save(bookInfo);
        verify(bookRepository, times(1)).save(book);
        Assertions.assertThat(bookInfo.getTotalQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("Test 6:Verify Update Book Check")
    @Order(6)
    public void srvTestUpdateBookSuccess() {
        // Arrange
        Book book2 = Book.builder().bookId(1).shelfReference("Shelf2").location("Whitechapel")
                .edition("2.14").available(true).bookInfo(bookInfo).build();

        when(bookRepository.save(Mockito.any(Book.class))).thenReturn(book2);
        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.of(book2));

        // Act
        Book bookDB = bookService.updateBook(book.getBookId(), book2);

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
    public void srvTestDeleteBookByIdSuccess() {
        Assertions.assertThat(bookInfo.getTotalQuantity()).isEqualTo(2);

        // Mocking the repository calls
        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.of(book));
        when(transactionRepository.findCountOfAllTransactionsByBook(book.getBookId())).thenReturn(0);  // No transactions

        // Simulate a successful delete scenario
        doNothing().when(bookRepository).deleteById(book.getBookId());

        // Call the method under test
        bookService.deleteBookById(book.getBookId());

        // Verify interactions with repositories
        verify(bookRepository, times(1)).findById(book.getBookId());
        verify(transactionRepository, times(1)).findCountOfAllTransactionsByBook(book.getBookId());
        verify(bookRepository, times(1)).deleteById(book.getBookId());
        verify(bookInfoRepository, times(1)).save(bookInfo);
        Assertions.assertThat(bookInfo.getTotalQuantity()).isEqualTo(1);
    }

    @Test
    @DisplayName("Test 8:Verify Delete Book Error Borrowed Check")
    @Order(8)
    public void srvTestDeleteBookByIdErrorBorrowed() {
        book.setAvailable(false);
        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.of(book));
        bookService.deleteBookById(book.getBookId());
        verify(bookRepository, times(1)).findById(book.getBookId());
        verify(transactionRepository, times(0)).findCountOfAllTransactionsByBook(book.getBookId());
        verify(bookRepository, times(0)).deleteById(book.getBookId());
    }

    @Test
    @DisplayName("Test 9:Verify Delete Book Error History Check")
    @Order(9)
    public void srvTestDeleteBookByIdErrorHistory() {
        book.setAvailable(true);
        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.of(book));
        when(transactionRepository.findCountOfAllTransactionsByBook(book.getBookId())).thenReturn(1);
        bookService.deleteBookById(book.getBookId());
        verify(bookRepository, times(0)).deleteById(book.getBookId());
    }
}