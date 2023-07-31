package com.example.walletwatch.expense.controller;


import com.example.walletwatch.expense.model.Category;
import com.example.walletwatch.expense.repository.CategoryRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


    @RestController
    @RequestMapping("/api")
    public class CategoryController {

        private CategoryRepository categoryRepository;

        public CategoryController(CategoryRepository categoryRepository) {
            super();
            this.categoryRepository = categoryRepository;
        }


        @GetMapping("/categories")
        Collection<Category> categories(){
            return categoryRepository.findAll();
        }

        //category/2
        @GetMapping("/category/{id}")
        ResponseEntity<?> getCategory(@PathVariable Long id){
            Optional<Category> category = categoryRepository.findById(id);
            return category.map(response -> ResponseEntity.ok().body(response))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        }

        @PostMapping("/category")
        ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) throws URISyntaxException {
            Category result= categoryRepository.save(category);
            return ResponseEntity.created(new URI("/api/category" + result.getId())).body(result);

        }

        @PutMapping("/category/{id}")
        ResponseEntity<Category> updateCategory(@Valid @RequestBody Category category){
            Category result= categoryRepository.save(category);
            return ResponseEntity.ok().body(result);
        }

        @DeleteMapping("/category/{id}")
        ResponseEntity<?> deleteCategory(@PathVariable Long id){
            categoryRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        @GetMapping("/categories/csv")
        public void getAsCSV(HttpServletResponse response) throws IOException {
            response.setContentType("text/csv");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"categories.csv\"");

            List<Category> categories = categoryRepository.findAll();

            ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
            String[] csvHeader = {"ID", "Name",};
            String[] nameMapping = {"id", "name",};

            csvWriter.writeHeader(csvHeader);

            for (Category category : categories) {
                csvWriter.write(category, nameMapping);
            }

            csvWriter.close();
        }

    }

