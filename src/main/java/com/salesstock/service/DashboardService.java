package com.salesstock.service;

import com.salesstock.dto.DashboardSummary;
import com.salesstock.repository.ProductRepository;
import com.salesstock.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DashboardService {

    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;

    public DashboardService(ProductRepository productRepository,
                            SaleRepository saleRepository) {
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
    }

    public DashboardSummary getSummary() {
        long totalProducts = productRepository.count();
        long lowStockProducts = productRepository.countLowStockProducts();
        long totalSales = saleRepository.count();

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime startOfTomorrow = startOfDay.plusDays(1);

        BigDecimal todaySales = saleRepository.sumSalesBetween(
                startOfDay,
                startOfTomorrow
        );

        if (todaySales == null) {
            todaySales = BigDecimal.ZERO;
        }

        BigDecimal totalRevenue = saleRepository.sumAllSales();

        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        return new DashboardSummary(
                totalProducts,
                lowStockProducts,
                totalSales,
                todaySales,
                totalRevenue
        );
    }
}