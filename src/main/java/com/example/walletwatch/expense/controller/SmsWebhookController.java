package com.example.walletwatch.expense.controller;

import com.example.walletwatch.expense.model.Expense;
import com.example.walletwatch.expense.model.MultipleSmsRequest;
import com.example.walletwatch.expense.model.SmsRequest;
import com.example.walletwatch.expense.model.TransactionDetails;
import com.example.walletwatch.expense.repository.ExpenseRepository;
import com.example.walletwatch.expense.repository.TransactionDetailsRepository;
import com.example.walletwatch.expense.repository.UserRepository;
import com.example.walletwatch.expense.service.MessageParsingService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("/sms")
public class SmsWebhookController {

    private final MessageParsingService messageParsingService;
    private final TransactionDetailsRepository transactionDetailsRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private static final Logger logger = LoggerFactory.getLogger(SmsWebhookController.class);

    public SmsWebhookController(MessageParsingService messageParsingService, TransactionDetailsRepository transactionDetailsRepository, UserRepository userRepository, ExpenseRepository expenseRepository) {
        this.messageParsingService = messageParsingService;
        this.transactionDetailsRepository = transactionDetailsRepository;
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
    }

    @PostMapping("/inbound")
    public ResponseEntity<?> handleIncomingSms(@RequestBody SmsRequest request) {
        String messageContent = request.getBody();
        Long userId = request.getUserId();


        if (Objects.isNull(userId)) {
            return ResponseEntity.badRequest().body("User ID must be provided.");
        }

        TransactionDetails details = messageParsingService.parseMessage(messageContent, userId);



        return ResponseEntity.ok(details);
    }

    @PostMapping("/inbound/multiple")
    public ResponseEntity<String> handleIncomingMultipleSms(@RequestBody MultipleSmsRequest request) {
        List<SmsRequest> smsRequests = request.getTransactionDetails();
        Long userId = request.getUserId();

        if (userId == null) {
            return ResponseEntity.badRequest().body("User ID must be provided.");
        }

        for (SmsRequest req : smsRequests) {
            String messageContent = req.getBody();
            TransactionDetails details = messageParsingService.parseMessage(messageContent, userId);

            logger.info("Received transaction details: {}", details);

        }

        return ResponseEntity.ok("");  // Responds with a 200 OK status
    }


        @GetMapping("/expenses/csv/{id}")
        public void getExpensesAsCSV(HttpServletResponse response,@PathVariable String id) throws IOException {
            response.setContentType("text/csv");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"expenses.csv\"");

            List<Expense> expenses = expenseRepository.findAllByUserId(Long.parseLong(id));

            ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
            String[] csvHeader = {"Expense ID", "Amount", "Description", "Date", "Recipient Name", "Time",};
            String[] nameMapping = {"id", "amount","description","expensedate", "recipientName", "time",};

            csvWriter.writeHeader(csvHeader);

            for (Expense expense : expenses) {
                csvWriter.write(expense, nameMapping);
            }

            csvWriter.close();
        }

    }
