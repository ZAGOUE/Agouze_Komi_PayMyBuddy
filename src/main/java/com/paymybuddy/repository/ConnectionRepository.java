package com.paymybuddy.repository;

import com.paymybuddy.entity.Connection;
import com.paymybuddy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends JpaRepository<Connection, Integer> {
    List<Connection> findByUserUserId(Integer userId);
    Optional<Connection> findByUserAndFriend(User user, User friend);
    boolean existsByUserAndFriend(User user, User friend);
}
