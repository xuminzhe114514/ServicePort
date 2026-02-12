package com.example.demo.service;

import com.example.demo.entity.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
@Transactional
public interface MedicineService extends BaseService<Medicine, Long> {
    Medicine findByMedicineCode(String medicineCode);
    Page<Medicine> findAll(Pageable pageable);
    Page<Medicine> findByStatus(Integer status, Pageable pageable);
    List<Medicine> searchMedicines(String keyword);
    List<Medicine> findByCategoryId(Long categoryId);
    Medicine updatePrice(Long id, BigDecimal retailPrice, BigDecimal purchasePrice);
    long countByStatus(Integer status);
}