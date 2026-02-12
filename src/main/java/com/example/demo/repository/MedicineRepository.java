package com.example.demo.repository;

import com.example.demo.entity.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    // 根据药品编码查找
    Optional<Medicine> findByMedicineCode(String medicineCode);

    // 检查药品编码是否存在
    boolean existsByMedicineCode(String medicineCode);

    // 根据名称模糊查询
    List<Medicine> findByNameContaining(String name);

    // 根据分类查找药品
    @Query("SELECT m FROM Medicine m WHERE m.category.id = :categoryId")
    List<Medicine> findByCategoryId(@Param("categoryId") Long categoryId);

    // 根据状态查找药品
    List<Medicine> findByStatus(Integer status);

    // 根据生产厂家查找
    List<Medicine> findByManufacturerContaining(String manufacturer);

    // 分页查询
    Page<Medicine> findAll(Pageable pageable);

    // 分页查询（按状态）
    Page<Medicine> findByStatus(Integer status, Pageable pageable);

    // 多条件查询：按名称和分类
    @Query("SELECT m FROM Medicine m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')) AND m.category.id = :categoryId")
    List<Medicine> findByNameContainingAndCategoryId(@Param("name") String name, @Param("categoryId") Long categoryId);

    // 自定义查询：搜索药品（名称、通用名、生产厂家）
    @Query("SELECT m FROM Medicine m WHERE " +
            "(LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.genericName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.manufacturer) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "m.status = :status")
    List<Medicine> searchMedicines(@Param("keyword") String keyword, @Param("status") Integer status);

    // 统计药品数量
    @Query("SELECT COUNT(m) FROM Medicine m WHERE m.status = :status")
    long countByStatus(@Param("status") Integer status);

    // 获取所有药品的分类统计
    @Query("SELECT m.category.id, COUNT(m) FROM Medicine m WHERE m.status = 1 GROUP BY m.category.id")
    List<Object[]> countByCategory();

    // 根据症状ID查找药品
    @Query("SELECT m FROM Medicine m JOIN m.symptoms s WHERE s.id = :symptomId")
    List<Medicine> findBySymptomId(@Param("symptomId") Integer symptomId);

    // 根据症状名称查找药品
    @Query("SELECT m FROM Medicine m JOIN m.symptoms s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :symptomName, '%'))")
    List<Medicine> findBySymptomName(@Param("symptomName") String symptomName);

    // 统计药品按症状分类
    @Query("SELECT s.name, COUNT(m) FROM Medicine m JOIN m.symptoms s WHERE m.status = 1 GROUP BY s.id, s.name")
    List<Object[]> countMedicinesBySymptom();
}