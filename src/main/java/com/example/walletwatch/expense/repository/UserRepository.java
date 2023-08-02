package com.example.walletwatch.expense.repository;

import com.example.walletwatch.expense.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long > {
    User findByEmail(String email);


}
