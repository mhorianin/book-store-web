package com.example.bookstoreweb.service.impl;

import com.example.bookstoreweb.dto.category.CategoryDto;
import com.example.bookstoreweb.dto.category.CategoryRequestDto;
import com.example.bookstoreweb.exception.EntityNotFoundException;
import com.example.bookstoreweb.mapper.CategoryMapper;
import com.example.bookstoreweb.model.Category;
import com.example.bookstoreweb.repository.CategoryRepository;
import com.example.bookstoreweb.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto getById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find category by id: " + id)
        );
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto save(CategoryRequestDto requestDto) {
        Category category = categoryMapper.toEntity(requestDto);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto update(Long id, CategoryRequestDto requestDto) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find category by id: " + id)
        );
        categoryMapper.update(category, requestDto);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
