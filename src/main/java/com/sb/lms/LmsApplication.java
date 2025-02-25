package com.sb.lms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main class of this LMS Spring boot application
 * This class tells Springboot that this is a Spring Boot application
 * and it starts Tomcat (Web) container and services required
 * @author Saarah Bedekar
 */
@SpringBootApplication
//@EnableSwagger2
public class LmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(LmsApplication.class, args);
	}
}




	/*
	@Bean
	public Docket swaggerConfiguration() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.sb"))
				.paths(PathSelectors.ant("/lms/"))
				.build();
				//.apiInfo(apiDetails());
	}


	private ApiInfo apiDetails() {
		return new ApiInfo(
			"LMS",
			"Library Management System",
			"1.0",
			"Free to use",
			new springfox.documentation.service.Contact("Saarah Bedekar","http://myurl","a.b@c.com"),
			"API License"
		);

	}

	@GetMapping("/hello")
	public String sayHello(@RequestParam(value = "myName", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}
 	*/

