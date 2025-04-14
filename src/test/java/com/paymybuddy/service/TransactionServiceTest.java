package com.paymybuddy.service;

import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.User;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private TransactionService transactionService;

    @BeforeEach
    void setup() {
        transactionRepository = mock(TransactionRepository.class);
        userRepository = mock(UserRepository.class);
        transactionService = new TransactionService(transactionRepository, userRepository);
    }

    @Test
    void shouldTransferMoneyCorrectly() {
        User sender = new User();
        sender.setUserId(1);
        sender.setEmail("sender@example.com");
        sender.setBalance(new BigDecimal("100.00"));


        User receiver = new User();
        receiver.setUserId(2);
        receiver.setEmail("receiver@example.com");
        receiver.setBalance(new BigDecimal("50.00"));

        // Mock : simule la base de données
        when(userRepository.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
        when(userRepository.findByEmail("receiver@example.com")).thenReturn(Optional.of(receiver));



        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal amount = new BigDecimal("10.00");
        String description = "Test paiement";

        Transaction transaction = transactionService.transferMoney(sender, receiver, amount, description);

        assertEquals(new BigDecimal("89.95"), sender.getBalance().setScale(2, RoundingMode.HALF_UP));
        // 100 - 10 - 0.05
        assertEquals(new BigDecimal("60.00"), receiver.getBalance().setScale(2, RoundingMode.HALF_UP));


        verify(userRepository).save(sender);
        verify(userRepository).save(receiver);
        verify(transactionRepository).save(any(Transaction.class));
        assertEquals(transaction.getAmount(), amount);
    }

    @Test
    void shouldThrowExceptionIfInsufficientBalance() {
        User sender = new User();
        sender.setBalance(new BigDecimal("5.00"));
        User receiver = new User();
        BigDecimal amount = new BigDecimal("10.00");

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.transferMoney(sender, receiver, amount, "Test")
        );
    }
}
