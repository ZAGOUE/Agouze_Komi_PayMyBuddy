package com.paymybuddy.controller.admin;

import com.paymybuddy.entity.User;
import com.paymybuddy.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login"; // Redirige vers le login
        }

        Optional<User> user = userService.findByEmail(authentication.getName());
        user.ifPresent(value -> model.addAttribute("user", value));

        return "dashboard";
    }
    @GetMapping("/change-password")
    public String showChangePasswordForm() {
        return "/change-password";
    }
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Authentication authentication,
                                 Model model) {

        Optional<User> userOpt = userService.findByEmail(authentication.getName());

        if (userOpt.isEmpty()) {
            logger.error("Utilisateur non trouvé lors du changement de mot de passe.");
            model.addAttribute("error", "Utilisateur introuvable.");
            return "change-password";
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            logger.warn("Mot de passe actuel incorrect");
            model.addAttribute("error", "Mot de passe actuel incorrect.");
            return "change-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Les nouveaux mots de passe ne correspondent pas.");
            return "change-password";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userService.updateUser(user);
        logger.info("Mot de passe modifié avec succès pour");

        model.addAttribute("success", "Mot de passe modifié avec succès.");
        return "change-password";
    }


}

