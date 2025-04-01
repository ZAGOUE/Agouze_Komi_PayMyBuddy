package com.paymybuddy.controller.api;

import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.User;
import com.paymybuddy.service.TransactionService;
import com.paymybuddy.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
public class TransactionRestController {

    private final TransactionService transactionService;
    private final UserService userService;

    public TransactionRestController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transferMoney(
            Authentication authentication,
            @RequestParam String receiverEmail,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String description) {

        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("Utilisateur non authentifié");
        }

        Optional<User> sender = userService.findByEmail(authentication.getName());
        Optional<User> receiver = userService.findByEmail(receiverEmail);

        if (sender.isEmpty()) {
            return ResponseEntity.status(404).body("Expéditeur introuvable");
        }
        if (receiver.isEmpty()) {
            return ResponseEntity.status(404).body("Destinataire introuvable");
        }

        if (sender.get().getBalance().compareTo(amount) < 0) {
            return ResponseEntity.status(400).body("Fonds insuffisants");
        }

        Transaction transaction = transactionService.transferMoney(sender.get(), receiver.get(), amount, description);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/sent")
    public ResponseEntity<List<Transaction>> getSentTransactions(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }
        Optional<User> user = userService.findByEmail(authentication.getName());
        return user.map(value -> ResponseEntity.ok(transactionService.getSentTransactions(value)))
                .orElseGet(() -> ResponseEntity.status(404).build());
    }

    @GetMapping("/received")
    public ResponseEntity<List<Transaction>> getReceivedTransactions(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }
        Optional<User> user = userService.findByEmail(authentication.getName());
        return user.map(value -> ResponseEntity.ok(transactionService.getReceivedTransactions(value)))
                .orElseGet(() -> ResponseEntity.status(404).build());
    }
}
