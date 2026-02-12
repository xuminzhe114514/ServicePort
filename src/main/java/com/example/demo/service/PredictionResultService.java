package com.example.demo.service;

import com.example.demo.entity.PredictionResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
@Transactional
public interface PredictionResultService extends BaseService<PredictionResult, Long> {
    List<PredictionResult> findByMedicineId(Long medicineId);
    List<PredictionResult> findByPredictionDate(LocalDate predictionDate);
    PredictionResult findLatestByMedicineId(Long medicineId);
    List<PredictionResult> findByPredictionDateRange(LocalDate startDate, LocalDate endDate);
    List<PredictionResult> findNeedReprediction(Double threshold);
    Map<String, Double> getAverageAccuracyByModel();
    PredictionResult generatePrediction(Long medicineId, String modelType,
                                        LocalDate predictionDate);
    List<PredictionResult> generateBatchPredictions(List<Long> medicineIds,
                                                    String modelType,
                                                    LocalDate startDate,
                                                    LocalDate endDate);
    Map<Long, Integer> getRecommendedOrderQuantities(LocalDate targetDate);
}