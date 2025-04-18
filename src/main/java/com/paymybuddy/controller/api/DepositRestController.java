package com.paymybuddy.controller.api;

import com.paymybuddy.entity.Deposit;
import com.paymybuddy.entity.User;
import com.paymybuddy.service.DepositService;
import com.paymybuddy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/deposits")
@RequiredArgsConstructor
public class DepositRestController {

    private final DepositService depositService;
    private final UserService userService;

    @PostMapping("/add")
    public ResponseEntity<Deposit> makeDeposit(
            Authentication authentication,
            @RequestParam BigDecimal amount) {

        Optional<User> user = userService.findByEmail(authentication.getName());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Deposit deposit = depositService.makeDeposit(user.get(), amount);
        return ResponseEntity.ok(deposit);
    }

    @GetMapping("/{email}")
    public ResponseEntity<List<Deposit>> getUserDeposits(Authentication authentication) {
        Optional<User> user = userService.findByEmail(authentication.getName());
        return user.map(value -> ResponseEntity.ok(depositService.getUserDeposits(value)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}

