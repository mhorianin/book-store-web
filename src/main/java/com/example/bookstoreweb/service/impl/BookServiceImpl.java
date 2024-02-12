package com.example.bookstoreweb.service.impl;

import com.example.bookstoreweb.model.Book;
import com.example.bookstoreweb.repository.BookRepository;
import com.example.bookstoreweb.service.BookService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookRepository bookRepository;

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }
}
