package com.example.demo.repository;

import com.example.demo.entity.PredictionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PredictionResultRepository extends JpaRepository<PredictionResult, Long> {

    // 根据药品ID查找预测结果
    @Query("SELECT pr FROM PredictionResult pr WHERE pr.medicine.id = :medicineId")
    List<PredictionResult> findByMedicineId(@Param("medicineId") Long medicineId);

    // 根据预测日期查找
    List<PredictionResult> findByPredictionDate(LocalDate predictionDate);

    // 根据模型类型查找
    List<PredictionResult> findByModelType(String modelType);

    // 查找某个药品最新的预测结果
    @Query("SELECT pr FROM PredictionResult pr WHERE pr.medicine.id = :medicineId ORDER BY pr.predictionDate DESC LIMIT 1")
    Optional<PredictionResult> findFirstByMedicineIdOrderByPredictionDateDesc(@Param("medicineId")Long medicineId);

    // 查找未来某段时间的预测结果
    List<PredictionResult> findByPredictionDateBetween(LocalDate startDate, LocalDate endDate);

    // 查找准确率高于某个值的预测结果
    List<PredictionResult> findByAccuracyRateGreaterThanEqual(Double minAccuracyRate);

    // 统计各个模型的平均准确率
    @Query("SELECT pr.modelType, AVG(pr.accuracyRate) FROM PredictionResult pr GROUP BY pr.modelType")
    List<Object[]> findAverageAccuracyByModel();

    // 查找需要重新预测的记录
    @Query("SELECT pr FROM PredictionResult pr WHERE (pr.accuracyRate IS NULL OR pr.accuracyRate < :threshold) OR pr.predictionDate < CURRENT_DATE ORDER BY pr.predictionDate DESC")
    List<PredictionResult> findNeedReprediction(@Param("threshold") Double threshold);

    // 查找部分药品最新的预测结果
    @Query("SELECT pr FROM PredictionResult pr WHERE pr.medicine.id IN :medicineIds AND pr.predictionDate = (SELECT MAX(pr2.predictionDate) FROM PredictionResult pr2 WHERE pr2.medicine.id = pr.medicine.id)")
    List<PredictionResult> findLatestByMedicineIds(@Param("medicineIds") List<Long> medicineIds);
}
