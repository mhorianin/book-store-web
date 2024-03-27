package com.example.bookstoreweb.dto.shoppingcart;

public record CartItemRequestDto(
        Long bookId,
        int quantity
) {
}
