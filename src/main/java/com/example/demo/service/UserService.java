package com.example.demo.service;

import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional
public interface UserService extends BaseService<User, Long> {

    User findByUsername(String username);
    User login(String username, String password);
    User matchPassword(User user, String password);
    User updatePassword(User user);
    Page<User> findAll(Pageable pageable);
    List<User> findByRole(String role);
    User changeStatus(Long id, Integer status);
    boolean existsByUsername(String username);
    Page<User> findByKeyword(String keyword, Pageable pageable);
    int countByUserStatus(Integer userStatus);
    int countByRole(String role);
    int countAll();

}