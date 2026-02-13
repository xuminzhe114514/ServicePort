package com.example.demo;

import com.example.demo.entity.Symptom;
import org.junit.jupiter.api.*;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SymptomService测试")
class SymptomServiceTest extends BaseServiceTest {

    @Test
    @Order(1)
    @DisplayName("测试BaseService方法 - save")
    void testSave() {
        System.out.println("=== 测试SymptomService.save() ===");

        // 创建新症状
        Symptom symptom = new Symptom();
        symptom.setName("头痛");
        symptom.setDescription("头部疼痛症状");

        // 保存症状
        Symptom savedSymptom = symptomService.save(symptom);
        assertNotNull(savedSymptom, "保存的症状不应为空");
        assertNotNull(savedSymptom.getId(), "保存的症状ID不应为空");
        assertEquals("头痛", savedSymptom.getName(), "症状名称应正确");

        System.out.println("SymptomService.save()测试通过 ✓");
    }

    @Test
    @Order(2)
    @DisplayName("测试BaseService方法 - update")
    void testUpdate() {
        System.out.println("=== 测试SymptomService.update() ===");

        // 获取测试症状
        Symptom symptom = symptomService.findById(testSymptomId);
        assertNotNull(symptom, "症状应存在");

        // 更新症状
        symptom.setName("发热");
        symptom.setDescription("体温升高症状，可能伴随其他不适");

        // 保存更新
        Symptom updatedSymptom = symptomService.update(symptom);
        assertNotNull(updatedSymptom, "更新后的症状不应为空");
        assertEquals("发热", updatedSymptom.getName(), "症状名称应已更新");
        assertEquals("体温升高症状，可能伴随其他不适", updatedSymptom.getDescription(), "症状描述应已更新");

        System.out.println("SymptomService.update()测试通过 ✓");
    }

    @Test
    @Order(3)
    @DisplayName("测试BaseService方法 - delete")
    void testDelete() {
        System.out.println("=== 测试SymptomService.delete() ===");

        // 创建一个临时症状用于删除测试
        Symptom symptom = new Symptom();
        symptom.setName("临时症状");
        symptom.setDescription("临时症状描述");
        Symptom savedSymptom = symptomService.save(symptom);
        Integer tempSymptomId = savedSymptom.getId();
        assertNotNull(tempSymptomId, "临时症状ID不应为空");

        // 验证症状存在
        Symptom foundSymptom = symptomService.findById(tempSymptomId);
        assertNotNull(foundSymptom, "临时症状应存在");

        // 删除症状
        symptomService.delete(tempSymptomId);

        // 验证症状已删除
        Symptom deletedSymptom = symptomService.findById(tempSymptomId);
        assertNull(deletedSymptom, "删除后的症状应不存在");

        System.out.println("SymptomService.delete()测试通过 ✓");
    }

    @Test
    @Order(4)
    @DisplayName("测试BaseService方法 - findById")
    void testFindById() {
        System.out.println("=== 测试SymptomService.findById() ===");

        // 查找测试症状
        Symptom symptom = symptomService.findById(testSymptomId);
        assertNotNull(symptom, "症状应存在");
        assertNotNull(symptom.getName(), "症状名称不应为空");

        System.out.println("SymptomService.findById()测试通过 ✓");
    }

    @Test
    @Order(5)
    @DisplayName("测试BaseService方法 - findAll")
    void testFindAll() {
        System.out.println("=== 测试SymptomService.findAll() ===");

        // 测试无参findAll
        List<Symptom> symptoms = symptomService.findAll();
        assertNotNull(symptoms, "症状列表不应为空");
        assertTrue(symptoms.size() > 0, "症状列表应包含数据");

        // 测试带分页的findAll
        Page<Symptom> symptomPage = symptomService.findAll(pageable);
        assertNotNull(symptomPage, "分页症状列表不应为空");
        assertTrue(symptomPage.getTotalElements() > 0, "分页症状列表应包含数据");

        System.out.println("SymptomService.findAll()测试通过 ✓");
    }

    @Test
    @Order(6)
    @DisplayName("测试BaseService方法 - saveAll")
    void testSaveAll() {
        System.out.println("=== 测试SymptomService.saveAll() ===");

        // 创建多个症状
        Symptom symptom1 = new Symptom();
        symptom1.setName("咳嗽");
        symptom1.setDescription("咳嗽症状");

        Symptom symptom2 = new Symptom();
        symptom2.setName("流鼻涕");
        symptom2.setDescription("流鼻涕症状");

        List<Symptom> symptoms = List.of(symptom1, symptom2);

        // 批量保存
        List<Symptom> savedSymptoms = symptomService.saveAll(symptoms);
        assertNotNull(savedSymptoms, "批量保存的症状列表不应为空");
        assertEquals(2, savedSymptoms.size(), "批量保存的症状数量应正确");
        for (Symptom savedSymptom : savedSymptoms) {
            assertNotNull(savedSymptom.getId(), "保存的症状ID不应为空");
        }

        System.out.println("SymptomService.saveAll()测试通过 ✓");
    }

