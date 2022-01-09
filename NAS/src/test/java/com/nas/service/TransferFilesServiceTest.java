package com.nas.service;

import com.nas.communicationsecurity.service.JwtService;
import com.nas.configuration.SecurityConfiguration;
import com.nas.persistence.dto.UserDetailsDTO;
import com.nas.persistence.model.UserView;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

class TransferFilesServiceTest {

    private static LoadBalancerService loadBalancerService;
    private static DiscoveryClient discoveryClient;
    private static JwtService jwtService;
    private static SecurityConfiguration securityConfiguration;

    @BeforeAll
    private static void beforeAll() {
        loadBalancerService = Mockito.mock(LoadBalancerService.class);
        discoveryClient = Mockito.mock(DiscoveryClient.class);
        jwtService = Mockito.mock(JwtService.class);
        securityConfiguration = Mockito.mock(SecurityConfiguration.class);
        Mockito.when(securityConfiguration.getLoggedUser()).thenReturn(new UserDetailsDTO());
    }

    @Test
    void testLoadUserByUsername() {
        TransferFilesService service = new TransferFilesService(loadBalancerService, discoveryClient, jwtService, securityConfiguration);
        service.setUploadPath("");
        assertNotNull(service.getFolderList(1L, ""));
    }

    @Test
    void testHasSubFoldersFase() {
        TransferFilesService service = new TransferFilesService(loadBalancerService, discoveryClient, jwtService, securityConfiguration);
        assertFalse(service.hasSubFolders(""));
    }

    @Test
    void testHasSubFoldersTrue() {
        TransferFilesService service = new TransferFilesService(loadBalancerService, discoveryClient, jwtService, securityConfiguration);
        assertTrue(service.hasSubFolders("/"));
    }

    @SneakyThrows
    @Test
    void testLoadVariablesToView() {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);

        Mockito.when(authentication.getPrincipal()).thenReturn(new UserDetailsDTO());
        securityContext.setAuthentication(authentication);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        TransferFilesService service = new TransferFilesService(loadBalancerService, discoveryClient, jwtService, securityConfiguration);
        service.setUploadPath("");
        Mockito.when(jwtService.validateToken(Mockito.any())).thenReturn(new UserView());

        service.loadVariablesToView(Mockito.mock(ModelMap.class), true, new HashMap<>());
    }

    @Test
    @SneakyThrows
    void loadVariablesToViewTest() {
        TransferFilesService service = new TransferFilesService(loadBalancerService, discoveryClient, jwtService, securityConfiguration);
        List<String> services = new ArrayList<>();
        services.add("service1");
        services.add("service2");
        Mockito.when(discoveryClient.getServices()).thenReturn(services);
        Mockito.when(discoveryClient.getInstances(Mockito.anyString())).thenReturn(new ArrayList<>());
        Mockito.when(loadBalancerService.getInstanceUrl(Mockito.any())).thenReturn("");
        Mockito.when(jwtService.validateToken(Mockito.anyString())).thenReturn(new UserView());
        service.loadVariablesToView(new ModelMap(), false, new HashMap<>(), "");
    }
}
