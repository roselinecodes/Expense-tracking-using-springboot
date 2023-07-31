package com.example.walletwatch.expense.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "expense")
public class Expense {

    @Id
    private Long id;

    private Instant expensedate;

    private String recipientName;

    private LocalTime time;

    private double amount;

    private String description;


    @ManyToOne
    private Category category;

    @ManyToOne
    private User user;
}

