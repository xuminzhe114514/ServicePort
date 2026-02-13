package com.example.demo;

import com.example.demo.entity.SaleRecord;
import org.junit.jupiter.api.*;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SaleRecordService测试")
class SaleRecordServiceTest extends BaseServiceTest {

    @Test
    @Order(1)
    @DisplayName("测试BaseService方法 - save")
    void testSave() {
        System.out.println("=== 测试SaleRecordService.save() ===");

        // 创建新销售记录
        SaleRecord saleRecord = new SaleRecord();
        saleRecord.setMedicine(medicineService.findById(testMedicineId));
        saleRecord.setQuantity(10);
        saleRecord.setUnitPrice(new BigDecimal("25.50"));
        saleRecord.setTotalAmount(new BigDecimal("255.00"));
        saleRecord.setRecordNo("SALE" + System.currentTimeMillis());
        saleRecord.setSaleTime(LocalDateTime.now());
        saleRecord.setOperator(userService.findById(testUserId));

        // 保存销售记录
        SaleRecord savedSaleRecord = saleRecordService.save(saleRecord);
        assertNotNull(savedSaleRecord, "保存的销售记录不应为空");
        assertNotNull(savedSaleRecord.getId(), "保存的销售记录ID不应为空");
        assertEquals(10, savedSaleRecord.getQuantity(), "销售数量应正确");

        System.out.println("SaleRecordService.save()测试通过 ✓");
    }

    @Test
    @Order(2)
    @DisplayName("测试BaseService方法 - update")
    void testUpdate() {
        System.out.println("=== 测试SaleRecordService.update() ===");

        // 获取测试销售记录
        SaleRecord saleRecord = saleRecordService.findById(testSaleRecordId);
        assertNotNull(saleRecord, "销售记录应存在");

        // 更新销售记录
        saleRecord.setQuantity(20); // 更改销售数量

        // 保存更新
        SaleRecord updatedSaleRecord = saleRecordService.update(saleRecord);
        assertNotNull(updatedSaleRecord, "更新后的销售记录不应为空");
        assertEquals(20, updatedSaleRecord.getQuantity(), "销售数量应已更新");

        System.out.println("SaleRecordService.update()测试通过 ✓");
    }

    @Test
    @Order(3)
    @DisplayName("测试BaseService方法 - delete")
    void testDelete() {
        System.out.println("=== 测试SaleRecordService.delete() ===");

        // 创建一个临时销售记录用于删除测试
        SaleRecord saleRecord = new SaleRecord();
        saleRecord.setMedicine(medicineService.findById(testMedicineId));
        saleRecord.setQuantity(3);
        saleRecord.setUnitPrice(new BigDecimal("25.50"));
        saleRecord.setTotalAmount(new BigDecimal("76.50"));
        saleRecord.setRecordNo("SALE" + System.currentTimeMillis());
        saleRecord.setSaleTime(LocalDateTime.now());
        saleRecord.setOperator(userService.findById(testUserId));
        SaleRecord savedSaleRecord = saleRecordService.save(saleRecord);
        Long tempSaleRecordId = savedSaleRecord.getId();
        assertNotNull(tempSaleRecordId, "临时销售记录ID不应为空");

        // 验证销售记录存在
        SaleRecord foundSaleRecord = saleRecordService.findById(tempSaleRecordId);
        assertNotNull(foundSaleRecord, "临时销售记录应存在");

        // 删除销售记录
        saleRecordService.delete(tempSaleRecordId);

        // 验证销售记录已删除
        SaleRecord deletedSaleRecord = saleRecordService.findById(tempSaleRecordId);
        assertNull(deletedSaleRecord, "删除后的销售记录应不存在");

        System.out.println("SaleRecordService.delete()测试通过 ✓");
    }

    @Test
    @Order(4)
    @DisplayName("测试BaseService方法 - findById")
    void testFindById() {
        System.out.println("=== 测试SaleRecordService.findById() ===");

        // 查找测试销售记录
        SaleRecord saleRecord = saleRecordService.findById(testSaleRecordId);
        assertNotNull(saleRecord, "销售记录应存在");
        assertNotNull(saleRecord.getRecordNo(), "销售记录编号不应为空");

        System.out.println("SaleRecordService.findById()测试通过 ✓");
    }

