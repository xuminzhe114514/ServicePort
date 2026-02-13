package com.example.demo;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.*;
import com.example.demo.service.impl.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(true) // 测试完成后自动回滚
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class BaseServiceTest {

    @Autowired
    protected CategoryRepository categoryRepository;

    @Autowired
    protected SymptomRepository symptomRepository;

    @Autowired
    protected MedicineRepository medicineRepository;

    @Autowired
    protected StockRepository stockRepository;
    
    @Autowired
    protected UserRepository userRepository;
    
    @Autowired
    protected SaleRecordRepository saleRecordRepository;
    
    @Autowired
    protected PurchaseOrderRepository purchaseOrderRepository;
    
    @Autowired
    protected PredictionResultRepository predictionResultRepository;

    @Autowired
    protected CategoryService categoryService;
    
    @Autowired
    protected SymptomService symptomService;
    
    @Autowired
    protected MedicineService medicineService;
    
    @Autowired
    protected StockService stockService;
    
    @Autowired
    protected UserService userService;
    
    @Autowired
    protected SaleRecordService saleRecordService;
    
    @Autowired
    protected PurchaseOrderService purchaseOrderService;
    
    @Autowired
    protected PredictionResultService predictionResultService;

    // 测试数据ID缓存
    protected Long testCategoryId;
    protected Long testMedicineId;
    protected Integer testSymptomId;
    protected Long testStockId;
    protected Long testUserId;
    protected Long testSaleRecordId;
    protected Long testPurchaseOrderId;
    protected Long testPredictionResultId;

    // 共享的分页对象
    protected Pageable pageable;

    @BeforeEach
    void setUp() {
        // 初始化分页对象
        pageable = PageRequest.of(0, 10);

        // 1. 创建分类
        if (testCategoryId == null) {
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
        }

        // 2. 创建症状
        if (testSymptomId == null) {
            Symptom symptom = new Symptom();
            symptom.setName("发烧");
            symptom.setDescription("体温升高症状");
            Symptom savedSymptom = symptomRepository.save(symptom);
            testSymptomId = savedSymptom.getId();
            assertNotNull(testSymptomId, "症状ID不应为空");
        }

        // 3. 创建用户
        if (testUserId == null) {
            User user = new User();
            user.setUsername("testuser");
            user.setPassword("password123");
            user.setRealName("测试用户");
            user.setPhone("13800138000");
            user.setEmail("test@example.com");
            user.setRole("PHARMACIST");
            user.setStatus(1);
            User savedUser = userService.save(user);
            testUserId = savedUser.getId();
            assertNotNull(testUserId, "用户ID不应为空");
        }

        // 4. 创建药品
        if (testMedicineId == null) {
            Medicine medicine = new Medicine();
            medicine.setMedicineCode("MED001");
            medicine.setName("测试药品");
            medicine.setGenericName("测试通用名");
            Category category = categoryRepository.findById(testCategoryId).orElse(null);
            assertNotNull(category, "分类应存在");
            medicine.setCategory(category);
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
            Symptom symptom = symptomRepository.findById(testSymptomId).orElse(null);
            assertNotNull(symptom, "症状应存在");
            medicine.setSymptoms(Arrays.asList(symptom));

            Medicine savedMedicine = medicineRepository.save(medicine);
            testMedicineId = savedMedicine.getId();
            assertNotNull(testMedicineId, "药品ID不应为空");
        }

        // 5. 创建库存
        if (testStockId == null) {
            Stock stock = new Stock();
            Medicine medicine = medicineRepository.findById(testMedicineId).orElse(null);
            assertNotNull(medicine, "药品应存在");
            stock.setMedicine(medicine);
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

        // 6. 创建销售记录
        if (testSaleRecordId == null) {
            SaleRecord saleRecord = new SaleRecord();
            Medicine medicine = medicineRepository.findById(testMedicineId).orElse(null);
            assertNotNull(medicine, "药品应存在");
            saleRecord.setMedicine(medicine);
            saleRecord.setQuantity(5);
            saleRecord.setUnitPrice(new BigDecimal("25.50"));
            saleRecord.setTotalAmount(new BigDecimal("127.50"));
            saleRecord.setRecordNo("SALE" + System.currentTimeMillis());
            saleRecord.setSaleTime(LocalDateTime.now());
            // 设置操作员
            User operator = userRepository.findById(testUserId).orElse(null);
            assertNotNull(operator, "操作员应存在");
            saleRecord.setOperator(operator);

            SaleRecord savedSaleRecord = saleRecordRepository.save(saleRecord);
            testSaleRecordId = savedSaleRecord.getId();
            assertNotNull(testSaleRecordId, "销售记录ID不应为空");
        }

        // 7. 创建采购订单
        if (testPurchaseOrderId == null) {
            PurchaseOrder purchaseOrder = new PurchaseOrder();
            Medicine medicine = medicineRepository.findById(testMedicineId).orElse(null);
            assertNotNull(medicine, "药品应存在");
            purchaseOrder.setMedicine(medicine);
            purchaseOrder.setQuantity(50);
            purchaseOrder.setUnitPrice(new BigDecimal("18.00"));
            purchaseOrder.setTotalAmount(new BigDecimal("900.00"));
            purchaseOrder.setOrderNo("PO" + System.currentTimeMillis());
            purchaseOrder.setOrderTime(LocalDateTime.now());
            purchaseOrder.setExpectedArrival(LocalDate.now().plusDays(3));
            purchaseOrder.setSupplier("测试供应商");
            purchaseOrder.setOrderStatus(0); // 待处理
            // 设置操作员
            User operator = userRepository.findById(testUserId).orElse(null);
            assertNotNull(operator, "操作员应存在");
            purchaseOrder.setOperator(operator);

            PurchaseOrder savedPurchaseOrder = purchaseOrderRepository.save(purchaseOrder);
            testPurchaseOrderId = savedPurchaseOrder.getId();
            assertNotNull(testPurchaseOrderId, "采购订单ID不应为空");
        }

        // 8. 创建预测结果
        if (testPredictionResultId == null) {
            PredictionResult predictionResult = new PredictionResult();
            Medicine medicine = medicineRepository.findById(testMedicineId).orElse(null);
            assertNotNull(medicine, "药品应存在");
            predictionResult.setMedicine(medicine);
            predictionResult.setPredictionDate(LocalDate.now().plusDays(7));
            predictionResult.setPredictedQuantity(20);
            predictionResult.setModelType("ARIMA");
            predictionResult.setAccuracyRate(new BigDecimal("85.00"));
            predictionResult.setConfidenceIntervalLower(15);
            predictionResult.setConfidenceIntervalUpper(25);
            predictionResult.setCreateTime(LocalDateTime.now());

            PredictionResult savedPredictionResult = predictionResultRepository.save(predictionResult);
            testPredictionResultId = savedPredictionResult.getId();
            assertNotNull(testPredictionResultId, "预测结果ID不应为空");
        }
    }

    @Test
    @Order(100)
    void contextLoads() {
        System.out.println("=== Spring上下文加载测试 ===");

        assertNotNull(categoryRepository, "CategoryRepository应被注入");
        assertNotNull(symptomRepository, "SymptomRepository应被注入");
        assertNotNull(medicineRepository, "MedicineRepository应被注入");
        assertNotNull(stockRepository, "StockRepository应被注入");
        assertNotNull(userRepository, "UserRepository应被注入");
        assertNotNull(saleRecordRepository, "SaleRecordRepository应被注入");
        assertNotNull(purchaseOrderRepository, "PurchaseOrderRepository应被注入");
        assertNotNull(predictionResultRepository, "PredictionResultRepository应被注入");

        assertNotNull(categoryService, "CategoryService应被注入");
        assertNotNull(symptomService, "SymptomService应被注入");
        assertNotNull(medicineService, "MedicineService应被注入");
        assertNotNull(stockService, "StockService应被注入");
        assertNotNull(userService, "UserService应被注入");
        assertNotNull(saleRecordService, "SaleRecordService应被注入");
        assertNotNull(purchaseOrderService, "PurchaseOrderService应被注入");
        assertNotNull(predictionResultService, "PredictionResultService应被注入");

        System.out.println("Spring上下文加载成功 ✓");
    }

    @Test
    @Order(99)
    void testTransactionRollback() {
        System.out.println("=== 测试事务回滚 ===");

        long categoryCount = categoryRepository.count();
        long medicineCount = medicineRepository.count();
        long userCount = userRepository.count();
        long stockCount = stockRepository.count();

        System.out.println("当前事务中的数据统计:");
        System.out.println("分类数量: " + categoryCount);
        System.out.println("药品数量: " + medicineCount);
        System.out.println("用户数量: " + userCount);
        System.out.println("库存数量: " + stockCount);

        // 这些数据会在方法结束后回滚
        assertTrue(categoryCount > 0, "事务中应有测试数据");
        assertTrue(medicineCount > 0, "事务中应有测试数据");
        assertTrue(userCount > 0, "事务中应有测试数据");
        assertTrue(stockCount > 0, "事务中应有测试数据");

        System.out.println("事务回滚测试完成，数据将在方法结束后回滚 ✓");
    }

    // 工具方法：生成唯一的字符串
    protected String generateUniqueString(String prefix) {
        return prefix + System.currentTimeMillis();
    }

    // 工具方法：验证对象不为空
    protected void assertNotNull(Object obj, String message) {
        org.junit.jupiter.api.Assertions.assertNotNull(obj, message);
    }

    // 工具方法：验证布尔值
    protected void assertTrue(boolean condition, String message) {
        org.junit.jupiter.api.Assertions.assertTrue(condition, message);
    }

    // 工具方法：验证布尔值
    protected void assertFalse(boolean condition, String message) {
        org.junit.jupiter.api.Assertions.assertFalse(condition, message);
    }

    // 工具方法：验证相等性
    protected void assertEquals(Object expected, Object actual, String message) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, actual, message);
    }

    // 工具方法：验证大小
    protected void assertSize(int expectedSize, Iterable<?> iterable, String message) {
        int actualSize = 0;
        if (iterable != null) {
            for (Object ignored : iterable) {
                actualSize++;
            }
        }
        assertEquals(expectedSize, actualSize, message);
    }
}
