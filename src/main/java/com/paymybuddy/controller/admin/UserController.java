package com.paymybuddy.controller.admin;

import com.paymybuddy.entity.User;
import com.paymybuddy.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login"; // Redirige vers le login
        }

        Optional<User> user = userService.findByEmail(authentication.getName());
        user.ifPresent(value -> model.addAttribute("user", value));

        return "dashboard"; // Affiche dashboard.html
    }
}

