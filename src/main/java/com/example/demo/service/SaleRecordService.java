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
}