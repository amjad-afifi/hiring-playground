package com.celfocus.hiring.kickstarter.exception;

import com.celfocus.hiring.kickstarter.api.CartAPIController;
import com.celfocus.hiring.kickstarter.api.CartService;
import com.celfocus.hiring.kickstarter.api.dto.CartItemInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * @author amjad.afifi
 */
public class ExceptionTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartAPIController cartAPIController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testGetCartThrowsCartNotFoundException() {
        when(cartService.getCart("john"))
                .thenThrow(new CartNotFoundException("Cart not found"));

        assertThrows(CartNotFoundException.class, () -> cartAPIController.getCart("john"));
    }

    @Test
    void testAddItemToCartThrowsItemNotFoundException() {
        CartItemInput input = new CartItemInput("invalidSku");

        doThrow(new ProductDoesNotExistException("Product does not exist"))
                .when(cartService).addItemToCart("john", input);

        assertThrows(ProductDoesNotExistException.class, () -> cartAPIController.addItemToCart("john", input));
    }

    @Test
    void testAddItemToCartThrowsInsufficientStockException() {
        CartItemInput input = new CartItemInput("sku123");

        doThrow(new InsufficientStockException("Out of stock"))
                .when(cartService).addItemToCart("john", input);

        assertThrows(InsufficientStockException.class, () -> cartAPIController.addItemToCart("john", input));
    }

    @Test
    void testRemoveItemFromCartThrowsItemNotFoundException() {
        doThrow(new ItemNotFoundException("Item not found in cart"))
                .when(cartService).removeItemFromCart("john", "sku123");

        assertThrows(ItemNotFoundException.class, () -> cartAPIController.removeItemFromCart("john", "sku123"));
    }

    @Test
    void testClearCartThrowsCartNotFoundException() {
        doThrow(new CartNotFoundException("Cart not found"))
                .when(cartService).clearCart("john");

        assertThrows(CartNotFoundException.class, () -> cartAPIController.clearCart("john"));
    }

}
