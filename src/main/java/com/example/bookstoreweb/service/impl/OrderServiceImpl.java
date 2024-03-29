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
import com.example.bookstoreweb.model.User;
import com.example.bookstoreweb.repository.OrderItemRepository;
import com.example.bookstoreweb.repository.OrderRepository;
import com.example.bookstoreweb.repository.ShoppingCartRepository;
import com.example.bookstoreweb.repository.UserRepository;
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
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public List<OrderResponseDto> findAllOrdersByUser(String email, Pageable pageable) {
        UserResponseDto responseDto = userService.getByEmail(email);
        return orderRepository.findAllByUserId(responseDto.getId()).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public OrderResponseDto create(String email, OrderRequestDto requestDto) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("Can't find user by email:" + email)
        );
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Can't find shopping cart"));

        Order order = orderMapper.toModel(requestDto);
        order.setUser(user);
        order.setStatus(Order.Status.PENDING);
        order.setTotal(shoppingCart.getCartItems().stream()
                .map(cartItem -> cartItem.getBook().getPrice()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        order.setOrderDate(LocalDateTime.now());
        Order save = orderRepository.save(order);
        for (CartItem cartItem : shoppingCart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setBook(cartItem.getBook());
            orderItem.setOrder(save);
            orderItem.setPrice(cartItem.getBook().getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            order.getOrderItems().add(orderItem);
            orderItemRepository.save(orderItem);
        }
        return orderMapper.toDto(save);
    }

    @Override
    public OrderResponseDto update(Long id, OrderRequestUpdateDto requestUpdateDto) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find order by id: " + id)
        );
        orderMapper.update(order, requestUpdateDto);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public List<OrderItemDto> findAllItemsByOrderId(String email, Long orderId, Pageable pageable) {
        Order orderById = findOrderById(email, orderId);
        return orderById.getOrderItems().stream()
                .map(orderMapper::toOrderItemDto)
                .toList();
    }

    @Override
    public OrderItemDto findItemByIdByOrderId(String email, Long orderId, Long itemId) {
        Order orderById = findOrderById(email, orderId);
        return orderMapper.toOrderItemDto(orderById.getOrderItems().stream()
                .filter(item -> Objects.equals(itemId, item.getId()))
                .findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find order by id: " + itemId)
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
