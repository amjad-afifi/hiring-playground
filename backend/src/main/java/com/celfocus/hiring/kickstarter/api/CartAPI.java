package com.celfocus.hiring.kickstarter.api;

import com.celfocus.hiring.kickstarter.api.dto.AuthRequest;
import com.celfocus.hiring.kickstarter.api.dto.CartItemInput;
import com.celfocus.hiring.kickstarter.api.dto.CartResponse;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
public interface CartAPI {
    @PostMapping("/items")
    ResponseEntity<Void> addItemToCart(@Valid @RequestBody CartItemInput itemInput);

    @DeleteMapping
    ResponseEntity<Void> clearCart();

    @GetMapping
    ResponseEntity<CartResponse> getCart();

    @DeleteMapping("/items/{itemId}")
    ResponseEntity<Void> removeItemFromCart(@PathVariable("itemId") String itemId);
}
