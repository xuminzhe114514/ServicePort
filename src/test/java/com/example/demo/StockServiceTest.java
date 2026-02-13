package com.example.demo;

import com.example.demo.entity.Stock;
import org.junit.jupiter.api.*;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StockService测试")
class StockServiceTest extends BaseServiceTest {

    @Test
    @Order(1)
    @DisplayName("测试BaseService方法 - save")
    void testSave() {
        System.out.println("=== 测试StockService.save() ===");

        // 创建新库存
        Stock stock = new Stock();
        stock.setMedicine(medicineService.findById(testMedicineId));
        stock.setBatchNumber("BATCH202502");
        stock.setProductionDate(LocalDate.now().minusDays(90));
        stock.setExpirationDate(LocalDate.now().plusDays(270));
        stock.setQuantity(200);
        stock.setWarningQuantity(40);
        stock.setShelfLocation("B区-02架");
        stock.setStatus(1);
        stock.setMinimumOrderQuantity(100);
        stock.setLeadTimeDays(5);
        stock.setReorderPoint(60);

        // 保存库存
        Stock savedStock = stockService.save(stock);
        assertNotNull(savedStock, "保存的库存不应为空");
        assertNotNull(savedStock.getId(), "保存的库存ID不应为空");
        assertEquals("BATCH202502", savedStock.getBatchNumber(), "批次号应正确");

        System.out.println("StockService.save()测试通过 ✓");
    }

    @Test
    @Order(2)
    @DisplayName("测试BaseService方法 - update")
    void testUpdate() {
        System.out.println("=== 测试StockService.update() ===");

        // 获取测试库存
        Stock stock = stockService.findById(testStockId);
        assertNotNull(stock, "库存应存在");

        // 更新库存
        stock.setQuantity(150);
        stock.setShelfLocation("A区-02架");

        // 保存更新
        Stock updatedStock = stockService.update(stock);
        assertNotNull(updatedStock, "更新后的库存不应为空");
        assertEquals(150, updatedStock.getQuantity(), "库存数量应已更新");
        assertEquals("A区-02架", updatedStock.getShelfLocation(), "货架位置应已更新");

        System.out.println("StockService.update()测试通过 ✓");
    }

    @Test
    @Order(3)
    @DisplayName("测试BaseService方法 - delete")
    void testDelete() {
        System.out.println("=== 测试StockService.delete() ===");

        // 创建一个临时库存用于删除测试
        Stock stock = new Stock();
        stock.setMedicine(medicineService.findById(testMedicineId));
        stock.setBatchNumber("BATCH999999");
        stock.setProductionDate(LocalDate.now().minusDays(90));
        stock.setExpirationDate(LocalDate.now().plusDays(270));
        stock.setQuantity(50);
        stock.setWarningQuantity(10);
        stock.setShelfLocation("C区-01架");
        stock.setStatus(1);
        stock.setMinimumOrderQuantity(30);
        stock.setLeadTimeDays(3);
        stock.setReorderPoint(15);
        Stock savedStock = stockService.save(stock);
        Long tempStockId = savedStock.getId();
        assertNotNull(tempStockId, "临时库存ID不应为空");

        // 验证库存存在
        Stock foundStock = stockService.findById(tempStockId);
        assertNotNull(foundStock, "临时库存应存在");

        // 删除库存
        stockService.delete(tempStockId);

        // 验证库存已删除
        Stock deletedStock = stockService.findById(tempStockId);
        assertNull(deletedStock, "删除后的库存应不存在");

        System.out.println("StockService.delete()测试通过 ✓");
    }

    @Test
    @Order(4)
    @DisplayName("测试BaseService方法 - findById")
    void testFindById() {
        System.out.println("=== 测试StockService.findById() ===");

        // 查找测试库存
        Stock stock = stockService.findById(testStockId);
        assertNotNull(stock, "库存应存在");
        assertEquals("BATCH202501", stock.getBatchNumber(), "批次号应正确");

        System.out.println("StockService.findById()测试通过 ✓");
    }

    @Test
    @Order(5)
    @DisplayName("测试BaseService方法 - findAll")
    void testFindAll() {
        System.out.println("=== 测试StockService.findAll() ===");

        // 测试无参findAll
        List<Stock> stocks = stockService.findAll();
        assertNotNull(stocks, "库存列表不应为空");
        assertTrue(stocks.size() > 0, "库存列表应包含数据");

        // 测试带分页的findAll
        Page<Stock> stockPage = stockService.findAll(pageable);
        assertNotNull(stockPage, "分页库存列表不应为空");
        assertTrue(stockPage.getTotalElements() > 0, "分页库存列表应包含数据");

        System.out.println("StockService.findAll()测试通过 ✓");
    }

