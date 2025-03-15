package com.paymybuddy.repository;

import com.paymybuddy.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findBySenderUserId(Integer senderId);
    List<Transaction> findByReceiverUserId(Integer receiverId);
}
