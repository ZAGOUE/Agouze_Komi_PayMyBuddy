package com.paymybuddy.service;

import com.paymybuddy.entity.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.paymybuddy.entity.User;
import com.paymybuddy.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getFriends(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));
        System.out.println("Amis trouvés pour " + userEmail + " : " + user.getFriends());
               return user.getFriends();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable : " + email));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority(user.getRole().name())) // ✅ Le bon format
                .build();
    }

    @Transactional
    public User saveUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("L'email existe déjà !");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire !");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword())); // Hash du mot de passe
        if (user.getRole() == null) {
            user.setRole(Role.ROLE_USER);
        }
        return userRepository.save(user);
    }

    @Transactional
    public void updateBalance(User user, BigDecimal newBalance) {
        logger.debug("Mise à jour du solde pour {} : {} €", user.getEmail(), newBalance);
        user.setBalance(newBalance);
        userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public User findById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable par ID"));
    }


    public User findByEmailWithFriends(String email) {
        return userRepository.findByEmailWithFriends(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));
    }
    public List<User> getUsersNotAlreadyFriends(String email) {
        return userRepository.findUsersNotAlreadyFriends(email);
    }



    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User promoteToAdmin(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        user.setRole(Role.ROLE_ADMIN);
        return userRepository.save(user);
    }
    @Transactional
    public void registerUser(String firstName, String lastName, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            logger.warn("Tentative d'enregistrement avec un email existant : {}", email);
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.ROLE_USER);
        user.setBalance(BigDecimal.valueOf(0)); // Solde initial à 0

        userRepository.save(user);
        logger.info("Utilisateur enregistré : {}", email);
    }
    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }



}
