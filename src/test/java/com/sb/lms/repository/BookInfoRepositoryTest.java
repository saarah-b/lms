package com.sb.lms.repository;

import com.sb.lms.model.*;
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
public class BookInfoRepositoryTest {

    @Mock
    private BookInfoRepository bookInfoRepository;

    BookInfo bookInfo;

    @BeforeEach
    public void setup() {
        bookInfo = BookInfo.builder().bookInfoId(1).title("The Jungle Book").price(2.99).totalQuantity(2).build();
    }

    @Test
    @DisplayName("Test 1:Verify BookInfo By Id Retrieval Check")
    @Order(1)
    public void repTestGetBookInfoById() {

        when(bookInfoRepository.findById(bookInfo.getBookInfoId())).thenReturn(Optional.of(bookInfo));

        BookInfo bookInfoDB = bookInfoRepository.findById(bookInfo.getBookInfoId()).orElse(null);

        Assertions.assertThat(bookInfoDB).isNotNull();
        Assertions.assertThat(bookInfoDB).isInstanceOf(BookInfo.class);
        Assertions.assertThat(bookInfoDB).isNotInstanceOf(Book.class);
        Assertions.assertThat(bookInfo.getTitle()).isEqualTo(bookInfoDB.getTitle());
        Assertions.assertThat(bookInfo.getPrice()).isEqualTo(bookInfoDB.getPrice());
        Assertions.assertThat(bookInfo.getTotalQuantity()).isEqualTo(bookInfoDB.getTotalQuantity());
        Assertions.assertThat(bookInfoDB.getFault()).isNull();
        verify(bookInfoRepository, times(1)).findById(bookInfo.getBookInfoId());

    }

    @Test
    @DisplayName("Test 3:Verify BookInfo By Title Retrieval Check")
    @Order(3)
    public void repTestGetBookInfoByTitle() {
        String title = "ede";
        List<BookInfo> bookInfoList = new ArrayList<>();
        bookInfoList.add(bookInfo);
        BookInfo bookInfo2 = BookInfo.builder().bookInfoId(1).title("Horrid Henry").price(10.00).build();
        bookInfoList.add(bookInfo2);

        when(bookInfoRepository.findBookInfoByTitle(title)).thenReturn(bookInfoList);

        List<BookInfo> bookInfoDBList = bookInfoRepository.findBookInfoByTitle(title);
        BookInfo bookInfoInsideList = bookInfoDBList.get(0);

        //Verify
        Assertions.assertThat(bookInfoDBList).isInstanceOf(ArrayList.class);
        Assertions.assertThat(bookInfoDBList).isNotInstanceOf(Map.class);
        Assertions.assertThat(bookInfoInsideList).isInstanceOf(BookInfo.class);
        Assertions.assertThat(bookInfoInsideList).isNotInstanceOf(Book.class);
        Assertions.assertThat(bookInfoInsideList.getFault()).isNull();
        Assertions.assertThat(bookInfo.getTitle()).isEqualTo(bookInfoList.get(0).getTitle());
        Assertions.assertThat(bookInfo.getPrice()).isEqualTo(bookInfoList.get(0).getPrice());
        Assertions.assertThat(bookInfo2.getTitle()).isEqualTo(bookInfoList.get(1).getTitle());
        Assertions.assertThat(bookInfo2.getPrice()).isEqualTo(bookInfoList.get(1).getPrice());
        Assertions.assertThat(bookInfoList.size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Test 5:Verify Add BookInfo Check")
    @Order(5)
    public void repTestAddBookInfoSuccess() {

        when(bookInfoRepository.save(Mockito.any(BookInfo.class))).thenReturn(bookInfo);

        // Act
        BookInfo bookInfoDB = bookInfoRepository.save(bookInfo);

        // Assert
        Assertions.assertThat(bookInfoDB).isNotNull(); // Assert the BookInfo returned is not null
        Assertions.assertThat(bookInfoDB).isInstanceOf(BookInfo.class); // Assert the method returns BookInfo instance
        Assertions.assertThat(bookInfoDB.getTitle()).isEqualTo(bookInfo.getTitle());
        Assertions.assertThat(bookInfoDB.getPrice()).isEqualTo(bookInfo.getPrice());

        // Verify that save was called
        verify(bookInfoRepository, times(1)).save(bookInfo);
    }

    @Test
    @DisplayName("Test 7:Verify Delete BookInfo Success Check")
    @Order(7)
    public void repTestDeleteBookInfoByIdSuccess() {
        bookInfoRepository.deleteById(bookInfo.getBookInfoId());
        verify(bookInfoRepository, times(1)).deleteById(bookInfo.getBookInfoId());
    }

    @Test
    @DisplayName("Test 6:Verify Update BookInfo Check")
    @Order(6)
    public void repTestUpdateBookInfoSuccess() {
        // Arrange
        BookInfo bookInfo2 = BookInfo.builder().bookInfoId(1).title("Horrid Henry").price(10.00).build();

        when(bookInfoRepository.save(Mockito.any(BookInfo.class))).thenReturn(bookInfo2);
        when(bookInfoRepository.findById(bookInfo.getBookInfoId())).thenReturn(Optional.of(bookInfo2));

        // Act
        BookInfo bookInfoDB = bookInfoRepository.save(bookInfo2);

        // Assert
        Assertions.assertThat(bookInfoDB).isNotNull(); // Assert the BookInfo returned is not null
        Assertions.assertThat(bookInfoDB).isInstanceOf(BookInfo.class); // Assert the method returns BookInfo instance
        Assertions.assertThat(bookInfoDB.getTitle()).isEqualTo(bookInfo2.getTitle());
        Assertions.assertThat(bookInfoDB.getPrice()).isEqualTo(bookInfo2.getPrice());

        // Verify that save was called
        verify(bookInfoRepository, times(1)).save(bookInfo2);
    }

    @Test
    @DisplayName("Test 8:Verify Delete BookInfo Check")
    @Order(8)
    public void repTestDeleteBookInfoById() {

        doNothing().when(bookInfoRepository).deleteById(bookInfo.getBookInfoId());
        bookInfoRepository.deleteById(bookInfo.getBookInfoId());
        verify(bookInfoRepository, times(1)).deleteById(bookInfo.getBookInfoId());

        BookInfo deletedBookInfo = bookInfoRepository.findById(bookInfo.getBookInfoId()).orElse(null);
        //Verify
        Assertions.assertThat(deletedBookInfo).isNull();
    }
}