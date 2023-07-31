package com.example.walletwatch.expense.repository;

import com.example.walletwatch.expense.model.TransactionDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionDetailsRepository extends JpaRepository<TransactionDetails, Long> {
    List<TransactionDetails> findAllByUserId (Long id);
}
