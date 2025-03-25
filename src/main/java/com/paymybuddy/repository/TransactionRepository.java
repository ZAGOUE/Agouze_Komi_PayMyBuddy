package com.paymybuddy.repository;

import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findBySenderUserId(Long senderId);
    List<Transaction> findByReceiverUserId(Long receiverId);

    List<Transaction> findAll();
    List<Transaction> findBySender(User sender);

    List<Transaction> findByReceiver(User receiver);

}


