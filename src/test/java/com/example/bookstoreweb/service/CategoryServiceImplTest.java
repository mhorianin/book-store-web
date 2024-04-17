package com.example.bookstoreweb.service;

import com.example.bookstoreweb.dto.category.CategoryDto;
import com.example.bookstoreweb.dto.category.CategoryRequestDto;
import com.example.bookstoreweb.exception.EntityNotFoundException;
import com.example.bookstoreweb.mapper.CategoryMapper;
import com.example.bookstoreweb.model.Category;
import com.example.bookstoreweb.repository.CategoryRepository;
import com.example.bookstoreweb.service.impl.CategoryServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
class CategoryServiceImplTest {
    private static final Long ID = 1L;
    private Category category;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        category = new Category(ID);
        category.setName("Detective");
        category.setDescription("Adventures of detectives");
    }

    @Test
    @DisplayName("Save category with valid data")
    void saveCategory_ValidData_Success() {
        CategoryRequestDto requestDto = createCategoryRequestDto();

        CategoryDto expected = createCategoryDto();

        Mockito.when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        Mockito.when(categoryMapper.toDto(category)).thenReturn(expected);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);

        CategoryDto actual = categoryService.save(requestDto);

        Assertions.assertEquals(expected, actual);
        Mockito.verify(categoryRepository, Mockito.times(1)).save(category);
    }

    @Test
    @DisplayName("Find all categories in database")
    void findAllCategories_ValidPageable_Success() {
        List<Category> categories = new ArrayList<>();
        categories.add(category);

        List<CategoryDto> expected = new ArrayList<>();
        expected.add(createCategoryDto());

        Page<Category> categoryPage = new PageImpl<>(categories);

        Mockito.when(categoryRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(categoryPage);
        Mockito.when(categoryMapper.toDto(category)).thenReturn(createCategoryDto());

        List<CategoryDto> actual = categoryService.findAll(Mockito.mock(Pageable.class));
        Assertions.assertEquals(expected, actual);
        Mockito.verify(categoryRepository, Mockito.times(1))
                .findAll(Mockito.any(Pageable.class));
    }

    @Test
    @DisplayName("Check for an exception if the category id is invalid")
    void findCategoryById_InvalidId_Failed() {
        Mockito.when(categoryRepository.findById(ID)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(ID));
        Mockito.verify(categoryRepository, Mockito.times(1)).findById(ID);
    }

    @Test
    @DisplayName("Find category by valid id")
    void findCategoryById_ValidId_Success() {
        Mockito.when(categoryRepository.findById(ID)).thenReturn(Optional.of(category));
        CategoryDto expected = createCategoryDto();
        Mockito.when(categoryMapper.toDto(category)).thenReturn(expected);
        CategoryDto actual = categoryService.getById(ID);
        Assertions.assertEquals(expected, actual);
        Mockito.verify(categoryRepository, Mockito.times(1)).findById(ID);
    }

    @Test
    @DisplayName("Update category with valid id and data")
    void updateCategory_ValidIdAndData_Success() {
        Category newCategory = category;
        newCategory.setDescription("The investigation of Sherlock Holmes");

        CategoryDto expected = createCategoryDto();
        expected.setDescription(newCategory.getDescription());

        CategoryRequestDto requestDto =
                new CategoryRequestDto("Detective", "The investigation of Sherlock Holmes");

        Mockito.when(categoryRepository.findById(ID)).thenReturn(Optional.of(category));
        Mockito.when(categoryMapper.toDto(newCategory)).thenReturn(expected);
        Mockito.when(categoryRepository.save(newCategory)).thenReturn(newCategory);

        CategoryDto actual = categoryService.update(ID, requestDto);

        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
        Assertions.assertEquals(expected, actual);

        Mockito.verify(categoryRepository, Mockito.times(1)).findById(ID);
        Mockito.verify(categoryRepository, Mockito.times(1)).save(newCategory);
    }

    private CategoryDto createCategoryDto() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(ID);
        categoryDto.setName("Detective");
        categoryDto.setDescription("Adventures of detectives");
        return categoryDto;
    }

    private CategoryRequestDto createCategoryRequestDto() {
        return new CategoryRequestDto("Detective", "Adventures of detectives");
    }
}
