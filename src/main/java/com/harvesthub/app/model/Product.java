package com.harvesthub.app.model;

import jakarta.persistence.*;
import lombok.Data; // Or generate Getters/Setters manually like you did for User

@Entity
@Data
@Table(name = "products")
public class Product {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getListingType() {
        return listingType;
    }

    public void setListingType(String listingType) {
        this.listingType = listingType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public User getFarmer() {
        return farmer;
    }

    public void setFarmer(User farmer) {
        this.farmer = farmer;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;        // e.g., "Potato (Agra)"

    private String description; // e.g., "Fresh organic potatoes"

    @Column(nullable = false)
    private Double price;       // e.g., 40.0

    @Column(nullable = false)
    private Integer stock;      // e.g., 500 (kg)

    @Column(nullable = false)
    private String listingType; // Values: "RETAIL" or "WHOLESALE"

    private String imageUrl;    // We will just store the filename for now

    // We link this product to the Farmer who added it
    @ManyToOne
    @JoinColumn(name = "farmer_id", nullable = false)
    private User farmer;
}