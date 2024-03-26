package com.example.bookstoreweb.dto.shoppingcart;

public record CartItemDto(
        Long id,
        Long bookId,
        String bookTitle,
        int quantity
) {
}
