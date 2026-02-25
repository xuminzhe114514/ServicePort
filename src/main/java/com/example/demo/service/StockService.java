package com.example.demo.service;

import com.example.demo.entity.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
@Transactional
public interface StockService extends BaseService<Stock, Long> {
    Page<Stock> findByMedicineId(Long medicineId, Pageable pageable);
    Integer getTotalStock(Long medicineId);
    Page<Stock> getExpiringStock(LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<Stock> getLowStock(Pageable pageable);
    Map<Long, Integer> getLowStockSummary();
    void reduceStock(Long medicineId, Integer quantity);
    void increaseStock(Long medicineId, Integer quantity, String batchNumber, LocalDate expirationDate);
    boolean checkStockAvailability(Long medicineId, Integer requiredQuantity);
    Page<Stock> findByStatus(Integer status, Pageable pageable);
    Page<Stock> findByBatchNumber(String batchNumber, Pageable pageable);
    Page<Stock> findByShelfLocation(String shelfLocation, Pageable pageable);
    List<Stock> findExpiringWithinDays(int days);
    Map<String, Object> getStockStatisticsByMedicine(Long medicineId);
    Page<Stock> findByMedicineIdAndBatchNumber(Long medicineId, String batchNumber, Pageable pageable);
    Page<Stock> findExpiredStock(Pageable pageable);
    Page<Stock> findNearExpiryStock(int days, Pageable pageable);

    Double calculateStockTurnoverRate(String period);
    Double calculateTotalStockValue();
    Map<String, Object> getStockValueByCategory();
    Page<Stock> getStockAlerts(Pageable pageable);
    void transferStock(Long fromStockId, Long toStockId, Integer quantity);
    void setMinimumStockLevel(Long medicineId, Integer minLevel);
    Map<Long, Object> getStockInventoryReport();
    Page<Stock> findByStorageCondition(Integer condition, Pageable pageable);
    Page<Stock> findByShelfLocationContaining(String location, Pageable pageable);
}