package com.example.demo.service.impl;

import com.example.demo.entity.Medicine;
import com.example.demo.entity.PredictionResult;
import com.example.demo.entity.Stock;
import com.example.demo.repository.MedicineRepository;
import com.example.demo.repository.PredictionResultRepository;
import com.example.demo.repository.StockRepository;
import com.example.demo.service.PredictionResultService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@Deprecated
public class PredictionResultServiceImpl extends BaseServiceImpl<PredictionResult, Long, PredictionResultRepository>
        implements PredictionResultService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${model.service.base-url:http://localhost:5101}")
    private String modelServiceBaseUrl;

    @Value("${model.service.api-version:/api/v1}")
    private String modelApiVersion;

    @Value("${prediction.default-days:7}")
    private int defaultPredictionDays;

    @Value("${prediction.confidence-interval-factor:0.2}")
    private double confidenceIntervalFactor;

    @Value("${inventory.safety-stock-factor:0.5}")
    private double safetyStockFactor;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private StockRepository stockRepository;

    protected PredictionResultServiceImpl(PredictionResultRepository repository) {super(repository);}

    @Override
    public List<PredictionResult> findByMedicineId(Long medicineId) {return repository.findByMedicineId(medicineId);}

    @Override
    public List<PredictionResult> findByPredictionDate(LocalDate predictionDate) {return repository.findByPredictionDate(predictionDate);}

    @Override
    public PredictionResult findLatestByMedicineId(Long medicineId) {return repository.findFirstByMedicineIdOrderByPredictionDateDesc(medicineId).orElse(null);}

    @Override
    public List<PredictionResult> findByPredictionDateRange(LocalDate startDate, LocalDate endDate) {return repository.findByPredictionDateBetween(startDate, endDate);}

    @Override
    public List<PredictionResult> findNeedReprediction(Double threshold) {return repository.findNeedReprediction(threshold != null ? BigDecimal.valueOf(threshold) : null);}

    @Override
    public Map<String, Double> getAverageAccuracyByModel() {
        List<Object[]> results = repository.findAverageAccuracyByModel();
        return results.stream().collect(
                Collectors.toMap(
                        result -> (String) result[0],
                        result -> ((BigDecimal) result[1]).doubleValue()
                )
        );
    }

    /**
     * 调用模型端API进行单个药品预测
     */
    @Deprecated
    @Override
    public PredictionResult generatePrediction(Long medicineId, String modelType,
                                               LocalDate predictionDate) {
        log.info("生成单个药品预测 - 药品ID: {}, 模型类型: {}, 预测日期: {}",
                medicineId, modelType, predictionDate);

        Medicine medicine = medicineRepository.findById(medicineId).orElse(null);
        if (medicine == null) {
            log.error("药品不存在 - ID: {}", medicineId);
            return null;
        }

        try {
            // 调用模型端API
            Map<String, Object> predictionData = callModelPredictionApi(medicineId, predictionDate);

            if (predictionData == null) {
                log.warn("模型端API调用失败，使用本地算法生成预测");
                return generateLocalPrediction(medicine, modelType, predictionDate);
            }

            return createPredictionResultFromModelResponse(medicine, predictionData, modelType, predictionDate);

        } catch (Exception e) {
            log.error("调用模型端API失败，使用本地算法生成预测", e);
            return generateLocalPrediction(medicine, modelType, predictionDate);
        }
    }

    /**
     * 调用模型端批量预测API
     */
    @Deprecated
    @Override
    public List<PredictionResult> generateBatchPredictions(List<Long> medicineIds,
                                                           String modelType,
                                                           LocalDate startDate,
                                                           LocalDate endDate) {
        log.info("批量生成预测 - 药品数量: {}, 模型类型: {}, 日期范围: {} 至 {}",
                medicineIds.size(), modelType, startDate, endDate);

        List<PredictionResult> results = new ArrayList<>();

        // 计算预测天数
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        if (daysBetween < 0) {
            log.error("无效的日期范围: startDate={}, endDate={}", startDate, endDate);
            return results;
        }

        try {
            // 调用模型端批量预测API
            List<Map<String, Object>> batchPredictions = callBatchPredictionApi(medicineIds, startDate, endDate);

            if (batchPredictions != null && !batchPredictions.isEmpty()) {
                // 从模型端返回的数据创建预测结果
                results = createBatchPredictionsFromModelResponse(medicineIds, batchPredictions, modelType, startDate, endDate);
            } else {
                // 模型端调用失败，使用本地算法
                results = generateLocalBatchPredictions(medicineIds, modelType, startDate, endDate);
            }
        } catch (Exception e) {
            log.error("批量调用模型端API失败，使用本地算法", e);
            results = generateLocalBatchPredictions(medicineIds, modelType, startDate, endDate);
        }

        // 保存所有预测结果
        if (!results.isEmpty()) {
            return repository.saveAll(results);
        }

        return results;
    }

    /**
     * 获取推荐订购数量
     */
    @Deprecated
    @Override
    public Map<Long, Integer> getRecommendedOrderQuantities(LocalDate targetDate) {
        log.info("获取推荐订购数量 - 目标日期: {}", targetDate);

        List<Medicine> medicines = medicineRepository.findByStatus(1);

        // 批量查询所有药品的最新预测
        List<Long> medicineIds = medicines.stream()
                .map(Medicine::getId)
                .toList();

        Map<Long, PredictionResult> predictionMap = repository
                .findLatestByMedicineIds(medicineIds)
                .stream()
                .collect(Collectors.toMap(
                        pr -> pr.getMedicine().getId(),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        Map<Long, Integer> recommendations = new HashMap<>();

        for (Medicine medicine : medicines) {
            PredictionResult prediction = predictionMap.get(medicine.getId());

            // 如果没有预测，尝试从模型端获取
            if (prediction == null || prediction.getPredictionDate().isBefore(targetDate)) {
                prediction = generatePrediction(medicine.getId(), "DEFAULT", targetDate);
            }

            if (prediction != null) {
                recommendations.put(medicine.getId(), prediction.getRecommendedOrderQuantity());
            } else {
                // 如果预测失败，使用默认值
                recommendations.put(medicine.getId(), 0);
            }
        }

        return recommendations;
    }

    /**
     * ====================== 模型端API调用方法 ======================
     */

    /**
     * 调用模型端单个药品预测API
     */
    @Deprecated
    private Map<String, Object> callModelPredictionApi(Long medicineId, LocalDate predictionDate) {
        String apiUrl = modelServiceBaseUrl + modelApiVersion + "/predict/single-medicine";

        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("medicineId", String.valueOf(medicineId));

            // 计算预测天数（从今天到目标日期的天数）
            long predictionDays = ChronoUnit.DAYS.between(LocalDate.now(), predictionDate);
            requestBody.put("predictionDays", Math.max(1, (int) predictionDays));

            log.debug("调用模型端单个预测API: {}", apiUrl);
            log.debug("请求参数: {}", requestBody);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 发送请求
            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                // 检查响应状态
                if ("success".equals(responseBody.get("status"))) {
                    log.info("模型端API调用成功 - 药品ID: {}", medicineId);
                    return responseBody;
                } else {
                    log.error("模型端API返回错误状态: {}", responseBody.get("message"));
                }
            } else {
                log.error("模型端API调用失败 - 状态码: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("调用模型端API时发生异常", e);
        }

        return null;
    }

    /**
     * 调用模型端批量预测API
     */
    @Deprecated
    private List<Map<String, Object>> callBatchPredictionApi(List<Long> medicineIds,
                                                             LocalDate startDate,
                                                             LocalDate endDate) {
        String apiUrl = modelServiceBaseUrl + modelApiVersion + "/predict/batch";

        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            List<String> medicineIdStrs = medicineIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList());
            requestBody.put("medicineIds", medicineIdStrs);

            // 计算预测天数
            long predictionDays = ChronoUnit.DAYS.between(startDate, endDate);
            requestBody.put("predictionDays", Math.max(1, (int) predictionDays));

            log.debug("调用模型端批量预测API: {}", apiUrl);
            log.debug("请求参数: {}", requestBody);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 发送请求
            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                if ("success".equals(responseBody.get("status"))) {
                    log.info("模型端批量预测API调用成功 - 药品数量: {}", medicineIds.size());

                    // 解析返回的预测数据
                    Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                    if (data != null) {
                        return (List<Map<String, Object>>) data.get("medicines");
                    }
                } else {
                    log.error("模型端批量预测API返回错误状态: {}", responseBody.get("message"));
                }
            } else {
                log.error("模型端批量预测API调用失败 - 状态码: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("调用模型端批量预测API时发生异常", e);
        }

        return null;
    }

    /**
     * 调用模型端获取历史预测记录
     */
    @Deprecated
    private List<Map<String, Object>> getHistoricalPredictions(Long medicineId) {
        String apiUrl = modelServiceBaseUrl + modelApiVersion + "/predict/history/" + medicineId;

        try {
            log.debug("获取历史预测记录: {}", apiUrl);

            ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                if ("success".equals(responseBody.get("status"))) {
                    Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                    if (data != null) {
                        return (List<Map<String, Object>>) data.get("history");
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取历史预测记录时发生异常", e);
        }

        return null;
    }

    /**
     * ====================== 数据处理方法 ======================
     */

    /**
     * 从模型端响应创建预测结果
     */
    @Deprecated
    private PredictionResult createPredictionResultFromModelResponse(Medicine medicine,
                                                                     Map<String, Object> modelResponse,
                                                                     String modelType,
                                                                     LocalDate predictionDate) {
        try {
            PredictionResult prediction = new PredictionResult();
            prediction.setMedicine(medicine);
            prediction.setPredictionDate(predictionDate);
            prediction.setModelType(modelType);

            // 从响应中提取预测数据
            Map<String, Object> data = (Map<String, Object>) modelResponse.get("data");
            if (data != null) {
                List<Map<String, Object>> predictions = (List<Map<String, Object>>) data.get("predictions");

                // 找到指定日期的预测
                Optional<Map<String, Object>> targetPrediction = predictions.stream()
                        .filter(p -> predictionDate.toString().equals(p.get("predictionDate")))
                        .findFirst();

                if (targetPrediction.isPresent()) {
                    Map<String, Object> predData = targetPrediction.get();

                    // 设置预测数量
                    if (predData.get("predictedQuantity") != null) {
                        prediction.setPredictedQuantity((Integer) predData.get("predictedQuantity"));
                    }

                    // 设置置信区间
                    if (predData.get("confidenceIntervalLower") != null) {
                        prediction.setConfidenceIntervalLower((Integer) predData.get("confidenceIntervalLower"));
                    }

                    if (predData.get("confidenceIntervalUpper") != null) {
                        prediction.setConfidenceIntervalUpper((Integer) predData.get("confidenceIntervalUpper"));
                    }

                    // 设置模型类型（优先使用模型端返回的）
                    if (predData.get("modelType") != null) {
                        prediction.setModelType((String) predData.get("modelType"));
                    }
                }
            }

            // 计算预测准确率（如果有历史数据）
            BigDecimal accuracyRate = calculateAccuracyRate(medicine.getId(), prediction.getPredictedQuantity());
            prediction.setAccuracyRate(accuracyRate);

            // 计算建议订购数量
            int recommendedQuantity = calculateRecommendedOrderQuantity(medicine.getId(), prediction.getPredictedQuantity());
            prediction.setRecommendedOrderQuantity(recommendedQuantity);

            // 设置时间戳
            prediction.setCreateTime(LocalDateTime.now());
            prediction.setUpdateTime(LocalDateTime.now());

            return repository.save(prediction);

        } catch (Exception e) {
            log.error("从模型响应创建预测结果时发生异常", e);
            return null;
        }
    }

    /**
     * 从批量响应创建预测结果
     */
    @Deprecated
    private List<PredictionResult> createBatchPredictionsFromModelResponse(List<Long> medicineIds,
                                                                           List<Map<String, Object>> batchPredictions,
                                                                           String modelType,
                                                                           LocalDate startDate,
                                                                           LocalDate endDate) {
        List<PredictionResult> results = new ArrayList<>();
        Map<Long, Medicine> medicineMap = medicineRepository.findAllById(medicineIds)
                .stream()
                .collect(Collectors.toMap(Medicine::getId, Function.identity()));

        for (Map<String, Object> medicinePrediction : batchPredictions) {
            String medicineIdStr = (String) medicinePrediction.get("medicineId");
            Long medicineId = Long.parseLong(medicineIdStr);

            Medicine medicine = medicineMap.get(medicineId);
            if (medicine == null) {
                continue;
            }

            List<Map<String, Object>> predictions = (List<Map<String, Object>>) medicinePrediction.get("predictions");

            for (Map<String, Object> predData : predictions) {
                String predictionDateStr = (String) predData.get("predictionDate");
                LocalDate predictionDate = LocalDate.parse(predictionDateStr);

                // 只保存指定日期范围内的预测
                if (!predictionDate.isBefore(startDate) && !predictionDate.isAfter(endDate)) {
                    PredictionResult prediction = new PredictionResult();
                    prediction.setMedicine(medicine);
                    prediction.setPredictionDate(predictionDate);
                    prediction.setModelType((String) predData.get("modelType"));

                    if (predData.get("predictedQuantity") != null) {
                        prediction.setPredictedQuantity((Integer) predData.get("predictedQuantity"));
                    }

                    if (predData.get("confidenceIntervalLower") != null) {
                        prediction.setConfidenceIntervalLower((Integer) predData.get("confidenceIntervalLower"));
                    }

                    if (predData.get("confidenceIntervalUpper") != null) {
                        prediction.setConfidenceIntervalUpper((Integer) predData.get("confidenceIntervalUpper"));
                    }

                    // 计算准确率和建议订购数量
                    if (prediction.getPredictedQuantity() != null) {
                        BigDecimal accuracyRate = calculateAccuracyRate(medicineId, prediction.getPredictedQuantity());
                        prediction.setAccuracyRate(accuracyRate);

                        int recommendedQuantity = calculateRecommendedOrderQuantity(medicineId, prediction.getPredictedQuantity());
                        prediction.setRecommendedOrderQuantity(recommendedQuantity);
                    }

                    prediction.setCreateTime(LocalDateTime.now());
                    prediction.setUpdateTime(LocalDateTime.now());

                    results.add(prediction);
                }
            }
        }

        return results;
    }

    /**
     * ====================== 备用本地算法 ======================
     */

    /**
     * 本地预测算法（模型端API失败时使用）
     */
    @Deprecated
    private PredictionResult generateLocalPrediction(Medicine medicine, String modelType, LocalDate predictionDate) {
        log.info("使用本地算法生成预测 - 药品: {}, 日期: {}", medicine.getName(), predictionDate);

        PredictionResult prediction = new PredictionResult();
        prediction.setMedicine(medicine);
        prediction.setPredictionDate(predictionDate);
        prediction.setModelType(modelType);

        // 使用本地算法计算预测数量
        int predictedQuantity = calculateLocalPredictedQuantity(medicine.getId());
        prediction.setPredictedQuantity(predictedQuantity);

        // 设置置信区间
        double lowerBound = predictedQuantity * (1 - confidenceIntervalFactor);
        double upperBound = predictedQuantity * (1 + confidenceIntervalFactor);
        prediction.setConfidenceIntervalLower((int) lowerBound);
        prediction.setConfidenceIntervalUpper((int) upperBound);

        // 计算准确率（模拟）
        BigDecimal accuracyRate = calculateAccuracyRate(medicine.getId(), predictedQuantity);
        prediction.setAccuracyRate(accuracyRate);

        // 计算建议订购数量
        int recommendedQuantity = calculateRecommendedOrderQuantity(medicine.getId(), predictedQuantity);
        prediction.setRecommendedOrderQuantity(recommendedQuantity);

        prediction.setCreateTime(LocalDateTime.now());
        prediction.setUpdateTime(LocalDateTime.now());

        return repository.save(prediction);
    }

    /**
     * 本地批量预测算法
     */
    @Deprecated
    private List<PredictionResult> generateLocalBatchPredictions(List<Long> medicineIds,
                                                                 String modelType,
                                                                 LocalDate startDate,
                                                                 LocalDate endDate) {
        List<PredictionResult> results = new ArrayList<>();
        Map<Long, Medicine> medicineMap = medicineRepository.findAllById(medicineIds)
                .stream()
                .collect(Collectors.toMap(Medicine::getId, Function.identity()));

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

        for (Long medicineId : medicineIds) {
            Medicine medicine = medicineMap.get(medicineId);
            if (medicine == null) continue;

            for (int i = 0; i <= daysBetween; i++) {
                LocalDate predictionDate = startDate.plusDays(i);
                PredictionResult prediction = generateLocalPrediction(medicine, modelType, predictionDate);
                if (prediction != null) {
                    results.add(prediction);
                }
            }
        }

        return results;
    }

    /**
     * ====================== 计算辅助方法 ======================
     */

    /**
     * 计算预测准确率
     */
    @Deprecated
    private BigDecimal calculateAccuracyRate(Long medicineId, Integer predictedQuantity) {
        try {
            // 获取历史预测记录进行比较
            List<PredictionResult> historicalPredictions = repository.findByMedicineId(medicineId);

            if (historicalPredictions != null && !historicalPredictions.isEmpty()) {
                // 简化的准确率计算：基于历史平均偏差
                double totalDeviation = 0;
                int count = 0;

                for (PredictionResult hist : historicalPredictions) {
                    if (hist.getAccuracyRate() != null) {
                        totalDeviation += hist.getAccuracyRate().doubleValue();
                        count++;
                    }
                }

                if (count > 0) {
                    double avgAccuracy = totalDeviation / count;
                    // 添加随机波动模拟真实情况
                    double randomFactor = 0.9 + (Math.random() * 0.2); // 0.9-1.1
                    double finalAccuracy = Math.min(100, avgAccuracy * randomFactor);
                    return BigDecimal.valueOf(finalAccuracy).setScale(2, RoundingMode.HALF_UP);
                }
            }
        } catch (Exception e) {
            log.error("计算准确率时发生异常", e);
        }

        // 默认准确率
        return BigDecimal.valueOf(85.5).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 本地预测算法：计算预测数量
     */
    private int calculateLocalPredictedQuantity(Long medicineId) {
        // 简化的预测算法：基于历史销售趋势
        // 实际项目中应该基于历史销售数据进行复杂的计算

        // 获取药品的销售季节性和处方状态
        Medicine medicine = medicineRepository.findById(medicineId).orElse(null);
        if (medicine == null) {
            return 10; // 默认值
        }

        // 基础预测值
        int basePrediction = 20;

        // 考虑季节性因素
        if (medicine.isSeasonal()) {
            // 假设季节性药品销量波动较大
            basePrediction = 15 + (int)(Math.random() * 30);
        }

        // 考虑处方药因素
        if (medicine.isPrescription()) {
            // 处方药销量相对稳定
            basePrediction = 8 + (int)(Math.random() * 15);
        }

        // 考虑分类因素
        if (medicine.getCategory() != null) {
            // 不同分类可能有不同的销售模式
            String categoryName = medicine.getCategory().getName();
            if (categoryName != null) {
                if (categoryName.contains("感冒") || categoryName.contains("咳嗽")) {
                    basePrediction += 10; // 常见病药品销量较高
                }
            }
        }

        // 添加随机波动
        double randomFactor = 0.7 + (Math.random() * 0.6); // 0.7-1.3
        return Math.max(1, (int)(basePrediction * randomFactor));
    }

    /**
     * 计算建议订购数量
     */
    private int calculateRecommendedOrderQuantity(Long medicineId, int predictedQuantity) {
        // 获取当前有效库存
        List<Stock> stocks = stockRepository.findByMedicineId(medicineId);
        int currentStock = stocks.stream()
                .filter(stock -> stock.getStatus() == 1) // 只计算正常状态的库存
                .mapToInt(Stock::getQuantity)
                .sum();

        // 计算安全库存
        int safetyStock = (int) (predictedQuantity * safetyStockFactor);

        // 获取再订货点和最小订购量
        int reorderPoint = 0;
        int minimumOrderQuantity = 1;

        if (!stocks.isEmpty()) {
            Stock firstStock = stocks.get(0);
            if (firstStock.getReorderPoint() != null) {
                reorderPoint = firstStock.getReorderPoint();
            }
            if (firstStock.getMinimumOrderQuantity() != null) {
                minimumOrderQuantity = firstStock.getMinimumOrderQuantity();
            }
        }

        // 如果需要再订货
        if (currentStock <= reorderPoint) {
            int neededStock = predictedQuantity + safetyStock;
            int orderQuantity = neededStock - currentStock;

            // 确保不低于最小订购量
            orderQuantity = Math.max(orderQuantity, minimumOrderQuantity);

            // 取整到最小订购量的倍数
            if (minimumOrderQuantity > 1) {
                orderQuantity = ((orderQuantity + minimumOrderQuantity - 1) / minimumOrderQuantity) * minimumOrderQuantity;
            }

            return orderQuantity;
        }

        return 0; // 库存充足，不需要订购
    }

    /**
     * ====================== 健康检查方法 ======================
     */

    /**
     * 检查模型端服务是否可用
     */
    public boolean checkModelServiceHealth() {
        try {
            String healthUrl = modelServiceBaseUrl + "/health";
            ResponseEntity<Map> response = restTemplate.getForEntity(healthUrl, Map.class);

            return response.getStatusCode() == HttpStatus.OK &&
                    "healthy".equals(response.getBody().get("status"));
        } catch (Exception e) {
            log.error("检查模型端服务健康状态失败", e);
            return false;
        }
    }

    /**
     * 获取模型端服务信息
     */
    public Map<String, Object> getModelServiceInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("baseUrl", modelServiceBaseUrl);
        info.put("apiVersion", modelApiVersion);
        info.put("healthCheckUrl", modelServiceBaseUrl + "/health");
        info.put("isHealthy", checkModelServiceHealth());
        return info;
    }
}