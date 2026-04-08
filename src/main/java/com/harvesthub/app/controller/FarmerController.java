package com.harvesthub.app.controller;

import com.harvesthub.app.model.Product; 
import com.harvesthub.app.model.User;
import com.harvesthub.app.model.Order;
import com.harvesthub.app.repository.ProductRepository;
import com.harvesthub.app.repository.UserRepository;
import com.harvesthub.app.repository.OrderRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/farmer")
public class FarmerController {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public FarmerController(ProductRepository productRepository,
                            UserRepository userRepository,
                            OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    // 1. Dashboard (View Crops)
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        String email = principal.getName();
        User farmer = userRepository.findByEmail(email).orElseThrow();
        model.addAttribute("username", farmer.getFullName());
        model.addAttribute("products", productRepository.findByFarmerId(farmer.getId()));
        return "farmer/farmer_dashboard";
    }

    // 2. Show "Add Crop" Form
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        return "farmer/add_product";
    }

    // 3. SAVE THE CROP (This was missing!)
    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product product,
                              @RequestParam("imageFile") MultipartFile file,
                              Principal principal) {

        try {
            // 1. Link Farmer
            User farmer = userRepository.findByEmail(principal.getName()).orElseThrow();
            product.setFarmer(farmer);

            // 2. Process Image Upload
            if (!file.isEmpty()) {
                // Convert the uploaded file to a Base64 String
                String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
                product.setImageBase64(base64Image);
            }

            // 3. Save to Database
            productRepository.save(product);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/farmer/dashboard";
    }

    // 4. Delete Crop
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return "redirect:/farmer/dashboard";
    }

    // 5. Edit Crop
    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id).orElseThrow();
        model.addAttribute("product", product);
        return "farmer/add_product";
    }

    // 6. View Sales History
    @GetMapping("/orders")
    public String viewSales(Model model, Principal principal) {
        User farmer = userRepository.findByEmail(principal.getName()).orElseThrow();
        List<Order> sales = orderRepository.findOrdersByFarmerId(farmer.getId());
        model.addAttribute("orders", sales);
        return "farmer/sales_history";
    }
}
