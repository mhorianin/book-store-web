package com.example.bookstoreweb;

import com.example.bookstoreweb.model.Book;
import com.example.bookstoreweb.service.BookService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookStoreWebApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(BookStoreWebApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                Book shrek = new Book();
                shrek.setTitle("Shrek");
                shrek.setAuthor("Dreamworks");
                shrek.setPrice(BigDecimal.valueOf(199));
                shrek.setCoverlmage("Something");
                shrek.setDescription("It is very good book");

                bookService.save(shrek);

                System.out.println(bookService.findAll());
            }
        };
    }
}
