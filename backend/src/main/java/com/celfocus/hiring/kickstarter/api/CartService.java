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
import com.celfocus.hiring.kickstarter.exception.ProductDoesNotExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class CartService {
    private static final Logger logger = LoggerFactory.getLogger(CartService.class);
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @CacheEvict(value = "cart", key = "#username")
    public void addItemToCart(String username, CartItemInput itemInput) {
        logger.trace("In method addItemToCart");
        var cart = cartRepository.findByUserId(username).orElseGet(() -> {
            logger.debug("No cart found for user: [{}], creating new cart", username);
            var newCart = new CartEntity();
            newCart.setUserId(username);
            newCart.setLastModified(new Date());
            return cartRepository.save(newCart);
        });

        logger.debug("Cart ID [{}] loaded for user [{}]", cart.getId(), username);
        var product = productRepository.findBySku(itemInput.itemId())
                .orElseThrow(() -> {
                    logger.error("Product with SKU [{}] does not exist", itemInput.itemId());
                    return new ProductDoesNotExistException("Product does not exist");}
                );

        logger.debug("Found product [{}] with price [{}]", product.getSku(), product.getPrice());

        cartItemRepository.findById(new CartItemPK(itemInput.itemId(), cart.getId()))
                .ifPresentOrElse(
                        (existingItem) -> {
                        int newQuantity = existingItem.getQuantity() + 1;
                            if(newQuantity > product.getQuantity()) {
                                logger.error("Cannot add more items. Requested [{}], Available [{}]",
                                        newQuantity, product.getQuantity());
                                throw new InsufficientStockException("Not enough stock to add more of this item");
                            }
                                logger.info("Item already exists in cart. Updating quantity.");
                                updateItemQuantity(existingItem, 1);
                        }, () -> {

                            if(product.getQuantity() < 1) {
                                logger.error("Not enough stock to add this item. Item: [{}]. Quantity [{}]",
                                        itemInput.itemId(), product.getQuantity());
                                throw new InsufficientStockException("Not enough stock to add this item");
                            }
                            logger.info("Item not in cart. Adding new item.");
                            addNewItemToCart(itemInput, cart, product);
                });
        cart.setLastModified(new Date());
        cartRepository.save(cart);
        logger.debug("Finished addItemToCart for user: [{}]", username);
    }

    private void addNewItemToCart(CartItemInput itemInput, CartEntity cart, Product product) {
        var cartItem = new CartItemEntity();
        cartItem.setQuantity(1);
        cartItem.setItemId(itemInput.itemId());
        cartItem.setCartId(cart.getId());
        cartItem.setCart(cart);
        cartItem.setPrice(product.getPrice());
        cartItemRepository.save(cartItem);
        logger.debug("Finished Adding new item to cart: [{}]. Cart: [{}] and Product [{}]", itemInput, cart.getId(), product.getSku());
    }

    private void updateItemQuantity(CartItemEntity item, int byCount) {
        setItemQuantity(item, item.getQuantity() + byCount);
        logger.debug("Updating item [{}] to quantity [{}]", item.getItemId(), item.getQuantity() + byCount);
    }

    private void setItemQuantity(CartItemEntity item, int quantity) {
        logger.debug("Setting item [{}] to quantity [{}]", item.getItemId(), item.getQuantity());
        item.setQuantity(quantity);
        cartItemRepository.save(item);
        logger.debug("Item [{}] quantity set to [{}] successfully", item.getItemId(), item.getQuantity());
    }
    @CacheEvict(value = "cart", key = "#username")
    public void clearCart(String username) {
        logger.debug("Clearing cart for user: [{}]", username);
        var cart = cartRepository.findByUserId(username)
                .orElseThrow(() -> {
                    logger.error("Cart not found for user: [{}]", username);
                    return new CartNotFoundException("Cart not found for user: " + username);
                });

        logger.debug("Deleting cart with ID [{}] for user [{}]", cart.getId(), username);
        cartRepository.delete(cart);
        cart.setLastModified(new Date());
        logger.info("Cart cleared successfully for user: [{}]", username);
    }

    @Cacheable(value = "cart", key = "#username")
    public Cart<? extends CartItem> getCart(String username) {
        logger.debug("Getting cart for user: [{}]", username);
        return cartRepository.findByUserId(username)
                .map(this::mapToCart)
                .orElseThrow(() -> {
                    logger.error("Could not get cart for [{}]. Cart not found" , username);
                    return new CartNotFoundException("Cart not found for user: " + username);
                });
    }

    @CacheEvict(value = "cart", key = "#username")
    public void removeItemFromCart(String username, String itemId) {
        logger.debug("Remove item [{}] from cart for user: [{}]", itemId, username);
        var cart = cartRepository.findByUserId(username)
                .orElseThrow(() -> {
                    logger.warn("Cart not found for user [{}] while removing item", username);
                    return new CartNotFoundException("Cart not found for user: " + username);
                }
        );

        logger.debug("Found cart for user: [{}]", username);
        var cartItemId = new CartItemPK(itemId, cart.getId());

        if (!cartItemRepository.existsById(cartItemId)) {
            logger.debug("Removing item [{}] from cart for user: [{}]", itemId, username);
            throw new ItemNotFoundException("Item not found in cart");
        }

        cartItemRepository.deleteById(cartItemId);
        cart.setLastModified(new Date());
        cartRepository.save(cart);
        logger.info("Item [{}] removed from cart for user [{}]", itemId, username);
    }

    private Cart<? extends CartItem> mapToCart(CartEntity cartEntity) {
        logger.debug("Mapping cart...");
        Cart<CartItemEntity> cart = new Cart<>();
        cart.setUserId(cartEntity.getUserId());
        cart.setItems(cartEntity.getItems());
        return cart;
    }
}
