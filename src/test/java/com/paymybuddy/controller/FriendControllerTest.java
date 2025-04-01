package com.paymybuddy.controller;

import com.paymybuddy.controller.api.FriendController;
import com.paymybuddy.entity.User;
import com.paymybuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class FriendControllerTest {

    private UserService userService;
    private FriendController friendController;

    @BeforeEach
    void setup() {
        userService = mock(UserService.class);
        friendController = new FriendController(userService);
    }

    @Test
    void shouldReturnAddFriendView() {
        User user = new User();
        String userEmail = "test@example.com";

        // ✅ Crée un vrai mock Authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(userEmail);

        when(userService.findByEmailWithFriends(userEmail)).thenReturn(user);
        when(userService.getUsersNotAlreadyFriends(userEmail)).thenReturn(Collections.emptyList());

        Model model = mock(Model.class);
        String view = friendController.showPotentialFriends(model, authentication);

        assertEquals("add-friend", view);
        verify(model).addAttribute("user", user);
        verify(model).addAttribute("potentialFriends", Collections.emptyList());
    }

}
