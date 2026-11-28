package com.salesstock.dto;

import java.math.BigDecimal;

public class DashboardSummary {

    private long totalProducts;
    private long lowStockProducts;
    private long totalSales;
    private BigDecimal todaySales;
    private BigDecimal totalRevenue;

    public DashboardSummary() {
    }

    public DashboardSummary(long totalProducts, long lowStockProducts,
                            long totalSales, BigDecimal todaySales,
                            BigDecimal totalRevenue) {
        this.totalProducts = totalProducts;
        this.lowStockProducts = lowStockProducts;
        this.totalSales = totalSales;
        this.todaySales = todaySales;
        this.totalRevenue = totalRevenue;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public long getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(long lowStockProducts) {
        this.lowStockProducts = lowStockProducts;
    }

    public long getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(long totalSales) {
        this.totalSales = totalSales;
    }

    public BigDecimal getTodaySales() {
        return todaySales;
    }

    public void setTodaySales(BigDecimal todaySales) {
        this.todaySales = todaySales;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}