    @Test
    @Order(5)
    @DisplayName("测试BaseService方法 - findAll")
    void testFindAll() {
        System.out.println("=== 测试SaleRecordService.findAll() ===");

        // 测试无参findAll
        List<SaleRecord> saleRecords = saleRecordService.findAll();
        assertNotNull(saleRecords, "销售记录列表不应为空");
        assertTrue(saleRecords.size() > 0, "销售记录列表应包含数据");

        // 测试带分页的findAll
        Page<SaleRecord> saleRecordPage = saleRecordService.findAll(pageable);
        assertNotNull(saleRecordPage, "分页销售记录列表不应为空");
        assertTrue(saleRecordPage.getTotalElements() > 0, "分页销售记录列表应包含数据");

        System.out.println("SaleRecordService.findAll()测试通过 ✓");
    }

    @Test
    @Order(6)
    @DisplayName("测试BaseService方法 - saveAll")
    void testSaveAll() {
        System.out.println("=== 测试SaleRecordService.saveAll() ===");

        // 创建多个销售记录
        SaleRecord saleRecord1 = new SaleRecord();
        saleRecord1.setMedicine(medicineService.findById(testMedicineId));
        saleRecord1.setQuantity(2);
        saleRecord1.setUnitPrice(new BigDecimal("25.50"));
        saleRecord1.setTotalAmount(new BigDecimal("51.00"));
        saleRecord1.setRecordNo("SALE" + System.currentTimeMillis() + "1");
        saleRecord1.setSaleTime(LocalDateTime.now());
        saleRecord1.setOperator(userService.findById(testUserId));

        SaleRecord saleRecord2 = new SaleRecord();
        saleRecord2.setMedicine(medicineService.findById(testMedicineId));
        saleRecord2.setQuantity(4);
        saleRecord2.setUnitPrice(new BigDecimal("25.50"));
        saleRecord2.setTotalAmount(new BigDecimal("102.00"));
        saleRecord2.setRecordNo("SALE" + System.currentTimeMillis() + "2");
        saleRecord2.setSaleTime(LocalDateTime.now());
        saleRecord2.setOperator(userService.findById(testUserId));

        List<SaleRecord> saleRecords = List.of(saleRecord1, saleRecord2);

        // 批量保存
        List<SaleRecord> savedSaleRecords = saleRecordService.saveAll(saleRecords);
        assertNotNull(savedSaleRecords, "批量保存的销售记录列表不应为空");
        assertEquals(2, savedSaleRecords.size(), "批量保存的销售记录数量应正确");
        for (SaleRecord savedSaleRecord : savedSaleRecords) {
            assertNotNull(savedSaleRecord.getId(), "保存的销售记录ID不应为空");
        }

        System.out.println("SaleRecordService.saveAll()测试通过 ✓");
    }

    @Test
    @Order(7)
    @DisplayName("测试BaseService方法 - deleteAll")
    void testDeleteAll() {
        System.out.println("=== 测试SaleRecordService.deleteAll() ===");

        // 创建多个临时销售记录用于删除测试
        SaleRecord saleRecord1 = new SaleRecord();
        saleRecord1.setMedicine(medicineService.findById(testMedicineId));
        saleRecord1.setQuantity(1);
        saleRecord1.setUnitPrice(new BigDecimal("25.50"));
        saleRecord1.setTotalAmount(new BigDecimal("25.50"));
        saleRecord1.setRecordNo("SALE" + System.currentTimeMillis() + "3");
        saleRecord1.setSaleTime(LocalDateTime.now());
        saleRecord1.setOperator(userService.findById(testUserId));

        SaleRecord saleRecord2 = new SaleRecord();
        saleRecord2.setMedicine(medicineService.findById(testMedicineId));
        saleRecord2.setQuantity(2);
        saleRecord2.setUnitPrice(new BigDecimal("25.50"));
        saleRecord2.setTotalAmount(new BigDecimal("51.00"));
        saleRecord2.setRecordNo("SALE" + System.currentTimeMillis() + "4");
        saleRecord2.setSaleTime(LocalDateTime.now());
        saleRecord2.setOperator(userService.findById(testUserId));

        List<SaleRecord> saleRecords = List.of(saleRecord1, saleRecord2);
        List<SaleRecord> savedSaleRecords = saleRecordService.saveAll(saleRecords);
        List<Long> ids = savedSaleRecords.stream().map(SaleRecord::getId).toList();

        // 验证销售记录存在
        for (Long id : ids) {
            assertNotNull(saleRecordService.findById(id), "临时销售记录应存在");
        }

        // 批量删除
        saleRecordService.deleteAll(ids);

        // 验证销售记录已删除
        for (Long id : ids) {
            assertNull(saleRecordService.findById(id), "删除后的销售记录应不存在");
        }

        System.out.println("SaleRecordService.deleteAll()测试通过 ✓");
    }

