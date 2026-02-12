package com.example.demo.repository;

import com.example.demo.entity.SaleRecord;
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
public interface SaleRecordRepository extends JpaRepository<SaleRecord, Long> {

    // 根据销售单号查找
    Optional<SaleRecord> findByRecordNo(String recordNo);

    // 根据药品ID查找销售记录（使用关联对象）
    @Query("SELECT sr FROM SaleRecord sr WHERE sr.medicine.id = :medicineId")
    List<SaleRecord> findByMedicineId(@Param("medicineId") Long medicineId);

    // 根据操作员ID查找销售记录（使用关联对象）
    @Query("SELECT sr FROM SaleRecord sr WHERE sr.operator.id = :operatorId")
    List<SaleRecord> findByOperatorId(@Param("operatorId") Long operatorId);

    // 根据时间段查找销售记录
    List<SaleRecord> findBySaleTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    // 分页查询销售记录
    Page<SaleRecord> findAll(Pageable pageable);

    // 根据时间段分页查询
    Page<SaleRecord> findBySaleTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    // 自定义查询：统计某个药品的销售总量 - 添加COALESCE处理null
    @Query("SELECT COALESCE(SUM(sr.quantity), 0) FROM SaleRecord sr WHERE sr.medicine.id = :medicineId")
    Integer sumQuantityByMedicineId(@Param("medicineId") Long medicineId);

    // 统计某个时间段的销售总额 - 添加COALESCE处理null
    @Query("SELECT COALESCE(SUM(sr.totalAmount), 0.0) FROM SaleRecord sr WHERE sr.saleTime BETWEEN :startTime AND :endTime")
    Double sumTotalAmountByPeriod(@Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime);

    // 统计每天的销售数据（用于需求预测）
    @Query("SELECT DATE(sr.saleTime), sr.medicine.id, SUM(sr.quantity) " +
            "FROM SaleRecord sr " +
            "WHERE sr.saleTime BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(sr.saleTime), sr.medicine.id " +
            "ORDER BY DATE(sr.saleTime)")
    List<Object[]> findDailySales(@Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate);

    // 查找最畅销的药品
    @Query("SELECT sr.medicine.id, SUM(sr.quantity) as totalQuantity " +
            "FROM SaleRecord sr " +
            "WHERE sr.saleTime BETWEEN :startDate AND :endDate " +
            "GROUP BY sr.medicine.id " +
            "ORDER BY totalQuantity DESC")
    List<Object[]> findTopSellingMedicines(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate,
                                           Pageable pageable);

    // 根据症状ID查找销售记录
    @Query("SELECT sr FROM SaleRecord sr JOIN sr.symptom s WHERE s.id = :symptomId")
    List<SaleRecord> findBySymptomId(@Param("symptomId") Integer symptomId);

    // 根据症状名称查找销售记录
    @Query("SELECT sr FROM SaleRecord sr JOIN sr.symptom s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :symptomName, '%'))")
    List<SaleRecord> findBySymptomName(@Param("symptomName") String symptomName);

    // 统计按症状分类的销售数据
    @Query("SELECT s.name, SUM(sr.quantity) as totalQuantity, SUM(sr.totalAmount) as totalAmount " +
            "FROM SaleRecord sr JOIN sr.symptom s " +
            "WHERE sr.saleTime BETWEEN :startDate AND :endDate " +
            "GROUP BY s.id, s.name " +
            "ORDER BY totalQuantity DESC")
    List<Object[]> findSalesBySymptom(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);
}