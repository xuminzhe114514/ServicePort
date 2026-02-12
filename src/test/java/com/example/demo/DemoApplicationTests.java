package com.example.demo;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
@Rollback(true) // 测试完成后自动回滚
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DemoApplicationTests {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SymptomRepository symptomRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private UserRepository userRepository;

    // 测试数据ID缓存
    private Long testCategoryId;
    private Long testMedicineId;
    private Integer testSymptomId;
    private Long testStockId;
    private Long testUserId;


    @Test
    @Order(100)
    void contextLoads() {
        System.out.println("=== Spring上下文加载测试 ===");

        assertNotNull(categoryRepository, "CategoryRepository应被注入");
        assertNotNull(symptomRepository, "SymptomRepository应被注入");
        assertNotNull(medicineRepository, "MedicineRepository应被注入");
        assertNotNull(stockRepository, "StockRepository应被注入");
        assertNotNull(userRepository, "UserRepository应被注入");

        System.out.println("Spring上下文加载成功 ✓");
    }

    @BeforeEach
    void setUp() {
        // 1. 创建分类
        Category category = new Category();
        category.setName("测试分类");
        category.setParentId(0L);
        category.setLevel(1);
        category.setDescription("测试分类描述");
        category.setSort(1);
        category.setStatus(1);
        Category savedCategory = categoryRepository.save(category);
        testCategoryId = savedCategory.getId();
        assertNotNull(testCategoryId, "分类ID不应为空");

        // 2. 创建症状
        Symptom symptom = new Symptom();
        symptom.setName("发烧");
        symptom.setDescription("体温升高症状");
        Symptom savedSymptom = symptomRepository.save(symptom);
        testSymptomId = savedSymptom.getId();
        assertNotNull(testSymptomId, "症状ID不应为空");

        // 3. 创建用户
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setRealName("测试用户");
        user.setPhone("13800138000");
        user.setEmail("test@example.com");
        user.setRole("PHARMACIST");
        user.setStatus(1);
        User savedUser = userRepository.save(user);
        testUserId = savedUser.getId();
        assertNotNull(testUserId, "用户ID不应为空");

        // 4. 创建药品
        Medicine medicine = new Medicine();
        medicine.setMedicineCode("MED001");
        medicine.setName("测试药品");
        medicine.setGenericName("测试通用名");
        medicine.setCategory(savedCategory);
        medicine.setSpecification("10mg/片");
        medicine.setUnit("盒");
        medicine.setManufacturer("测试药厂");
        medicine.setApprovalNumber("国药准字Z123456");
        medicine.setDescription("测试药品描述");
        medicine.setRetailPrice(new BigDecimal("25.50"));
        medicine.setPurchasePrice(new BigDecimal("18.00"));
        medicine.setStatus(1);
        medicine.setSeasonal(false);
        medicine.setPrescription(false);
        medicine.setStorageRequirement(1);
        // 关联症状
        medicine.setSymptoms(Arrays.asList(savedSymptom));

        Medicine savedMedicine = medicineRepository.save(medicine);
        testMedicineId = savedMedicine.getId();
        assertNotNull(testMedicineId, "药品ID不应为空");

        // 5. 创建库存
        Stock stock = new Stock();
        stock.setMedicine(savedMedicine);
        stock.setBatchNumber("BATCH202501");
        stock.setProductionDate(LocalDate.now().minusDays(180));
        stock.setExpirationDate(LocalDate.now().plusDays(180));
        stock.setQuantity(100);
        stock.setWarningQuantity(20);
        stock.setShelfLocation("A区-01架");
        stock.setStatus(1);
        stock.setMinimumOrderQuantity(50);
        stock.setLeadTimeDays(3);
        stock.setReorderPoint(30);

        Stock savedStock = stockRepository.save(stock);
        testStockId = savedStock.getId();
        assertNotNull(testStockId, "库存ID不应为空");
    }

    @Test
    @Order(1)
    void testCategoryRepository() {
        System.out.println("=== 测试CategoryRepository ===");

        // 1. 测试findByParentId
        List<Category> rootCategories = categoryRepository.findByParentId(0L);
        assertFalse(rootCategories.isEmpty(), "应能找到父分类ID为0的分类");
        assertThat(rootCategories).anyMatch(c -> c.getName().equals("测试分类"));

        // 2. 测试findByLevel
        List<Category> level1Categories = categoryRepository.findByLevel(1);
        assertFalse(level1Categories.isEmpty(), "应能找到级别为1的分类");

        // 3. 测试findByStatus
        List<Category> activeCategories = categoryRepository.findByStatus(1);
        assertFalse(activeCategories.isEmpty(), "应能找到状态为1的分类");

        // 4. 测试findByName
        Optional<Category> foundCategory = categoryRepository.findByName("测试分类");
        assertTrue(foundCategory.isPresent(), "应能找到名称为'测试分类'的分类");
        assertEquals("测试分类描述", foundCategory.get().getDescription());

        // 5. 测试findByParentIdAndStatusOrderBySortAsc
        List<Category> sortedCategories = categoryRepository.findByParentIdAndStatusOrderBySortAsc(0L, 1);
        assertFalse(sortedCategories.isEmpty(), "应能找到排序的分类");

        System.out.println("CategoryRepository测试通过 ");
    }

    @Test
    @Order(2)
    void testSymptomRepository() {
        System.out.println("=== 测试SymptomRepository ===");

        // 1. 测试findByName
        Optional<Symptom> foundSymptom = symptomRepository.findByName("发烧");
        assertTrue(foundSymptom.isPresent(), "应能找到名称为'发烧'的症状");
        assertEquals("体温升高症状", foundSymptom.get().getDescription());

        // 2. 测试findByNameContaining
        List<Symptom> symptoms = symptomRepository.findByNameContaining("发");
        assertFalse(symptoms.isEmpty(), "应能找到包含'发'字的症状");

        // 3. 测试existsByName
        boolean exists = symptomRepository.existsByName("发烧");
        assertTrue(exists, "症状'发烧'应存在");

        // 4. 测试searchSymptoms
        List<Symptom> searchResults = symptomRepository.searchSymptoms("发烧");
        assertFalse(searchResults.isEmpty(), "搜索'发烧'应返回结果");

        // 5. 验证数据完整性
        Symptom symptom = symptomRepository.findById(testSymptomId).orElse(null);
        assertNotNull(symptom, "应能通过ID找到症状");
        assertEquals("发烧", symptom.getName());

        System.out.println("SymptomRepository测试通过 ");
    }

    @Test
    @Order(3)
    void testMedicineRepository() {
        System.out.println("=== 测试MedicineRepository ===");

        // 1. 测试findByMedicineCode
        Optional<Medicine> foundMedicine = medicineRepository.findByMedicineCode("MED001");
        assertTrue(foundMedicine.isPresent(), "应能找到编码为MED001的药品");
        assertEquals("测试药品", foundMedicine.get().getName());

        // 2. 测试existsByMedicineCode
        boolean exists = medicineRepository.existsByMedicineCode("MED001");
        assertTrue(exists, "药品编码MED001应存在");

        // 3. 测试findByNameContaining
        List<Medicine> medicines = medicineRepository.findByNameContaining("测试");
        assertFalse(medicines.isEmpty(), "应能找到名称包含'测试'的药品");

        // 4. 测试findByCategoryId
        List<Medicine> categoryMedicines = medicineRepository.findByCategoryId(testCategoryId);
        assertFalse(categoryMedicines.isEmpty(), "应能找到指定分类的药品");

        // 5. 测试findByStatus
        List<Medicine> activeMedicines = medicineRepository.findByStatus(1);
        assertFalse(activeMedicines.isEmpty(), "应能找到状态为1的药品");

        // 6. 测试searchMedicines
        List<Medicine> searchResults = medicineRepository.searchMedicines("测试", 1);
        assertFalse(searchResults.isEmpty(), "搜索'测试'应返回结果");

        // 7. 测试findBySymptomId
        List<Medicine> symptomMedicines = medicineRepository.findBySymptomId(testSymptomId);
        assertFalse(symptomMedicines.isEmpty(), "应能找到关联症状的药品");

        System.out.println("MedicineRepository测试通过 ✓");
    }

    @Test
    @Order(4)
    void testStockRepository() {
        System.out.println("=== 测试StockRepository ===");

        // 1. 测试findByMedicineId
        List<Stock> medicineStocks = stockRepository.findByMedicineId(testMedicineId);
        assertFalse(medicineStocks.isEmpty(), "应能找到指定药品的库存");
        assertEquals(100, medicineStocks.get(0).getQuantity());

        // 2. 测试findByExpirationDateBeforeAndStatus
        List<Stock> expiredStocks = stockRepository.findByExpirationDateBeforeAndStatus(
                LocalDate.now().minusDays(1), 1);
        // 这里应该为空，因为我们的测试数据有效期是未来180天
        assertTrue(expiredStocks.isEmpty(), "应找不到过期库存");

        // 3. 测试findExpiringStock
        List<Stock> expiringStocks = stockRepository.findExpiringStock(
                LocalDate.now().plusDays(30),
                LocalDate.now().plusDays(200));
        assertFalse(expiringStocks.isEmpty(), "应能找到即将过期的库存");

        // 4. 测试findLowStock
        List<Stock> lowStocks = stockRepository.findLowStock();
        // 当前库存100，预警值20，所以不是低库存
        assertTrue(lowStocks.isEmpty(), "应找不到低库存");

        // 5. 测试findByBatchNumber
        List<Stock> stockByBatch = stockRepository.findByBatchNumber("BATCH202501");
        assertFalse(stockByBatch.isEmpty(), "应能通过批号找到库存");

        // 6. 测试sumQuantityByMedicineId
        Integer totalQuantity = stockRepository.sumQuantityByMedicineId(testMedicineId);
        assertNotNull(totalQuantity);
        assertEquals(100, totalQuantity, "库存总量应为100");

        System.out.println("StockRepository测试通过 ✓");
    }

    @Test
    @Order(5)
    void testUserRepository() {
        System.out.println("=== 测试UserRepository ===");

        // 1. 测试findByUsername
        Optional<User> foundUser = userRepository.findByUsername("testuser");
        assertTrue(foundUser.isPresent(), "应能找到用户名为testuser的用户");
        assertEquals("测试用户", foundUser.get().getRealName());

        // 2. 测试existsByUsername
        boolean exists = userRepository.existsByUsername("testuser");
        assertTrue(exists, "用户名testuser应存在");

        // 3. 测试findByRole
        List<User> pharmacists = userRepository.findByRole("PHARMACIST");
        assertFalse(pharmacists.isEmpty(), "应能找到角色为PHARMACIST的用户");

        // 4. 测试findByStatus
        List<User> activeUsers = userRepository.findByStatus(1);
        assertFalse(activeUsers.isEmpty(), "应能找到状态为1的用户");

        // 5. 测试findByUsernameAndStatus
        Optional<User> activeUser = userRepository.findByUsernameAndStatus("testuser", 1);
        assertTrue(activeUser.isPresent(), "应能找到状态为1的testuser");

        // 6. 测试searchUsers
        List<User> searchResults = userRepository.searchUsers("测试");
        assertFalse(searchResults.isEmpty(), "搜索'测试'应返回结果");

        System.out.println("UserRepository测试通过 ✓");
    }

    @Test
    @Order(99)
    void testTransactionRollback() {
        System.out.println("=== 测试事务回滚 ===");

        long categoryCount = categoryRepository.count();
        long medicineCount = medicineRepository.count();
        long userCount = userRepository.count();

        System.out.println("当前事务中的数据统计:");
        System.out.println("分类数量: " + categoryCount);
        System.out.println("药品数量: " + medicineCount);
        System.out.println("用户数量: " + userCount);

        // 这些数据会在方法结束后回滚
        assertTrue(categoryCount > 0, "事务中应有测试数据");
        assertTrue(medicineCount > 0, "事务中应有测试数据");
        assertTrue(userCount > 0, "事务中应有测试数据");

        System.out.println("事务回滚测试完成，数据将在方法结束后回滚 ✓");
    }




}