    @Test
    @Order(8)
    @DisplayName("测试BaseService方法 - exists")
    void testExists() {
        System.out.println("=== 测试SaleRecordService.exists() ===");

        // 测试存在的销售记录
        boolean exists = saleRecordService.exists(testSaleRecordId);
        assertTrue(exists, "测试销售记录应存在");

        // 测试不存在的销售记录
        boolean notExists = saleRecordService.exists(999999L);
        assertFalse(notExists, "不存在的销售记录应返回false");

        System.out.println("SaleRecordService.exists()测试通过 ✓");
    }

    @Test
    @Order(10)
    @DisplayName("测试SaleRecordService特有方法 - findByRecordNo")
    void testFindByRecordNo() {
        System.out.println("=== 测试SaleRecordService.findByRecordNo() ===");

        // 获取测试销售记录的编号
        SaleRecord testRecord = saleRecordService.findById(testSaleRecordId);
        assertNotNull(testRecord, "测试销售记录应存在");
        String recordNo = testRecord.getRecordNo();
        assertNotNull(recordNo, "销售记录编号不应为空");

        // 测试查找销售记录编号
        SaleRecord saleRecord = saleRecordService.findByRecordNo(recordNo);
        assertNotNull(saleRecord, "销售记录应存在");
        assertEquals(recordNo, saleRecord.getRecordNo(), "销售记录编号应正确");

        System.out.println("SaleRecordService.findByRecordNo()测试通过 ✓");
    }

    @Test
    @Order(11)
    @DisplayName("测试SaleRecordService特有方法 - findBySaleTimeBetween")
    void testFindBySaleTimeBetween() {
        System.out.println("=== 测试SaleRecordService.findBySaleTimeBetween() ===");

        // 测试查找指定时间范围内的销售记录
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(1);
        List<SaleRecord> saleRecords = saleRecordService.findBySaleTimeBetween(startTime, endTime);
        assertNotNull(saleRecords, "指定时间范围内的销售记录列表不应为空");
        assertTrue(saleRecords.size() > 0, "指定时间范围内的销售记录列表应包含数据");

        System.out.println("SaleRecordService.findBySaleTimeBetween()测试通过 ✓");
    }

    @Test
    @Order(12)
    @DisplayName("测试SaleRecordService特有方法 - findByMedicineId")
    void testFindByMedicineId() {
        System.out.println("=== 测试SaleRecordService.findByMedicineId() ===");

        // 测试查找指定药品的销售记录
        List<SaleRecord> medicineSaleRecords = saleRecordService.findByMedicineId(testMedicineId);
        assertNotNull(medicineSaleRecords, "指定药品的销售记录列表不应为空");
        assertTrue(medicineSaleRecords.size() > 0, "指定药品的销售记录列表应包含数据");

        System.out.println("SaleRecordService.findByMedicineId()测试通过 ✓");
    }

    @Test
    @Order(13)
    @DisplayName("测试SaleRecordService特有方法 - findByOperatorId")
    void testFindByOperatorId() {
        System.out.println("=== 测试SaleRecordService.findByOperatorId() ===");

        // 测试查找指定操作员的销售记录
        List<SaleRecord> operatorSaleRecords = saleRecordService.findByOperatorId(testUserId);
        assertNotNull(operatorSaleRecords, "指定操作员的销售记录列表不应为空");
        assertTrue(operatorSaleRecords.size() > 0, "指定操作员的销售记录列表应包含数据");

        System.out.println("SaleRecordService.findByOperatorId()测试通过 ✓");
    }

