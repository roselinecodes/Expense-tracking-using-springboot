package com.example.walletwatch.expense.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.validation.Valid;

import com.example.walletwatch.expense.model.Category;
import com.example.walletwatch.expense.model.Expense;
import com.example.walletwatch.expense.repository.ExpenseRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;


@RestController
@RequestMapping("/api")
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @GetMapping("/expenses")
    List<Expense> getExpenses(){
        return expenseRepository.findAll();
    }

    @DeleteMapping("/expenses/{id}")
    ResponseEntity<?> deleteExpense(@PathVariable Long id){
        expenseRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/expenses")
    ResponseEntity<Expense> createExpense(@Valid @RequestBody Expense expense) throws URISyntaxException {
        Expense result = (Expense) expenseRepository.save(expense);
        return ResponseEntity.created(new URI("/api/expenses" + result.getId())).body(result);
    }

    @PutMapping("/expenses/update")
    ResponseEntity<Expense> updateExpense(@Valid @RequestBody Expense expense) {
            Expense result= expenseRepository.save(expense);
            return ResponseEntity.ok().body(result);
        }
    @GetMapping("/expenses/csv")
    public void getAsCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"expenses.csv\"");

        List<Expense> expenses = expenseRepository.findAll();

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"Expense ID","Description", "Expense Date", "Amount",};
        String[] nameMapping = {"id", "description", "expensedate", "amount", };

        csvWriter.writeHeader(csvHeader);

        for (Expense expense : expenses) {
            csvWriter.write(expense, nameMapping);
        }

        csvWriter.close();
    }

}
