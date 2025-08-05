package com.celfocus.hiring.kickstarter.service;

/**
 * @author amjad.afifi
 */

import com.celfocus.hiring.kickstarter.api.CartService;
import com.celfocus.hiring.kickstarter.api.dto.CartItemInput;
import com.celfocus.hiring.kickstarter.db.entity.CartEntity;
import com.celfocus.hiring.kickstarter.db.entity.CartItemEntity;
import com.celfocus.hiring.kickstarter.db.entity.CartItemPK;
import com.celfocus.hiring.kickstarter.db.entity.ProductEntity;
import com.celfocus.hiring.kickstarter.db.repo.CartItemRepository;
import com.celfocus.hiring.kickstarter.db.repo.CartRepository;
import com.celfocus.hiring.kickstarter.db.repo.ProductRepository;
import com.celfocus.hiring.kickstarter.domain.Product;
import com.celfocus.hiring.kickstarter.exception.CartNotFoundException;
import com.celfocus.hiring.kickstarter.exception.InsufficientStockException;
import com.celfocus.hiring.kickstarter.exception.ItemNotFoundException;
import com.celfocus.hiring.kickstarter.exception.ProductDoesNotExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddNewItemToCartSuccess() {
        CartEntity cart = new CartEntity();
        cart.setId(1L);
        cart.setUserId("john");

        ProductEntity productEntity = new ProductEntity();
        productEntity.setSku("sku123");
        productEntity.setQuantity(3);

        when(cartRepository.findByUserId("john")).thenReturn(Optional.of(cart));
        when(productRepository.findBySku("sku123")).thenReturn(Optional.of(productEntity));
        when(cartItemRepository.findById(any())).thenReturn(Optional.empty());

        cartService.addItemToCart("john", new CartItemInput("sku123"));

        verify(cartItemRepository).save(any(CartItemEntity.class));
    }

    @Test
    void testAddItemProductNotExist() {
        when(cartRepository.findByUserId("john")).thenReturn(Optional.of(new CartEntity()));
        when(productRepository.findBySku("sku123")).thenReturn(Optional.empty());

        assertThrows(ProductDoesNotExistException.class, () ->
                cartService.addItemToCart("john", new CartItemInput("sku123")));
    }

    @Test
    void testAddItemInsufficientStockNewItem() {
        CartEntity cart = new CartEntity();
        cart.setId(1L);

        ProductEntity productEntity = new ProductEntity();
        productEntity.setSku("sku123");
        productEntity.setQuantity(0);

        when(cartRepository.findByUserId("john")).thenReturn(Optional.of(cart));
        when(productRepository.findBySku("sku123")).thenReturn(Optional.of(productEntity));
        when(cartItemRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(InsufficientStockException.class, () ->
                cartService.addItemToCart("john", new CartItemInput("sku123")));
    }

    @Test
    void testClearCartSuccess() {
        CartEntity cart = new CartEntity();
        cart.setUserId("john");
        when(cartRepository.findByUserId("john")).thenReturn(Optional.of(cart));

        cartService.clearCart("john");
        verify(cartRepository).delete(cart);
    }

    @Test
    void testClearCartNotFound() {
        when(cartRepository.findByUserId("john")).thenReturn(Optional.empty());
        assertThrows(CartNotFoundException.class, () -> cartService.clearCart("john"));
    }

    @Test
    void testRemoveItemFromCartSuccess() {
        CartEntity cart = new CartEntity();
        cart.setId(1L);
        cart.setUserId("john");

        when(cartRepository.findByUserId("john")).thenReturn(Optional.of(cart));
        when(cartItemRepository.existsById(any(CartItemPK.class))).thenReturn(true);

        cartService.removeItemFromCart("john", "sku123");
        verify(cartItemRepository).deleteById(any(CartItemPK.class));
    }

    @Test
    void testRemoveItemNotFoundInCart() {
        CartEntity cart = new CartEntity();
        cart.setId(1L);

        when(cartRepository.findByUserId("john")).thenReturn(Optional.of(cart));
        when(cartItemRepository.existsById(any(CartItemPK.class))).thenReturn(false);

        assertThrows(ItemNotFoundException.class, () -> cartService.removeItemFromCart("john", "sku123"));
    }

    @Test
    void testRemoveItemCartNotFound() {
        when(cartRepository.findByUserId("john")).thenReturn(Optional.empty());
        assertThrows(CartNotFoundException.class, () -> cartService.removeItemFromCart("john", "sku123"));
    }
}