package com.example.demo.service.impl;

import com.example.demo.entity.SaleRecord;
import com.example.demo.entity.User;
import com.example.demo.repository.SaleRecordRepository;
import com.example.demo.service.SaleRecordService;
import com.example.demo.service.UserService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SaleRecordServiceImpl extends BaseServiceImpl<SaleRecord, Long, SaleRecordRepository>
        implements SaleRecordService {


    protected SaleRecordServiceImpl(SaleRecordRepository repository) {
        super(repository);
    }

    @Autowired
    private UserService userService;

    @Override
    public SaleRecord findByRecordNo(String recordNo) {
        SaleRecord record = repository.findByRecordNo(recordNo).orElse(null);
        if (record != null) {
            Hibernate.initialize(record.getMedicine());
            Hibernate.initialize(record.getOperator());
            Hibernate.initialize(record.getSymptom());
        }
        return record;
    }

    @Override
    @Transactional(readOnly = true)
    public SaleRecord findById(Long id) {
        SaleRecord record = repository.findById(id).orElse(null);
        if (record != null) {
            Hibernate.initialize(record.getMedicine());
            Hibernate.initialize(record.getOperator());
            Hibernate.initialize(record.getSymptom());
        }
        return record;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleRecord> findAll() {
        List<SaleRecord> records = repository.findAll();
        if (!records.isEmpty()) {
            records.forEach(record -> {
                Hibernate.initialize(record.getMedicine());
                Hibernate.initialize(record.getOperator());
                Hibernate.initialize(record.getSymptom());
            });
        }
        return records;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SaleRecord> findAll(Pageable pageable) {
        Page<SaleRecord> page = repository.findAll(pageable);
        if (page.hasContent()) {
            page.getContent().forEach(record -> {
                Hibernate.initialize(record.getMedicine());
                Hibernate.initialize(record.getOperator());
                Hibernate.initialize(record.getSymptom());
            });
        }
        return page;
    }

    @Override
    @Transactional
    public SaleRecord save(SaleRecord record) {
        SaleRecord savedRecord = repository.save(record);
        if (savedRecord != null) {
            Hibernate.initialize(savedRecord.getMedicine());
            Hibernate.initialize(savedRecord.getOperator());
            Hibernate.initialize(savedRecord.getSymptom());
        }
        return savedRecord;
    }

    @Override
    @Transactional
    public SaleRecord update(SaleRecord record) {
        SaleRecord updatedRecord = repository.save(record);
        if (updatedRecord != null) {
            Hibernate.initialize(updatedRecord.getMedicine());
            Hibernate.initialize(updatedRecord.getOperator());
            Hibernate.initialize(updatedRecord.getSymptom());
        }
        return updatedRecord;
    }

    @Override
    @Transactional
    public List<SaleRecord> saveAll(List<SaleRecord> records) {
        List<SaleRecord> savedRecords = repository.saveAll(records);
        if (!savedRecords.isEmpty()) {
            savedRecords.forEach(record -> {
                Hibernate.initialize(record.getMedicine());
                Hibernate.initialize(record.getOperator());
                Hibernate.initialize(record.getSymptom());
            });
        }
        return savedRecords;
    }

    @Override
    public List<SaleRecord> findBySaleTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        List<SaleRecord> records = repository.findBySaleTimeBetween(startTime, endTime);
        if (!records.isEmpty()) {
            records.forEach(record -> {
                Hibernate.initialize(record.getMedicine());
                Hibernate.initialize(record.getOperator());
                Hibernate.initialize(record.getSymptom());
            });
        }
        return records;
    }

    @Override
    public List<SaleRecord> findByMedicineId(Long medicineId) {
        List<SaleRecord> records = repository.findByMedicineId(medicineId);
        if (!records.isEmpty()) {
            records.forEach(record -> {
                Hibernate.initialize(record.getMedicine());
                Hibernate.initialize(record.getOperator());
                Hibernate.initialize(record.getSymptom());
            });
        }
        return records;
    }

    @Override
    public List<SaleRecord> findByOperatorId(Long operatorId) {
        List<SaleRecord> records = repository.findByOperatorId(operatorId);
        if (!records.isEmpty()) {
            records.forEach(record -> {
                Hibernate.initialize(record.getMedicine());
                Hibernate.initialize(record.getOperator());
                Hibernate.initialize(record.getSymptom());
            });
        }
        return records;
    }

    @Override
    public Double getTotalSalesByPeriod(LocalDateTime startTime, LocalDateTime endTime) {
        Object totalObj = repository.sumTotalAmountByPeriod(startTime, endTime);
        Double total = totalObj != null ? (totalObj instanceof BigDecimal ? ((BigDecimal)totalObj).doubleValue() : totalObj instanceof Double ? (Double)totalObj : 0.0) : 0.0;
        return total;
    }

    @Override
    public Integer getTotalQuantityByMedicineId(Long medicineId) {
        Object totalObj = repository.sumQuantityByMedicineId(medicineId);
        Integer total = totalObj != null ? (totalObj instanceof Long ? ((Long)totalObj).intValue() : totalObj instanceof Integer ? (Integer)totalObj : 0) : 0;
        return total;
    }

    @Override
    public List<Map<String, Object>> getDailySalesReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = repository.findDailySales(startDate, endDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return results.stream().map(result -> {
            Map<String, Object> map = new HashMap<>();
            map.put("date", (((Date) result[0]).toLocalDate()).format(formatter));
            map.put("medicineId", result[1]);
            map.put("totalQuantity", result[2]);
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getTopSellingMedicines(int limit, LocalDateTime startDate, LocalDateTime endDate) {

        if (endDate == null) endDate = LocalDateTime.now();
        if (startDate == null) startDate = endDate.minusDays(30);
        if (endDate.isBefore(startDate)) endDate = startDate;

        List<Object[]> results = repository.findTopSellingMedicines(
                startDate, endDate, PageRequest.of(0, limit)
        );

        return results.stream().map(result -> {
            Map<String, Object> map = new HashMap<>();
            map.put("medicineId", result[0]);
            map.put("totalQuantity", result[1]);
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public SaleRecord createSaleRecord(SaleRecord saleRecord, Long operatorId) {
        User operator = userService.findById(operatorId);
        if (operator != null) {
            saleRecord.setOperator(operator);
        }

        if (saleRecord.getRecordNo() == null) {
            String recordNo = "SALE" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                    + String.format("%04d", (int)(Math.random() * 10000));
            saleRecord.setRecordNo(recordNo);
        }

        if (saleRecord.getUnitPrice() != null && saleRecord.getQuantity() != null) {
            saleRecord.calculateTotalAmount();
        }

        SaleRecord savedRecord = repository.save(saleRecord);
        if (savedRecord != null) {
            Hibernate.initialize(savedRecord.getMedicine());
            Hibernate.initialize(savedRecord.getOperator());
            Hibernate.initialize(savedRecord.getSymptom());
        }
        return savedRecord;
    }

    // 新增方法实现
    @Override
    public Page<SaleRecord> findByCustomerType(Integer customerType, Pageable pageable) {
        List<SaleRecord> sales = repository.findByCustomerType(customerType);
        int total = sales.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        List<SaleRecord> content;
        if (start >= total) {
            content = Collections.emptyList();
        } else {
            content = sales.subList(start, end);
        }

        if (!content.isEmpty()) {
            content.forEach(record -> {
                Hibernate.initialize(record.getMedicine());
                Hibernate.initialize(record.getOperator());
                Hibernate.initialize(record.getSymptom());
            });
        }

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Map<String, Object>> getSalesByCategory(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        List<SaleRecord> allSales = repository.findBySaleTimeBetween(startDate, endDate);
        Map<String, Map<String, Object>> categorySales = new HashMap<>();
        allSales.forEach(sale -> {
            if (sale.getMedicine() != null && sale.getMedicine().getCategory() != null) {
                String categoryName = sale.getMedicine().getCategory().getName();
                categorySales.computeIfAbsent(categoryName, k -> {
                    Map<String, Object> stats = new HashMap<>();
                    stats.put("totalQuantity", 0);
                    stats.put("totalAmount", 0.0);
                    return stats;
                });

                Map<String, Object> stats = categorySales.get(categoryName);
                stats.put("totalQuantity", (Integer) stats.get("totalQuantity") + sale.getQuantity());
                stats.put("totalAmount", (Double) stats.get("totalAmount") + sale.getTotalAmount().doubleValue());
            }
        });

        List<Map<String, Object>> categoryStats = categorySales.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("category", entry.getKey());
                    map.putAll(entry.getValue());
                    return map;
                })
                .collect(Collectors.toList());

        int total = categoryStats.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        List<Map<String, Object>> content;
        if (start >= total) {
            content = Collections.emptyList();
        } else {
            content = categoryStats.subList(start, end);
        }

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Map<String, Object>> getSalesBySymptom(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        List<Object[]> results = repository.findSalesBySymptom(startDate, endDate);
        List<Map<String, Object>> symptomStats = results.stream().map(result -> {
            Map<String, Object> map = new HashMap<>();
            map.put("symptom", result[0]);
            map.put("totalQuantity", result[1]);
            map.put("totalAmount", result[2]);
            return map;
        }).collect(Collectors.toList());

        int total = symptomStats.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        List<Map<String, Object>> content;
        if (start >= total) {
            content = Collections.emptyList();
        } else {
            content = symptomStats.subList(start, end);
        }

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Map<String, Object> getOperatorSalesPerformance(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = repository.findOperatorSalesPerformance(startDate, endDate);
        Map<String, Object> performance = new HashMap<>();

        results.forEach(result -> {
            String operatorName = result[1].toString();
            Map<String, Object> stats = new HashMap<>();
            stats.put("recordCount", result[2]);
            stats.put("totalAmount", result[3]);
            performance.put(operatorName, stats);
        });

        return performance;
    }

    @Override
    public Page<Map<String, Object>> getMonthlySalesTrend(int months, Pageable pageable) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(months);

        List<Object[]> results = repository.findDailySalesAmount(startDate, endDate);
        List<Map<String, Object>> trendData = results.stream().map(result -> {
            Map<String, Object> map = new HashMap<>();
            map.put("date", result[0]);
            map.put("amount", result[1]);
            return map;
        }).collect(Collectors.toList());

        int total = trendData.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        List<Map<String, Object>> content;
        if (start >= total) {
            content = Collections.emptyList();
        } else {
            content = trendData.subList(start, end);
        }

        return new PageImpl<>(content, pageable, total);
    }

    @Deprecated
    @Override
    public Page<Map<String, Object>> getSalesPrediction(int days, Pageable pageable) {
        // 这里简化实现，实际应该使用预测算法
        List<Map<String, Object>> predictions = new ArrayList<>();
        LocalDate startDate = LocalDate.now();
        
        for (int i = 1; i <= days; i++) {
            LocalDate date = startDate.plusDays(i);
            Map<String, Object> prediction = new HashMap<>();
            prediction.put("date", date);
            prediction.put("predictedAmount", Math.random() * 10000);
            predictions.add(prediction);
        }

        int total = predictions.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        List<Map<String, Object>> content;
        if (start >= total) {
            content = Collections.emptyList();
        } else {
            content = predictions.subList(start, end);
        }

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<SaleRecord> findPrescriptionSales(Pageable pageable) {
        List<SaleRecord> allSales = repository.findAll();
        List<SaleRecord> prescriptionSales = allSales.stream()
                .filter(SaleRecord::isRx)
                .collect(Collectors.toList());

        int total = prescriptionSales.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        List<SaleRecord> content;
        if (start >= total) {
            content = Collections.emptyList();
        } else {
            content = prescriptionSales.subList(start, end);
        }

        if (!content.isEmpty()) {
            content.forEach(record -> {
                Hibernate.initialize(record.getMedicine());
                Hibernate.initialize(record.getOperator());
                Hibernate.initialize(record.getSymptom());
            });
        }

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Map<String, Object> getSalesStatisticsByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> stats = new HashMap<>();
        
        // 销售总额
        Object totalAmountObj = repository.sumTotalAmountByPeriod(startDate, endDate);
        Double totalAmount = totalAmountObj != null ? (totalAmountObj instanceof BigDecimal ? ((BigDecimal)totalAmountObj).doubleValue() : totalAmountObj instanceof Double ? (Double)totalAmountObj : 0.0) : 0.0;
        stats.put("totalAmount", totalAmount);
        
        // 销售记录数
        List<SaleRecord> sales = repository.findBySaleTimeBetween(startDate, endDate);
        stats.put("recordCount", sales.size());
        
        // 平均销售额
        if (!sales.isEmpty()) {
            double averageAmount = sales.stream()
                    .mapToDouble(sale -> sale.getTotalAmount().doubleValue())
                    .average()
                    .orElse(0.0);
            stats.put("averageAmount", averageAmount);
        } else {
            stats.put("averageAmount", 0.0);
        }
        
        return stats;
    }
}
