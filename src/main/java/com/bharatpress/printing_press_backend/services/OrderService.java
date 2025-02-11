package com.bharatpress.printing_press_backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bharatpress.printing_press_backend.Model.Order;
import com.bharatpress.printing_press_backend.Repository.OrderRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
	 @Value("${file.upload-dir}")
	    private String uploadDir;

	    private final OrderRepository orderRepository;

	    public OrderService(OrderRepository orderRepository) {
	        this.orderRepository = orderRepository;
	    }

	    public Order saveOrder(Order order, MultipartFile file) throws IOException {
	        // Validate the file
	        if (file == null || file.isEmpty()) {
	            throw new IllegalArgumentException("File must not be empty.");
	        }

	        // Generate a unique filename to avoid conflicts
	        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
	        File destinationFile = new File(uploadDir + File.separator + fileName);

	        // Create the upload directory if it doesn't exist
	        if (!destinationFile.getParentFile().exists()) {
	            boolean created = destinationFile.getParentFile().mkdirs();
	            if (!created) {
	                throw new IOException("Failed to create upload directory.");
	            }
	        }

	        // Save the file to the local directory
	        file.transferTo(destinationFile);

	        // Save the file path in the database
	        order.setOrderPhoto(fileName);

	        // Save the order in the database
	        return orderRepository.save(order);
	    }
	    
	    public List<Order> getAllOrders() {
	        return orderRepository.findAll();
	    }
	    
	    public Order updateOrder(Long id, Order updatedOrder) {
	        return orderRepository.findById(id).map(order -> {
	            order.setOrderDetails(updatedOrder.getOrderDetails());
	            order.setPhoneNumber(updatedOrder.getPhoneNumber());
	            order.setStatus(updatedOrder.getStatus());
	            order.setOrderDate(updatedOrder.getOrderDate());
	            order.setEstimatedDays(updatedOrder.getEstimatedDays());
	            order.setPriority(updatedOrder.getPriority());
	            order.setTotalAmount(updatedOrder.getTotalAmount());
	            order.setAdvance(updatedOrder.getAdvance());
	            order.setRemainingBalance(updatedOrder.getRemainingBalance());
	            // Do NOT update order photo
	            return orderRepository.save(order);
	        }).orElseThrow(() -> new RuntimeException("Order not found"));
	    }
	    
}