package com.example.demo.repository;

import com.example.demo.entity.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
    Integer sumPurchasedQuantityByMedicineId(@Param("medicineId") Long medicineId);

    // 统计某个时间段的采购总额
    @Query("SELECT COALESCE(SUM(po.totalAmount), 0.0) FROM PurchaseOrder po WHERE po.orderTime BETWEEN :startTime AND :endTime AND po.orderStatus = 2")
    Double sumTotalAmountByPeriod(@Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime);

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

}