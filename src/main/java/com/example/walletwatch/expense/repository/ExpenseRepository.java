package com.example.walletwatch.expense.repository;

import com.example.walletwatch.expense.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;



public interface ExpenseRepository extends JpaRepository<Expense, Long>{


    List<Expense> findAllByUserId(long user);
}
