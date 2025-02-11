package com.bharatpress.printing_press_backend.Config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
            	 registry.addMapping("/**") // Apply CORS to all endpoints
                 .allowedOrigins("http://localhost:4200") // Allow Angular frontend
                 .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow required methods
                 .allowedHeaders("*") // Allow all headers
                 .exposedHeaders("Authorization") // Expose Authorization header if using JWT
                 .allowCredentials(true) // Allow credentials
                 .maxAge(3600); // Cache CORS settings for 1 hour
                
            }
        };
    }
}
