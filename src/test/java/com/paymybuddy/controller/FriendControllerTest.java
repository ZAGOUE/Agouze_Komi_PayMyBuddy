
package com.paymybuddy.controller;

import com.paymybuddy.controller.api.FriendController;
import com.paymybuddy.entity.User;
import com.paymybuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FriendControllerTest {

    @InjectMocks
    private FriendController friendController;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private RedirectAttributes redirectAttributes;


    @Mock
    private Model model;

    private User currentUser;
    private User friendUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        currentUser = new User();
        currentUser.setEmail("me@example.com");

        friendUser = new User();
        friendUser.setEmail("friend@example.com");
    }

    @Test
    void showPotentialFriends_shouldReturnView() {
        when(authentication.getName()).thenReturn(currentUser.getEmail());
        when(userService.findByEmailWithFriends(currentUser.getEmail())).thenReturn(currentUser);
        when(userService.getUsersNotAlreadyFriends(currentUser.getEmail())).thenReturn(List.of(friendUser));

        String view = friendController.showPotentialFriends(model, authentication);
        assertEquals("add-friend", view);
        verify(model).addAttribute("user", currentUser);
        verify(model).addAttribute(eq("potentialFriends"), any());
    }

    @Test
    void addFriend_shouldRedirectToDashboard() {
        // GIVEN
        User currentUser = new User();
        currentUser.setEmail("user@example.com");
        currentUser.setFriends(new ArrayList<>());

        User friendUser = new User();
        friendUser.setEmail("friend@example.com");
        friendUser.setFriends(new ArrayList<>());

        when(authentication.getName()).thenReturn("user@example.com");
        when(userService.findByEmailWithFriends("user@example.com")).thenReturn(currentUser);
        when(userService.findById(anyInt())).thenReturn(friendUser);

        // WHEN
        String view = friendController.addFriend(123, authentication); // 123 = friendId fictif

        // THEN
        assertEquals("redirect:/dashboard", view);
        assertTrue(currentUser.getFriends().contains(friendUser));
    }


    @Test
    void addFriendByEmail_shouldRedirect() {
        // Arrange
        currentUser.setEmail("user@example.com");
        friendUser.setEmail("friend@example.com");

        currentUser.setFriends(new ArrayList<>());

        when(authentication.getName()).thenReturn("user@example.com");
        when(userService.findByEmailWithFriends("user@example.com")).thenReturn(currentUser);
        when(userService.findByEmail("friend@example.com")).thenReturn(Optional.of(friendUser));

        // Act
        String view = friendController.addFriendByEmail("friend@example.com", authentication, redirectAttributes);

        // Assert
        assertEquals("redirect:/friends/add", view);
        assertTrue(currentUser.getFriends().contains(friendUser));
    }



    @Test
    void addFriendByEmail_shouldRedirectIfUserNotFound() {
        when(authentication.getName()).thenReturn("me@example.com");
        when(userService.findByEmail("me@example.com")).thenReturn(Optional.empty());

        String view = friendController.addFriendByEmail("friend@example.com", authentication, redirectAttributes);
        assertEquals("redirect:/friends/add", view);
    }

    @Test
    void addFriendByEmail_shouldRedirectIfFriendNotFound() {
        when(authentication.getName()).thenReturn(currentUser.getEmail());
        when(userService.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(userService.findByEmail("friend@example.com")).thenReturn(Optional.empty());

        String view = friendController.addFriendByEmail("friend@example.com", authentication, redirectAttributes);
        assertEquals("redirect:/friends/add", view);
    }
}
