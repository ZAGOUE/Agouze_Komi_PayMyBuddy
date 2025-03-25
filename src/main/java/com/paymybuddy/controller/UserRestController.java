package com.paymybuddy.controller;

import com.paymybuddy.entity.User;
import com.paymybuddy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;



    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {


        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Email déjà utilisé"));
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {

            return ResponseEntity.badRequest().body(Map.of("error", "Le mot de passe est obligatoire !"));
        }

        try {
            User savedUser = userService.saveUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/by-email")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getUserDetails(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Utilisateur non authentifié"));
        }


        return userService.findByEmail(authentication.getName())
                .map(user -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("email", user.getEmail());
                    response.put("firstName", user.getFirstName());
                    response.put("lastName", user.getLastName());
                    response.put("balance", user.getBalance());
                    response.put("role", user.getRole());
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Utilisateur non trouvé")));
    }

    @PutMapping("/promote/{email}")
    public ResponseEntity<Map<String, String>> promoteToAdmin(@PathVariable String email) {
        User user = userService.promoteToAdmin(email);
        return ResponseEntity.ok(Map.of("message", "L'utilisateur " + email + " est maintenant ADMIN"));
    }
}
