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

    public WholesaleController(ProductRepository productRepository,
                               UserRepository userRepository,
                               CartService cartService,
                               OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/home")
    public String wholesaleHome(Model model, Principal principal) {
        String email = principal.getName();
        User currentUser = userRepository.findByEmail(email).orElse(new User());
        model.addAttribute("username", currentUser.getFullName());

        // Fetch ONLY products tagged as 'WHOLESALE'
        model.addAttribute("products", productRepository.findByListingType("WHOLESALE"));
        return "wholesale/wholesale_home";
    }

    @GetMapping("/add-to-cart/{id}")
    public String addToCart(@PathVariable Long id) {
        cartService.addToCart(id);
        return "redirect:/wholesale/home";
    }

    @GetMapping("/cart")
    public String viewCart(Model model) {
        model.addAttribute("cartItems", cartService.getProductsInCart());
        model.addAttribute("total", cartService.getTotal());
        return "wholesale/wholesale_cart";
    }

    @GetMapping("/checkout")
    public String checkout(Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();

        Order order = new Order();
        order.setOrderDate(java.time.LocalDateTime.now());
        order.setStatus("CONFIRMED");
        order.setUser(user);
        order.setTotalAmount(cartService.getTotal()); // BigDecimal

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
            item.setPriceAtPurchase(product.getPrice()); // BigDecimal
            orderItems.add(item);
        }

        order.setItems(orderItems);
        orderRepository.save(order);
        cartService.clearCart();

        return "redirect:/wholesale/home?success=OrderPlaced";
    }
}