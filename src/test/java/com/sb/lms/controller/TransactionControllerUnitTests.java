package com.sb.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sb.lms.model.Book;
import com.sb.lms.model.Transaction;
import com.sb.lms.model.User;
import com.sb.lms.service.TransactionService;
import com.sb.lms.utils.Utils;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    Transaction transaction;
    User user;
    Book book;

    @BeforeEach
    public void setup() {
        user = User.builder().userId(1).build();
        book = Book.builder().bookId(1).build();
        transaction = Transaction.builder().transactionId(1).user(user).book(book)
                .build();
    }

    @Test
    @DisplayName("Test 1:Verify Transaction By Id Retrieval Check")
    @Order(1)
    public void ctrTestGetTransactionById() throws Exception {
        // arrange
        when(transactionService.getTransactionById(1)).thenReturn(transaction);

        // action
        ResultActions response = mockMvc.perform(get("/lms/v1/transactions/{transactionId}", 1));
        response.andExpect(status().isOk()) // Expect HTTP 200
                .andDo(print())
                .andExpect(jsonPath("$.transactionId").value(transaction.getTransactionId()))
                .andExpect(jsonPath("$.user.userId").value(transaction.getUser().getUserId()))
                .andExpect(jsonPath("$.book.bookId").value(transaction.getBook().getBookId()));
    }
/*
    @Test
    @DisplayName("Test 3:Verify User By Name Retrieval Check")
    @Order(3)
    public void ctrTestGetUserByName() throws Exception {

        // arrange
        String name = "ede";
        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transaction);
        Transaction transaction2 = Transaction.builder().transactionId(2).user(user).book(book).build();
        transactionList.add(transaction2);

        when(transactionService.getUserByName(name)).thenReturn(transactionList);

        // action
        ResultActions response = mockMvc.perform(get("/lms/v1/users/name/{name}", name)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        // verify the output
        response.andExpect(status().isOk()) // Expect HTTP 200
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(userList.size())))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(user.getLastName()))
                .andExpect(jsonPath("$[1].userId").value(user2.getUserId()))
                .andExpect(jsonPath("$[1].firstName").value(user2.getFirstName()))
                .andExpect(jsonPath("$[1].lastName").value(user2.getLastName()));
    }
*/
    //Post Controller
    @Test
    @DisplayName("Test 5:Verify Add Transaction Check")
    @Order(5)
    public void ctrTestAddTransactionSuccess() throws Exception {
        // arrange
        transaction.setIssueDate(Utils.convertNowToSQLTS());
        String strTS = transaction.getIssueDate().toString().substring(0,10);
        System.out.println("strTS = " + strTS);
        when(transactionService.addTransaction(any(Transaction.class))).thenReturn(transaction);

        // action
        ResultActions response = mockMvc.perform(post("/lms/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)));

        // verify
        response.andDo(print()).
            andExpect(status().isCreated()) // Expect HTTP 201
            .andExpect(jsonPath("$.transactionId").value(1))
            .andExpect(jsonPath("$.user.userId", is(transaction.getUser().getUserId())))
            .andExpect(jsonPath("$.book.bookId", is(transaction.getBook().getBookId())))
            .andExpect(result -> {
                // Find the starting index of "issueDate"
                String responseDate = result.getResponse().getContentAsString();
                // Find the starting index of "issueDate"
                int startIndex = responseDate.indexOf("\"issueDate\":\"") + "\"issueDate\":\"".length();

                responseDate = responseDate.substring(startIndex,startIndex + 10);
                String transactionDate = transaction.getIssueDate().toString().substring(0, 10);
                assertEquals(transactionDate, responseDate);
            });
    }

    //Update employee
    @Test
    @DisplayName("Test 6:Verify Update Transaction Check")
    @Order(6)
    public void ctrTestUpdateTransactionSuccess() throws Exception{
        // arrange
        transaction.setReturnDate(Utils.convertNowToSQLTS());
        transaction.setActualReturnDate(Utils.convertNowToSQLTS());
        transaction.setFine(transaction.calculateFine());
        when(transactionService.updateTransaction(1)).thenReturn(transaction);

        // action
        ResultActions response = mockMvc.perform(put("/lms/v1/transactions/{transactionId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)));

        // verify
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.fine", is(transaction.getFine())))
                .andExpect(result -> {
                    // Find the starting index of "actualReturnDate"
                    String responseDate = result.getResponse().getContentAsString();
                    System.out.println("responseDate = " + responseDate);
                    // Find the starting index of "actualReturnDate"
                    int startIndex = responseDate.indexOf("\"actualReturnDate\":\"") + "\"actualReturnDate\":\"".length();

                    responseDate = responseDate.substring(startIndex,startIndex + 10);
                    String transactionDate = transaction.getActualReturnDate().toString().substring(0, 10);
                    assertEquals(transactionDate, responseDate);
                });
    }
}