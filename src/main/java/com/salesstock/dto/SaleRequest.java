package com.salesstock.dto;

import java.util.List;

public class SaleRequest {

    private String customerName;
    private List<SaleLineRequest> items;

    public SaleRequest() {
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public List<SaleLineRequest> getItems() {
        return items;
    }

    public void setItems(List<SaleLineRequest> items) {
        this.items = items;
    }

    public static class SaleLineRequest {

        private Long productId;
        private int quantity;

        public SaleLineRequest() {
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}