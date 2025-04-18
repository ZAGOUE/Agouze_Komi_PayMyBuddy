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

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;
    @Mock


    private PasswordEncoder passwordEncoder;

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
    // saveUser utilisateur neuf
    @Test
    void testSaveUser_NewUser_Success() {
        User user = new User();
        user.setEmail("new@example.com");
        user.setPassword("password");

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPwd");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.saveUser(user);

        assertNotNull(saved);
        assertEquals(Role.ROLE_USER, saved.getRole());
        assertEquals("hashedPwd", saved.getPassword());
    }
    // saveUser email existant → exception
    @Test
    void testSaveUser_ExistingEmail_ThrowsException() {
        User user = new User();
        user.setEmail("exist@example.com");
        user.setPassword("pass");

        when(userRepository.existsByEmail("exist@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.saveUser(user));
    }

    // registerUser création simple
    @Test
    void registerUser_ShouldSaveEncodedUser() {
        when(userRepository.findByEmail("toto@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");

        userService.registerUser("Toto", "Tata", "toto@example.com", "pass");

        verify(userRepository).save(argThat(u ->
                u.getEmail().equals("toto@example.com") &&
                        u.getPassword().equals("encodedPass") &&
                        u.getRole() == Role.ROLE_USER
        ));
    }

    // updateBalance
    @Test
    void updateBalance_ShouldCallSave() {
        User user = new User();
        user.setEmail("u@ex.com");
        user.setBalance(BigDecimal.ZERO);

        userService.updateBalance(user, new BigDecimal("200"));

        verify(userRepository).save(user);
        assertEquals(new BigDecimal("200"), user.getBalance());
    }
    // findByEmailWithFriends
    @Test
    void findByEmailWithFriends_ShouldReturnUser() {
        User user = new User();
        user.setEmail("email@example.com");

        when(userRepository.findByEmailWithFriends("email@example.com")).thenReturn(Optional.of(user));

        User result = userService.findByEmailWithFriends("email@example.com");

        assertEquals("email@example.com", result.getEmail());
    }

}
