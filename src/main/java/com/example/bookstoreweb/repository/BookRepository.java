package com.example.bookstoreweb.repository;

import com.example.bookstoreweb.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

}
