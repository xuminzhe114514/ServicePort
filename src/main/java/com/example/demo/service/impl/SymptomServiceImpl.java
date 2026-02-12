package com.example.demo.service.impl;

import com.example.demo.entity.Symptom;
import com.example.demo.repository.SymptomRepository;
import com.example.demo.service.SymptomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SymptomServiceImpl extends BaseServiceImpl<Symptom, Integer, SymptomRepository>
        implements SymptomService {

    @Autowired
    public SymptomServiceImpl(SymptomRepository symptomRepository) {
        super(symptomRepository);
    }

    @Override
    public Symptom findByName(String name) {
        Optional<Symptom> symptom = repository.findByName(name);
        return symptom.orElse(null);
    }

    @Override
    public List<Symptom> findByNameContaining(String name) {
        return repository.findByNameContaining(name);
    }

    @Override
    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    @Override
    public List<Symptom> findByDescriptionContaining(String description) {
        return repository.findByDescriptionContaining(description);
    }

    @Override
    public List<Symptom> searchSymptoms(String keyword) {
        return repository.searchSymptoms(keyword);
    }

    @Override
    public Page<Symptom> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Symptom> searchSymptoms(String keyword, Pageable pageable) {
        List<Symptom> allSymptoms = repository.searchSymptoms(keyword);
        int total =  allSymptoms.size();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allSymptoms.size());
        List<Symptom> pageContent;

        if (start>=total) {
            pageContent = new ArrayList<>();
        }else {
            pageContent = allSymptoms.subList(start, Math.min(total, end));
        }

        return new PageImpl<>(pageContent, pageable, total);
    }

    @Override
    public List<Symptom> saveAll(List<Symptom> symptoms) {
        return repository.saveAll(symptoms);
    }

    @Override
    public long countAll() {
        return repository.count();
    }

    @Override
    public Symptom save(Symptom symptom) {
        if (symptom.getId() == null) {
            if (repository.existsByName(symptom.getName())) {
                throw new IllegalArgumentException("症状名称已存在: " + symptom.getName());
            }
        } else {
            Optional<Symptom> existing = repository.findById(symptom.getId());
            if (existing.isPresent() && !existing.get().getName().equals(symptom.getName())) {
                if (repository.existsByName(symptom.getName())) {
                    throw new IllegalArgumentException("症状名称已存在: " + symptom.getName());
                }
            }
        }

        return super.save(symptom);
    }

    @Override
    public void deleteAll(List<Integer> ids) {
        for (Integer id : ids) {
            repository.deleteById(id);
        }
    }
}