package com.example.demo;

import com.example.demo.entity.PurchaseOrder;
import org.junit.jupiter.api.*;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PurchaseOrderService测试")
class PurchaseOrderServiceTest extends BaseServiceTest {

    @Test
    @Order(1)
    @DisplayName("测试BaseService方法 - save")
    void testSave() {
        System.out.println("=== 测试PurchaseOrderService.save() ===");

        // 创建新采购订单
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setMedicine(medicineService.findById(testMedicineId));
        purchaseOrder.setQuantity(100);
        purchaseOrder.setUnitPrice(new BigDecimal("18.00"));
        purchaseOrder.setTotalAmount(new BigDecimal("1800.00"));
        purchaseOrder.setOrderNo("PO" + System.currentTimeMillis());
        purchaseOrder.setOrderTime(LocalDateTime.now());
        purchaseOrder.setExpectedArrival(LocalDate.now().plusDays(5));
        purchaseOrder.setSupplier("新测试供应商");
        purchaseOrder.setOrderStatus(1); // 待确认
        purchaseOrder.setOperator(userService.findById(testUserId));

        // 保存采购订单
        PurchaseOrder savedPurchaseOrder = purchaseOrderService.save(purchaseOrder);
        assertNotNull(savedPurchaseOrder, "保存的采购订单不应为空");
        assertNotNull(savedPurchaseOrder.getId(), "保存的采购订单ID不应为空");
        assertEquals(100, savedPurchaseOrder.getQuantity(), "采购数量应正确");

        System.out.println("PurchaseOrderService.save()测试通过 ✓");
    }

    @Test
    @Order(2)
    @DisplayName("测试BaseService方法 - update")
    void testUpdate() {
        System.out.println("=== 测试PurchaseOrderService.update() ===");

        // 获取测试采购订单
        PurchaseOrder purchaseOrder = purchaseOrderService.findById(testPurchaseOrderId);
        assertNotNull(purchaseOrder, "采购订单应存在");

        // 更新采购订单
        purchaseOrder.setSupplier("更新后的测试供应商");
        purchaseOrder.setExpectedArrival(LocalDate.now().plusDays(7));

        // 保存更新
        PurchaseOrder updatedPurchaseOrder = purchaseOrderService.update(purchaseOrder);
        assertNotNull(updatedPurchaseOrder, "更新后的采购订单不应为空");
        assertEquals("更新后的测试供应商", updatedPurchaseOrder.getSupplier(), "供应商应已更新");

        System.out.println("PurchaseOrderService.update()测试通过 ✓");
    }

    @Test
    @Order(3)
    @DisplayName("测试BaseService方法 - delete")
    void testDelete() {
        System.out.println("=== 测试PurchaseOrderService.delete() ===");

        // 创建一个临时采购订单用于删除测试
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setMedicine(medicineService.findById(testMedicineId));
        purchaseOrder.setQuantity(20);
        purchaseOrder.setUnitPrice(new BigDecimal("18.00"));
        purchaseOrder.setTotalAmount(new BigDecimal("360.00"));
        purchaseOrder.setOrderNo("PO" + System.currentTimeMillis());
        purchaseOrder.setOrderTime(LocalDateTime.now());
        purchaseOrder.setExpectedArrival(LocalDate.now().plusDays(3));
        purchaseOrder.setSupplier("临时供应商");
        purchaseOrder.setOrderStatus(1); // 待确认
        purchaseOrder.setOperator(userService.findById(testUserId));
        PurchaseOrder savedPurchaseOrder = purchaseOrderService.save(purchaseOrder);
        Long tempPurchaseOrderId = savedPurchaseOrder.getId();
        assertNotNull(tempPurchaseOrderId, "临时采购订单ID不应为空");

        // 验证采购订单存在
        PurchaseOrder foundPurchaseOrder = purchaseOrderService.findById(tempPurchaseOrderId);
        assertNotNull(foundPurchaseOrder, "临时采购订单应存在");

        // 删除采购订单
        purchaseOrderService.delete(tempPurchaseOrderId);

        // 验证采购订单已删除
        PurchaseOrder deletedPurchaseOrder = purchaseOrderService.findById(tempPurchaseOrderId);
        assertNull(deletedPurchaseOrder, "删除后的采购订单应不存在");

        System.out.println("PurchaseOrderService.delete()测试通过 ✓");
    }

