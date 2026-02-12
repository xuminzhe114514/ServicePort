package com.example.demo.repository;

import com.example.demo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // 根据父分类ID查找子分类
    List<Category> findByParentId(Long parentId);

    // 根据分类级别查找
    List<Category> findByLevel(Integer level);

    // 根据状态查找分类
    List<Category> findByStatus(Integer status);

    // 查找所有启用状态的分类
    List<Category> findByStatusOrderBySortAsc(Integer status);

    // 查找一级分类（parentId = 0）
    List<Category> findByParentIdAndStatusOrderBySortAsc(Long parentId, Integer status);

    // 根据名称查找分类
    Optional<Category> findByName(String name);

    // 自定义查询：查找某个分类的所有子孙分类
    @Query("SELECT c FROM Category c WHERE c.parentId = :parentId OR c.id IN " +
            "(SELECT c2.id FROM Category c2 WHERE c2.parentId IN " +
            "(SELECT c3.id FROM Category c3 WHERE c3.parentId = :parentId))")
    List<Category> findDescendantsByParentId(@Param("parentId") Long parentId);
}