package com.example.demo.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
@Transactional
public interface BaseService<T, ID> {

    T save(T entity);
    T update(T entity);
    void delete(ID id);
    T findById(ID id);
    List<T> findAll();

    // 分页查询方法
    Page<T> findAll(Pageable pageable);

    // 批量操作方法
    List<T> saveAll(List<T> entities);
    void deleteAll(List<ID> ids);

    // 检查是否存在
    boolean exists(ID id);
}