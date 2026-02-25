package com.example.demo.service;

import com.example.demo.entity.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;
@Transactional
public interface MedicineService extends BaseService<Medicine, Long> {

    Medicine findByMedicineCode(String medicineCode);
    Page<Medicine> findAll(Pageable pageable);
    Page<Medicine> findByStatus(Integer status, Pageable pageable);
    List<Medicine> searchMedicines(String keyword);
    List<Medicine> findByCategoryId(Long categoryId);
    Medicine updatePrice(Long id, BigDecimal retailPrice, BigDecimal purchasePrice);
    long countByStatus(Integer status);
    
    Page<Medicine> findSeasonalMedicines(Pageable pageable);
    Page<Medicine> findPrescriptionMedicines(Pageable pageable);
    Page<Medicine> findByManufacturer(String manufacturer, Pageable pageable);
    Page<Medicine> findLowStockMedicines(Pageable pageable);
    Map<String, Object> getMedicineSalesStatistics(Long medicineId);
    Map<String, Object> getMedicinePurchaseStatistics(Long medicineId);
    void batchUpdateStatus(List<Long> medicineIds, Integer status);
    Page<Medicine> findExpiringMedicines(int daysThreshold, Pageable pageable);
    Page<Medicine> findByStorageRequirement(Integer storageRequirement, Pageable pageable);
    Page<Medicine> searchMedicinesWithPagination(String keyword, Pageable pageable);

}