package com.example.demo.service.impl;

import com.example.demo.entity.Medicine;
import com.example.demo.entity.Stock;
import com.example.demo.repository.MedicineRepository;
import com.example.demo.repository.StockRepository;
import com.example.demo.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class MedicineServiceImpl extends BaseServiceImpl<Medicine, Long, MedicineRepository>
        implements MedicineService {

    @Autowired
    private StockRepository stockRepository;

    private final int DEFAULT_ALERT_QUANTITY = 10;

    protected MedicineServiceImpl(MedicineRepository repository) {
        super(repository);
    }

    @Override
    @Transactional
    public Medicine findByMedicineCode(String medicineCode) {
        return repository.findByMedicineCode(medicineCode).orElse(null);
    }

    @Override
    public Page<Medicine> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Medicine> findByStatus(Integer status, Pageable pageable) {
        return repository.findByStatus(status, pageable);
    }

    @Override
    public List<Medicine> searchMedicines(String keyword) {
        return repository.searchMedicines(keyword, 1);
    }

    @Override
    public List<Medicine> findByCategoryId(Long categoryId) {
        return repository.findByCategoryId(categoryId);
    }

    @Override
    public Medicine updatePrice(Long id, BigDecimal retailPrice, BigDecimal purchasePrice) {
        Medicine medicine = findById(id);
        if (medicine != null) {
            if (retailPrice != null) medicine.setRetailPrice(retailPrice);
            if (purchasePrice != null) medicine.setPurchasePrice(purchasePrice);
            return save(medicine);
        }
        return null;
    }

    @Override
    public long countByStatus(Integer status) {
        return repository.countByStatus(status);
    }

    // 新增方法实现
    @Override
    public Page<Medicine> findSeasonalMedicines(Pageable pageable) {
        List<Medicine> allMedicines = repository.findByStatus(1);
        List<Medicine> seasonalMedicines = allMedicines.stream()
                .filter(Medicine::isSeasonal)
                .collect(Collectors.toList());

        int total = seasonalMedicines.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        List<Medicine> content;
        if (start >= total) {
            content = Collections.emptyList();
        } else {
            content = seasonalMedicines.subList(start, end);
        }

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Medicine> findPrescriptionMedicines(Pageable pageable) {
        List<Medicine> allMedicines = repository.findByStatus(1);
        List<Medicine> prescriptionMedicines = allMedicines.stream()
                .filter(Medicine::isPrescription)
                .collect(Collectors.toList());

        int total = prescriptionMedicines.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        List<Medicine> content;
        if (start >= total) {
            content = Collections.emptyList();
        } else {
            content = prescriptionMedicines.subList(start, end);
        }

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Medicine> findByManufacturer(String manufacturer, Pageable pageable) {
        List<Medicine> medicines = repository.findByManufacturerContaining(manufacturer);
        int total = medicines.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        List<Medicine> content;
        if (start >= total) {
            content = Collections.emptyList();
        } else {
            content = medicines.subList(start, end);
        }

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Medicine> findLowStockMedicines(Pageable pageable) {
        List<Medicine> allMedicines = repository.findByStatus(1);
        List<Medicine> lowStockMedicines = allMedicines.stream()
                .filter(medicine -> {
                    Object stockObj = repository.sumCurrentStockByMedicineId(medicine.getId());
                    Integer stock = stockObj != null ? (stockObj instanceof Long ? ((Long)stockObj).intValue() : stockObj instanceof Integer ? (Integer)stockObj : 0) : 0;
                    if (stock == null) return false;
                    // 获取药品的库存记录，计算最小预警阈值
                    List<Stock> stocks = repository.findStocksByMedicineId(medicine.getId());
                    if (stocks.isEmpty()) return false;
                    int minWarningQuantity = stocks.stream()
                            .mapToInt(Stock::getWarningQuantity)
                            .min()
                            .orElse(DEFAULT_ALERT_QUANTITY); // 默认预警阈值
                    return stock <= minWarningQuantity;
                })
                .collect(Collectors.toList());

        int total = lowStockMedicines.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        List<Medicine> content;
        if (start >= total) {
            content = Collections.emptyList();
        } else {
            content = lowStockMedicines.subList(start, end);
        }

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Map<String, Object> getMedicineSalesStatistics(Long medicineId) {
        Map<String, Object> stats = new HashMap<>();
        Medicine medicine = repository.findById(medicineId).orElse(null);
        if (medicine == null) {
            return stats;
        }

        // 销售总量
        Object totalSalesObj = repository.sumSaleQuantityByMedicineId(medicineId);
        Integer totalSales = totalSalesObj != null ? (totalSalesObj instanceof Long ? ((Long)totalSalesObj).intValue() : totalSalesObj instanceof Integer ? (Integer)totalSalesObj : 0) : 0;
        stats.put("totalSales", totalSales);

        // 销售趋势
        List<Object[]> saleTrend = repository.findSaleTrendByMedicineId(medicineId);
        stats.put("saleTrend", saleTrend);

        return stats;
    }

    @Override
    public Map<String, Object> getMedicinePurchaseStatistics(Long medicineId) {
        Map<String, Object> stats = new HashMap<>();
        Medicine medicine = repository.findById(medicineId).orElse(null);
        if (medicine == null) {
            return stats;
        }

        // 采购总量
        Object totalPurchaseObj = repository.sumPurchaseQuantityByMedicineId(medicineId);
        Integer totalPurchase = totalPurchaseObj != null ? (totalPurchaseObj instanceof Long ? ((Long)totalPurchaseObj).intValue() : totalPurchaseObj instanceof Integer ? (Integer)totalPurchaseObj : 0) : 0;
        stats.put("totalPurchase", totalPurchase);

        return stats;
    }

    @Override
    public void batchUpdateStatus(List<Long> medicineIds, Integer status) {
        List<Medicine> medicines = repository.findAllById(medicineIds);
        medicines.forEach(medicine -> medicine.setStatus(status));
        repository.saveAll(medicines);
    }

    @Override
    public Page<Medicine> findExpiringMedicines(int daysThreshold, Pageable pageable) {
        LocalDate now = LocalDate.now();
        LocalDate endDate = now.plusDays(daysThreshold);
        List<Stock> expiringStocks = stockRepository.findExpiringStock(now, endDate);
        
        // 提取不重复的药品
        Set<Medicine> expiringMedicinesSet = expiringStocks.stream()
                .filter(stock -> stock.getStatus() == 1)
                .map(Stock::getMedicine)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        
        List<Medicine> expiringMedicines = new ArrayList<>(expiringMedicinesSet);
        int total = expiringMedicines.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        List<Medicine> content;
        if (start >= total) {
            content = Collections.emptyList();
        } else {
            content = expiringMedicines.subList(start, end);
        }

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Medicine> findByStorageRequirement(Integer storageRequirement, Pageable pageable) {
        List<Medicine> allMedicines = repository.findByStatus(1);
        List<Medicine> filteredMedicines = allMedicines.stream()
                .filter(medicine -> medicine.getStorageRequirement() != null && 
                        medicine.getStorageRequirement().equals(storageRequirement))
                .collect(Collectors.toList());

        int total = filteredMedicines.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        List<Medicine> content;
        if (start >= total) {
            content = Collections.emptyList();
        } else {
            content = filteredMedicines.subList(start, end);
        }

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Medicine> searchMedicinesWithPagination(String keyword, Pageable pageable) {
        List<Medicine> allResults = repository.searchMedicines(keyword, 1);
        int total = allResults.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        List<Medicine> content;
        if (start >= total) {
            content = Collections.emptyList();
        } else {
            content = allResults.subList(start, end);
        }

        return new PageImpl<>(content, pageable, total);
    }
}