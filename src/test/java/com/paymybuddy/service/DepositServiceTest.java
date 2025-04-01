package com.paymybuddy.service;

import com.paymybuddy.entity.Deposit;
import com.paymybuddy.entity.User;
import com.paymybuddy.repository.DepositRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DepositServiceTest {
    private DepositRepository depositRepository;
    private UserService userService;
    private DepositService depositService;

    @BeforeEach
    void setup() {
        depositRepository = mock(DepositRepository.class);
        userService = mock(UserService.class);
        depositService = new DepositService(depositRepository, userService);
    }

    @Test
    void shouldMakeDepositAndUpdateBalance() {
        User user = new User();
        user.setUserId(1);
        user.setBalance(new BigDecimal("100.00"));

        BigDecimal amount = new BigDecimal("50.00");

        when(depositRepository.save(any(Deposit.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Deposit deposit = depositService.makeDeposit(user, amount);

        verify(userService).updateBalance(eq(user), eq(new BigDecimal("150.00")));
        verify(depositRepository).save(any(Deposit.class));
        assertEquals(deposit.getAmount(), amount);
    }
}
