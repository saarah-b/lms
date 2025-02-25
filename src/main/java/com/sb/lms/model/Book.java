package com.sb.lms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Data / Model / Value Object to store Book Entity
 * @author Saarah Bedekar
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "Book")
@Data // Generates getters, setters, toString, equals, and hashCode methods.
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Integer bookId;

    @ManyToOne
    @JoinColumn(name = "bookinfo_id")
    private BookInfo bookInfo;

    @Column(name = "shelf_reference")
    private String shelfReference;

    @Column(name = "location")
    private String location;

    @Column(name = "edition")
    private String edition;

    @Column(name = "available")
    private Boolean available = true;

    @Transient
    private LmsFault fault;

    @Override
    public String toString() {
        //System.out.println("Book details = " + this);
        return "Book {" +
                "bookID=" + bookId +
                "bookTile=" + bookInfo.getTitle() +
                ", shelfReference='" + shelfReference + '\'' +
                ", location='" + location + '\'' +
                ", edition='" + edition + '\'' +
                ", available='" + available + '\'' +
                '}';
    }
}
