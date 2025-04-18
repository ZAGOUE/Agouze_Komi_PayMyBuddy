
package com.paymybuddy.controller;

import com.paymybuddy.controller.api.UserRestController;
import com.paymybuddy.entity.Role;
import com.paymybuddy.entity.User;
import com.paymybuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class UserRestControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserRestController userRestController;

    private User user;

    @BeforeEach
    void setUp() {
        openMocks(this);
        user = new User();
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBalance(BigDecimal.TEN);
        user.setRole(Role.ROLE_USER);
    }

    @Test
    void registerUser_shouldReturnConflict_whenEmailExists() {
        when(userService.existsByEmail(user.getEmail())).thenReturn(true);

        ResponseEntity<?> response = userRestController.registerUser(user);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void getUserByEmail_shouldReturnUser_whenFound() {
        when(userService.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userRestController.getUserByEmail("user@example.com");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("user@example.com", response.getBody().getEmail());
    }

    @Test
    void getUserDetails_shouldReturnUnauthorized_whenNoAuthentication() {
        ResponseEntity<?> response = userRestController.getUserDetails(null);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    @Test
    void registerUser_shouldReturnCreated_whenValid() {
        when(userService.existsByEmail(user.getEmail())).thenReturn(false);
        when(userService.saveUser(user)).thenReturn(user);

        ResponseEntity<?> response = userRestController.registerUser(user);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(user, response.getBody());
    }
    @Test
    void promoteToAdmin_shouldReturnSuccessMessage() {
        when(userService.promoteToAdmin(user.getEmail())).thenReturn(user);

        ResponseEntity<?> response = userRestController.promoteToAdmin(user.getEmail());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("est maintenant ADMIN"));
    }

    @Test
    void getUserDetails_shouldReturnUserDetails_whenAuthenticated() {
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userService.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ResponseEntity<?> response = userRestController.getUserDetails(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((java.util.Map<?, ?>) response.getBody()).containsKey("email"));
    }



}
