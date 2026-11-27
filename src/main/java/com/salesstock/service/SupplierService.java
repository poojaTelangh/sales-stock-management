
package com.salesstock.service;

import com.salesstock.entity.Supplier;
import com.salesstock.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public Supplier addSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + id));
    }

    public Supplier updateSupplier(Long id, Supplier updatedSupplier) {
        Supplier existingSupplier = getSupplierById(id);

        existingSupplier.setName(updatedSupplier.getName());
        existingSupplier.setContactPerson(updatedSupplier.getContactPerson());
        existingSupplier.setPhone(updatedSupplier.getPhone());
        existingSupplier.setEmail(updatedSupplier.getEmail());
        existingSupplier.setAddress(updatedSupplier.getAddress());

        return supplierRepository.save(existingSupplier);
    }

    public void deleteSupplier(Long id) {
        Supplier supplier = getSupplierById(id);
        supplierRepository.delete(supplier);
    }

    public List<Supplier> searchByName(String name) {
        return supplierRepository.findByNameContainingIgnoreCase(name);
    }
}
