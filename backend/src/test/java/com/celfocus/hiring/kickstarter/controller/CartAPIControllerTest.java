package com.celfocus.hiring.kickstarter.controller;

/**
 * @author amjad.afifi
 */

import com.celfocus.hiring.kickstarter.api.CartAPIController;
import com.celfocus.hiring.kickstarter.api.CartService;
import com.celfocus.hiring.kickstarter.api.ProductService;
import com.celfocus.hiring.kickstarter.api.dto.CartItemInput;
import com.celfocus.hiring.kickstarter.api.dto.CartItemResponse;
import com.celfocus.hiring.kickstarter.api.dto.CartResponse;
import com.celfocus.hiring.kickstarter.domain.Cart;
import com.celfocus.hiring.kickstarter.domain.CartItem;
import com.celfocus.hiring.kickstarter.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartAPIControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartAPIController cartAPIController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIndex() {
        assertEquals("Greetings from Celfocus!", cartAPIController.index());
    }

    @Test
    void testAddItemToCart() {
        CartItemInput input = new CartItemInput("sku123");
        ResponseEntity<Void> response = cartAPIController.addItemToCart("john", input);
        assertEquals(201, response.getStatusCodeValue());
        verify(cartService).addItemToCart("john", input);
    }

    @Test
    void testClearCart() {
        ResponseEntity<Void> response = cartAPIController.clearCart("john");
        assertEquals(204, response.getStatusCodeValue());
        verify(cartService).clearCart("john");
    }

    @Test
    void testGetCart() {
        CartItem item = new CartItem() {
            public String getItemId() {
                return "sku123";
            }

            public Integer getQuantity() {
                return 2;
            }
        };
        Cart<CartItem> cart = new Cart<>();
        cart.setUserId("john");
        cart.setItems(List.of(item));

        Product product = new Product("Laptop", "sku123", "A test laptop",
                BigDecimal.valueOf(1000), "imageUrl");

        doReturn(cart).when(cartService).getCart("john");
        doReturn(Optional.of(product))
                .when(productService)
                .getProduct("sku123");

        ResponseEntity<CartResponse> response = cartAPIController.getCart("john");

        assertEquals(200, response.getStatusCodeValue());
        CartResponse cartResponse = response.getBody();
        assertNotNull(cartResponse);
        assertEquals(1, cartResponse.items().size());
        CartItemResponse itemResponse = cartResponse.items().get(0);
        assertEquals("sku123", itemResponse.itemId());
        assertEquals(2, itemResponse.quantity());
        assertEquals(BigDecimal.valueOf(1000), itemResponse.price());
        assertEquals("Laptop", itemResponse.name());
    }

    @Test
    void testRemoveItemFromCart() {
        ResponseEntity<Void> response = cartAPIController.removeItemFromCart("john", "sku123");
        assertEquals(204, response.getStatusCodeValue());
        verify(cartService).removeItemFromCart("john", "sku123");
    }

    static class StubCartItem extends CartItem {
        @Override
        public String getItemId() {
            return "sku123";
        }

        @Override
        public Integer getQuantity() {
            return 2;
        }
    }
}
