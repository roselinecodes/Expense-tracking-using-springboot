package com.example.walletwatch.expense.model;

import ch.qos.logback.core.model.NamedModel;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
@Data
@Entity
public class TransactionDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String amount;
    private String recipientName;

    @Column(columnDefinition = "DATE")
    private LocalDate date;

    @Column(columnDefinition = "TIME")
    private LocalTime time;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    public TransactionDetails(String amount, String recipientName, String date, String time,User user) {
        this.amount = amount;
        this.recipientName = recipientName;
        this.date = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.time = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        this.user = user;


    }

    public Expense toExpense() {

        Expense expense = new Expense();

        expense.setAmount(Double.parseDouble(this.amount));
        expense.setRecipientName(this.recipientName);
        expense.setTime(this.time);
        expense.setExpensedate(Instant.from(this.date));
        expense.setUser(this.user);

        return expense;


    }

}
