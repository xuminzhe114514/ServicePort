package com.example.demo.controller;

import com.example.demo.entity.PredictionResult;
import com.example.demo.service.PredictionResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/predictions")
@CrossOrigin(origins = "*")
public class PredictionResultController {

    @Autowired
    private PredictionResultService predictionResultService;

    /**
     * 测试接口
     * GET /api/predictions/test
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "PredictionResultController is working!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取所有预测结果（分页）
     * GET /api/predictions
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPredictions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "predictionDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        try {
            Sort sort = direction.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<PredictionResult> predictionPage = predictionResultService.findAll(pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("currentPage", predictionPage.getNumber());
            response.put("totalItems", predictionPage.getTotalElements());
            response.put("totalPages", predictionPage.getTotalPages());

            List<Map<String, Object>> predictionList = predictionPage.getContent().stream()
                    .map(this::createPredictionResponse)
                    .toList();
            response.put("data", predictionList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据ID获取预测结果
     * GET /api/predictions/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPredictionById(@PathVariable Long id) {
        try {
            PredictionResult prediction = predictionResultService.findById(id);

            if (prediction != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", createPredictionResponse(prediction));
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "预测结果不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 创建新预测结果
     * POST /api/predictions
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPrediction(@RequestBody PredictionResult predictionResult) {
        try {
            PredictionResult savedPrediction = predictionResultService.save(predictionResult);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "预测结果创建成功");
            response.put("data", createPredictionResponse(savedPrediction));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "创建失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 更新预测结果
     * PUT /api/predictions/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePrediction(
            @PathVariable Long id,
            @RequestBody PredictionResult predictionResult) {
        try {
            PredictionResult existingPrediction = predictionResultService.findById(id);

            if (existingPrediction == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "预测结果不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            existingPrediction.setPredictionDate(predictionResult.getPredictionDate());
            existingPrediction.setPredictedQuantity(predictionResult.getPredictedQuantity());
            existingPrediction.setConfidenceIntervalLower(predictionResult.getConfidenceIntervalLower());
            existingPrediction.setConfidenceIntervalUpper(predictionResult.getConfidenceIntervalUpper());
            existingPrediction.setModelType(predictionResult.getModelType());
            existingPrediction.setAccuracyRate(predictionResult.getAccuracyRate());
            existingPrediction.setRecommendedOrderQuantity(predictionResult.getRecommendedOrderQuantity());

            PredictionResult updatedPrediction = predictionResultService.update(existingPrediction);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "预测结果更新成功");
            response.put("data", createPredictionResponse(updatedPrediction));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 删除预测结果
     * DELETE /api/predictions/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePrediction(@PathVariable Long id) {
        try {
            PredictionResult existingPrediction = predictionResultService.findById(id);

            if (existingPrediction == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "预测结果不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            predictionResultService.delete(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "预测结果删除成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据药品ID获取预测结果
     * GET /api/predictions/medicine/{medicineId}
     */
    @GetMapping("/medicine/{medicineId}")
    public ResponseEntity<Map<String, Object>> getPredictionsByMedicineId(
            @PathVariable Long medicineId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<PredictionResult> predictions = predictionResultService.findByMedicineId(medicineId);

            // 分页处理
            int start = page * size;
            int end = Math.min(start + size, predictions.size());

            if (start > predictions.size()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", List.of());
                response.put("currentPage", page);
                response.put("totalItems", predictions.size());
                response.put("totalPages", (int) Math.ceil((double) predictions.size() / size));
                return ResponseEntity.ok(response);
            }

            List<PredictionResult> pageContent = predictions.subList(start, end);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);

            Map<String, Object> pageData = new HashMap<>();
            pageData.put("content", pageContent.stream().map(this::createPredictionResponse).toList());
            pageData.put("currentPage", page);
            pageData.put("pageSize", size);
            pageData.put("totalItems", predictions.size());
            pageData.put("totalPages", (int) Math.ceil((double) predictions.size() / size));
            pageData.put("isFirst", page == 0);
            pageData.put("isLast", end >= predictions.size());

