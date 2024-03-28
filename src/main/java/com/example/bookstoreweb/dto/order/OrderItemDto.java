package com.example.bookstoreweb.dto.order;

public record OrderItemDto(
        Long id,
        Long bookId,
        int quantity
) {
}
