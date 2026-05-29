package com.example.demo.repository;

import com.example.demo.entity.SaleRecord;
import com.example.demo.entity.Medicine;
import com.example.demo.entity.User;
import com.example.demo.entity.Symptom;
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
public interface SaleRecordRepository extends JpaRepository<SaleRecord, Long> {

    // 根据销售单号查找
    Optional<SaleRecord> findByRecordNo(String recordNo);

    // 根据药品ID查找销售记录（使用关联对象）
    @Query("SELECT sr FROM SaleRecord sr WHERE sr.medicine.id = :medicineId")
    List<SaleRecord> findByMedicineId(@Param("medicineId") Long medicineId);

    // 根据药品ID和时间段查找销售记录（用于准确率计算）
    @Query("SELECT sr FROM SaleRecord sr WHERE sr.medicine.id = :medicineId AND sr.saleTime BETWEEN :startTime AND :endTime")
    List<SaleRecord> findByMedicineIdAndSaleTimeBetween(
        @Param("medicineId") Long medicineId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

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
    Long sumQuantityByMedicineId(@Param("medicineId") Long medicineId);

    // 统计某个时间段的销售总额 - 添加COALESCE处理null
    @Query("SELECT COALESCE(SUM(sr.totalAmount), CAST(0 AS BigDecimal)) FROM SaleRecord sr WHERE sr.saleTime BETWEEN :startTime AND :endTime")
    BigDecimal sumTotalAmountByPeriod(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    // 统计每天的销售数据（用于需求预测）
    @Query(value = "SELECT CAST(sr.sale_time AS DATE), sr.medicine_id, SUM(sr.quantity) " +
            "FROM sale_record sr " +
            "WHERE sr.sale_time BETWEEN :startDate AND :endDate " +
            "GROUP BY CAST(sr.sale_time AS DATE), sr.medicine_id " +
            "ORDER BY CAST(sr.sale_time AS DATE)", nativeQuery = true)
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

    // 根据销售记录ID查询关联的药品详情
    @Query("SELECT sr.medicine FROM SaleRecord sr WHERE sr.id = :saleRecordId")
    Medicine findMedicineBySaleRecordId(@Param("saleRecordId") Long saleRecordId);

    // 根据销售记录ID查询关联的操作员详情
    @Query("SELECT sr.operator FROM SaleRecord sr WHERE sr.id = :saleRecordId")
    User findOperatorBySaleRecordId(@Param("saleRecordId") Long saleRecordId);

    // 根据销售记录ID查询关联的症状列表
    @Query("SELECT sr.symptom FROM SaleRecord sr WHERE sr.id = :saleRecordId")
    List<Symptom> findSymptomsBySaleRecordId(@Param("saleRecordId") Long saleRecordId);

    // 根据操作员ID查询销售记录（带分页）
    @Query("SELECT sr FROM SaleRecord sr WHERE sr.operator.id = :operatorId ORDER BY sr.saleTime DESC")
    Page<SaleRecord> findByOperatorIdWithPagination(@Param("operatorId") Long operatorId, Pageable pageable);

    // 根据顾客类型查询销售记录
    @Query("SELECT sr FROM SaleRecord sr WHERE sr.customerType = :customerType ORDER BY sr.saleTime DESC")
    List<SaleRecord> findByCustomerType(@Param("customerType") Integer customerType);

    // 统计操作员的销售业绩
    @Query("SELECT sr.operator.id, sr.operator.realName, COUNT(sr) as recordCount, SUM(sr.totalAmount) as totalAmount " +
            "FROM SaleRecord sr WHERE sr.saleTime BETWEEN :startDate AND :endDate " +
            "GROUP BY sr.operator.id, sr.operator.realName " +
            "ORDER BY totalAmount DESC")
    List<Object[]> findOperatorSalesPerformance(@Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    // 统计每天的销售总额
    @Query(value = "SELECT CAST(sr.sale_time AS DATE), SUM(sr.total_amount) as dailyAmount " +
            "FROM sale_record sr WHERE sr.sale_time BETWEEN :startDate AND :endDate " +
            "GROUP BY CAST(sr.sale_time AS DATE) " +
            "ORDER BY CAST(sr.sale_time AS DATE)", nativeQuery = true)
    List<Object[]> findDailySalesAmount(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    // 查询销售额最高的前N个药品
    @Query("SELECT m.id, m.name, SUM(sr.totalAmount) as totalSales " +
            "FROM SaleRecord sr JOIN sr.medicine m " +
            "WHERE sr.saleTime BETWEEN :startDate AND :endDate " +
            "GROUP BY m.id, m.name " +
            "ORDER BY totalSales DESC")
    List<Object[]> findTopSellingMedicinesByAmount(@Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate,
                                                  Pageable pageable);

    // 根据销售单号或药品名称模糊搜索
    @Query("SELECT sr FROM SaleRecord sr JOIN sr.medicine m WHERE " +
           "(LOWER(sr.recordNo) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<SaleRecord> searchByKeyword(@Param("keyword") String keyword);

    // 多条件联立查询
    @Query("SELECT sr FROM SaleRecord sr JOIN sr.medicine m LEFT JOIN sr.symptom s WHERE " +
           "(:keyword IS NULL OR LOWER(sr.recordNo) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:startTime IS NULL OR sr.saleTime >= :startTime) AND " +
           "(:endTime IS NULL OR sr.saleTime <= :endTime) AND " +
           "(:operatorId IS NULL OR sr.operator.id = :operatorId) AND " +
           "(:symptomId IS NULL OR s.id = :symptomId) AND " +
           "(:medicineId IS NULL OR m.id = :medicineId)")
    List<SaleRecord> findByMultipleConditions(
            @Param("keyword") String keyword,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("operatorId") Long operatorId,
            @Param("symptomId") Integer symptomId,
            @Param("medicineId") Long medicineId);
}