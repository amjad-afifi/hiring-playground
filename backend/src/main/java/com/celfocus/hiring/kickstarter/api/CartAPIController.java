package com.celfocus.hiring.kickstarter.api;

import com.celfocus.hiring.kickstarter.api.dto.CartItemInput;
import com.celfocus.hiring.kickstarter.api.dto.CartItemResponse;
import com.celfocus.hiring.kickstarter.api.dto.CartResponse;
import com.celfocus.hiring.kickstarter.domain.Cart;
import com.celfocus.hiring.kickstarter.domain.CartItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping(CartAPIController.CARTS_PATH)
public class CartAPIController implements CartAPI {

    private static final Logger logger = LoggerFactory.getLogger(CartAPIController.class);
    static final String CARTS_PATH = "/api/v1/carts";
    private final CartService cartService;
    private final ProductService productService;

    @Autowired
    public CartAPIController(CartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    @GetMapping("/")
    public String index() {
        return "Greetings from Celfocus!";
    }

    @Override
    public ResponseEntity<Void> addItemToCart(String username, CartItemInput itemInput) {
        logger.info("Adding item [{}] in cart for user [{}]", itemInput.itemId() ,username);
        cartService.addItemToCart(username, itemInput);
        logger.info("Successfully added item [{}] in cart for user [{}]", itemInput.itemId() ,username);
        return ResponseEntity.status(201).build();
    }

    @Override
    public ResponseEntity<Void> clearCart(String username) {
        logger.info("Request to clear cart for user [{}] ",username);
        cartService.clearCart(username);
        logger.info("Cart cleared successfully for [{}]", username);
        return ResponseEntity.status(204).build();
    }

    @Override
    public ResponseEntity<CartResponse> getCart(String username) {
        logger.info("GET cart for user : [{}]", username);
        var cart = cartService.getCart(username);
        logger.info("Cart retrieved successfully for user: [{}]", username);
        return ResponseEntity.ok(mapToCartResponse(cart));
    }

    @Override
    public ResponseEntity<Void> removeItemFromCart(String username, String itemId) {
        logger.info("Removing item [{}] from cart for user :[{}]", itemId, username);
        cartService.removeItemFromCart(username, itemId);
        logger.info("Item [{}] removed successfully from cart for user :[{}]", itemId, username);
        return ResponseEntity.status(204).build();
    }

    private CartResponse mapToCartResponse(Cart<? extends CartItem> cart) {

        return new CartResponse(cart.getItems().stream().map(this::mapToCartItemResponse).collect(Collectors.toList()));
    }

    private CartItemResponse mapToCartItemResponse(CartItem item) {
        var product = productService.getProduct(item.getItemId());
        return new CartItemResponse(item.getItemId(), item.getQuantity(), product.orElseThrow().getPrice(), product.orElseThrow().getName());
    }
}
