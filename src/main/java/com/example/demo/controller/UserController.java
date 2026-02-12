package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        try {
            return ResponseEntity.ok("UserController is working!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("操作失败: " + e.getMessage());
        }
    }

    /**
     * 用户登录
     * POST /api/users/login
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");

            User user = userService.login(username, password);

            if (user != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "登录成功");
                response.put("user", createUserResponse(user));
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "用户名或密码错误");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "登录失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取所有用户（分页）
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        try {
            Sort sort = direction.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<User> userPage = userService.findAll(pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("currentPage", userPage.getNumber());
            response.put("totalItems", userPage.getTotalElements());
            response.put("totalPages", userPage.getTotalPages());

            List<Map<String, Object>> userList = userPage.getContent().stream()
                    .map(this::createUserResponse)
                    .toList();
            response.put("data", userList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据ID获取用户
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        try {
            User user = userService.findById(id);

            if (user != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", createUserResponse(user));
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据用户名获取用户
     * GET /api/users/username/{username}
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<Map<String, Object>> getUserByUsername(@PathVariable String username) {
        try {
            User user = userService.findByUsername(username);

            if (user != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", createUserResponse(user));
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 创建新用户
     * POST /api/users
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody User user) {
        try {
            if (userService.existsByUsername(user.getUsername())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "用户名已存在");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            if (user.getStatus() == null) {
                user.setStatus(1);
            }

            User savedUser = userService.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "用户创建成功");
            response.put("data", createUserResponse(savedUser));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "创建失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 更新用户信息
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            User existingUser = userService.findById(id);

            if (existingUser == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            existingUser.setRealName(user.getRealName());
            existingUser.setPhone(user.getPhone());
            existingUser.setEmail(user.getEmail());
            existingUser.setRole(user.getRole());

            if (user.getStatus() != null) {
                existingUser.setStatus(user.getStatus());
            }

            User updatedUser = userService.update(existingUser);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "用户更新成功");
            response.put("data", createUserResponse(updatedUser));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 删除用户
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        try {
            User existingUser = userService.findById(id);

            if (existingUser == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            userService.delete(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "用户删除成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 修改用户状态
     * PUT /api/users/{id}/status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> changeUserStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        try {
            User existingUser = userService.findById(id);

            if (existingUser == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (status != 0 && status != 1) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "状态值无效，应为0（禁用）或1（启用）");
                return ResponseEntity.badRequest().body(response);
            }

            User updatedUser = userService.changeStatus(id, status);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "用户状态更新成功");
            response.put("data", createUserResponse(updatedUser));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "状态更新失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据角色获取用户
     * GET /api/users/role/{role}
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<Map<String, Object>> getUsersByRole(@PathVariable String role) {
        try {
            List<User> users = userService.findByRole(role);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", users.stream().map(this::createUserResponse).toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 用户名或真实名字模糊搜索用户
     * GET /api/users/search
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "关键词不能为空");
                response.put("data", List.of());
                return ResponseEntity.badRequest().body(response);
            }

            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

            Page<User> userPage = userService.findByKeyword(keyword, pageable);

            List<Map<String, Object>> userList = userPage.getContent().stream()
                    .map(this::createUserResponse)
                    .toList();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "搜索成功");

            Map<String, Object> pageData = new HashMap<>();
            pageData.put("content", userList);                    // 当前页数据
            pageData.put("currentPage", userPage.getNumber());    // 当前页码
            pageData.put("pageSize", userPage.getSize());         // 每页大小
            pageData.put("totalItems", userPage.getTotalElements()); // 总记录数
            pageData.put("totalPages", userPage.getTotalPages()); // 总页数
            pageData.put("isFirst", userPage.isFirst());          // 是否第一页
            pageData.put("isLast", userPage.isLast());            // 是否最后一页

            response.put("data", pageData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "搜索失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 检查用户名是否存在
     * GET /api/users/check-username/{username}
     */
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Map<String, Object>> checkUsernameExists(@PathVariable String username) {
        try {
            boolean exists = userService.existsByUsername(username);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("exists", exists);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 创建用户响应对象
     */
    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("username", user.getUsername());
        userResponse.put("realName", user.getRealName());
        userResponse.put("phone", user.getPhone());
        userResponse.put("email", user.getEmail());
        userResponse.put("role", user.getRole());
        userResponse.put("status", user.getStatus());
        userResponse.put("createTime", user.getCreateTime());
        userResponse.put("updateTime", user.getUpdateTime());

        return userResponse;
    }

    /**
     * 获取用户统计信息
     * GET /api/users/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getUserStatistics() {
        try {
            // 获取不同角色的用户数量
            List<User> admins = userService.findByRole("ADMIN");
            List<User> pharmacists = userService.findByRole("PHARMACIST");
            List<User> purchasers = userService.findByRole("PURCHASER");

            // 获取启用和禁用的用户数量
            long total = userService.countAll();
            long enabledCount = userService.countByUserStatus(1);
            long disabledCount = total - enabledCount;

            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalUsers", total);
            statistics.put("enabledUsers", enabledCount);
            statistics.put("disabledUsers", disabledCount);
            statistics.put("adminCount", admins.size());
            statistics.put("pharmacistCount", pharmacists.size());
            statistics.put("purchaserCount", purchasers.size());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", statistics);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 更新用户密码
     * PUT /api/users/{id}/password
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<Map<String, Object>> updatePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> passwordRequest) {
        try {
            User existingUser = userService.findById(id);

            if (existingUser == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            String oldPassword = passwordRequest.get("oldPassword");
            String newPassword = passwordRequest.get("newPassword");
            String confirmPassword = passwordRequest.get("confirmPassword");

            User targetUser = userService.matchPassword(existingUser, oldPassword);

            if (targetUser == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "旧密码不正确");
                return ResponseEntity.badRequest().body(response);
            }

            if (newPassword == null || newPassword.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "新密码不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            if (!newPassword.equals(confirmPassword)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "两次输入的密码不一致");
                return ResponseEntity.badRequest().body(response);
            }

            existingUser.setPassword(newPassword);

            User updatedUser = userService.updatePassword(existingUser);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "密码更新成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "密码更新失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}