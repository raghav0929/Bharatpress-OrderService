package com.bharatpress.printing_press_backend.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(@Value("${cloudinary.cloud-name}") String cloudName,
                             @Value("${cloudinary.api-key}") String apiKey,
                             @Value("${cloudinary.api-secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }

    public String uploadImage(byte[] imageBytes) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(imageBytes, ObjectUtils.emptyMap());
        return uploadResult.get("url").toString(); // Get the URL of the uploaded image
    }
    
    public String extractPublicIdFromUrl(String imageUrl) {
    	 if (imageUrl == null || imageUrl.isEmpty()) {
    	        return null;
    	    }

    	    // Extract the filename from the URL
    	    String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

    	    // Remove the file extension
    	    return fileName.substring(0, fileName.lastIndexOf("."));
    }
    
    public void deleteImage(String publicId) {
        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            System.out.println("Cloudinary Delete Response: " + result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete image from Cloudinary");
        }
    }
}
