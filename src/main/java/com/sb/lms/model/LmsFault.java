package com.sb.lms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * The Data / Model / Value Object to store Fault Information
 * @author Saarah Bedekar
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LmsFault {

    private String http;
    private String code;
    private String message;
    private String path;

    public LmsFault() {}
    public LmsFault(String http, String code, String message, String path) {
        this.http = http;
        this.code = code;
        this.message = message;
        this.path = path;
    }

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
