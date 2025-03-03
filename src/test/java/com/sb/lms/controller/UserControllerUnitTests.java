package com.sb.lms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sb.lms.model.User;
import com.sb.lms.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    User user;

    @BeforeEach
    public void setup() {
        user = User.builder().userId(1).firstName("Saarah").lastName("Bedekar")
                .email("a.b@c.com").mobileNumber("07894444555").build();
    }

    @Test
    @DisplayName("Test 1:Verify User By Id Retrieval Check")
    @Order(1)
    public void ctrTestGetUserById() throws Exception {
        // arrange
        when(userService.getUserById(1)).thenReturn(user);

        // action
        ResultActions response = mockMvc.perform(get("/lms/v1/users/{userId}", 1));
        response.andExpect(status().isOk()) // Expect HTTP 200
                .andDo(print())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()));
    }

    @Test
    @DisplayName("Test 3:Verify User By Name Retrieval Check")
    @Order(3)
    public void ctrTestGetUserByName() throws Exception {

        // arrange
        String name = "ede";
        List<User> userList = new ArrayList<>();
        userList.add(user);
        User user2 = User.builder().userId(2).firstName("Sam").lastName("Bedekar")
                .email("sam_curran@gmail.com").build();
        userList.add(user2);

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
                .andExpect(jsonPath("$[0].firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(user.getLastName()))
                .andExpect(jsonPath("$[1].userId").value(user2.getUserId()))
                .andExpect(jsonPath("$[1].firstName").value(user2.getFirstName()))
                .andExpect(jsonPath("$[1].lastName").value(user2.getLastName()));
    }

    //Post Controller
    @Test
    @DisplayName("Test 5:Verify Add User Check")
    @Order(5)
    public void ctrTestAddUserSuccess() throws Exception {
        // arrange
        when(userService.addUser(any(User.class))).thenReturn(user);

        // action
        ResultActions response = mockMvc.perform(post("/lms/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        // verify
        response.andDo(print()).
                andExpect(status().isCreated()) // Expect HTTP 201
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.firstName", is(user.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(user.getLastName())));
    }

    //Update employee
    @Test
    @DisplayName("Test 6:Verify Update User Check")
    @Order(6)
    public void ctrTestUpdateUserSuccess() throws Exception{
        // arrange
        user.setEmail("max@gmail.com");
        user.setMobileNumber("07894444000");

        when(userService.updateUser(1,user)).thenReturn(user);

        // action
        ResultActions response = mockMvc.perform(put("/lms/v1/users/{userId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        // verify
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.mobileNumber", is(user.getMobileNumber())));
    }


    // delete User
    @Test
    @DisplayName("Test 7:Verify Delete User Check")
    @Order(7)
    public void ctrTestDeleteUserByIdSucess() throws Exception {
        // arrange
        when(userService.deleteUserById(1)).thenReturn("success:User");

        // Act: Perform the DELETE request
        ResultActions response = mockMvc.perform(delete("/lms/v1/users/{userId}", user.getUserId()));
        response.andExpect(content().string(containsString("success")));

    }

    // delete User
    @Test
    @DisplayName("Test 8:Verify Delete User Error Check")
    @Order(8)
    public void ctrTestDeleteUserByIdError() throws Exception {
        // arrange
        when(userService.deleteUserById(1)).thenReturn("error:User");

        // Act: Perform the DELETE request
        ResultActions response = mockMvc.perform(delete("/lms/v1/users/{userId}", user.getUserId()));
        response.andExpect(content().string(containsString("error")));

    }
}