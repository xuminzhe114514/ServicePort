package com.example.demo.repository;

import com.example.demo.entity.PurchaseOrder;
import com.example.demo.entity.Medicine;
import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    // 根据订单号查找
    Optional<PurchaseOrder> findByOrderNo(String orderNo);

    // 根据药品ID查找采购订单
    @Query("SELECT po FROM PurchaseOrder po WHERE po.medicine.id = :medicineId")
    List<PurchaseOrder> findByMedicineId(@Param("medicineId") Long medicineId);

    // 根据订单状态查找
    List<PurchaseOrder> findByOrderStatus(Integer orderStatus);

    // 根据操作员ID查找
    @Query("SELECT po FROM PurchaseOrder po WHERE po.operator.id = :operatorId")
    List<PurchaseOrder> findByOperatorId(@Param("operatorId") Long operatorId);

    // 根据供应商查找
    List<PurchaseOrder> findBySupplierContaining(String supplier);

    // 根据下单时间范围查找
    List<PurchaseOrder> findByOrderTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    // 分页查询
    Page<PurchaseOrder> findAll(Pageable pageable);

    // 根据状态分页查询
    Page<PurchaseOrder> findByOrderStatus(Integer orderStatus, Pageable pageable);

    //统计某个药品的采购总量
    @Query("SELECT COALESCE(SUM(po.quantity), 0) FROM PurchaseOrder po WHERE po.medicine.id = :medicineId AND po.orderStatus = 2")
    Long sumPurchasedQuantityByMedicineId(@Param("medicineId") Long medicineId);

    // 统计某个时间段的采购总额
    @Query("SELECT COALESCE(SUM(po.totalAmount), CAST(0 AS BigDecimal)) FROM PurchaseOrder po WHERE po.orderTime BETWEEN :startTime AND :endTime AND po.orderStatus = 2")
    BigDecimal sumTotalAmountByPeriod(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    // 查找待处理的采购订单
    @Query("SELECT po FROM PurchaseOrder po WHERE po.orderStatus = 0 ORDER BY po.orderTime ASC")
    List<PurchaseOrder> findPendingOrders();

    // 查找过期的采购订单（预计到货日期已过但未到货）
    @Query("SELECT po FROM PurchaseOrder po WHERE po.expectedArrival < CURRENT_DATE AND po.orderStatus IN (0, 1) ORDER BY po.expectedArrival ASC")
    List<PurchaseOrder> findOverdueOrders();

    // 根据供应商或药品名称或订单编号进行模糊字段搜索
    @Query("SELECT po FROM PurchaseOrder po " +
            "LEFT JOIN po.medicine m " +
            "WHERE (:keyword IS NULL OR :keyword = '' OR " +
            "       LOWER(po.orderNo) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "       LOWER(po.supplier) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "       LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<PurchaseOrder> findByKeywordContaining(@Param("keyword") String keyword);

    // 根据采购订单ID查询关联的药品详情
    @Query("SELECT po.medicine FROM PurchaseOrder po WHERE po.id = :purchaseOrderId")
    Medicine findMedicineByPurchaseOrderId(@Param("purchaseOrderId") Long purchaseOrderId);

    // 根据采购订单ID查询关联的操作员详情
    @Query("SELECT po.operator FROM PurchaseOrder po WHERE po.id = :purchaseOrderId")
    User findOperatorByPurchaseOrderId(@Param("purchaseOrderId") Long purchaseOrderId);

    // 根据供应商查询采购订单（带分页）
    @Query("SELECT po FROM PurchaseOrder po WHERE po.supplier LIKE LOWER(CONCAT('%', :supplier, '%')) ORDER BY po.orderTime DESC")
    Page<PurchaseOrder> findBySupplierWithPagination(@Param("supplier") String supplier, Pageable pageable);

    // 统计供应商的采购总额
    @Query("SELECT po.supplier, COUNT(po) as orderCount, SUM(po.totalAmount) as totalAmount " +
            "FROM PurchaseOrder po WHERE po.orderStatus = 2 " +
            "GROUP BY po.supplier " +
            "ORDER BY totalAmount DESC")
    List<Object[]> findSupplierPurchaseStatistics();

    // 统计每个状态的采购订单数量
    @Query("SELECT po.orderStatus, COUNT(po) as orderCount " +
            "FROM PurchaseOrder po " +
            "GROUP BY po.orderStatus")
    List<Object[]> findOrderStatusStatistics();

    // 查询即将到期的采购订单（7天内）
    @Query(value = "SELECT * FROM purchase_order po WHERE po.expected_arrival BETWEEN CURRENT_DATE() AND DATE_ADD(CURRENT_DATE(), INTERVAL 7 DAY) AND po.order_status IN (0, 1)", nativeQuery = true)
    List<PurchaseOrder> findUpcomingOrders();

    // 统计某个时间段的采购订单数量和总金额
    @Query("SELECT COUNT(po) as orderCount, COALESCE(SUM(po.totalAmount), CAST(0 AS BigDecimal)) as totalAmount FROM PurchaseOrder po WHERE po.orderTime BETWEEN :startTime AND :endTime AND po.orderStatus = 2")
    Object[] countAndSumByPeriod(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}