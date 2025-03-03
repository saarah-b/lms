package com.sb.lms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sb.lms.utils.Utils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * The Data / Model / Value Object to store Transaction Entity
 * @author Saarah Bedekar
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "Transaction")
@Data // Generates getters, setters, toString, equals, and hashCode methods.
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "issue_date")
    private Timestamp issueDate;

    @Column(name = "return_date")
    private Timestamp returnDate;

    @Column(name = "actual_return_date_date")
    private Timestamp actualReturnDate;

    @Column(name = "fine")
    private Double fine = 0.0;

    @Column(name = "returned")
    private Boolean returned = false;

    @Transient
    private LmsFault fault;

    @Transient
    private static final Double FINE_PER_WEEK = 2.0;

    /**
     * Handles the asociation of a User and a Book to this transaction
     */
    public void issueBook(User user, Book book) {
        this.user = user;
        this.book = book;
    }

    /**
     * Handles the calculation of fine if case of delayed book returned
     * @return the calculated fine
     */
    public Double calculateFine() {
        Timestamp tempActualReturnDate = Utils.convertNowToSQLTS();
        Long finedWeeks = calculateWeeksBetweenTimestamps(tempActualReturnDate, returnDate);
        fine = finedWeeks * FINE_PER_WEEK;
        //System.out.println("tempActualReturnDate = " + tempActualReturnDate + ", returnDate = " + returnDate);
        //System.out.println("finedWeeks " + finedWeeks + ", FINE_PER_WEEK " + FINE_PER_WEEK + ", fine = " + fine);
        return fine;
    }

    /**
     * Handles the calculation of number of weeks between the scheduled return date and actual return date
     * @return the calculated number of weeks
     */
    private long calculateWeeksBetweenTimestamps(Timestamp tempActualReturnTS, Timestamp returnTS) {
        // Convert java.sql.Timestamp to LocalDate
        LocalDate tempActualReturnDate = tempActualReturnTS.toLocalDateTime().toLocalDate();
        LocalDate returnDate = returnTS.toLocalDateTime().toLocalDate();

        // Check if tempActualReturnDate is same or earlier than returnDate
        if (!tempActualReturnDate.isAfter(returnDate)) {
            return 0;
        }

        long totalDays = ChronoUnit.DAYS.between(returnDate, tempActualReturnDate);
        long weeks = totalDays / 7;
        long remainingDays = totalDays % 7;
        if (remainingDays > 0)
            weeks++;

        // Calculate the number of weeks between date1 and date2
        return weeks;
    }
}