            response.put("data", pageData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据日期获取预测结果
     * GET /api/predictions/date/{date}
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<Map<String, Object>> getPredictionsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<PredictionResult> predictions = predictionResultService.findByPredictionDate(date);

            // 分页处理
            int start = page * size;
            int end = Math.min(start + size, predictions.size());

            if (start > predictions.size()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", List.of());
                response.put("currentPage", page);
                response.put("totalItems", predictions.size());
                response.put("totalPages", (int) Math.ceil((double) predictions.size() / size));
                return ResponseEntity.ok(response);
            }

            List<PredictionResult> pageContent = predictions.subList(start, end);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);

            Map<String, Object> pageData = new HashMap<>();
            pageData.put("content", pageContent.stream().map(this::createPredictionResponse).toList());
            pageData.put("currentPage", page);
            pageData.put("pageSize", size);
            pageData.put("totalItems", predictions.size());
            pageData.put("totalPages", (int) Math.ceil((double) predictions.size() / size));
            pageData.put("isFirst", page == 0);
            pageData.put("isLast", end >= predictions.size());

            response.put("data", pageData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据日期范围获取预测结果
     * GET /api/predictions/date/range
     */
    @GetMapping("/date/range")
    public ResponseEntity<Map<String, Object>> getPredictionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<PredictionResult> predictions = predictionResultService.findByPredictionDateRange(startDate, endDate);

            // 分页处理
            int start = page * size;
            int end = Math.min(start + size, predictions.size());

            if (start > predictions.size()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", List.of());
                response.put("currentPage", page);
                response.put("totalItems", predictions.size());
                response.put("totalPages", (int) Math.ceil((double) predictions.size() / size));
                return ResponseEntity.ok(response);
            }

            List<PredictionResult> pageContent = predictions.subList(start, end);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);

            Map<String, Object> pageData = new HashMap<>();
            pageData.put("content", pageContent.stream().map(this::createPredictionResponse).toList());
            pageData.put("currentPage", page);
            pageData.put("pageSize", size);
            pageData.put("totalItems", predictions.size());
            pageData.put("totalPages", (int) Math.ceil((double) predictions.size() / size));
            pageData.put("isFirst", page == 0);
            pageData.put("isLast", end >= predictions.size());

