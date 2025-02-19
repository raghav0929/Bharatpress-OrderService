package com.bharatpress.printing_press_backend.services;

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

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order saveOrder(Order order, MultipartFile file) throws IOException {
    	if (file != null && !file.isEmpty()) {
            // Ensure directory exists
            File uploadDirectory = new File(uploadDir);
            if (!uploadDirectory.exists() && !uploadDirectory.mkdirs()) {
                throw new IOException("Could not create upload directory: " + uploadDir);
            }

            // Generate unique filename and save
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path destinationPath = Path.of(uploadDir, fileName);

            // Use Files.copy instead of transferTo
            Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            
            // Save file name in database (not full path)
            order.setOrderPhoto(fileName);
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
            String fileName = order.getOrderPhoto();
            if (fileName != null && !fileName.isEmpty()) {
                Path filePath = Paths.get(uploadDir, fileName);
                try {
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

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
