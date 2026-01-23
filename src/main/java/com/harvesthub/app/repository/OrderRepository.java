package com.harvesthub.app.repository;

import com.harvesthub.app.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Import Query
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 1. Find orders for a Customer (You already added this)
    List<Order> findByUserId(Long userId);

    // 2. Find orders for a Farmer (The New Magic Query)
    @Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.product.farmer.id = :farmerId")
    List<Order> findOrdersByFarmerId(Long farmerId);
}