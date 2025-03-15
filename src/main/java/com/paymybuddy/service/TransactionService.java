package com.paymybuddy.service;

import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.User;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Transactional
    public Transaction transferMoney(User sender, User receiver, BigDecimal amount, String description) {
        BigDecimal fee = amount.multiply(BigDecimal.valueOf(0.005)); // 0,5% frais
        BigDecimal total = amount.add(fee);

        if (sender.getBalance().compareTo(total) < 0) {
            throw new IllegalArgumentException("Solde insuffisant ! Vous avez " + sender.getBalance() + " mais vous devez payer " + total);
        }

        sender.setBalance(sender.getBalance().subtract(total));
        receiver.setBalance(receiver.getBalance().add(amount));

        userRepository.save(sender);
        userRepository.save(receiver);

        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(amount);
        transaction.setFee(fee);
        transaction.setDescription(description);

        Transaction savedTransaction = transactionRepository.save(transaction);

        // log après sauvegarde de la transaction
        System.out.println("💸 Transaction enregistrée : " + sender.getEmail() + " -> " + receiver.getEmail() +
                " | Montant : " + amount + " | Frais : " + fee + " | Total débité : " + total);

        return savedTransaction;
    }

    public List<Transaction> getSentTransactions(User sender) {
        return transactionRepository.findBySenderUserId(sender.getUserId());
    }

    public List<Transaction> getReceivedTransactions(User receiver) {
        return transactionRepository.findByReceiverUserId(receiver.getUserId());
    }
}
