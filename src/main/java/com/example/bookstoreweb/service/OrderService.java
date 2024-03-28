package com.example.bookstoreweb.service;

import com.example.bookstoreweb.dto.order.OrderItemDto;
import com.example.bookstoreweb.dto.order.OrderRequestDto;
import com.example.bookstoreweb.dto.order.OrderRequestUpdateDto;
import com.example.bookstoreweb.dto.order.OrderResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    List<OrderResponseDto> findAllOrders(String email, Pageable pageable);

    OrderResponseDto create(String email, OrderRequestDto requestDto);

    OrderResponseDto update(Long id, OrderRequestUpdateDto requestUpdateDto);

    List<OrderItemDto> findAllItemsByOrderId(String email, Long orderId, Pageable pageable);

    OrderItemDto findItemById(String email, Long orderId, Long itemId);
}
