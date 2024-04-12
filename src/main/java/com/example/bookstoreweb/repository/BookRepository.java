package com.example.bookstoreweb.repository;

import com.example.bookstoreweb.model.Book;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    @EntityGraph(attributePaths = "categories")
    List<Book> findAllByCategoryId(Long categoryId);
}
