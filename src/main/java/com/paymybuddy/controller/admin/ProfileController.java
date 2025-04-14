package com.paymybuddy.controller.admin;

import com.paymybuddy.entity.User;
import com.paymybuddy.entity.Role;
import com.paymybuddy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    private final UserService userService;

    @GetMapping("/profile")
    public String getProfile(Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userService.findByEmail(email).orElse(null);

        if (user != null && user.getRole()== Role.ROLE_ADMIN) {
            logger.info("Admin connecté, redirection vers dashboard.");
            return "redirect:/dashboard";
        }
        model.addAttribute("user", user);
        logger.info("Affichage du profil pour l'utilisateur : {}", email);
        return "profil";
    }
}
