package com.paymybuddy.controller;

import com.paymybuddy.entity.User;
import com.paymybuddy.service.DepositService;
import com.paymybuddy.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequestMapping("/deposits")
public class DepositController {
    private final DepositService depositService;
    private final UserService userService;

    public DepositController(DepositService depositService, UserService userService) {
        this.depositService = depositService;
        this.userService = userService;
    }

    @GetMapping("/add")
    public String showDepositPage(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login"; // Redirige vers le login
        }

        Optional<User> user = userService.findByEmail(authentication.getName());
        user.ifPresent(value -> model.addAttribute("user", value));

        return "deposit"; // Affiche deposit.html
    }

    @PostMapping("/add")
    public String processDeposit(@RequestParam BigDecimal amount, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login"; // Redirige vers le login
        }

        Optional<User> user = userService.findByEmail(authentication.getName());
        user.ifPresent(u -> depositService.deposit(u, amount));

        return "redirect:/users/dashboard"; // Redirige vers le dashboard après dépôt
}
}