package com.example.demo.service.impl;

import com.example.demo.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public abstract class BaseServiceImpl<T, ID, R extends JpaRepository<T, ID>>
        implements BaseService<T, ID> {


    protected final R repository;

    protected BaseServiceImpl(R repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public T save(T entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public T update(T entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(ID id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public T findById(ID id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll() {return repository.findAll();}

    @Override
    @Transactional(readOnly = true)
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    @Transactional
    public List<T> saveAll(List<T> entities) {
        return repository.saveAll(entities);
    }

    @Override
    @Transactional
    public void deleteAll(List<ID> ids) {
        for (ID id : ids) {
            repository.deleteById(id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exists(ID id) {
        return repository.existsById(id);
    }
}