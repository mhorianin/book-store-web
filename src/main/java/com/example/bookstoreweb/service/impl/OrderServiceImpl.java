package com.example.bookstoreweb.service.impl;

import com.example.bookstoreweb.dto.order.OrderItemDto;
import com.example.bookstoreweb.dto.order.OrderRequestDto;
import com.example.bookstoreweb.dto.order.OrderRequestUpdateDto;
import com.example.bookstoreweb.dto.order.OrderResponseDto;
import com.example.bookstoreweb.dto.user.UserResponseDto;
import com.example.bookstoreweb.exception.EntityNotFoundException;
import com.example.bookstoreweb.mapper.OrderMapper;
import com.example.bookstoreweb.model.CartItem;
import com.example.bookstoreweb.model.Order;
import com.example.bookstoreweb.model.OrderItem;
import com.example.bookstoreweb.model.ShoppingCart;
import com.example.bookstoreweb.repository.OrderRepository;
import com.example.bookstoreweb.repository.ShoppingCartRepository;
import com.example.bookstoreweb.service.OrderService;
import com.example.bookstoreweb.service.UserService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserService userService;
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public List<OrderResponseDto> findAllOrders(String email, Pageable pageable) {
        UserResponseDto responseDto = userService.getByEmail(email);
        return orderRepository.findAllByUserId(responseDto.getId())
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public OrderResponseDto create(String email, OrderRequestDto requestDto) {
        UserResponseDto responseDto = userService.getByEmail(email);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(responseDto.getId())
                .orElseThrow(() -> new RuntimeException("Can't find shopping cart"));

        Order model = orderMapper.toModel(requestDto);
        model.setUser(shoppingCart.getUser());
        model.setStatus(Order.Status.PENDING);
        model.setTotal(shoppingCart.getCartItems()
                .stream()
                .map(cartItem -> cartItem.getBook().getPrice()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        model.setOrderDate(LocalDateTime.now());
        for (CartItem cartItem : shoppingCart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setBook(cartItem.getBook());
            orderItem.setOrder(model);
            orderItem.setPrice(cartItem.getBook().getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            model.getOrderItems().add(orderItem);
        }
        return orderMapper.toDto(orderRepository.save(model));
    }

    @Override
    public OrderResponseDto update(String email, Long id, OrderRequestUpdateDto requestUpdateDto) {
        Order orderById = findOrderById(email, id);
        orderMapper.update(orderById, requestUpdateDto);
        return orderMapper.toDto(orderRepository.save(orderById));
    }

    @Override
    public List<OrderItemDto> findAllItemsByOrderId(String email, Long orderId, Pageable pageable) {
        Order orderById = findOrderById(email, orderId);
        return orderById.getOrderItems().stream()
                .map(orderMapper::toOrderItemDto)
                .toList();
    }

    @Override
    public OrderItemDto findItemById(String email, Long orderId, Long itemId) {
        Order orderById = findOrderById(email, orderId);
        return orderMapper.toOrderItemDto(orderById.getOrderItems()
                .stream()
                .filter(item -> Objects.equals(itemId, item.getId()))
                .findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find order by id: " + orderId)
                ));
    }

    private Order findOrderById(String email, Long id) {
        UserResponseDto responseDto = userService.getByEmail(email);
        return orderRepository.findAllByUserId(responseDto.getId()).stream()
                .filter(order -> Objects.equals(id, order.getId()))
                .findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find order by id: " + id)
                );
    }
}
