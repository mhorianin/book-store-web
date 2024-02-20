package com.example.bookstoreweb.repository;

import com.example.bookstoreweb.model.Book;
import java.util.List;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
