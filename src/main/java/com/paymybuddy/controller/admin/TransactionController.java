package com.paymybuddy.controller.admin;

import com.paymybuddy.entity.User;
import com.paymybuddy.service.TransactionService;
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
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @GetMapping("/sent")
    public String showSentTransactions(Model model, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return "redirect:/login";
        }
        Optional<User> user = userService.findByEmail(authentication.getName());
        user.ifPresent(value -> {
            model.addAttribute("transactions", transactionService.getSentTransactions(value));
            model.addAttribute("type", "envoyées");
            model.addAttribute("user", value);
        });
        return "transactions";
    }

    @GetMapping("/received")
    public String showReceivedTransactions(Model model, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return "redirect:/login";
        }
        Optional<User> user = userService.findByEmail(authentication.getName());
        user.ifPresent(value -> {
            model.addAttribute("transactions", transactionService.getReceivedTransactions(value)); // ✅ corrigé ici
            model.addAttribute("type", "reçues"); // ✅ corrigé ici
            model.addAttribute("user", value);   // ✅ ajouté pour la navbar
        });
        return "transactions";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return "redirect:/login";
        }
        Optional<User> user = userService.findByEmail(authentication.getName());
        user.ifPresent(value -> model.addAttribute("user", value));
        return "dashboard";
    }
    @PostMapping("/sent")
    public String transferMoney(@RequestParam String receiverEmail,
                                @RequestParam BigDecimal amount,
                                @RequestParam(required = false) String description,
                                Authentication authentication) {
        Optional<User> sender = userService.findByEmail(authentication.getName());
        Optional<User> receiver = userService.findByEmail(receiverEmail);

        if (sender.isEmpty() || receiver.isEmpty() || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return "redirect:/dashboard?error";
        }

        transactionService.transferMoney(sender.get().getEmail(), receiver.get().getEmail(), amount, description);

        return "redirect:/dashboard?success";
    }
    @GetMapping("/new")
    public String showTransferPage(Model model, Authentication auth) {
        String email = auth.getName();

        User user = userService.findByEmailWithFriends(email);


        model.addAttribute("user", user);
        model.addAttribute("friends", user.getFriends());

        return "transfert";
    }



}
