package com.example.demo;

import com.example.demo.entity.PredictionResult;
import org.junit.jupiter.api.*;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PredictionResultService测试")
class PredictionResultServiceTest extends BaseServiceTest {

    @Test
    @Order(1)
    @DisplayName("测试BaseService方法 - save")
    void testSave() {
        System.out.println("=== 测试PredictionResultService.save() ===");

        // 创建新预测结果
        PredictionResult predictionResult = new PredictionResult();
        predictionResult.setMedicine(medicineService.findById(testMedicineId));
        predictionResult.setPredictionDate(LocalDate.now().plusDays(14));
        predictionResult.setPredictedQuantity(30);
        predictionResult.setModelType("ARIMA");
        predictionResult.setAccuracyRate(new BigDecimal("88.00"));
        predictionResult.setConfidenceIntervalLower(25);
        predictionResult.setConfidenceIntervalUpper(35);
        predictionResult.setCreateTime(LocalDateTime.now());

        // 保存预测结果
        PredictionResult savedPredictionResult = predictionResultService.save(predictionResult);
        assertNotNull(savedPredictionResult, "保存的预测结果不应为空");
        assertNotNull(savedPredictionResult.getId(), "保存的预测结果ID不应为空");
        assertEquals(30, savedPredictionResult.getPredictedQuantity(), "预测数量应正确");

        System.out.println("PredictionResultService.save()测试通过 ✓");
    }

    @Test
    @Order(2)
    @DisplayName("测试BaseService方法 - update")
    void testUpdate() {
        System.out.println("=== 测试PredictionResultService.update() ===");

        // 获取测试预测结果
        PredictionResult predictionResult = predictionResultService.findById(testPredictionResultId);
        assertNotNull(predictionResult, "预测结果应存在");

        // 更新预测结果
        predictionResult.setPredictedQuantity(25);
        predictionResult.setAccuracyRate(new BigDecimal("90.00"));

        // 保存更新
        PredictionResult updatedPredictionResult = predictionResultService.update(predictionResult);
        assertNotNull(updatedPredictionResult, "更新后的预测结果不应为空");
        assertEquals(25, updatedPredictionResult.getPredictedQuantity(), "预测数量应已更新");
        assertEquals(new BigDecimal("90.00"), updatedPredictionResult.getAccuracyRate(), "准确率应已更新");

        System.out.println("PredictionResultService.update()测试通过 ✓");
    }

    @Test
    @Order(3)
    @DisplayName("测试BaseService方法 - delete")
    void testDelete() {
        System.out.println("=== 测试PredictionResultService.delete() ===");

        // 创建一个临时预测结果用于删除测试
        PredictionResult predictionResult = new PredictionResult();
        predictionResult.setMedicine(medicineService.findById(testMedicineId));
        predictionResult.setPredictionDate(LocalDate.now().plusDays(21));
        predictionResult.setPredictedQuantity(15);
        predictionResult.setModelType("ARIMA");
        predictionResult.setAccuracyRate(new BigDecimal("85.00"));
        predictionResult.setConfidenceIntervalLower(10);
        predictionResult.setConfidenceIntervalUpper(20);
        predictionResult.setCreateTime(LocalDateTime.now());
        PredictionResult savedPredictionResult = predictionResultService.save(predictionResult);
        Long tempPredictionResultId = savedPredictionResult.getId();
        assertNotNull(tempPredictionResultId, "临时预测结果ID不应为空");

        // 验证预测结果存在
        PredictionResult foundPredictionResult = predictionResultService.findById(tempPredictionResultId);
        assertNotNull(foundPredictionResult, "临时预测结果应存在");

        // 删除预测结果
        predictionResultService.delete(tempPredictionResultId);

        // 验证预测结果已删除
        PredictionResult deletedPredictionResult = predictionResultService.findById(tempPredictionResultId);
        assertNull(deletedPredictionResult, "删除后的预测结果应不存在");

        System.out.println("PredictionResultService.delete()测试通过 ✓");
    }

