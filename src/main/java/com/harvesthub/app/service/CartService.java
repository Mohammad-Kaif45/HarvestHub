package com.harvesthub.app.service;

import com.harvesthub.app.model.Product;
import com.harvesthub.app.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal; // Import BigDecimal
import java.util.HashMap;
import java.util.Map;

@Service
@SessionScope
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

    // UPDATED: Now returns BigDecimal and uses precise math
    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Product, Integer> entry : getProductsInCart().entrySet()) {
            BigDecimal price = entry.getKey().getPrice();
            BigDecimal quantity = BigDecimal.valueOf(entry.getValue());

            // total = total + (price * quantity)
            total = total.add(price.multiply(quantity));
        }

        return total;
    }

    public void clearCart() {
        items.clear();
    }
}