package com.salesstock.service;

import com.salesstock.dto.SaleRequest;
import com.salesstock.entity.Product;
import com.salesstock.entity.Sale;
import com.salesstock.entity.SaleItem;
import com.salesstock.repository.ProductRepository;
import com.salesstock.repository.SaleRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class SaleService {

    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;

    public SaleService(
            ProductRepository productRepository,
            SaleRepository saleRepository) {

        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
    }

    @Transactional
    public Sale createSale(SaleRequest request) {

        if (request == null || request.getItems() == null
                || request.getItems().isEmpty()) {
            throw new RuntimeException(
                    "Sale must contain at least one product");
        }

        Sale sale = new Sale();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (SaleRequest.SaleLineRequest line : request.getItems()) {

            if (line.getProductId() == null || line.getQuantity() <= 0) {
                throw new RuntimeException(
                        "Invalid product ID or quantity");
            }

            Product product = productRepository
                    .findById(line.getProductId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Product not found: "
                                            + line.getProductId()));

            int quantity = line.getQuantity();

            if (product.getQuantity() < quantity) {
                throw new RuntimeException(
                        "Insufficient stock for product: "
                                + product.getName());
            }

            BigDecimal unitPrice = product.getPrice();

            BigDecimal subtotal = unitPrice.multiply(
                    BigDecimal.valueOf(quantity));

            SaleItem item = new SaleItem();

            item.setSale(sale);
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setUnitPrice(unitPrice);
            item.setSubtotal(subtotal);

            sale.getItems().add(item);

            totalAmount = totalAmount.add(subtotal);

            // Reduce the available stock
            product.setQuantity(product.getQuantity() - quantity);
        }

        sale.setTotalAmount(totalAmount);

        // Save the sale and its items
        return saleRepository.save(sale);
    }
}