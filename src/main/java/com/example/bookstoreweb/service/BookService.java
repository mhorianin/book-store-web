package com.example.bookstoreweb.service;

import com.example.bookstoreweb.model.Book;
import java.util.List;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
