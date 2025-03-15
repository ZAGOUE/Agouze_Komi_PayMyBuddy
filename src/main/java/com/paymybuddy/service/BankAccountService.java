package com.paymybuddy.service;

import com.paymybuddy.entity.BankAccount;
import com.paymybuddy.entity.User;
import com.paymybuddy.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    @Transactional
    public BankAccount addBankAccount(BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount);
    }

    public List<BankAccount> getUserBankAccounts(User user) {
        return bankAccountRepository.findByUserUserId(user.getUserId());
    }

    @Transactional
    public void deleteBankAccount(Integer bankAccountId) {
        bankAccountRepository.deleteById(bankAccountId);
    }
}
