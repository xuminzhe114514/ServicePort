package com.example.demo.controller;

import com.example.demo.entity.Stock;
import com.example.demo.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stocks")
@CrossOrigin(origins = "*")
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "StockController is working!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取所有库存（分页）
     * GET /api/stocks
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllStocks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        try {
            Sort sort = direction.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Stock> stockPage = stockService.findAll(pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("currentPage", stockPage.getNumber());
            response.put("totalItems", stockPage.getTotalElements());
            response.put("totalPages", stockPage.getTotalPages());

            List<Map<String, Object>> stockList = stockPage.getContent().stream()
                    .map(this::createStockResponse)
                    .toList();
            response.put("data", stockList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据ID获取库存
     * GET /api/stocks/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getStockById(@PathVariable Long id) {
        try {
            Stock stock = stockService.findById(id);
            if (stock != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", createStockResponse(stock));
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "库存记录不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 创建新库存
     * POST /api/stocks
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createStock(@RequestBody Stock stock) {
        if (stock.getStatus() == null) {
            stock.setStatus(1);
        }
        if (stock.getQuantity() == null) {
            stock.setQuantity(0);
        }
        if (stock.getWarningQuantity() == null) {
            stock.setWarningQuantity(10);
        }

        try {
            Stock savedStock = stockService.save(stock);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "库存创建成功");
            response.put("data", createStockResponse(savedStock));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "创建失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 更新库存信息
     * PUT /api/stocks/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateStock(
            @PathVariable Long id,
            @RequestBody Stock stock) {
        Stock existingStock = stockService.findById(id);
        if (existingStock == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "库存记录不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        existingStock.setMedicine(stock.getMedicine());
        existingStock.setBatchNumber(stock.getBatchNumber());
        existingStock.setProductionDate(stock.getProductionDate());
        existingStock.setExpirationDate(stock.getExpirationDate());
        existingStock.setQuantity(stock.getQuantity());
        existingStock.setWarningQuantity(stock.getWarningQuantity());
        existingStock.setShelfLocation(stock.getShelfLocation());
        existingStock.setStatus(stock.getStatus());
        existingStock.setMinimumOrderQuantity(stock.getMinimumOrderQuantity());
        existingStock.setLeadTimeDays(stock.getLeadTimeDays());
        existingStock.setReorderPoint(stock.getReorderPoint());

        try {
            Stock updatedStock = stockService.update(existingStock);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "库存更新成功");
            response.put("data", createStockResponse(updatedStock));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 删除库存
     * DELETE /api/stocks/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteStock(@PathVariable Long id) {
        try {
            Stock existingStock = stockService.findById(id);

            if (existingStock == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "库存记录不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            stockService.delete(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "库存删除成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据药品ID查找库存
     * GET /api/stocks/medicine/{medicineId}
     */
    @GetMapping("/medicine/{medicineId}")
    public ResponseEntity<Map<String, Object>> getStocksByMedicine(
            @PathVariable Long medicineId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

            Page<Stock> allStocks = stockService.findByMedicineId(medicineId, pageable);
            List<Stock> pageContent = allStocks.getContent();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);

            Map<String, Object> pageData = new HashMap<>();
            pageData.put("content", pageContent.stream().map(this::createStockResponse).toList());
            pageData.put("currentPage", page);
            pageData.put("pageSize", size);
            pageData.put("totalItems", allStocks.getTotalElements());
            pageData.put("totalPages", allStocks.getTotalPages());
            pageData.put("isFirst", allStocks.isFirst());
            pageData.put("isLast", allStocks.isLast());

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
     * 获取某个药品的总库存量
     * GET /api/stocks/medicine/{medicineId}/total
     */
    @GetMapping("/medicine/{medicineId}/total")
    public ResponseEntity<Map<String, Object>> getTotalStockByMedicine(@PathVariable Long medicineId) {
        try {
            Integer totalStock = stockService.getTotalStock(medicineId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                    "medicineId", medicineId,
                    "totalStock", totalStock != null ? totalStock : 0
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取即将过期的库存
     * GET /api/stocks/expiring
     */
    @GetMapping("/expiring")
    public ResponseEntity<Map<String, Object>> getExpiringStock(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            if (startDate == null) {
                startDate = LocalDate.now();
            }
            if (endDate == null) {
                endDate = LocalDate.now().plusDays(30);
            }
            Pageable pageable = PageRequest.of(page, size, Sort.by("expirationDate").ascending());

            Page<Stock> allStocks = stockService.getExpiringStock(startDate, endDate, pageable);
            List<Stock> pageContent = allStocks.getContent();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "查询成功");
            Map<String, Object> pageData = new HashMap<>();
            pageData.put("startDate", startDate);
            pageData.put("endDate", endDate);
            pageData.put("content", pageContent.stream().map(this::createStockResponse).toList());
            pageData.put("currentPage", page);
            pageData.put("pageSize", size);
            pageData.put("totalItems", allStocks.getTotalElements());
            pageData.put("totalPages", allStocks.getTotalPages());
            pageData.put("isFirst", allStocks.isFirst());
            pageData.put("isLast", allStocks.isLast());

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
     * 获取库存不足的药品
     * GET /api/stocks/low-stock
     */
    @GetMapping("/low-stock")
    public ResponseEntity<Map<String, Object>> getLowStock(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("quantity").ascending());
            Page<Stock> lowStocks = stockService.getLowStock(pageable);

            List<Stock> pageContent = lowStocks.getContent();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "查询成功");

            Map<String, Object> pageData = new HashMap<>();
            pageData.put("content", pageContent.stream().map(this::createStockResponse).toList());
            pageData.put("currentPage", page);
            pageData.put("pageSize", size);
            pageData.put("totalItems", lowStocks.getTotalElements());
            pageData.put("totalPages", lowStocks.getTotalPages());
            pageData.put("isFirst", lowStocks.isFirst());
            pageData.put("isLast", lowStocks.isLast());

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
     * 获取库存不足药品的汇总信息
     * GET /api/stocks/low-stock-summary
     */
    @GetMapping("/low-stock-summary")
    public ResponseEntity<Map<String, Object>> getLowStockSummary() {
        try {
            Map<Long, Integer> summary = stockService.getLowStockSummary();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", summary);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 减少库存（销售出库）
     * PUT /api/stocks/reduce
     */
    @PutMapping("/reduce")
    public ResponseEntity<Map<String, Object>> reduceStock(
            @RequestParam Long medicineId,
            @RequestParam Integer quantity) {

        if (medicineId == null || medicineId <= 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "药品ID无效");
            return ResponseEntity.badRequest().body(response);
        }
        if (quantity == null || quantity <= 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "减少数量必须大于0");
            return ResponseEntity.badRequest().body(response);
        }
        boolean isAvailable = stockService.checkStockAvailability(medicineId, quantity);
        if (!isAvailable) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "库存不足，无法减少");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            stockService.reduceStock(medicineId, quantity);
            Integer remainingStock = stockService.getTotalStock(medicineId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "库存减少成功");
            response.put("data", Map.of(
                    "medicineId", medicineId,
                    "reducedQuantity", quantity,
                    "remainingStock", remainingStock != null ? remainingStock : 0
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "库存减少失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 增加库存（采购入库）
     * PUT /api/stocks/increase
     */
    @PutMapping("/increase")
    public ResponseEntity<Map<String, Object>> increaseStock(
            @RequestParam Long medicineId,
            @RequestParam Integer quantity,
            @RequestParam(required = false) String batchNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expirationDate) {
        if (medicineId == null || medicineId <= 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "药品ID无效");
            return ResponseEntity.badRequest().body(response);
        }
        if (quantity == null || quantity <= 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "增加数量必须大于0");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            stockService.increaseStock(medicineId, quantity, batchNumber, expirationDate);
            Integer newTotalStock = stockService.getTotalStock(medicineId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "库存增加成功");
            response.put("data", Map.of(
                    "medicineId", medicineId,
                    "addedQuantity", quantity,
                    "batchNumber", batchNumber,
                    "expirationDate", expirationDate,
                    "totalStock", newTotalStock != null ? newTotalStock : 0
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "库存增加失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 检查库存是否足够
     * GET /api/stocks/check-availability
     */
    @GetMapping("/check-availability")
    public ResponseEntity<Map<String, Object>> checkStockAvailability(
            @RequestParam Long medicineId,
            @RequestParam Integer requiredQuantity) {
        try {
            if (medicineId == null || medicineId <= 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "药品ID无效");
                return ResponseEntity.badRequest().body(response);
            }
            if (requiredQuantity == null || requiredQuantity <= 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "需求数量必须大于0");
                return ResponseEntity.badRequest().body(response);
            }
            boolean isAvailable = stockService.checkStockAvailability(medicineId, requiredQuantity);
            Integer currentStock = stockService.getTotalStock(medicineId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                    "medicineId", medicineId,
                    "requiredQuantity", requiredQuantity,
                    "currentStock", currentStock != null ? currentStock : 0,
                    "isAvailable", isAvailable,
                    "message", isAvailable ? "库存充足" : "库存不足"
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据批号查找库存
     * GET /api/stocks/batch/{batchNumber}
     */
    @GetMapping("/batch/{batchNumber}")
    public ResponseEntity<Map<String, Object>> getStockByBatchNumber(
            @PathVariable String batchNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            if (batchNumber == null || batchNumber.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "批号不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<Stock> batchStocks = stockService.findByBatchNumber(batchNumber,pageable);

            Map<String, Object> response = new HashMap<>();
            if (batchStocks.getTotalElements() == 0) {
                response.put("success", false);
                response.put("message", "未找到对应批号的药品库存");
                response.put("data", List.of());
            } else {
                response.put("success", true);
                response.put("message","查找成功");
                Map<String, Object> pageData = new HashMap<>();
                pageData.put("batchNumber", batchNumber);
                pageData.put("content", batchStocks.getContent().stream().map(this::createStockResponse).toList());
                pageData.put("currentPage", page);
                pageData.put("pageSize", size);
                pageData.put("totalItems", batchStocks.getTotalElements());
                pageData.put("totalPages", batchStocks.getTotalPages());
                pageData.put("isFirst", batchStocks.isFirst());
                pageData.put("isLast", batchStocks.isLast());
                response.put("data", pageData);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取过期库存
     * GET /api/stocks/expired
     */
    @GetMapping("/expired")
    public ResponseEntity<Map<String, Object>> getExpiredStock(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            Page<Stock> expiredStocks = stockService.findByStatus(0, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "查询成功");
            Map<String, Object> pageData = new HashMap<>();
            pageData.put("content", expiredStocks.stream().map(this::createStockResponse).toList());
            pageData.put("currentPage", page);
            pageData.put("pageSize", size);
            pageData.put("totalItems", expiredStocks.getTotalElements());
            pageData.put("totalPages", expiredStocks.getTotalPages());
            pageData.put("isFirst", expiredStocks.isFirst());
            pageData.put("isLast", expiredStocks.isLast());
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
     * 批量删除库存
     * DELETE /api/stocks/batch
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> deleteStocks(@RequestBody List<Long> ids) {
        try {
            for (Long id : ids) {
                if (!stockService.exists(id)) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "ID为 " + id + " 的库存记录不存在");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            }
            stockService.deleteAll(ids);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "批量删除成功，共删除 " + ids.size() + " 个库存记录");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 批量保存库存
     * POST /api/stocks/batch
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> createStocks(@RequestBody List<Stock> stocks) {
        for (Stock stock : stocks) {
            if (stock.getStatus() == null) {
                stock.setStatus(1);
            }
            if (stock.getQuantity() == null) {
                stock.setQuantity(0);
            }
            if (stock.getWarningQuantity() == null) {
                stock.setWarningQuantity(10);
            }
        }

        List<Stock> savedStocks;
        try {
            savedStocks = stockService.saveAll(stocks);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "批量保存失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "批量创建成功，共创建 " + savedStocks.size() + " 个库存记录");
        response.put("data", savedStocks.stream().map(this::createStockResponse).toList());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 获取库存统计信息
     * GET /api/stocks/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStockStatistics() {
        try {
            // 构建统计信息
            Map<String, Object> statistics = new HashMap<>();

            // 1. 低库存药品统计
            Map<Long, Integer> lowStockSummary = stockService.getLowStockSummary();
            statistics.put("lowStockCount", lowStockSummary.size());
            statistics.put("lowStockDetails", lowStockSummary);

            // 2. 即将过期库存统计（7天内）
            List<Stock> expiringStock = stockService.findExpiringWithinDays(7);
            statistics.put("expiringStockCount", expiringStock.size());
            statistics.put("expiringStockDetails", expiringStock.stream()
                    .map(this::createStockResponse)
                    .collect(Collectors.toList()));

            // 3. 库存状态统计
            Page<Stock> activeStocks = stockService.findByStatus(1, PageRequest.of(0, 1));
            Page<Stock> inactiveStocks = stockService.findByStatus(0, PageRequest.of(0, 1));
            statistics.put("activeStockCount", activeStocks.getTotalElements());
            statistics.put("inactiveStockCount", inactiveStocks.getTotalElements());

            // 4. 过期库存统计
            Page<Stock> expiredStocks = stockService.findExpiredStock(PageRequest.of(0, 1));
            statistics.put("expiredStockCount", expiredStocks.getTotalElements());

            // 5. 近过期库存统计（30天内）
            Page<Stock> nearExpiryStocks = stockService.findNearExpiryStock(30, PageRequest.of(0, 1));
            statistics.put("nearExpiryStockCount", nearExpiryStocks.getTotalElements());

            // 6. 库存价值统计
            Double totalStockValue = stockService.calculateTotalStockValue();
            statistics.put("totalStockValue", totalStockValue != null ? totalStockValue : 0);

            // 7. 库存周转率
            Double turnoverRate = stockService.calculateStockTurnoverRate("month");
            statistics.put("monthlyTurnoverRate", turnoverRate != null ? turnoverRate : 0);

            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "库存统计信息获取成功");
            response.put("data", statistics);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "统计信息获取失败: " + e.getMessage());
            response.put("data", Map.of());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 库存盘点启动
     * POST /api/stocks/inventory/initiate
     */
    @Deprecated
    @PostMapping("/inventory/initiate")
    public ResponseEntity<Map<String, Object>> initiateInventory() {
        try {
            // 注：需要在Service层添加Inventory相关的实现
            // 1. 创建盘点任务记录
            // 2. 锁定当前库存状态
            // 3. 生成盘点清单
            
            Map<String, Object> inventoryInfo = new HashMap<>();
            inventoryInfo.put("inventoryId", System.currentTimeMillis());
            inventoryInfo.put("initiateTime", LocalDateTime.now());
            inventoryInfo.put("status", "INITIATED");
            inventoryInfo.put("note", "库存盘点功能需要在Service层实现Inventory相关功能");

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "库存盘点启动成功");
            response.put("data", inventoryInfo);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 盘点结果记录
     * POST /api/stocks/inventory/record
     */
    @Deprecated
    @PostMapping("/inventory/record")
    public ResponseEntity<Map<String, Object>> recordInventory(
            @RequestParam Long inventoryId,
            @RequestParam Long stockId,
            @RequestParam Integer actualQuantity) {
        try {
            // 注：需要在Service层添加Inventory相关的实现
            // 1. 获取库存记录
            // 2. 计算差异
            // 3. 更新库存数量
            // 4. 记录盘点差异
            
            // 获取当前库存信息
            Stock stock = stockService.findById(stockId);
            if (stock == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "库存记录不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Map<String, Object> inventoryRecord = new HashMap<>();
            inventoryRecord.put("inventoryId", inventoryId);
            inventoryRecord.put("stockId", stockId);
            inventoryRecord.put("medicineName", stock.getMedicine() != null ? stock.getMedicine().getName() : "");
            inventoryRecord.put("batchNumber", stock.getBatchNumber());
            inventoryRecord.put("systemQuantity", stock.getQuantity());
            inventoryRecord.put("actualQuantity", actualQuantity);
            inventoryRecord.put("difference", actualQuantity - stock.getQuantity());
            inventoryRecord.put("recordTime", LocalDateTime.now());
            inventoryRecord.put("note", "库存盘点功能需要在Service层实现Inventory相关功能");

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "盘点结果记录成功");
            response.put("data", inventoryRecord);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 盘点历史查询
     * GET /api/stocks/inventory/history
     */
    @Deprecated
    @GetMapping("/inventory/history")
    public ResponseEntity<Map<String, Object>> getInventoryHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            // 注：需要在Service层添加Inventory相关的实现
            // 1. 查询盘点任务历史
            // 2. 按条件筛选
            // 3. 分页返回结果
            
            Map<String, Object> response = new HashMap<>();
            Map<String, Object> pageData = new HashMap<>();
            pageData.put("content", List.of());
            pageData.put("currentPage", page);
            pageData.put("pageSize", size);
            pageData.put("totalItems", 0);
            pageData.put("totalPages", 0);
            pageData.put("note", "库存盘点功能需要在Service层实现Inventory相关功能");
            
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
     * 库存交易记录
     * GET /api/stocks/transactions
     */
    @GetMapping("/transactions")
    public ResponseEntity<Map<String, Object>> getStockTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {

            Pageable pageable = PageRequest.of(page, size, Sort.by("updateTime").descending());
            Page<Stock> stockPage = stockService.findAll(pageable);

            List<Map<String, Object>> transactions = stockPage.getContent().stream()
                    .map(stock -> {
                        Map<String, Object> transaction = new HashMap<>();
                        transaction.put("transactionId", stock.getId());
                        transaction.put("medicineId", stock.getMedicine() != null ? stock.getMedicine().getId() : null);
                        transaction.put("medicineName", stock.getMedicine() != null ? stock.getMedicine().getName() : null);
                        transaction.put("batchNumber", stock.getBatchNumber());
                        transaction.put("quantity", stock.getQuantity());
                        transaction.put("shelfLocation", stock.getShelfLocation());
                        transaction.put("transactionTime", stock.getUpdateTime());
                        transaction.put("transactionType", "STOCK_UPDATE");
                        return transaction;
                    })
                    .toList();

            Map<String, Object> response = new HashMap<>();
            Map<String, Object> pageData = new HashMap<>();
            pageData.put("content", transactions);
            pageData.put("currentPage", page);
            pageData.put("pageSize", size);
            pageData.put("totalItems", stockPage.getTotalElements());
            pageData.put("totalPages", stockPage.getTotalPages());
            response.put("data", pageData);
            response.put("success", true);
            response.put("message", "查询成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 库存价值评估
     * GET /api/stocks/valuation
     */
    @GetMapping("/valuation")
    public ResponseEntity<Map<String, Object>> getStockValuation() {
        try {
            Double totalStockValue = stockService.calculateTotalStockValue();
            Map<String, Object> stockValueByCategory = stockService.getStockValueByCategory();

            Map<String, Object> valuation = new HashMap<>();
            valuation.put("totalStockValue", totalStockValue != null ? totalStockValue : 0);
            valuation.put("stockValueByCategory", stockValueByCategory);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "库存价值评估成功");
            response.put("data", valuation);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 按库位查询库存
     * GET /api/stocks/location/{location}
     */
    @GetMapping("/location/{location}")
    public ResponseEntity<Map<String, Object>> getStocksByLocation(
            @PathVariable String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Stock> locationStocks = stockService.findByShelfLocationContaining(location, pageable);

            Map<String, Object> response = new HashMap<>();
            Map<String, Object> pageData = new HashMap<>();
            pageData.put("content", locationStocks.getContent().stream().map(this::createStockResponse).toList());
            pageData.put("currentPage", page);
            pageData.put("pageSize", size);
            pageData.put("totalItems", locationStocks.getTotalElements());
            pageData.put("totalPages", locationStocks.getTotalPages());
            response.put("data", pageData);
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 库存周转率分析
     * GET /api/stocks/turnover
     */
    @GetMapping("/turnover")
    public ResponseEntity<Map<String, Object>> getStockTurnover(
            @RequestParam(defaultValue = "month") String period) {
        try {
            Double turnoverRate = stockService.calculateStockTurnoverRate(period);

            Map<String, Object> turnoverData = new HashMap<>();
            turnoverData.put("period", period);
            turnoverData.put("turnoverRate", turnoverRate != null ? turnoverRate : 0);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "库存周转率分析成功");
            response.put("data", turnoverData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 创建库存响应对象
     */
    private Map<String, Object> createStockResponse(Stock stock) {
        Map<String, Object> stockResponse = new HashMap<>();
        stockResponse.put("id", stock.getId());

        if (stock.getMedicine() != null) {
            Map<String, Object> medicineInfo = new HashMap<>();
            medicineInfo.put("id", stock.getMedicine().getId());
            medicineInfo.put("medicineCode", stock.getMedicine().getMedicineCode());
            medicineInfo.put("name", stock.getMedicine().getName());
            medicineInfo.put("specification", stock.getMedicine().getSpecification());
            medicineInfo.put("unit", stock.getMedicine().getUnit());
            stockResponse.put("medicine", medicineInfo);
        }

        stockResponse.put("batchNumber", stock.getBatchNumber());
        stockResponse.put("productionDate", stock.getProductionDate());
        stockResponse.put("expirationDate", stock.getExpirationDate());
        stockResponse.put("quantity", stock.getQuantity());
        stockResponse.put("warningQuantity", stock.getWarningQuantity());
        stockResponse.put("shelfLocation", stock.getShelfLocation());
        stockResponse.put("status", stock.getStatus());
        stockResponse.put("minimumOrderQuantity", stock.getMinimumOrderQuantity());
        stockResponse.put("leadTimeDays", stock.getLeadTimeDays());
        stockResponse.put("reorderPoint", stock.getReorderPoint());
        stockResponse.put("createTime", stock.getCreateTime());
        stockResponse.put("updateTime", stock.getUpdateTime());

        if (stock.getExpirationDate() != null) {
            boolean isExpired = stock.getExpirationDate().isBefore(LocalDate.now());
            stockResponse.put("isExpired", isExpired);
        }
        if (stock.getQuantity() != null && stock.getWarningQuantity() != null) {
            boolean needsWarning = stock.getQuantity() <= stock.getWarningQuantity();
            stockResponse.put("needsWarning", needsWarning);
        }

        return stockResponse;
    }
}