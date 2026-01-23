package com.harvesthub.app.controller;
import com.harvesthub.app.repository.OrderRepository;
import com.harvesthub.app.model.Order; // Don't forget this!
import java.util.List;
import com.harvesthub.app.model.Product;
import com.harvesthub.app.model.User;
import com.harvesthub.app.repository.ProductRepository;
import com.harvesthub.app.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/farmer")
public class FarmerController {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository; // Add this field

    public FarmerController(ProductRepository productRepository,
                            UserRepository userRepository,
                            OrderRepository orderRepository) { // Add to constructor
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    // 1. Show Dashboard with List of Products
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        // Get the logged-in Farmer's details
        String email = principal.getName();
        User farmer = userRepository.findByEmail(email).orElseThrow();

        // Fetch ONLY this farmer's products
        model.addAttribute("products", productRepository.findByFarmerId(farmer.getId()));
        model.addAttribute("farmerName", farmer.getFullName());

        return "farmer/farmer_dashboard";
    }

    // 2. Show the "Add Product" Page
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        return "farmer/product_add";
    }

    // 3. Handle the Form Submission (Save to DB)
    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product, Principal principal) {
        String email = principal.getName();
        User farmer = userRepository.findByEmail(email).orElseThrow();

        // Link the product to this farmer
        product.setFarmer(farmer);
        productRepository.save(product);

        return "redirect:/farmer/dashboard";
    }
    @GetMapping("/orders")
    public String viewSales(Model model, Principal principal) {
        String email = principal.getName();
        User farmer = userRepository.findByEmail(email).orElseThrow();

        // Use our magic query
        List<Order> sales = orderRepository.findOrdersByFarmerId(farmer.getId());

        model.addAttribute("orders", sales);
        return "farmer/sales_history";
    }
}