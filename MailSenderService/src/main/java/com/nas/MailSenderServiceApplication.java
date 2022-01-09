package com.nas;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
@OpenAPIDefinition
public class MailSenderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MailSenderServiceApplication.class, args);
    }

}