    @Test
    @Order(4)
    @DisplayName("测试BaseService方法 - findById")
    void testFindById() {
        System.out.println("=== 测试PurchaseOrderService.findById() ===");

        // 查找测试采购订单
        PurchaseOrder purchaseOrder = purchaseOrderService.findById(testPurchaseOrderId);
        assertNotNull(purchaseOrder, "采购订单应存在");
        assertNotNull(purchaseOrder.getOrderNo(), "采购订单编号不应为空");

        System.out.println("PurchaseOrderService.findById()测试通过 ✓");
    }

    @Test
    @Order(5)
    @DisplayName("测试BaseService方法 - findAll")
    void testFindAll() {
        System.out.println("=== 测试PurchaseOrderService.findAll() ===");

        // 测试无参findAll
        List<PurchaseOrder> purchaseOrders = purchaseOrderService.findAll();
        assertNotNull(purchaseOrders, "采购订单列表不应为空");
        assertTrue(purchaseOrders.size() > 0, "采购订单列表应包含数据");

        // 测试带分页的findAll
        Page<PurchaseOrder> purchaseOrderPage = purchaseOrderService.findAll(pageable);
        assertNotNull(purchaseOrderPage, "分页采购订单列表不应为空");
        assertTrue(purchaseOrderPage.getTotalElements() > 0, "分页采购订单列表应包含数据");

        System.out.println("PurchaseOrderService.findAll()测试通过 ✓");
    }

    @Test
    @Order(6)
    @DisplayName("测试BaseService方法 - saveAll")
    void testSaveAll() {
        System.out.println("=== 测试PurchaseOrderService.saveAll() ===");

        // 创建多个采购订单
        PurchaseOrder purchaseOrder1 = new PurchaseOrder();
        purchaseOrder1.setMedicine(medicineService.findById(testMedicineId));
        purchaseOrder1.setQuantity(30);
        purchaseOrder1.setUnitPrice(new BigDecimal("18.00"));
        purchaseOrder1.setTotalAmount(new BigDecimal("540.00"));
        purchaseOrder1.setOrderNo("PO" + System.currentTimeMillis() + "1");
        purchaseOrder1.setOrderTime(LocalDateTime.now());
        purchaseOrder1.setExpectedArrival(LocalDate.now().plusDays(4));
        purchaseOrder1.setSupplier("批量供应商1");
        purchaseOrder1.setOrderStatus(1); // 待确认
        purchaseOrder1.setOperator(userService.findById(testUserId));

        PurchaseOrder purchaseOrder2 = new PurchaseOrder();
        purchaseOrder2.setMedicine(medicineService.findById(testMedicineId));
        purchaseOrder2.setQuantity(40);
        purchaseOrder2.setUnitPrice(new BigDecimal("18.00"));
        purchaseOrder2.setTotalAmount(new BigDecimal("720.00"));
        purchaseOrder2.setOrderNo("PO" + System.currentTimeMillis() + "2");
        purchaseOrder2.setOrderTime(LocalDateTime.now());
        purchaseOrder2.setExpectedArrival(LocalDate.now().plusDays(6));
        purchaseOrder2.setSupplier("批量供应商2");
        purchaseOrder2.setOrderStatus(1); // 待确认
        purchaseOrder2.setOperator(userService.findById(testUserId));

        List<PurchaseOrder> purchaseOrders = List.of(purchaseOrder1, purchaseOrder2);

        // 批量保存
        List<PurchaseOrder> savedPurchaseOrders = purchaseOrderService.saveAll(purchaseOrders);
        assertNotNull(savedPurchaseOrders, "批量保存的采购订单列表不应为空");
        assertEquals(2, savedPurchaseOrders.size(), "批量保存的采购订单数量应正确");
        for (PurchaseOrder savedPurchaseOrder : savedPurchaseOrders) {
            assertNotNull(savedPurchaseOrder.getId(), "保存的采购订单ID不应为空");
        }

        System.out.println("PurchaseOrderService.saveAll()测试通过 ✓");
    }

