package com.example.demo.controller;

import com.example.demo.entity.Medicine;
import com.example.demo.entity.SaleRecord;
import com.example.demo.entity.User;
import com.example.demo.service.MedicineService;
import com.example.demo.service.SaleRecordService;
import com.example.demo.service.StockService;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sale-records")
@CrossOrigin(origins = "*")
@Slf4j
public class SaleRecordController {

    @Autowired
    private SaleRecordService saleRecordService;

    @Autowired
    private UserService userService;

    @Autowired
    private StockService stockService;
    
    @Autowired
    private MedicineService medicineService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("SaleRecordController is working!");
    }

    /**
     * 获取所有销售记录（分页），支持关键词搜索
     * GET /api/sale-records
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSaleRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "saleTime") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String keyword) {
        try {
            Sort sort = direction.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<SaleRecord> saleRecordPage;

            if (keyword != null && !keyword.trim().isEmpty()) {
                saleRecordPage = saleRecordService.searchByKeyword(keyword.trim(), pageable);
            } else {
                saleRecordPage = saleRecordService.findAll(pageable);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("currentPage", saleRecordPage.getNumber());
            response.put("totalItems", saleRecordPage.getTotalElements());
            response.put("totalPages", saleRecordPage.getTotalPages());

            List<Map<String, Object>> recordList = saleRecordPage.getContent().stream()
                    .map(this::createSaleRecordResponse)
                    .collect(Collectors.toList());
            response.put("data", recordList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取销售记录失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 多条件联立查询销售记录
     * GET /api/sale-records/search
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchSaleRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "saleTime") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) Long operatorId,
            @RequestParam(required = false) Integer symptomId,
            @RequestParam(required = false) Long medicineId) {
        try {
            Sort sort = direction.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            LocalDateTime startDateTime = null;
            LocalDateTime endDateTime = null;

            if (startTime != null && !startTime.trim().isEmpty()) {
                try {
                    startDateTime = LocalDateTime.parse(startTime.trim(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                } catch (Exception e) {
                    startDateTime = LocalDate.parse(startTime.trim()).atStartOfDay();
                }
            }

            if (endTime != null && !endTime.trim().isEmpty()) {
                try {
                    endDateTime = LocalDateTime.parse(endTime.trim(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                } catch (Exception e) {
                    endDateTime = LocalDate.parse(endTime.trim()).atTime(23, 59, 59);
                }
            }

            Page<SaleRecord> saleRecordPage = saleRecordService.findByMultipleConditions(
                    keyword, startDateTime, endDateTime, operatorId, symptomId, medicineId, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("currentPage", saleRecordPage.getNumber());
            response.put("totalItems", saleRecordPage.getTotalElements());
            response.put("totalPages", saleRecordPage.getTotalPages());

            List<Map<String, Object>> recordList = saleRecordPage.getContent().stream()
                    .map(this::createSaleRecordResponse)
                    .collect(Collectors.toList());
            response.put("data", recordList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "查询销售记录失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据ID获取销售记录
     * GET /api/sale-records/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSaleRecordById(@PathVariable Long id) {
        try {
            SaleRecord saleRecord = saleRecordService.findById(id);

            if (saleRecord != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", createSaleRecordResponse(saleRecord));
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "销售记录不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取销售记录失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据销售单号获取销售记录
     * GET /api/sale-records/record-no/{recordNo}
     */
    @GetMapping("/record-no/{recordNo}")
    public ResponseEntity<Map<String, Object>> getSaleRecordByRecordNo(@PathVariable String recordNo) {
        try {
            SaleRecord saleRecord = saleRecordService.findByRecordNo(recordNo);

            if (saleRecord != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", createSaleRecordResponse(saleRecord));
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "销售记录不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取销售记录失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 创建销售记录
     * POST /api/sale-records
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createSaleRecord(@RequestBody SaleRecord saleRecord) {
        try {
            // Validate medicine
            if (saleRecord.getMedicine() == null || saleRecord.getMedicine().getId() == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "请选择药品");
                return ResponseEntity.badRequest().body(response);
            }
            
            Medicine medicine = medicineService.findById(saleRecord.getMedicine().getId());
            if (medicine == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "药品不存在，请重新选择");
                return ResponseEntity.badRequest().body(response);
            }
            saleRecord.setMedicine(medicine);
            
            // Validate operator
            Long operatorId = null;
            if (saleRecord.getOperator() != null && saleRecord.getOperator().getId() != null) {
                operatorId = saleRecord.getOperator().getId();
                User operator = userService.findById(operatorId);
                if (operator == null) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "操作员不存在");
                    return ResponseEntity.badRequest().body(response);
                }
            }
            
            // Calculate total amount if not provided
            if (saleRecord.getTotalAmount() == null || saleRecord.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
                if (saleRecord.getQuantity() != null && saleRecord.getUnitPrice() != null) {
                    BigDecimal total = saleRecord.getUnitPrice().multiply(BigDecimal.valueOf(saleRecord.getQuantity()));
                    saleRecord.setTotalAmount(total);
                }
            }

            // Set sale time if not provided
            if (saleRecord.getSaleTime() == null) {
                saleRecord.setSaleTime(LocalDateTime.now());
            }

            SaleRecord savedRecord = saleRecordService.createSaleRecordWithStockDeduction(saleRecord, operatorId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "销售记录创建成功，库存已自动扣减");
            response.put("data", createSaleRecordResponse(savedRecord));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Failed to create sale record: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "创建销售记录失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 更新销售记录
     * PUT /api/sale-records/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateSaleRecord(@PathVariable Long id, @RequestBody SaleRecord saleRecord) {
        try {
            SaleRecord existingRecord = saleRecordService.findById(id);

            if (existingRecord == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "销售记录不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (saleRecord.getCustomerInfo() != null) {
                existingRecord.setCustomerInfo(saleRecord.getCustomerInfo());
            }

            if (saleRecord.getCustomerType() != null) {
                existingRecord.setCustomerType(saleRecord.getCustomerType());
            }

            existingRecord.setRx(saleRecord.isRx());

            if (saleRecord.getRemark() != null) {
                existingRecord.setRemark(saleRecord.getRemark());
            }

            if (saleRecord.getSymptom() != null) {
                existingRecord.setSymptom(saleRecord.getSymptom());
            }

            SaleRecord updatedRecord = saleRecordService.update(existingRecord);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "销售记录更新成功");
            response.put("data", createSaleRecordResponse(updatedRecord));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "更新销售记录失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 删除销售记录
     * DELETE /api/sale-records/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSaleRecord(@PathVariable Long id) {
        try {
            SaleRecord existingRecord = saleRecordService.findById(id);

            if (existingRecord == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "销售记录不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            saleRecordService.delete(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "销售记录删除成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "删除销售记录失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据时间段查询销售记录
     * GET /api/sale-records/time-range
     */
    @GetMapping("/time-range")
    public ResponseEntity<Map<String, Object>> getSaleRecordsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<SaleRecord> allSaleRecords = saleRecordService.findBySaleTimeBetween(startTime, endTime);

            int start = page * size;
            int end = Math.min(start + size, allSaleRecords.size());

            List<SaleRecord> pagedRecords;
            if (start >= allSaleRecords.size()) {
                pagedRecords = new ArrayList<>();
            } else {
                pagedRecords = allSaleRecords.subList(start, end);
            }

            int totalPages = (int) Math.ceil((double) allSaleRecords.size() / size);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("currentPage", page);
            response.put("totalItems", allSaleRecords.size());
            response.put("totalPages", totalPages);

            List<Map<String, Object>> recordList = pagedRecords.stream()
                    .map(this::createSaleRecordResponse)
                    .collect(Collectors.toList());
            response.put("data", recordList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取销售记录失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据药品ID查询销售记录
     * GET /api/sale-records/medicine/{medicineId}
     */
    @GetMapping("/medicine/{medicineId}")
    public ResponseEntity<Map<String, Object>> getSaleRecordsByMedicineId(
            @PathVariable Long medicineId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<SaleRecord> saleRecords = saleRecordService.findByMedicineId(medicineId);

            int start = page * size;
            int end = Math.min(start + size, saleRecords.size());

            List<SaleRecord> pagedRecords;
            if (start >= saleRecords.size()) {
                pagedRecords = new ArrayList<>();
            } else {
                pagedRecords = saleRecords.subList(start, end);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("currentPage", page);
            response.put("totalItems", saleRecords.size());
            response.put("totalPages", (int) Math.ceil((double) saleRecords.size() / size));

            List<Map<String, Object>> recordList = pagedRecords.stream()
                    .map(this::createSaleRecordResponse)
                    .collect(Collectors.toList());
            response.put("data", recordList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取销售记录失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据操作员ID查询销售记录
     * GET /api/sale-records/operator/{operatorId}
     */
    @GetMapping("/operator/{operatorId}")
    public ResponseEntity<Map<String, Object>> getSaleRecordsByOperatorId(
            @PathVariable Long operatorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<SaleRecord> saleRecords = saleRecordService.findByOperatorId(operatorId);

            int start = page * size;
            int end = Math.min(start + size, saleRecords.size());

            List<SaleRecord> pagedRecords;
            if (start >= saleRecords.size()) {
                pagedRecords = new ArrayList<>();
            } else {
                pagedRecords = saleRecords.subList(start, end);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("currentPage", page);
            response.put("totalItems", saleRecords.size());
            response.put("totalPages", (int) Math.ceil((double) saleRecords.size() / size));

            List<Map<String, Object>> recordList = pagedRecords.stream()
                    .map(this::createSaleRecordResponse)
                    .collect(Collectors.toList());
            response.put("data", recordList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取销售记录失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取销售统计信息
     * GET /api/sale-records/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getSaleStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            if (startTime == null || endTime == null) {
                endTime = LocalDateTime.now();
                startTime = endTime.minusDays(30);
            }
            Double totalAmount = saleRecordService.getTotalSalesByPeriod(startTime, endTime);
            List<Map<String, Object>> dailySales = saleRecordService.getDailySalesReport(startTime, endTime);
            List<Map<String, Object>> topSellingMedicines = saleRecordService.getTopSellingMedicines(10,startTime, endTime);

            Map<String, Object> statistics = new HashMap<>();
            statistics.put("period", Map.of("start", startTime, "end", endTime));
            statistics.put("totalAmount", totalAmount != null ? totalAmount : 0.0);
            statistics.put("dailySales", dailySales);
            statistics.put("topSellingMedicines", topSellingMedicines);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", statistics);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取销售统计信息失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取最畅销药品
     * GET /api/sale-records/top-selling
     */
    @GetMapping("/top-selling")
    public ResponseEntity<Map<String, Object>> getTopSellingMedicines(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            if (startDate == null || endDate == null) {
                endDate = LocalDateTime.now();
                startDate = endDate.minusDays(30);
            }

            List<Map<String, Object>> topMedicines = saleRecordService.getTopSellingMedicines(limit,startDate,endDate);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("period", Map.of("start", startDate, "end", endDate));
            response.put("data", topMedicines);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取畅销药品失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取某个药品的销售总量
     * GET /api/sale-records/medicine/{medicineId}/total-quantity
     */
    @GetMapping("/medicine/{medicineId}/total-quantity")
    public ResponseEntity<Map<String, Object>> getTotalQuantityByMedicineId(@PathVariable Long medicineId) {
        try {
            Integer totalQuantity = saleRecordService.getTotalQuantityByMedicineId(medicineId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("medicineId", medicineId);
            response.put("totalQuantity", totalQuantity != null ? totalQuantity : 0);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取药品销售总量失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 按症状查询销售记录
     * GET /api/sale-records/symptom/{symptomId}
     */
    @GetMapping("/symptom/{symptomId}")
    public ResponseEntity<Map<String, Object>> getSalesBySymptom(
            @PathVariable Integer symptomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("saleTime").descending());
            Page<SaleRecord> allSales = saleRecordService.findAll(pageable);
            
            List<SaleRecord> salesWithSymptom = allSales.getContent().stream()
                    .filter(sale -> {
                        List<com.example.demo.entity.Symptom> symptoms = sale.getSymptom();
                        return symptoms != null && symptoms.stream()
                                .anyMatch(symptom -> symptom.getId().equals(symptomId.longValue()));
                    })
                    .collect(Collectors.toList());

            int start = page * size;
            int end = Math.min(start + size, salesWithSymptom.size());
            List<SaleRecord> pagedSales = new ArrayList<>();
            if (start < salesWithSymptom.size()) {
                pagedSales = salesWithSymptom.subList(start, end);
            }

            Map<String, Object> response = new HashMap<>();
            Map<String, Object> pageData = new HashMap<>();
            pageData.put("content", pagedSales.stream()
                    .map(this::createSaleRecordResponse)
                    .collect(Collectors.toList()));
            pageData.put("currentPage", page);
            pageData.put("pageSize", size);
            pageData.put("totalItems", salesWithSymptom.size());
            pageData.put("totalPages", (int) Math.ceil((double) salesWithSymptom.size() / size));
            
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", pageData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 处理销售并更新库存
     * PUT /api/sale-records/{id}/process
     */
    @PutMapping("/{id}/process")
    public ResponseEntity<Map<String, Object>> processSaleRecord(
            @PathVariable Long id) {
        try {
            SaleRecord saleRecord = saleRecordService.findById(id);
            if (saleRecord == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "销售记录不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (saleRecord.getMedicine() == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "销售记录缺少药品信息");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Long medicineId = saleRecord.getMedicine().getId();
            Integer quantity = saleRecord.getQuantity();

            boolean isAvailable = stockService.checkStockAvailability(medicineId, quantity);
            if (!isAvailable) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "库存不足，无法处理销售");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            stockService.reduceStock(medicineId, quantity);

            Integer remainingStock = stockService.getTotalStock(medicineId);

            Map<String, Object> response = new HashMap<>();
            Map<String, Object> data = new HashMap<>();
            data.put("saleRecordId", id);
            data.put("medicineId", medicineId);
            data.put("medicineName", saleRecord.getMedicine().getName());
            data.put("processedQuantity", quantity);
            data.put("remainingStock", remainingStock != null ? remainingStock : 0);
            data.put("processedTime", LocalDateTime.now());
            data.put("status", "PROCESSED");

            response.put("success", true);
            response.put("message", "销售处理成功，库存已更新");
            response.put("data", data);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 出库单创建
     * POST /api/sales/issuances
     * @deprecated 该功能暂未实现，建议使用销售记录创建API（POST /api/sale-records）代替
     */
    @Deprecated
    @PostMapping("/issuances")
    public ResponseEntity<Map<String, Object>> createSalesIssuance(
            @RequestBody Map<String, Object> issuanceData) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "出库单功能暂未实现，请使用销售记录创建API代替");
            response.put("data", Map.of(
                    "note", "该API已标记为废弃，建议使用销售记录创建API（POST /api/sale-records）",
                    "alternative", "/api/sale-records"
            ));

            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 出库单查询
     * GET /api/sales/issuances
     * @deprecated 该功能暂未实现，建议使用销售记录查询API代替
     */
    @Deprecated
    @GetMapping("/issuances")
    public ResponseEntity<Map<String, Object>> getSalesIssuances(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "出库单功能暂未实现，请使用销售记录查询API代替");
            response.put("data", Map.of(
                    "note", "该API已标记为废弃，建议使用销售记录查询API（GET /api/sale-records）",
                    "alternative", "/api/sale-records"
            ));

            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 创建销售记录响应对象
     */
    private Map<String, Object> createSaleRecordResponse(SaleRecord saleRecord) {
        Map<String, Object> recordResponse = new HashMap<>();
        recordResponse.put("id", saleRecord.getId());
        recordResponse.put("recordNo", saleRecord.getRecordNo());

        if (saleRecord.getMedicine() != null) {
            Map<String, Object> medicineInfo = new HashMap<>();
            medicineInfo.put("id", saleRecord.getMedicine().getId());
            medicineInfo.put("medicineCode", saleRecord.getMedicine().getMedicineCode());
            medicineInfo.put("name", saleRecord.getMedicine().getName());
            medicineInfo.put("specification", saleRecord.getMedicine().getSpecification());
            medicineInfo.put("unit", saleRecord.getMedicine().getUnit());
            recordResponse.put("medicine", medicineInfo);
        }

        recordResponse.put("quantity", saleRecord.getQuantity());
        recordResponse.put("unitPrice", saleRecord.getUnitPrice());
        recordResponse.put("totalAmount", saleRecord.getTotalAmount());
        recordResponse.put("customerInfo", saleRecord.getCustomerInfo());
        recordResponse.put("customerType", saleRecord.getCustomerType());
        recordResponse.put("isRx", saleRecord.isRx());
        recordResponse.put("saleTime", saleRecord.getSaleTime());

        if (saleRecord.getSymptom() != null && !saleRecord.getSymptom().isEmpty()) {
            List<Map<String, Object>> symptomList = saleRecord.getSymptom().stream()
                    .map(symptom -> {
                        Map<String, Object> symptomMap = new HashMap<>();
                        symptomMap.put("id", symptom.getId());
                        symptomMap.put("name", symptom.getName());
                        return symptomMap;
                    })
                    .collect(Collectors.toList());
            recordResponse.put("symptoms", symptomList);
        }

        if (saleRecord.getOperator() != null) {
            Map<String, Object> operatorInfo = new HashMap<>();
            operatorInfo.put("id", saleRecord.getOperator().getId());
            operatorInfo.put("username", saleRecord.getOperator().getUsername());
            operatorInfo.put("realName", saleRecord.getOperator().getRealName());
            recordResponse.put("operator", operatorInfo);
        }

        recordResponse.put("remark", saleRecord.getRemark());

        return recordResponse;
    }
}