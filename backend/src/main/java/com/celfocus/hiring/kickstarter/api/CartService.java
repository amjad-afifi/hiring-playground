package com.celfocus.hiring.kickstarter.api;

import com.celfocus.hiring.kickstarter.api.dto.CartItemInput;
import com.celfocus.hiring.kickstarter.db.entity.CartEntity;
import com.celfocus.hiring.kickstarter.db.entity.CartItemEntity;
import com.celfocus.hiring.kickstarter.db.entity.CartItemPK;
import com.celfocus.hiring.kickstarter.db.repo.CartItemRepository;
import com.celfocus.hiring.kickstarter.db.repo.CartRepository;
import com.celfocus.hiring.kickstarter.db.repo.ProductRepository;
import com.celfocus.hiring.kickstarter.domain.Cart;
import com.celfocus.hiring.kickstarter.domain.CartItem;
import com.celfocus.hiring.kickstarter.domain.Product;
import com.celfocus.hiring.kickstarter.exception.CartNotFoundException;
import com.celfocus.hiring.kickstarter.exception.InsufficientStockException;
import com.celfocus.hiring.kickstarter.exception.ItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public void addItemToCart(String username, CartItemInput itemInput) {
        var cart = cartRepository.findByUserId(username).orElseGet(() -> {
            var newCart = new CartEntity();
            newCart.setUserId(username);
            return cartRepository.save(newCart);
        });

        var product = productRepository.findBySku(itemInput.itemId())
                .orElseThrow(() -> new ItemNotFoundException("Cart Item not found for user: " + username));

        cartItemRepository.findById(new CartItemPK(itemInput.itemId(), cart.getId()))
                .ifPresentOrElse(
                        (existingItem) -> {
                        int newQuantity = existingItem.getQuantity() + 1;
                        if(newQuantity > product.getQuantity()) {
                            throw new InsufficientStockException("Not enough stock to add more of this item");
                        }
                            updateItemQuantity(existingItem, 1);
                        }, () -> {
                            if(product.getQuantity() < 1) {
                                throw new InsufficientStockException("Not enough stock to add this item");
                            }
                    addNewItemToCart(itemInput, cart, product);
                });
    }

    private void addNewItemToCart(CartItemInput itemInput, CartEntity cart, Product product) {
        var cartItem = new CartItemEntity();
        cartItem.setQuantity(1);
        cartItem.setItemId(itemInput.itemId());
        cartItem.setCartId(cart.getId());
        cartItem.setCart(cart);
        cartItem.setPrice(product.getPrice());
        cartItemRepository.save(cartItem);
    }

    private void updateItemQuantity(CartItemEntity item, int byCount) {
        setItemQuantity(item, item.getQuantity() + byCount);
    }

    private void setItemQuantity(CartItemEntity item, int quantity) {
        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }

    public void clearCart(String username) {
        var cart = cartRepository.findByUserId(username)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + username));

        cartRepository.delete(cart);
    }

    public Cart<? extends CartItem> getCart(String username) {
        return cartRepository.findByUserId(username)
                .map(this::mapToCart)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + username));
    }
    public void removeItemFromCart(String username, String itemId) {
        var cart = cartRepository.findByUserId(username)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + username));

        var cartItemId = new CartItemPK(itemId, cart.getId());

        if (!cartItemRepository.existsById(cartItemId)) {
            throw new ItemNotFoundException("Item not found in cart");
        }

        cartItemRepository.deleteById(cartItemId);
    }

    private Cart<? extends CartItem> mapToCart(CartEntity cartEntity) {
        Cart<CartItemEntity> cart = new Cart<>();
        cart.setUserId(cartEntity.getUserId());
        cart.setItems(cartEntity.getItems());
        return cart;
    }
}
