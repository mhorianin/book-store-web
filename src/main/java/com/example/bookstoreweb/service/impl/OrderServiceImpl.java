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
import com.example.bookstoreweb.repository.OrderItemRepository;
import com.example.bookstoreweb.repository.OrderRepository;
import com.example.bookstoreweb.repository.ShoppingCartRepository;
import com.example.bookstoreweb.service.OrderService;
import com.example.bookstoreweb.service.UserService;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final UserService userService;
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public List<OrderResponseDto> findAllOrdersByUser(String email, Pageable pageable) {
        UserResponseDto responseDto = userService.getByEmail(email);
        return orderRepository.findAllByUserId(responseDto.getId()).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public OrderResponseDto create(String email, OrderRequestDto requestDto) {
        UserResponseDto responseDto = userService.getByEmail(email);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(responseDto.getId())
                .orElseThrow(() -> new RuntimeException("Can't find shopping cart"));
        Order order = new Order(shoppingCart);
        order.setShippingAddress(requestDto.shippingAddress());
        orderRepository.save(order);
        for (CartItem cartItem : shoppingCart.getCartItems()) {
            OrderItem orderItem = new OrderItem(cartItem);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
            orderItemRepository.save(orderItem);
        }
        return orderMapper.toDto(order);
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
