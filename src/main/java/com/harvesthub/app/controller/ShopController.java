package com.harvesthub.app.controller;

import com.harvesthub.app.model.Order;
import com.harvesthub.app.model.OrderItem;
import com.harvesthub.app.model.Product;
import com.harvesthub.app.model.User;
import com.harvesthub.app.repository.OrderRepository; // New Import
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
@RequestMapping("/retail")
public class ShopController {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final OrderRepository orderRepository; // Added for Checkout

    // Updated Constructor with ALL 4 dependencies
    public ShopController(ProductRepository productRepository,
                          UserRepository userRepository,
                          CartService cartService,
                          OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.orderRepository = orderRepository;
    }

    // 1. The Home Page
    @GetMapping("/home")
    public String retailHome(Model model, Principal principal) {
        String email = principal.getName();
        User currentUser = userRepository.findByEmail(email).orElse(new User());
        model.addAttribute("username", currentUser.getFullName());

        // Fetch ONLY products tagged as 'RETAIL'
        model.addAttribute("products", productRepository.findByListingType("RETAIL"));

        return "retail/retail_home";
    }

    // 2. The "Add to Cart" Action
    @GetMapping("/add-to-cart/{id}")
    public String addToCart(@PathVariable Long id) {
        cartService.addToCart(id);
        return "redirect:/retail/home";
    }

    // 3. The "View Cart" Page
    @GetMapping("/cart")
    public String viewCart(Model model) {
        model.addAttribute("cartItems", cartService.getProductsInCart());
        model.addAttribute("total", cartService.getTotal());
        return "retail/cart_page";
    }

    // 4. The CHECKOUT Logic (New!)
    @GetMapping("/checkout")
    public String checkout(Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();

        // A. Create the Order Object
        Order order = new Order();
        order.setOrderDate(java.time.LocalDateTime.now());
        order.setStatus("CONFIRMED");
        order.setUser(user);
        order.setTotalAmount(cartService.getTotal());

        // B. Convert Cart Items to Order Items & Reduce Stock
        List<OrderItem> orderItems = new ArrayList<>();

        for (var entry : cartService.getProductsInCart().entrySet()) {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();

            // Check if we have enough stock
            if (product.getStock() < quantity) {
                return "redirect:/retail/cart?error=NotEnoughStock";
            }

            // REDUCE STOCK in Database
            product.setStock(product.getStock() - quantity);
            productRepository.save(product);

            // Create OrderItem
            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setPriceAtPurchase(product.getPrice());
            orderItems.add(item);
        }

        // C. Save the Order to Database
        order.setItems(orderItems);
        orderRepository.save(order);

        // D. Clear Cart & Redirect
        cartService.clearCart();
        return "redirect:/retail/home?success=OrderPlaced";
    }

    // 5. The "My Orders" History Page
    @GetMapping("/orders")
    public String myOrders(Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();

        // Fetch orders for this logged-in user
        List<Order> myOrders = orderRepository.findByUserId(user.getId());

        model.addAttribute("orders", myOrders);
        return "retail/my_orders";
    }
}