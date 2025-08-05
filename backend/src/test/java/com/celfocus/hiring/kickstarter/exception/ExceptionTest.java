package com.celfocus.hiring.kickstarter.exception;

import com.celfocus.hiring.kickstarter.api.CartAPIController;
import com.celfocus.hiring.kickstarter.api.CartService;
import com.celfocus.hiring.kickstarter.api.dto.CartItemInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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
        mockSecurityContextWithUser("john");
    }

    private void mockSecurityContextWithUser(String username) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetCartThrowsCartNotFoundException() {
        when(cartService.getCart("john"))
                .thenThrow(new CartNotFoundException("Cart not found"));

        assertThrows(CartNotFoundException.class, () -> cartAPIController.getCart());
    }

    @Test
    void testAddItemToCartThrowsItemNotFoundException() {
        CartItemInput input = new CartItemInput("invalidSku");

        doThrow(new ProductDoesNotExistException("Product does not exist"))
                .when(cartService).addItemToCart("john", input);

        assertThrows(ProductDoesNotExistException.class, () -> cartAPIController.addItemToCart(input));
    }

    @Test
    void testAddItemToCartThrowsInsufficientStockException() {
        CartItemInput input = new CartItemInput("sku123");

        doThrow(new InsufficientStockException("Out of stock"))
                .when(cartService).addItemToCart("john", input);

        assertThrows(InsufficientStockException.class, () -> cartAPIController.addItemToCart(input));
    }

    @Test
    void testRemoveItemFromCartThrowsItemNotFoundException() {
        doThrow(new ItemNotFoundException("Item not found in cart"))
                .when(cartService).removeItemFromCart("john", "sku123");

        assertThrows(ItemNotFoundException.class, () -> cartAPIController.removeItemFromCart("sku123"));
    }

    @Test
    void testClearCartThrowsCartNotFoundException() {
        doThrow(new CartNotFoundException("Cart not found"))
                .when(cartService).clearCart("john");

        assertThrows(CartNotFoundException.class, () -> cartAPIController.clearCart());
    }
}
