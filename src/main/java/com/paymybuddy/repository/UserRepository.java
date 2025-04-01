package com.paymybuddy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.paymybuddy.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u.friends FROM User u WHERE u.email = :email")
    List<User> findFriendsByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.friends WHERE u.email = :email")
    Optional<User> findByEmailWithFriends(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.email <> :email AND u NOT IN (SELECT f FROM User usr JOIN usr.friends f WHERE usr.email = :email)")
    List<User> findUsersNotAlreadyFriends(@Param("email") String email);


}
