package com.example.bookstoreweb.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bookstoreweb.dto.category.CategoryDto;
import com.example.bookstoreweb.dto.category.CategoryRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerTest {
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
                    new ClassPathResource("database/add-category-for-book.sql"));
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
                    new ClassPathResource("database/remove-data-from-all-tables.sql"));
        }
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    @DisplayName("Get a list of all categories")
    void findAllCategories_Success() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andReturn();
        List<CategoryDto> expected = new ArrayList<>();
        expected.add(createCategoryDto());
        CategoryDto[] actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CategoryDto[].class);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    @DisplayName("Find category by id")
    void findCategoryById_ValidId_Success() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CategoryDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals("Detective", actual.getName());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DisplayName("Create a new category")
    @Sql(scripts = "classpath:database/remove-saved-category-from-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createCategory_ValidRequestDto_Success() throws Exception {
        CategoryRequestDto requestDto = saveCategoryRequestDto();
        CategoryDto expected = new CategoryDto();
        expected.setId(2L);
        expected.setName(requestDto.name());
        expected.setDescription(requestDto.description());
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult mvcResult = mockMvc.perform(post("/api/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), CategoryDto.class);

        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DisplayName("Update a category by id")
    void updateBook_ValidId_Success() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto("Fantasy", "Fantasy world");

        CategoryDto expected = new CategoryDto();
        expected.setId(1L);
        expected.setName(requestDto.name());
        expected.setDescription(requestDto.description());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult mvcResult = mockMvc.perform(put("/api/books/1")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), CategoryDto.class);

        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DisplayName("Delete a category by id")
    void deleteCategory_ValidId_Success() throws Exception {
        mockMvc.perform(delete("/api/categories/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    private CategoryRequestDto createCategoryRequestDto() {
        return new CategoryRequestDto("Detective", "Adventures of detectives");
    }

    private CategoryDto createCategoryDto() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Detective");
        categoryDto.setDescription("Adventures of detectives");
        return categoryDto;
    }

    private CategoryRequestDto saveCategoryRequestDto() {
        return new CategoryRequestDto("Verse", "Ukrainian poems");
    }
}
