package com.sb.lms.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Contains RestTemplate initialise for injection
 * @author Saarah Bedekar
 */
@Configuration
public class AppConfig {

    /**
     * Handles the injection of RestTemplate used for calling an end point from a Service call.
     * @return a new RestTemplate to be injected
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
