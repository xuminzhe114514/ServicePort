package com.example.demo.service.impl;

import com.example.demo.entity.PurchaseOrder;
import com.example.demo.entity.User;
import com.example.demo.repository.PurchaseOrderRepository;
import com.example.demo.service.PurchaseOrderService;
import com.example.demo.service.UserService;
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

    @Override
    public PurchaseOrder findByOrderNo(String orderNo) {
        return repository.findByOrderNo(orderNo).orElse(null);
    }

    @Override
    public Page<PurchaseOrder> findAll(Pageable pageable) {
        return repository.findAll(pageable);
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

        return new PageImpl<>(pageOrders, pageable, total);
    }

    @Override
    public Integer getTotalPurchasedQuantity(Long medicineId) {
        Integer total = repository.sumPurchasedQuantityByMedicineId(medicineId);
        return total != null ? total : 0;
    }

    @Override
    public Double getTotalPurchaseAmountByPeriod(LocalDateTime startTime, LocalDateTime endTime) {
        Double total = repository.sumTotalAmountByPeriod(startTime, endTime);
        return total != null ? total : 0.0;
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

        return repository.save(order);
    }

    @Override
    public PurchaseOrder confirmOrder(Long orderId) {
        PurchaseOrder order = findById(orderId);
        if (order != null && order.isPending()) {
            order.setOrderStatus(1);
            return repository.save(order);
        }
        return null;
    }

    @Override
    public PurchaseOrder markAsArrived(Long orderId) {
        PurchaseOrder order = findById(orderId);
        if (order != null && (order.isPending() || order.isConfirmed())) {
            order.setOrderStatus(2);
            order.setActualArrival(LocalDateTime.now());
            return repository.save(order);
        }
        return null;
    }

    @Override
    public PurchaseOrder cancelOrder(Long orderId) {
        PurchaseOrder order = findById(orderId);
        if (order != null && !order.isArrived() && !order.isCancelled()) {
            order.setOrderStatus(3);
            return repository.save(order);
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
}