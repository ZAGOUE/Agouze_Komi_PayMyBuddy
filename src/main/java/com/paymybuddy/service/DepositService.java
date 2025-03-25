package com.paymybuddy.service;

import com.paymybuddy.entity.Deposit;
import com.paymybuddy.entity.User;
import com.paymybuddy.repository.DepositRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepositService {

    private final DepositRepository depositRepository;
    private final UserService userService;

    @Transactional
    public Deposit makeDeposit(User user, BigDecimal amount) {
        Deposit deposit = new Deposit();
        deposit.setUser(user);
        deposit.setAmount(amount);

        userService.updateBalance(user, user.getBalance().add(amount));
        return depositRepository.save(deposit);
    }

    public List<Deposit> getUserDeposits(User user) {
        return depositRepository.findByUserUserId(user.getUserId());
    }

    public void deposit(User u, BigDecimal amount) {
        this.makeDeposit(u, amount);

    }
}
