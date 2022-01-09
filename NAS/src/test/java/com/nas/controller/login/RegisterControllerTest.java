package com.nas.controller.login;

import com.nas.communicationsecurity.service.JwtService;
import com.nas.persistence.dto.OperationDTO;
import com.nas.persistence.dto.RegisterDTO;
import com.nas.persistence.model.Role;
import com.nas.persistence.model.User;
import com.nas.persistence.model.VerificationToken;
import com.nas.persistence.repository.RoleRepository;
import com.nas.persistence.repository.UserRepository;
import com.nas.persistence.repository.VerificationTokenRepository;
import com.nas.proxy.MailSenderServiceProxy;
import com.nas.service.login.RegisterService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegisterControllerTest {

    private static MailSenderServiceProxy mailSenderServiceProxy;
    private static UserRepository userRepository;
    private static VerificationTokenRepository verificationTokenRepository;
    private static RoleRepository roleRepository;
    private static JwtService jwtService;
    private static RegisterService registerService;
    private static ModelMap modelMap;

    @BeforeAll
    private static void beforeAll() {
        mailSenderServiceProxy = Mockito.mock(MailSenderServiceProxy.class);
        userRepository = Mockito.mock(UserRepository.class);
        verificationTokenRepository = Mockito.mock(VerificationTokenRepository.class);
        roleRepository = Mockito.mock(RoleRepository.class);
        jwtService = Mockito.mock(JwtService.class);
        registerService = new RegisterService(userRepository, roleRepository, mailSenderServiceProxy, jwtService, verificationTokenRepository);
        modelMap = Mockito.mock(ModelMap.class);
        Mockito.when(modelMap.put(Mockito.anyString(), Mockito.any())).thenReturn(null);
    }

    @Test
    void testShowForgotPasswordPageOnlyFragment() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        RegisterController controller = new RegisterController(registerService);
        assertEquals("fragments/login/register", controller.showRegisterPage(modelMap,"only-fragment"));
    }

    @Test
    void testShowForgotPasswordPage() {
        LocaleContextHolder.setLocale(Locale.GERMANY);
        RegisterController controller = new RegisterController(registerService);
        assertEquals("login/register", controller.showRegisterPage(modelMap,""));
    }

    @Test
    @SneakyThrows
    void testRegisterUser() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        RegisterController controller = new RegisterController(registerService);

        User user = new User();
        List<Role> roles = new ArrayList<>();
        Role role = new Role();
        role.setRole("USER_ROLE");
        roles.add(role);
        user.setRoles(roles);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
        Mockito.when(roleRepository.save(Mockito.any())).thenReturn(new Role());
        Mockito.when(mailSenderServiceProxy.sendEmail(Mockito.any(), Mockito.anyString())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        assertEquals(HttpStatus.OK, controller.registerUser(modelMap, new RegisterDTO("", "", "", "")).getStatusCode());
    }

    @Test
    @SneakyThrows
    void testRegisterUserSendEmailException() {
        LocaleContextHolder.setLocale(Locale.GERMANY);
        RegisterController controller = new RegisterController(registerService);

        Mockito.when(userRepository.save(Mockito.any())).thenReturn(new User());
        Mockito.when(roleRepository.save(Mockito.any())).thenReturn(new Role());
        Mockito.when(mailSenderServiceProxy.sendEmail(Mockito.any(), Mockito.anyString())).thenThrow(new RuntimeException());

        assertThrows(Exception.class, () -> {
            assertEquals(HttpStatus.OK, controller.registerUser(modelMap, new RegisterDTO("", "", "", "")).getStatusCode());
        });
    }

    @Test
    @SneakyThrows
    void testRegisterUserPasswordMismatch() {
        LocaleContextHolder.setLocale(Locale.GERMANY);
        RegisterController controller = new RegisterController(registerService);
        assertEquals(HttpStatus.CONFLICT, controller.registerUser(modelMap, new RegisterDTO("", "", "", "1")).getStatusCode());
    }

    @Test
    @SneakyThrows
    void testActivateAccount() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        RegisterController controller = new RegisterController(registerService);
        VerificationToken vt = new VerificationToken();
        vt.setUsed(false);
        vt.setUser(new User());
        vt.setOperation(OperationDTO.types.REGISTER.toString());
        vt.setExpiryDate(Instant.ofEpochMilli(System.currentTimeMillis() * 2));

        Mockito.when(verificationTokenRepository.findByToken(Mockito.anyString())).thenReturn(vt);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(new User());
        Mockito.when(verificationTokenRepository.save(Mockito.any())).thenReturn(vt);

        assertEquals("login/registerConfirm", controller.activateAccount(modelMap, "1"));
    }

    @Test
    @SneakyThrows
    void testActivateAccountException() {
        LocaleContextHolder.setLocale(Locale.GERMANY);
        RegisterController controller = new RegisterController(registerService);

        Mockito.when(verificationTokenRepository.findByToken(Mockito.anyString())).thenReturn(null);

        assertThrows(Exception.class, () -> {
            controller.activateAccount(modelMap, "1");
        });
    }
}