    @Test
    @Order(4)
    @DisplayName("测试BaseService方法 - findById")
    void testFindById() {
        System.out.println("=== 测试PredictionResultService.findById() ===");

        // 查找测试预测结果
        PredictionResult predictionResult = predictionResultService.findById(testPredictionResultId);
        assertNotNull(predictionResult, "预测结果应存在");
        assertNotNull(predictionResult.getModelType(), "模型类型不应为空");

        System.out.println("PredictionResultService.findById()测试通过 ✓");
    }

    @Test
    @Order(5)
    @DisplayName("测试BaseService方法 - findAll")
    void testFindAll() {
        System.out.println("=== 测试PredictionResultService.findAll() ===");

        // 测试无参findAll
        List<PredictionResult> predictionResults = predictionResultService.findAll();
        assertNotNull(predictionResults, "预测结果列表不应为空");
        assertTrue(predictionResults.size() > 0, "预测结果列表应包含数据");

        // 测试带分页的findAll
        Page<PredictionResult> predictionResultPage = predictionResultService.findAll(pageable);
        assertNotNull(predictionResultPage, "分页预测结果列表不应为空");
        assertTrue(predictionResultPage.getTotalElements() > 0, "分页预测结果列表应包含数据");

        System.out.println("PredictionResultService.findAll()测试通过 ✓");
    }

    @Test
    @Order(6)
    @DisplayName("测试BaseService方法 - saveAll")
    void testSaveAll() {
        System.out.println("=== 测试PredictionResultService.saveAll() ===");

        // 创建多个预测结果
        PredictionResult predictionResult1 = new PredictionResult();
        predictionResult1.setMedicine(medicineService.findById(testMedicineId));
        predictionResult1.setPredictionDate(LocalDate.now().plusDays(7));
        predictionResult1.setPredictedQuantity(20);
        predictionResult1.setModelType("ARIMA");
        predictionResult1.setAccuracyRate(new BigDecimal("87.00"));
        predictionResult1.setConfidenceIntervalLower(15);
        predictionResult1.setConfidenceIntervalUpper(25);
        predictionResult1.setCreateTime(LocalDateTime.now());

        PredictionResult predictionResult2 = new PredictionResult();
        predictionResult2.setMedicine(medicineService.findById(testMedicineId));
        predictionResult2.setPredictionDate(LocalDate.now().plusDays(14));
        predictionResult2.setPredictedQuantity(25);
        predictionResult2.setModelType("ARIMA");
        predictionResult2.setAccuracyRate(new BigDecimal("89.00"));
        predictionResult2.setConfidenceIntervalLower(20);
        predictionResult2.setConfidenceIntervalUpper(30);
        predictionResult2.setCreateTime(LocalDateTime.now());

        List<PredictionResult> predictionResults = List.of(predictionResult1, predictionResult2);

        // 批量保存
        List<PredictionResult> savedPredictionResults = predictionResultService.saveAll(predictionResults);
        assertNotNull(savedPredictionResults, "批量保存的预测结果列表不应为空");
        assertEquals(2, savedPredictionResults.size(), "批量保存的预测结果数量应正确");
        for (PredictionResult savedPredictionResult : savedPredictionResults) {
            assertNotNull(savedPredictionResult.getId(), "保存的预测结果ID不应为空");
        }

        System.out.println("PredictionResultService.saveAll()测试通过 ✓");
    }

