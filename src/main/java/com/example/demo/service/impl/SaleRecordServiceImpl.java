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

    private void initializeSaleRecordAssociations(SaleRecord record) {
        if (record != null) {
            Hibernate.initialize(record.getMedicine());
            Hibernate.initialize(record.getOperator());
            Hibernate.initialize(record.getSymptom());
            
            if (record.getMedicine() != null) {
                Hibernate.initialize(record.getMedicine().getCategory());
                Hibernate.initialize(record.getMedicine().getStocks());
                Hibernate.initialize(record.getMedicine().getSaleRecords());
                Hibernate.initialize(record.getMedicine().getPurchaseOrders());
                Hibernate.initialize(record.getMedicine().getSymptoms());
            }
            
            if (record.getOperator() != null) {
                Hibernate.initialize(record.getOperator());
            }
        }
    }

    @Override
    public SaleRecord findByRecordNo(String recordNo) {
        SaleRecord record = repository.findByRecordNo(recordNo).orElse(null);
        if (record != null) {
            initializeSaleRecordAssociations(record);
        }
        return record;
    }

    @Override
    @Transactional(readOnly = true)
    public SaleRecord findById(Long id) {
        SaleRecord record = repository.findById(id).orElse(null);
        if (record != null) {
            initializeSaleRecordAssociations(record);
        }
        return record;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleRecord> findAll() {
        List<SaleRecord> records = repository.findAll();
        if (!records.isEmpty()) {
            records.forEach(this::initializeSaleRecordAssociations);
        }
        return records;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SaleRecord> findAll(Pageable pageable) {
        Page<SaleRecord> page = repository.findAll(pageable);
        if (page.hasContent()) {
            page.getContent().forEach(this::initializeSaleRecordAssociations);
        }
        return page;
    }

    @Override
    @Transactional
    public SaleRecord save(SaleRecord record) {
        SaleRecord savedRecord = repository.save(record);
        if (savedRecord != null) {
            initializeSaleRecordAssociations(savedRecord);
        }
        return savedRecord;
    }

    @Override
    @Transactional
    public SaleRecord update(SaleRecord record) {
        SaleRecord updatedRecord = repository.save(record);
        if (updatedRecord != null) {
            initializeSaleRecordAssociations(updatedRecord);
        }
        return updatedRecord;
    }

    @Override
    @Transactional
    public List<SaleRecord> saveAll(List<SaleRecord> records) {
        List<SaleRecord> savedRecords = repository.saveAll(records);
        if (!savedRecords.isEmpty()) {
            savedRecords.forEach(this::initializeSaleRecordAssociations);
        }
        return savedRecords;
    }

    @Override
    public List<SaleRecord> findBySaleTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        List<SaleRecord> records = repository.findBySaleTimeBetween(startTime, endTime);
        if (!records.isEmpty()) {
            records.forEach(this::initializeSaleRecordAssociations);
        }
        return records;
    }

    @Override
    public List<SaleRecord> findByMedicineId(Long medicineId) {
        List<SaleRecord> records = repository.findByMedicineId(medicineId);
        if (!records.isEmpty()) {
            records.forEach(this::initializeSaleRecordAssociations);
        }
        return records;
    }

    @Override
    public List<SaleRecord> findByOperatorId(Long operatorId) {
        List<SaleRecord> records = repository.findByOperatorId(operatorId);
        if (!records.isEmpty()) {
            records.forEach(this::initializeSaleRecordAssociations);
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
            
            Object dateObj = result[0];
            if (dateObj instanceof java.sql.Date) {
                map.put("date", ((java.sql.Date) dateObj).toLocalDate().format(formatter));
            } else if (dateObj instanceof java.util.Date) {
                map.put("date", ((java.util.Date) dateObj).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().format(formatter));
            } else {
                map.put("date", dateObj != null ? dateObj.toString() : null);
            }
            
            Object medicineIdObj = result[1];
            Long medicineId;
            if (medicineIdObj instanceof Long) {
                medicineId = (Long) medicineIdObj;
            } else if (medicineIdObj instanceof Integer) {
                medicineId = ((Integer) medicineIdObj).longValue();
            } else if (medicineIdObj instanceof BigDecimal) {
                medicineId = ((BigDecimal) medicineIdObj).longValue();
            } else {
                try {
                    medicineId = Long.parseLong(medicineIdObj.toString());
                } catch (NumberFormatException e) {
                    medicineId = 0L;
                }
            }
            map.put("medicineId", medicineId);
            
            Object quantityObj = result[2];
            Integer quantity;
            if (quantityObj instanceof Long) {
                quantity = ((Long) quantityObj).intValue();
            } else if (quantityObj instanceof Integer) {
                quantity = (Integer) quantityObj;
            } else if (quantityObj instanceof BigDecimal) {
                quantity = ((BigDecimal) quantityObj).intValue();
            } else {
                try {
                    quantity = Integer.parseInt(quantityObj.toString());
                } catch (NumberFormatException e) {
                    quantity = 0;
                }
            }
            map.put("totalQuantity", quantity);
            
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
            
            Object medicineIdObj = result[0];
            Long medicineId;
            if (medicineIdObj instanceof Long) {
                medicineId = (Long) medicineIdObj;
            } else if (medicineIdObj instanceof Integer) {
                medicineId = ((Integer) medicineIdObj).longValue();
            } else if (medicineIdObj instanceof BigDecimal) {
                medicineId = ((BigDecimal) medicineIdObj).longValue();
            } else {
                try {
                    medicineId = Long.parseLong(medicineIdObj.toString());
                } catch (NumberFormatException e) {
                    medicineId = 0L;
                }
            }
            map.put("medicineId", medicineId);
            
            Object quantityObj = result[1];
            Long quantity;
            if (quantityObj instanceof Long) {
                quantity = (Long) quantityObj;
            } else if (quantityObj instanceof Integer) {
                quantity = ((Integer) quantityObj).longValue();
            } else if (quantityObj instanceof BigDecimal) {
                quantity = ((BigDecimal) quantityObj).longValue();
            } else {
                try {
                    quantity = Long.parseLong(quantityObj.toString());
                } catch (NumberFormatException e) {
                    quantity = 0L;
                }
            }
            map.put("totalQuantity", quantity);
            
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
            initializeSaleRecordAssociations(savedRecord);
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
            content.forEach(this::initializeSaleRecordAssociations);
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
            
            Object quantityObj = result[1];
            Integer quantity;
            if (quantityObj instanceof Long) {
                quantity = ((Long) quantityObj).intValue();
            } else if (quantityObj instanceof Integer) {
                quantity = (Integer) quantityObj;
            } else {
                try {
                    quantity = Integer.parseInt(quantityObj.toString());
                } catch (NumberFormatException e) {
                    quantity = 0;
                }
            }
            map.put("totalQuantity", quantity);
            
            Object amountObj = result[2];
            Double amount;
            if (amountObj instanceof BigDecimal) {
                amount = ((BigDecimal) amountObj).doubleValue();
            } else if (amountObj instanceof Long) {
                amount = ((Long) amountObj).doubleValue();
            } else if (amountObj instanceof Integer) {
                amount = ((Integer) amountObj).doubleValue();
            } else if (amountObj instanceof Double) {
                amount = (Double) amountObj;
            } else {
                try {
                    amount = Double.parseDouble(amountObj.toString());
                } catch (NumberFormatException e) {
                    amount = 0.0;
                }
            }
            map.put("totalAmount", amount);
            
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
            
            Object countObj = result[2];
            Long count;
            if (countObj instanceof Long) {
                count = (Long) countObj;
            } else if (countObj instanceof Integer) {
                count = ((Integer) countObj).longValue();
            } else {
                try {
                    count = Long.parseLong(countObj.toString());
                } catch (NumberFormatException e) {
                    count = 0L;
                }
            }
            stats.put("recordCount", count);
            
            Object amountObj = result[3];
            Double amount;
            if (amountObj instanceof BigDecimal) {
                amount = ((BigDecimal) amountObj).doubleValue();
            } else if (amountObj instanceof Long) {
                amount = ((Long) amountObj).doubleValue();
            } else if (amountObj instanceof Integer) {
                amount = ((Integer) amountObj).doubleValue();
            } else if (amountObj instanceof Double) {
                amount = (Double) amountObj;
            } else {
                try {
                    amount = Double.parseDouble(amountObj.toString());
                } catch (NumberFormatException e) {
                    amount = 0.0;
                }
            }
            stats.put("totalAmount", amount);
            
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
            
            Object dateObj = result[0];
            if (dateObj instanceof java.sql.Date) {
                map.put("date", ((java.sql.Date) dateObj).toLocalDate());
            } else if (dateObj instanceof java.util.Date) {
                map.put("date", ((java.util.Date) dateObj).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            } else {
                map.put("date", dateObj);
            }
            
            Object amountObj = result[1];
            Double amount;
            if (amountObj instanceof BigDecimal) {
                amount = ((BigDecimal) amountObj).doubleValue();
            } else if (amountObj instanceof Long) {
                amount = ((Long) amountObj).doubleValue();
            } else if (amountObj instanceof Integer) {
                amount = ((Integer) amountObj).doubleValue();
            } else if (amountObj instanceof Double) {
                amount = (Double) amountObj;
            } else {
                try {
                    amount = Double.parseDouble(amountObj.toString());
                } catch (NumberFormatException e) {
                    amount = 0.0;
                }
            }
            map.put("amount", amount);
            
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
            content.forEach(this::initializeSaleRecordAssociations);
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
