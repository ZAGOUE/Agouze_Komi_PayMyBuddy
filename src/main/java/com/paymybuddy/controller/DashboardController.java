package com.paymybuddy.controller;


import com.paymybuddy.entity.User;
import com.paymybuddy.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final UserService userService;

    public DashboardController(UserService userService)
    {
        this.userService = userService;
    }


    @GetMapping
    public String showDashboard(Model model, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return "redirect:/login";
        }

        String userEmail = authentication.getName();
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        model.addAttribute("user", user); // seulement l'utilisateur nécessaire
        return "dashboard";
    }



}
