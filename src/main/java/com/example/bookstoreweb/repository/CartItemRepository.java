package com.example.bookstoreweb.repository;

import com.example.bookstoreweb.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
