package com.sb.lms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Data / Model / Value Object to store BookInfo Entity
 * @author Saarah Bedekar
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "Bookinfo")
@Data // Generates getters, setters, toString, equals, and hashCode methods.
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BookInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookinfo_id")
    private Integer bookInfoId;

    @Column(name = "title")
    protected String title;

    @Column(name = "author")
    private String author;

    @Column(name = "genre")
    private String genre;

    @Column(name = "category")
    private String category;

    @Column(name = "isbn")
    private String isbn;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "price")
    private Double price;

    @Column(name = "total_quantity")
    private Integer totalQuantity = 0;

    @Transient
    private LmsFault fault;

    // Will be called when adding a new associated book
    public void incrementTotalQuantity() {
        ++totalQuantity;
    }

    // Will be called when deleting an existing associated book
    public void decrementTotalQuantity() {
        --totalQuantity;
    }
}