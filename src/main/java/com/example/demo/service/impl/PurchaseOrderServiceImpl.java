package com.example.demo.service.impl;

import com.example.demo.entity.PurchaseOrder;
import com.example.demo.entity.User;
import com.example.demo.entity.Medicine;
import com.example.demo.entity.SaleRecord;
import com.example.demo.entity.Stock;
import com.example.demo.repository.PurchaseOrderRepository;
import com.example.demo.repository.MedicineRepository;
import com.example.demo.repository.SaleRecordRepository;
import com.example.demo.repository.StockRepository;
import com.example.demo.service.PurchaseOrderService;
import com.example.demo.service.UserService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PurchaseOrderServiceImpl extends BaseServiceImpl<PurchaseOrder, Long, PurchaseOrderRepository>
        implements PurchaseOrderService {

    protected PurchaseOrderServiceImpl(PurchaseOrderRepository repository) {
        super(repository);
    }

    @Autowired
    private UserService userService;
    
    @Autowired
    private MedicineRepository medicineRepository;
    
    @Autowired
    private SaleRecordRepository saleRepository;
    
    @Autowired
    private StockRepository stockRepository;

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrder findByOrderNo(String orderNo) {
        PurchaseOrder order = repository.findByOrderNo(orderNo).orElse(null);
        if (order != null) {
            Hibernate.initialize(order.getMedicine());
            Hibernate.initialize(order.getOperator());
        }
        return order;
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrder findById(Long id) {
        PurchaseOrder order = repository.findById(id).orElse(null);
        if (order != null) {
            Hibernate.initialize(order.getMedicine());
            Hibernate.initialize(order.getOperator());
        }
        return order;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrder> findAll() {
        List<PurchaseOrder> orders = repository.findAll();
        if (!orders.isEmpty()) {
            orders.forEach(order -> {
                Hibernate.initialize(order.getMedicine());
                Hibernate.initialize(order.getOperator());
            });
        }
        return orders;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseOrder> findAll(Pageable pageable) {
        Page<PurchaseOrder> page = repository.findAll(pageable);
        if (page.hasContent()) {
            page.getContent().forEach(order -> {
                Hibernate.initialize(order.getMedicine());
                Hibernate.initialize(order.getOperator());
            });
        }
        return page;
    }

    @Override
    @Transactional
    public PurchaseOrder save(PurchaseOrder order) {
        PurchaseOrder savedOrder = repository.save(order);
        if (savedOrder != null) {
            Hibernate.initialize(savedOrder.getMedicine());
            Hibernate.initialize(savedOrder.getOperator());
        }
        return savedOrder;
    }

    @Override
    @Transactional
    public PurchaseOrder update(PurchaseOrder order) {
        PurchaseOrder updatedOrder = repository.save(order);
        if (updatedOrder != null) {
            Hibernate.initialize(updatedOrder.getMedicine());
            Hibernate.initialize(updatedOrder.getOperator());
        }
        return updatedOrder;
    }

    @Override
    @Transactional
    public List<PurchaseOrder> saveAll(List<PurchaseOrder> orders) {
        List<PurchaseOrder> savedOrders = repository.saveAll(orders);
        if (!savedOrders.isEmpty()) {
            savedOrders.forEach(order -> {
                Hibernate.initialize(order.getMedicine());
                Hibernate.initialize(order.getOperator());
            });
        }
        return savedOrders;
    }

    @Override
    public Page<PurchaseOrder> findByOrderStatus(Integer orderStatus, Pageable pageable) {
        List<PurchaseOrder> allOrders =  repository.findByOrderStatus(orderStatus);
        int total = allOrders.size();
        int start = (int)pageable.getOffset();
        int end = start + pageable.getPageSize();

        List<PurchaseOrder> pageOrders;
        if (start>=total) {
            pageOrders = new ArrayList<>();
        }else {
            pageOrders = allOrders.subList(start, Math.min(end,total));
        }

        if (!pageOrders.isEmpty()) {
            pageOrders.forEach(order -> {
                Hibernate.initialize(order.getMedicine());
                Hibernate.initialize(order.getOperator());
            });
        }

        return new PageImpl<>(pageOrders, pageable, total);
    }

    @Override
    public Page<PurchaseOrder> findByMedicineId(Long medicineId,Pageable pageable) {
        List<PurchaseOrder> ordersById =  repository.findByMedicineId(medicineId);
        int total = ordersById.size();
        int start = (int)pageable.getOffset();
        int end = start + pageable.getPageSize();

        List<PurchaseOrder> pageOrders;
        if (start>=total) {
            pageOrders = new ArrayList<>();
        }else {
            pageOrders = ordersById.subList(start, Math.min(end,total));
        }

        if (!pageOrders.isEmpty()) {
            pageOrders.forEach(order -> {
                Hibernate.initialize(order.getMedicine());
                Hibernate.initialize(order.getOperator());
            });
        }

        return new PageImpl<>(pageOrders, pageable, total);
    }

    @Override
    public Page<PurchaseOrder> findPendingOrders(Pageable pageable) {
        List<PurchaseOrder> pendingOrders =  repository.findPendingOrders();
        int total = pendingOrders.size();
        int start = (int)pageable.getOffset();
        int end = start + pageable.getPageSize();

        List<PurchaseOrder> pageOrders;
        if (start>=total) {
            pageOrders = new ArrayList<>();
        }else {
            pageOrders = pendingOrders.subList(start, Math.min(end,total));
        }

        if (!pageOrders.isEmpty()) {
            pageOrders.forEach(order -> {
                Hibernate.initialize(order.getMedicine());
                Hibernate.initialize(order.getOperator());
            });
        }

        return new PageImpl<>(pageOrders, pageable, total);
    }

    @Override
    public Page<PurchaseOrder> findOverdueOrders(Pageable pageable) {
        List<PurchaseOrder> overdueOrders =  repository.findOverdueOrders();
        int total = overdueOrders.size();
        int start = (int)pageable.getOffset();
        int end = start + pageable.getPageSize();

        List<PurchaseOrder> pageOrders;
        if (start>=total) {
            pageOrders = new ArrayList<>();
        }else {
            pageOrders = overdueOrders.subList(start, Math.min(end,total));
        }

        if (!pageOrders.isEmpty()) {
            pageOrders.forEach(order -> {
                Hibernate.initialize(order.getMedicine());
                Hibernate.initialize(order.getOperator());
            });
        }

        return new PageImpl<>(pageOrders, pageable, total);
    }

    @Override
    public Integer getTotalPurchasedQuantity(Long medicineId) {
        Object totalObj = repository.sumPurchasedQuantityByMedicineId(medicineId);
        Integer total = totalObj != null ? (totalObj instanceof Long ? ((Long)totalObj).intValue() : totalObj instanceof Integer ? (Integer)totalObj : 0) : 0;
        return total;
    }

    @Override
    public Double getTotalPurchaseAmountByPeriod(LocalDateTime startTime, LocalDateTime endTime) {
        Object totalObj = repository.sumTotalAmountByPeriod(startTime, endTime);
        Double total = totalObj != null ? (totalObj instanceof BigDecimal ? ((BigDecimal)totalObj).doubleValue() : totalObj instanceof Double ? (Double)totalObj : 0.0) : 0.0;
        return total;
    }

    @Override
    public PurchaseOrder createOrder(PurchaseOrder order, Long operatorId) {
        User operator = userService.findById(operatorId);
        if (operator != null) {
            order.setOperator(operator);
        }
        if (order.getOrderNo() == null) {
            String orderNo = "PO" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                    + String.format("%04d", (int)(Math.random() * 10000));
            order.setOrderNo(orderNo);
        }
        if (order.getUnitPrice() != null && order.getQuantity() != null) {
            order.calculateTotalAmount();
        }

        PurchaseOrder savedOrder = repository.save(order);
        if (savedOrder != null) {
            Hibernate.initialize(savedOrder.getMedicine());
            Hibernate.initialize(savedOrder.getOperator());
        }
        return savedOrder;
    }

    @Override
    public PurchaseOrder confirmOrder(Long orderId) {
        PurchaseOrder order = findById(orderId);
        if (order != null && order.isPending()) {
            order.setOrderStatus(1);
            PurchaseOrder savedOrder = repository.save(order);
            if (savedOrder != null) {
                Hibernate.initialize(savedOrder.getMedicine());
                Hibernate.initialize(savedOrder.getOperator());
            }
            return savedOrder;
        }
        return null;
    }

    @Override
    public PurchaseOrder markAsArrived(Long orderId) {
        PurchaseOrder order = findById(orderId);
        if (order != null && (order.isPending() || order.isConfirmed())) {
            order.setOrderStatus(2);
            order.setActualArrival(LocalDateTime.now());
            PurchaseOrder savedOrder = repository.save(order);
            if (savedOrder != null) {
                Hibernate.initialize(savedOrder.getMedicine());
                Hibernate.initialize(savedOrder.getOperator());
            }
            return savedOrder;
        }
        return null;
    }

    @Override
    public PurchaseOrder cancelOrder(Long orderId) {
        PurchaseOrder order = findById(orderId);
        if (order != null && !order.isArrived() && !order.isCancelled()) {
            order.setOrderStatus(3);
            PurchaseOrder savedOrder = repository.save(order);
            if (savedOrder != null) {
                Hibernate.initialize(savedOrder.getMedicine());
                Hibernate.initialize(savedOrder.getOperator());
            }
            return savedOrder;
        }
        return null;
    }

    @Override
    public Map<String, Object> getOrderStatistics() {
        Map<String, Object> stats = new HashMap<>();

        Pageable pageable = PageRequest.of(0, 1);

        Page<PurchaseOrder> pending = repository.findByOrderStatus(0,pageable);
        Page<PurchaseOrder> confirmed = repository.findByOrderStatus(1,pageable);
        Page<PurchaseOrder> arrived = repository.findByOrderStatus(2,pageable);
        Page<PurchaseOrder> cancelled = repository.findByOrderStatus(3,pageable);

        stats.put("pendingCount", pending.getTotalElements());
        stats.put("confirmedCount", confirmed.getTotalElements());
        stats.put("arrivedCount", arrived.getTotalElements());
        stats.put("cancelledCount", cancelled.getTotalElements());

        Double totalAmount = getTotalPurchaseAmountByPeriod(
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now()
        );
        stats.put("monthlyTotalAmount", totalAmount);

        Page<PurchaseOrder> overdue = findOverdueOrders(pageable);
        stats.put("overdueCount", overdue.getTotalElements());

        return stats;
    }

    @Override
    public Page<PurchaseOrder> searchOrders(String keyword, Pageable pageable) {
        List<PurchaseOrder> targetOrders = repository.findByKeywordContaining(keyword);

        int total = targetOrders.size();
        int start = (int)pageable.getOffset();
        int end = start + pageable.getPageSize();

        List<PurchaseOrder> orders;
        if (start>=total) {
            orders = new ArrayList<>();
        }else {
            orders = targetOrders.subList(start, Math.min(end,total));
        }

        if (!orders.isEmpty()) {
            orders.forEach(order -> {
                Hibernate.initialize(order.getMedicine());
                Hibernate.initialize(order.getOperator());
            });
        }

        return new PageImpl<>(orders, pageable, total);

    }

    @Override
    public Page<PurchaseOrder> findBySupplierContaining(String supplier, Pageable pageable) {
        List<PurchaseOrder> targetOrders = repository.findBySupplierContaining(supplier);

        int total = targetOrders.size();
        int start = (int)pageable.getOffset();
        int end = start + pageable.getPageSize();

        List<PurchaseOrder> orders;
        if (start>=total) {
            orders = new ArrayList<>();
        }else {
            orders = targetOrders.subList(start, Math.min(end,total));
        }

        if (!orders.isEmpty()) {
            orders.forEach(order -> {
                Hibernate.initialize(order.getMedicine());
                Hibernate.initialize(order.getOperator());
            });
        }

        return new PageImpl<>(orders, pageable, total);
    }

    @Override
    public Page<PurchaseOrder> findByOrderTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        List<PurchaseOrder> targetOrders = repository.findByOrderTimeBetween(startTime, endTime);

        int total = targetOrders.size();
        int start = (int)pageable.getOffset();
        int end = start + pageable.getPageSize();

        List<PurchaseOrder> orders;
        if (start>=total) {
            orders = new ArrayList<>();
        }else {
            orders = targetOrders.subList(start, Math.min(end,total));
        }

        if (!orders.isEmpty()) {
            orders.forEach(order -> {
                Hibernate.initialize(order.getMedicine());
                Hibernate.initialize(order.getOperator());
            });
        }

        return new PageImpl<>(orders, pageable, total);
    }

    @Override
    public Map<String, Long> countByStatus() {
        Map<String, Long> statusCountMap = new HashMap<>();

        for (int status = 0; status <= 3; status++) {
            List<PurchaseOrder> orders = repository.findByOrderStatus(status);
            statusCountMap.put(String.valueOf(status), (long) orders.size());
        }

        return statusCountMap;
    }

    @Override
    public Map<String, Object> getMonthlyStatistics(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();

        // 将LocalDate转换为LocalDateTime
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // 获取该时间段内的所有订单
        List<PurchaseOrder> allOrders = repository.findByOrderTimeBetween(startDateTime, endDateTime);

        // 按状态分类
        List<PurchaseOrder> pendingOrders = allOrders.stream()
                .filter(order -> order.getOrderStatus() == 0)
                .collect(Collectors.toList());

        List<PurchaseOrder> confirmedOrders = allOrders.stream()
                .filter(order -> order.getOrderStatus() == 1)
                .collect(Collectors.toList());

        List<PurchaseOrder> arrivedOrders = allOrders.stream()
                .filter(order -> order.getOrderStatus() == 2)
                .collect(Collectors.toList());

        List<PurchaseOrder> cancelledOrders = allOrders.stream()
                .filter(order -> order.getOrderStatus() == 3)
                .collect(Collectors.toList());

        // 计算统计数据
        // 1. 各状态订单数量
        Map<String, Long> orderCountByStatus = new HashMap<>();
        orderCountByStatus.put("pending", (long) pendingOrders.size());
        orderCountByStatus.put("confirmed", (long) confirmedOrders.size());
        orderCountByStatus.put("arrived", (long) arrivedOrders.size());
        orderCountByStatus.put("cancelled", (long) cancelledOrders.size());
        orderCountByStatus.put("total", (long) allOrders.size());

        // 2. 采购金额统计
        BigDecimal totalAmount = arrivedOrders.stream()
                .map(PurchaseOrder::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal confirmedAmount = confirmedOrders.stream()
                .map(PurchaseOrder::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. 按药品统计（前10个）
        Map<Long, Map<String, Object>> medicineStats = new HashMap<>();
        Map<Long, Long> medicineOrderCount = new HashMap<>();
        Map<Long, BigDecimal> medicineTotalAmount = new HashMap<>();

        for (PurchaseOrder order : arrivedOrders) {
            if (order.getMedicine() != null) {
                Long medicineId = order.getMedicine().getId();
                medicineOrderCount.merge(medicineId, 1L, Long::sum);

                if (order.getTotalAmount() != null) {
                    medicineTotalAmount.merge(medicineId, order.getTotalAmount(), BigDecimal::add);
                }
            }
        }

        // 转换为药品统计列表
        List<Map<String, Object>> topMedicines = medicineOrderCount.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(10)
                .map(entry -> {
                    Long medicineId = entry.getKey();
                    Map<String, Object> stat = new HashMap<>();
                    stat.put("medicineId", medicineId);
                    stat.put("orderCount", entry.getValue());
                    stat.put("totalAmount", medicineTotalAmount.getOrDefault(medicineId, BigDecimal.ZERO));
                    return stat;
                })
                .collect(Collectors.toList());

        // 4. 按供应商统计
        Map<String, Long> supplierOrderCount = new HashMap<>();
        Map<String, BigDecimal> supplierTotalAmount = new HashMap<>();

        for (PurchaseOrder order : arrivedOrders) {
            if (order.getSupplier() != null) {
                String supplier = order.getSupplier();
                supplierOrderCount.merge(supplier, 1L, Long::sum);

                if (order.getTotalAmount() != null) {
                    supplierTotalAmount.merge(supplier, order.getTotalAmount(), BigDecimal::add);
                }
            }
        }

        // 转换为供应商统计列表
        List<Map<String, Object>> topSuppliers = supplierOrderCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(entry -> {
                    String supplier = entry.getKey();
                    Map<String, Object> stat = new HashMap<>();
                    stat.put("supplier", supplier);
                    stat.put("orderCount", entry.getValue());
                    stat.put("totalAmount", supplierTotalAmount.getOrDefault(supplier, BigDecimal.ZERO));
                    return stat;
                })
                .collect(Collectors.toList());

        // 5. 按月份统计
        Map<String, Map<String, Object>> monthlyStats = new HashMap<>();

        for (PurchaseOrder order : allOrders) {
            if (order.getOrderTime() != null) {
                String monthKey = order.getOrderTime().format(DateTimeFormatter.ofPattern("yyyy-MM"));

                Map<String, Object> monthStat = monthlyStats.computeIfAbsent(monthKey, k -> {
                    Map<String, Object> stat = new HashMap<>();
                    stat.put("month", monthKey);
                    stat.put("orderCount", 0L);
                    stat.put("totalAmount", BigDecimal.ZERO);
                    return stat;
                });

                monthStat.put("orderCount", (Long) monthStat.get("orderCount") + 1);

                if (order.getTotalAmount() != null && order.getOrderStatus() == 2) {
                    BigDecimal currentAmount = (BigDecimal) monthStat.get("totalAmount");
                    monthStat.put("totalAmount", currentAmount.add(order.getTotalAmount()));
                }
            }
        }

        // 组装结果
        BigDecimal averageOrderAmount = allOrders.isEmpty() ? BigDecimal.ZERO :
                totalAmount.divide(BigDecimal.valueOf(arrivedOrders.isEmpty() ? 1 : arrivedOrders.size()),
                        2, RoundingMode.HALF_UP);
        result.put("dateRange", Map.of(
                "startDate", startDate,
                "endDate", endDate
        ));
        result.put("orderCountByStatus", orderCountByStatus);
        result.put("amountStatistics", Map.of(
                "totalAmount", totalAmount,
                "confirmedAmount", confirmedAmount,
                "averageOrderAmount", averageOrderAmount
        ));
        result.put("topMedicines", topMedicines);
        result.put("topSuppliers", topSuppliers);
        result.put("monthlyStatistics", new ArrayList<>(monthlyStats.values()));

        return result;
    }

    // 新增方法实现
    @Override
    public Page<Map<String, Object>> getSupplierPurchaseStatistics(Pageable pageable) {
        List<Object[]> results = repository.findSupplierPurchaseStatistics();
        List<Map<String, Object>> supplierStats = results.stream().map(result -> {
            Map<String, Object> stats = new HashMap<>();
            stats.put("supplier", result[0]);
            stats.put("orderCount", result[1]);
            stats.put("totalAmount", result[2]);
            return stats;
        }).collect(Collectors.toList());

        int total = supplierStats.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        List<Map<String, Object>> content;
        if (start >= total) {
            content = Collections.emptyList();
        } else {
            content = supplierStats.subList(start, end);
        }

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<PurchaseOrder> findUpcomingOrders(Pageable pageable) {
        List<PurchaseOrder> upcomingOrders = repository.findUpcomingOrders();
        int total = upcomingOrders.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        List<PurchaseOrder> content;
        if (start >= total) {
            content = Collections.emptyList();
        } else {
            content = upcomingOrders.subList(start, end);
        }

        if (!content.isEmpty()) {
            content.forEach(order -> {
                Hibernate.initialize(order.getMedicine());
                Hibernate.initialize(order.getOperator());
            });
        }

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public void batchConfirmOrders(List<Long> orderIds) {
        List<PurchaseOrder> orders = repository.findAllById(orderIds);
        orders.forEach(order -> {
            if (order != null && order.isPending()) {
                order.setOrderStatus(1);
                repository.save(order);
            }
        });
    }

    @Override
    public void batchCancelOrders(List<Long> orderIds) {
        List<PurchaseOrder> orders = repository.findAllById(orderIds);
        orders.forEach(order -> {
            if (order != null && !order.isArrived() && !order.isCancelled()) {
                order.setOrderStatus(3);
                repository.save(order);
            }
        });
    }

    @Deprecated
    @Override
    public Page<Map<String, Object>> getPurchaseSuggestions(Pageable pageable) {
        // 基于销售数据和库存水平生成采购建议
        List<Map<String, Object>> suggestions = new ArrayList<>();
        
        // 获取所有药品
        List<Medicine> allMedicines = medicineRepository.findByStatus(1);
        
        // 计算最近30天的销售数据
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(30);
        
        // 为每种药品生成采购建议
        for (Medicine medicine : allMedicines) {
            // 计算当前库存量
            Object currentStockObj = stockRepository.sumQuantityByMedicineId(medicine.getId());
            Integer currentStock = currentStockObj != null ? (currentStockObj instanceof Long ? ((Long)currentStockObj).intValue() : currentStockObj instanceof Integer ? (Integer)currentStockObj : 0) : 0;
            
            // 计算最近30天的平均日销售量
            List<SaleRecord> recentSales = saleRepository.findByMedicineId(medicine.getId());
            int totalRecentSales = recentSales.stream()
                    .filter(sale -> sale.getSaleTime().isAfter(startDate))
                    .mapToInt(SaleRecord::getQuantity)
                    .sum();
            double averageDailySales = recentSales.isEmpty() ? 0 : (double) totalRecentSales / 30;
            
            // 计算建议采购量（基于安全库存水平和销售预测）
            int safetyStock = 10; // 默认安全库存
            int suggestedOrderQuantity = 0;
            
            // 如果当前库存低于安全库存，生成采购建议
            if (currentStock < safetyStock) {
                // 建议采购量 = 安全库存 - 当前库存 + 预计30天销售量
                suggestedOrderQuantity = safetyStock - currentStock + (int) (averageDailySales * 30);
                
                // 确保采购量为正数
                if (suggestedOrderQuantity > 0) {
                    Map<String, Object> suggestion = new HashMap<>();
                    suggestion.put("medicineId", medicine.getId());
                    suggestion.put("medicineName", medicine.getName());
                    suggestion.put("currentStock", currentStock);
                    suggestion.put("safetyStock", safetyStock);
                    suggestion.put("averageDailySales", String.format("%.2f", averageDailySales));
                    suggestion.put("suggestedOrderQuantity", suggestedOrderQuantity);
                    suggestion.put("unitPrice", medicine.getPurchasePrice());
                    suggestions.add(suggestion);
                }
            }
        }

        int total = suggestions.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        List<Map<String, Object>> content;
        if (start >= total) {
            content = Collections.emptyList();
        } else {
            content = suggestions.subList(start, end);
        }

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Map<String, Object>> getPurchaseByCategory(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        List<PurchaseOrder> allOrders = repository.findByOrderTimeBetween(startDate, endDate);
        Map<String, Map<String, Object>> categoryPurchase = new HashMap<>();

        allOrders.forEach(order -> {
            if (order.getMedicine() != null && order.getMedicine().getCategory() != null) {
                String categoryName = order.getMedicine().getCategory().getName();
                categoryPurchase.computeIfAbsent(categoryName, k -> {
                    Map<String, Object> stats = new HashMap<>();
                    stats.put("totalQuantity", 0);
                    stats.put("totalAmount", 0.0);
                    return stats;
                });

                Map<String, Object> stats = categoryPurchase.get(categoryName);
                stats.put("totalQuantity", (Integer) stats.get("totalQuantity") + order.getQuantity());
                stats.put("totalAmount", (Double) stats.get("totalAmount") + order.getTotalAmount().doubleValue());
            }
        });

        List<Map<String, Object>> categoryStats = categoryPurchase.entrySet().stream()
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
    public Map<String, Object> getOrderDetailsWithMedicine(Long orderId) {
        PurchaseOrder order = repository.findById(orderId).orElse(null);
        Map<String, Object> details = new HashMap<>();
        
        if (order != null) {
            details.put("orderId", order.getId());
            details.put("orderNo", order.getOrderNo());
            details.put("orderStatus", order.getOrderStatus());
            details.put("orderTime", order.getOrderTime());
            details.put("expectedArrival", order.getExpectedArrival());
            details.put("actualArrival", order.getActualArrival());
            details.put("supplier", order.getSupplier());
            details.put("quantity", order.getQuantity());
            details.put("unitPrice", order.getUnitPrice());
            details.put("totalAmount", order.getTotalAmount());
            
            if (order.getMedicine() != null) {
                Map<String, Object> medicineInfo = new HashMap<>();
                medicineInfo.put("medicineId", order.getMedicine().getId());
                medicineInfo.put("medicineName", order.getMedicine().getName());
                medicineInfo.put("specification", order.getMedicine().getSpecification());
                medicineInfo.put("manufacturer", order.getMedicine().getManufacturer());
                details.put("medicine", medicineInfo);
            }
        }
        
        return details;
    }
}