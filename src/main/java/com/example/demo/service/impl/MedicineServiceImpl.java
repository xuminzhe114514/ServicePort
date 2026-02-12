package com.example.demo.service.impl;

import com.example.demo.entity.Medicine;
import com.example.demo.repository.MedicineRepository;
import com.example.demo.service.MedicineService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class MedicineServiceImpl extends BaseServiceImpl<Medicine, Long, MedicineRepository>
        implements MedicineService {

    protected MedicineServiceImpl(MedicineRepository repository) {
        super(repository);
    }

    @Override
    @Transactional
    public Medicine findByMedicineCode(String medicineCode) {
        return repository.findByMedicineCode(medicineCode).orElse(null);
    }

    @Override
    public Page<Medicine> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Medicine> findByStatus(Integer status, Pageable pageable) {
        return repository.findByStatus(status, pageable);
    }

    @Override
    public List<Medicine> searchMedicines(String keyword) {
        return repository.searchMedicines(keyword, 1);
    }

    @Override
    public List<Medicine> findByCategoryId(Long categoryId) {
        return repository.findByCategoryId(categoryId);
    }

    @Override
    public Medicine updatePrice(Long id, BigDecimal retailPrice, BigDecimal purchasePrice) {
        Medicine medicine = findById(id);
        if (medicine != null) {
            if (retailPrice != null) medicine.setRetailPrice(retailPrice);
            if (purchasePrice != null) medicine.setPurchasePrice(purchasePrice);
            return save(medicine);
        }
        return null;
    }

    @Override
    public long countByStatus(Integer status) {
        return repository.countByStatus(status);
    }
}