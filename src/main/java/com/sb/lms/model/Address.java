package com.sb.lms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Data / Model / Value Object to store Address Entity
 * @author Saarah Bedekar
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "Address")
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Address {
    @Id
    @Column(name = "address_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer addressId;

    @Column(name = "door_number")
    private String doorNumber;

    @Column(name = "line1")
    private String line1;

    @Column(name = "line2")
    private String line2;

    @Column(name = "city")
    private String city;

    @Column(name = "postcode")
    private String postcode;

    @Transient
    private String error;

    @Override
    public String toString() {
        return "Address{" +
                "addressId=" + addressId +
                ", doorNumber='" + doorNumber + '\'' +
                ", line1='" + line1 + '\'' +
                ", line2='" + line2 + '\'' +
                ", city='" + city + '\'' +
                ", postcode='" + postcode + '\'' +
                '}';
    }
}
