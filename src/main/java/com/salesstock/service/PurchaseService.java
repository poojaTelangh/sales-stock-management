
package com.salesstock.service;

import com.salesstock.entity.Purchase;
import com.salesstock.repository.PurchaseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    public PurchaseService(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    public Purchase addPurchase(Purchase purchase) {
        purchase.setTotalAmount(
                purchase.getQuantity() * purchase.getPurchasePrice()
        );

        if (purchase.getPurchaseDate() == null) {
            purchase.setPurchaseDate(LocalDate.now());
        }

        return purchaseRepository.save(purchase);
    }

    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    public Purchase getPurchaseById(Long id) {
        return purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Purchase not found with ID: " + id));
    }

    public Purchase updatePurchase(Long id, Purchase updatedPurchase) {
        Purchase existingPurchase = getPurchaseById(id);

        existingPurchase.setProductName(updatedPurchase.getProductName());
        existingPurchase.setSupplierName(updatedPurchase.getSupplierName());
        existingPurchase.setQuantity(updatedPurchase.getQuantity());
        existingPurchase.setPurchasePrice(updatedPurchase.getPurchasePrice());

        existingPurchase.setTotalAmount(
                updatedPurchase.getQuantity() * updatedPurchase.getPurchasePrice()
        );

        if (updatedPurchase.getPurchaseDate() != null) {
            existingPurchase.setPurchaseDate(updatedPurchase.getPurchaseDate());
        } else if (existingPurchase.getPurchaseDate() == null) {
            existingPurchase.setPurchaseDate(LocalDate.now());
        }

        return purchaseRepository.save(existingPurchase);
    }

    public void deletePurchase(Long id) {
        Purchase purchase = getPurchaseById(id);
        purchaseRepository.delete(purchase);
    }

    public List<Purchase> searchByProductName(String productName) {
        return purchaseRepository.findByProductNameContainingIgnoreCase(productName);
    }

    public List<Purchase> searchBySupplierName(String supplierName) {
        return purchaseRepository.findBySupplierNameContainingIgnoreCase(supplierName);
    }
}
