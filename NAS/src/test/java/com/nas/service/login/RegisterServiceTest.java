package com.nas.service.login;

import com.nas.communicationsecurity.service.JwtService;
import com.nas.persistence.dto.RegisterDTO;
import com.nas.persistence.model.User;
import com.nas.persistence.repository.RoleRepository;
import com.nas.persistence.repository.UserRepository;
import com.nas.persistence.repository.VerificationTokenRepository;
import com.nas.proxy.MailSenderServiceProxy;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import static org.junit.Assert.assertEquals;

class RegisterServiceTest {

    private static UserRepository userRepository;
    private static RoleRepository roleRepository;
    private static MailSenderServiceProxy mailSenderServiceProxy;
    private static JwtService jwtService;
    private static VerificationTokenRepository verificationTokenRepository;

    @BeforeAll
    private static void beforeAll() {
        userRepository = Mockito.mock(UserRepository.class);
        roleRepository = Mockito.mock(RoleRepository.class);
        mailSenderServiceProxy = Mockito.mock(MailSenderServiceProxy.class);
        jwtService = Mockito.mock(JwtService.class);
        verificationTokenRepository = Mockito.mock(VerificationTokenRepository.class);
    }

    @Test
    @SneakyThrows
    void testLoadUserByUsername() {
        RegisterService  service = new RegisterService(userRepository, roleRepository, mailSenderServiceProxy, jwtService, verificationTokenRepository);
        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(new User());
        service.registerUser(new RegisterDTO("","","",""));
        assertEquals(HttpStatus.NOT_FOUND, service.registerUser(new RegisterDTO("","","","")).getStatusCode());
    }
}
