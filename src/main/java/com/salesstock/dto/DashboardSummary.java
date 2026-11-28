package com.salesstock.dto;

import java.math.BigDecimal;

public class DashboardSummary {

    private long totalProducts;
    private long lowStockProducts;
    private long totalSales;
    private BigDecimal totalSalesAmount;
    private BigDecimal todaySales;

    public DashboardSummary() {
    }

    public DashboardSummary(long totalProducts, long lowStockProducts,
                            long totalSales, BigDecimal totalSalesAmount,
                            BigDecimal todaySales) {
        this.totalProducts = totalProducts;
        this.lowStockProducts = lowStockProducts;
        this.totalSales = totalSales;
        this.totalSalesAmount = totalSalesAmount;
        this.todaySales = todaySales;
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

    /** Number of sales recorded. */
    public long getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(long totalSales) {
        this.totalSales = totalSales;
    }

    /** Total money (sum of all sales). */
    public BigDecimal getTotalSalesAmount() {
        return totalSalesAmount;
    }

    public void setTotalSalesAmount(BigDecimal totalSalesAmount) {
        this.totalSalesAmount = totalSalesAmount;
    }

    public BigDecimal getTodaySales() {
        return todaySales;
    }

    public void setTodaySales(BigDecimal todaySales) {
        this.todaySales = todaySales;
    }
}
