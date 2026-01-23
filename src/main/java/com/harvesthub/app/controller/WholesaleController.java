package com.harvesthub.app.controller;

import com.harvesthub.app.model.Order;
import com.harvesthub.app.model.OrderItem;
import com.harvesthub.app.model.Product;
import com.harvesthub.app.model.User;
import com.harvesthub.app.repository.OrderRepository;
import com.harvesthub.app.repository.ProductRepository;
import com.harvesthub.app.repository.UserRepository;
import com.harvesthub.app.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/wholesale")
public class WholesaleController {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final OrderRepository orderRepository;

    public WholesaleController(ProductRepository productRepository, UserRepository userRepository, CartService cartService, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.orderRepository = orderRepository;
    }

    // 1. Wholesale Home Page (Shows ONLY Wholesale Items)
    @GetMapping("/home")
    public String wholesaleHome(Model model, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        model.addAttribute("username", user.getFullName());

        // FILTER: Only fetch WHOLESALE items
        model.addAttribute("products", productRepository.findByListingType("WHOLESALE"));

        return "wholesale/wholesale_home";
    }

    // 2. Add to Cart (Redirects back to Wholesale Home)
    @GetMapping("/add-to-cart/{id}")
    public String addToCart(@PathVariable Long id) {
        cartService.addToCart(id);
        return "redirect:/wholesale/home";
    }

    // 3. View Wholesale Cart
    @GetMapping("/cart")
    public String viewCart(Model model) {
        model.addAttribute("cartItems", cartService.getProductsInCart());
        model.addAttribute("total", cartService.getTotal());
        return "wholesale/wholesale_cart"; // We need a separate HTML for this
    }

    // 4. Checkout Logic (Same logic, but redirects to Wholesale Home)
    @GetMapping("/checkout")
    public String checkout(Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();

        Order order = new Order();
        order.setOrderDate(java.time.LocalDateTime.now());
        order.setStatus("WHOLESALE_CONFIRMED");
        order.setUser(user);
        order.setTotalAmount(cartService.getTotal());

        List<OrderItem> orderItems = new ArrayList<>();
        for (var entry : cartService.getProductsInCart().entrySet()) {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();

            if (product.getStock() < quantity) {
                return "redirect:/wholesale/cart?error=NotEnoughStock";
            }
            product.setStock(product.getStock() - quantity);
            productRepository.save(product);

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setPriceAtPurchase(product.getPrice());
            orderItems.add(item);
        }

        order.setItems(orderItems);
        orderRepository.save(order);
        cartService.clearCart();

        return "redirect:/wholesale/home?success=BulkOrderPlaced";
    }
}