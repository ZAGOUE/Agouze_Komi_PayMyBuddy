package com.paymybuddy.controller;

import com.paymybuddy.controller.security.LoginController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginControllerTest {

    private final LoginController loginController = new LoginController();

    @Test
    void login_shouldReturnLoginView() {
        String viewName = loginController.login();
        assertEquals("login", viewName);
    }
}
