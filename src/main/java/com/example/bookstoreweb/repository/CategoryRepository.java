package com.example.bookstoreweb.repository;

import com.example.bookstoreweb.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