    @Test
    @Order(7)
    @DisplayName("测试BaseService方法 - deleteAll")
    void testDeleteAll() {
        System.out.println("=== 测试PredictionResultService.deleteAll() ===");

        // 创建多个临时预测结果用于删除测试
        PredictionResult predictionResult1 = new PredictionResult();
        predictionResult1.setMedicine(medicineService.findById(testMedicineId));
        predictionResult1.setPredictionDate(LocalDate.now().plusDays(21));
        predictionResult1.setPredictedQuantity(10);
        predictionResult1.setModelType("ARIMA");
        predictionResult1.setAccuracyRate(new BigDecimal("85.00"));
        predictionResult1.setConfidenceIntervalLower(5);
        predictionResult1.setConfidenceIntervalUpper(15);
        predictionResult1.setCreateTime(LocalDateTime.now());

        PredictionResult predictionResult2 = new PredictionResult();
        predictionResult2.setMedicine(medicineService.findById(testMedicineId));
        predictionResult2.setPredictionDate(LocalDate.now().plusDays(28));
        predictionResult2.setPredictedQuantity(15);
        predictionResult2.setModelType("ARIMA");
        predictionResult2.setAccuracyRate(new BigDecimal("86.00"));
        predictionResult2.setConfidenceIntervalLower(10);
        predictionResult2.setConfidenceIntervalUpper(20);
        predictionResult2.setCreateTime(LocalDateTime.now());

        List<PredictionResult> predictionResults = List.of(predictionResult1, predictionResult2);
        List<PredictionResult> savedPredictionResults = predictionResultService.saveAll(predictionResults);
        List<Long> ids = savedPredictionResults.stream().map(PredictionResult::getId).toList();

        // 验证预测结果存在
        for (Long id : ids) {
            assertNotNull(predictionResultService.findById(id), "临时预测结果应存在");
        }

        // 批量删除
        predictionResultService.deleteAll(ids);

        // 验证预测结果已删除
        for (Long id : ids) {
            assertNull(predictionResultService.findById(id), "删除后的预测结果应不存在");
        }

        System.out.println("PredictionResultService.deleteAll()测试通过 ✓");
    }

    @Test
    @Order(8)
    @DisplayName("测试BaseService方法 - exists")
    void testExists() {
        System.out.println("=== 测试PredictionResultService.exists() ===");

        // 测试存在的预测结果
        boolean exists = predictionResultService.exists(testPredictionResultId);
        assertTrue(exists, "测试预测结果应存在");

        // 测试不存在的预测结果
        boolean notExists = predictionResultService.exists(999999L);
        assertFalse(notExists, "不存在的预测结果应返回false");

        System.out.println("PredictionResultService.exists()测试通过 ✓");
    }

    @Test
    @Order(10)
    @DisplayName("测试PredictionResultService特有方法 - findByMedicineId")
    void testFindByMedicineId() {
        System.out.println("=== 测试PredictionResultService.findByMedicineId() ===");

        // 测试查找指定药品的预测结果
        List<PredictionResult> medicinePredictions = predictionResultService.findByMedicineId(testMedicineId);
        assertNotNull(medicinePredictions, "指定药品的预测结果列表不应为空");
        assertTrue(medicinePredictions.size() > 0, "指定药品的预测结果列表应包含数据");

        System.out.println("PredictionResultService.findByMedicineId()测试通过 ✓");
    }

    @Test
    @Order(11)
    @DisplayName("测试PredictionResultService特有方法 - findByPredictionDate")
    void testFindByPredictionDate() {
        System.out.println("=== 测试PredictionResultService.findByPredictionDate() ===");

        // 获取测试预测结果的预测日期
        PredictionResult testPrediction = predictionResultService.findById(testPredictionResultId);
        assertNotNull(testPrediction, "测试预测结果应存在");
        LocalDate predictionDate = testPrediction.getPredictionDate();
        assertNotNull(predictionDate, "预测日期不应为空");

        // 测试查找指定预测日期的预测结果
        List<PredictionResult> datePredictions = predictionResultService.findByPredictionDate(predictionDate);
        assertNotNull(datePredictions, "指定预测日期的预测结果列表不应为空");

        System.out.println("PredictionResultService.findByPredictionDate()测试通过 ✓");
    }

    @Test
    @Order(12)
    @DisplayName("测试PredictionResultService特有方法 - findLatestByMedicineId")
    void testFindLatestByMedicineId() {
        System.out.println("=== 测试PredictionResultService.findLatestByMedicineId() ===");

        // 测试查找指定药品的最新预测结果
        PredictionResult latestPrediction = predictionResultService.findLatestByMedicineId(testMedicineId);
        assertNotNull(latestPrediction, "最新预测结果不应为空");

        System.out.println("PredictionResultService.findLatestByMedicineId()测试通过 ✓");
    }