    @Test
    @Order(6)
    @DisplayName("测试BaseService方法 - saveAll")
    void testSaveAll() {
        System.out.println("=== 测试StockService.saveAll() ===");

        // 创建多个库存
        Stock stock1 = new Stock();
        stock1.setMedicine(medicineService.findById(testMedicineId));
        stock1.setBatchNumber("BATCH202503");
        stock1.setProductionDate(LocalDate.now().minusDays(60));
        stock1.setExpirationDate(LocalDate.now().plusDays(300));
        stock1.setQuantity(100);
        stock1.setWarningQuantity(20);
        stock1.setShelfLocation("B区-01架");
        stock1.setStatus(1);
        stock1.setMinimumOrderQuantity(50);
        stock1.setLeadTimeDays(3);
        stock1.setReorderPoint(30);

        Stock stock2 = new Stock();
        stock2.setMedicine(medicineService.findById(testMedicineId));
        stock2.setBatchNumber("BATCH202504");
        stock2.setProductionDate(LocalDate.now().minusDays(30));
        stock2.setExpirationDate(LocalDate.now().plusDays(330));
        stock2.setQuantity(150);
        stock2.setWarningQuantity(30);
        stock2.setShelfLocation("B区-02架");
        stock2.setStatus(1);
        stock2.setMinimumOrderQuantity(75);
        stock2.setLeadTimeDays(3);
        stock2.setReorderPoint(45);

        List<Stock> stocks = List.of(stock1, stock2);

        // 批量保存
        List<Stock> savedStocks = stockService.saveAll(stocks);
        assertNotNull(savedStocks, "批量保存的库存列表不应为空");
        assertEquals(2, savedStocks.size(), "批量保存的库存数量应正确");
        for (Stock savedStock : savedStocks) {
            assertNotNull(savedStock.getId(), "保存的库存ID不应为空");
        }

        System.out.println("StockService.saveAll()测试通过 ✓");
    }

    @Test
    @Order(7)
    @DisplayName("测试BaseService方法 - deleteAll")
    void testDeleteAll() {
        System.out.println("=== 测试StockService.deleteAll() ===");

        // 创建多个临时库存用于删除测试
        Stock stock1 = new Stock();
        stock1.setMedicine(medicineService.findById(testMedicineId));
        stock1.setBatchNumber("BATCH999997");
        stock1.setProductionDate(LocalDate.now().minusDays(60));
        stock1.setExpirationDate(LocalDate.now().plusDays(300));
        stock1.setQuantity(50);
        stock1.setWarningQuantity(10);
        stock1.setShelfLocation("C区-02架");
        stock1.setStatus(1);
        stock1.setMinimumOrderQuantity(30);
        stock1.setLeadTimeDays(3);
        stock1.setReorderPoint(15);

        Stock stock2 = new Stock();
        stock2.setMedicine(medicineService.findById(testMedicineId));
        stock2.setBatchNumber("BATCH999998");
        stock2.setProductionDate(LocalDate.now().minusDays(30));
        stock2.setExpirationDate(LocalDate.now().plusDays(330));
        stock2.setQuantity(75);
        stock2.setWarningQuantity(15);
        stock2.setShelfLocation("C区-03架");
        stock2.setStatus(1);
        stock2.setMinimumOrderQuantity(45);
        stock2.setLeadTimeDays(3);
        stock2.setReorderPoint(22);

        List<Stock> stocks = List.of(stock1, stock2);
        List<Stock> savedStocks = stockService.saveAll(stocks);
        List<Long> ids = savedStocks.stream().map(Stock::getId).toList();

        // 验证库存存在
        for (Long id : ids) {
            assertNotNull(stockService.findById(id), "临时库存应存在");
        }

        // 批量删除
        stockService.deleteAll(ids);

        // 验证库存已删除
        for (Long id : ids) {
            assertNull(stockService.findById(id), "删除后的库存应不存在");
        }

        System.out.println("StockService.deleteAll()测试通过 ✓");
    }

    @Test
    @Order(8)
    @DisplayName("测试BaseService方法 - exists")
    void testExists() {
        System.out.println("=== 测试StockService.exists() ===");

        // 测试存在的库存
        boolean exists = stockService.exists(testStockId);
        assertTrue(exists, "测试库存应存在");

        // 测试不存在的库存
        boolean notExists = stockService.exists(999999L);
        assertFalse(notExists, "不存在的库存应返回false");

        System.out.println("StockService.exists()测试通过 ✓");
    }

