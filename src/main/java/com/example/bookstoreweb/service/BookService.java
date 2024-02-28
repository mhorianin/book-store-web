package com.example.bookstoreweb.service;

import com.example.bookstoreweb.dto.BookDto;
import com.example.bookstoreweb.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll();

    BookDto findById(Long id);

    void deleteById(Long id);

    BookDto update(Long id, CreateBookRequestDto requestDto);
}