    @Test
    @Order(13)
    @DisplayName("测试PredictionResultService特有方法 - findByPredictionDateRange")
    void testFindByPredictionDateRange() {
        System.out.println("=== 测试PredictionResultService.findByPredictionDateRange() ===");

        // 测试查找指定日期范围内的预测结果
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);
        List<PredictionResult> rangePredictions = predictionResultService.findByPredictionDateRange(startDate, endDate);
        assertNotNull(rangePredictions, "指定日期范围内的预测结果列表不应为空");

        System.out.println("PredictionResultService.findByPredictionDateRange()测试通过 ✓");
    }

    @Test
    @Order(14)
    @DisplayName("测试PredictionResultService特有方法 - findNeedReprediction")
    void testFindNeedReprediction() {
        System.out.println("=== 测试PredictionResultService.findNeedReprediction() ===");

        // 测试查找需要重新预测的结果
        List<PredictionResult> needReprediction = predictionResultService.findNeedReprediction(0.9);
        assertNotNull(needReprediction, "需要重新预测的结果列表不应为空");

        System.out.println("PredictionResultService.findNeedReprediction()测试通过 ✓");
    }

    @Test
    @Order(15)
    @DisplayName("测试PredictionResultService特有方法 - getAverageAccuracyByModel")
    void testGetAverageAccuracyByModel() {
        System.out.println("=== 测试PredictionResultService.getAverageAccuracyByModel() ===");

        // 测试获取各模型的平均准确率
        Map<String, Double> averageAccuracy = predictionResultService.getAverageAccuracyByModel();
        assertNotNull(averageAccuracy, "各模型的平均准确率不应为空");

        System.out.println("PredictionResultService.getAverageAccuracyByModel()测试通过 ✓");
    }

    @Test
    @Order(16)
    @DisplayName("测试PredictionResultService特有方法 - generatePrediction")
    void testGeneratePrediction() {
        System.out.println("=== 测试PredictionResultService.generatePrediction() ===");

        // 测试生成预测结果
        LocalDate predictionDate = LocalDate.now().plusDays(10);
        PredictionResult generatedPrediction = predictionResultService.generatePrediction(testMedicineId, "ARIMA", predictionDate);
        assertNotNull(generatedPrediction, "生成的预测结果不应为空");
        assertNotNull(generatedPrediction.getId(), "生成的预测结果ID不应为空");

        System.out.println("PredictionResultService.generatePrediction()测试通过 ✓");
    }

    @Test
    @Order(17)
    @DisplayName("测试PredictionResultService特有方法 - generateBatchPredictions")
    void testGenerateBatchPredictions() {
        System.out.println("=== 测试PredictionResultService.generateBatchPredictions() ===");

        // 测试批量生成预测结果
        List<Long> medicineIds = List.of(testMedicineId);
        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate endDate = LocalDate.now().plusDays(15);
        List<PredictionResult> batchPredictions = predictionResultService.generateBatchPredictions(medicineIds, "ARIMA", startDate, endDate);
        assertNotNull(batchPredictions, "批量生成的预测结果列表不应为空");

        System.out.println("PredictionResultService.generateBatchPredictions()测试通过 ✓");
    }

    @Test
    @Order(18)
    @DisplayName("测试PredictionResultService特有方法 - getRecommendedOrderQuantities")
    void testGetRecommendedOrderQuantities() {
        System.out.println("=== 测试PredictionResultService.getRecommendedOrderQuantities() ===");

        // 测试获取推荐订货数量
        LocalDate targetDate = LocalDate.now().plusDays(7);
        Map<Long, Integer> recommendedQuantities = predictionResultService.getRecommendedOrderQuantities(targetDate);
        assertNotNull(recommendedQuantities, "推荐订货数量不应为空");

        System.out.println("PredictionResultService.getRecommendedOrderQuantities()测试通过 ✓");
    }
}
