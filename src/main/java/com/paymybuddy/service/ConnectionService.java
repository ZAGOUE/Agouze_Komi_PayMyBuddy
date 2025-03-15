package com.paymybuddy.service;

import com.paymybuddy.entity.Connection;
import com.paymybuddy.entity.User;
import com.paymybuddy.repository.ConnectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final UserService userService;

    @Transactional
    public Connection addConnection(User user, User friend) {
        if (user.getUserId().equals(friend.getUserId())) {
            throw new IllegalArgumentException("Tu ne peux pas t'ajouter toi-même !");
        }
        if (connectionRepository.findByUserAndFriend(user, friend).isPresent()) {
            throw new IllegalArgumentException("Cette connexion existe déjà !");
        }

        Connection connection = new Connection();
        connection.setUser(user);
        connection.setFriend(friend);

        return connectionRepository.save(connection);
    }

    @Transactional(readOnly = true)
    public List<Connection> getUserConnections(User user) {
        return connectionRepository.findByUserUserId(user.getUserId());
    }

    @Transactional
    public void removeConnection(User user, User friend) {
        connectionRepository.findByUserAndFriend(user, friend).ifPresentOrElse(
                connectionRepository::delete,
                () -> { throw new IllegalArgumentException("Connexion introuvable !"); }
        );
    }
}