    @Test
    @Order(10)
    @DisplayName("测试StockService特有方法 - findByMedicineId")
    void testFindByMedicineId() {
        System.out.println("=== 测试StockService.findByMedicineId() ===");

        // 测试查找指定药品的库存
        Page<Stock> medicineStocks = stockService.findByMedicineId(testMedicineId, pageable);
        assertNotNull(medicineStocks, "指定药品的库存列表不应为空");
        assertTrue(medicineStocks.getTotalElements() > 0, "指定药品的库存列表应包含数据");

        System.out.println("StockService.findByMedicineId()测试通过 ✓");
    }

    @Test
    @Order(11)
    @DisplayName("测试StockService特有方法 - getTotalStock")
    void testGetTotalStock() {
        System.out.println("=== 测试StockService.getTotalStock() ===");

        // 测试获取药品总库存
        Integer totalStock = stockService.getTotalStock(testMedicineId);
        assertNotNull(totalStock, "总库存不应为空");
        assertTrue(totalStock > 0, "总库存应大于0");

        System.out.println("StockService.getTotalStock()测试通过 ✓");
    }

    @Test
    @Order(12)
    @DisplayName("测试StockService特有方法 - getExpiringStock")
    void testGetExpiringStock() {
        System.out.println("=== 测试StockService.getExpiringStock() ===");

        // 测试获取即将过期的库存
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(200);
        Page<Stock> expiringStocks = stockService.getExpiringStock(startDate, endDate, pageable);
        assertNotNull(expiringStocks, "即将过期的库存列表不应为空");

        System.out.println("StockService.getExpiringStock()测试通过 ✓");
    }

    @Test
    @Order(13)
    @DisplayName("测试StockService特有方法 - getLowStock")
    void testGetLowStock() {
        System.out.println("=== 测试StockService.getLowStock() ===");

        // 测试获取低库存
        Page<Stock> lowStocks = stockService.getLowStock(pageable);
        assertNotNull(lowStocks, "低库存列表不应为空");

        System.out.println("StockService.getLowStock()测试通过 ✓");
    }

    @Test
    @Order(14)
    @DisplayName("测试StockService特有方法 - getLowStockSummary")
    void testGetLowStockSummary() {
        System.out.println("=== 测试StockService.getLowStockSummary() ===");

        // 测试获取低库存摘要
        Map<Long, Integer> lowStockSummary = stockService.getLowStockSummary();
        assertNotNull(lowStockSummary, "低库存摘要不应为空");

        System.out.println("StockService.getLowStockSummary()测试通过 ✓");
    }

    @Test
    @Order(15)
    @DisplayName("测试StockService特有方法 - reduceStock")
    void testReduceStock() {
        System.out.println("=== 测试StockService.reduceStock() ===");

        // 获取当前库存数量
        Stock stock = stockService.findById(testStockId);
        assertNotNull(stock, "库存应存在");
        Integer originalQuantity = stock.getQuantity();
        assertTrue(originalQuantity > 0, "原始库存数量应大于0");

        // 减少库存
        int reduceAmount = 10;
        stockService.reduceStock(testMedicineId, reduceAmount);

        // 验证库存已减少
        Stock updatedStock = stockService.findById(testStockId);
        assertNotNull(updatedStock, "更新后的库存不应为空");
        assertEquals(originalQuantity - reduceAmount, updatedStock.getQuantity(), "库存数量应已减少");

        System.out.println("StockService.reduceStock()测试通过 ✓");
    }

    @Test
    @Order(16)
    @DisplayName("测试StockService特有方法 - increaseStock")
    void testIncreaseStock() {
        System.out.println("=== 测试StockService.increaseStock() ===");

        // 增加库存
        int increaseAmount = 50;
        String batchNumber = "BATCH202505";
        LocalDate expirationDate = LocalDate.now().plusDays(365);
        stockService.increaseStock(testMedicineId, increaseAmount, batchNumber, expirationDate);

        // 验证库存已增加
        Integer totalStock = stockService.getTotalStock(testMedicineId);
        assertNotNull(totalStock, "总库存不应为空");
        assertTrue(totalStock > 0, "总库存应大于0");

        System.out.println("StockService.increaseStock()测试通过 ✓");
    }

    @Test
    @Order(17)
    @DisplayName("测试StockService特有方法 - checkStockAvailability")
    void testCheckStockAvailability() {
        System.out.println("=== 测试StockService.checkStockAvailability() ===");

        // 测试检查库存可用性（足够）
        boolean available = stockService.checkStockAvailability(testMedicineId, 10);
        assertTrue(available, "库存应足够");

        // 测试检查库存可用性（不足）
        boolean notAvailable = stockService.checkStockAvailability(testMedicineId, 1000);
        assertFalse(notAvailable, "库存应不足");

        System.out.println("StockService.checkStockAvailability()测试通过 ✓");
    }

