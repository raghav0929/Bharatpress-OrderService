package com.bharatpress.printing_press_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.bharatpress.printing_press_backend.Model.Order;
import com.bharatpress.printing_press_backend.services.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
	 private final OrderService orderService;

	    public OrderController(OrderService orderService) {
	        this.orderService = orderService;
	    }

	    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	    public ResponseEntity<String> submitOrder(
	            @RequestPart("order") String orderJson,
	            @RequestPart("file") MultipartFile file) {
	        try {
	            // Use the pre-configured ObjectMapper bean
	            ObjectMapper objectMapper = new ObjectMapper();
	            objectMapper.registerModule(new JavaTimeModule()); // Ensure LocalDate support
	            Order order = objectMapper.readValue(orderJson, Order.class);

	            // Process the order and file
	            orderService.saveOrder(order, file);

	            return ResponseEntity.ok("Order submitted successfully.");
	        } catch (Exception e) {
	            // Log the error and return an appropriate response
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("Failed to submit order: " + e.getMessage());
	        }
	    }
	    
	    @GetMapping("/all")
	    public ResponseEntity<List<Order>> getAllOrders() {
	        try {
	            List<Order> orders = orderService.getAllOrders();
	            return ResponseEntity.ok(orders);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(null);
	        }
	    }
	    
	    
	    @PutMapping("/{id}")
	    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order updatedOrder) {
	        try {
	            Order order = orderService.updateOrder(id, updatedOrder);
	            return ResponseEntity.ok(order);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(null);
	        }
	    }
	    
	    @PostMapping("/delete-multiple")
	    public ResponseEntity<Void> deleteMultipleOrders(@RequestBody Map<String, List<Long>> request) {
	        List<Long> orderIds = request.get("orderIds");
	        orderService.deleteOrdersByIds(orderIds);
	        return ResponseEntity.noContent().build();
	    }
	    
}