    @Test
    @Order(7)
    @DisplayName("测试BaseService方法 - deleteAll")
    void testDeleteAll() {
        System.out.println("=== 测试SymptomService.deleteAll() ===");

        // 创建多个临时症状用于删除测试
        Symptom symptom1 = new Symptom();
        symptom1.setName("临时症状1");
        symptom1.setDescription("临时症状1描述");

        Symptom symptom2 = new Symptom();
        symptom2.setName("临时症状2");
        symptom2.setDescription("临时症状2描述");

        List<Symptom> symptoms = List.of(symptom1, symptom2);
        List<Symptom> savedSymptoms = symptomService.saveAll(symptoms);
        List<Integer> ids = savedSymptoms.stream().map(Symptom::getId).toList();

        // 验证症状存在
        for (Integer id : ids) {
            assertNotNull(symptomService.findById(id), "临时症状应存在");
        }

        // 批量删除
        symptomService.deleteAll(ids);

        // 验证症状已删除
        for (Integer id : ids) {
            assertNull(symptomService.findById(id), "删除后的症状应不存在");
        }

        System.out.println("SymptomService.deleteAll()测试通过 ✓");
    }

    @Test
    @Order(8)
    @DisplayName("测试BaseService方法 - exists")
    void testExists() {
        System.out.println("=== 测试SymptomService.exists() ===");

        // 测试存在的症状
        boolean exists = symptomService.exists(testSymptomId);
        assertTrue(exists, "测试症状应存在");

        // 测试不存在的症状
        boolean notExists = symptomService.exists(999999);
        assertFalse(notExists, "不存在的症状应返回false");

        System.out.println("SymptomService.exists()测试通过 ✓");
    }

    @Test
    @Order(10)
    @DisplayName("测试SymptomService特有方法 - findByName")
    void testFindByName() {
        System.out.println("=== 测试SymptomService.findByName() ===");

        // 测试查找症状名称
        Symptom symptom = symptomService.findByName("发烧");
        assertNotNull(symptom, "症状应存在");
        assertEquals("发烧", symptom.getName(), "症状名称应正确");

        System.out.println("SymptomService.findByName()测试通过 ✓");
    }

    @Test
    @Order(11)
    @DisplayName("测试SymptomService特有方法 - findByNameContaining")
    void testFindByNameContaining() {
        System.out.println("=== 测试SymptomService.findByNameContaining() ===");

        // 测试查找名称包含"发"的症状
        List<Symptom> symptoms = symptomService.findByNameContaining("发");
        assertNotNull(symptoms, "症状列表不应为空");
        // 验证至少有一个症状名称包含"发"
        boolean hasMatchingSymptom = symptoms.stream().anyMatch(s -> s.getName().contains("发"));
        assertTrue(hasMatchingSymptom, "应找到名称包含'发'的症状");

        System.out.println("SymptomService.findByNameContaining()测试通过 ✓");
    }

    @Test
    @Order(12)
    @DisplayName("测试SymptomService特有方法 - existsByName")
    void testExistsByName() {
        System.out.println("=== 测试SymptomService.existsByName() ===");

        // 测试存在的症状名称
        boolean exists = symptomService.existsByName("发烧");
        assertTrue(exists, "症状名称'发烧'应存在");

        // 测试不存在的症状名称
        boolean notExists = symptomService.existsByName("不存在的症状");
        assertFalse(notExists, "不存在的症状名称应返回false");

        System.out.println("SymptomService.existsByName()测试通过 ✓");
    }

    @Test
    @Order(13)
    @DisplayName("测试SymptomService特有方法 - findByDescriptionContaining")
    void testFindByDescriptionContaining() {
        System.out.println("=== 测试SymptomService.findByDescriptionContaining() ===");

        // 测试查找描述包含"体温"的症状
        List<Symptom> symptoms = symptomService.findByDescriptionContaining("体温");
        assertNotNull(symptoms, "症状列表不应为空");
        // 验证至少有一个症状描述包含"体温"
        boolean hasMatchingSymptom = symptoms.stream().anyMatch(s -> s.getDescription().contains("体温"));
        assertTrue(hasMatchingSymptom, "应找到描述包含'体温'的症状");

        System.out.println("SymptomService.findByDescriptionContaining()测试通过 ✓");
    }

    @Test
    @Order(14)
    @DisplayName("测试SymptomService特有方法 - searchSymptoms")
    void testSearchSymptoms() {
        System.out.println("=== 测试SymptomService.searchSymptoms() ===");

        // 测试搜索症状
        List<Symptom> searchResults = symptomService.searchSymptoms("发烧");
        assertNotNull(searchResults, "搜索结果不应为空");
        // 验证至少有一个症状与搜索词匹配
        assertTrue(searchResults.size() > 0, "搜索结果应包含数据");

        System.out.println("SymptomService.searchSymptoms()测试通过 ✓");
    }

    @Test
    @Order(15)
    @DisplayName("测试SymptomService特有方法 - searchSymptoms(带分页)")
    void testSearchSymptomsWithPageable() {
        System.out.println("=== 测试SymptomService.searchSymptoms(带分页) ===");

        // 测试带分页的搜索症状
        Page<Symptom> searchResults = symptomService.searchSymptoms("发烧", pageable);
        assertNotNull(searchResults, "搜索结果不应为空");

        System.out.println("SymptomService.searchSymptoms(带分页)测试通过 ✓");
    }

    @Test
    @Order(16)
    @DisplayName("测试SymptomService特有方法 - countAll")
    void testCountAll() {
        System.out.println("=== 测试SymptomService.countAll() ===");

        // 测试统计所有症状数量
        long count = symptomService.countAll();
        assertTrue(count >= 0, "症状数量应大于等于0");

        System.out.println("SymptomService.countAll()测试通过 ✓");
    }
}
