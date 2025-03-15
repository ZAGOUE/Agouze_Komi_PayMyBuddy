package com.paymybuddy.service;

import com.paymybuddy.entity.Role;
import com.paymybuddy.entity.User;
import com.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPromoteToAdmin_Success() {
        // Arrange : Création d'un utilisateur normal
        User user = new User();
        user.setUserId(1);
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("securePass123"));
        user.setRole(Role.ROLE_USER);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act : Promotion de l'utilisateur
        userService.promoteToAdmin("test@example.com");

        // Assert : Vérifie que le rôle a bien changé
        assertEquals(Role.ROLE_ADMIN, user.getRole(), "L'utilisateur doit maintenant être ADMIN");

        // Vérifie que la méthode `save()` est bien appelée
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testPromoteToAdmin_UserNotFound() {
        // Arrange : Aucun utilisateur trouvé
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert : Vérifie que l'exception est bien levée
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.promoteToAdmin("nonexistent@example.com");
        });

        assertEquals("Utilisateur introuvable", exception.getMessage());
    }

}
