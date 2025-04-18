package com.paymybuddy.controller;

import com.paymybuddy.controller.security.RegisterController;
import com.paymybuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class RegisterControllerTest {

    private UserService userService;
    private RegisterController registerController;
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        redirectAttributes = mock(RedirectAttributes.class);
        registerController = new RegisterController(userService);
    }

    @Test
    void showRegistrationForm_shouldReturnRegisterView() {
        String view = registerController.showRegistrationForm();
        assertEquals("register", view);
    }

    @Test
    void registerUser_shouldRedirectToLoginOnSuccess() {
        String view = registerController.registerUser("John", "Doe", "john@example.com", "secret", redirectAttributes);

        verify(userService).registerUser("John", "Doe", "john@example.com", "secret");
        verify(redirectAttributes).addFlashAttribute(eq("success"), anyString());
        assertEquals("redirect:/login", view);
    }

    @Test
    void registerUser_shouldRedirectToRegisterOnError() {
        doThrow(new RuntimeException("Email déjà utilisé")).when(userService)
                .registerUser(anyString(), anyString(), anyString(), anyString());

        String view = registerController.registerUser("Jane", "Doe", "jane@example.com", "secret", redirectAttributes);

        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Email déjà utilisé"));
        assertEquals("redirect:/register", view);
    }
}
