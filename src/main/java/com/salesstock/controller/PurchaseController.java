
package com.salesstock.controller;

import com.salesstock.entity.Purchase;
import com.salesstock.service.PurchaseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin(origins = "*")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping
    public Purchase addPurchase(@RequestBody Purchase purchase) {
        return purchaseService.addPurchase(purchase);
    }

    @GetMapping
    public List<Purchase> getAllPurchases() {
        return purchaseService.getAllPurchases();
    }

    @GetMapping("/{id}")
    public Purchase getPurchaseById(@PathVariable Long id) {
        return purchaseService.getPurchaseById(id);
    }

    @PutMapping("/{id}")
    public Purchase updatePurchase(@PathVariable Long id,
                                   @RequestBody Purchase purchase) {
        return purchaseService.updatePurchase(id, purchase);
    }

    @DeleteMapping("/{id}")
    public String deletePurchase(@PathVariable Long id) {
        purchaseService.deletePurchase(id);
        return "Purchase deleted successfully";
    }

    @GetMapping("/search/product")
    public List<Purchase> searchByProductName(@RequestParam String productName) {
        return purchaseService.searchByProductName(productName);
    }

    @GetMapping("/search/supplier")
    public List<Purchase> searchBySupplierName(@RequestParam String supplierName) {
        return purchaseService.searchBySupplierName(supplierName);
    }
}
