package com.paymybuddy.service;

import com.paymybuddy.entity.Deposit;
import com.paymybuddy.entity.User;
import com.paymybuddy.repository.DepositRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepositService {

    private static final Logger logger = LoggerFactory.getLogger(DepositService.class);

    private final DepositRepository depositRepository;
    private final UserService userService;

    @Transactional
    public Deposit makeDeposit(User user, BigDecimal amount) {
        logger.info("Dépôt demandé : {} € pour {}", amount, user.getEmail());
        Deposit deposit = new Deposit();
        deposit.setUser(user);
        deposit.setAmount(amount);

        userService.updateBalance(user, user.getBalance().add(amount));
        return depositRepository.save(deposit);
    }

    public List<Deposit> getUserDeposits(User user) {

        logger.debug("Récupération des dépôts pour {}", user.getEmail());
        return depositRepository.findByUserUserId(user.getUserId());
    }

    public void deposit(User u, BigDecimal amount) {
        this.makeDeposit(u, amount);

    }
}
