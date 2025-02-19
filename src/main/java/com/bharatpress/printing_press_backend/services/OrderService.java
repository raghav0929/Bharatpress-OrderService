package com.bharatpress.printing_press_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bharatpress.printing_press_backend.Model.Order;
import com.bharatpress.printing_press_backend.Repository.OrderRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class OrderService {

    @Value("${file.upload-dir}")
    private String uploadDir; // Defined in application.properties

    private final OrderRepository orderRepository;

    private final CloudinaryService cloudinaryService; // Inject CloudinaryService

    @Autowired
    public OrderService(OrderRepository orderRepository, CloudinaryService cloudinaryService) {
        this.orderRepository = orderRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public Order saveOrder(Order order, MultipartFile file) throws IOException {
    	if (file != null && !file.isEmpty()) {
            // Ensure directory exists
    		String imageUrl = cloudinaryService.uploadImage(file.getBytes());

            // Save the Cloudinary URL in the database
            order.setOrderPhoto(imageUrl);
        }

        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public byte[] getImage(String fileName) throws IOException {
        Path imagePath = Paths.get(uploadDir, fileName);
        return Files.readAllBytes(imagePath);
    }

    public void deleteOrdersByIds(List<Long> orderIds) {
        List<Order> orders = orderRepository.findAllById(orderIds);

        for (Order order : orders) {
            String imageUrl = order.getOrderPhoto();
            
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // Extract Public ID from Cloudinary URL
                String publicId = cloudinaryService.extractPublicIdFromUrl(imageUrl);

                // Call Cloudinary Service to delete the image
                cloudinaryService.deleteImage(publicId);
            }
        }

        // Delete orders from database
        orderRepository.deleteAllById(orderIds);
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
            return orderRepository.save(order);
        }).orElseThrow(() -> new RuntimeException("Order not found"));
    }
}
