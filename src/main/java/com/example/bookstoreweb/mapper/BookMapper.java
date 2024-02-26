package com.example.bookstoreweb.mapper;

import com.example.bookstoreweb.config.MapperConfig;
import com.example.bookstoreweb.dto.BookDto;
import com.example.bookstoreweb.dto.CreateBookRequestDto;
import com.example.bookstoreweb.model.Book;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto createBookRequestDto);
}
