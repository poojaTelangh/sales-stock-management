package com.salesstock.repository;

import com.salesstock.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findAllByOrderBySaleDateDesc();

    @Query("SELECT s FROM Sale s LEFT JOIN FETCH s.items WHERE s.id = :id")
    Optional<Sale> findWithItemsById(@Param("id") Long id);

    @Query("""
           SELECT SUM(s.totalAmount)
           FROM Sale s
           WHERE s.saleDate >= :start AND s.saleDate < :end
           """)
    BigDecimal sumSalesBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT SUM(s.totalAmount) FROM Sale s")
    BigDecimal sumAllSales();
}