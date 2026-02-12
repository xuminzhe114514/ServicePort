package com.example.demo.repository;


import com.example.demo.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    // 根据药品ID查找库存（使用关联对象）
    @Query("SELECT s FROM Stock s WHERE s.medicine.id = :medicineId")
    List<Stock> findByMedicineId(@Param("medicineId") Long medicineId);

    // 根据药品ID和状态查找库存
    @Query("SELECT s FROM Stock s WHERE s.medicine.id = :medicineId AND s.status = :status")
    List<Stock> findByMedicineIdAndStatus(@Param("medicineId") Long medicineId,
                                          @Param("status") Integer status);

    // 查找过期库存
    List<Stock> findByExpirationDateBeforeAndStatus(LocalDate date, Integer status);

    // 查找即将过期的库存
    @Query("SELECT s FROM Stock s WHERE s.expirationDate BETWEEN :startDate AND :endDate AND s.status = 1")
    List<Stock> findExpiringStock(@Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate);

    // 查找库存不足的药品（数量小于等于预警数量）
    @Query("SELECT s FROM Stock s WHERE s.quantity <= s.warningQuantity AND s.status = 1")
    List<Stock> findLowStock();

    // 根据批号查找库存
    List<Stock> findByBatchNumber(String batchNumber);

    // 计算某个药品的总库存量
    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Stock s WHERE s.medicine.id = :medicineId AND s.status = 1")
    Integer sumQuantityByMedicineId(@Param("medicineId") Long medicineId);

    // 查找所有库存不足的药品
    @Query("SELECT s.medicine.id, SUM(s.quantity) as totalQuantity, MIN(s.warningQuantity) as warningQuantity " +
            "FROM Stock s WHERE s.status = 1 GROUP BY s.medicine.id HAVING SUM(s.quantity) <= MIN(s.warningQuantity)")
    List<Object[]> findLowStockSummary();

    // 根据货架位置查找库存
    List<Stock> findByShelfLocation(String shelfLocation);

    //根据货品状态查找库存
    List<Stock> findByStatus(Integer status);
}