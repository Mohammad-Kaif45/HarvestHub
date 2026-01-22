package com.harvesthub.app.service;

import com.harvesthub.app.model.Product;
import com.harvesthub.app.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashMap;
import java.util.Map;

@Service
@SessionScope // This magic annotation keeps the cart alive as long as the user is logged in
public class CartService {

    private final ProductRepository productRepository;

    // Map<ProductId, Quantity>
    private Map<Long, Integer> items = new HashMap<>();

    public CartService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void addToCart(Long productId) {
        // If item exists, add 1. If not, set to 1.
        items.put(productId, items.getOrDefault(productId, 0) + 1);
    }

    public Map<Product, Integer> getProductsInCart() {
        Map<Product, Integer> productMap = new HashMap<>();
        for (Map.Entry<Long, Integer> entry : items.entrySet()) {
            Product product = productRepository.findById(entry.getKey()).orElse(null);
            if (product != null) {
                productMap.put(product, entry.getValue());
            }
        }
        return productMap;
    }

    public Double getTotal() {
        return getProductsInCart().entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
    }

    public void clearCart() {
        items.clear();
    }
}