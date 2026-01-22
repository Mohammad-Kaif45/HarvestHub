package com.harvesthub.app.repository;

import com.harvesthub.app.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 1. Find all products for a specific "Type" (Used by Shop Page)
    List<Product> findByListingType(String listingType);

    // 2. Find all products uploaded by a specific Farmer (Used by Farmer Dashboard)
    List<Product> findByFarmerId(Long farmerId);
}