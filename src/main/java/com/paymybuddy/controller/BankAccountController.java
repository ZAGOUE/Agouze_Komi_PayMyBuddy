package com.paymybuddy.controller;

import com.paymybuddy.entity.BankAccount;
import com.paymybuddy.entity.User;
import com.paymybuddy.service.BankAccountService;
import com.paymybuddy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bank-accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;
    private final UserService userService;

    @PostMapping("/add")
    public ResponseEntity<BankAccount> addBankAccount(
            Authentication authentication,
            @RequestBody BankAccount bankAccount) {

        Optional<User> user = userService.findByEmail(authentication.getName());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        bankAccount.setUser(user.get());
        return ResponseEntity.ok(bankAccountService.addBankAccount(bankAccount));
    }

    @GetMapping("/{email}")
    public ResponseEntity<List<BankAccount>> getUserBankAccounts(Authentication authentication) {
        Optional<User> user = userService.findByEmail(authentication.getName());
        return user.map(value -> ResponseEntity.ok(bankAccountService.getUserBankAccounts(value)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
