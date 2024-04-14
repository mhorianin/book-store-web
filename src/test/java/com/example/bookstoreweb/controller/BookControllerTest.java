package com.example.bookstoreweb.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bookstoreweb.dto.book.BookDto;
import com.example.bookstoreweb.dto.book.CreateBookRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/add-books-and-categories-to-db.sql"));
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/remove-book-from-books-table.sql"));
        }
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DisplayName("Create a new book")
    @Sql(scripts = "classpath:database/remove-saved-book-from-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createBook_ValidRequestDto_Success() throws Exception {
        CreateBookRequestDto requestDto = createBookRequestDto();
        BookDto expected = createBookDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult mvcResult = mockMvc.perform(post("/api/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        BookDto actual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), BookDto.class);

        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DisplayName("Return 400-th status when invalid data is entered")
    void createBook_InvalidRequestDto_Failed() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("White Fang");
        requestDto.setIsbn("12587946831871");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        ResultActions resultActions = mockMvc.perform(post("/api/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DisplayName("Update a book by id")
    @Sql(scripts = "classpath:database/remove-saved-book-from-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateBook_ValidId_Success() throws Exception {
        CreateBookRequestDto requestDto = updateBookRequestDto();

        BookDto expected = new BookDto();
        expected.setId(2L);
        expected.setTitle(requestDto.getTitle());
        expected.setAuthor(requestDto.getAuthor());
        expected.setPrice(requestDto.getPrice());
        expected.setIsbn(requestDto.getIsbn());
        expected.setCategoryIds(requestDto.getCategories());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult mvcResult = mockMvc.perform(put("/api/books/2")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), BookDto.class);

        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DisplayName("Delete a book by id")
    void deleteBook_ValidId_Success() throws Exception {
        mockMvc.perform(delete("/api/books/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    private CreateBookRequestDto createBookRequestDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("White Fang");
        requestDto.setAuthor("Jack London");
        requestDto.setPrice(BigDecimal.valueOf(299));
        requestDto.setIsbn("12587946831871");
        requestDto.setCategories(List.of(1L));
        return requestDto;
    }

    private BookDto createBookDto() {
        BookDto bookDto = new BookDto();
        bookDto.setId(3L);
        bookDto.setTitle("White Fang");
        bookDto.setAuthor("Jack London");
        bookDto.setPrice(BigDecimal.valueOf(299));
        bookDto.setIsbn("12587946831871");
        bookDto.setCategoryIds(List.of(1L));
        return bookDto;
    }

    private CreateBookRequestDto updateBookRequestDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Sleep");
        requestDto.setAuthor("Taras Shevchenko");
        requestDto.setPrice(BigDecimal.valueOf(359));
        requestDto.setIsbn("125876332278624");
        requestDto.setCategories(List.of(2L));
        return requestDto;
    }
}
