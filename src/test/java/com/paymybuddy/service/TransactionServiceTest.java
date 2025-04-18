
package com.paymybuddy.service;

import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.User;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User sender;
    private User receiver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sender = new User();
        sender.setUserId(1);
        sender.setEmail("sender@example.com");
        sender.setBalance(new BigDecimal("100.00"));

        receiver = new User();
        receiver.setUserId(2);
        receiver.setEmail("receiver@example.com");
        receiver.setBalance(new BigDecimal("50.00"));
    }

    @Test
    void shouldTransferMoneyCorrectly() {
        when(userRepository.findByEmail(sender.getEmail())).thenReturn(Optional.of(sender));
        when(userRepository.findByEmail(receiver.getEmail())).thenReturn(Optional.of(receiver));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal amount = new BigDecimal("10.00");
        String description = "Test paiement";

        Transaction transaction = transactionService.transferMoney(sender, receiver, amount, description);

        assertEquals(new BigDecimal("89.95"), sender.getBalance().setScale(2));
        assertEquals(new BigDecimal("60.00"), receiver.getBalance().setScale(2));
        assertEquals(amount, transaction.getAmount());

        verify(userRepository).save(sender);
        verify(userRepository).save(receiver);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void shouldThrowExceptionIfInsufficientBalance() {
        sender.setBalance(new BigDecimal("5.00"));

        when(userRepository.findByEmail(sender.getEmail())).thenReturn(Optional.of(sender));
        when(userRepository.findByEmail(receiver.getEmail())).thenReturn(Optional.of(receiver));

        BigDecimal amount = new BigDecimal("10.00");

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.transferMoney(sender, receiver, amount, "Test"));
    }

    @Test
    void shouldThrowExceptionIfAmountNegative() {
        when(userRepository.findByEmail(sender.getEmail())).thenReturn(Optional.of(sender));
        when(userRepository.findByEmail(receiver.getEmail())).thenReturn(Optional.of(receiver));

        BigDecimal amount = new BigDecimal("-10.00");

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.transferMoney(sender, receiver, amount, "Test"));
    }

    @Test
    void getSentTransactions_shouldReturnList() {
        when(transactionRepository.findBySender(sender)).thenReturn(List.of(new Transaction()));
        List<Transaction> result = transactionService.getSentTransactions(sender);
        assertFalse(result.isEmpty());
    }

    @Test
    void getReceivedTransactions_shouldReturnList() {
        when(transactionRepository.findByReceiver(receiver)).thenReturn(List.of(new Transaction()));
        List<Transaction> result = transactionService.getReceivedTransactions(receiver);
        assertFalse(result.isEmpty());
    }

    @Test
    void findBySenderUserId_shouldReturnList() {
        when(transactionRepository.findBySenderUserId(1L)).thenReturn(Collections.singletonList(new Transaction()));
        assertEquals(1, transactionService.findBySenderUserId(1L).size());
    }

    @Test
    void findAll_shouldReturnAllTransactions() {
        when(transactionRepository.findAll()).thenReturn(List.of(new Transaction()));
        assertEquals(1, transactionService.findAll().size());
    }

    @Test
    void save_shouldCallRepository() {
        Transaction t = new Transaction();
        transactionService.save(t);
        verify(transactionRepository).save(t);
    }
}
