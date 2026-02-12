package com.example.demo.service.impl;

import com.example.demo.entity.SaleRecord;
import com.example.demo.entity.User;
import com.example.demo.repository.SaleRecordRepository;
import com.example.demo.service.SaleRecordService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return repository.findByRecordNo(recordNo).orElse(null);
    }

    @Override
    public Page<SaleRecord> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public List<SaleRecord> findBySaleTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return repository.findBySaleTimeBetween(startTime, endTime);
    }

    @Override
    public List<SaleRecord> findByMedicineId(Long medicineId) {
        return repository.findByMedicineId(medicineId);
    }

    @Override
    public List<SaleRecord> findByOperatorId(Long operatorId) {
        return repository.findByOperatorId(operatorId);
    }

    @Override
    public Double getTotalSalesByPeriod(LocalDateTime startTime, LocalDateTime endTime) {
        Double total = repository.sumTotalAmountByPeriod(startTime, endTime);
        return total != null ? total : 0.0;
    }

    @Override
    public Integer getTotalQuantityByMedicineId(Long medicineId) {
        Integer total = repository.sumQuantityByMedicineId(medicineId);
        return total != null ? total : 0;
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

        return repository.save(saleRecord);
    }
}
