package com.example.demo.service;

import com.example.demo.entity.Symptom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
@Transactional
public interface SymptomService extends BaseService<Symptom, Integer> {

    Symptom findByName(String name);
    List<Symptom> findByNameContaining(String name);
    boolean existsByName(String name);
    List<Symptom> findByDescriptionContaining(String description);
    List<Symptom> searchSymptoms(String keyword);
    Page<Symptom> findAll(Pageable pageable);
    Page<Symptom> searchSymptoms(String keyword, Pageable pageable);
    List<Symptom> saveAll(List<Symptom> symptoms);
    long countAll();
    
    Page<Symptom> findByMedicineId(Long medicineId, Pageable pageable);
    Page<Symptom> findMostCommonSymptoms(int limit, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Map<String, Object> getSymptomUsageStatistics(Integer symptomId);
    Page<Symptom> findSymptomsWithMedicines(Pageable pageable);
    Page<Symptom> findBySaleRecordId(Long saleRecordId, Pageable pageable);

}