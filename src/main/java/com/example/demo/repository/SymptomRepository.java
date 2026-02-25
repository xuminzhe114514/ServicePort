package com.example.demo.repository;

import com.example.demo.entity.Symptom;
import com.example.demo.entity.Medicine;
import com.example.demo.entity.SaleRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface SymptomRepository extends JpaRepository<Symptom, Integer> {

    //根据名称精确查找症状
    Optional<Symptom> findByName(String name);

    //根据名称模糊查询
    List<Symptom> findByNameContaining(String name);

    //检查症状名称是否存在
    boolean existsByName(String name);

    //根据描述模糊查询
    List<Symptom> findByDescriptionContaining(String description);

    //搜索症状（名称或描述模糊匹配）
    @Query("SELECT s FROM Symptom s WHERE " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Symptom> searchSymptoms(@Param("keyword") String keyword);

    // 根据症状ID查询关联的药品
    @Query("SELECT m FROM Medicine m JOIN m.symptoms s WHERE s.id = :symptomId")
    List<Medicine> findMedicinesBySymptomId(@Param("symptomId") Integer symptomId);

    // 根据症状名称查询关联的药品
    @Query("SELECT m FROM Medicine m JOIN m.symptoms s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :symptomName, '%'))")
    List<Medicine> findMedicinesBySymptomName(@Param("symptomName") String symptomName);

    // 根据症状ID查询关联的销售记录
    @Query("SELECT sr FROM SaleRecord sr JOIN sr.symptom s WHERE s.id = :symptomId")
    List<SaleRecord> findSaleRecordsBySymptomId(@Param("symptomId") Integer symptomId);

    // 根据症状名称查询关联的销售记录
    @Query("SELECT sr FROM SaleRecord sr JOIN sr.symptom s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :symptomName, '%'))")
    List<SaleRecord> findSaleRecordsBySymptomName(@Param("symptomName") String symptomName);

    // 统计症状关联的药品数量
    @Query("SELECT COUNT(DISTINCT m) FROM Medicine m JOIN m.symptoms s WHERE s.id = :symptomId")
    long countMedicinesBySymptomId(@Param("symptomId") Integer symptomId);

    // 统计症状关联的销售记录数量
    @Query("SELECT COUNT(sr) FROM SaleRecord sr JOIN sr.symptom s WHERE s.id = :symptomId")
    long countSaleRecordsBySymptomId(@Param("symptomId") Integer symptomId);

    // 统计症状关联的销售总额
    @Query("SELECT COALESCE(SUM(sr.totalAmount), CAST(0 AS BigDecimal)) FROM SaleRecord sr JOIN sr.symptom s WHERE s.id = :symptomId")
    BigDecimal sumSaleAmountBySymptomId(@Param("symptomId") Integer symptomId);
    
    // 查找指定时间范围内最常见的症状
    @Query("SELECT s.id, COUNT(DISTINCT sr.id) as usageCount FROM SaleRecord sr JOIN sr.symptom s WHERE sr.saleTime BETWEEN :start AND :end GROUP BY s.id ORDER BY usageCount DESC")
    List<Object[]> findMostCommonSymptoms(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);
}