package com.example.demo;

import com.example.demo.entity.Medicine;
import org.junit.jupiter.api.*;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MedicineService测试")
class MedicineServiceTest extends BaseServiceTest {

    @Test
    @Order(1)
    @DisplayName("测试BaseService方法 - save")
    void testSave() {
        System.out.println("=== 测试MedicineService.save() ===");

        // 创建新药品
        Medicine medicine = new Medicine();
        medicine.setMedicineCode("MED002");
        medicine.setName("新测试药品");
        medicine.setGenericName("新测试通用名");
        medicine.setCategory(categoryService.findById(testCategoryId));
        medicine.setSpecification("20mg/片");
        medicine.setUnit("盒");
        medicine.setManufacturer("新测试药厂");
        medicine.setApprovalNumber("国药准字Z654321");
        medicine.setDescription("新测试药品描述");
        medicine.setRetailPrice(new BigDecimal("35.50"));
        medicine.setPurchasePrice(new BigDecimal("25.00"));
        medicine.setStatus(1);
        medicine.setSeasonal(false);
        medicine.setPrescription(false);
        medicine.setStorageRequirement(1);

        // 保存药品
        Medicine savedMedicine = medicineService.save(medicine);
        assertNotNull(savedMedicine, "保存的药品不应为空");
        assertNotNull(savedMedicine.getId(), "保存的药品ID不应为空");
        assertEquals("新测试药品", savedMedicine.getName(), "药品名称应正确");

        System.out.println("MedicineService.save()测试通过 ✓");
    }

    @Test
    @Order(2)
    @DisplayName("测试BaseService方法 - update")
    void testUpdate() {
        System.out.println("=== 测试MedicineService.update() ===");

        // 获取测试药品
        Medicine medicine = medicineService.findById(testMedicineId);
        assertNotNull(medicine, "药品应存在");

        // 更新药品
        medicine.setName("更新后的测试药品");
        medicine.setDescription("更新后的测试药品描述");

        // 保存更新
        Medicine updatedMedicine = medicineService.update(medicine);
        assertNotNull(updatedMedicine, "更新后的药品不应为空");
        assertEquals("更新后的测试药品", updatedMedicine.getName(), "药品名称应已更新");
        assertEquals("更新后的测试药品描述", updatedMedicine.getDescription(), "药品描述应已更新");

        System.out.println("MedicineService.update()测试通过 ✓");
    }

    @Test
    @Order(3)
    @DisplayName("测试BaseService方法 - delete")
    void testDelete() {
        System.out.println("=== 测试MedicineService.delete() ===");

        // 创建一个临时药品用于删除测试
        Medicine medicine = new Medicine();
        medicine.setMedicineCode("MED999");
        medicine.setName("临时药品");
        medicine.setGenericName("临时通用名");
        medicine.setCategory(categoryService.findById(testCategoryId));
        medicine.setSpecification("10mg/片");
        medicine.setUnit("盒");
        medicine.setManufacturer("临时药厂");
        medicine.setApprovalNumber("国药准字Z999999");
        medicine.setDescription("临时药品描述");
        medicine.setRetailPrice(new BigDecimal("10.00"));
        medicine.setPurchasePrice(new BigDecimal("5.00"));
        medicine.setStatus(1);
        medicine.setSeasonal(false);
        medicine.setPrescription(false);
        medicine.setStorageRequirement(1);
        Medicine savedMedicine = medicineService.save(medicine);
        Long tempMedicineId = savedMedicine.getId();
        assertNotNull(tempMedicineId, "临时药品ID不应为空");

        // 验证药品存在
        Medicine foundMedicine = medicineService.findById(tempMedicineId);
        assertNotNull(foundMedicine, "临时药品应存在");

        // 删除药品
        medicineService.delete(tempMedicineId);

        // 验证药品已删除
        Medicine deletedMedicine = medicineService.findById(tempMedicineId);
        assertNull(deletedMedicine, "删除后的药品应不存在");

        System.out.println("MedicineService.delete()测试通过 ✓");
    }

    @Test
    @Order(4)
    @DisplayName("测试BaseService方法 - findById")
    void testFindById() {
        System.out.println("=== 测试MedicineService.findById() ===");

        // 查找测试药品
        Medicine medicine = medicineService.findById(testMedicineId);
        assertNotNull(medicine, "药品应存在");
        assertEquals("测试药品", medicine.getName(), "药品名称应正确");

        System.out.println("MedicineService.findById()测试通过 ✓");
    }

    @Test
    @Order(5)
    @DisplayName("测试BaseService方法 - findAll")
    void testFindAll() {
        System.out.println("=== 测试MedicineService.findAll() ===");

        // 测试无参findAll
        List<Medicine> medicines = medicineService.findAll();
        assertNotNull(medicines, "药品列表不应为空");
        assertTrue(medicines.size() > 0, "药品列表应包含数据");

        // 测试带分页的findAll
        Page<Medicine> medicinePage = medicineService.findAll(pageable);
        assertNotNull(medicinePage, "分页药品列表不应为空");
        assertTrue(medicinePage.getTotalElements() > 0, "分页药品列表应包含数据");

        System.out.println("MedicineService.findAll()测试通过 ✓");
    }

