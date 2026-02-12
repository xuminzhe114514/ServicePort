package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/user")
    public User createTestUser() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("123456");
        user.setRealName("系统管理员");
        user.setRole("ADMIN");
        user.setStatus(1);
        return userRepository.save(user);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/tables")
    public String checkTables() {
        return "✅ 实体类创建成功！请检查数据库中的表结构。";
    }
}
