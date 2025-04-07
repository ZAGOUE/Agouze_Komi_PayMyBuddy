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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/transactions")
public class TransactionController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

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
                                Authentication authentication,
                                Model model) {
        Optional<User> senderOpt = userService.findByEmail(authentication.getName());
        Optional<User> receiverOpt = userService.findByEmail(receiverEmail);

        if (senderOpt.isEmpty() || receiverOpt.isEmpty() ) {

            model.addAttribute("error", "Erreur lors du transfert.");
            return "dashboard"; // ou la vue appropriée
        }

        try {
            transactionService.transferMoney(senderOpt.get().getEmail(), receiverOpt.get().getEmail(), amount, description);
            return "redirect:/dashboard?success";
        } catch (IllegalArgumentException e) {

            User userObj = userService.findByEmailWithFriends(authentication.getName()); // Utiliser la méthode qui charge les amis

            // On s'assure que la liste d'amis n'est jamais null
            model.addAttribute("user", userObj);
            model.addAttribute("friends", userObj.getFriends() == null ? new ArrayList<>() : userObj.getFriends());
            model.addAttribute("error", e.getMessage());
            return "transfert"; // la vue du formulaire de transfert
        }

    }

    @GetMapping("/new")
    public String showTransferPage(Model model, Authentication auth) {
        String email = auth.getName();

        User user = userService.findByEmailWithFriends(email);

        logger.debug("Friends list size: {}", user.getFriends() == null ? 0 : user.getFriends().size());

        model.addAttribute("user", user);
        model.addAttribute("friends", user.getFriends());

        return "transfert";
    }



}