    @Test
    @Order(6)
    @DisplayName("测试BaseService方法 - saveAll")
    void testSaveAll() {
        System.out.println("=== 测试MedicineService.saveAll() ===");

        // 创建多个药品
        Medicine medicine1 = new Medicine();
        medicine1.setMedicineCode("MED003");
        medicine1.setName("批量药品1");
        medicine1.setGenericName("批量通用名1");
        medicine1.setCategory(categoryService.findById(testCategoryId));
        medicine1.setSpecification("10mg/片");
        medicine1.setUnit("盒");
        medicine1.setManufacturer("批量药厂1");
        medicine1.setApprovalNumber("国药准字Z111111");
        medicine1.setDescription("批量药品1描述");
        medicine1.setRetailPrice(new BigDecimal("20.00"));
        medicine1.setPurchasePrice(new BigDecimal("15.00"));
        medicine1.setStatus(1);
        medicine1.setSeasonal(false);
        medicine1.setPrescription(false);
        medicine1.setStorageRequirement(1);

        Medicine medicine2 = new Medicine();
        medicine2.setMedicineCode("MED004");
        medicine2.setName("批量药品2");
        medicine2.setGenericName("批量通用名2");
        medicine2.setCategory(categoryService.findById(testCategoryId));
        medicine2.setSpecification("15mg/片");
        medicine2.setUnit("盒");
        medicine2.setManufacturer("批量药厂2");
        medicine2.setApprovalNumber("国药准字Z222222");
        medicine2.setDescription("批量药品2描述");
        medicine2.setRetailPrice(new BigDecimal("25.00"));
        medicine2.setPurchasePrice(new BigDecimal("18.00"));
        medicine2.setStatus(1);
        medicine2.setSeasonal(false);
        medicine2.setPrescription(false);
        medicine2.setStorageRequirement(1);

        List<Medicine> medicines = List.of(medicine1, medicine2);

        // 批量保存
        List<Medicine> savedMedicines = medicineService.saveAll(medicines);
        assertNotNull(savedMedicines, "批量保存的药品列表不应为空");
        assertEquals(2, savedMedicines.size(), "批量保存的药品数量应正确");
        for (Medicine savedMedicine : savedMedicines) {
            assertNotNull(savedMedicine.getId(), "保存的药品ID不应为空");
        }

        System.out.println("MedicineService.saveAll()测试通过 ✓");
    }

    @Test
    @Order(7)
    @DisplayName("测试BaseService方法 - deleteAll")
    void testDeleteAll() {
        System.out.println("=== 测试MedicineService.deleteAll() ===");

        // 创建多个临时药品用于删除测试
        Medicine medicine1 = new Medicine();
        medicine1.setMedicineCode("MED997");
        medicine1.setName("临时药品1");
        medicine1.setGenericName("临时通用名1");
        medicine1.setCategory(categoryService.findById(testCategoryId));
        medicine1.setSpecification("10mg/片");
        medicine1.setUnit("盒");
        medicine1.setManufacturer("临时药厂1");
        medicine1.setApprovalNumber("国药准字Z999997");
        medicine1.setDescription("临时药品1描述");
        medicine1.setRetailPrice(new BigDecimal("10.00"));
        medicine1.setPurchasePrice(new BigDecimal("5.00"));
        medicine1.setStatus(1);
        medicine1.setSeasonal(false);
        medicine1.setPrescription(false);
        medicine1.setStorageRequirement(1);

        Medicine medicine2 = new Medicine();
        medicine2.setMedicineCode("MED998");
        medicine2.setName("临时药品2");
        medicine2.setGenericName("临时通用名2");
        medicine2.setCategory(categoryService.findById(testCategoryId));
        medicine2.setSpecification("10mg/片");
        medicine2.setUnit("盒");
        medicine2.setManufacturer("临时药厂2");
        medicine2.setApprovalNumber("国药准字Z999998");
        medicine2.setDescription("临时药品2描述");
        medicine2.setRetailPrice(new BigDecimal("10.00"));
        medicine2.setPurchasePrice(new BigDecimal("5.00"));
        medicine2.setStatus(1);
        medicine2.setSeasonal(false);
        medicine2.setPrescription(false);
        medicine2.setStorageRequirement(1);

        List<Medicine> medicines = List.of(medicine1, medicine2);
        List<Medicine> savedMedicines = medicineService.saveAll(medicines);
        List<Long> ids = savedMedicines.stream().map(Medicine::getId).toList();

        // 验证药品存在
        for (Long id : ids) {
            assertNotNull(medicineService.findById(id), "临时药品应存在");
        }

        // 批量删除
        medicineService.deleteAll(ids);

        // 验证药品已删除
        for (Long id : ids) {
            assertNull(medicineService.findById(id), "删除后的药品应不存在");
        }

        System.out.println("MedicineService.deleteAll()测试通过 ✓");
    }

