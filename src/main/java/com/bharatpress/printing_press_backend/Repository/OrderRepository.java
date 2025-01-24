package com.bharatpress.printing_press_backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bharatpress.printing_press_backend.Model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // You can define custom queries if needed, e.g.:
    // List<Order> findByCustomerName(String customerName);
}