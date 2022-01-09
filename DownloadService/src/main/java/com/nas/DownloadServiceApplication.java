package com.nas;

import com.nas.download.controller.DownloadController;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springdoc.core.SpringDocUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@OpenAPIDefinition
@SpringBootApplication
public class DownloadServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DownloadServiceApplication.class, args);
        SpringDocUtils.getConfig().addRestControllers(DownloadController.class);
    }

}
