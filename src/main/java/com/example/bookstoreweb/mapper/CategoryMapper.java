package com.example.bookstoreweb.mapper;

import com.example.bookstoreweb.config.MapperConfig;
import com.example.bookstoreweb.dto.category.CategoryDto;
import com.example.bookstoreweb.dto.category.CategoryRequestDto;
import com.example.bookstoreweb.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CategoryRequestDto requestDto);

    void update(@MappingTarget Category category, CategoryRequestDto requestDto);
}
