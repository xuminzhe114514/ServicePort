package com.example.demo.service;

import com.example.demo.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
@Transactional
public interface CategoryService extends BaseService<Category, Long> {
    List<Category> findByParentId(Long parentId);
    List<Category> findByLevel(Integer level);
    List<Category> findRootCategories();
    Page<Category> findAll(Pageable pageable);
    List<Map<String, Object>> getCategoryTree();
    boolean existsByName(String name);
    void updateCategoryTree();
    Map<Long, String> getCategoryPath(Long categoryId);
    Page<Category> searchCategories(String keyword, Pageable pageable);
    boolean hasAssociatedMedicines(Long categoryId);
}