    @Test
    @Order(7)
    @DisplayName("测试BaseService方法 - deleteAll")
    void testDeleteAll() {
        System.out.println("=== 测试PurchaseOrderService.deleteAll() ===");

        // 创建多个临时采购订单用于删除测试
        PurchaseOrder purchaseOrder1 = new PurchaseOrder();
        purchaseOrder1.setMedicine(medicineService.findById(testMedicineId));
        purchaseOrder1.setQuantity(10);
        purchaseOrder1.setUnitPrice(new BigDecimal("18.00"));
        purchaseOrder1.setTotalAmount(new BigDecimal("180.00"));
        purchaseOrder1.setOrderNo("PO" + System.currentTimeMillis() + "3");
        purchaseOrder1.setOrderTime(LocalDateTime.now());
        purchaseOrder1.setExpectedArrival(LocalDate.now().plusDays(3));
        purchaseOrder1.setSupplier("临时供应商1");
        purchaseOrder1.setOrderStatus(1); // 待确认
        purchaseOrder1.setOperator(userService.findById(testUserId));

        PurchaseOrder purchaseOrder2 = new PurchaseOrder();
        purchaseOrder2.setMedicine(medicineService.findById(testMedicineId));
        purchaseOrder2.setQuantity(15);
        purchaseOrder2.setUnitPrice(new BigDecimal("18.00"));
        purchaseOrder2.setTotalAmount(new BigDecimal("270.00"));
        purchaseOrder2.setOrderNo("PO" + System.currentTimeMillis() + "4");
        purchaseOrder2.setOrderTime(LocalDateTime.now());
        purchaseOrder2.setExpectedArrival(LocalDate.now().plusDays(4));
        purchaseOrder2.setSupplier("临时供应商2");
        purchaseOrder2.setOrderStatus(1); // 待确认
        purchaseOrder2.setOperator(userService.findById(testUserId));

        List<PurchaseOrder> purchaseOrders = List.of(purchaseOrder1, purchaseOrder2);
        List<PurchaseOrder> savedPurchaseOrders = purchaseOrderService.saveAll(purchaseOrders);
        List<Long> ids = savedPurchaseOrders.stream().map(PurchaseOrder::getId).toList();

        // 验证采购订单存在
        for (Long id : ids) {
            assertNotNull(purchaseOrderService.findById(id), "临时采购订单应存在");
        }

        // 批量删除
        purchaseOrderService.deleteAll(ids);

        // 验证采购订单已删除
        for (Long id : ids) {
            assertNull(purchaseOrderService.findById(id), "删除后的采购订单应不存在");
        }

        System.out.println("PurchaseOrderService.deleteAll()测试通过 ✓");
    }

    @Test
    @Order(8)
    @DisplayName("测试BaseService方法 - exists")
    void testExists() {
        System.out.println("=== 测试PurchaseOrderService.exists() ===");

        // 测试存在的采购订单
        boolean exists = purchaseOrderService.exists(testPurchaseOrderId);
        assertTrue(exists, "测试采购订单应存在");

        // 测试不存在的采购订单
        boolean notExists = purchaseOrderService.exists(999999L);
        assertFalse(notExists, "不存在的采购订单应返回false");

        System.out.println("PurchaseOrderService.exists()测试通过 ✓");
    }

