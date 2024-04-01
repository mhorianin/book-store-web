package com.example.bookstoreweb.controller;

import com.example.bookstoreweb.dto.shoppingcart.CartItemDto;
import com.example.bookstoreweb.dto.shoppingcart.CartItemRequestDto;
import com.example.bookstoreweb.dto.shoppingcart.CartItemRequestUpdateDto;
import com.example.bookstoreweb.dto.shoppingcart.ShoppingCartDto;
import com.example.bookstoreweb.model.User;
import com.example.bookstoreweb.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoints for managing shopping carts")
@RestController
@RequestMapping(value = "/api/cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    @Operation(summary = "Get shopping cart for user",
            description = "Get a shopping cart for a specific user")
    public ShoppingCartDto getShoppingCartForUser(
            Authentication authentication, Pageable pageable
    ) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.getShoppingCartForUser(user.getEmail(), pageable);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    @Operation(summary = "Save new cart item", description = "Add new book to the shopping cart")
    public CartItemDto saveCartItem(Authentication authentication,
                                    @RequestBody @Valid CartItemRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.save(user.getEmail(), requestDto);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PutMapping("/cart-items/{id}")
    @Operation(summary = "Update cart item",
            description = "Update cart item")
    public CartItemDto updateCartItem(Authentication authentication, @PathVariable Long id,
                                      @RequestBody @Valid CartItemRequestUpdateDto requestUpdateDto
    ) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.update(user.getEmail(), id, requestUpdateDto);
    }

    @PreAuthorize("hasAuthority('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/cart-items/{id}")
    @Operation(summary = "Delete cart item",
            description = "Delete cart item from the shopping cart")
    public void deleteCartItem(Authentication authentication, @PathVariable Long id) {
        User user = (User) authentication.getPrincipal();
        shoppingCartService.deleteById(user.getEmail(), id);
    }
}
