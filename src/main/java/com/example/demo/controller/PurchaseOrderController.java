package com.example.demo.controller;

import com.example.demo.entity.PurchaseOrder;
import com.example.demo.service.PurchaseOrderService;
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
@RequestMapping("/purchase-orders")
@CrossOrigin(origins = "*")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "PurchaseOrderController is working!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取所有采购订单（分页），支持多条件查询
     * GET /api/purchase-orders
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPurchaseOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) Integer orderStatus,
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

            boolean hasFilter = (keyword != null && !keyword.trim().isEmpty()) ||
                    (startTime != null && !startTime.trim().isEmpty()) ||
                    (endTime != null && !endTime.trim().isEmpty()) ||
                    (orderStatus != null) ||
                    (medicineId != null);

            Page<PurchaseOrder> orderPage;

            if (hasFilter) {
                orderPage = purchaseOrderService.findByMultipleConditions(keyword, startDateTime, endDateTime, orderStatus, medicineId, pageable);
            } else {
                orderPage = purchaseOrderService.findAll(pageable);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("currentPage", orderPage.getNumber());
            response.put("totalItems", orderPage.getTotalElements());
            response.put("totalPages", orderPage.getTotalPages());

            List<Map<String, Object>> orderList = orderPage.getContent().stream()
                    .map(this::createOrderResponse)
                    .collect(Collectors.toList());
            response.put("data", orderList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取采购订单失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据ID获取采购订单
     * GET /api/purchase-orders/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPurchaseOrderById(@PathVariable Long id) {
        try {
            PurchaseOrder order = purchaseOrderService.findById(id);

            if (order != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", createOrderResponse(order));
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "采购订单不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取采购订单失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据订单号获取采购订单
     * GET /api/purchase-orders/orderNo/{orderNo}
     */
    @GetMapping("/orderNo/{orderNo}")
    public ResponseEntity<Map<String, Object>> getPurchaseOrderByOrderNo(@PathVariable String orderNo) {
        try {
            PurchaseOrder order = purchaseOrderService.findByOrderNo(orderNo);

            if (order != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", createOrderResponse(order));
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "采购订单不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取采购订单失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 创建新采购订单
     * POST /api/purchase-orders
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPurchaseOrder(
            @RequestBody PurchaseOrder order,
            @RequestParam Long operatorId) {
        try {
            if (order.getOrderNo() != null) {
                PurchaseOrder existingOrder = purchaseOrderService.findByOrderNo(order.getOrderNo());
                if (existingOrder != null) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "订单号已存在");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
            }

            PurchaseOrder savedOrder = purchaseOrderService.createOrder(order, operatorId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "采购订单创建成功");
            response.put("data", createOrderResponse(savedOrder));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "创建失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 更新采购订单信息
     * PUT /api/purchase-orders/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePurchaseOrder(
            @PathVariable Long id,
            @RequestBody PurchaseOrder order) {
        try {
            PurchaseOrder existingOrder = purchaseOrderService.findById(id);

            if (existingOrder == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "采购订单不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (order.getOrderNo() != null && !order.getOrderNo().equals(existingOrder.getOrderNo())) {
                PurchaseOrder duplicateOrder = purchaseOrderService.findByOrderNo(order.getOrderNo());
                if (duplicateOrder != null) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "订单号已存在");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
            }

            // 更新订单信息
            existingOrder.setOrderNo(order.getOrderNo());
            existingOrder.setMedicine(order.getMedicine());
            existingOrder.setQuantity(order.getQuantity());
            existingOrder.setUnitPrice(order.getUnitPrice());
            existingOrder.setSupplier(order.getSupplier());
            existingOrder.setOrderStatus(order.getOrderStatus());
            existingOrder.setOrderTime(order.getOrderTime());
            existingOrder.setExpectedArrival(order.getExpectedArrival());
            existingOrder.setActualArrival(order.getActualArrival());
            existingOrder.setOperator(order.getOperator());
            existingOrder.setRemark(order.getRemark());

            // 重新计算总金额
            if (existingOrder.getQuantity() != null && existingOrder.getUnitPrice() != null) {
                existingOrder.calculateTotalAmount();
            }

            PurchaseOrder updatedOrder = purchaseOrderService.update(existingOrder);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "采购订单更新成功");
            response.put("data", createOrderResponse(updatedOrder));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 删除采购订单
     * DELETE /api/purchase-orders/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePurchaseOrder(@PathVariable Long id) {
        try {
            PurchaseOrder existingOrder = purchaseOrderService.findById(id);

            if (existingOrder == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "采购订单不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // 检查订单状态，已到货的订单不能删除
            if (existingOrder.isArrived()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "已到货的订单不能删除");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            purchaseOrderService.delete(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "采购订单删除成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "删除采购订单失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据订单状态分页查询采购订单
     * GET /api/purchase-orders/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getPurchaseOrdersByStatus(
            @PathVariable Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            if (status < 0 || status > 3) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "订单状态值无效，应为0-3");
                return ResponseEntity.badRequest().body(response);
            }

            Pageable pageable = PageRequest.of(page, size, Sort.by("orderTime").descending());

            Page<PurchaseOrder> allOrders = purchaseOrderService.findByOrderStatus(status, pageable);
            if (allOrders.getContent().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", List.of());
                response.put("currentPage", page);
                response.put("totalItems", allOrders.getTotalElements());
                response.put("totalPages", allOrders.getTotalPages());
                return ResponseEntity.ok(response);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);

            Map<String, Object> pageData = new HashMap<>();
            pageData.put("content", allOrders.getContent().stream().map(this::createOrderResponse).collect(Collectors.toList()));
            pageData.put("currentPage", page);
            pageData.put("pageSize", size);
            pageData.put("totalItems", allOrders.getTotalElements());
            pageData.put("totalPages", allOrders.getTotalPages());
            pageData.put("isFirst", allOrders.isFirst());
            pageData.put("isLast", allOrders.isLast());

            response.put("data", pageData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取采购订单失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据药品ID分页查询采购订单
     * GET /api/purchase-orders/medicine/{medicineId}
     */
    @GetMapping("/medicine/{medicineId}")
    public ResponseEntity<Map<String, Object>> getPurchaseOrdersByMedicineId(
            @PathVariable Long medicineId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("orderTime").descending());

            Page<PurchaseOrder> allOrders = purchaseOrderService.findByMedicineId(medicineId, pageable);
            if (allOrders.getContent().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", List.of());
                response.put("currentPage", page);
                response.put("totalItems", allOrders.getTotalElements());
                response.put("totalPages", allOrders.getTotalPages());
                return ResponseEntity.ok(response);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);

            Map<String, Object> pageData = new HashMap<>();
            pageData.put("content", allOrders.getContent().stream().map(this::createOrderResponse).collect(Collectors.toList()));
            pageData.put("currentPage", page);
            pageData.put("pageSize", size);
            pageData.put("totalItems", allOrders.getTotalElements());
            pageData.put("totalPages", allOrders.getTotalPages());
            pageData.put("isFirst", allOrders.isFirst());
            pageData.put("isLast", allOrders.isLast());

            response.put("data", pageData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取采购订单失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取待处理的采购订单
     * GET /api/purchase-orders/pending
     */
    @GetMapping("/pending")
    public ResponseEntity<Map<String, Object>> getPendingOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<PurchaseOrder> allOrders = purchaseOrderService.findPendingOrders(pageable);

            if (allOrders.getContent().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", List.of());
                response.put("currentPage", page);
                response.put("totalItems", allOrders.getTotalElements());
                response.put("totalPages", allOrders.getTotalPages());
                return ResponseEntity.ok(response);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);

            Map<String, Object> pageData = new HashMap<>();
            pageData.put("content", allOrders.getContent().stream().map(this::createOrderResponse).collect(Collectors.toList()));
            pageData.put("currentPage", page);
            pageData.put("pageSize", size);
            pageData.put("totalItems", allOrders.getTotalElements());
            pageData.put("totalPages", allOrders.getTotalPages());
            pageData.put("isFirst", allOrders.isFirst());
            pageData.put("isLast", allOrders.isLast());

            response.put("data", pageData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取待处理订单失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取过期的采购订单（预计到货日期已过但未到货）
     * GET /api/purchase-orders/overdue
     */
    @GetMapping("/overdue")
    public ResponseEntity<Map<String, Object>> getOverdueOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<PurchaseOrder> allOrders = purchaseOrderService.findOverdueOrders(pageable);

            if (allOrders.getContent().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", List.of());
                response.put("currentPage", page);
                response.put("totalItems", allOrders.getTotalElements());
                response.put("totalPages", allOrders.getTotalPages());
                return ResponseEntity.ok(response);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);

            Map<String, Object> pageData = new HashMap<>();
            pageData.put("content", allOrders.getContent().stream().map(this::createOrderResponse).collect(Collectors.toList()));
            pageData.put("currentPage", page);
            pageData.put("pageSize", size);
            pageData.put("totalItems", allOrders.getTotalElements());
            pageData.put("totalPages", allOrders.getTotalPages());
            pageData.put("isFirst", allOrders.isFirst());
            pageData.put("isLast", allOrders.isLast());

            response.put("data", pageData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取过期订单失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取某个药品的已到货采购总量
     * GET /api/purchase-orders/medicine/{medicineId}/total-purchased
     */
    @GetMapping("/medicine/{medicineId}/total-purchased")
    public ResponseEntity<Map<String, Object>> getTotalPurchasedQuantity(@PathVariable Long medicineId) {
        try {
            Integer totalQuantity = purchaseOrderService.getTotalPurchasedQuantity(medicineId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                    "medicineId", medicineId,
                    "totalPurchasedQuantity", totalQuantity != null ? totalQuantity : 0
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取采购总量失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取时间段内的已到货采购总额
     * GET /api/purchase-orders/total-amount
     */
    @GetMapping("/total-amount")
    public ResponseEntity<Map<String, Object>> getTotalPurchaseAmountByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            Double totalAmount = purchaseOrderService.getTotalPurchaseAmountByPeriod(startTime, endTime);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                    "startTime", startTime,
                    "endTime", endTime,
                    "totalAmount", totalAmount != null ? totalAmount : 0.0
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取采购总额失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 确认采购订单
     * PUT /api/purchase-orders/{id}/confirm
     */
    @PutMapping("/{id}/confirm")
    public ResponseEntity<Map<String, Object>> confirmOrder(@PathVariable Long id) {
        try {
            PurchaseOrder confirmedOrder = purchaseOrderService.confirmOrder(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "采购订单确认成功");
            response.put("data", createOrderResponse(confirmedOrder));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "确认失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * 标记采购订单为已到货
     * PUT /api/purchase-orders/{id}/arrive
     */
    @PutMapping("/{id}/arrive")
    public ResponseEntity<Map<String, Object>> markAsArrived(@PathVariable Long id) {
        try {
            PurchaseOrder arrivedOrder = purchaseOrderService.markAsArrived(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "采购订单已标记为已到货");
            response.put("data", createOrderResponse(arrivedOrder));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "标记失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * 取消采购订单
     * PUT /api/purchase-orders/{id}/cancel
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable Long id) {
        try {
            PurchaseOrder cancelledOrder = purchaseOrderService.cancelOrder(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "采购订单已取消");
            response.put("data", createOrderResponse(cancelledOrder));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "取消失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * 获取采购订单统计信息
     * GET /api/purchase-orders/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getOrderStatistics() {
        Map<String, Object> statistics = purchaseOrderService.getOrderStatistics();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", statistics);

        return ResponseEntity.ok(response);
    }

    /**
     * 批量删除采购订单
     * DELETE /api/purchase-orders/batch
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> deletePurchaseOrders(@RequestBody List<Long> ids) {
        try {
            for (Long id : ids) {
                if (!purchaseOrderService.exists(id)) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "ID为 " + id + " 的采购订单不存在");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            }

            purchaseOrderService.deleteAll(ids);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "批量删除成功，共删除 " + ids.size() + " 个采购订单");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "批量删除失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 批量保存采购订单
     * POST /api/purchase-orders/batch
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> createPurchaseOrders(
            @RequestBody List<PurchaseOrder> orders,
            @RequestParam Long operatorId) {

        for (PurchaseOrder order : orders) {
            if (order.getOrderNo() != null) {
                PurchaseOrder existingOrder = purchaseOrderService.findByOrderNo(order.getOrderNo());
                if (existingOrder != null) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "订单号 '" + order.getOrderNo() + "' 已存在");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
            }
        }
        // 批量创建订单
        for (PurchaseOrder order : orders) {
            if (order.getOrderStatus() == null) {
                order.setOrderStatus(0); // 默认待处理状态
            }
        }
        List<PurchaseOrder> savedOrders;
        try {
            for (PurchaseOrder order : orders) {
                purchaseOrderService.createOrder(order, operatorId);
            }
            savedOrders = orders;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "批量创建失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "批量创建成功，共创建 " + savedOrders.size() + " 个采购订单");
        response.put("data", savedOrders.stream().map(this::createOrderResponse).collect(Collectors.toList()));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 搜索采购订单（供应商、订单号等）
     * GET /api/purchase-orders/search
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchPurchaseOrders(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (keyword == null || keyword.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "关键词不能为空");
            response.put("data", List.of());
            return ResponseEntity.badRequest().body(response);
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<PurchaseOrder> searchOrders = purchaseOrderService.searchOrders(keyword,pageable);

        if (searchOrders.getContent().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "未找到符合条件的采购订单");
            response.put("data", List.of());
            return ResponseEntity.ok(response);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "已找到符合条件的采购订单");

        Map<String, Object> pageData = new HashMap<>();
        pageData.put("content", searchOrders.getContent().stream().map(this::createOrderResponse).collect(Collectors.toList()));
        pageData.put("currentPage", page);
        pageData.put("pageSize", size);
        pageData.put("totalItems", searchOrders.getTotalElements());
        pageData.put("totalPages", searchOrders.getTotalPages());
        pageData.put("isFirst", searchOrders.isFirst());
        pageData.put("isLast", searchOrders.isLast());

        response.put("data", pageData);
      

        return ResponseEntity.ok(response);
    }

    /**
     * 根据时间段查询采购订单
     * GET /api/purchase-orders/time-range
     */
    @GetMapping("/time-range")
    public ResponseEntity<Map<String, Object>> getPurchaseOrdersByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

            if (startTime.isAfter(endTime)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "开始时间不能晚于结束时间");
                response.put("data", List.of());
                return ResponseEntity.badRequest().body(response);
            }

        Pageable pageable = PageRequest.of(page, size);
        Page<PurchaseOrder> orders = purchaseOrderService.findByOrderTimeBetween(startTime, endTime, pageable);

        if (orders.getContent().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "未找到符合条件的采购订单");
            response.put("data", List.of());
            return ResponseEntity.ok(response);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "已找到符合条件的采购订单");
        
        Map<String, Object> pageData = new HashMap<>();
        pageData.put("content", orders.getContent().stream().map(this::createOrderResponse).collect(Collectors.toList()));
        pageData.put("currentPage", page);
        pageData.put("pageSize", size);
        pageData.put("totalItems", orders.getTotalElements());
        pageData.put("totalPages", orders.getTotalPages());
        pageData.put("isFirst", orders.isFirst());
        pageData.put("isLast", orders.isLast());
        
        response.put("data", pageData);

        return ResponseEntity.ok(response);
    }

    /**
     * 创建采购订单响应对象
     */
    private Map<String, Object> createOrderResponse(PurchaseOrder order) {
        Map<String, Object> orderResponse = new HashMap<>();
        orderResponse.put("id", order.getId());
        orderResponse.put("orderNo", order.getOrderNo());

        if (order.getMedicine() != null) {
            Map<String, Object> medicineInfo = new HashMap<>();
            medicineInfo.put("id", order.getMedicine().getId());
            medicineInfo.put("medicineCode", order.getMedicine().getMedicineCode());
            medicineInfo.put("name", order.getMedicine().getName());
            medicineInfo.put("specification", order.getMedicine().getSpecification());
            medicineInfo.put("unit", order.getMedicine().getUnit());
            orderResponse.put("medicine", medicineInfo);
        }

        orderResponse.put("quantity", order.getQuantity());
        orderResponse.put("unitPrice", order.getUnitPrice());
        orderResponse.put("totalAmount", order.getTotalAmount());
        orderResponse.put("supplier", order.getSupplier());
        orderResponse.put("orderStatus", order.getOrderStatus());
        orderResponse.put("orderTime", order.getOrderTime());
        orderResponse.put("expectedArrival", order.getExpectedArrival());
        orderResponse.put("actualArrival", order.getActualArrival());

        if (order.getOperator() != null) {
            Map<String, Object> operatorInfo = new HashMap<>();
            operatorInfo.put("id", order.getOperator().getId());
            operatorInfo.put("username", order.getOperator().getUsername());
            operatorInfo.put("realName", order.getOperator().getRealName());
            orderResponse.put("operator", operatorInfo);
        }

        orderResponse.put("remark", order.getRemark());
        String[] statusTexts = {"待处理", "已确认", "已到货", "已取消"};
        if (order.getOrderStatus() >= 0 && order.getOrderStatus() < statusTexts.length) {
            orderResponse.put("orderStatusText", statusTexts[order.getOrderStatus()]);
        }
        if (order.getExpectedArrival() != null && order.getOrderStatus() < 2) {
            boolean isOverdue = order.getExpectedArrival().isBefore(LocalDate.now());
            orderResponse.put("isOverdue", isOverdue);
        }
        return orderResponse;
    }
}