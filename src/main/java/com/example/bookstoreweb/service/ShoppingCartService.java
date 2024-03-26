package com.example.bookstoreweb.service;

import com.example.bookstoreweb.dto.shoppingcart.CartItemDto;
import com.example.bookstoreweb.dto.shoppingcart.CartItemRequestDto;
import com.example.bookstoreweb.dto.shoppingcart.CartItemRequestUpdateDto;
import com.example.bookstoreweb.dto.shoppingcart.ShoppingCartDto;
import org.springframework.data.domain.Pageable;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCartForUser(String email, Pageable pageable);

    CartItemDto save(String email, CartItemRequestDto requestDto);

    CartItemDto update(String email, Long id, CartItemRequestUpdateDto requestDto);

    void deleteById(String email, Long id);
}
