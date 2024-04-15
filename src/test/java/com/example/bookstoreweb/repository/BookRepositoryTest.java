package com.example.bookstoreweb.repository;

import com.example.bookstoreweb.model.Book;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql(scripts = "classpath:database/add-book-to-books-table.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/remove-data-from-all-tables.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Find all books by category id")
    void findAllByCategoryId_ValidId_ReturnOneBook() {
        List<Book> books = bookRepository.findAllByCategoriesId(1L);

        Assertions.assertEquals(1, books.size());
        Assertions.assertEquals("White Fang", books.get(0).getTitle());
    }

    @Test
    @DisplayName("Find all books by category id if no book has that category")
    void findAllByCategoryId_ReturnEmpty() {
        List<Book> books = bookRepository.findAllByCategoriesId(5L);

        Assertions.assertEquals(0, books.size());
    }
}
