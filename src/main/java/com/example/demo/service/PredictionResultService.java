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
    
    PredictionResult generatePrediction(Long medicineId, String modelType, LocalDate predictionDate);
    PredictionResult generatePrediction(Long medicineId, String modelType, LocalDate predictionDate, int predictionDays);
    List<PredictionResult> generateBatchPredictions(List<Long> medicineIds, String modelType, LocalDate startDate, LocalDate endDate);
    List<PredictionResult> generateBatchPredictions(List<Long> medicineIds, String modelType, int predictionDays);
    
    Map<String, Object> getModelPerformance(Long medicineId, int testPeriods);
    
    Map<String, Object> calculateABCClassification(List<Long> medicineIds);
    Map<String, Object> detectSlowMovingItems(int thresholdDays);
    Map<String, Object> calculateExpiryRisk();
    Map<String, Object> getInventoryStatusSummary();
    
    Map<String, Object> calculateDynamicSafetyStock(Long medicineId, int predictionDays);
    Map<String, Object> generateReplenishmentSuggestions(List<Long> medicineIds);
    Map<String, Object> generateReplenishmentSuggestionsWithInTransit(List<Map<String, Object>> inTransitOrders);
    Map<String, Object> getCurrentInventoryStatus();
    Map<String, Object> getInventoryWithExpiry();
    Map<String, Object> getMedicineInventoryStatus(Long medicineId);
    Map<String, Object> calculateSafetyStock(Long medicineId, int leadTimeDays, double serviceLevel);
    Map<String, Object> calculateBatchSafetyStock(int leadTimeDays, double serviceLevel);
    
    Map<Long, Integer> getRecommendedOrderQuantities(LocalDate targetDate);
    Map<Long, Integer> getRecommendedOrderQuantities(LocalDate targetDate, List<Long> medicineIds);
    
    Map<String, Object> pushSalesData(List<Map<String, Object>> salesData);
    Map<String, Object> pushMedicineData(List<Map<String, Object>> medicineData);
    Map<String, Object> pushStockData(List<Map<String, Object>> stockData);
    Map<String, Object> pushPurchaseOrderData(List<Map<String, Object>> purchaseOrderData);
    Map<String, Object> pushCategoryData(List<Map<String, Object>> categoryData);
    Map<String, Object> pushSymptomData(List<Map<String, Object>> symptomData);
    Map<String, Object> pushDailySalesData(List<Map<String, Object>> dailySalesData);
    Map<String, Object> pushSalesBySymptomData(List<Map<String, Object>> salesBySymptomData);
    Map<String, Object> pushStockTurnoverData(List<Map<String, Object>> stockTurnoverData);
    Map<String, Object> pushStockValueData(List<Map<String, Object>> stockValueData);
    Map<String, Object> pushExpiringStockData(List<Map<String, Object>> expiringStockData);
    Map<String, Object> pushLowStockData(List<Map<String, Object>> lowStockData);

    Map<String, Object> getDataSummary();
    
    boolean checkModelServiceHealth();
    Map<String, Object> getModelServiceInfo();
}