    @Test
    @Order(14)
    @DisplayName("测试SaleRecordService特有方法 - getTotalSalesByPeriod")
    void testGetTotalSalesByPeriod() {
        System.out.println("=== 测试SaleRecordService.getTotalSalesByPeriod() ===");

        // 测试获取指定时间段的总销售额
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(1);
        Double totalSales = saleRecordService.getTotalSalesByPeriod(startTime, endTime);
        assertNotNull(totalSales, "总销售额不应为空");
        assertTrue(totalSales >= 0, "总销售额应大于等于0");

        System.out.println("SaleRecordService.getTotalSalesByPeriod()测试通过 ✓");
    }

    @Test
    @Order(15)
    @DisplayName("测试SaleRecordService特有方法 - getTotalQuantityByMedicineId")
    void testGetTotalQuantityByMedicineId() {
        System.out.println("=== 测试SaleRecordService.getTotalQuantityByMedicineId() ===");

        // 测试获取指定药品的总销售数量
        Integer totalQuantity = saleRecordService.getTotalQuantityByMedicineId(testMedicineId);
        assertNotNull(totalQuantity, "总销售数量不应为空");
        assertTrue(totalQuantity >= 0, "总销售数量应大于等于0");

        System.out.println("SaleRecordService.getTotalQuantityByMedicineId()测试通过 ✓");
    }

    @Test
    @Order(16)
    @DisplayName("测试SaleRecordService特有方法 - getDailySalesReport")
    void testGetDailySalesReport() {
        System.out.println("=== 测试SaleRecordService.getDailySalesReport() ===");

        // 测试获取每日销售报告
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        List<Map<String, Object>> dailySalesReport = saleRecordService.getDailySalesReport(startDate, endDate);
        assertNotNull(dailySalesReport, "每日销售报告不应为空");

        System.out.println("SaleRecordService.getDailySalesReport()测试通过 ✓");
    }

    @Test
    @Order(17)
    @DisplayName("测试SaleRecordService特有方法 - getTopSellingMedicines")
    void testGetTopSellingMedicines() {
        System.out.println("=== 测试SaleRecordService.getTopSellingMedicines() ===");

        // 测试获取畅销药品
        int limit = 5;
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        List<Map<String, Object>> topSellingMedicines = saleRecordService.getTopSellingMedicines(limit, startDate, endDate);
        assertNotNull(topSellingMedicines, "畅销药品列表不应为空");

        System.out.println("SaleRecordService.getTopSellingMedicines()测试通过 ✓");
    }

    @Test
    @Order(18)
    @DisplayName("测试SaleRecordService特有方法 - createSaleRecord")
    void testCreateSaleRecord() {
        System.out.println("=== 测试SaleRecordService.createSaleRecord() ===");

        // 创建销售记录
        SaleRecord saleRecord = new SaleRecord();
        saleRecord.setMedicine(medicineService.findById(testMedicineId));
        saleRecord.setQuantity(3);
        saleRecord.setUnitPrice(new BigDecimal("25.50"));
        saleRecord.setTotalAmount(new BigDecimal("76.50"));
        saleRecord.setRecordNo("SALE" + System.currentTimeMillis());
        saleRecord.setSaleTime(LocalDateTime.now());

        // 测试创建销售记录
        SaleRecord createdSaleRecord = saleRecordService.createSaleRecord(saleRecord, testUserId);
        assertNotNull(createdSaleRecord, "创建的销售记录不应为空");
        assertNotNull(createdSaleRecord.getId(), "创建的销售记录ID不应为空");
        assertNotNull(createdSaleRecord.getOperator(), "操作员不应为空");
        assertEquals(testUserId, createdSaleRecord.getOperator().getId(), "操作员ID应正确");

        System.out.println("SaleRecordService.createSaleRecord()测试通过 ✓");
    }
}
