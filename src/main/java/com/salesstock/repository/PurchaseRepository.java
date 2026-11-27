
package com.salesstock.repository;

import com.salesstock.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    List<Purchase> findByProductNameContainingIgnoreCase(String productName);

    List<Purchase> findBySupplierNameContainingIgnoreCase(String supplierName);
}
