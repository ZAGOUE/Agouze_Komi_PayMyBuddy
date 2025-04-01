package com.paymybuddy.controller.api;

import com.paymybuddy.entity.Connection;
import com.paymybuddy.entity.User;
import com.paymybuddy.service.ConnectionService;
import com.paymybuddy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/connections")
@RequiredArgsConstructor
public class ConnectionController {

    private final ConnectionService connectionService;
    private final UserService userService;

    @PostMapping("/add")
    public ResponseEntity<Connection> addConnection(
            Authentication authentication,
            @RequestParam String friendEmail) {

        Optional<User> user = userService.findByEmail(authentication.getName());
        Optional<User> friend = userService.findByEmail(friendEmail);

        if (user.isEmpty() || friend.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(connectionService.addConnection(user.get(), friend.get()));
    }

    @GetMapping("/{email}")
    public ResponseEntity<List<Connection>> getUserConnections(Authentication authentication) {
        Optional<User> user = userService.findByEmail(authentication.getName());
        return user.map(value -> ResponseEntity.ok(connectionService.getUserConnections(value)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
