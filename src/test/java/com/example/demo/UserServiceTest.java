package com.example.demo;

import com.example.demo.entity.User;
import org.junit.jupiter.api.*;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserService测试")
class UserServiceTest extends BaseServiceTest {

    @Test
    @Order(1)
    @DisplayName("测试BaseService方法 - save")
    void testSave() {
        System.out.println("=== 测试UserService.save() ===");

        // 创建新用户
        User user = new User();
        user.setUsername("newtestuser");
        user.setPassword("password123");
        user.setRealName("新测试用户");
        user.setPhone("13800138001");
        user.setEmail("newtest@example.com");
        user.setRole("PHARMACIST");
        user.setStatus(1);

        // 保存用户
        User savedUser = userService.save(user);
        assertNotNull(savedUser, "保存的用户不应为空");
        assertNotNull(savedUser.getId(), "保存的用户ID不应为空");
        assertEquals("新测试用户", savedUser.getRealName(), "用户姓名应正确");

        System.out.println("UserService.save()测试通过 ✓");
    }

    @Test
    @Order(2)
    @DisplayName("测试BaseService方法 - update")
    void testUpdate() {
        System.out.println("=== 测试UserService.update() ===");

        // 获取测试用户
        User user = userService.findById(testUserId);
        assertNotNull(user, "用户应存在");

        // 更新用户
        user.setRealName("更新后的测试用户");
        user.setEmail("updatedtest@example.com");

        // 保存更新
        User updatedUser = userService.update(user);
        assertNotNull(updatedUser, "更新后的用户不应为空");
        assertEquals("更新后的测试用户", updatedUser.getRealName(), "用户姓名应已更新");
        assertEquals("updatedtest@example.com", updatedUser.getEmail(), "用户邮箱应已更新");

        System.out.println("UserService.update()测试通过 ✓");
    }

    @Test
    @Order(3)
    @DisplayName("测试BaseService方法 - delete")
    void testDelete() {
        System.out.println("=== 测试UserService.delete() ===");

        // 创建一个临时用户用于删除测试
        User user = new User();
        user.setUsername("temptestuser");
        user.setPassword("password123");
        user.setRealName("临时测试用户");
        user.setPhone("13800138002");
        user.setEmail("temp@example.com");
        user.setRole("PHARMACIST");
        user.setStatus(1);
        User savedUser = userService.save(user);
        Long tempUserId = savedUser.getId();
        assertNotNull(tempUserId, "临时用户ID不应为空");

        // 验证用户存在
        User foundUser = userService.findById(tempUserId);
        assertNotNull(foundUser, "临时用户应存在");

        // 删除用户
        userService.delete(tempUserId);

        // 验证用户已删除
        User deletedUser = userService.findById(tempUserId);
        assertNull(deletedUser, "删除后的用户应不存在");

        System.out.println("UserService.delete()测试通过 ✓");
    }

    @Test
    @Order(4)
    @DisplayName("测试BaseService方法 - findById")
    void testFindById() {
        System.out.println("=== 测试UserService.findById() ===");

        // 查找测试用户
        User user = userService.findById(testUserId);
        assertNotNull(user, "用户应存在");
        assertEquals("testuser", user.getUsername(), "用户名应正确");

        System.out.println("UserService.findById()测试通过 ✓");
    }

    @Test
    @Order(5)
    @DisplayName("测试BaseService方法 - findAll")
    void testFindAll() {
        System.out.println("=== 测试UserService.findAll() ===");

        // 测试无参findAll
        List<User> users = userService.findAll();
        assertNotNull(users, "用户列表不应为空");
        assertTrue(users.size() > 0, "用户列表应包含数据");

        // 测试带分页的findAll
        Page<User> userPage = userService.findAll(pageable);
        assertNotNull(userPage, "分页用户列表不应为空");
        assertTrue(userPage.getTotalElements() > 0, "分页用户列表应包含数据");

        System.out.println("UserService.findAll()测试通过 ✓");
    }

