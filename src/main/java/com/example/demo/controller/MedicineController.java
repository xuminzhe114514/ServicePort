package com.example.demo.controller;

import com.example.demo.entity.Medicine;
import com.example.demo.entity.PurchaseOrder;
import com.example.demo.entity.Symptom;
import com.example.demo.service.MedicineService;
import com.example.demo.service.PurchaseOrderService;
import com.example.demo.service.SaleRecordService;
import com.example.demo.service.StockService;
import com.example.demo.service.SymptomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/medicines")
@CrossOrigin(origins = "*")
public class MedicineController {

    @Autowired
    private MedicineService medicineService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private SaleRecordService  saleRecordService;

    @Autowired
    private StockService  stockService;

    @Autowired
    private SymptomService symptomService;

    private final int DEFAULT_WARNING_VALUE = 10;

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "MedicineController is working!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取所有药品（分页）
     * GET /api/medicines
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllMedicines(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        try {
            Sort sort = direction.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Medicine> medicinePage = medicineService.findAll(pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("currentPage", medicinePage.getNumber());
            response.put("totalItems", medicinePage.getTotalElements());
            response.put("totalPages", medicinePage.getTotalPages());

            List<Map<String, Object>> medicineList = medicinePage.getContent().stream()
                    .map(this::createMedicineResponse)
                    .toList();
            response.put("data", medicineList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据ID获取药品
     * GET /api/medicines/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMedicineById(@PathVariable Long id) {
        try {
            Medicine medicine = medicineService.findById(id);

            if (medicine != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", createMedicineResponse(medicine));
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "药品不存在");
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
     * 创建新药品
     * POST /api/medicines
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createMedicine(@RequestBody Medicine medicine) {
        Medicine existingMedicine = medicineService.findByMedicineCode(medicine.getMedicineCode());

        if (existingMedicine != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "药品编码已存在");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        if (medicine.getStatus() == null) {
            medicine.setStatus(1);
        }

        try {
            Medicine savedMedicine = medicineService.save(medicine);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "药品创建成功");
            response.put("data", createMedicineResponse(savedMedicine));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "创建失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 更新药品信息
     * PUT /api/medicines/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateMedicine(
            @PathVariable Long id,
            @RequestBody Medicine medicine) {
        Medicine existingMedicine = medicineService.findById(id);

        if (existingMedicine == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "药品不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        if (!existingMedicine.getMedicineCode().equals(medicine.getMedicineCode())) {
            Medicine duplicateMedicine = medicineService.findByMedicineCode(medicine.getMedicineCode());
            if (duplicateMedicine != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "药品编码已存在");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        }

        // 更新药品信息
        existingMedicine.setMedicineCode(medicine.getMedicineCode());
        existingMedicine.setName(medicine.getName());
        existingMedicine.setGenericName(medicine.getGenericName());
        existingMedicine.setCategory(medicine.getCategory());
        existingMedicine.setSpecification(medicine.getSpecification());
        existingMedicine.setUnit(medicine.getUnit());
        existingMedicine.setManufacturer(medicine.getManufacturer());
        existingMedicine.setRetailPrice(medicine.getRetailPrice());
        existingMedicine.setPurchasePrice(medicine.getPurchasePrice());

        // 只有状态不为null时才更新状态
        if (medicine.getStatus() != null) {
            existingMedicine.setStatus(medicine.getStatus());
        }

        try {
            Medicine updatedMedicine = medicineService.update(existingMedicine);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "药品更新成功");
            response.put("data", createMedicineResponse(updatedMedicine));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 删除药品
     * DELETE /api/medicines/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMedicine(@PathVariable Long id) {
        try {
            Medicine existingMedicine = medicineService.findById(id);

            if (existingMedicine == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "药品不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Page<PurchaseOrder> purchaseMedicines = purchaseOrderService.findByMedicineId(id, PageRequest.of(0, 1));
            int total = (int)purchaseMedicines.getTotalElements();

            List<PurchaseOrder> purchasedMedicines = purchaseOrderService.findByMedicineId(id, PageRequest.of(0, total)).getContent().stream().filter(purchaseOrder -> purchaseOrder.getOrderStatus()!=3).toList();

            if (!purchasedMedicines.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "药品存在采购订单");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Integer totalQuantity = saleRecordService.getTotalQuantityByMedicineId(id);

            if (totalQuantity > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "药品存在未清理的销售记录");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Integer totalStocks = stockService.getTotalStock(id);

            if (totalStocks > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "药品存在未清理的库存记录");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            

            medicineService.delete(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "药品删除成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据药品编码查找药品
     * GET /api/medicines/code/{medicineCode}
     */
    @GetMapping("/code/{medicineCode}")
    public ResponseEntity<Map<String, Object>> getMedicineByCode(@PathVariable String medicineCode) {
        try {
            Medicine medicine = medicineService.findByMedicineCode(medicineCode);

            if (medicine != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", createMedicineResponse(medicine));
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "药品不存在");
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
     * 根据状态分页查询药品
     * GET /api/medicines/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getMedicinesByStatus(
            @PathVariable Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            // 验证状态值
            if (status != 0 && status != 1) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "状态值无效，应为0（停用）或1（启用）");
                return ResponseEntity.badRequest().body(response);
            }

            Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
            Page<Medicine> medicinePage = medicineService.findByStatus(status, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("currentPage", medicinePage.getNumber());
            response.put("totalItems", medicinePage.getTotalElements());
            response.put("totalPages", medicinePage.getTotalPages());

            List<Map<String, Object>> medicineList = medicinePage.getContent().stream()
                    .map(this::createMedicineResponse)
                    .toList();
            response.put("data", medicineList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 搜索药品（名称、通用名、生产厂家）
     * GET /api/medicines/search
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchMedicines(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "关键词不能为空");
                response.put("data", List.of());
                return ResponseEntity.badRequest().body(response);
            }

            List<Medicine> allMedicines = medicineService.searchMedicines(keyword);

            int start = page * size;
            int end = Math.min(start + size, allMedicines.size());

            if (start > allMedicines.size()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", List.of());
                response.put("currentPage", page);
                response.put("totalItems", allMedicines.size());
                response.put("totalPages", (int) Math.ceil((double) allMedicines.size() / size));
                return ResponseEntity.ok(response);
            }

            List<Medicine> pageContent = allMedicines.subList(start, end);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "搜索成功");

            Map<String, Object> pageData = new HashMap<>();
            pageData.put("content", pageContent.stream().map(this::createMedicineResponse).toList());
            pageData.put("currentPage", page);
            pageData.put("pageSize", size);
            pageData.put("totalItems", allMedicines.size());
            pageData.put("totalPages", (int) Math.ceil((double) allMedicines.size() / size));
            pageData.put("isFirst", page == 0);
            pageData.put("isLast", end >= allMedicines.size());

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
     * 根据分类ID查找药品
     * GET /api/medicines/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Map<String, Object>> getMedicinesByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

            List<Medicine> allMedicines = medicineService.findByCategoryId(categoryId);

            int start = page * size;
            int end = Math.min(start + size, allMedicines.size());

            if (start > allMedicines.size()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", List.of());
                response.put("currentPage", page);
                response.put("totalItems", allMedicines.size());
                response.put("totalPages", (int) Math.ceil((double) allMedicines.size() / size));
                return ResponseEntity.ok(response);
            }

            List<Medicine> pageContent = allMedicines.subList(start, end);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);

            Map<String, Object> pageData = new HashMap<>();
            pageData.put("content", pageContent.stream().map(this::createMedicineResponse).toList());
            pageData.put("currentPage", page);
            pageData.put("pageSize", size);
            pageData.put("totalItems", allMedicines.size());
            pageData.put("totalPages", (int) Math.ceil((double) allMedicines.size() / size));
            pageData.put("isFirst", page == 0);
            pageData.put("isLast", end >= allMedicines.size());

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
     * 更新药品价格
     * PUT /api/medicines/{id}/price
     */
    @PutMapping("/{id}/price")
    public ResponseEntity<Map<String, Object>> updateMedicinePrice(
            @PathVariable Long id,
            @RequestParam(required = false) BigDecimal retailPrice,
            @RequestParam(required = false) BigDecimal purchasePrice) {

        Medicine existingMedicine = medicineService.findById(id);

        if (existingMedicine == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "药品不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // 验证价格参数
        if (retailPrice == null && purchasePrice == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "至少需要提供一个价格参数");
            return ResponseEntity.badRequest().body(response);
        }

        if (retailPrice != null && retailPrice.compareTo(BigDecimal.ZERO) <= 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "零售价必须大于0");
            return ResponseEntity.badRequest().body(response);
        }

        if (purchasePrice != null && purchasePrice.compareTo(BigDecimal.ZERO) <= 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "采购价必须大于0");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Medicine updatedMedicine = medicineService.updatePrice(id, retailPrice, purchasePrice);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "药品价格更新成功");
            response.put("data", createMedicineResponse(updatedMedicine));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "价格更新失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 检查药品编码是否存在
     * GET /api/medicines/check-code/{medicineCode}
     */
    @GetMapping("/check-code/{medicineCode}")
    public ResponseEntity<Map<String, Object>> checkMedicineCodeExists(@PathVariable String medicineCode) {
        try {
            Medicine medicine = medicineService.findByMedicineCode(medicineCode);
            boolean exists = medicine != null;

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("exists", exists);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 批量删除药品
     * DELETE /api/medicines/batch
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> deleteMedicines(@RequestBody List<Long> ids) {
        try {
            // 检查所有药品是否存在
            for (Long id : ids) {
                if (!medicineService.exists(id)) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "ID为 " + id + " 的药品不存在");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            }

            medicineService.deleteAll(ids);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "批量删除成功，共删除 " + ids.size() + " 个药品");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 批量保存药品
     * POST /api/medicines/batch
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> createMedicines(@RequestBody List<Medicine> medicines) {
        // 检查药品编码是否重复
        for (Medicine medicine : medicines) {
            Medicine existingMedicine = medicineService.findByMedicineCode(medicine.getMedicineCode());
            if (existingMedicine != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "药品编码 '" + medicine.getMedicineCode() + "' 已存在");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        }

        for (Medicine medicine : medicines) {
            if (medicine.getStatus() == null) {
                medicine.setStatus(1);
            }
        }

        List<Medicine> savedMedicines;
        try {
            savedMedicines = medicineService.saveAll(medicines);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "批量保存失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "批量创建成功，共创建 " + savedMedicines.size() + " 个药品");
        response.put("data", savedMedicines.stream().map(this::createMedicineResponse).toList());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 检查药品是否存在
     * GET /api/medicines/{id}/exists
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Map<String, Object>> checkMedicineExists(@PathVariable Long id) {
        try {
            boolean exists = medicineService.exists(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("exists", exists);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取药品统计信息
     * GET /api/medicines/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getMedicineStatistics() {
        try {
            long totalMedicines = medicineService.findAll().size();
            long enabledCount = medicineService.countByStatus(1);
            long disabledCount = totalMedicines - enabledCount;

            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalMedicines", totalMedicines);
            statistics.put("enabledMedicines", enabledCount);
            statistics.put("disabledMedicines", disabledCount);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", statistics);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 修改药品状态
     * PUT /api/medicines/{id}/status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> changeMedicineStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        try {
            Medicine existingMedicine = medicineService.findById(id);

            if (existingMedicine == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "药品不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (status != 0 && status != 1) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "状态值无效，应为0（停用）或1（启用）");
                return ResponseEntity.badRequest().body(response);
            }

            existingMedicine.setStatus(status);
            Medicine updatedMedicine = medicineService.update(existingMedicine);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "药品状态更新成功");
            response.put("data", createMedicineResponse(updatedMedicine));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取药品简要列表
     * GET /api/medicines/simple
     */
    @GetMapping("/simple")
    public ResponseEntity<Map<String, Object>> getSimpleMedicineList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
            Page<Medicine> medicinePage = medicineService.findAll(pageable);

            List<Map<String, Object>> simpleList = medicinePage.getContent().stream()
                    .map(medicine -> {
                        Map<String, Object> simple = new HashMap<>();
                        simple.put("id", medicine.getId());
                        simple.put("medicineCode", medicine.getMedicineCode());
                        simple.put("name", medicine.getName());
                        simple.put("specification", medicine.getSpecification());
                        simple.put("unit", medicine.getUnit());
                        simple.put("retailPrice", medicine.getRetailPrice());
                        return simple;
                    })
                    .toList();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("currentPage", medicinePage.getNumber());
            response.put("totalItems", medicinePage.getTotalElements());
            response.put("totalPages", medicinePage.getTotalPages());
            response.put("data", simpleList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据症状查询药品
     * GET /api/medicines/symptom/{symptomId}
     */
    @GetMapping("/symptom/{symptomId}")
    public ResponseEntity<Map<String, Object>> getMedicinesBySymptom(
            @PathVariable Integer symptomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            // 获取所有药品
            List<Medicine> allMedicines = medicineService.findAll();
            
            // 过滤出包含指定症状的药品
            List<Medicine> medicinesWithSymptom = allMedicines.stream()
                    .filter(medicine -> {
                        List<Symptom> symptoms = medicine.getSymptoms();
                        return symptoms != null && symptoms.stream()
                                .anyMatch(symptom -> symptom.getId().equals(symptomId));
                    })
                    .toList();

            // 分页处理
            int start = page * size;
            int end = Math.min(start + size, medicinesWithSymptom.size());
            List<Medicine> pageContent = medicinesWithSymptom.subList(
                    Math.min(start, medicinesWithSymptom.size()), 
                    end
            );

            Map<String, Object> response = new HashMap<>();
            Map<String, Object> pageData = new HashMap<>();
            pageData.put("content", pageContent.stream().map(this::createMedicineResponse).toList());
            pageData.put("currentPage", page);
            pageData.put("pageSize", size);
            pageData.put("totalItems", medicinesWithSymptom.size());
            pageData.put("totalPages", (int) Math.ceil((double) medicinesWithSymptom.size() / size));
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
     * 药品库存状态批量查询
     * GET /api/medicines/stock-status
     */
    @GetMapping("/stock-status")
    public ResponseEntity<Map<String, Object>> getMedicinesStockStatus(
            @RequestParam List<Long> medicineIds) {
        try {
            Map<Long, Map<String, Object>> stockStatusMap = new HashMap<>();
            
            for (Long medicineId : medicineIds) {
                Integer totalStock = stockService.getTotalStock(medicineId);
                Map<String, Object> status = new HashMap<>();
                status.put("totalStock", totalStock != null ? totalStock : 0);
                status.put("isLowStock", totalStock != null && totalStock < DEFAULT_WARNING_VALUE); 
                stockStatusMap.put(medicineId, status);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stockStatusMap);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 近效期药品查询
     * GET /api/medicines/expiry-warning
     */
    @GetMapping("/expiry-warning")
    public ResponseEntity<Map<String, Object>> getExpiringMedicines(
            @RequestParam(defaultValue = "30") int daysThreshold,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Medicine> expiringMedicines = medicineService.findExpiringMedicines(daysThreshold, pageable);

            Map<String, Object> response = new HashMap<>();
            Map<String, Object> pageData = new HashMap<>();
            pageData.put("content", expiringMedicines.getContent().stream().map(this::createMedicineResponse).toList());
            pageData.put("currentPage", page);
            pageData.put("pageSize", size);
            pageData.put("totalItems", expiringMedicines.getTotalElements());
            pageData.put("totalPages", expiringMedicines.getTotalPages());
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
     * 药品库存价值评估
     * GET /api/medicines/stock-value
     */
    @GetMapping("/stock-value")
    public ResponseEntity<Map<String, Object>> getMedicineStockValue() {
        try {
            Double totalStockValue = stockService.calculateTotalStockValue();
            Map<String, Object> stockValueByCategory = stockService.getStockValueByCategory();

            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalStockValue", totalStockValue != null ? totalStockValue : 0);
            statistics.put("stockValueByCategory", stockValueByCategory);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", statistics);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 创建药品响应对象
     */
    private Map<String, Object> createMedicineResponse(Medicine medicine) {
        Map<String, Object> medicineResponse = new HashMap<>();
        medicineResponse.put("id", medicine.getId());
        medicineResponse.put("medicineCode", medicine.getMedicineCode());
        medicineResponse.put("name", medicine.getName());
        medicineResponse.put("genericName", medicine.getGenericName());

        // 分类信息
        if (medicine.getCategory() != null) {
            Map<String, Object> categoryInfo = new HashMap<>();
            categoryInfo.put("id", medicine.getCategory().getId());
            categoryInfo.put("name", medicine.getCategory().getName());
            medicineResponse.put("category", categoryInfo);
        }

        medicineResponse.put("specification", medicine.getSpecification());
        medicineResponse.put("unit", medicine.getUnit());
        medicineResponse.put("manufacturer", medicine.getManufacturer());
        medicineResponse.put("retailPrice", medicine.getRetailPrice());
        medicineResponse.put("purchasePrice", medicine.getPurchasePrice());
        medicineResponse.put("status", medicine.getStatus());
        medicineResponse.put("createTime", medicine.getCreateTime());

        return medicineResponse;
    }
}