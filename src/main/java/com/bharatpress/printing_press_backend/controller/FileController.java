package com.bharatpress.printing_press_backend.controller;


import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
public class FileController {

	 private final String uploadDir = "src/main/resources/static/order-photos"; // Update this path as needed

	    @GetMapping("/{filename}")
	    public ResponseEntity<Resource> getFile(@PathVariable String filename) throws MalformedURLException {
	        Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
	        Resource resource = new UrlResource(filePath.toUri());

	        if (resource.exists() || resource.isReadable()) {
	            return ResponseEntity.ok()
	                    .contentType(MediaType.IMAGE_JPEG)  // Adjust MIME type if needed
	                    .body(resource);
	        } else {
	            return ResponseEntity.notFound().build();
	        }
	    }
}
