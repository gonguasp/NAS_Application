package com.nas.mail.service;

import com.nas.persistence.model.Parameter;
import com.nas.persistence.repository.ParameterRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.internet.MimeMessage;

class SendEmailServiceTest {

    @Test
    void testOnApplicationEventRegiser() {
        JavaMailSenderImpl mailSender = Mockito.mock(JavaMailSenderImpl.class);
        ParameterRepository parameterRepository = Mockito.mock(ParameterRepository.class);
        SendEmailService sendEmailService = new SendEmailService(mailSender, parameterRepository);

        Mockito.when(mailSender.createMimeMessage()).thenReturn(Mockito.mock(MimeMessage.class));
        Mockito.doNothing().when(mailSender).send((MimeMessage) Mockito.any());
        Mockito.when(parameterRepository.findByName(Mockito.anyString())).thenReturn(new Parameter());

        sendEmailService.sendEmail("target", "subject", "body");
    }

    @Test
    void testOnApplicationEventRegiserException() {
        JavaMailSenderImpl mailSender = Mockito.mock(JavaMailSenderImpl.class);
        ParameterRepository parameterRepository = Mockito.mock(ParameterRepository.class);
        SendEmailService sendEmailService = new SendEmailService(mailSender, parameterRepository);

        Mockito.when(mailSender.createMimeMessage()).thenReturn(Mockito.mock(MimeMessage.class));
        Mockito.doThrow(new MailAuthenticationException("")).when(mailSender).send((MimeMessage) Mockito.any());
        Mockito.when(parameterRepository.findByName(Mockito.anyString())).thenReturn(new Parameter());

        sendEmailService.sendEmail("target", "subject", "body");
    }
}
