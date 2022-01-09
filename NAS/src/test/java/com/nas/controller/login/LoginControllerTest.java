package com.nas.controller.login;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.ModelMap;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

class LoginControllerTest {

    @Test
    void testShowForgotPasswordPageOnlyFragment() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        ModelMap modelMap = Mockito.mock(ModelMap.class);
        Mockito.when(modelMap.put(Mockito.anyString(), Mockito.any())).thenReturn(null);
        LoginController controller = new LoginController();
        assertEquals("fragments/login/login", controller.showLoginPage(modelMap, "only-fragment"));
    }

    @Test
    void testShowForgotPasswordPage() {
        LocaleContextHolder.setLocale(Locale.GERMAN);
        ModelMap modelMap = Mockito.mock(ModelMap.class);
        Mockito.when(modelMap.put(Mockito.anyString(), Mockito.any())).thenReturn(null);
        LoginController controller = new LoginController();
        assertEquals("login/login", controller.showLoginPage(modelMap, ""));
    }
}
