package com.celfocus.hiring.kickstarter.api;

import com.celfocus.hiring.kickstarter.db.entity.CartEntity;
import com.celfocus.hiring.kickstarter.db.repo.CartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.List;

/**
 * @author amjad.afifi
 */
@Service
public class CartCleanupJob {

    private static final Logger logger = LoggerFactory.getLogger(CartCleanupJob.class);

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartService cartService;

    private static final long EXPIRATION_MS = Duration.ofDays(1).toMillis();
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void removeInactiveCarts() {
        logger.info("Cart cleanup job running...");
        Date expirationCutoff = new Date(System.currentTimeMillis() - EXPIRATION_MS);

        List<CartEntity> oldCarts = cartRepository.findAllByLastModifiedBefore(expirationCutoff);

        for (CartEntity cart : oldCarts) {
            cartService.clearCart(cart.getUserId());
        }
    }
}