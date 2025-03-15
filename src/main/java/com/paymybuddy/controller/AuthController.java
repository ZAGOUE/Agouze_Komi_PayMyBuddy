package com.paymybuddy.controller;

import com.paymybuddy.entity.User;
import com.paymybuddy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        System.out.println("[DEBUG] Tentative de connexion : " + email);
        System.out.println("[DEBUG] Mot de passe reçu : " + password);

        User user = userService.findByEmail(email).orElse(null);

        if (user == null) {
            System.out.println("[DEBUG] Échec : utilisateur introuvable !");
            return ResponseEntity.status(401).body(Map.of("error", "Échec de connexion"));
        }

        System.out.println("[DEBUG] Mot de passe stocké : " + user.getPassword());

        if (!passwordEncoder.matches(password, user.getPassword())) {
            System.out.println("[DEBUG] Échec : mot de passe incorrect !");
            return ResponseEntity.status(401).body(Map.of("error", "Échec de connexion"));
        }

        return ResponseEntity.ok(Map.of("message", "Connexion réussie"));
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User savedUser = userService.saveUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}

