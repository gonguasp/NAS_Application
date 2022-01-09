package com.nas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nas.communicationsecurity.service.JwtService;
import com.nas.persistence.model.User;
import com.nas.persistence.model.UserView;
import com.nas.persistence.repository.*;
import com.nas.service.SettingsService;
import com.nas.service.TransferFilesService;
import com.nas.service.login.ForgotPasswordService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

class SettingsControllerTest {

    private static SettingsService settingsService ;
    private static TransferFilesService transferFilesService;
    private static UserViewRepository userViewRepository;
    private static UserRepository userRepository;
    private static VerificationTokenRepository verificationTokenRepository;
    private static FileRepository fileRepository;
    private static RoleRepository roleRepository;
    private static ObjectMapper objectMapper;
    private static JwtService jwtService;
    private static ForgotPasswordService forgotPasswordService;



    @BeforeAll
    private static void beforeAll() {
        userViewRepository = Mockito.mock(UserViewRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        verificationTokenRepository = Mockito.mock(VerificationTokenRepository.class);
        fileRepository = Mockito.mock(FileRepository.class);
        roleRepository = Mockito.mock(RoleRepository.class);
        objectMapper = Mockito.mock(ObjectMapper.class);
        jwtService = Mockito.mock(JwtService.class);
        forgotPasswordService = Mockito.mock(ForgotPasswordService.class);
        transferFilesService = Mockito.mock(TransferFilesService.class);
    }

    @Test
    @SneakyThrows
    void showUploadPageTest() {
        settingsService = new SettingsService(userViewRepository, userRepository, verificationTokenRepository, fileRepository, roleRepository, objectMapper, jwtService, forgotPasswordService);
        Mockito.doNothing().when(transferFilesService).loadVariablesToView(Mockito.any(), Mockito.anyBoolean(), Mockito.any());
        SettingsController settingsController = new SettingsController(settingsService, transferFilesService);

        assertEquals("fragments/settings", settingsController.showUploadPage(new ModelMap()));
    }

    @Test
    @SneakyThrows
    void showUploadPageOverLoadTest() {
        UserView userView = new UserView();
        userView.setRoles("ADMIN");
        Mockito.when(jwtService.validateToken(Mockito.anyString())).thenReturn(userView);
        Mockito.when(userViewRepository.findAll()).thenReturn(new ArrayList<>());
        settingsService = new SettingsService(userViewRepository, userRepository, verificationTokenRepository, fileRepository, roleRepository, objectMapper, jwtService, forgotPasswordService);
        SettingsController settingsController = new SettingsController(settingsService, transferFilesService);

        assertNotNull(settingsController.showUploadPage(""));
    }

    @Test
    @SneakyThrows
    void getUserJwtAdminTest() {
        UserView userView = new UserView();
        userView.setRoles("ADMIN");
        Mockito.when(jwtService.validateToken(Mockito.anyString())).thenReturn(userView);
        Mockito.when(userViewRepository.findByEmail(Mockito.anyString())).thenReturn(userView);
        Mockito.when(jwtService.generateToken(Mockito.any())).thenReturn("test");
        settingsService = new SettingsService(userViewRepository, userRepository, verificationTokenRepository, fileRepository, roleRepository, objectMapper, jwtService, forgotPasswordService);
        SettingsController settingsController = new SettingsController(settingsService, transferFilesService);

        assertEquals("test", settingsController.getUserJwt("", ""));
    }

    @Test
    @SneakyThrows
    void getUserJwtNoAdminTest() {
        UserView userView = new UserView();
        userView.setRoles("USER");
        Mockito.when(jwtService.validateToken(Mockito.anyString())).thenReturn(userView);
        settingsService = new SettingsService(userViewRepository, userRepository, verificationTokenRepository, fileRepository, roleRepository, objectMapper, jwtService, forgotPasswordService);
        SettingsController settingsController = new SettingsController(settingsService, transferFilesService);

        assertEquals("", settingsController.getUserJwt("", ""));
    }

    @Test
    @SneakyThrows
    void deleteUserTest() {
        UserView userView = new UserView();
        userView.setRoles("ADMIN");
        Mockito.when(jwtService.validateToken(Mockito.anyString())).thenReturn(userView);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));
        List<Integer> userIds = new ArrayList<>();
        userIds.add(1);
        userIds.add(2);
        userIds.add(3);
        Mockito.when(objectMapper.readValue(Mockito.anyString(), (Class<Object>) Mockito.any())).thenReturn(userIds);
        Mockito.doNothing().when(fileRepository).deleteByUser(Mockito.any());
        Mockito.doNothing().when(roleRepository).deleteByUser(Mockito.any());
        Mockito.doNothing().when(verificationTokenRepository).deleteByUser(Mockito.any());
        Mockito.doNothing().when(userRepository).deleteById(Mockito.anyLong());
        settingsService = new SettingsService(userViewRepository, userRepository, verificationTokenRepository, fileRepository, roleRepository, objectMapper, jwtService, forgotPasswordService);
        SettingsController settingsController = new SettingsController(settingsService, transferFilesService);

        settingsController.deleteUsers("", "");
    }

    @Test
    @SneakyThrows
    void resetUsersPasswordTest() {
        UserView userView = new UserView();
        userView.setRoles("ADMIN");
        Mockito.when(jwtService.validateToken(Mockito.anyString())).thenReturn(userView);
        List<Integer> userIds = new ArrayList<>();
        userIds.add(1);
        userIds.add(2);
        userIds.add(3);
        Mockito.when(objectMapper.readValue(Mockito.anyString(), (Class<Object>) Mockito.any())).thenReturn(userIds);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));
        Mockito.when(forgotPasswordService.forgotPassword(Mockito.any())).thenReturn(ResponseEntity.ok().build());
        settingsService = new SettingsService(userViewRepository, userRepository, verificationTokenRepository, fileRepository, roleRepository, objectMapper, jwtService, forgotPasswordService);
        SettingsController settingsController = new SettingsController(settingsService, transferFilesService);

        settingsController.resetUsersPassword("", "");
    }

    @Test
    @SneakyThrows
    void changeUserThemeTest() {
        UserView userView = new UserView();
        Mockito.when(jwtService.validateToken(Mockito.anyString())).thenReturn(userView);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(new User());
        settingsService = new SettingsService(userViewRepository, userRepository, verificationTokenRepository, fileRepository, roleRepository, objectMapper, jwtService, forgotPasswordService);
        SettingsController settingsController = new SettingsController(settingsService, transferFilesService);

        settingsController.changeUserTheme("", "true");
    }
}
