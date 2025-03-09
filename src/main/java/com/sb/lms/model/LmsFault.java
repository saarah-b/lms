package com.sb.lms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Data / Model / Value Object to store Fault Information
 * @author Saarah Bedekar
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LmsFault {

    private String http;
    private String code;
    private String message;
    private String path;

    @Override
    public String toString() {
        return "LmsFault{" +
                "http failure=" + http +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
