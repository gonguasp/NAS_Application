package com.nas;

import com.nas.controller.*;
import com.nas.controller.login.*;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springdoc.core.SpringDocUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients("com.nas")
@EnableEurekaClient
@OpenAPIDefinition
@SecurityScheme(name = "swagger", scheme = "basic", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class NasApplication {

    public static void main(String[] args) {
        SpringApplication.run(NasApplication.class, args);
        SpringDocUtils.getConfig().addRestControllers(
                LoginController.class, ForgotPasswordController.class, RegisterController.class,
                DownloadFilesController.class, UploadFilesController.class, WelcomeController.class);
    }

}
