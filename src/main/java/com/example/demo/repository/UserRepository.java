package com.example.demo.repository;


import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 根据用户名查找用户
    Optional<User> findByUsername(String username);

    // 检查用户名是否存在
    boolean existsByUsername(String username);

    // 根据角色查找用户
    List<User> findByRole(String role);

    // 根据状态查找用户
    List<User> findByStatus(Integer status);

    // 根据用户名和状态查找
    Optional<User> findByUsernameAndStatus(String username, Integer status);

    // 自定义查询：搜索用户（按用户名或真实姓名）
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.realName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> searchUsers(@Param("keyword") String keyword);
}