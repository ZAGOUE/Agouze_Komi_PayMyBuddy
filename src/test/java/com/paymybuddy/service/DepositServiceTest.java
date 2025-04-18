
package com.paymybuddy.service;

import com.paymybuddy.entity.Deposit;
import com.paymybuddy.entity.User;
import com.paymybuddy.repository.DepositRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DepositServiceTest {

    @Mock
    private DepositRepository depositRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private DepositService depositService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUserId(1);
        user.setEmail("test@example.com");
        user.setBalance(BigDecimal.ZERO);
    }

    @Test
    void shouldMakeDepositAndUpdateBalance() {
        BigDecimal amount = new BigDecimal("100.00");

        when(depositRepository.save(any(Deposit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Deposit result = depositService.makeDeposit(user, amount);

        assertNotNull(result);
        assertEquals(amount, result.getAmount());

        verify(userService).updateBalance(user, amount);
        verify(depositRepository).save(any(Deposit.class));
    }

    @Test
    void getUserDeposits_shouldReturnList() {
        when(depositRepository.findByUserUserId(user.getUserId())).thenReturn(List.of(new Deposit()));
        List<Deposit> result = depositService.getUserDeposits(user);
        assertEquals(1, result.size());
    }

    @Test
    void deposit_shouldDelegateToMakeDeposit() {
        DepositService spyService = spy(depositService);
        doReturn(new Deposit()).when(spyService).makeDeposit(user, new BigDecimal("50.00"));

        spyService.deposit(user, new BigDecimal("50.00"));

        verify(spyService).makeDeposit(user, new BigDecimal("50.00"));
    }
}
