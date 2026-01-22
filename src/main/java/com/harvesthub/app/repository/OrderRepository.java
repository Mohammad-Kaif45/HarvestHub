package com.harvesthub.app.repository;

import com.harvesthub.app.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // We can add methods later like findByUserId(Long userId)
}