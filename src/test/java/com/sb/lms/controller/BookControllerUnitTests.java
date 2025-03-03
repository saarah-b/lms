package com.sb.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sb.lms.model.Book;
import com.sb.lms.model.BookInfo;
import com.sb.lms.service.BookService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    Book book;
    BookInfo bookInfo;

    @BeforeEach
    public void setup() {
        bookInfo = BookInfo.builder().bookInfoId(1).title("The Jungle Book").price(2.99).totalQuantity(2).build();
        book = Book.builder().bookId(1).shelfReference("Shelf1").location("Whitechapel")
                .edition("2.13").available(true).bookInfo(bookInfo).build();
    }

    @Test
    @DisplayName("Test 1:Verify Book By Id Retrieval Check")
    @Order(1)
    public void ctrTestGetBookById() throws Exception {
        // arrange
        when(bookService.getBookById(1)).thenReturn(book);

        // action
        ResultActions response = mockMvc.perform(get("/lms/v1/books/{bookId}", 1));
        response.andExpect(status().isOk()) // Expect HTTP 200
                .andDo(print())
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.shelfReference").value(book.getShelfReference()))
                .andExpect(jsonPath("$.location").value(book.getLocation()))
                .andExpect(jsonPath("$.edition").value(book.getEdition()))
                .andExpect(jsonPath("$.available").value(book.getAvailable()))
                .andExpect(jsonPath("$.bookInfo.bookInfoId").value(book.getBookInfo().getBookInfoId()));
    }
/*
    @Test
    @DisplayName("Test 3:Verify Book By Name Retrieval Check")
    @Order(3)
    public void ctrTestGetBookByName() throws Exception {

        // arrange
        String name = "ede";
        List<User> userList = new ArrayList<>();
        userList.add(user);
        userList.add(User.builder().userId(2).firstName("Sam").lastName("Bedekar")
                .email("sam_curran@gmail.com").build());

        when(userService.getUserByName(name)).thenReturn(userList);

        // action
        ResultActions response = mockMvc.perform(get("/lms/v1/users/name/{name}", name)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        // verify the output
        response.andExpect(status().isOk()) // Expect HTTP 200
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(userList.size())))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Saarah"))
                .andExpect(jsonPath("$[0].lastName").value("Bedekar"))
                .andExpect(jsonPath("$[1].userId").value(2))
                .andExpect(jsonPath("$[1].firstName").value("Sam"))
                .andExpect(jsonPath("$[1].lastName").value("Bedekar"));
    }*/

    //Post Controller
    @Test
    @DisplayName("Test 5:Verify Add Book Check")
    @Order(5)
    public void ctrTestAddBookSuccess() throws Exception {
        // arrange
        when(bookService.addBook(any(Book.class))).thenReturn(book);

        // action
        ResultActions response = mockMvc.perform(post("/lms/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)));

        // verify
        response.andDo(print()).
                andExpect(status().isCreated()) // Expect HTTP 201
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.shelfReference").value(book.getShelfReference()))
                .andExpect(jsonPath("$.location").value(book.getLocation()))
                .andExpect(jsonPath("$.edition").value(book.getEdition()))
                .andExpect(jsonPath("$.available").value(book.getAvailable()))
                .andExpect(jsonPath("$.bookInfo.bookInfoId").value(book.getBookInfo().getBookInfoId()));
    }

    //Update employee
    @Test
    @DisplayName("Test 6:Verify Update Book Check")
    @Order(6)
    public void ctrTestUpdateBookSuccess() throws Exception{
        // arrange
        book.setShelfReference("Shelf2");
        book.setAvailable(false);

        when(bookService.updateBook(1,book)).thenReturn(book);

        // action
        ResultActions response = mockMvc.perform(put("/lms/v1/books/{bookId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)));

        // verify
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.shelfReference", is(book.getShelfReference())))
                .andExpect(jsonPath("$.available", is(book.getAvailable())));
    }


    // delete Book
    @Test
    @DisplayName("Test 7:Verify Delete Book Check")
    @Order(7)
    public void ctrTestDeleteBookById() throws Exception {
        // arrange
        when(bookService.deleteBookById(1)).thenReturn("success:Book");

        // Act: Perform the DELETE request
        ResultActions response = mockMvc.perform(delete("/lms/v1/books/{bookId}", book.getBookId()));
        response.andExpect(content().string(containsString("success")));

    }

    // delete Book
    @Test
    @DisplayName("Test 8:Verify Delete Book Error Check")
    @Order(8)
    public void ctrTestDeleteBookByIdError() throws Exception {
        // arrange
        when(bookService.deleteBookById(1)).thenReturn("error:Book");

        // Act: Perform the DELETE request
        ResultActions response = mockMvc.perform(delete("/lms/v1/books/{bookId}", book.getBookId()));
        response.andExpect(content().string(containsString("error")));

    }
}