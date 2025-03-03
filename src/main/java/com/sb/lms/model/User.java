package com.sb.lms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.*;

/**
 * The Data / Model / Value Object to store User Entity
 * @author Saarah Bedekar
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "User")
@Data // Generates getters, setters, toString, equals, and hashCode methods.
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class User {
    @Id // Specifies its an ID column
    @Column(name = "user_id") // Name of the column in DB
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Specifies that DB will auto generate this ID
    private Integer userId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)  // Allows setting during insert/update but no reads
    @Column(name = "password")
    @ColumnDefault("qwerty12") // Default value which can be updated later
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "birth")
    private Date birth;

    @Column(name = "type")
    private Character type;

    @Column(name = "last_login")
    private Date lastLogin;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @Transient
    private LmsFault fault;

    @Override
    public String toString() {
        // password not to be shown
        return "User {" +
                "userID=" + userId +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", birth='" + birth + '\'' +
                ", type='" + type + '\'' +
                ", lastLogin='" + lastLogin +
                '}';
    }
}