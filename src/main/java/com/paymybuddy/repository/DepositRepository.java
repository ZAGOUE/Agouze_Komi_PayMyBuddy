package com.paymybuddy.repository;

import com.paymybuddy.entity.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DepositRepository extends JpaRepository<Deposit, Integer> {
    List<Deposit> findByUserUserId(Integer userId);
}
