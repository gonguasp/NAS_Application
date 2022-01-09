package com.nas.mail.listener;

import com.nas.mail.event.PreAuthenticationEvent;
import com.nas.mail.service.SendEmailService;
import com.nas.persistence.dto.OperationDTO;
import com.nas.persistence.model.User;
import com.nas.persistence.model.VerificationToken;
import com.nas.persistence.repository.VerificationTokenRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PreAuthenticationListenerTest {

    private static VerificationTokenRepository verificationTokenRepository;
    private static SendEmailService sendEmailService;

    @BeforeAll
    private static void beforeAll() {
        verificationTokenRepository = Mockito.mock(VerificationTokenRepository.class);
        sendEmailService = Mockito.mock(SendEmailService.class);
    }

    @Test
    void testOnApplicationEventRegiser() {
        PreAuthenticationListener listener = new PreAuthenticationListener(verificationTokenRepository, sendEmailService);
        PreAuthenticationEvent event = Mockito.mock(PreAuthenticationEvent.class);

        Mockito.when(verificationTokenRepository.save(Mockito.any())).thenReturn(new VerificationToken());
        Mockito.when(event.getOperation()).thenReturn(OperationDTO.types.REGISTER.toString());
        Mockito.when(event.getUser()).thenReturn(new User());
        Mockito.doNothing().when(sendEmailService).sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        listener.onApplicationEvent(event);
    }

    @Test
    void testOnApplicationEventRegiserResetPassword() {
        PreAuthenticationListener listener = new PreAuthenticationListener(verificationTokenRepository, sendEmailService);
        PreAuthenticationEvent event = Mockito.mock(PreAuthenticationEvent.class);

        Mockito.when(verificationTokenRepository.save(Mockito.any())).thenReturn(new VerificationToken());
        Mockito.when(event.getOperation()).thenReturn(OperationDTO.types.RESET_PASSWORD.toString());
        Mockito.when(event.getUser()).thenReturn(new User());
        Mockito.doNothing().when(sendEmailService).sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        listener.onApplicationEvent(event);
    }
}
