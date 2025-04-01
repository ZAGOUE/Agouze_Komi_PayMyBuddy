package com.paymybuddy.service;

import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface ITransactionService {

    List<Transaction> getSentTransactions(User sender);

    List<Transaction> getReceivedTransactions(User receiver);

    List<Transaction> findBySenderUserId(Long userId);

    List<Transaction> findByReceiverUserId(Long userId);

    Transaction transferMoney(String senderEmail, String receiverEmail, BigDecimal amount, String description);

    List<Transaction> findAll();

    void save(Transaction transaction);
}
