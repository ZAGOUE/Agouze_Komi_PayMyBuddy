package com.paymybuddy.service;

import com.paymybuddy.entity.BankAccount;
import com.paymybuddy.entity.User;
import com.paymybuddy.entity.Withdrawal;
import com.paymybuddy.repository.WithdrawalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;
    private final UserService userService;

    @Transactional
    public Withdrawal makeWithdrawal(User user, BankAccount bankAccount, BigDecimal amount) {
        if (user.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Solde insuffisant");
        }

        user.setBalance(user.getBalance().subtract(amount));
        userService.saveUser(user);

        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setUser(user);
        withdrawal.setBankAccount(bankAccount);
        withdrawal.setAmount(amount);

        return withdrawalRepository.save(withdrawal);
    }

    public List<Withdrawal> getUserWithdrawals(User user) {
        return withdrawalRepository.findByUserUserId(user.getUserId());
    }
}