            response.put("data", pageData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取需要重新预测的结果
     * GET /api/predictions/need-reprediction
     */
    @GetMapping("/need-reprediction")
    public ResponseEntity<Map<String, Object>> getNeedReprediction(
            @RequestParam(required = false, defaultValue = "80") Double threshold,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<PredictionResult> predictions = predictionResultService.findNeedReprediction(threshold);

            // 分页处理
            int start = page * size;
            int end = Math.min(start + size, predictions.size());

            if (start > predictions.size()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", List.of());
                response.put("currentPage", page);
                response.put("totalItems", predictions.size());
                response.put("totalPages", (int) Math.ceil((double) predictions.size() / size));
                return ResponseEntity.ok(response);
            }

            List<PredictionResult> pageContent = predictions.subList(start, end);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);

            Map<String, Object> pageData = new HashMap<>();
            pageData.put("content", pageContent.stream().map(this::createPredictionResponse).toList());
            pageData.put("currentPage", page);
            pageData.put("pageSize", size);
            pageData.put("totalItems", predictions.size());
            pageData.put("totalPages", (int) Math.ceil((double) predictions.size() / size));
            pageData.put("isFirst", page == 0);
            pageData.put("isLast", end >= predictions.size());

            response.put("data", pageData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取药品的最新预测结果
     * GET /api/predictions/latest/{medicineId}
     */
    @GetMapping("/latest/{medicineId}")
    public ResponseEntity<Map<String, Object>> getLatestPredictionByMedicineId(@PathVariable Long medicineId) {
        try {
            PredictionResult prediction = predictionResultService.findLatestByMedicineId(medicineId);

            if (prediction != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", createPredictionResponse(prediction));
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "该药品暂无预测结果");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 生成预测结果
     * POST /api/predictions/generate/{medicineId}
     */
    @PostMapping("/generate/{medicineId}")
    public ResponseEntity<Map<String, Object>> generatePrediction(
            @PathVariable Long medicineId,
            @RequestParam(required = false) String modelType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate predictionDate,
            @RequestParam(required = false, defaultValue = "30") int predictionDays) {
        try {
            PredictionResult prediction;
            if (predictionDate != null) {
                prediction = predictionResultService.generatePrediction(medicineId, modelType, predictionDate, predictionDays);
            } else {
                prediction = predictionResultService.generatePrediction(medicineId, modelType, LocalDate.now(), predictionDays);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "预测生成成功");
            response.put("data", createPredictionResponse(prediction));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "预测生成失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 批量生成预测结果
     * POST /api/predictions/generate/batch
     */
    @PostMapping("/generate/batch")
    public ResponseEntity<Map<String, Object>> generateBatchPredictions(
            @RequestBody List<Long> medicineIds,
            @RequestParam(required = false) String modelType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "30") int predictionDays) {
        try {
            List<PredictionResult> predictions;
            if (startDate != null && endDate != null) {
                predictions = predictionResultService.generateBatchPredictions(medicineIds, modelType, startDate, endDate);
            } else {
                predictions = predictionResultService.generateBatchPredictions(medicineIds, modelType, predictionDays);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "批量预测生成成功，共生成 " + predictions.size() + " 个预测结果");
            response.put("data", predictions.stream().map(this::createPredictionResponse).toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "批量预测生成失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 重新生成预测结果
     * POST /api/predictions/regenerate/{id}
     */
    @PostMapping("/regenerate/{id}")
    public ResponseEntity<Map<String, Object>> regeneratePrediction(
            @PathVariable Long id,
            @RequestParam(required = false) String modelType) {
        try {
            PredictionResult existingPrediction = predictionResultService.findById(id);
            if (existingPrediction == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "预测结果不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            PredictionResult regeneratedPrediction = predictionResultService.generatePrediction(
                    existingPrediction.getMedicine().getId(), 
                    modelType, 
                    existingPrediction.getPredictionDate()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "预测重新生成成功");
            response.put("data", createPredictionResponse(regeneratedPrediction));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "预测重新生成失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取预测准确率统计
     * GET /api/predictions/statistics/accuracy
     */
    @GetMapping("/statistics/accuracy")
    public ResponseEntity<Map<String, Object>> getAccuracyStatistics() {
        try {
            Map<String, Double> averageAccuracyByModel = predictionResultService.getAverageAccuracyByModel();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", averageAccuracyByModel);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取推荐订单数量
     * GET /api/predictions/recommendations
     */
    @GetMapping("/recommendations")
    public ResponseEntity<Map<String, Object>> getRecommendedOrderQuantities(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate,
            @RequestParam(required = false) List<Long> medicineIds) {
        try {
            Map<Long, Integer> recommendations;
            if (medicineIds != null && !medicineIds.isEmpty()) {
                recommendations = predictionResultService.getRecommendedOrderQuantities(
                        targetDate != null ? targetDate : LocalDate.now(),
                        medicineIds
                );
            } else {
                recommendations = predictionResultService.getRecommendedOrderQuantities(
                        targetDate != null ? targetDate : LocalDate.now()
                );
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", recommendations);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取特定药品的推荐订单数量
     * GET /api/predictions/recommendations/{medicineId}
     */
    @GetMapping("/recommendations/{medicineId}")
    public ResponseEntity<Map<String, Object>> getRecommendedQuantityForMedicine(
            @PathVariable Long medicineId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate) {
        try {
            Map<Long, Integer> recommendations = predictionResultService.getRecommendedOrderQuantities(
                    targetDate != null ? targetDate : LocalDate.now(),
                    List.of(medicineId)
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", recommendations.get(medicineId));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 检查健康状态
     * GET /api/predictions/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> checkHealth() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Prediction service is healthy");
            response.put("modelServiceHealthy", predictionResultService.checkModelServiceHealth());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 检查模型服务健康状态
     * GET /api/predictions/model-service/health
     */
    @GetMapping("/model-service/health")
    public ResponseEntity<Map<String, Object>> checkModelServiceHealth() {
        try {
            boolean isHealthy = predictionResultService.checkModelServiceHealth();
            Map<String, Object> modelServiceInfo = predictionResultService.getModelServiceInfo();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("healthy", isHealthy);
            response.put("info", modelServiceInfo);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 批量删除预测结果
     * DELETE /api/predictions/batch
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> deletePredictions(@RequestBody List<Long> ids) {
        try {
            // 检查所有预测结果是否存在
            for (Long id : ids) {
                if (!predictionResultService.exists(id)) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "ID为 " + id + " 的预测结果不存在");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            }

            predictionResultService.deleteAll(ids);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "批量删除成功，共删除 " + ids.size() + " 个预测结果");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 批量创建预测结果
     * POST /api/predictions/batch
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> createPredictions(@RequestBody List<PredictionResult> predictionResults) {
        try {
            List<PredictionResult> savedPredictions = predictionResultService.saveAll(predictionResults);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "批量创建成功，共创建 " + savedPredictions.size() + " 个预测结果");
            response.put("data", savedPredictions.stream().map(this::createPredictionResponse).toList());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "批量创建失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 推送销售数据到模型
     * POST /api/predictions/push/sales
     */
    @PostMapping("/push/sales")
    public ResponseEntity<Map<String, Object>> pushSalesData(@RequestBody List<Map<String, Object>> salesData) {
        try {
            Map<String, Object> result = predictionResultService.pushSalesData(salesData);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "销售数据推送成功");
            response.put("data", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "数据推送失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 推送药品数据到模型
     * POST /api/predictions/push/medicine
     */
    @PostMapping("/push/medicine")
    public ResponseEntity<Map<String, Object>> pushMedicineData(@RequestBody List<Map<String, Object>> medicineData) {
        try {
            Map<String, Object> result = predictionResultService.pushMedicineData(medicineData);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "药品数据推送成功");
            response.put("data", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "数据推送失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 推送库存数据到模型
     * POST /api/predictions/push/stock
     */
    @PostMapping("/push/stock")
    public ResponseEntity<Map<String, Object>> pushStockData(@RequestBody List<Map<String, Object>> stockData) {
        try {
            Map<String, Object> result = predictionResultService.pushStockData(stockData);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "库存数据推送成功");
            response.put("data", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "数据推送失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取ABC分类
     * GET /api/predictions/analysis/abc
     */
    @GetMapping("/analysis/abc")
    public ResponseEntity<Map<String, Object>> calculateABCClassification(
            @RequestParam(required = false) List<Long> medicineIds) {
        try {
            Map<String, Object> result = predictionResultService.calculateABCClassification(
                    medicineIds != null ? medicineIds : List.of()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 检测滞销品
     * GET /api/predictions/analysis/slow-moving
     */
    @GetMapping("/analysis/slow-moving")
    public ResponseEntity<Map<String, Object>> detectSlowMovingItems(
            @RequestParam(defaultValue = "90") int thresholdDays) {
        try {
            Map<String, Object> result = predictionResultService.detectSlowMovingItems(thresholdDays);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 计算过期风险
     * GET /api/predictions/analysis/expiry-risk
     */
    @GetMapping("/analysis/expiry-risk")
    public ResponseEntity<Map<String, Object>> calculateExpiryRisk() {
        try {
            Map<String, Object> result = predictionResultService.calculateExpiryRisk();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取库存状态摘要
     * GET /api/predictions/inventory/summary
     */
    @GetMapping("/inventory/summary")
    public ResponseEntity<Map<String, Object>> getInventoryStatusSummary() {
        try {
            Map<String, Object> result = predictionResultService.getInventoryStatusSummary();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 计算动态安全库存
     * GET /api/predictions/inventory/safety-stock/dynamic
     */
    @GetMapping("/inventory/safety-stock/dynamic")
    public ResponseEntity<Map<String, Object>> calculateDynamicSafetyStock(
            @RequestParam Long medicineId,
            @RequestParam(defaultValue = "30") int predictionDays) {
        try {
            Map<String, Object> result = predictionResultService.calculateDynamicSafetyStock(medicineId, predictionDays);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 生成补货建议
     * GET /api/predictions/inventory/replenishment
     */
    @GetMapping("/inventory/replenishment")
    public ResponseEntity<Map<String, Object>> generateReplenishmentSuggestions(
            @RequestParam(required = false) List<Long> medicineIds) {
        try {
            Map<String, Object> result = predictionResultService.generateReplenishmentSuggestions(
                    medicineIds != null ? medicineIds : List.of()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取当前库存状态
     * GET /api/predictions/inventory/current
     */
    @GetMapping("/inventory/current")
    public ResponseEntity<Map<String, Object>> getCurrentInventoryStatus() {
        try {
            Map<String, Object> result = predictionResultService.getCurrentInventoryStatus();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取带过期信息的库存
     * GET /api/predictions/inventory/expiry
     */
    @GetMapping("/inventory/expiry")
    public ResponseEntity<Map<String, Object>> getInventoryWithExpiry() {
        try {
            Map<String, Object> result = predictionResultService.getInventoryWithExpiry();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取药品库存状态
     * GET /api/predictions/inventory/medicine/{medicineId}
     */
    @GetMapping("/inventory/medicine/{medicineId}")
    public ResponseEntity<Map<String, Object>> getMedicineInventoryStatus(@PathVariable Long medicineId) {
        try {
            Map<String, Object> result = predictionResultService.getMedicineInventoryStatus(medicineId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 计算安全库存
     * GET /api/predictions/inventory/safety-stock
     */
    @GetMapping("/inventory/safety-stock")
    public ResponseEntity<Map<String, Object>> calculateSafetyStock(
            @RequestParam Long medicineId,
            @RequestParam(defaultValue = "14") int leadTimeDays,
            @RequestParam(defaultValue = "0.95") double serviceLevel) {
        try {
            Map<String, Object> result = predictionResultService.calculateSafetyStock(medicineId, leadTimeDays, serviceLevel);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 批量计算安全库存
     * GET /api/predictions/inventory/safety-stock/batch
     */
    @GetMapping("/inventory/safety-stock/batch")
    public ResponseEntity<Map<String, Object>> calculateBatchSafetyStock(
            @RequestParam(defaultValue = "14") int leadTimeDays,
            @RequestParam(defaultValue = "0.95") double serviceLevel) {
        try {
            Map<String, Object> result = predictionResultService.calculateBatchSafetyStock(leadTimeDays, serviceLevel);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取数据摘要
     * GET /api/predictions/data/summary
     */
    @GetMapping("/data/summary")
    public ResponseEntity<Map<String, Object>> getDataSummary() {
        try {
            Map<String, Object> result = predictionResultService.getDataSummary();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取模型性能
     * GET /api/predictions/model/performance
     */
    @GetMapping("/model/performance")
    public ResponseEntity<Map<String, Object>> getModelPerformance(
            @RequestParam Long medicineId,
            @RequestParam(defaultValue = "3") int testPeriods) {
        try {
            Map<String, Object> result = predictionResultService.getModelPerformance(medicineId, testPeriods);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 创建预测结果响应对象
     */
    private Map<String, Object> createPredictionResponse(PredictionResult prediction) {
        Map<String, Object> response = new HashMap<>();
        
        response.put("id", prediction.getId());

        if (prediction.getMedicine() != null) {
            Map<String, Object> medicineInfo = new HashMap<>();
            medicineInfo.put("id", prediction.getMedicine().getId());
            medicineInfo.put("medicineCode", prediction.getMedicine().getMedicineCode());
            medicineInfo.put("name", prediction.getMedicine().getName());
            medicineInfo.put("specification", prediction.getMedicine().getSpecification());
            medicineInfo.put("unit", prediction.getMedicine().getUnit());
            response.put("medicine", medicineInfo);
        }
        
        response.put("predictionDate", prediction.getPredictionDate());
        response.put("predictedQuantity", prediction.getPredictedQuantity());
        response.put("confidenceIntervalLower", prediction.getConfidenceIntervalLower());
        response.put("confidenceIntervalUpper", prediction.getConfidenceIntervalUpper());
        response.put("modelType", prediction.getModelType());
        response.put("accuracyRate", prediction.getAccuracyRate());
        response.put("recommendedOrderQuantity", prediction.getRecommendedOrderQuantity());
        response.put("createTime", prediction.getCreateTime());
        response.put("updateTime", prediction.getUpdateTime());
        
        return response;
    }
}