    @Test
    @Order(6)
    @DisplayName("测试BaseService方法 - saveAll")
    void testSaveAll() {
        System.out.println("=== 测试UserService.saveAll() ===");

        // 创建多个用户
        User user1 = new User();
        user1.setUsername("batchuser1");
        user1.setPassword("password123");
        user1.setRealName("批量用户1");
        user1.setPhone("13800138003");
        user1.setEmail("batch1@example.com");
        user1.setRole("PHARMACIST");
        user1.setStatus(1);

        User user2 = new User();
        user2.setUsername("batchuser2");
        user2.setPassword("password123");
        user2.setRealName("批量用户2");
        user2.setPhone("13800138004");
        user2.setEmail("batch2@example.com");
        user2.setRole("PHARMACIST");
        user2.setStatus(1);

        List<User> users = List.of(user1, user2);

        // 批量保存
        List<User> savedUsers = userService.saveAll(users);
        assertNotNull(savedUsers, "批量保存的用户列表不应为空");
        assertEquals(2, savedUsers.size(), "批量保存的用户数量应正确");
        for (User savedUser : savedUsers) {
            assertNotNull(savedUser.getId(), "保存的用户ID不应为空");
        }

        System.out.println("UserService.saveAll()测试通过 ✓");
    }

    @Test
    @Order(7)
    @DisplayName("测试BaseService方法 - deleteAll")
    void testDeleteAll() {
        System.out.println("=== 测试UserService.deleteAll() ===");

        // 创建多个临时用户用于删除测试
        User user1 = new User();
        user1.setUsername("temptestuser1");
        user1.setPassword("password123");
        user1.setRealName("临时测试用户1");
        user1.setPhone("13800138005");
        user1.setEmail("temp1@example.com");
        user1.setRole("PHARMACIST");
        user1.setStatus(1);

        User user2 = new User();
        user2.setUsername("temptestuser2");
        user2.setPassword("password123");
        user2.setRealName("临时测试用户2");
        user2.setPhone("13800138006");
        user2.setEmail("temp2@example.com");
        user2.setRole("PHARMACIST");
        user2.setStatus(1);

        List<User> users = List.of(user1, user2);
        List<User> savedUsers = userService.saveAll(users);
        List<Long> ids = savedUsers.stream().map(User::getId).toList();

        // 验证用户存在
        for (Long id : ids) {
            assertNotNull(userService.findById(id), "临时用户应存在");
        }

        // 批量删除
        userService.deleteAll(ids);

        // 验证用户已删除
        for (Long id : ids) {
            assertNull(userService.findById(id), "删除后的用户应不存在");
        }

        System.out.println("UserService.deleteAll()测试通过 ✓");
    }

    @Test
    @Order(8)
    @DisplayName("测试BaseService方法 - exists")
    void testExists() {
        System.out.println("=== 测试UserService.exists() ===");

        // 测试存在的用户
        boolean exists = userService.exists(testUserId);
        assertTrue(exists, "测试用户应存在");

        // 测试不存在的用户
        boolean notExists = userService.exists(999999L);
        assertFalse(notExists, "不存在的用户应返回false");

        System.out.println("UserService.exists()测试通过 ✓");
    }

    @Test
    @Order(10)
    @DisplayName("测试UserService特有方法 - findByUsername")
    void testFindByUsername() {
        System.out.println("=== 测试UserService.findByUsername() ===");

        // 测试查找用户名
        User user = userService.findByUsername("testuser");
        assertNotNull(user, "用户应存在");
        assertEquals("testuser", user.getUsername(), "用户名应正确");

        System.out.println("UserService.findByUsername()测试通过 ✓");
    }

    @Test
    @Order(11)
    @DisplayName("测试UserService特有方法 - login")
    void testLogin() {
        System.out.println("=== 测试UserService.login() ===");

        // 测试正确的用户名和密码
        User user = userService.login("testuser", "password123");
        assertNotNull(user, "登录应成功");
        assertEquals("testuser", user.getUsername(), "用户名应正确");

        // 测试错误的密码
        User failedUser = userService.login("testuser", "wrongpassword");
        assertNull(failedUser, "密码错误应登录失败");

        System.out.println("UserService.login()测试通过 ✓");
    }

    @Test
    @Order(12)
    @DisplayName("测试UserService特有方法 - matchPassword")
    void testMatchPassword() {
        System.out.println("=== 测试UserService.matchPassword() ===");

        // 获取测试用户
        User user = userService.findById(testUserId);
        assertNotNull(user, "用户应存在");

        // 测试正确的密码
        User matchedUser = userService.matchPassword(user, "password123");
        assertNotNull(matchedUser, "密码匹配应成功");

        // 测试错误的密码
        User notMatchedUser = userService.matchPassword(user, "wrongpassword");
        assertNull(notMatchedUser, "密码不匹配应返回null");

        System.out.println("UserService.matchPassword()测试通过 ✓");
    }

