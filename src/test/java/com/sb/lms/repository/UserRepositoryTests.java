package com.sb.lms.repository;

import com.sb.lms.model.Address;
import com.sb.lms.model.Book;
import com.sb.lms.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserRepositoryTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    User user;

    @BeforeEach
    public void setup() {
        user = User.builder().userId(1).firstName("Saarah").lastName("Bedekar")
                .email("a.b@c.com").mobileNumber("07894444555").build();
    }

    @Test
    @DisplayName("Test 1:Verify User By Id Retrieval Check")
    @Order(1)
    public void repTestGetUserById() {
        // arrange
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        // action
        User userDB = userRepository.findById(user.getUserId()).orElse(null);

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
    @DisplayName("Test 3:Verify User By Name Retrieval Check")
    @Order(3)
    public void repTestGetUserByName() {
        String name = "ede";
        List<User> userList = new ArrayList<>();
        userList.add(user);
        User user2 = User.builder().userId(2).firstName("Sam").lastName("Bedekar")
                .email("sam_curran@gmail.com").build();
        userList.add(user2);

        when(userRepository.findUserByName(name)).thenReturn(userList);

        List<User> userDBList = userRepository.findUserByName(name);
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
    @DisplayName("Test 5:Verify Add User Check")
    @Order(5)
    public void repTestAddUserSuccess() {

        Address address = Address.builder().addressId(1).build();
        user.setAddress(address);

        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        when(addressRepository.findById(1)).thenReturn(Optional.ofNullable(address));

        // Act
        User userDB = userRepository.save(user);

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
    public void repTestUpdateUserSuccess() {
        // Arrange
        User user2 = User.builder().userId(1).email("a.b@c.com").mobileNumber("07894444555").build();

        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        // Act
        User userDB = userRepository.save(user2);

        // Assert
        Assertions.assertThat(userDB).isNotNull(); // Assert the User returned is not null
        Assertions.assertThat(userDB).isInstanceOf(User.class); // Assert the method returns User instance
        Assertions.assertThat(userDB.getEmail()).isEqualTo(user2.getEmail());
        Assertions.assertThat(userDB.getMobileNumber()).isEqualTo(user2.getMobileNumber());

        // Verify that save was called
        verify(userRepository, times(1)).save(user2);

    }

    @Test
    @DisplayName("Test 7:Verify Delete User Check")
    @Order(7)
    public void repTestDeleteUserById() {
        doNothing().when(userRepository).deleteById(user.getUserId());
        userRepository.deleteById(user.getUserId());
        verify(userRepository, times(1)).deleteById(user.getUserId());

        User deletedUser = userRepository.findById(user.getUserId()).orElse(null);
        //Verify
        Assertions.assertThat(deletedUser).isNull();
    }
}