package com.example.walletwatch.expense.service;

import com.example.walletwatch.expense.model.Expense;
import com.example.walletwatch.expense.model.TransactionDetails;
import com.example.walletwatch.expense.model.User;
import com.example.walletwatch.expense.repository.ExpenseRepository;
import com.example.walletwatch.expense.repository.TransactionDetailsRepository;
import com.example.walletwatch.expense.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MessageParsingService {

    private static final Logger logger = LoggerFactory.getLogger(MessageParsingService.class);

    private final List<Pattern> patterns;

    @Autowired
    private final TransactionDetailsRepository transactionDetailsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    public MessageParsingService(TransactionDetailsRepository transactionDetailsRepository) {
        this.transactionDetailsRepository = transactionDetailsRepository;

        patterns = new ArrayList<>();
        patterns.add(Pattern.compile("Paid Ksh(\\d+\\.?\\d*) to ([\\w\\s]+) on (\\d{1,2}/\\d{1,2}/\\d{2,4}) at (\\d{1,2}:\\d{1,2}(?:\\s?[AP]M)?)"));
        patterns.add(Pattern.compile("Sent Ksh(\\d+\\.?\\d*) to ([\\w\\s]+) on (\\d{1,2}/\\d{1,2}/\\d{2,4}) at (\\d{1,2}:\\d{1,2}(?:\\s?[AP]M)?)"));
    }

    public TransactionDetails parseMessage(String message, Long userId) {


        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                String amount = matcher.group(1);
                String recipientName = matcher.group(2);
                String date = matcher.group(3);
                String time = matcher.group(4);

                logger.info("Amount: {} Recipient Name: {} Date: {} Time: {}", amount, recipientName, date, time);

                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("h:mm a");
                DateTimeFormatter outputFormatter = DateTimeFormatter.ISO_LOCAL_TIME;
                LocalTime localTime = LocalTime.parse(time, inputFormatter);
                String isoTime = localTime.format(outputFormatter);

                //TODO create custom exception for situations to be faced
                User user  = userRepository.findById(userId).orElseThrow(()->new RuntimeException("The user is not Found"));

                TransactionDetails transactionDetails = new TransactionDetails(amount, recipientName, date, isoTime,user);



                // Save the parsed transaction details to the database
                TransactionDetails savedTransactionDetails = transactionDetailsRepository.save(transactionDetails);
                Expense expense = savedTransactionDetails.toExpense();
                Expense savedExpense = expenseRepository.save(expense);



                logger.info("TransactionDetails saved: {}", savedTransactionDetails);

                return savedTransactionDetails;
            }
        }

        throw new IllegalArgumentException("Invalid message format");
    }
}

