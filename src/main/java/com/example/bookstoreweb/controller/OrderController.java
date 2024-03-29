package com.example.bookstoreweb.controller;

import com.example.bookstoreweb.dto.order.OrderItemDto;
import com.example.bookstoreweb.dto.order.OrderRequestDto;
import com.example.bookstoreweb.dto.order.OrderRequestUpdateDto;
import com.example.bookstoreweb.dto.order.OrderResponseDto;
import com.example.bookstoreweb.model.User;
import com.example.bookstoreweb.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management",
        description = "Endpoints for managing orders and order items")
@RestController
@RequestMapping(value = "/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    @Operation(summary = "Create new order",
            description = "Create new order and add shipping address")
    public OrderResponseDto createOrder(Authentication authentication,
                                        @RequestBody @Valid OrderRequestDto requestDto) {
        return orderService.create(getUser(authentication), requestDto);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    @Operation(summary = "Get all orders for user",
            description = "Get a list of all orders for a specific user")
    public List<OrderResponseDto> getAllOrders(Authentication authentication, Pageable pageable) {
        return orderService.findAllOrdersByUser(getUser(authentication), pageable);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{id}")
    @Operation(summary = "Update order's status",
            description = "Update order's status")
    public OrderResponseDto updateOrder(@PathVariable Long id,
                                   @RequestBody @Valid OrderRequestUpdateDto requestUpdateDto) {
        return orderService.update(id, requestUpdateDto);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{id}/items")
    @Operation(summary = "Get all order items for user",
            description = "Get a list of all order items for a specific user's order")
    public List<OrderItemDto> getAllItemsByOrderId(Authentication authentication,
                                                   @PathVariable Long id,
                                                   Pageable pageable) {
        return orderService.findAllItemsByOrderId(getUser(authentication), id, pageable);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Get order item by id for user",
            description = "Get order item by id for a specific user's order")
    public OrderItemDto getItemById(Authentication authentication, @PathVariable Long orderId,
                                    @PathVariable Long itemId) {
        return orderService.findItemByIdByOrderId(getUser(authentication),
                orderId, itemId);
    }

    private String getUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.getEmail();
    }
}
