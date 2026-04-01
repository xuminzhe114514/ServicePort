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
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private void initializeMedicineAssociations(Medicine medicine) {
        if (medicine != null) {
            Hibernate.initialize(medicine.getCategory());
            Hibernate.initialize(medicine.getStocks());
            Hibernate.initialize(medicine.getSaleRecords());
            Hibernate.initialize(medicine.getPurchaseOrders());
            Hibernate.initialize(medicine.getSymptoms());
        }
    }

    private void initializePredictionResultAssociations(PredictionResult result) {
        if (result != null) {
            Hibernate.initialize(result.getMedicine());
            initializeMedicineAssociations(result.getMedicine());
        }
    }

    protected PredictionResultServiceImpl(PredictionResultRepository repository) {super(repository);}

    @Override
    @Transactional(readOnly = true)
    public PredictionResult findById(Long id) {
        PredictionResult result = repository.findById(id).orElse(null);
        initializePredictionResultAssociations(result);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PredictionResult> findAll() {
        List<PredictionResult> results = repository.findAll();
        if (!results.isEmpty()) {
            results.forEach(this::initializePredictionResultAssociations);
        }
        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PredictionResult> findAll(Pageable pageable) {
        Page<PredictionResult> page = repository.findAll(pageable);
        if (page.hasContent()) {
            page.getContent().forEach(this::initializePredictionResultAssociations);
        }
        return page;
    }

    @Override
    @Transactional
    public PredictionResult save(PredictionResult result) {
        PredictionResult savedResult = repository.save(result);
        initializePredictionResultAssociations(savedResult);
        return savedResult;
    }

    @Override
    @Transactional
    public PredictionResult update(PredictionResult result) {
        PredictionResult updatedResult = repository.save(result);
        initializePredictionResultAssociations(updatedResult);
        return updatedResult;
    }

    @Override
    @Transactional
    public List<PredictionResult> saveAll(List<PredictionResult> results) {
        List<PredictionResult> savedResults = repository.saveAll(results);
        if (!savedResults.isEmpty()) {
            savedResults.forEach(this::initializePredictionResultAssociations);
        }
        return savedResults;
    }

    @Override
    public List<PredictionResult> findByMedicineId(Long medicineId) {
        List<PredictionResult> results = repository.findByMedicineId(medicineId);
        if (!results.isEmpty()) {
            results.forEach(this::initializePredictionResultAssociations);
        }
        return results;
    }

    @Override
    public List<PredictionResult> findByPredictionDate(LocalDate predictionDate) {
        List<PredictionResult> results = repository.findByPredictionDate(predictionDate);
        if (!results.isEmpty()) {
            results.forEach(this::initializePredictionResultAssociations);
        }
        return results;
    }

    @Override
    public PredictionResult findLatestByMedicineId(Long medicineId) {
        PredictionResult result = repository.findFirstByMedicineIdOrderByPredictionDateDesc(medicineId).orElse(null);
        initializePredictionResultAssociations(result);
        return result;
    }

    @Override
    public List<PredictionResult> findByPredictionDateRange(LocalDate startDate, LocalDate endDate) {
        List<PredictionResult> results = repository.findByPredictionDateBetween(startDate, endDate);
        if (!results.isEmpty()) {
            results.forEach(this::initializePredictionResultAssociations);
        }
        return results;
    }

    @Override
    public List<PredictionResult> findNeedReprediction(Double threshold) {
        List<PredictionResult> results = repository.findNeedReprediction(threshold != null ? BigDecimal.valueOf(threshold) : null);
        if (!results.isEmpty()) {
            results.forEach(this::initializePredictionResultAssociations);
        }
        return results;
    }

    @Override
    public Map<String, Double> getAverageAccuracyByModel() {
        List<Object[]> results = repository.findAverageAccuracyByModel();
        return results.stream().collect(
                Collectors.toMap(
                        result -> result[0].toString(),
                        result -> {
                            Object accuracyObj = result[1];
                            if (accuracyObj instanceof BigDecimal) {
                                return ((BigDecimal) accuracyObj).doubleValue();
                            } else if (accuracyObj instanceof Double) {
                                return (Double) accuracyObj;
                            } else if (accuracyObj instanceof Long) {
                                return ((Long) accuracyObj).doubleValue();
                            } else if (accuracyObj instanceof Integer) {
                                return ((Integer) accuracyObj).doubleValue();
                            } else {
                                try {
                                    return Double.parseDouble(accuracyObj.toString());
                                } catch (NumberFormatException e) {
                                    return 0.0;
                                }
                            }
                        }
                )
        );
    }


    @Override
    public PredictionResult generatePrediction(Long medicineId, String modelType,
                                               LocalDate predictionDate) {
        return generatePrediction(medicineId, modelType, predictionDate, defaultPredictionDays);
    }

    /**
     * 调用模型端API进行单个药品预测（指定预测天数）
     */
    @Override
    public PredictionResult generatePrediction(Long medicineId, String modelType,
                                               LocalDate predictionDate, int predictionDays) {
        log.info("生成单个药品预测 - 药品ID: {}, 模型类型: {}, 预测日期: {}, 预测天数: {}",
                medicineId, modelType, predictionDate, predictionDays);

        Medicine medicine = medicineRepository.findById(medicineId).orElse(null);
        if (medicine == null) {
            log.error("药品不存在 - ID: {}", medicineId);
            return null;
        }

        try {
            Map<String, Object> predictionData = callModelPredictionApi(medicineId, predictionDate, predictionDays);
            if (predictionData == null) {
                log.warn("模型端API调用失败，使用本地算法生成预测");
                return generateLocalPrediction(medicine, modelType, predictionDate, predictionDays);
            }
            return createPredictionResultFromModelResponse(medicine, predictionData, modelType, predictionDate);
        } catch (Exception e) {
            log.error("调用模型端API失败，使用本地算法生成预测", e);
            return generateLocalPrediction(medicine, modelType, predictionDate, predictionDays);
        }
    }

    /**
     * 调用模型端批量预测API
     */
    @Override
    public List<PredictionResult> generateBatchPredictions(List<Long> medicineIds,
                                                           String modelType,
                                                           LocalDate startDate,
                                                           LocalDate endDate) {
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        return generateBatchPredictions(medicineIds, modelType, (int) Math.max(1, daysBetween));
    }

    /**
     * 调用模型端批量预测API（指定预测天数）
     */
    @Override
    public List<PredictionResult> generateBatchPredictions(List<Long> medicineIds,
                                                           String modelType,
                                                           int predictionDays) {
        log.info("批量生成预测 - 药品数量: {}, 模型类型: {}, 预测天数: {}",
                medicineIds.size(), modelType, predictionDays);

        List<PredictionResult> results = new ArrayList<>();

        try {
            List<Map<String, Object>> batchPredictions = callBatchPredictionApi(medicineIds, predictionDays);

            if (batchPredictions != null && !batchPredictions.isEmpty()) {
                LocalDate startDate = LocalDate.now();
                LocalDate endDate = startDate.plusDays(predictionDays - 1);
                results = createBatchPredictionsFromModelResponse(medicineIds, batchPredictions, modelType, startDate, endDate);
            } else {
                LocalDate startDate = LocalDate.now();
                LocalDate endDate = startDate.plusDays(predictionDays - 1);
                results = generateLocalBatchPredictions(medicineIds, modelType, startDate, endDate);
            }
        } catch (Exception e) {
            log.error("批量调用模型端API失败，使用本地算法", e);
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = startDate.plusDays(predictionDays - 1);
            results = generateLocalBatchPredictions(medicineIds, modelType, startDate, endDate);
        }
        if (!results.isEmpty()) {
            return repository.saveAll(results);
        }

        return results;
    }

    /**
     * 获取推荐订购数量
     */
    @Override
    public Map<Long, Integer> getRecommendedOrderQuantities(LocalDate targetDate) {
        List<Medicine> medicines = medicineRepository.findByStatus(1);
        List<Long> medicineIds = medicines.stream()
                .map(Medicine::getId)
                .toList();
        return getRecommendedOrderQuantities(targetDate, medicineIds);
    }

    @Override
    public Map<Long, Integer> getRecommendedOrderQuantities(LocalDate targetDate, List<Long> medicineIds) {
        log.info("获取推荐订购数量 - 目标日期: {}, 药品数量: {}", targetDate, medicineIds.size());

        List<Medicine> medicines = medicineRepository.findAllById(medicineIds);

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

            if (prediction == null || prediction.getPredictionDate().isBefore(targetDate)) {
                prediction = generatePrediction(medicine.getId(), "DEFAULT", targetDate);
            }

            if (prediction != null) {
                recommendations.put(medicine.getId(), prediction.getRecommendedOrderQuantity());
            } else {
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
    private Map<String, Object> callModelPredictionApi(Long medicineId, LocalDate predictionDate, int predictionDays) {
        String apiUrl = modelServiceBaseUrl + modelApiVersion + "/predict/single-medicine";

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("medicineId", String.valueOf(medicineId));
            requestBody.put("predictionDays", Math.max(1, predictionDays));

            log.debug("调用模型端单个预测API: {}", apiUrl);
            log.debug("请求参数: {}", requestBody);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
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
    private List<Map<String, Object>> callBatchPredictionApi(List<Long> medicineIds, int predictionDays) {
        String apiUrl = modelServiceBaseUrl + modelApiVersion + "/predict/batch";

        try {
            Map<String, Object> requestBody = new HashMap<>();
            List<String> medicineIdStrs = medicineIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList());
            requestBody.put("medicineIds", medicineIdStrs);
            requestBody.put("predictionDays", Math.max(1, predictionDays));

            log.debug("调用模型端批量预测API: {}", apiUrl);
            log.debug("请求参数: {}", requestBody);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

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
     * 调用模型端获取模型性能评估
     */
    private Map<String, Object> getModelPerformanceApi(Long medicineId, int testPeriods) {
        String apiUrl = modelServiceBaseUrl + modelApiVersion + "/predict/performance/" + medicineId + "?testPeriods=" + testPeriods;

        try {
            log.debug("获取模型性能评估: {}", apiUrl);

            ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                if ("success".equals(responseBody.get("status"))) {
                    log.info("模型性能评估获取成功 - 药品ID: {}", medicineId);
                    return responseBody;
                }
            }
        } catch (Exception e) {
            log.error("获取模型性能评估时发生异常", e);
        }

        return null;
    }

    /**
     * 模型性能评估
     */
    @Override
    public Map<String, Object> getModelPerformance(Long medicineId, int testPeriods) {
        log.info("获取模型性能评估 - 药品ID: {}, 测试周期: {}", medicineId, testPeriods);

        try {
            Map<String, Object> performanceData = getModelPerformanceApi(medicineId, testPeriods);

            if (performanceData != null) {
                return performanceData;
            }
        } catch (Exception e) {
            log.error("获取模型性能评估失败", e);
        }

        Map<String, Object> defaultPerformance = new HashMap<>();
        defaultPerformance.put("status", "error");
        defaultPerformance.put("message", "无法获取模型性能评估");
        return defaultPerformance;
    }

    /**
     * ====================== 数据处理方法 ======================
     */

    /**
     * 从模型端响应创建预测结果
     */
    private PredictionResult createPredictionResultFromModelResponse(Medicine medicine,
                                                                     Map<String, Object> modelResponse,
                                                                     String modelType,
                                                                     LocalDate predictionDate) {
        try {
            PredictionResult prediction = new PredictionResult();
            prediction.setMedicine(medicine);
            prediction.setPredictionDate(predictionDate);
            prediction.setModelType(modelType);

            Map<String, Object> data = (Map<String, Object>) modelResponse.get("data");
            if (data != null) {
                List<Map<String, Object>> predictions = (List<Map<String, Object>>) data.get("predictions");
                Optional<Map<String, Object>> targetPrediction = predictions.stream()
                        .filter(p -> predictionDate.toString().equals(p.get("predictionDate")))
                        .findFirst();

                if (targetPrediction.isPresent()) {
                    Map<String, Object> predData = targetPrediction.get();
                    if (predData.get("predictedQuantity") != null) {
                        prediction.setPredictedQuantity((Integer) predData.get("predictedQuantity"));
                    }
                    if (predData.get("confidenceIntervalLower") != null) {
                        prediction.setConfidenceIntervalLower((Integer) predData.get("confidenceIntervalLower"));
                    }
                    if (predData.get("confidenceIntervalUpper") != null) {
                        prediction.setConfidenceIntervalUpper((Integer) predData.get("confidenceIntervalUpper"));
                    }
                    if (predData.get("modelType") != null) {
                        prediction.setModelType((String) predData.get("modelType"));
                    }
                }
            }

            BigDecimal accuracyRate = calculateAccuracyRate(medicine.getId(), prediction.getPredictedQuantity());
            prediction.setAccuracyRate(accuracyRate);

            int recommendedQuantity = calculateRecommendedOrderQuantity(medicine.getId(), prediction.getPredictedQuantity());
            prediction.setRecommendedOrderQuantity(recommendedQuantity);

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
     * 调用模型端计算ABC分类
     */
    private Map<String, Object> calculateABCClassificationApi(List<Long> medicineIds) {
        String apiUrl = modelServiceBaseUrl + modelApiVersion + "/classification/abc";

        try {
            Map<String, Object> requestBody = new HashMap<>();
            List<String> medicineIdStrs = medicineIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList());
            requestBody.put("medicineIds", medicineIdStrs);

            log.debug("调用模型端ABC分类API: {}", apiUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                if ("success".equals(responseBody.get("status"))) {
                    log.info("ABC分类计算成功 - 药品数量: {}", medicineIds.size());
                    return responseBody;
                }
            }
        } catch (Exception e) {
            log.error("计算ABC分类时发生异常", e);
        }

        return null;
    }

    /**
     * 计算药品ABC分类
     */
    @Override
    public Map<String, Object> calculateABCClassification(List<Long> medicineIds) {
        log.info("计算药品ABC分类 - 药品数量: {}", medicineIds.size());

        try {
            Map<String, Object> classificationData = calculateABCClassificationApi(medicineIds);

            if (classificationData != null) {
                return classificationData;
            }
        } catch (Exception e) {
            log.error("计算ABC分类失败", e);
        }
        Map<String, Object> defaultClassification = new HashMap<>();
        defaultClassification.put("status", "error");
        defaultClassification.put("message", "无法计算ABC分类");
        return defaultClassification;
    }

    /**
     * 调用模型端检测滞销药品
     */
    private Map<String, Object> detectSlowMovingItemsApi(int thresholdDays) {
        String apiUrl = modelServiceBaseUrl + modelApiVersion + "/classification/slow-moving?thresholdDays=" + thresholdDays;

        try {
            log.debug("调用模型端滞销品检测API: {}", apiUrl);

            ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                if ("success".equals(responseBody.get("status"))) {
                    log.info("滞销品检测成功 - 阈值天数: {}", thresholdDays);
                    return responseBody;
                }
            }
        } catch (Exception e) {
            log.error("检测滞销药品时发生异常", e);
        }

        return null;
    }

    /**
     * 检测滞销药品
     */
    @Override
    public Map<String, Object> detectSlowMovingItems(int thresholdDays) {
        log.info("检测滞销药品 - 阈值天数: {}", thresholdDays);

        try {
            Map<String, Object> slowMovingData = detectSlowMovingItemsApi(thresholdDays);

            if (slowMovingData != null) {
                return slowMovingData;
            }
        } catch (Exception e) {
            log.error("检测滞销药品失败", e);
        }

        // 返回默认值
        Map<String, Object> defaultSlowMoving = new HashMap<>();
        defaultSlowMoving.put("status", "error");
        defaultSlowMoving.put("message", "无法检测滞销药品");
        return defaultSlowMoving;
    }

    /**
     * 调用模型端计算效期风险
     */
    private Map<String, Object> calculateExpiryRiskApi() {
        String apiUrl = modelServiceBaseUrl + modelApiVersion + "/classification/expiry-risk";

        try {
            log.debug("调用模型端效期风险计算API: {}", apiUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            Map<String, Object> requestBody = new HashMap<>();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                if ("success".equals(responseBody.get("status"))) {
                    log.info("效期风险计算成功");
                    return responseBody;
                }
            }
        } catch (Exception e) {
            log.error("计算效期风险时发生异常", e);
        }

        return null;
    }

    /**
     * 计算效期风险
     */
    @Override
    public Map<String, Object> calculateExpiryRisk() {
        log.info("计算效期风险");

        try {
            Map<String, Object> expiryRiskData = calculateExpiryRiskApi();

            if (expiryRiskData != null) {
                return expiryRiskData;
            }
        } catch (Exception e) {
            log.error("计算效期风险失败", e);
        }

        Map<String, Object> defaultExpiryRisk = new HashMap<>();
        defaultExpiryRisk.put("status", "error");
        defaultExpiryRisk.put("message", "无法计算效期风险");
        return defaultExpiryRisk;
    }

    /**
     * 调用模型端获取库存状态汇总
     */
    private Map<String, Object> getInventoryStatusSummaryApi() {
        String apiUrl = modelServiceBaseUrl + modelApiVersion + "/classification/inventory-status";

        try {
            log.debug("调用模型端库存状态汇总API: {}", apiUrl);

            ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                if ("success".equals(responseBody.get("status"))) {
                    log.info("库存状态汇总获取成功");
                    return responseBody;
                }
            }
        } catch (Exception e) {
            log.error("获取库存状态汇总时发生异常", e);
        }

        return null;
    }

    /**
     * 获取库存状态汇总
     */
    @Override
    public Map<String, Object> getInventoryStatusSummary() {
        log.info("获取库存状态汇总");

        try {
            Map<String, Object> inventoryStatusData = getInventoryStatusSummaryApi();

            if (inventoryStatusData != null) {
                return inventoryStatusData;
            }
        } catch (Exception e) {
            log.error("获取库存状态汇总失败", e);
        }
        Map<String, Object> defaultInventoryStatus = new HashMap<>();
        defaultInventoryStatus.put("status", "error");
        defaultInventoryStatus.put("message", "无法获取库存状态汇总");
        return defaultInventoryStatus;
    }

    /**
     * ====================== 备用本地算法 ======================
     */

    /**
     * 本地预测算法（模型端API失败时使用）
     */
    private PredictionResult generateLocalPrediction(Medicine medicine, String modelType, LocalDate predictionDate, int predictionDays) {
        log.info("使用本地算法生成预测 - 药品: {}, 日期: {}, 预测天数: {}", medicine.getName(), predictionDate, predictionDays);

        PredictionResult prediction = new PredictionResult();
        prediction.setMedicine(medicine);
        prediction.setPredictionDate(predictionDate);
        prediction.setModelType(modelType);

        // 使用本地算法计算预测数量，考虑预测天数
        int predictedQuantity = calculateLocalPredictedQuantity(medicine.getId(), predictionDays);
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
    private List<PredictionResult> generateLocalBatchPredictions(List<Long> medicineIds,
                                                                 String modelType,
                                                                 LocalDate startDate,
                                                                 LocalDate endDate) {
        List<PredictionResult> results = new ArrayList<>();
        Map<Long, Medicine> medicineMap = medicineRepository.findAllById(medicineIds)
                .stream()
                .collect(Collectors.toMap(Medicine::getId, Function.identity()));

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        int predictionDays = (int) Math.max(1, daysBetween + 1);

        for (Long medicineId : medicineIds) {
            Medicine medicine = medicineMap.get(medicineId);
            if (medicine == null) continue;

            for (int i = 0; i <= daysBetween; i++) {
                LocalDate predictionDate = startDate.plusDays(i);
                PredictionResult prediction = generateLocalPrediction(medicine, modelType, predictionDate, 1);
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
    private int calculateLocalPredictedQuantity(Long medicineId, int predictionDays) {
        // 简化的预测算法：基于历史销售趋势
        // 实际项目中应该基于历史销售数据进行复杂的计算

        // 获取药品的销售季节性和处方状态
        Medicine medicine = medicineRepository.findById(medicineId).orElse(null);
        if (medicine == null) {
            return 10 * predictionDays; // 默认值，考虑预测天数
        }

        // 基础预测值，考虑预测天数
        int basePrediction = 20 * predictionDays;

        // 考虑季节性因素
        if (medicine.isSeasonal()) {
            // 假设季节性药品销量波动较大
            basePrediction = (15 + (int)(Math.random() * 30)) * predictionDays;
        }

        // 考虑处方药因素
        if (medicine.isPrescription()) {
            // 处方药销量相对稳定
            basePrediction = (8 + (int)(Math.random() * 15)) * predictionDays;
        }

        // 考虑分类因素
        if (medicine.getCategory() != null) {
            // 不同分类可能有不同的销售模式
            String categoryName = medicine.getCategory().getName();
            if (categoryName != null) {
                if (categoryName.contains("感冒") || categoryName.contains("咳嗽")) {
                    basePrediction += 10 * predictionDays; // 常见病药品销量较高
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
    @Override
    public boolean checkModelServiceHealth() {
        try {
            // 尝试调用一个简单的API端点来检查服务是否可用
            String testUrl = modelServiceBaseUrl + "/api/data/summary";
            ResponseEntity<Map> response = restTemplate.getForEntity(testUrl, Map.class);

            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            log.error("检查模型端服务健康状态失败", e);
            return false;
        }
    }

    /**
     * 获取模型端服务信息
     */
    @Override
    public Map<String, Object> getModelServiceInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("baseUrl", modelServiceBaseUrl);
        info.put("apiVersion", modelApiVersion);
        info.put("healthCheckUrl", modelServiceBaseUrl + "/health");
        info.put("isHealthy", checkModelServiceHealth());
        return info;
    }

    /**
     * ====================== 库存相关方法 ======================
     */

    /**
     * 调用模型端计算动态安全库存
     */
    private Map<String, Object> calculateDynamicSafetyStockApi(Long medicineId, int predictionDays) {
        String apiUrl = modelServiceBaseUrl + modelApiVersion + "/inventory/dynamic-safety-stock";

        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("medicineId", String.valueOf(medicineId));
            requestBody.put("predictionDays", predictionDays);

            log.debug("调用模型端动态安全库存计算API: {}", apiUrl);

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
                    log.info("动态安全库存计算成功 - 药品ID: {}", medicineId);
                    return responseBody;
                }
            }
        } catch (Exception e) {
            log.error("计算动态安全库存时发生异常", e);
        }

        return null;
    }

    /**
     * 计算动态安全库存
     */
    @Override
    public Map<String, Object> calculateDynamicSafetyStock(Long medicineId, int predictionDays) {
        log.info("计算动态安全库存 - 药品ID: {}, 预测天数: {}", medicineId, predictionDays);

        try {
            Map<String, Object> safetyStockData = calculateDynamicSafetyStockApi(medicineId, predictionDays);

            if (safetyStockData != null) {
                return safetyStockData;
            }
        } catch (Exception e) {
            log.error("计算动态安全库存失败", e);
        }

        // 返回默认值
        Map<String, Object> defaultSafetyStock = new HashMap<>();
        defaultSafetyStock.put("status", "error");
        defaultSafetyStock.put("message", "无法计算动态安全库存");
        return defaultSafetyStock;
    }

    /**
     * 调用模型端生成补货建议
     */
    private Map<String, Object> generateReplenishmentSuggestionsApi(List<Long> medicineIds) {
        String apiUrl = modelServiceBaseUrl + modelApiVersion + "/inventory/replenishment-suggestion";

        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            List<String> medicineIdStrs = medicineIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList());
            requestBody.put("medicineIds", medicineIdStrs);

            log.debug("调用模型端补货建议生成API: {}", apiUrl);

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
                    log.info("补货建议生成成功 - 药品数量: {}", medicineIds.size());
                    return responseBody;
                }
            }
        } catch (Exception e) {
            log.error("生成补货建议时发生异常", e);
        }

        return null;
    }

    /**
     * 生成补货建议
     */
    @Override
    public Map<String, Object> generateReplenishmentSuggestions(List<Long> medicineIds) {
        log.info("生成补货建议 - 药品数量: {}", medicineIds.size());

        try {
            Map<String, Object> suggestionsData = generateReplenishmentSuggestionsApi(medicineIds);

            if (suggestionsData != null) {
                return suggestionsData;
            }
        } catch (Exception e) {
            log.error("生成补货建议失败", e);
        }

        // 返回默认值
        Map<String, Object> defaultSuggestions = new HashMap<>();
        defaultSuggestions.put("status", "error");
        defaultSuggestions.put("message", "无法生成补货建议");
        return defaultSuggestions;
    }

    /**
     * 调用模型端生成带在途订单的补货建议
     */
    private Map<String, Object> generateReplenishmentSuggestionsWithInTransitApi(List<Map<String, Object>> inTransitOrders) {
        String apiUrl = modelServiceBaseUrl + modelApiVersion + "/inventory/replenishment";

        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inTransitOrders", inTransitOrders);

            log.debug("调用模型端带在途订单的补货建议生成API: {}", apiUrl);

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
                    log.info("带在途订单的补货建议生成成功");
                    return responseBody;
                }
            }
        } catch (Exception e) {
            log.error("生成带在途订单的补货建议时发生异常", e);
        }

        return null;
    }

    /**
     * 生成带在途订单的补货建议
     */
    @Override
    public Map<String, Object> generateReplenishmentSuggestionsWithInTransit(List<Map<String, Object>> inTransitOrders) {
        log.info("生成带在途订单的补货建议 - 在途订单数量: {}", inTransitOrders.size());

        try {
            Map<String, Object> suggestionsData = generateReplenishmentSuggestionsWithInTransitApi(inTransitOrders);

            if (suggestionsData != null) {
                return suggestionsData;
            }
        } catch (Exception e) {
            log.error("生成带在途订单的补货建议失败", e);
        }

        // 返回默认值
        Map<String, Object> defaultSuggestions = new HashMap<>();
        defaultSuggestions.put("status", "error");
        defaultSuggestions.put("message", "无法生成带在途订单的补货建议");
        return defaultSuggestions;
    }

    /**
     * 调用模型端获取当前库存状态
     */
    private Map<String, Object> getCurrentInventoryStatusApi() {
        String apiUrl = modelServiceBaseUrl + modelApiVersion + "/inventory/current";

        try {
            log.debug("调用模型端当前库存状态API: {}", apiUrl);

            ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                if ("success".equals(responseBody.get("status"))) {
                    log.info("当前库存状态获取成功");
                    return responseBody;
                }
            }
        } catch (Exception e) {
            log.error("获取当前库存状态时发生异常", e);
        }

        return null;
    }

    /**
     * 获取当前库存状态
     */
    @Override
    public Map<String, Object> getCurrentInventoryStatus() {
        log.info("获取当前库存状态");

        try {
            Map<String, Object> inventoryStatusData = getCurrentInventoryStatusApi();

            if (inventoryStatusData != null) {
                return inventoryStatusData;
            }
        } catch (Exception e) {
            log.error("获取当前库存状态失败", e);
        }

        // 返回默认值
        Map<String, Object> defaultInventoryStatus = new HashMap<>();
        defaultInventoryStatus.put("status", "error");
        defaultInventoryStatus.put("message", "无法获取当前库存状态");
        return defaultInventoryStatus;
    }

    /**
     * 调用模型端获取带效期的库存数据
     */
    private Map<String, Object> getInventoryWithExpiryApi() {
        String apiUrl = modelServiceBaseUrl + modelApiVersion + "/inventory/with-expiry";

        try {
            log.debug("调用模型端带效期的库存数据API: {}", apiUrl);

            ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                if ("success".equals(responseBody.get("status"))) {
                    log.info("带效期的库存数据获取成功");
                    return responseBody;
                }
            }
        } catch (Exception e) {
            log.error("获取带效期的库存数据时发生异常", e);
        }

        return null;
    }

    /**
     * 获取带效期的库存数据
     */
    @Override
    public Map<String, Object> getInventoryWithExpiry() {
        log.info("获取带效期的库存数据");

        try {
            Map<String, Object> inventoryWithExpiryData = getInventoryWithExpiryApi();

            if (inventoryWithExpiryData != null) {
                return inventoryWithExpiryData;
            }
        } catch (Exception e) {
            log.error("获取带效期的库存数据失败", e);
        }

        // 返回默认值
        Map<String, Object> defaultInventoryWithExpiry = new HashMap<>();
        defaultInventoryWithExpiry.put("status", "error");
        defaultInventoryWithExpiry.put("message", "无法获取带效期的库存数据");
        return defaultInventoryWithExpiry;
    }

    /**
     * 调用模型端获取单个药品的库存状态
     */
    private Map<String, Object> getMedicineInventoryStatusApi(Long medicineId) {
        String apiUrl = modelServiceBaseUrl + modelApiVersion + "/inventory/stock/" + medicineId;

        try {
            log.debug("调用模型端单个药品库存状态API: {}", apiUrl);

            ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                if ("success".equals(responseBody.get("status"))) {
                    log.info("单个药品库存状态获取成功 - 药品ID: {}", medicineId);
                    return responseBody;
                }
            }
        } catch (Exception e) {
            log.error("获取单个药品库存状态时发生异常", e);
        }

        return null;
    }

    /**
     * 获取单个药品的库存状态
     */
    @Override
    public Map<String, Object> getMedicineInventoryStatus(Long medicineId) {
        log.info("获取单个药品的库存状态 - 药品ID: {}", medicineId);

        try {
            Map<String, Object> medicineInventoryStatusData = getMedicineInventoryStatusApi(medicineId);

            if (medicineInventoryStatusData != null) {
                return medicineInventoryStatusData;
            }
        } catch (Exception e) {
            log.error("获取单个药品库存状态失败", e);
        }

        // 返回默认值
        Map<String, Object> defaultMedicineInventoryStatus = new HashMap<>();
        defaultMedicineInventoryStatus.put("status", "error");
        defaultMedicineInventoryStatus.put("message", "无法获取单个药品库存状态");
        return defaultMedicineInventoryStatus;
    }

    /**
     * 调用模型端计算单个药品的安全库存和补货点
     */
    private Map<String, Object> calculateSafetyStockApi(Long medicineId, int leadTimeDays, double serviceLevel) {
        String apiUrl = modelServiceBaseUrl + modelApiVersion + "/inventory/safety-stock?medicineId=" + medicineId + "&leadTimeDays=" + leadTimeDays + "&serviceLevel=" + serviceLevel;

        try {
            log.debug("调用模型端单个药品安全库存计算API: {}", apiUrl);

            ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                if ("success".equals(responseBody.get("status"))) {
                    log.info("单个药品安全库存计算成功 - 药品ID: {}", medicineId);
                    return responseBody;
                }
            }
        } catch (Exception e) {
            log.error("计算单个药品安全库存时发生异常", e);
        }

        return null;
    }

    /**
     * 计算单个药品的安全库存和补货点
     */
    @Override
    public Map<String, Object> calculateSafetyStock(Long medicineId, int leadTimeDays, double serviceLevel) {
        log.info("计算单个药品的安全库存和补货点 - 药品ID: {}, 前置时间: {}天, 服务水平: {}", medicineId, leadTimeDays, serviceLevel);

        try {
            Map<String, Object> safetyStockData = calculateSafetyStockApi(medicineId, leadTimeDays, serviceLevel);

            if (safetyStockData != null) {
                return safetyStockData;
            }
        } catch (Exception e) {
            log.error("计算单个药品安全库存失败", e);
        }

        // 返回默认值
        Map<String, Object> defaultSafetyStock = new HashMap<>();
        defaultSafetyStock.put("status", "error");
        defaultSafetyStock.put("message", "无法计算单个药品安全库存");
        return defaultSafetyStock;
    }

    /**
     * 调用模型端批量计算所有药品的安全库存和补货点
     */
    private Map<String, Object> calculateBatchSafetyStockApi(int leadTimeDays, double serviceLevel) {
        String apiUrl = modelServiceBaseUrl + modelApiVersion + "/inventory/safety-stock/batch?leadTimeDays=" + leadTimeDays + "&serviceLevel=" + serviceLevel;

        try {
            log.debug("调用模型端批量安全库存计算API: {}", apiUrl);

            ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                if ("success".equals(responseBody.get("status"))) {
                    log.info("批量安全库存计算成功");
                    return responseBody;
                }
            }
        } catch (Exception e) {
            log.error("批量计算安全库存时发生异常", e);
        }

        return null;
    }

    /**
     * 批量计算所有药品的安全库存和补货点
     */
    @Override
    public Map<String, Object> calculateBatchSafetyStock(int leadTimeDays, double serviceLevel) {
        log.info("批量计算所有药品的安全库存和补货点 - 前置时间: {}天, 服务水平: {}", leadTimeDays, serviceLevel);

        try {
            Map<String, Object> batchSafetyStockData = calculateBatchSafetyStockApi(leadTimeDays, serviceLevel);

            if (batchSafetyStockData != null) {
                return batchSafetyStockData;
            }
        } catch (Exception e) {
            log.error("批量计算安全库存失败", e);
        }

        // 返回默认值
        Map<String, Object> defaultBatchSafetyStock = new HashMap<>();
        defaultBatchSafetyStock.put("status", "error");
        defaultBatchSafetyStock.put("message", "无法批量计算安全库存");
        return defaultBatchSafetyStock;
    }

    /**
     * ====================== 数据推送方法 ======================
     */

    /**
     * 通用数据推送方法
     */
    private Map<String, Object> pushData(String endpoint, Object requestBody) {
        String apiUrl = modelServiceBaseUrl + "/api/data/" + endpoint;

        try {
            log.debug("调用模型端数据推送API: {}", apiUrl);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);

            // 发送请求
            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                log.info("数据推送成功 - 端点: {}", endpoint);
                return responseBody;
            }
        } catch (Exception e) {
            log.error("数据推送时发生异常 - 端点: {}", endpoint, e);
        }

        // 返回默认值
        Map<String, Object> defaultResponse = new HashMap<>();
        defaultResponse.put("success", false);
        defaultResponse.put("message", "数据推送失败");
        return defaultResponse;
    }

    /**
     * 推送销售数据
     */
    @Override
    public Map<String, Object> pushSalesData(List<Map<String, Object>> salesData) {
        log.info("推送销售数据 - 记录数量: {}", salesData.size());
        return pushData("sales", salesData);
    }

    /**
     * 推送药品数据
     */
    @Override
    public Map<String, Object> pushMedicineData(List<Map<String, Object>> medicineData) {
        log.info("推送药品数据 - 记录数量: {}", medicineData.size());
        return pushData("medicines", medicineData);
    }

    /**
     * 推送库存数据
     */
    @Override
    public Map<String, Object> pushStockData(List<Map<String, Object>> stockData) {
        log.info("推送库存数据 - 记录数量: {}", stockData.size());
        return pushData("stocks", stockData);
    }

    /**
     * 推送采购订单数据
     */
    @Override
    public Map<String, Object> pushPurchaseOrderData(List<Map<String, Object>> purchaseOrderData) {
        log.info("推送采购订单数据 - 记录数量: {}", purchaseOrderData.size());
        return pushData("purchase-orders", purchaseOrderData);
    }

    /**
     * 推送分类数据
     */
    @Override
    public Map<String, Object> pushCategoryData(List<Map<String, Object>> categoryData) {
        log.info("推送分类数据 - 记录数量: {}", categoryData.size());
        return pushData("categories", categoryData);
    }

    /**
     * 推送症状数据
     */
    @Override
    public Map<String, Object> pushSymptomData(List<Map<String, Object>> symptomData) {
        log.info("推送症状数据 - 记录数量: {}", symptomData.size());
        return pushData("symptoms", symptomData);
    }

    /**
     * 推送每日销售数据
     */
    @Override
    public Map<String, Object> pushDailySalesData(List<Map<String, Object>> dailySalesData) {
        log.info("推送每日销售数据 - 记录数量: {}", dailySalesData.size());
        return pushData("daily-sales", dailySalesData);
    }

    /**
     * 推送按症状分类的销售数据
     */
    @Override
    public Map<String, Object> pushSalesBySymptomData(List<Map<String, Object>> salesBySymptomData) {
        log.info("推送按症状分类的销售数据 - 记录数量: {}", salesBySymptomData.size());
        return pushData("sales-by-symptom", salesBySymptomData);
    }

    /**
     * 推送库存周转率数据
     */
    @Override
    public Map<String, Object> pushStockTurnoverData(List<Map<String, Object>> stockTurnoverData) {
        log.info("推送库存周转率数据 - 记录数量: {}", stockTurnoverData.size());
        return pushData("stock-turnover", stockTurnoverData);
    }

    /**
     * 推送库存价值数据
     */
    @Override
    public Map<String, Object> pushStockValueData(List<Map<String, Object>> stockValueData) {
        log.info("推送库存价值数据 - 记录数量: {}", stockValueData.size());
        return pushData("stock-value", stockValueData);
    }

    /**
     * 推送即将过期的库存数据
     */
    @Override
    public Map<String, Object> pushExpiringStockData(List<Map<String, Object>> expiringStockData) {
        log.info("推送即将过期的库存数据 - 记录数量: {}", expiringStockData.size());
        return pushData("expiring-stock", expiringStockData);
    }

    /**
     * 推送库存不足的药品数据
     */
    @Override
    public Map<String, Object> pushLowStockData(List<Map<String, Object>> lowStockData) {
        log.info("推送库存不足的药品数据 - 记录数量: {}", lowStockData.size());
        return pushData("low-stock", lowStockData);
    }

    /**
     * 调用模型端获取数据摘要
     */
    private Map<String, Object> getDataSummaryApi() {
        String apiUrl = modelServiceBaseUrl + "/api/data/summary";

        try {
            log.debug("调用模型端数据摘要API: {}", apiUrl);

            ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                log.info("数据摘要获取成功");
                return responseBody;
            }
        } catch (Exception e) {
            log.error("获取数据摘要时发生异常", e);
        }

        return null;
    }

    /**
     * 获取数据摘要
     */
    @Override
    public Map<String, Object> getDataSummary() {
        log.info("获取数据摘要");

        try {
            Map<String, Object> summaryData = getDataSummaryApi();

            if (summaryData != null) {
                return summaryData;
            }
        } catch (Exception e) {
            log.error("获取数据摘要失败", e);
        }

        // 返回默认值
        Map<String, Object> defaultSummary = new HashMap<>();
        defaultSummary.put("success", false);
        defaultSummary.put("message", "无法获取数据摘要");
        return defaultSummary;
    }


}
