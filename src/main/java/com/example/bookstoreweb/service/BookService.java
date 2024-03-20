package com.example.bookstoreweb.service;

import com.example.bookstoreweb.dto.book.BookDto;
import com.example.bookstoreweb.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookstoreweb.dto.book.CreateBookRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll(Pageable pageable);

    BookDto findById(Long id);

    void deleteById(Long id);

    BookDto update(Long id, CreateBookRequestDto requestDto);

    List<BookDtoWithoutCategoryIds> findAllByCategoryId(Long id);
}