    @Test
    @Order(8)
    @DisplayName("测试BaseService方法 - exists")
    void testExists() {
        System.out.println("=== 测试MedicineService.exists() ===");

        // 测试存在的药品
        boolean exists = medicineService.exists(testMedicineId);
        assertTrue(exists, "测试药品应存在");

        // 测试不存在的药品
        boolean notExists = medicineService.exists(999999L);
        assertFalse(notExists, "不存在的药品应返回false");

        System.out.println("MedicineService.exists()测试通过 ✓");
    }

    @Test
    @Order(10)
    @DisplayName("测试MedicineService特有方法 - findByMedicineCode")
    void testFindByMedicineCode() {
        System.out.println("=== 测试MedicineService.findByMedicineCode() ===");

        // 测试查找药品编码
        Medicine medicine = medicineService.findByMedicineCode("MED001");
        assertNotNull(medicine, "药品应存在");
        assertEquals("测试药品", medicine.getName(), "药品名称应正确");

        System.out.println("MedicineService.findByMedicineCode()测试通过 ✓");
    }

    @Test
    @Order(11)
    @DisplayName("测试MedicineService特有方法 - findByStatus")
    void testFindByStatus() {
        System.out.println("=== 测试MedicineService.findByStatus() ===");

        // 测试查找状态为1的药品
        Page<Medicine> activeMedicines = medicineService.findByStatus(1, pageable);
        assertNotNull(activeMedicines, "状态为1的药品列表不应为空");
        assertTrue(activeMedicines.getTotalElements() > 0, "状态为1的药品列表应包含数据");

        // 测试查找状态为0的药品
        Page<Medicine> inactiveMedicines = medicineService.findByStatus(0, pageable);
        assertNotNull(inactiveMedicines, "状态为0的药品列表不应为空");

        System.out.println("MedicineService.findByStatus()测试通过 ✓");
    }

    @Test
    @Order(12)
    @DisplayName("测试MedicineService特有方法 - searchMedicines")
    void testSearchMedicines() {
        System.out.println("=== 测试MedicineService.searchMedicines() ===");

        // 测试搜索药品
        List<Medicine> searchResults = medicineService.searchMedicines("测试");
        assertNotNull(searchResults, "搜索结果不应为空");
        assertTrue(searchResults.size() > 0, "搜索结果应包含数据");

        System.out.println("MedicineService.searchMedicines()测试通过 ✓");
    }

    @Test
    @Order(13)
    @DisplayName("测试MedicineService特有方法 - findByCategoryId")
    void testFindByCategoryId() {
        System.out.println("=== 测试MedicineService.findByCategoryId() ===");

        // 测试查找指定分类的药品
        List<Medicine> categoryMedicines = medicineService.findByCategoryId(testCategoryId);
        assertNotNull(categoryMedicines, "分类药品列表不应为空");
        assertTrue(categoryMedicines.size() > 0, "分类药品列表应包含数据");

        System.out.println("MedicineService.findByCategoryId()测试通过 ✓");
    }

    @Test
    @Order(14)
    @DisplayName("测试MedicineService特有方法 - updatePrice")
    void testUpdatePrice() {
        System.out.println("=== 测试MedicineService.updatePrice() ===");

        // 测试更新药品价格
        BigDecimal newRetailPrice = new BigDecimal("30.00");
        BigDecimal newPurchasePrice = new BigDecimal("22.00");
        Medicine updatedMedicine = medicineService.updatePrice(testMedicineId, newRetailPrice, newPurchasePrice);
        assertNotNull(updatedMedicine, "更新后的药品不应为空");
        assertEquals(newRetailPrice, updatedMedicine.getRetailPrice(), "零售价格应已更新");
        assertEquals(newPurchasePrice, updatedMedicine.getPurchasePrice(), "采购价格应已更新");

        System.out.println("MedicineService.updatePrice()测试通过 ✓");
    }

    @Test
    @Order(15)
    @DisplayName("测试MedicineService特有方法 - countByStatus")
    void testCountByStatus() {
        System.out.println("=== 测试MedicineService.countByStatus() ===");

        // 测试统计状态为1的药品数量
        long activeCount = medicineService.countByStatus(1);
        assertTrue(activeCount > 0, "状态为1的药品数量应大于0");

        // 测试统计状态为0的药品数量
        long inactiveCount = medicineService.countByStatus(0);
        // 这里可能为0，因为我们的测试数据都是状态为1的

        System.out.println("MedicineService.countByStatus()测试通过 ✓");
    }
}
