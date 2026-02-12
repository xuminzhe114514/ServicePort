package com.example.demo.controller;

import com.example.demo.entity.Symptom;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/symptoms")
@CrossOrigin(origins = "*")
public class SymptomController {

    @Autowired
    private SymptomService symptomService;

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "SymptomController is working!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取所有症状
     * GET /api/symptoms
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSymptoms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        try {
            Sort sort = direction.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Symptom> symptomPage = symptomService.findAll(pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("currentPage", symptomPage.getNumber());
            response.put("totalItems", symptomPage.getTotalElements());
            response.put("totalPages", symptomPage.getTotalPages());

            List<Map<String, Object>> symptomList = symptomPage.getContent().stream()
                    .map(this::createSymptomResponse)
                    .toList();
            response.put("data", symptomList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据ID获取症状
     * GET /api/symptoms/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSymptomById(@PathVariable Integer id) {
        try {
            Symptom symptom = symptomService.findById(id);
            
            if (symptom != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", createSymptomResponse(symptom));
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "症状不存在");
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
     * 创建新症状
     * POST /api/symptoms
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createSymptom(@RequestBody Symptom symptom) {
        if (symptomService.existsByName(symptom.getName())) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "症状名称已存在");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        try {
            Symptom savedSymptom = symptomService.save(symptom);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "症状创建成功");
            response.put("data", createSymptomResponse(savedSymptom));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新症状信息
     * PUT /api/symptoms/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateSymptom(
            @PathVariable Integer id,
            @RequestBody Symptom symptom) {
        Symptom existingSymptom = symptomService.findById(id);

        if (existingSymptom == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "症状不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        if (!existingSymptom.getName().equals(symptom.getName())) {
            if (symptomService.existsByName(symptom.getName())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "症状名称已存在");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        }
        existingSymptom.setName(symptom.getName());
        existingSymptom.setDescription(symptom.getDescription());
        try {
            Symptom updatedSymptom = symptomService.save(existingSymptom);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "症状更新成功");
            response.put("data", createSymptomResponse(updatedSymptom));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除症状
     * DELETE /api/symptoms/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSymptom(@PathVariable Integer id) {
        try {
            Symptom existingSymptom = symptomService.findById(id);

            if (existingSymptom == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "症状不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            symptomService.delete(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "症状删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据症状名称精确查找
     * GET /api/symptoms/name/{name}
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<Map<String, Object>> getSymptomByName(@PathVariable String name) {
        try {
            Symptom symptom = symptomService.findByName(name);

            if (symptom != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", createSymptomResponse(symptom));
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "症状不存在");
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
     * 根据症状名称模糊查询
     * GET /api/symptoms/search/name
     */
    @GetMapping("/search/name")
    public ResponseEntity<Map<String, Object>> searchSymptomsByName(
            @RequestParam String keyword) {
        try {
            List<Symptom> symptoms = symptomService.findByNameContaining(keyword);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", symptoms.stream().map(this::createSymptomResponse).toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据描述模糊查询症状
     * GET /api/symptoms/search/description
     */
    @GetMapping("/search/description")
    public ResponseEntity<Map<String, Object>> searchSymptomsByDescription(
            @RequestParam String keyword) {
        try {
            List<Symptom> symptoms = symptomService.findByDescriptionContaining(keyword);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", symptoms.stream().map(this::createSymptomResponse).toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据关键词搜索症状（名称或描述）
     * GET /api/symptoms/search
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchSymptoms(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "关键词不能为空");
                response.put("data", List.of());
                return ResponseEntity.badRequest().body(response);
            }

            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<Symptom> symptomPage = symptomService.searchSymptoms(keyword, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "搜索成功");
            Map<String, Object> pageData = new HashMap<>();
            pageData.put("content", symptomPage.getContent().stream().map(this::createSymptomResponse).toList());
            pageData.put("currentPage", symptomPage.getNumber());
            pageData.put("pageSize", symptomPage.getSize());
            pageData.put("totalItems", symptomPage.getTotalElements());
            pageData.put("totalPages", symptomPage.getTotalPages());
            pageData.put("isFirst", symptomPage.isFirst());
            pageData.put("isLast", symptomPage.isLast());
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
     * 检查症状名称是否存在
     * GET /api/symptoms/check-name/{name}
     */
    @GetMapping("/check-name/{name}")
    public ResponseEntity<Map<String, Object>> checkSymptomNameExists(@PathVariable String name) {
        try {
            boolean exists = symptomService.existsByName(name);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("exists", exists);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 批量删除症状
     * DELETE /api/symptoms/batch
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> deleteSymptoms(@RequestBody List<Integer> ids) {
        try {
            for (Integer id : ids) {
                if (!symptomService.exists(id)) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "ID为 " + id + " 的症状不存在");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            }

            symptomService.deleteAll(ids);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "批量删除成功，共删除 " + ids.size() + " 个症状");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 批量保存症状
     * POST /api/symptoms/batch
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> createSymptoms(@RequestBody List<Symptom> symptoms) {
        for (Symptom symptom : symptoms) {
            if (symptomService.existsByName(symptom.getName())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "症状名称 '" + symptom.getName() + "' 已存在");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        }
        List<Symptom> savedSymptoms;
        try {
            savedSymptoms = symptomService.saveAll(symptoms);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "批量保存失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "批量创建成功，共创建 " + savedSymptoms.size() + " 个症状");
        response.put("data", savedSymptoms.stream().map(this::createSymptomResponse).toList());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 检查症状是否存在
     * GET /api/symptoms/{id}/exists
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Map<String, Object>> checkSymptomExists(@PathVariable Integer id) {
        try {
            boolean exists = symptomService.exists(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("exists", exists);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取症状统计信息
     * GET /api/symptoms/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getSymptomStatistics() {
        try {
            long totalSymptoms = symptomService.countAll();
            List<Symptom> allSymptoms = symptomService.findAll(PageRequest.of(0, Integer.MAX_VALUE)).getContent();
            long hasDescription = allSymptoms.stream()
                    .filter(s -> s.getDescription() != null && !s.getDescription().trim().isEmpty())
                    .count();

            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalSymptoms", totalSymptoms);
            statistics.put("hasDescription", hasDescription);
            statistics.put("noDescription", totalSymptoms - hasDescription);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", statistics);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 创建症状响应对象
     */
    private Map<String, Object> createSymptomResponse(Symptom symptom) {
        Map<String, Object> symptomResponse = new HashMap<>();
        symptomResponse.put("id", symptom.getId());
        symptomResponse.put("name", symptom.getName());
        symptomResponse.put("description", symptom.getDescription());
        return symptomResponse;
    }

    /**
     * 获取症状简要列表（仅包含ID和名称）
     * GET /api/symptoms/simple
     */
    @GetMapping("/simple")
    public ResponseEntity<Map<String, Object>> getSimpleSymptomList() {
        try {
            Page<Symptom> symptomPage = symptomService.findAll(PageRequest.of(0, 1000, Sort.by("name").ascending()));
            List<Map<String, Object>> simpleList = symptomPage.getContent().stream()
                    .map(symptom -> {
                        Map<String, Object> simple = new HashMap<>();
                        simple.put("id", symptom.getId());
                        simple.put("name", symptom.getName());
                        return simple;
                    })
                    .toList();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", simpleList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据ID列表获取症状
     * POST /api/symptoms/by-ids
     */
    @PostMapping("/by-ids")
    public ResponseEntity<Map<String, Object>> getSymptomsByIds(@RequestBody List<Integer> ids) {
        try {
            List<Symptom> symptoms = ids.stream()
                    .map(symptomService::findById)
                    .filter(symptom -> symptom != null)
                    .toList();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", symptoms.stream().map(this::createSymptomResponse).toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 快速搜索症状（用于自动完成）
     * GET /api/symptoms/autocomplete
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<Map<String, Object>> autocompleteSymptoms(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            if (query == null || query.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", List.of());
                return ResponseEntity.ok(response);
            }
            List<Symptom> symptoms = symptomService.findByNameContaining(query);
            List<Symptom> limitedSymptoms = symptoms.stream()
                    .limit(limit)
                    .toList();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", limitedSymptoms.stream().map(this::createSymptomResponse).toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}