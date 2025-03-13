package com.sb.lms.service;

import com.sb.lms.model.Address;
import com.sb.lms.model.Book;
import com.sb.lms.model.LmsFault;
import com.sb.lms.model.User;
import com.sb.lms.repository.AddressRepository;
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
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private UserService userService;

    User user;

    @BeforeEach
    public void setup() {
        user = User.builder().userId(1).firstName("Saarah").lastName("Bedekar")
                .email("a.b@c.com").mobileNumber("07894444555").build();
    }

    @Test
    @DisplayName("Test 1:Verify User By Id Retrieval Check")
    @Order(1)
    public void srvTestGetUserById() {
        // arrange
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        // action
        User userDB = userService.getUserById(user.getUserId());

        // Verify and assert
        Assertions.assertThat(userDB).isNotNull();
        Assertions.assertThat(userDB).isInstanceOf(User.class);
        Assertions.assertThat(userDB).isNotInstanceOf(Book.class);
        Assertions.assertThat(user.getFirstName()).isEqualTo(userDB.getFirstName());
        Assertions.assertThat(user.getLastName()).isEqualTo(userDB.getLastName());
        Assertions.assertThat(userDB.getFault()).isNull();
        verify(userRepository, times(1)).findById(user.getUserId());

    }

    @Test
    @DisplayName("Test 2:Verify User Fault By Id Retrieval Check")
    @Order(2)
    public void srvTestGetUserByIdFault() {

        LmsFault lmsFault = new LmsFault("Resource Not Found", "404", "User Not Found","/lms/v1/users/9999");
        User faultyUser = Utils.createFaultyUser(lmsFault);

        User userDB = userService.getUserById(9999);

        //Verify
        Assertions.assertThat(userDB).isNotNull();
        Assertions.assertThat(userDB).isInstanceOf(User.class);
        Assertions.assertThat(userDB.getFault()).isNotNull();
        Assertions.assertThat(userDB.getFault().getHttp()).isEqualTo(faultyUser.getFault().getHttp());
        Assertions.assertThat(userDB.getFault().getCode()).isEqualTo(faultyUser.getFault().getCode());
        Assertions.assertThat(userDB.getFault().getMessage()).isEqualTo(faultyUser.getFault().getMessage());
        Assertions.assertThat(userDB.getFault().getPath()).isEqualTo(faultyUser.getFault().getPath());
        Assertions.assertThat(userDB.getFault()).isNotNull();
        Assertions.assertThat(userDB.getFirstName()).isNull();
        Assertions.assertThat(userDB.getLastName()).isNull();
    }

    @Test
    @DisplayName("Test 3:Verify User By Name Retrieval Check")
    @Order(3)
    public void srvTestGetUserByName() {
        String name = "ede";
        List<User> userList = new ArrayList<>();
        userList.add(user);
        User user2 = User.builder().userId(2).firstName("Sam").lastName("Bedekar")
                .email("sam_curran@gmail.com").build();
        userList.add(user2);

        when(userRepository.findUserByName(name)).thenReturn(userList);

        List<User> userDBList = userService.getUserByName(name);
        User userInsideList = userDBList.get(0);

        //Verify
        Assertions.assertThat(userDBList).isInstanceOf(ArrayList.class);
        Assertions.assertThat(userDBList).isNotInstanceOf(Map.class);
        Assertions.assertThat(userInsideList).isInstanceOf(User.class);
        Assertions.assertThat(userInsideList).isNotInstanceOf(Book.class);
        Assertions.assertThat(userInsideList.getFault()).isNull();
        Assertions.assertThat(user.getFirstName()).isEqualTo(userList.get(0).getFirstName());
        Assertions.assertThat(user.getLastName()).isEqualTo(userList.get(0).getLastName());
        Assertions.assertThat(user2.getFirstName()).isEqualTo(userList.get(1).getFirstName());
        Assertions.assertThat(user2.getLastName()).isEqualTo(userList.get(1).getLastName());
        Assertions.assertThat(userDBList.size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Test 4:Verify User Fault By Name Retrieval Check")
    @Order(4)
    public void srvTestGetUserByNameFault() {
        String name = "xyz";
        LmsFault lmsFault = new LmsFault("Resource Not Found", "404", "User Not Found","/lms/v1/users/name/xyz");
        List<User> faultyUserList = Utils.createFaultyUserInList(lmsFault);

        when(userRepository.findUserByName(name)).thenReturn(faultyUserList);

        List<User> userDBList = userService.getUserByName(name);
        User userDBInsideList = userDBList.get(0);

        //Verify
        Assertions.assertThat(faultyUserList).isNotNull();
        Assertions.assertThat(faultyUserList.size()).isEqualTo(1);
        Assertions.assertThat(userDBInsideList).isInstanceOf(User.class);
        Assertions.assertThat(userDBInsideList.getFault()).isNotNull();
        Assertions.assertThat(userDBInsideList.getFault().getHttp()).isEqualTo(lmsFault.getHttp());
        Assertions.assertThat(userDBInsideList.getFault().getCode()).isEqualTo(lmsFault.getCode());
        Assertions.assertThat(userDBInsideList.getFault().getMessage()).isEqualTo(lmsFault.getMessage());
        Assertions.assertThat(userDBInsideList.getFault().getPath()).isEqualTo(lmsFault.getPath());
        Assertions.assertThat(userDBInsideList.getFault()).isNotNull();
        Assertions.assertThat(userDBInsideList.getFirstName()).isNull();
        Assertions.assertThat(userDBInsideList.getLastName()).isNull();
    }

    @Test
    @DisplayName("Test 5:Verify Add User Check")
    @Order(5)
    public void srvTestAddUserSuccess() {

        Address address = Address.builder().addressId(1).build();
        user.setAddress(address);

        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        when(addressRepository.findById(1)).thenReturn(Optional.ofNullable(address));

        // Act
        User userDB = userService.addUser(user);

        // Assert
        Assertions.assertThat(userDB).isNotNull(); // Assert the User returned is not null
        Assertions.assertThat(userDB).isInstanceOf(User.class); // Assert the method returns User instance
        Assertions.assertThat(userDB.getFirstName()).isEqualTo(user.getFirstName());
        Assertions.assertThat(userDB.getLastName()).isEqualTo(user.getLastName());
        Assertions.assertThat(user.getUserId()).isGreaterThan(0);

        // Verify that save was called
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Test 6:Verify Update User Check")
    @Order(6)
    public void srvTestUpdateUserSuccess() {
        // Arrange
        User user2 = User.builder().userId(1).email("a.b@c.com").mobileNumber("07894444555").build();

        when(userRepository.save(Mockito.any(User.class))).thenReturn(user2);
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user2));

        // Act
        User userDB = userService.updateUser(1, user2);

        // Assert
        Assertions.assertThat(userDB).isNotNull(); // Assert the User returned is not null
        Assertions.assertThat(userDB).isInstanceOf(User.class); // Assert the method returns User instance
        Assertions.assertThat(userDB.getEmail()).isEqualTo(user2.getEmail());
        Assertions.assertThat(userDB.getMobileNumber()).isEqualTo(user2.getMobileNumber());

        // Verify that save was called
        verify(userRepository, times(1)).save(user2);
    }

    @Test
    @DisplayName("Test 7:Verify Delete User Success Check")
    @Order(7)
    public void srvTestDeleteUserById() {
        when(transactionRepository.findCountOfOpenTransactionsByUser(user.getUserId())).thenReturn(0);
        when(transactionRepository.findCountOfAllTransactionsByUser(user.getUserId())).thenReturn(0);
        userService.deleteUserById(user.getUserId());
        verify(userRepository, times(1)).deleteById(user.getUserId());

        Optional<User> userOptional = userRepository.findById(user.getUserId());
        //Verify
        Assertions.assertThat(userOptional).isEmpty();
    }

    @Test
    @DisplayName("Test 8:Verify Delete User Error Open Check")
    @Order(8)
    public void srvTestDeleteUserByIdErrorOpen() {
        when(transactionRepository.findCountOfOpenTransactionsByUser(user.getUserId())).thenReturn(1);
        when(transactionRepository.findCountOfAllTransactionsByUser(user.getUserId())).thenReturn(0);
        userService.deleteUserById(user.getUserId());
        verify(userRepository, times(0)).deleteById(user.getUserId());
    }

    @Test
    @DisplayName("Test 9:Verify Delete User Error All Check")
    @Order(9)
    public void srvTestDeleteUserByIdErrorAll() {
        when(transactionRepository.findCountOfOpenTransactionsByUser(user.getUserId())).thenReturn(0);
        when(transactionRepository.findCountOfAllTransactionsByUser(user.getUserId())).thenReturn(1);
        userService.deleteUserById(user.getUserId());
        verify(userRepository, times(0)).deleteById(user.getUserId());
    }

    @Test
    @DisplayName("Test 10:Verify Authenticate User Check")
    @Order(10)
    public void srvTestAuthenticateUserSuccess() {
        user.setPassword("abcd1234");
        user.setType('A');

        Mockito.when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        String userState = userService.authenticate(user.getUserId(), user.getPassword());
        String result = userState.split("-")[0];
        String userInfo = userState.split("-")[1];
        String userId = userState.split("-")[2];
        String userType = userState.split("-")[3];

        //Verify
        Assertions.assertThat(userState).isNotNull();
        Assertions.assertThat(Utils.isStringBlank(userState)).isFalse();

        Assertions.assertThat(result).isEqualTo("success");
        Assertions.assertThat(userInfo).isEqualTo(user.getFirstName().charAt(0) + "" +
                user.getLastName().charAt(0));
        Assertions.assertThat(userId).isEqualTo(String.valueOf(user.getUserId()));
        Assertions.assertThat(userType).isEqualTo(String.valueOf(user.getType()));
    }

    @Test
    @DisplayName("Test 11: Verify Authenticate User Invalid Password Check")
    @Order(11)
    public void srvTestAuthenticateUserInvalidPassword() {
        user.setPassword("mysecurePassword");
        Mockito.when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        String userState = userService.authenticate(user.getUserId(), "Wrong_Password");
        String result = userState.split("-")[0];

        //Verify
        Assertions.assertThat(userState).isNotNull();
        Assertions.assertThat(Utils.isStringBlank(userState)).isFalse();

        Assertions.assertThat(result).isEqualTo("invalid_password");
    }

    @Test
    @DisplayName("Test 12:Verify Authenticate User Not Found Check")
    @Order(12)
    public void srvTestAuthenticateUserNotFound() {
        user.setUserId(9999);
        user.setPassword("abcd1234");

        String userState = userService.authenticate(user.getUserId(), user.getPassword());
        String result = userState.split("-")[0];

        //Verify
        Assertions.assertThat(userState).isNotNull();
        Assertions.assertThat(Utils.isStringBlank(userState)).isFalse();

        Assertions.assertThat(result).isEqualTo("user_not_found");
    }

    @Test
    @DisplayName("Test 13:Verify BCrypt Password Hasher Match Check")
    @Order(13)
    public void srvTestBCryptPasswordHasherMatch() {
        String rawPassword = "abcd1234";
        String hashedPassword = BCryptPasswordHasher.hashPassword(rawPassword);
        System.out.println("BCrypt Hashed Password: " + hashedPassword);

        boolean isMatch = BCryptPasswordHasher.verifyPassword(rawPassword, hashedPassword);

        //Verify
        Assertions.assertThat(hashedPassword).isNotNull();
        Assertions.assertThat(Utils.isStringBlank(hashedPassword)).isFalse();
        Assertions.assertThat(isMatch).isTrue();
    }

    @Test
    @DisplayName("Test 14:Verify BCrypt Password Hasher No Match Check")
    @Order(14)
    public void srvTestBCryptPasswordHasherNotMatch() {
        String rawPassword = "abcd1234";
        String hashedPassword = BCryptPasswordHasher.hashPassword(rawPassword);
        System.out.println("BCrypt Hashed Password: " + hashedPassword);

        boolean isMatch = BCryptPasswordHasher.verifyPassword("Wrong_Password", hashedPassword);

        //Verify
        Assertions.assertThat(hashedPassword).isNotNull();
        Assertions.assertThat(Utils.isStringBlank(hashedPassword)).isFalse();
        Assertions.assertThat(isMatch).isFalse();
    }
}