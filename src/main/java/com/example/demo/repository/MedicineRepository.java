package com.example.demo.repository;

import com.example.demo.entity.Medicine;
import com.example.demo.entity.SaleRecord;
import com.example.demo.entity.PurchaseOrder;
import com.example.demo.entity.Stock;
import com.example.demo.entity.PredictionResult;
import com.example.demo.entity.Symptom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    // 根据药品编码查找
    Optional<Medicine> findByMedicineCode(String medicineCode);

    // 检查药品编码是否存在
    boolean existsByMedicineCode(String medicineCode);

    // 根据名称模糊查询
    List<Medicine> findByNameContaining(String name);

    // 根据分类查找药品
    @Query("SELECT m FROM Medicine m WHERE m.category.id = :categoryId")
    List<Medicine> findByCategoryId(@Param("categoryId") Long categoryId);

    // 根据状态查找药品
    List<Medicine> findByStatus(Integer status);

    // 根据生产厂家查找
    List<Medicine> findByManufacturerContaining(String manufacturer);

    // 分页查询
    Page<Medicine> findAll(Pageable pageable);

    // 分页查询（按状态）
    Page<Medicine> findByStatus(Integer status, Pageable pageable);

    // 多条件查询：按名称和分类
    @Query("SELECT m FROM Medicine m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')) AND m.category.id = :categoryId")
    List<Medicine> findByNameContainingAndCategoryId(@Param("name") String name, @Param("categoryId") Long categoryId);

    // 自定义查询：搜索药品（名称、通用名、生产厂家）
    @Query("SELECT m FROM Medicine m WHERE " +
            "(LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.genericName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.manufacturer) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "m.status = :status")
    List<Medicine> searchMedicines(@Param("keyword") String keyword, @Param("status") Integer status);

    // 统计药品数量
    @Query("SELECT COUNT(m) FROM Medicine m WHERE m.status = :status")
    long countByStatus(@Param("status") Integer status);

    // 获取所有药品的分类统计
    @Query("SELECT m.category.id, COUNT(m) FROM Medicine m WHERE m.status = 1 GROUP BY m.category.id")
    List<Object[]> countByCategory();

    // 根据症状ID查找药品
    @Query("SELECT m FROM Medicine m JOIN m.symptoms s WHERE s.id = :symptomId")
    List<Medicine> findBySymptomId(@Param("symptomId") Integer symptomId);

    // 根据症状名称查找药品
    @Query("SELECT m FROM Medicine m JOIN m.symptoms s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :symptomName, '%'))")
    List<Medicine> findBySymptomName(@Param("symptomName") String symptomName);

    // 统计药品按症状分类
    @Query("SELECT s.name, COUNT(m) FROM Medicine m JOIN m.symptoms s WHERE m.status = 1 GROUP BY s.id, s.name")
    List<Object[]> countMedicinesBySymptom();

    // 根据药品ID查询关联的销售记录（包含详细信息）
    @Query("SELECT sr FROM SaleRecord sr WHERE sr.medicine.id = :medicineId ORDER BY sr.saleTime DESC")
    List<SaleRecord> findSaleRecordsByMedicineId(@Param("medicineId") Long medicineId);

    // 根据药品ID查询关联的采购订单（包含详细信息）
    @Query("SELECT po FROM PurchaseOrder po WHERE po.medicine.id = :medicineId ORDER BY po.orderTime DESC")
    List<PurchaseOrder> findPurchaseOrdersByMedicineId(@Param("medicineId") Long medicineId);

    // 根据药品ID查询关联的库存（包含详细信息）
    @Query("SELECT s FROM Stock s WHERE s.medicine.id = :medicineId ORDER BY s.expirationDate ASC")
    List<Stock> findStocksByMedicineId(@Param("medicineId") Long medicineId);

    // 根据药品ID查询关联的预测结果（包含详细信息）
    @Query("SELECT pr FROM PredictionResult pr WHERE pr.medicine.id = :medicineId ORDER BY pr.predictionDate DESC")
    List<PredictionResult> findPredictionResultsByMedicineId(@Param("medicineId") Long medicineId);

    // 根据药品ID查询关联的症状（包含详细信息）
    @Query("SELECT m.symptoms FROM Medicine m WHERE m.id = :medicineId")
    List<Symptom> findSymptomsByMedicineId(@Param("medicineId") Long medicineId);

    // 统计药品的销售总量
    @Query("SELECT COALESCE(SUM(sr.quantity), 0) FROM SaleRecord sr WHERE sr.medicine.id = :medicineId")
    Long sumSaleQuantityByMedicineId(@Param("medicineId") Long medicineId);

    // 统计药品的采购总量
    @Query("SELECT COALESCE(SUM(po.quantity), 0) FROM PurchaseOrder po WHERE po.medicine.id = :medicineId AND po.orderStatus = 2")
    Long sumPurchaseQuantityByMedicineId(@Param("medicineId") Long medicineId);

    // 统计药品的当前库存量
    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Stock s WHERE s.medicine.id = :medicineId AND s.status = 1")
    Long sumCurrentStockByMedicineId(@Param("medicineId") Long medicineId);

    // 查询药品的销售趋势（按月份）
    @Query("SELECT YEAR(sr.saleTime), MONTH(sr.saleTime), SUM(sr.quantity), SUM(sr.totalAmount) " +
            "FROM SaleRecord sr WHERE sr.medicine.id = :medicineId " +
            "GROUP BY YEAR(sr.saleTime), MONTH(sr.saleTime) " +
            "ORDER BY YEAR(sr.saleTime), MONTH(sr.saleTime)")
    List<Object[]> findSaleTrendByMedicineId(@Param("medicineId") Long medicineId);
}