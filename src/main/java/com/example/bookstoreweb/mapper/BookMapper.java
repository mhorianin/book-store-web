package com.example.bookstoreweb.mapper;

import com.example.bookstoreweb.config.MapperConfig;
import com.example.bookstoreweb.dto.book.BookDto;
import com.example.bookstoreweb.dto.book.CreateBookRequestDto;
import com.example.bookstoreweb.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto createBookRequestDto);

    void update(@MappingTarget Book book, CreateBookRequestDto requestDto);
}
