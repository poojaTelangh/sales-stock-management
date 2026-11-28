package com.salesstock.repository;

import com.salesstock.entity.Sale;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s WHERE s.saleDate >= :start AND s.saleDate < :end")
    BigDecimal sumSalesBetween(@Param("start") LocalDateTime start,
                               @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s")
    BigDecimal sumAllSales();

    @EntityGraph(attributePaths = {"items", "items.product"})
    List<Sale> findAllByOrderBySaleDateDesc();

    @EntityGraph(attributePaths = {"items", "items.product"})
    Optional<Sale> findWithItemsById(Long id);
}