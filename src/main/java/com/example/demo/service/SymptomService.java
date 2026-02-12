package com.example.demo.service;

import com.example.demo.entity.Symptom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
}