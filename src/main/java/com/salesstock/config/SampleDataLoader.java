package com.salesstock.config;

import com.salesstock.entity.Product;
import com.salesstock.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

/**
 * Adds sample products on startup, only when
 * app.seed-sample-data=true is set in application.properties.
 * Products whose name already exists are skipped, so it is safe to leave on.
 */
@Configuration
@ConditionalOnProperty(name = "app.seed-sample-data", havingValue = "true")
public class SampleDataLoader {

    @Bean
    CommandLineRunner seedProducts(ProductRepository productRepository) {
        return args -> {
            List<Product> samples = List.of(
                    product("Dell Laptop", "Electronics", "45000.00", 10, 3),
                    product("Samsung Phone", "Electronics", "25000.00", 20, 5),
                    product("HP Wireless Mouse", "Accessories", "499.00", 100, 10),
                    product("Mechanical Keyboard", "Accessories", "2499.00", 40, 8),
                    product("Office Chair", "Furniture", "6500.00", 15, 3),
                    product("Study Table", "Furniture", "8200.00", 8, 2),
                    product("A4 Paper Pack", "Stationery", "250.00", 200, 20),
                    product("Ball Pen Box", "Stationery", "120.00", 150, 25),
                    product("LED Bulb 9W", "Electricals", "99.00", 300, 40),
                    product("Water Bottle 1L", "Home", "199.00", 60, 10)
            );

            for (Product sample : samples) {
                boolean exists = !productRepository
                        .findByNameContainingIgnoreCase(sample.getName())
                        .stream()
                        .filter(p -> p.getName().equalsIgnoreCase(sample.getName()))
                        .toList()
                        .isEmpty();
                if (!exists) {
                    productRepository.save(sample);
                }
            }
        };
    }

    private Product product(String name, String category, String price,
                            int quantity, int minimumStock) {
        Product p = new Product();
        p.setName(name);
        p.setCategory(category);
        p.setPrice(new BigDecimal(price));
        p.setQuantity(quantity);
        p.setMinimumStock(minimumStock);
        return p;
    }
}
