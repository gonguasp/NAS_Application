package com.nas.controller;

import com.nas.configuration.SecurityConfiguration;
import com.nas.configuration.error.CustomErrorController;
import com.nas.configuration.error.CustomizedResponseEntityExceptionHandler;
import com.nas.persistence.repository.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CustomErrorControllerTest {

    private static UserRepository userRepository;
    private static SecurityConfiguration securityConfiguration;
    private static CustomizedResponseEntityExceptionHandler customizedResponseEntityExceptionHandler;

    @BeforeAll
    private static void beforeAll() {
        securityConfiguration = Mockito.mock(SecurityConfiguration.class);
        userRepository = Mockito.mock(UserRepository.class);
        customizedResponseEntityExceptionHandler = new CustomizedResponseEntityExceptionHandler(userRepository, securityConfiguration);
    }

    @Test
    void testHandleError401() {
        CustomErrorController customErrorController = new CustomErrorController(customizedResponseEntityExceptionHandler);

        WebRequest webRequest = Mockito.mock(WebRequest.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        Mockito.when(request.getAttribute(Mockito.anyString())).thenReturn("401");
        Mockito.when(webRequest.getDescription(Mockito.anyBoolean())).thenReturn("");

        assertNotNull(customErrorController.handleError(webRequest, request));
    }

    @Test
    void testHandleError404() {
        CustomErrorController customErrorController = new CustomErrorController(customizedResponseEntityExceptionHandler);

        WebRequest webRequest = Mockito.mock(WebRequest.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        Mockito.when(request.getAttribute(Mockito.anyString())).thenReturn("404");
        Mockito.when(webRequest.getDescription(Mockito.anyBoolean())).thenReturn("");
        Mockito.when(securityConfiguration.isLogged()).thenReturn(false);

        assertNotNull(customErrorController.handleError(webRequest, request));
    }
}
