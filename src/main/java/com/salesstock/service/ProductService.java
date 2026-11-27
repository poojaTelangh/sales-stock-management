
package com.salesstock.service;

import com.salesstock.entity.Product;
import com.salesstock.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    // Constructor injection
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Add a new product
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    // Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Get product by ID
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found with ID: " + id));
    }

    // Update product
    public Product updateProduct(Long id, Product updatedProduct) {

        Product existingProduct = getProductById(id);

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setQuantity(updatedProduct.getQuantity());
        existingProduct.setMinimumStock(updatedProduct.getMinimumStock());

        return productRepository.save(existingProduct);
    }

    // Delete product
    public void deleteProduct(Long id) {
        Product existingProduct = getProductById(id);
        productRepository.delete(existingProduct);
    }
}