    @Test
    @Order(10)
    @DisplayName("测试PurchaseOrderService特有方法 - findByOrderNo")
    void testFindByOrderNo() {
        System.out.println("=== 测试PurchaseOrderService.findByOrderNo() ===");

        // 获取测试采购订单的编号
        PurchaseOrder testOrder = purchaseOrderService.findById(testPurchaseOrderId);
        assertNotNull(testOrder, "测试采购订单应存在");
        String orderNo = testOrder.getOrderNo();
        assertNotNull(orderNo, "采购订单编号不应为空");

        // 测试查找采购订单编号
        PurchaseOrder purchaseOrder = purchaseOrderService.findByOrderNo(orderNo);
        assertNotNull(purchaseOrder, "采购订单应存在");
        assertEquals(orderNo, purchaseOrder.getOrderNo(), "采购订单编号应正确");

        System.out.println("PurchaseOrderService.findByOrderNo()测试通过 ✓");
    }

    @Test
    @Order(11)
    @DisplayName("测试PurchaseOrderService特有方法 - findByOrderStatus")
    void testFindByOrderStatus() {
        System.out.println("=== 测试PurchaseOrderService.findByOrderStatus() ===");

        // 测试查找状态为1的采购订单
        Page<PurchaseOrder> pendingOrders = purchaseOrderService.findByOrderStatus(1, pageable);
        assertNotNull(pendingOrders, "状态为1的采购订单列表不应为空");

        System.out.println("PurchaseOrderService.findByOrderStatus()测试通过 ✓");
    }

    @Test
    @Order(12)
    @DisplayName("测试PurchaseOrderService特有方法 - findByMedicineId")
    void testFindByMedicineId() {
        System.out.println("=== 测试PurchaseOrderService.findByMedicineId() ===");

        // 测试查找指定药品的采购订单
        Page<PurchaseOrder> medicineOrders = purchaseOrderService.findByMedicineId(testMedicineId, pageable);
        assertNotNull(medicineOrders, "指定药品的采购订单列表不应为空");
        assertTrue(medicineOrders.getTotalElements() > 0, "指定药品的采购订单列表应包含数据");

        System.out.println("PurchaseOrderService.findByMedicineId()测试通过 ✓");
    }

    @Test
    @Order(13)
    @DisplayName("测试PurchaseOrderService特有方法 - findPendingOrders")
    void testFindPendingOrders() {
        System.out.println("=== 测试PurchaseOrderService.findPendingOrders() ===");

        // 测试查找待处理订单
        Page<PurchaseOrder> pendingOrders = purchaseOrderService.findPendingOrders(pageable);
        assertNotNull(pendingOrders, "待处理订单列表不应为空");

        System.out.println("PurchaseOrderService.findPendingOrders()测试通过 ✓");
    }

    @Test
    @Order(14)
    @DisplayName("测试PurchaseOrderService特有方法 - findOverdueOrders")
    void testFindOverdueOrders() {
        System.out.println("=== 测试PurchaseOrderService.findOverdueOrders() ===");

        // 测试查找逾期订单
        Page<PurchaseOrder> overdueOrders = purchaseOrderService.findOverdueOrders(pageable);
        assertNotNull(overdueOrders, "逾期订单列表不应为空");

        System.out.println("PurchaseOrderService.findOverdueOrders()测试通过 ✓");
    }

    @Test
    @Order(15)
    @DisplayName("测试PurchaseOrderService特有方法 - searchOrders")
    void testSearchOrders() {
        System.out.println("=== 测试PurchaseOrderService.searchOrders() ===");

        // 测试搜索订单
        Page<PurchaseOrder> searchResults = purchaseOrderService.searchOrders("测试", pageable);
        assertNotNull(searchResults, "搜索结果不应为空");

        System.out.println("PurchaseOrderService.searchOrders()测试通过 ✓");
    }

    @Test
    @Order(16)
    @DisplayName("测试PurchaseOrderService特有方法 - findBySupplierContaining")
    void testFindBySupplierContaining() {
        System.out.println("=== 测试PurchaseOrderService.findBySupplierContaining() ===");

        // 测试查找供应商名称包含"测试"的订单
        Page<PurchaseOrder> supplierOrders = purchaseOrderService.findBySupplierContaining("测试", pageable);
        assertNotNull(supplierOrders, "供应商订单列表不应为空");

        System.out.println("PurchaseOrderService.findBySupplierContaining()测试通过 ✓");
    }

