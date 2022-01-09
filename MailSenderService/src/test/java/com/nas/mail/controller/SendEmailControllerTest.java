package com.nas.mail.controller;

import com.nas.communicationsecurity.service.JwtService;
import com.nas.persistence.dto.OperationDTO;
import com.nas.persistence.dto.UserDetailsDTO;
import com.nas.persistence.model.UserView;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SendEmailControllerTest {

    private static ApplicationEventPublisher eventPublisher;
    private static JwtService jwtService;

    @BeforeAll
    private static void beforeAll() {
        eventPublisher = Mockito.mock(ApplicationEventPublisher.class);
        jwtService = Mockito.mock(JwtService.class);
    }

    @Test
    @SneakyThrows
    void testRemoveProcessingFoldersOK() {
        SendEmailController controller = new SendEmailController(eventPublisher, jwtService);

        WebRequest webRequest = Mockito.mock(WebRequest.class);
        Mockito.doNothing().when(eventPublisher).publishEvent(Mockito.any());
        Mockito.when(webRequest.getLocale()).thenReturn(Locale.ENGLISH);
        Mockito.when(jwtService.validateToken(Mockito.anyString())).thenReturn(new UserView());

        assertEquals(new ResponseEntity<>(HttpStatus.NO_CONTENT), controller.sendEmail(webRequest, "jwt", OperationDTO.types.values()[0].toString()));
    }

    @Test
    @SneakyThrows
    void testRemoveProcessingFoldersBadRequest() {
        SendEmailController controller = new SendEmailController(eventPublisher, jwtService);
        assertEquals(new ResponseEntity<>(HttpStatus.BAD_REQUEST), controller.sendEmail(Mockito.mock(WebRequest.class), jwtService.generateToken(new UserDetailsDTO().setUsername("testEmail")), ""));
    }
}
