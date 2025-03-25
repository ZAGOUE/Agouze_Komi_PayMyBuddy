package com.paymybuddy.service;

import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.User;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService implements ITransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Transaction> getSentTransactions(User sender) {
        return transactionRepository.findBySender(sender);
    }

    @Override
    public List<Transaction> getReceivedTransactions(User receiver) {
        return transactionRepository.findByReceiver(receiver);
    }

    @Override
    public List<Transaction> findBySenderUserId(Long userId) {
        return transactionRepository.findBySenderUserId(userId);
    }

    @Override
    public List<Transaction> findByReceiverUserId(Long userId) {
        return transactionRepository.findByReceiverUserId(userId);
    }

    @Override
    @Transactional
    public Transaction transferMoney(String senderEmail, String receiverEmail, BigDecimal amount, String description) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new IllegalArgumentException("Expéditeur introuvable."));

        User receiver = userRepository.findByEmail(receiverEmail)
                .orElseThrow(() -> new IllegalArgumentException("Destinataire introuvable."));

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Solde insuffisant pour effectuer ce transfert.");
        }

        BigDecimal fee = amount.multiply(new BigDecimal("0.005")).setScale(5, RoundingMode.HALF_UP);
        BigDecimal totalDebit = amount.add(fee);

        sender.setBalance(sender.getBalance().subtract(totalDebit));
        receiver.setBalance(receiver.getBalance().add(amount));

        userRepository.save(sender);
        userRepository.save(receiver);

        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(amount);
        transaction.setFee(fee);
        transaction.setDescription(description);
        transaction.setTransactionDate(LocalDateTime.now());

        transactionRepository.save(transaction);

        System.out.println("\uD83D\uDCB8 Transaction enregistrée : " + sender.getEmail() + " -> " + receiver.getEmail()
                + " | Montant : " + amount + " | Frais : " + fee + " | Total débité : " + totalDebit);

        return transaction;
    }

    public Transaction transferMoney(User sender, User receiver, BigDecimal amount, String description) {
        return transferMoney(sender.getEmail(), receiver.getEmail(), amount, description);
    }

    /**
     * Récupère toutes les transactions du système.
     * Cette méthode est réservée à un usage administrateur.
     * Veillez à sécuriser son accès avec une vérification du rôle ADMIN.
     */
    @Override
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @Override
    public void save(Transaction transaction) {
        transactionRepository.save(transaction);
    }
}
