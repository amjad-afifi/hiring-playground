package com.celfocus.hiring.kickstarter.api;

import com.celfocus.hiring.kickstarter.db.repo.ProductRepository;
import com.celfocus.hiring.kickstarter.domain.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<? extends Product> getProducts() {
        logger.debug("Getting all products");
        return productRepository.findAll();
    }
    @Cacheable(value = "products", key = "#sku")
    public Optional<? extends Product> getProduct(String sku) {
        logger.debug("Getting product [{}]", sku);
        return productRepository.findBySku(sku);
    }
}
