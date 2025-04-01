package com.paymybuddy.controller;

import com.paymybuddy.controller.api.UserRestController;
import com.paymybuddy.entity.Role;
import com.paymybuddy.entity.User;
import com.paymybuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRestControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserRestController userRestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPromoteToAdmin_Success() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setRole(Role.ROLE_ADMIN);

        when(userService.promoteToAdmin("test@example.com")).thenReturn(user);

        // Act
        ResponseEntity<?> response = userRestController.promoteToAdmin("test@example.com");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode()); // ✅ Vérifie le statut HTTP

        // ✅ Récupère le message retourné sous forme de `Map`
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertNotNull(responseBody);

        // ✅ Vérifie que le message correspond
        assertEquals("L'utilisateur test@example.com est maintenant ADMIN", responseBody.get("message"));
    }


    @Test
    void testPromoteToAdmin_UserNotFound() {
        // Arrange
        when(userService.promoteToAdmin("nonexistent@example.com"))
                .thenThrow(new IllegalArgumentException("Utilisateur introuvable"));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userRestController.promoteToAdmin("nonexistent@example.com") // ✅ Utilisation d'une expression lambda simplifiée
        );

        assertEquals("Utilisateur introuvable", exception.getMessage());
    }
}
