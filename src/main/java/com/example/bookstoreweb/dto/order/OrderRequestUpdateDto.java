package com.example.bookstoreweb.dto.order;

import com.example.bookstoreweb.model.Order;

public record OrderRequestUpdateDto(
        Order.Status status
) {
}
