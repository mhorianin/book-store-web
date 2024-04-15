package com.example.bookstoreweb.service;

import com.example.bookstoreweb.dto.book.BookDto;
import com.example.bookstoreweb.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookstoreweb.dto.book.CreateBookRequestDto;
import com.example.bookstoreweb.exception.EntityNotFoundException;
import com.example.bookstoreweb.mapper.BookMapper;
import com.example.bookstoreweb.model.Book;
import com.example.bookstoreweb.model.Category;
import com.example.bookstoreweb.repository.BookRepository;
import com.example.bookstoreweb.service.impl.BookServiceImpl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    private static final Long ID = 1L;
    private Book book;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        book = new Book(ID);
        book.setTitle("White Fang");
        book.setAuthor("Jack London");
        book.setPrice(BigDecimal.valueOf(299));
        book.setIsbn("12587946831871");
        book.setCategories(Set.of(new Category(ID)));
    }

    @Test
    @DisplayName("Save book with valid data")
    void saveBook_ValidData_Success() {
        CreateBookRequestDto requestDto = createBookRequestDto();

        BookDto expected = createBookDto();

        Mockito.when(bookMapper.toModel(requestDto)).thenReturn(book);
        Mockito.when(bookMapper.toDto(book)).thenReturn(expected);
        Mockito.when(bookRepository.save(book)).thenReturn(book);

        BookDto actual = bookService.save(requestDto);

        Assertions.assertEquals(expected, actual);
        Mockito.verify(bookRepository, Mockito.times(1)).save(book);
    }

    @Test
    @DisplayName("Find all books in database")
    void findAllBooks_ValidPageable_Success() {
        List<Book> books = new ArrayList<>();
        books.add(book);

        List<BookDto> expected = new ArrayList<>();
        expected.add(createBookDto());

        Page<Book> bookPage = new PageImpl<>(books);

        Mockito.when(bookRepository.findAll(Mockito.any(Pageable.class))).thenReturn(bookPage);
        Mockito.when(bookMapper.toDto(book)).thenReturn(createBookDto());

        List<BookDto> actual = bookService.findAll(Mockito.mock(Pageable.class));
        Assertions.assertEquals(expected, actual);
        Mockito.verify(bookRepository, Mockito.times(1))
                .findAll(Mockito.any(Pageable.class));
    }

    @Test
    @DisplayName("Check for an exception if the book id is invalid")
    void findBookById_InvalidId_Failed() {
        Mockito.when(bookRepository.findById(ID)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookService.findById(ID));
        Mockito.verify(bookRepository, Mockito.times(1)).findById(ID);
    }

    @Test
    @DisplayName("Find book by valid id")
    void findBookById_ValidId_Success() {
        Mockito.when(bookRepository.findById(ID)).thenReturn(Optional.of(book));
        BookDto expected = createBookDto();
        Mockito.when(bookMapper.toDto(book)).thenReturn(expected);
        BookDto actual = bookService.findById(ID);
        Assertions.assertEquals(expected, actual);
        Mockito.verify(bookRepository, Mockito.times(1)).findById(ID);
    }

    @Test
    @DisplayName("Update book with valid id and data")
    void updateBook_ValidIdAndData_Success() {
        Book newBook = book;
        newBook.setTitle("Black Fang");
        newBook.setPrice(BigDecimal.valueOf(369));

        CreateBookRequestDto requestDto = createBookRequestDto();
        requestDto.setTitle("Black Fang");
        requestDto.setPrice(BigDecimal.valueOf(369));

        BookDto expected = createBookDto();
        expected.setTitle(newBook.getTitle());
        expected.setPrice(newBook.getPrice());

        Mockito.when(bookRepository.findById(ID)).thenReturn(Optional.of(book));
        Mockito.when(bookMapper.toDto(newBook)).thenReturn(expected);
        Mockito.when(bookRepository.save(newBook)).thenReturn(newBook);

        BookDto actual = bookService.update(ID, requestDto);

        Assertions.assertEquals(expected.getTitle(), actual.getTitle());
        Assertions.assertEquals(expected.getPrice(), actual.getPrice());
        Assertions.assertEquals(expected, actual);
        Mockito.verify(bookRepository, Mockito.times(1)).findById(ID);
        Mockito.verify(bookRepository, Mockito.times(1)).save(newBook);
    }

    @Test
    @DisplayName("Find all books by id category")
    void findAllBooksByCategoryId_ValidCategoryId_Success() {
        BookDtoWithoutCategoryIds expected = createBookDtoWithoutCategoryIds();

        Mockito.when(bookRepository.findAllByCategoriesId(ID)).thenReturn(List.of(book));
        Mockito.when(bookMapper.toDtoWithoutCategories(book)).thenReturn(expected);

        List<BookDtoWithoutCategoryIds> actual = bookService.findAllByCategoryId(ID);

        Assertions.assertEquals(expected, actual.get(0));
        Mockito.verify(bookRepository, Mockito.times(1)).findAllByCategoriesId(ID);
    }

    private CreateBookRequestDto createBookRequestDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("White Fang");
        requestDto.setAuthor("Jack London");
        requestDto.setPrice(BigDecimal.valueOf(299));
        requestDto.setIsbn("12587946831871");
        requestDto.setCategories(List.of(ID));
        return requestDto;
    }

    private BookDto createBookDto() {
        BookDto bookDto = new BookDto();
        bookDto.setId(ID);
        bookDto.setTitle("White Fang");
        bookDto.setAuthor("Jack London");
        bookDto.setPrice(BigDecimal.valueOf(299));
        bookDto.setIsbn("12587946831871");
        bookDto.setCategoryIds(List.of(ID));
        return bookDto;
    }

    private BookDtoWithoutCategoryIds createBookDtoWithoutCategoryIds() {
        BookDtoWithoutCategoryIds bookDto = new BookDtoWithoutCategoryIds();
        bookDto.setId(ID);
        bookDto.setTitle("White Fang");
        bookDto.setAuthor("Jack London");
        bookDto.setPrice(BigDecimal.valueOf(299));
        bookDto.setIsbn("12587946831871");
        return bookDto;
    }
}
