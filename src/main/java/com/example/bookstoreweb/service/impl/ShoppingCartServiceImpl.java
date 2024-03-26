package com.example.bookstoreweb.service.impl;

import com.example.bookstoreweb.dto.shoppingcart.CartItemDto;
import com.example.bookstoreweb.dto.shoppingcart.CartItemRequestDto;
import com.example.bookstoreweb.dto.shoppingcart.CartItemRequestUpdateDto;
import com.example.bookstoreweb.dto.shoppingcart.ShoppingCartDto;
import com.example.bookstoreweb.dto.user.UserResponseDto;
import com.example.bookstoreweb.exception.EntityNotFoundException;
import com.example.bookstoreweb.mapper.ShoppingCartMapper;
import com.example.bookstoreweb.model.CartItem;
import com.example.bookstoreweb.model.ShoppingCart;
import com.example.bookstoreweb.repository.CartItemRepository;
import com.example.bookstoreweb.repository.ShoppingCartRepository;
import com.example.bookstoreweb.service.ShoppingCartService;
import com.example.bookstoreweb.service.UserService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final UserService userService;

    @Override
    public ShoppingCartDto getShoppingCartForUser(String email, Pageable pageable) {
        return shoppingCartMapper.toDto(findShoppingCartByUser(email));
    }

    @Override
    public CartItemDto save(String email, CartItemRequestDto requestDto) {
        ShoppingCart shoppingCart = findShoppingCartByUser(email);
        CartItem cartItem = shoppingCartMapper.toModel(requestDto);
        cartItem.setShoppingCart(shoppingCart);

        shoppingCart.getCartItems().add(cartItem);

        return shoppingCartMapper.toCartItemDto(cartItemRepository.save(cartItem));
    }

    @Override
    public CartItemDto update(String email, Long id, CartItemRequestUpdateDto requestDto) {
        CartItem cartItem = findShoppingCartByUser(email).getCartItems().stream()
                .filter(item -> Objects.equals(id, item.getId()))
                .findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find cart item by id: " + id)
                );
        shoppingCartMapper.update(cartItem, requestDto);
        return shoppingCartMapper.toCartItemDto(cartItemRepository.save(cartItem));
    }

    @Override
    public void deleteById(String email, Long id) {
        ShoppingCart shoppingCart = findShoppingCartByUser(email);
        CartItem cartItem = shoppingCart.getCartItems().stream()
                .filter(item -> Objects.equals(id, item.getId()))
                .findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find cart item by id: " + id)
                );
        shoppingCart.getCartItems().remove(cartItem);
        shoppingCartRepository.save(shoppingCart);
    }

    private ShoppingCart findShoppingCartByUser(String email) {
        UserResponseDto responseDto = userService.getByEmail(email);
        return shoppingCartRepository.findByUserId(responseDto.getId())
                .orElseThrow(() -> new RuntimeException("Can't find shopping cart"));
    }
}
