/*package com.nas.mail.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class PropertiesConfig implements EnvironmentPostProcessor {

    private static final String PROPERTY_SOURCE_NAME = "application";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> username = new HashMap<>();
        username.put("spring.mail.username", "springbootvalidation@gmail.com");
        environment.getPropertySources().addLast(new MapPropertySource(PROPERTY_SOURCE_NAME, username));

        Map<String, Object> password = new HashMap<>();
        password.put("spring.mail.password", "0060bjmE");
        environment.getPropertySources().addLast(new MapPropertySource(PROPERTY_SOURCE_NAME, password));
    }
}*/
