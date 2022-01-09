package com.nas.service;

import com.nas.persistence.model.UserView;
import com.nas.persistence.repository.UserViewRepository;
import com.nas.service.security.JpaAuthenticationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JpaAuthenticationServiceTest {

    private static UserViewRepository userRepository;

    @BeforeAll
    private static void beforeAll() {
        userRepository = Mockito.mock(UserViewRepository.class);
    }

    @Test
    void testLoadUserByUsername() {
        JpaAuthenticationService service = new JpaAuthenticationService(userRepository);
        UserView userView = Mockito.mock(UserView.class);

        Mockito.when(userView.getRoles()).thenReturn("a, b");
        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(userView);


        assertNotNull(service.loadUserByUsername(""));
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        JpaAuthenticationService service = new JpaAuthenticationService(userRepository);

        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername("");
        });

    }
}
