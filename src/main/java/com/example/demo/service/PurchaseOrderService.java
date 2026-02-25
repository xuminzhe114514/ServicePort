package com.example.demo.service;

import com.example.demo.entity.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
@Transactional
public interface PurchaseOrderService extends BaseService<PurchaseOrder, Long> {

    PurchaseOrder findByOrderNo(String orderNo);
    Page<PurchaseOrder> findAll(Pageable pageable);
    Page<PurchaseOrder> findByOrderStatus(Integer orderStatus, Pageable pageable);
    Page<PurchaseOrder> findByMedicineId(Long medicineId, Pageable pageable);
    Page<PurchaseOrder> findPendingOrders(Pageable pageable);
    Page<PurchaseOrder> findOverdueOrders(Pageable pageable);
    Page<PurchaseOrder> searchOrders(String keyword, Pageable pageable);
    Page<PurchaseOrder> findBySupplierContaining(String supplier, Pageable pageable);
    Page<PurchaseOrder> findByOrderTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    Integer getTotalPurchasedQuantity(Long medicineId);
    Double getTotalPurchaseAmountByPeriod(LocalDateTime startTime, LocalDateTime endTime);
    PurchaseOrder createOrder(PurchaseOrder order, Long operatorId);
    PurchaseOrder confirmOrder(Long orderId);
    PurchaseOrder markAsArrived(Long orderId);
    PurchaseOrder cancelOrder(Long orderId);
    Map<String, Object> getOrderStatistics();
    Map<String, Long> countByStatus();
    Map<String, Object> getMonthlyStatistics(LocalDate startDate, LocalDate endDate);
    
    Page<Map<String, Object>> getSupplierPurchaseStatistics(Pageable pageable);
    Page<PurchaseOrder> findUpcomingOrders(Pageable pageable);
    void batchConfirmOrders(List<Long> orderIds);
    void batchCancelOrders(List<Long> orderIds);
    Page<Map<String, Object>> getPurchaseSuggestions(Pageable pageable);
    Page<Map<String, Object>> getPurchaseByCategory(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    Map<String, Object> getOrderDetailsWithMedicine(Long orderId);

}