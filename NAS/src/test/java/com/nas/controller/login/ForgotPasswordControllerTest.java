package com.nas.controller.login;

import com.nas.communicationsecurity.service.JwtService;
import com.nas.configuration.SecurityConfiguration;
import com.nas.persistence.dto.EmailDTO;
import com.nas.persistence.dto.OperationDTO;
import com.nas.persistence.dto.ResetPasswordDTO;
import com.nas.persistence.model.Role;
import com.nas.persistence.model.User;
import com.nas.persistence.model.UserView;
import com.nas.persistence.model.VerificationToken;
import com.nas.persistence.repository.UserRepository;
import com.nas.persistence.repository.VerificationTokenRepository;
import com.nas.proxy.MailSenderServiceProxy;
import com.nas.service.login.ForgotPasswordService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ForgotPasswordControllerTest {

    private static ForgotPasswordService forgotPasswordService;
    private static UserRepository userRepository;
    private static VerificationTokenRepository verificationTokenRepository;
    private static MailSenderServiceProxy mailSenderServiceProxy;
    private static ModelMap modelMap;

    @BeforeAll
    private static void beforeAll() {
        userRepository = Mockito.mock(UserRepository.class);
        verificationTokenRepository = Mockito.mock(VerificationTokenRepository.class);
        mailSenderServiceProxy = Mockito.mock(MailSenderServiceProxy.class);
        SecurityConfiguration securityConfiguration = Mockito.mock(SecurityConfiguration.class);
        Mockito.when(securityConfiguration.isLogged()).thenReturn(true);
        forgotPasswordService = new ForgotPasswordService(
                userRepository,
                mailSenderServiceProxy,
                Mockito.mock(JwtService.class),
                Mockito.mock(PasswordEncoder.class),
                verificationTokenRepository,
                securityConfiguration);
        modelMap = Mockito.mock(ModelMap.class);
        Mockito.when(modelMap.put(Mockito.anyString(), Mockito.any())).thenReturn(null);
    }

    @Test
    void testShowForgotPasswordPageOnlyFragment() {
        LocaleContextHolder.setLocale(Locale.GERMANY);
        ForgotPasswordController controller = new ForgotPasswordController(forgotPasswordService);
        assertEquals("fragments/login/forgotPassword", controller.showForgotPasswordPage(modelMap,"only-fragment"));
    }

    @Test
    void testShowForgotPasswordPage() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        ForgotPasswordController controller = new ForgotPasswordController(forgotPasswordService);
        assertEquals("login/forgotPassword", controller.showForgotPasswordPage(modelMap,""));
    }

    @Test
    void testForgotPasswordUserNotFound() {
        LocaleContextHolder.setLocale(Locale.GERMANY);
        ForgotPasswordController controller = new ForgotPasswordController(forgotPasswordService);

        Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(null);
        assertEquals(HttpStatus.NOT_FOUND, controller.forgotPassword(modelMap, new EmailDTO()).getStatusCode());
    }

    @Test
    void testForgotPassword() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        ForgotPasswordController controller = new ForgotPasswordController(forgotPasswordService);

        User user = new User();
        List<Role> roles = new ArrayList<>();
        Role role = new Role();
        role.setRole("USER_ROLE");
        roles.add(role);
        user.setRoles(roles);
        Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(user);
        Mockito.when(mailSenderServiceProxy.sendEmail(Mockito.any(), Mockito.anyString())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        assertEquals(HttpStatus.OK, controller.forgotPassword(modelMap, new EmailDTO()).getStatusCode());
    }

    @Test
    @SneakyThrows
    void testShowResetPasswordPage() {
        LocaleContextHolder.setLocale(Locale.GERMANY);
        ForgotPasswordController controller = new ForgotPasswordController(forgotPasswordService);
        VerificationToken vt = new VerificationToken();
        vt.setUsed(false);
        vt.setOperation(OperationDTO.types.RESET_PASSWORD.toString());
        vt.setExpiryDate(Instant.ofEpochMilli(System.currentTimeMillis() * 2));

        Mockito.when(verificationTokenRepository.findByToken(Mockito.anyString())).thenReturn(vt);
        assertEquals("login/resetPassword", controller.showResetPasswordPage(Mockito.mock(Model.class), ""));
    }

    @Test
    @SneakyThrows
    void testShowResetPasswordPageFragment() {
        LocaleContextHolder.setLocale(Locale.GERMANY);
        SecurityConfiguration securityConfiguration = Mockito.mock(SecurityConfiguration.class);
        Mockito.when(securityConfiguration.isLogged()).thenReturn(true);
        forgotPasswordService.setSecurityConfiguration(securityConfiguration);
        ForgotPasswordController controller = new ForgotPasswordController(forgotPasswordService);
        VerificationToken vt = new VerificationToken();
        vt.setUsed(false);
        vt.setOperation(OperationDTO.types.RESET_PASSWORD.toString());
        vt.setExpiryDate(Instant.ofEpochMilli(System.currentTimeMillis() * 2));

        Mockito.when(verificationTokenRepository.findByToken(Mockito.anyString())).thenReturn(vt);
        assertEquals("fragments/login/resetPassword", controller.showResetPasswordPage(Mockito.mock(Model.class), ""));
    }

    @Test
    @SneakyThrows
    void testShowResetPasswordPageNotFoundVerificationToken() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        SecurityConfiguration securityConfiguration = Mockito.mock(SecurityConfiguration.class);
        Mockito.when(securityConfiguration.isLogged()).thenReturn(false);
        forgotPasswordService.setSecurityConfiguration(securityConfiguration);
        ForgotPasswordController controller = new ForgotPasswordController(forgotPasswordService);

        Mockito.when(verificationTokenRepository.findByToken(Mockito.anyString())).thenReturn(null);

        assertThrows(Exception.class, () -> {
            controller.showResetPasswordPage(Mockito.mock(Model.class), "");
        });
    }

    @Test
    @SneakyThrows
    void testShowResetPasswordWithVerificationToken() {
        SecurityConfiguration securityConfiguration = Mockito.mock(SecurityConfiguration.class);
        Mockito.when(securityConfiguration.isLogged()).thenReturn(false);
        forgotPasswordService.setSecurityConfiguration(securityConfiguration);
        LocaleContextHolder.setLocale(Locale.GERMANY);
        ForgotPasswordController controller = new ForgotPasswordController(forgotPasswordService);
        VerificationToken vt = new VerificationToken();
        vt.setUsed(false);
        vt.setOperation(OperationDTO.types.RESET_PASSWORD.toString());
        vt.setExpiryDate(Instant.ofEpochMilli(System.currentTimeMillis() * 2));
        vt.setUser(new User());

        Mockito.when(verificationTokenRepository.findByToken(Mockito.anyString())).thenReturn(vt);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(new User());
        Mockito.when(verificationTokenRepository.save(Mockito.any())).thenReturn(vt);

        assertEquals(HttpStatus.NO_CONTENT, controller.resetPassword(modelMap, new ResetPasswordDTO("", "1", "1", "")).getStatusCode());
        vt.setUsed(false);
        assertEquals(HttpStatus.CONFLICT, controller.resetPassword(modelMap, new ResetPasswordDTO("", "1", "2", "")).getStatusCode());
    }

    @Test
    @SneakyThrows
    void testShowResetPassword() {
        SecurityConfiguration securityConfiguration = Mockito.mock(SecurityConfiguration.class);
        Mockito.when(securityConfiguration.isLogged()).thenReturn(false);
        forgotPasswordService.setSecurityConfiguration(securityConfiguration);
        LocaleContextHolder.setLocale(Locale.GERMANY);
        ForgotPasswordController controller = new ForgotPasswordController(forgotPasswordService);
        VerificationToken vt = new VerificationToken();
        vt.setUsed(false);
        vt.setOperation(OperationDTO.types.RESET_PASSWORD.toString());
        vt.setExpiryDate(Instant.ofEpochMilli(System.currentTimeMillis() * 2));
        vt.setUser(new User());

        Mockito.when(verificationTokenRepository.findByToken(Mockito.anyString())).thenReturn(vt);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(new User());
        Mockito.when(verificationTokenRepository.save(Mockito.any())).thenReturn(vt);

        assertEquals(HttpStatus.NO_CONTENT, controller.resetPassword(modelMap, new ResetPasswordDTO("", "", "", "")).getStatusCode());
    }

    @Test
    @SneakyThrows
    void testShowResetPasswordDifferentPasswords() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        ForgotPasswordController controller = new ForgotPasswordController(forgotPasswordService);
        VerificationToken vt = new VerificationToken();
        vt.setUsed(false);
        vt.setOperation(OperationDTO.types.RESET_PASSWORD.toString());
        vt.setExpiryDate(Instant.ofEpochMilli(System.currentTimeMillis() * 2));
        vt.setUser(new User());

        Mockito.when(verificationTokenRepository.findByToken(Mockito.anyString())).thenReturn(vt);

        assertEquals(HttpStatus.CONFLICT, controller.resetPassword(modelMap, new ResetPasswordDTO("", "1", "", "")).getStatusCode());
    }

    @Test
    @SneakyThrows
    void testResetPasswordNotFoundVerificationToken() {
        SecurityConfiguration securityConfiguration = Mockito.mock(SecurityConfiguration.class);
        Mockito.when(securityConfiguration.isLogged()).thenReturn(false);
        forgotPasswordService.setSecurityConfiguration(securityConfiguration);
        ForgotPasswordController controller = new ForgotPasswordController(forgotPasswordService);

        Mockito.when(verificationTokenRepository.findByToken(Mockito.anyString())).thenReturn(null);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, controller.resetPassword(modelMap, new ResetPasswordDTO("", "", "", "")).getStatusCode());
    }

    @Test
    @SneakyThrows
    void testresetUserpasswordUserLoggedConflict() {
        SecurityConfiguration securityConfiguration = Mockito.mock(SecurityConfiguration.class);
        Mockito.when(securityConfiguration.isLogged()).thenReturn(true);
        forgotPasswordService.setSecurityConfiguration(securityConfiguration);
        JwtService jwtService = Mockito.mock(JwtService.class);
        Mockito.when(jwtService.validateToken(Mockito.anyString())).thenReturn(new UserView());
        forgotPasswordService.setJwtService(jwtService);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.any())).thenReturn(true);
        forgotPasswordService.setPasswordEncoder(passwordEncoder);
        ForgotPasswordController controller = new ForgotPasswordController(forgotPasswordService);

        assertEquals(HttpStatus.CONFLICT, controller.resetPassword(modelMap, new ResetPasswordDTO("", "", "1", "")).getStatusCode());

        Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.any())).thenReturn(false);
        forgotPasswordService.setPasswordEncoder(passwordEncoder);

        assertEquals(HttpStatus.CONFLICT, controller.resetPassword(modelMap, new ResetPasswordDTO("", "", "1", "")).getStatusCode());
    }

    @Test
    @SneakyThrows
    void testresetUserpasswordUserLoggedNoContent() {
        SecurityConfiguration securityConfiguration = Mockito.mock(SecurityConfiguration.class);
        Mockito.when(securityConfiguration.isLogged()).thenReturn(true);
        forgotPasswordService.setSecurityConfiguration(securityConfiguration);
        JwtService jwtService = Mockito.mock(JwtService.class);
        Mockito.when(jwtService.validateToken(Mockito.anyString())).thenReturn(new UserView());
        forgotPasswordService.setJwtService(jwtService);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.any())).thenReturn(true);
        forgotPasswordService.setPasswordEncoder(passwordEncoder);

        ForgotPasswordController controller = new ForgotPasswordController(forgotPasswordService);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(new User());

        assertEquals(HttpStatus.NO_CONTENT, controller.resetPassword(modelMap, new ResetPasswordDTO("", "1", "1", "")).getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, controller.resetPassword(modelMap, new ResetPasswordDTO("", "", "", "")).getStatusCode());
    }
}
