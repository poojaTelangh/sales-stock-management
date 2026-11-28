package com.salesstock.service;

import com.salesstock.dto.SaleRequest;
import com.salesstock.dto.SaleResponse;
import com.salesstock.entity.Product;
import com.salesstock.entity.Sale;
import com.salesstock.entity.SaleItem;
import com.salesstock.exception.BadRequestException;
import com.salesstock.exception.ResourceNotFoundException;
import com.salesstock.repository.ProductRepository;
import com.salesstock.repository.SaleRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SaleService {

    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;

    public SaleService(ProductRepository productRepository,
                       SaleRepository saleRepository) {
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
    }

    @Transactional
    public SaleResponse createSale(SaleRequest request) {

        if (request == null || request.getItems() == null
                || request.getItems().isEmpty()) {
            throw new BadRequestException("Sale must contain at least one product");
        }

        Sale sale = new Sale();

        String customer = request.getCustomerName();
        sale.setCustomerName(
                customer == null || customer.isBlank()
                        ? "Walk-in customer"
                        : customer.trim());
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (SaleRequest.SaleLineRequest line : request.getItems()) {

            if (line.getProductId() == null || line.getQuantity() <= 0) {
                throw new BadRequestException("Invalid product ID or quantity");
            }

            Product product = productRepository.findById(line.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found: " + line.getProductId()));

            int quantity = line.getQuantity();

            if (product.getQuantity() < quantity) {
                throw new BadRequestException(
                        "Insufficient stock for product: " + product.getName());
            }

            BigDecimal unitPrice = product.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

            SaleItem item = new SaleItem();
            item.setSale(sale);
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setUnitPrice(unitPrice);
            item.setSubtotal(subtotal);

            sale.getItems().add(item);
            totalAmount = totalAmount.add(subtotal);

            product.setQuantity(product.getQuantity() - quantity);
        }

        sale.setTotalAmount(totalAmount);
        return SaleResponse.from(saleRepository.save(sale));
    }

    @Transactional(readOnly = true)
    public List<SaleResponse> getAllSales() {
        return saleRepository.findAllByOrderBySaleDateDesc()
                .stream().map(SaleResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public SaleResponse getSaleById(Long id) {
        return saleRepository.findWithItemsById(id)
                .map(SaleResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sale not found with ID: " + id));
    }
}