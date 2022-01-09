package com.nas.controller;

import com.nas.communicationsecurity.service.JwtService;
import com.nas.configuration.SecurityConfiguration;
import com.nas.persistence.dto.UserDetailsDTO;
import com.nas.persistence.model.Role;
import com.nas.persistence.model.User;
import com.nas.persistence.model.UserView;
import com.nas.persistence.repository.FileRepository;
import com.nas.persistence.repository.UserRepository;
import com.nas.service.LoadBalancerService;
import com.nas.service.TransferFilesService;
import com.nas.service.WelcomeService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.ui.ModelMap;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

class WelcomeControllerTest {

    private static UserRepository userRepository;
    private static TransferFilesService uploadFilesService;
    private static JwtService jwtService;
    private static SecurityConfiguration securityConfiguration;

    @BeforeAll
    @SneakyThrows
    private static void beforeAll() {
        securityConfiguration = Mockito.mock(SecurityConfiguration.class);
        Mockito.when(securityConfiguration.getLoggedUser()).thenReturn(new UserDetailsDTO());

        jwtService = Mockito.mock(JwtService.class);
        Mockito.when(jwtService.validateToken(Mockito.any())).thenReturn(new UserView());

        uploadFilesService = new TransferFilesService(
                Mockito.mock(LoadBalancerService.class),
                Mockito.mock(DiscoveryClient.class),
                jwtService,
                securityConfiguration);

        userRepository = Mockito.mock(UserRepository.class);
    }

    @SneakyThrows
    @Test
    void testShowForgotPasswordPageOnlyFragment() {
        User user = new User();
        user.setActiveSince(Instant.now());
        user.setRoles(new ArrayList<>());
        Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(user);
        WelcomeService welcomeService = new WelcomeService(
                userRepository,
                Mockito.mock(FileRepository.class));

        WelcomeController controller = new WelcomeController(uploadFilesService, welcomeService, securityConfiguration);

        assertEquals("welcome", controller.showWelcomePage(Mockito.mock(ModelMap.class), "operation"));
    }

    @SneakyThrows
    @Test
    void testShowForgotPasswordPageOnlyFragmentWithRoleUser() {
        User user = new User();
        user.setActiveSince(Instant.now());
        List<Role> roles = new ArrayList<>();
        roles.add(new Role("ROLE_USER", new User()));
        user.setRoles(roles);
        Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(user);
        WelcomeService welcomeService = new WelcomeService(
                userRepository,
                Mockito.mock(FileRepository.class));

        WelcomeController controller = new WelcomeController(uploadFilesService, welcomeService, securityConfiguration);

        assertEquals("fragments/welcome", controller.showWelcomePage(Mockito.mock(ModelMap.class), "only-fragment"));
    }

    @SneakyThrows
    @Test
    void testShowForgotPasswordPageOnlyFragmentWithRoleAdmin() {
        User user = new User();
        user.setActiveSince(Instant.now());
        List<Role> roles = new ArrayList<>();
        roles.add(new Role("ROLE_ADMIN", new User()));
        user.setRoles(roles);
        Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(user);
        WelcomeService welcomeService = new WelcomeService(
                userRepository,
                Mockito.mock(FileRepository.class));

        WelcomeController controller = new WelcomeController(uploadFilesService, welcomeService, securityConfiguration);

        assertEquals("fragments/welcome", controller.showWelcomePage(Mockito.mock(ModelMap.class), "only-fragment"));
    }

    @SneakyThrows
    @Test
    void testShowForgotPasswordPageOnlyFragmentWithRoleMultiple() {
        User user = new User();
        user.setActiveSince(Instant.now());
        List<Role> roles = new ArrayList<>();
        roles.add(new Role("ROLE_ADMIN", new User()));
        roles.add(new Role("ROLE_USER", new User()));
        roles.add(new Role("ROLE_TEST", new User()));
        user.setRoles(roles);
        Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(user);
        WelcomeService welcomeService = new WelcomeService(
                userRepository,
                Mockito.mock(FileRepository.class));

        WelcomeController controller = new WelcomeController(uploadFilesService, welcomeService, securityConfiguration);

        assertEquals("fragments/welcome", controller.showWelcomePage(Mockito.mock(ModelMap.class), "only-fragment"));
    }
}
