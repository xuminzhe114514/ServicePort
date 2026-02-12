package com.example.demo.controller;

import com.example.demo.entity.Category;
import com.example.demo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "CategoryController is working!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取所有分类（分页）
     * GET /api/categories
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "sort") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        try {
            Sort sort = direction.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Category> categoryPage = categoryService.findAll(pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("currentPage", categoryPage.getNumber());
            response.put("totalItems", categoryPage.getTotalElements());
            response.put("totalPages", categoryPage.getTotalPages());

            List<Map<String, Object>> categoryList = categoryPage.getContent().stream()
                    .map(this::createCategoryResponse)
                    .toList();
            response.put("data", categoryList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据ID获取分类
     * GET /api/categories/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCategoryById(@PathVariable Long id) {
        try {
            Category category = categoryService.findById(id);

            if (category != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", createCategoryResponse(category));
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "分类不存在");
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
     * 创建新分类
     * POST /api/categories
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCategory(@RequestBody Category category) {
        try {
            if (categoryService.existsByName(category.getName())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "分类名称已存在");
                response.put("data",new HashMap<>());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            if (category.getLevel() == null) {
                category.setLevel(1);
            }
            if (category.getSort() == null) {
                category.setSort(0);
            }
            if (category.getStatus() == null) {
                category.setStatus(1);
            }
            if (category.getParentId() == null) {
                category.setParentId(0L);
            }

            Category savedCategory = categoryService.save(category);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "分类创建成功");
            response.put("data", createCategoryResponse(savedCategory));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            response.put("data",new HashMap<>());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 更新分类信息
     * PUT /api/categories/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCategory(
            @PathVariable Long id,
            @RequestBody Category category) {
        try {
            Category existingCategory = categoryService.findById(id);

            if (existingCategory == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "分类不存在");
                response.put("data",new ArrayList<>());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            if (!existingCategory.getName().equals(category.getName())) {
                if (categoryService.existsByName(category.getName())) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "分类名称已存在");
                    response.put("data",new ArrayList<>());
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
            }

            existingCategory.setName(category.getName());
            existingCategory.setParentId(category.getParentId());
            existingCategory.setLevel(category.getLevel());
            existingCategory.setDescription(category.getDescription());
            existingCategory.setSort(category.getSort());

            if (category.getStatus() != null) {
                existingCategory.setStatus(category.getStatus());
            }

            Category updatedCategory = categoryService.update(existingCategory);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "分类更新成功");
            response.put("data", createCategoryResponse(updatedCategory));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 删除分类
     * DELETE /api/categories/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCategory(@PathVariable Long id) {
        try {
            Category existingCategory = categoryService.findById(id);

            if (existingCategory == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "分类不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (categoryService.hasAssociatedMedicines(id)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "该分类下存在药品，无法删除");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            categoryService.delete(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "分类删除成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据父分类ID获取子分类列表
     * GET /api/categories/parent/{parentId}
     */
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<Map<String, Object>> getCategoriesByParentId(@PathVariable Long parentId) {
        try {
            List<Category> categories = categoryService.findByParentId(parentId);

            if (categories.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message","未找到符合条件的分类");
                response.put("data",new ArrayList<>());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", categories.stream().map(this::createCategoryResponse).toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取所有一级分类（根分类）
     * GET /api/categories/roots
     */
    @GetMapping("/roots")
    public ResponseEntity<Map<String, Object>> getRootCategories() {
        try {
            List<Category> rootCategories = categoryService.findRootCategories();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", rootCategories.stream().map(this::createCategoryResponse).toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取分类树形结构
     * GET /api/categories/tree
     */
    @GetMapping("/tree")
    public ResponseEntity<Map<String, Object>> getCategoryTree() {
        try {
            List<Map<String, Object>> categoryTree = categoryService.getCategoryTree();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", categoryTree);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取分类路径映射
     * GET /api/categories/{id}/path
     */
    @GetMapping("/{id}/path")
    public ResponseEntity<Map<String, Object>> getCategoryPath(@PathVariable Long id) {
        try {
            Category category = categoryService.findById(id);

            if (category == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "分类不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Map<Long, String> pathMap = categoryService.getCategoryPath(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", pathMap);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据分类级别查找分类
     * GET /api/categories/level/{level}
     */
    @GetMapping("/level/{level}")
    public ResponseEntity<Map<String, Object>> getCategoriesByLevel(@PathVariable Integer level) {
        try {
            List<Category> categories = categoryService.findByLevel(level);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", categories.stream().map(this::createCategoryResponse).toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 检查分类名称是否存在
     * GET /api/categories/check-name/{name}
     */
    @GetMapping("/check-name/{name}")
    public ResponseEntity<Map<String, Object>> checkCategoryNameExists(@PathVariable String name) {
        try {
            boolean exists = categoryService.existsByName(name);

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
     * 更新分类树（重新计算所有分类的级别）
     * POST /api/categories/update-tree
     */
    @PostMapping("/update-tree")
    public ResponseEntity<Map<String, Object>> updateCategoryTree() {
        try {
            categoryService.updateCategoryTree();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "分类树更新成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "分类树更新失败: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 批量删除分类
     * DELETE /api/categories/batch
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> deleteCategories(@RequestBody List<Long> ids) {
        try {
            for (Long id : ids) {
                if (!categoryService.exists(id)) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "ID为 " + id + " 的分类不存在");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            }

            categoryService.deleteAll(ids);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "批量删除成功，共删除 " + ids.size() + " 个分类");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 批量保存分类
     * POST /api/categories/batch
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> createCategories(@RequestBody List<Category> categories) {
        try {
            for (Category category : categories) {
                if (categoryService.existsByName(category.getName())) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "分类名称 '" + category.getName() + "' 已存在");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
            }

            for (Category category : categories) {
                if (category.getLevel() == null) category.setLevel(1);
                if (category.getSort() == null) category.setSort(0);
                if (category.getStatus() == null) category.setStatus(1);
                if (category.getParentId() == null) category.setParentId(0L);
            }

            List<Category> savedCategories = categoryService.saveAll(categories);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "批量创建成功，共创建 " + savedCategories.size() + " 个分类");
            response.put("data", savedCategories.stream().map(this::createCategoryResponse).toList());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 检查分类是否存在
     * GET /api/categories/{id}/exists
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Map<String, Object>> checkCategoryExists(@PathVariable Long id) {
        try {
            boolean exists = categoryService.exists(id);

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
     * 根据关键词搜索分类
     * GET /api/categories/search
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCategories(
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

            Pageable pageable = PageRequest.of(page, size, Sort.by("sort").ascending());
            Page<Category> resultPage = categoryService.searchCategories(keyword.trim(), pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "搜索成功");

            Map<String, Object> pageData = new HashMap<>();
            pageData.put("content", resultPage.getContent().stream().map(this::createCategoryResponse).toList());
            pageData.put("currentPage", resultPage.getNumber());
            pageData.put("pageSize", resultPage.getSize());
            pageData.put("totalItems", resultPage.getTotalElements());
            pageData.put("totalPages", resultPage.getTotalPages());
            pageData.put("isFirst", resultPage.isFirst());
            pageData.put("isLast", resultPage.isLast());

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
     * 修改分类状态
     * PUT /api/categories/{id}/status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> changeCategoryStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        try {
            Category existingCategory = categoryService.findById(id);

            if (existingCategory == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "分类不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (status != 0 && status != 1) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "状态值无效，应为0（禁用）或1（启用）");
                return ResponseEntity.badRequest().body(response);
            }

            existingCategory.setStatus(status);
            Category updatedCategory = categoryService.update(existingCategory);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "分类状态更新成功");
            response.put("data", createCategoryResponse(updatedCategory));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取分类统计信息
     * GET /api/categories/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getCategoryStatistics() {
        try {
            List<Category> allCategories = categoryService.findAll();

            long totalCategories = allCategories.size();
            long enabledCategories = allCategories.stream().filter(c -> c.getStatus() == 1).count();
            long disabledCategories = totalCategories - enabledCategories;

            long level1Count = allCategories.stream().filter(c -> c.getLevel() == 1).count();
            long level2Count = allCategories.stream().filter(c -> c.getLevel() == 2).count();
            long level3Count = allCategories.stream().filter(c -> c.getLevel() == 3).count();

            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalCategories", totalCategories);
            statistics.put("enabledCategories", enabledCategories);
            statistics.put("disabledCategories", disabledCategories);
            statistics.put("level1Count", level1Count);
            statistics.put("level2Count", level2Count);
            statistics.put("level3Count", level3Count);

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
     * 创建分类响应对象
     */
    private Map<String, Object> createCategoryResponse(Category category) {
        Map<String, Object> categoryResponse = new HashMap<>();
        categoryResponse.put("id", category.getId());
        categoryResponse.put("name", category.getName());
        categoryResponse.put("parentId", category.getParentId());
        categoryResponse.put("level", category.getLevel());
        categoryResponse.put("description", category.getDescription());
        categoryResponse.put("sort", category.getSort());
        categoryResponse.put("status", category.getStatus());
        categoryResponse.put("createTime", category.getCreateTime());

        return categoryResponse;
    }


}