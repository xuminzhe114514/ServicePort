package com.example.demo.service;

import com.example.demo.entity.SaleRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
@Transactional
public interface SaleRecordService extends BaseService<SaleRecord, Long> {
    SaleRecord findByRecordNo(String recordNo);
    Page<SaleRecord> findAll(Pageable pageable);
    List<SaleRecord> findBySaleTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    List<SaleRecord> findByMedicineId(Long medicineId);
    List<SaleRecord> findByOperatorId(Long operatorId);
    Double getTotalSalesByPeriod(LocalDateTime startTime, LocalDateTime endTime);
    Integer getTotalQuantityByMedicineId(Long medicineId);
    List<Map<String, Object>> getDailySalesReport(LocalDateTime startDate, LocalDateTime endDate);
    List<Map<String, Object>> getTopSellingMedicines(int limit, LocalDateTime startDate, LocalDateTime endDate);
    SaleRecord createSaleRecord(SaleRecord saleRecord, Long operatorId);
    
    /**
     * ★ 创建销售记录并自动扣减库存（事务控制）
     * 如果库存扣减失败，整个操作会回滚
     */
    SaleRecord createSaleRecordWithStockDeduction(SaleRecord saleRecord, Long operatorId);

    Page<SaleRecord> findByCustomerType(Integer customerType, Pageable pageable);
    Page<Map<String, Object>> getSalesByCategory(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    Page<Map<String, Object>> getSalesBySymptom(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    Map<String, Object> getOperatorSalesPerformance(LocalDateTime startDate, LocalDateTime endDate);
    Page<Map<String, Object>> getMonthlySalesTrend(int months, Pageable pageable);
    Page<Map<String, Object>> getSalesPrediction(int days, Pageable pageable);
    Page<SaleRecord> findPrescriptionSales(Pageable pageable);
    Map<String, Object> getSalesStatisticsByPeriod(LocalDateTime startDate, LocalDateTime endDate);
    Page<SaleRecord> searchByKeyword(String keyword, Pageable pageable);
    Page<SaleRecord> findByMultipleConditions(String keyword, LocalDateTime startTime, LocalDateTime endTime, Long operatorId, Integer symptomId, Long medicineId, Pageable pageable);
}