    @Test
    @Order(18)
    @DisplayName("测试StockService特有方法 - findByStatus")
    void testFindByStatus() {
        System.out.println("=== 测试StockService.findByStatus() ===");

        // 测试查找状态为1的库存
        Page<Stock> activeStocks = stockService.findByStatus(1, pageable);
        assertNotNull(activeStocks, "状态为1的库存列表不应为空");
        assertTrue(activeStocks.getTotalElements() > 0, "状态为1的库存列表应包含数据");

        System.out.println("StockService.findByStatus()测试通过 ✓");
    }

    @Test
    @Order(19)
    @DisplayName("测试StockService特有方法 - findByBatchNumber")
    void testFindByBatchNumber() {
        System.out.println("=== 测试StockService.findByBatchNumber() ===");

        // 测试查找指定批次号的库存
        Page<Stock> batchStocks = stockService.findByBatchNumber("BATCH202501", pageable);
        assertNotNull(batchStocks, "指定批次号的库存列表不应为空");
        assertTrue(batchStocks.getTotalElements() > 0, "指定批次号的库存列表应包含数据");

        System.out.println("StockService.findByBatchNumber()测试通过 ✓");
    }

    @Test
    @Order(20)
    @DisplayName("测试StockService特有方法 - findByShelfLocation")
    void testFindByShelfLocation() {
        System.out.println("=== 测试StockService.findByShelfLocation() ===");

        // 测试查找指定货架位置的库存
        Page<Stock> shelfStocks = stockService.findByShelfLocation("A区-01架", pageable);
        assertNotNull(shelfStocks, "指定货架位置的库存列表不应为空");

        System.out.println("StockService.findByShelfLocation()测试通过 ✓");
    }

    @Test
    @Order(21)
    @DisplayName("测试StockService特有方法 - findExpiringWithinDays")
    void testFindExpiringWithinDays() {
        System.out.println("=== 测试StockService.findExpiringWithinDays() ===");

        // 测试查找指定天数内过期的库存
        List<Stock> expiringStocks = stockService.findExpiringWithinDays(200);
        assertNotNull(expiringStocks, "指定天数内过期的库存列表不应为空");

        System.out.println("StockService.findExpiringWithinDays()测试通过 ✓");
    }

    @Test
    @Order(22)
    @DisplayName("测试StockService特有方法 - getStockStatisticsByMedicine")
    void testGetStockStatisticsByMedicine() {
        System.out.println("=== 测试StockService.getStockStatisticsByMedicine() ===");

        // 测试获取药品库存统计
        Map<String, Object> stockStatistics = stockService.getStockStatisticsByMedicine(testMedicineId);
        assertNotNull(stockStatistics, "药品库存统计不应为空");

        System.out.println("StockService.getStockStatisticsByMedicine()测试通过 ✓");
    }

    @Test
    @Order(23)
    @DisplayName("测试StockService特有方法 - findByMedicineIdAndBatchNumber")
    void testFindByMedicineIdAndBatchNumber() {
        System.out.println("=== 测试StockService.findByMedicineIdAndBatchNumber() ===");

        // 测试查找指定药品和批次号的库存
        Page<Stock> stocks = stockService.findByMedicineIdAndBatchNumber(testMedicineId, "BATCH202501", pageable);
        assertNotNull(stocks, "指定药品和批次号的库存列表不应为空");

        System.out.println("StockService.findByMedicineIdAndBatchNumber()测试通过 ✓");
    }

    @Test
    @Order(24)
    @DisplayName("测试StockService特有方法 - findExpiredStock")
    void testFindExpiredStock() {
        System.out.println("=== 测试StockService.findExpiredStock() ===");

        // 测试查找已过期的库存
        Page<Stock> expiredStocks = stockService.findExpiredStock(pageable);
        assertNotNull(expiredStocks, "已过期的库存列表不应为空");

        System.out.println("StockService.findExpiredStock()测试通过 ✓");
    }

    @Test
    @Order(25)
    @DisplayName("测试StockService特有方法 - findNearExpiryStock")
    void testFindNearExpiryStock() {
        System.out.println("=== 测试StockService.findNearExpiryStock() ===");

        // 测试查找近期过期的库存
        Page<Stock> nearExpiryStocks = stockService.findNearExpiryStock(200, pageable);
        assertNotNull(nearExpiryStocks, "近期过期的库存列表不应为空");

        System.out.println("StockService.findNearExpiryStock()测试通过 ✓");
    }
}