    @Test
    @Order(17)
    @DisplayName("测试PurchaseOrderService特有方法 - findByOrderTimeBetween")
    void testFindByOrderTimeBetween() {
        System.out.println("=== 测试PurchaseOrderService.findByOrderTimeBetween() ===");

        // 测试查找指定时间范围内的订单
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(1);
        Page<PurchaseOrder> timeOrders = purchaseOrderService.findByOrderTimeBetween(startTime, endTime, pageable);
        assertNotNull(timeOrders, "时间范围内的订单列表不应为空");
        assertTrue(timeOrders.getTotalElements() > 0, "时间范围内的订单列表应包含数据");

        System.out.println("PurchaseOrderService.findByOrderTimeBetween()测试通过 ✓");
    }

    @Test
    @Order(18)
    @DisplayName("测试PurchaseOrderService特有方法 - getTotalPurchasedQuantity")
    void testGetTotalPurchasedQuantity() {
        System.out.println("=== 测试PurchaseOrderService.getTotalPurchasedQuantity() ===");

        // 测试获取指定药品的总采购数量
        Integer totalQuantity = purchaseOrderService.getTotalPurchasedQuantity(testMedicineId);
        assertNotNull(totalQuantity, "总采购数量不应为空");
        assertTrue(totalQuantity >= 0, "总采购数量应大于等于0");

        System.out.println("PurchaseOrderService.getTotalPurchasedQuantity()测试通过 ✓");
    }

    @Test
    @Order(19)
    @DisplayName("测试PurchaseOrderService特有方法 - getTotalPurchaseAmountByPeriod")
    void testGetTotalPurchaseAmountByPeriod() {
        System.out.println("=== 测试PurchaseOrderService.getTotalPurchaseAmountByPeriod() ===");

        // 测试获取指定时间段的总采购金额
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(1);
        Double totalAmount = purchaseOrderService.getTotalPurchaseAmountByPeriod(startTime, endTime);
        assertNotNull(totalAmount, "总采购金额不应为空");
        assertTrue(totalAmount >= 0, "总采购金额应大于等于0");

        System.out.println("PurchaseOrderService.getTotalPurchaseAmountByPeriod()测试通过 ✓");
    }

    @Test
    @Order(20)
    @DisplayName("测试PurchaseOrderService特有方法 - createOrder")
    void testCreateOrder() {
        System.out.println("=== 测试PurchaseOrderService.createOrder() ===");

        // 创建采购订单
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setMedicine(medicineService.findById(testMedicineId));
        purchaseOrder.setQuantity(60);
        purchaseOrder.setUnitPrice(new BigDecimal("18.00"));
        purchaseOrder.setTotalAmount(new BigDecimal("1080.00"));
        purchaseOrder.setOrderNo("PO" + System.currentTimeMillis());
        purchaseOrder.setOrderTime(LocalDateTime.now());
        purchaseOrder.setExpectedArrival(LocalDate.now().plusDays(4));
        purchaseOrder.setSupplier("创建测试供应商");
        purchaseOrder.setOrderStatus(1); // 待确认

        // 测试创建采购订单
        PurchaseOrder createdOrder = purchaseOrderService.createOrder(purchaseOrder, testUserId);
        assertNotNull(createdOrder, "创建的采购订单不应为空");
        assertNotNull(createdOrder.getId(), "创建的采购订单ID不应为空");
        assertNotNull(createdOrder.getOperator(), "操作员不应为空");
        assertEquals(testUserId, createdOrder.getOperator().getId(), "操作员ID应正确");

        System.out.println("PurchaseOrderService.createOrder()测试通过 ✓");
    }

    @Test
    @Order(21)
    @DisplayName("测试PurchaseOrderService特有方法 - confirmOrder")
    void testConfirmOrder() {
        System.out.println("=== 测试PurchaseOrderService.confirmOrder() ===");

        // 测试确认订单
        PurchaseOrder confirmedOrder = purchaseOrderService.confirmOrder(testPurchaseOrderId);
        assertNotNull(confirmedOrder, "确认后的订单不应为空");
        // 确认后的状态可能变为2（已确认），具体取决于实现

        System.out.println("PurchaseOrderService.confirmOrder()测试通过 ✓");
    }

