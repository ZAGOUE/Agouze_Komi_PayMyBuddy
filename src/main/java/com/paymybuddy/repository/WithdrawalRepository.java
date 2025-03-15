package com.paymybuddy.repository;

import com.paymybuddy.entity.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, Integer> {
    List<Withdrawal> findByUserUserId(Integer userId);
}