    @Test
    @Order(13)
    @DisplayName("测试UserService特有方法 - updatePassword")
    void testUpdatePassword() {
        System.out.println("=== 测试UserService.updatePassword() ===");

        // 获取测试用户
        User user = userService.findById(testUserId);
        assertNotNull(user, "用户应存在");

        // 更新密码
        user.setPassword("newpassword123");
        User updatedUser = userService.updatePassword(user);
        assertNotNull(updatedUser, "更新密码应成功");

        // 验证新密码是否生效
        User loginUser = userService.login("testuser", "newpassword123");
        assertNotNull(loginUser, "使用新密码登录应成功");

        System.out.println("UserService.updatePassword()测试通过 ✓");
    }

    @Test
    @Order(14)
    @DisplayName("测试UserService特有方法 - findByRole")
    void testFindByRole() {
        System.out.println("=== 测试UserService.findByRole() ===");

        // 测试查找角色为PHARMACIST的用户
        List<User> pharmacists = userService.findByRole("PHARMACIST");
        assertNotNull(pharmacists, "用户列表不应为空");
        // 验证至少有一个用户角色为PHARMACIST
        boolean hasPharmacist = pharmacists.stream().anyMatch(u -> "PHARMACIST".equals(u.getRole()));
        assertTrue(hasPharmacist, "应找到角色为PHARMACIST的用户");

        System.out.println("UserService.findByRole()测试通过 ✓");
    }

    @Test
    @Order(15)
    @DisplayName("测试UserService特有方法 - changeStatus")
    void testChangeStatus() {
        System.out.println("=== 测试UserService.changeStatus() ===");

        // 测试更改用户状态为0（禁用）
        User disabledUser = userService.changeStatus(testUserId, 0);
        assertNotNull(disabledUser, "更改状态应成功");
        assertEquals(0, disabledUser.getStatus(), "用户状态应已更改为0");

        // 测试更改用户状态为1（启用）
        User enabledUser = userService.changeStatus(testUserId, 1);
        assertNotNull(enabledUser, "更改状态应成功");
        assertEquals(1, enabledUser.getStatus(), "用户状态应已更改为1");

        System.out.println("UserService.changeStatus()测试通过 ✓");
    }

    @Test
    @Order(16)
    @DisplayName("测试UserService特有方法 - existsByUsername")
    void testExistsByUsername() {
        System.out.println("=== 测试UserService.existsByUsername() ===");

        // 测试存在的用户名
        boolean exists = userService.existsByUsername("testuser");
        assertTrue(exists, "用户名'testuser'应存在");

        // 测试不存在的用户名
        boolean notExists = userService.existsByUsername("nonexistentuser");
        assertFalse(notExists, "不存在的用户名应返回false");

        System.out.println("UserService.existsByUsername()测试通过 ✓");
    }

    @Test
    @Order(17)
    @DisplayName("测试UserService特有方法 - findByKeyword")
    void testFindByKeyword() {
        System.out.println("=== 测试UserService.findByKeyword() ===");

        // 测试按关键词搜索用户
        Page<User> searchResults = userService.findByKeyword("测试", pageable);
        assertNotNull(searchResults, "搜索结果不应为空");

        System.out.println("UserService.findByKeyword()测试通过 ✓");
    }

    @Test
    @Order(18)
    @DisplayName("测试UserService特有方法 - countByUserStatus")
    void testCountByUserStatus() {
        System.out.println("=== 测试UserService.countByUserStatus() ===");

        // 测试统计状态为1的用户数量
        int activeCount = userService.countByUserStatus(1);
        assertTrue(activeCount >= 0, "用户数量应大于等于0");

        // 测试统计状态为0的用户数量
        int inactiveCount = userService.countByUserStatus(0);
        assertTrue(inactiveCount >= 0, "用户数量应大于等于0");

        System.out.println("UserService.countByUserStatus()测试通过 ✓");
    }

    @Test
    @Order(19)
    @DisplayName("测试UserService特有方法 - countByRole")
    void testCountByRole() {
        System.out.println("=== 测试UserService.countByRole() ===");

        // 测试统计角色为PHARMACIST的用户数量
        int pharmacistCount = userService.countByRole("PHARMACIST");
        assertTrue(pharmacistCount >= 0, "用户数量应大于等于0");

        System.out.println("UserService.countByRole()测试通过 ✓");
    }

    @Test
    @Order(20)
    @DisplayName("测试UserService特有方法 - countAll")
    void testCountAll() {
        System.out.println("=== 测试UserService.countAll() ===");

        // 测试统计所有用户数量
        int totalCount = userService.countAll();
        assertTrue(totalCount >= 0, "用户数量应大于等于0");

        System.out.println("UserService.countAll()测试通过 ✓");
    }
}