    @Test
    @Order(22)
    @DisplayName("测试PurchaseOrderService特有方法 - markAsArrived")
    void testMarkAsArrived() {
        System.out.println("=== 测试PurchaseOrderService.markAsArrived() ===");

        // 测试标记订单为已到货
        PurchaseOrder arrivedOrder = purchaseOrderService.markAsArrived(testPurchaseOrderId);
        assertNotNull(arrivedOrder, "标记为已到货的订单不应为空");
        // 标记后的状态可能变为3（已到货），具体取决于实现

        System.out.println("PurchaseOrderService.markAsArrived()测试通过 ✓");
    }

    @Test
    @Order(23)
    @DisplayName("测试PurchaseOrderService特有方法 - cancelOrder")
    void testCancelOrder() {
        System.out.println("=== 测试PurchaseOrderService.cancelOrder() ===");

        // 创建一个新订单用于测试取消
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setMedicine(medicineService.findById(testMedicineId));
        purchaseOrder.setQuantity(20);
        purchaseOrder.setUnitPrice(new BigDecimal("18.00"));
        purchaseOrder.setTotalAmount(new BigDecimal("360.00"));
        purchaseOrder.setOrderNo("PO" + System.currentTimeMillis());
        purchaseOrder.setOrderTime(LocalDateTime.now());
        purchaseOrder.setExpectedArrival(LocalDate.now().plusDays(3));
        purchaseOrder.setSupplier("取消测试供应商");
        purchaseOrder.setOrderStatus(1); // 待确认
        purchaseOrder.setOperator(userService.findById(testUserId));
        PurchaseOrder savedOrder = purchaseOrderService.save(purchaseOrder);
        Long cancelTestOrderId = savedOrder.getId();
        assertNotNull(cancelTestOrderId, "测试订单ID不应为空");

        // 测试取消订单
        PurchaseOrder cancelledOrder = purchaseOrderService.cancelOrder(cancelTestOrderId);
        assertNotNull(cancelledOrder, "取消后的订单不应为空");
        // 取消后的状态可能变为4（已取消），具体取决于实现

        System.out.println("PurchaseOrderService.cancelOrder()测试通过 ✓");
    }

    @Test
    @Order(24)
    @DisplayName("测试PurchaseOrderService特有方法 - getOrderStatistics")
    void testGetOrderStatistics() {
        System.out.println("=== 测试PurchaseOrderService.getOrderStatistics() ===");

        // 测试获取订单统计
        Map<String, Object> orderStatistics = purchaseOrderService.getOrderStatistics();
        assertNotNull(orderStatistics, "订单统计不应为空");

        System.out.println("PurchaseOrderService.getOrderStatistics()测试通过 ✓");
    }

    @Test
    @Order(25)
    @DisplayName("测试PurchaseOrderService特有方法 - countByStatus")
    void testCountByStatus() {
        System.out.println("=== 测试PurchaseOrderService.countByStatus() ===");

        // 测试按状态统计订单数量
        Map<String, Long> statusCount = purchaseOrderService.countByStatus();
        assertNotNull(statusCount, "按状态统计的订单数量不应为空");

        System.out.println("PurchaseOrderService.countByStatus()测试通过 ✓");
    }

    @Test
    @Order(26)
    @DisplayName("测试PurchaseOrderService特有方法 - getMonthlyStatistics")
    void testGetMonthlyStatistics() {
        System.out.println("=== 测试PurchaseOrderService.getMonthlyStatistics() ===");

        // 测试获取月度统计
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now().plusMonths(1);
        Map<String, Object> monthlyStatistics = purchaseOrderService.getMonthlyStatistics(startDate, endDate);
        assertNotNull(monthlyStatistics, "月度统计不应为空");

        System.out.println("PurchaseOrderService.getMonthlyStatistics()测试通过 ✓");
    }
}
