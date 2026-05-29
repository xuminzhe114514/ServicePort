package com.example.demo.repository;

import com.example.demo.entity.Category;
import com.example.demo.entity.Medicine;
import com.example.demo.entity.SaleRecord;
import com.example.demo.entity.PurchaseOrder;
import com.example.demo.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // 根据父分类ID查找子分类
    List<Category> findByParentId(Long parentId);

    // 根据分类级别查找
    List<Category> findByLevel(Integer level);

    // 根据状态查找分类
    List<Category> findByStatus(Integer status);

    // 查找所有启用状态的分类
    List<Category> findByStatusOrderBySortAsc(Integer status);

    // 查找一级分类
    List<Category> findByParentIdAndStatusOrderBySortAsc(Long parentId, Integer status);

    // 根据名称查找分类
    Optional<Category> findByName(String name);

    // 查找某个分类的所有子孙分类
    @Query("SELECT c FROM Category c WHERE c.parentId = :parentId")
    List<Category> findDescendantsByParentId(@Param("parentId") Long parentId);

    // 根据分类ID查询关联的药品
    @Query("SELECT m FROM Medicine m WHERE m.category.id = :categoryId")
    List<Medicine> findMedicinesByCategoryId(@Param("categoryId") Long categoryId);

    // 根据分类ID查询关联的销售记录
    @Query("SELECT sr FROM SaleRecord sr JOIN sr.medicine m WHERE m.category.id = :categoryId")
    List<SaleRecord> findSaleRecordsByCategoryId(@Param("categoryId") Long categoryId);

    // 根据分类ID查询关联的采购订单
    @Query("SELECT po FROM PurchaseOrder po JOIN po.medicine m WHERE m.category.id = :categoryId")
    List<PurchaseOrder> findPurchaseOrdersByCategoryId(@Param("categoryId") Long categoryId);

    // 根据分类ID查询关联的库存
    @Query("SELECT s FROM Stock s JOIN s.medicine m WHERE m.category.id = :categoryId")
    List<Stock> findStocksByCategoryId(@Param("categoryId") Long categoryId);

    // 统计分类下的药品数量
    @Query("SELECT COUNT(m) FROM Medicine m WHERE m.category.id = :categoryId AND m.status = 1")
    long countMedicinesByCategoryId(@Param("categoryId") Long categoryId);

    // 统计分类下的销售总额
    @Query("SELECT COALESCE(SUM(sr.totalAmount), CAST(0 AS BigDecimal)) FROM SaleRecord sr JOIN sr.medicine m WHERE m.category.id = :categoryId")
    BigDecimal sumSaleAmountByCategoryId(@Param("categoryId") Long categoryId);
}