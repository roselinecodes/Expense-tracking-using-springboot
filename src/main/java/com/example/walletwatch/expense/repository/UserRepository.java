package com.example.walletwatch.expense.repository;

import com.example.walletwatch.expense.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.Email;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long > {
    User findByEmail(String email);


}
