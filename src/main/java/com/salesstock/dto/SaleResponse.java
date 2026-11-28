package com.salesstock.dto;

import com.salesstock.entity.Sale;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SaleResponse {

    private Long id;
    private LocalDateTime saleDate;
    private BigDecimal totalAmount;
    private List<Item> items;

    public static SaleResponse from(Sale sale) {
        SaleResponse r = new SaleResponse();
        r.id = sale.getId();
        r.saleDate = sale.getSaleDate();
        r.totalAmount = sale.getTotalAmount();
        r.items = sale.getItems().stream().map(i -> {
            Item item = new Item();
            item.productId = i.getProduct().getId();
            item.productName = i.getProduct().getName();
            item.quantity = i.getQuantity();
            item.unitPrice = i.getUnitPrice();
            item.subtotal = i.getSubtotal();
            return item;
        }).toList();
        return r;
    }

    public Long getId() { return id; }
    public LocalDateTime getSaleDate() { return saleDate; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public List<Item> getItems() { return items; }

    public static class Item {
        private Long productId;
        private String productName;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;

        public Long getProductId() { return productId; }
        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public BigDecimal getSubtotal() { return subtotal; }
    }
}