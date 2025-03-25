package com.paymybuddy.controller;

import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.User;
import com.paymybuddy.service.TransactionService;
import com.paymybuddy.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@PreAuthorize("hasRole('ADMIN')")
@Controller
@RequestMapping("/admin/transactions")
public class TransactionViewController {

    private final TransactionService transactionService;
    private final UserService userService;

    public TransactionViewController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @GetMapping
    public String viewTransactions(Model model, Authentication authentication) {
        Optional<User> admin = userService.findByEmail(authentication.getName());
        if (admin.isEmpty()) {
            return "redirect:/login";
        }

        List<Transaction> transactions = transactionService.findAll(); // ✅ Toutes les transactions
        model.addAttribute("transactions", transactions);
        model.addAttribute("user", admin.get());
        return "admin-transactions"; // ✅ Vue dédiée admin
    }
}
