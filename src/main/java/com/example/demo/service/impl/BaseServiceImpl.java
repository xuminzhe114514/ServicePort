package com.example.demo.service.impl;

import com.example.demo.service.BaseService;
import org.hibernate.Hibernate;
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
    public void delete(ID id) {
        repository.deleteById(id);
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

    @Override
    @Transactional(readOnly = true)
    public T findById(ID id) {
        T entity = repository.findById(id).orElse(null);
        if (entity != null) {
            Hibernate.initialize(entity);
        }
        return entity;
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll() {
        List<T> entities = repository.findAll();
        if (!entities.isEmpty()) {
            entities.forEach(Hibernate::initialize);
        }
        return entities;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<T> findAll(Pageable pageable) {
        Page<T> page = repository.findAll(pageable);
        if (page.hasContent()) {
            page.getContent().forEach(Hibernate::initialize);
        }
        return page;
    }

    @Override
    @Transactional
    public T save(T entity) {
        T savedEntity = repository.save(entity);
        if (savedEntity != null) {
            Hibernate.initialize(savedEntity);
        }
        return savedEntity;
    }

    @Override
    @Transactional
    public T update(T entity) {
        T updatedEntity = repository.save(entity);
        if (updatedEntity != null) {
            Hibernate.initialize(updatedEntity);
        }
        return updatedEntity;
    }

    @Override
    @Transactional
    public List<T> saveAll(List<T> entities) {
        List<T> savedEntities = repository.saveAll(entities);
        if (!savedEntities.isEmpty()) {
            savedEntities.forEach(Hibernate::initialize);
        }
        return savedEntities;
    }
}