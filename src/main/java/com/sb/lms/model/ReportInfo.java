package com.sb.lms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Data / Model / Value Object to store ReportInfo Entity
 * @author Saarah Bedekar
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "Reportinfo")
@Data // Generates getters, setters, toString, equals, and hashCode methods.
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ReportInfo {
    @Id
    @Column(name = "reportinfo_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reportInfoId;

    @Column(name = "name")
    private String name;

    @Column(name = "sql_statement")
    private String sqlStatement;

    @Column(name = "time_to_generate")
    private String timeToGenerate;

    @Transient
    private LmsFault fault;

}