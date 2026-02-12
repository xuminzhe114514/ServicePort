package com.example.demo.repository;

import com.example.demo.entity.Symptom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SymptomRepository extends JpaRepository<Symptom, Integer> {

    //根据名称精确查找症状
    Optional<Symptom> findByName(String name);

    //根据名称模糊查询
    List<Symptom> findByNameContaining(String name);

    //检查症状名称是否存在
    boolean existsByName(String name);

    //根据描述模糊查询
    List<Symptom> findByDescriptionContaining(String description);

    //搜索症状（名称或描述模糊匹配）
    @Query("SELECT s FROM Symptom s WHERE " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Symptom> searchSymptoms(@Param("keyword") String keyword);

}