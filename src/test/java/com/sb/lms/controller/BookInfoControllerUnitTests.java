package com.sb.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sb.lms.model.Book;
import com.sb.lms.model.BookInfo;
import com.sb.lms.service.BookInfoService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookInfoController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookInfoControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookInfoService bookInfoService;

    @Autowired
    private ObjectMapper objectMapper;

    BookInfo bookInfo;

    @BeforeEach
    public void setup() {
        bookInfo = BookInfo.builder().bookInfoId(1).title("The Jungle Book").price(2.99).totalQuantity(2).build();
    }

    @Test
    @DisplayName("Test 1:Verify BookInfo By Id Retrieval Check")
    @Order(1)
    public void ctrTestGetBookInfoById() throws Exception {
        // arrange
        when(bookInfoService.getBookInfoById(bookInfo.getBookInfoId())).thenReturn(bookInfo);

        // action
        ResultActions response = mockMvc.perform(get("/lms/v1/bookinfos/{bookInfoId}", 1));
        response.andExpect(status().isOk()) // Expect HTTP 200
                .andDo(print())
                .andExpect(jsonPath("$.bookInfoId").value(1))
                .andExpect(jsonPath("$.title").value(bookInfo.getTitle()))
                .andExpect(jsonPath("$.price").value(bookInfo.getPrice()))
                .andExpect(jsonPath("$.totalQuantity").value(bookInfo.getTotalQuantity()));
    }

    @Test
    @DisplayName("Test 3:Verify BookInfo By Name Retrieval Check")
    @Order(3)
    public void ctrTestGetBookInfoByTitle() throws Exception {

        // arrange
        String title = "Jungle";
        List<BookInfo> bookInfoList = new ArrayList<>();
        bookInfoList.add(bookInfo);
        BookInfo bookInfo2 = BookInfo.builder().bookInfoId(2).title("Magic").price(4.99)
                .totalQuantity(1).build();
        bookInfoList.add(bookInfo2);

        when(bookInfoService.getBookInfoByTitle(title)).thenReturn(bookInfoList);

        // action
        ResultActions response = mockMvc.perform(get("/lms/v1/bookinfos/title/{title}", title)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookInfo)));

        // verify the output
        response.andExpect(status().isOk()) // Expect HTTP 200
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(bookInfoList.size())))
                .andExpect(jsonPath("$[0].bookInfoId").value(bookInfo.getBookInfoId()))
                .andExpect(jsonPath("$[0].title").value(bookInfo.getTitle()))
                .andExpect(jsonPath("$[0].price").value(bookInfo.getPrice()))
                .andExpect(jsonPath("$[1].bookInfoId").value(bookInfo2.getBookInfoId()))
                .andExpect(jsonPath("$[1].title").value(bookInfo2.getTitle()))
                .andExpect(jsonPath("$[1].price").value(bookInfo2.getPrice()))  ;
    }

    //Post Controller
    @Test
    @DisplayName("Test 5:Verify Add BookInfo Check")
    @Order(5)
    public void ctrTestAddBookInfoSuccess() throws Exception {
        // arrange
        when(bookInfoService.addBookInfo(any(BookInfo.class))).thenReturn(bookInfo);

        // action
        ResultActions response = mockMvc.perform(post("/lms/v1/bookinfos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookInfo)));

        // verify
        response.andDo(print()).
                andExpect(status().isCreated()) // Expect HTTP 201
                .andExpect(jsonPath("$.bookInfoId").value(bookInfo.getBookInfoId()))
                .andExpect(jsonPath("$.title").value(bookInfo.getTitle()))
                .andExpect(jsonPath("$.price").value(bookInfo.getPrice()))
                .andExpect(jsonPath("$.totalQuantity").value(bookInfo.getTotalQuantity()));
    }

    //Update employee
    @Test
    @DisplayName("Test 6:Verify Update BookInfo Check")
    @Order(6)
    public void ctrTestUpdateBookInfoSuccess() throws Exception{
        // arrange
        bookInfo.setTitle("Horrid Henry");
        bookInfo.setPrice(10.00);

        when(bookInfoService.updateBookInfo(1,bookInfo)).thenReturn(bookInfo);

        // action
        ResultActions response = mockMvc.perform(put("/lms/v1/bookinfos/{bookInfoId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookInfo)));

        // verify
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title").value(bookInfo.getTitle()))
                .andExpect(jsonPath("$.price").value(bookInfo.getPrice()));
    }


    // delete Book
    @Test
    @DisplayName("Test 7:Verify Delete BookInfo Check")
    @Order(7)
    public void ctrTestDeleteBookInfoById() throws Exception {
        // arrange
        when(bookInfoService.deleteBookInfoById(1)).thenReturn("success:BookInfo");

        // Act: Perform the DELETE request
        ResultActions response = mockMvc.perform(delete("/lms/v1/bookinfos/{bookInfoId}", bookInfo.getBookInfoId()));
        response.andExpect(content().string(containsString("success")));

    }

    // delete Book
    @Test
    @DisplayName("Test 8:Verify Delete BookInfo Error Check")
    @Order(8)
    public void ctrTestDeleteBookInfoByIdError() throws Exception {
        // arrange
        when(bookInfoService.deleteBookInfoById(1)).thenReturn("error:BookInfo");

        // Act: Perform the DELETE request
        ResultActions response = mockMvc.perform(delete("/lms/v1/bookinfos/{bookInfoId}", bookInfo.getBookInfoId()));
        response.andExpect(content().string(containsString("error")));

    }
}