package com.example.demo.repository;


import com.example.demo.entity.Stock;
import com.example.demo.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    Long sumQuantityByMedicineId(@Param("medicineId") Long medicineId);

    // 查找所有库存不足的药品
    @Query("SELECT s.medicine.id, SUM(s.quantity) as totalQuantity, MIN(s.warningQuantity) as warningQuantity " +
            "FROM Stock s WHERE s.status = 1 GROUP BY s.medicine.id HAVING SUM(s.quantity) <= MIN(s.warningQuantity)")
    List<Object[]> findLowStockSummary();

    // 根据货架位置查找库存
    List<Stock> findByShelfLocation(String shelfLocation);

    //根据货品状态查找库存
    List<Stock> findByStatus(Integer status);

    // 根据库存ID查询关联的药品详情
    @Query("SELECT s.medicine FROM Stock s WHERE s.id = :stockId")
    Medicine findMedicineByStockId(@Param("stockId") Long stockId);

    // 根据药品ID查询库存状态统计
    @Query("SELECT s.status, SUM(s.quantity) as totalQuantity " +
            "FROM Stock s WHERE s.medicine.id = :medicineId " +
            "GROUP BY s.status")
    List<Object[]> findStatusStatisticsByMedicineId(@Param("medicineId") Long medicineId);

    // 根据货架位置查询库存
    @Query("SELECT s FROM Stock s WHERE s.shelfLocation LIKE LOWER(CONCAT('%', :location, '%')) AND s.status = 1")
    List<Stock> findByShelfLocationContaining(@Param("location") String location);

    // 查询过期库存（状态为0）
    @Query("SELECT s FROM Stock s WHERE s.status = 0 ORDER BY s.expirationDate ASC")
    List<Stock> findExpiredStock();

    // 统计库存总价值
    @Query("SELECT COALESCE(SUM(s.quantity * m.retailPrice), CAST(0 AS BigDecimal)) FROM Stock s JOIN s.medicine m WHERE s.status = 1")
    BigDecimal calculateTotalStockValue();

    // 统计每个分类的库存价值
    @Query("SELECT c.name, COALESCE(SUM(s.quantity * m.retailPrice), CAST(0 AS BigDecimal)) as totalValue FROM Stock s JOIN s.medicine m JOIN m.category c WHERE s.status = 1 GROUP BY c.id, c.name ORDER BY totalValue DESC")
    List<Object[]> calculateStockValueByCategory();

    // 查询库存周转率（基于销售记录）
    @Query(value = "SELECT m.id, m.name, " +
            "COALESCE(stock.currentQty, 0) AS currentStock, " +
            "COALESCE(sold.soldQty, 0) AS soldQuantity, " +
            "CASE WHEN COALESCE(stock.currentQty, 0) > 0 " +
            "THEN COALESCE(sold.soldQty, 0) / stock.currentQty " +
            "ELSE 0 END AS turnoverRate " +
            "FROM medicine m " +
            "LEFT JOIN (SELECT s.medicine_id AS medId, SUM(s.quantity) AS currentQty " +
            "           FROM stock s WHERE s.status = 1 GROUP BY s.medicine_id) stock " +
            "ON m.id = stock.medId " +
            "LEFT JOIN (SELECT sr.medicine_id AS medId, SUM(sr.quantity) AS soldQty " +
            "           FROM sale_record sr " +
            "           WHERE sr.sale_time BETWEEN :startDate AND :endDate " +
            "           GROUP BY sr.medicine_id) sold " +
            "ON m.id = sold.medId " +
            "ORDER BY turnoverRate DESC",
            nativeQuery = true)
    List<Object[]> calculateStockTurnoverRate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}