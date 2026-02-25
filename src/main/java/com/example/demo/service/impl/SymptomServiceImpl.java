package com.example.demo.service.impl;

import com.example.demo.entity.Medicine;
import com.example.demo.entity.SaleRecord;
import com.example.demo.entity.Symptom;
import com.example.demo.repository.MedicineRepository;
import com.example.demo.repository.SaleRecordRepository;
import com.example.demo.repository.SymptomRepository;
import com.example.demo.service.SaleRecordService;
import com.example.demo.service.SymptomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SymptomServiceImpl extends BaseServiceImpl<Symptom, Integer, SymptomRepository>
        implements SymptomService {

    @Autowired
    private MedicineRepository medicineRepository;
    
    @Autowired
    private SaleRecordRepository saleRecordRepository;

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

    @Override
    public Page<Symptom> findByMedicineId(Long medicineId, Pageable pageable) {
        Medicine medicine = medicineRepository.findById(medicineId).orElse(null);
        if (medicine == null) {
            return new PageImpl<>(new ArrayList<>());
        }
        int total =  medicine.getSymptoms().size();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), total);
        List<Symptom> pageContent;
        if (start>=total) {
            pageContent = new ArrayList<>();
        }else {
            pageContent = medicine.getSymptoms().subList(start, Math.min(total, end));
        }
        return new PageImpl<>(pageContent, pageable, total);
    }

    @Override
    public Page<Symptom> findMostCommonSymptoms(int limit,LocalDateTime start, LocalDateTime end, Pageable pageable) {
        List<Object[]> results = repository.findMostCommonSymptoms(start, end, pageable);
        List<Symptom> commonSymptoms = results.stream()
                .map(result -> {
                    Integer symptomId = (Integer) result[0];
                    return repository.findById(symptomId).orElse(null);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        int total = commonSymptoms.size();
        int startIdx = (int) pageable.getOffset();
        int endIdx = Math.min(startIdx + pageable.getPageSize(), total);

        List<Symptom> content;
        if (startIdx >= total) {
            content = Collections.emptyList();
        } else {
            content = commonSymptoms.subList(startIdx, endIdx);
        }

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Map<String, Object> getSymptomUsageStatistics(Integer symptomId) {
        Map<String, Object> stats = new HashMap<>();
        Symptom symptom = repository.findById(symptomId).orElse(null);
        if (symptom == null) {
            return stats;
        }
        long medicineCount = repository.countMedicinesBySymptomId(symptomId);
        stats.put("medicineCount", medicineCount);
        long saleRecordCount = repository.countSaleRecordsBySymptomId(symptomId);
        stats.put("saleRecordCount", saleRecordCount);
        Object saleAmountObj = repository.sumSaleAmountBySymptomId(symptomId);
        Double saleAmount = saleAmountObj != null ? (saleAmountObj instanceof BigDecimal ? ((BigDecimal)saleAmountObj).doubleValue() : saleAmountObj instanceof Double ? (Double)saleAmountObj : 0.0) : 0.0;
        stats.put("saleAmount", saleAmount);
        return stats;
    }

    @Override
    public Page<Symptom> findSymptomsWithMedicines(Pageable pageable) {
        List<Symptom> allSymptoms = repository.findAll();
        List<Symptom> symptomsWithMedicines = allSymptoms.stream()
                .filter(symptom -> {
                    long medicineCount = repository.countMedicinesBySymptomId(symptom.getId());
                    return medicineCount > 0;
                })
                .collect(Collectors.toList());

        int total = symptomsWithMedicines.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        List<Symptom> content;
        if (start >= total) {
            content = Collections.emptyList();
        } else {
            content = symptomsWithMedicines.subList(start, end);
        }

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Symptom> findBySaleRecordId(Long saleRecordId, Pageable pageable) {
        SaleRecord saleRecord = saleRecordRepository.findById(saleRecordId).orElse(null);
        if (saleRecord == null) {
            return new PageImpl<>(new ArrayList<>());
        }
        int total = saleRecord.getSymptom().size();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), total);
        List<Symptom> pageContent;
        if (start>=total) {
            pageContent = new ArrayList<>();
        }else {
            pageContent = saleRecord.getSymptom().subList(start, Math.min(total, end));
        }
        return new PageImpl<>(pageContent, pageable, total);
    }
}