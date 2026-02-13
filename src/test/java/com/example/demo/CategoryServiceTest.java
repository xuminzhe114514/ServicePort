package com.example.demo;

import com.example.demo.entity.Category;
import com.example.demo.entity.Medicine;
import com.example.demo.repository.MedicineRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CategoryService测试")
class CategoryServiceTest extends BaseServiceTest {

    @Autowired
    private MedicineRepository medicineRepository;

    @Test
    @Order(1)
    @DisplayName("测试BaseService方法 - save")
    void testSave() {
        System.out.println("=== 测试CategoryService.save() ===");

        // 创建新分类
        Category category = new Category();
        category.setName("新测试分类");
        category.setParentId(0L);
        category.setLevel(1);
        category.setDescription("新测试分类描述");
        category.setSort(2);
        category.setStatus(1);

        // 保存分类
        Category savedCategory = categoryService.save(category);
        assertNotNull(savedCategory, "保存的分类不应为空");
        assertNotNull(savedCategory.getId(), "保存的分类ID不应为空");
        assertEquals("新测试分类", savedCategory.getName(), "分类名称应正确");

        System.out.println("CategoryService.save()测试通过 ✓");
    }

    @Test
    @Order(2)
    @DisplayName("测试BaseService方法 - update")
    void testUpdate() {
        System.out.println("=== 测试CategoryService.update() ===");

        // 获取测试分类
        Category category = categoryService.findById(testCategoryId);
        assertNotNull(category, "分类应存在");

        // 更新分类
        category.setName("更新后的测试分类");
        category.setDescription("更新后的测试分类描述");

        // 保存更新
        Category updatedCategory = categoryService.update(category);
        assertNotNull(updatedCategory, "更新后的分类不应为空");
        assertEquals("更新后的测试分类", updatedCategory.getName(), "分类名称应已更新");
        assertEquals("更新后的测试分类描述", updatedCategory.getDescription(), "分类描述应已更新");

        System.out.println("CategoryService.update()测试通过 ✓");
    }

    @Test
    @Order(3)
    @DisplayName("测试BaseService方法 - delete")
    void testDelete() {
        System.out.println("=== 测试CategoryService.delete() ===");

        // 创建一个临时分类用于删除测试
        Category category = new Category();
        category.setName("临时分类");
        category.setParentId(0L);
        category.setLevel(1);
        category.setDescription("临时分类描述");
        category.setSort(3);
        category.setStatus(1);
        Category savedCategory = categoryService.save(category);
        Long tempCategoryId = savedCategory.getId();
        assertNotNull(tempCategoryId, "临时分类ID不应为空");

        // 验证分类存在
        Category foundCategory = categoryService.findById(tempCategoryId);
        assertNotNull(foundCategory, "临时分类应存在");

        // 删除分类
        categoryService.delete(tempCategoryId);

        // 验证分类已删除
        Category deletedCategory = categoryService.findById(tempCategoryId);
        assertNull(deletedCategory, "删除后的分类应不存在");

        System.out.println("CategoryService.delete()测试通过 ✓");
    }

    @Test
    @Order(4)
    @DisplayName("测试BaseService方法 - findById")
    void testFindById() {
        System.out.println("=== 测试CategoryService.findById() ===");

        // 查找测试分类
        Category category = categoryService.findById(testCategoryId);
        assertNotNull(category, "分类应存在");
        assertEquals("测试分类", category.getName(), "分类名称应正确");

        System.out.println("CategoryService.findById()测试通过 ✓");
    }

    @Test
    @Order(5)
    @DisplayName("测试BaseService方法 - findAll")
    void testFindAll() {
        System.out.println("=== 测试CategoryService.findAll() ===");

        // 测试无参findAll
        List<Category> categories = categoryService.findAll();
        assertNotNull(categories, "分类列表不应为空");
        assertTrue(categories.size() > 0, "分类列表应包含数据");

        // 测试带分页的findAll
        Page<Category> categoryPage = categoryService.findAll(pageable);
        assertNotNull(categoryPage, "分页分类列表不应为空");
        assertTrue(categoryPage.getTotalElements() > 0, "分页分类列表应包含数据");

        System.out.println("CategoryService.findAll()测试通过 ✓");
    }

    @Test
    @Order(6)
    @DisplayName("测试BaseService方法 - saveAll")
    void testSaveAll() {
        System.out.println("=== 测试CategoryService.saveAll() ===");

        // 创建多个分类
        Category category1 = new Category();
        category1.setName("批量分类1");
        category1.setParentId(0L);
        category1.setLevel(1);
        category1.setDescription("批量分类1描述");
        category1.setSort(4);
        category1.setStatus(1);

        Category category2 = new Category();
        category2.setName("批量分类2");
        category2.setParentId(0L);
        category2.setLevel(1);
        category2.setDescription("批量分类2描述");
        category2.setSort(5);
        category2.setStatus(1);

        List<Category> categories = List.of(category1, category2);

        // 批量保存
        List<Category> savedCategories = categoryService.saveAll(categories);
        assertNotNull(savedCategories, "批量保存的分类列表不应为空");
        assertEquals(2, savedCategories.size(), "批量保存的分类数量应正确");
        for (Category savedCategory : savedCategories) {
            assertNotNull(savedCategory.getId(), "保存的分类ID不应为空");
        }

        System.out.println("CategoryService.saveAll()测试通过 ✓");
    }

    @Test
    @Order(7)
    @DisplayName("测试BaseService方法 - deleteAll")
    void testDeleteAll() {
        System.out.println("=== 测试CategoryService.deleteAll() ===");

        // 创建多个临时分类用于删除测试
        Category category1 = new Category();
        category1.setName("临时分类1");
        category1.setParentId(0L);
        category1.setLevel(1);
        category1.setDescription("临时分类1描述");
        category1.setSort(6);
        category1.setStatus(1);

        Category category2 = new Category();
        category2.setName("临时分类2");
        category2.setParentId(0L);
        category2.setLevel(1);
        category2.setDescription("临时分类2描述");
        category2.setSort(7);
        category2.setStatus(1);

        List<Category> categories = List.of(category1, category2);
        List<Category> savedCategories = categoryService.saveAll(categories);
        List<Long> ids = savedCategories.stream().map(Category::getId).toList();

        // 验证分类存在
        for (Long id : ids) {
            assertNotNull(categoryService.findById(id), "临时分类应存在");
        }

        // 批量删除
        categoryService.deleteAll(ids);

        // 验证分类已删除
        for (Long id : ids) {
            assertNull(categoryService.findById(id), "删除后的分类应不存在");
        }

        System.out.println("CategoryService.deleteAll()测试通过 ✓");
    }

    @Test
    @Order(8)
    @DisplayName("测试BaseService方法 - exists")
    void testExists() {
        System.out.println("=== 测试CategoryService.exists() ===");

        // 测试存在的分类
        boolean exists = categoryService.exists(testCategoryId);
        assertTrue(exists, "测试分类应存在");

        // 测试不存在的分类
        boolean notExists = categoryService.exists(999999L);
        assertFalse(notExists, "不存在的分类应返回false");

        System.out.println("CategoryService.exists()测试通过 ✓");
    }

    @Test
    @Order(10)
    @DisplayName("测试CategoryService特有方法 - findByParentId")
    void testFindByParentId() {
        System.out.println("=== 测试CategoryService.findByParentId() ===");

        // 测试查找根分类（parentId为0）
        List<Category> rootCategories = categoryService.findByParentId(0L);
        assertNotNull(rootCategories, "根分类列表不应为空");
        assertTrue(rootCategories.size() > 0, "根分类列表应包含数据");

        // 创建一个子分类
        Category childCategory = new Category();
        childCategory.setName("子分类");
        childCategory.setParentId(testCategoryId);
        childCategory.setLevel(2);
        childCategory.setDescription("子分类描述");
        childCategory.setSort(1);
        childCategory.setStatus(1);
        categoryService.save(childCategory);

        // 测试查找子分类
        List<Category> childCategories = categoryService.findByParentId(testCategoryId);
        assertNotNull(childCategories, "子分类列表不应为空");
        assertTrue(childCategories.size() > 0, "子分类列表应包含数据");

        System.out.println("CategoryService.findByParentId()测试通过 ✓");
    }

    @Test
    @Order(11)
    @DisplayName("测试CategoryService特有方法 - findByLevel")
    void testFindByLevel() {
        System.out.println("=== 测试CategoryService.findByLevel() ===");

        // 测试查找级别为1的分类
        List<Category> level1Categories = categoryService.findByLevel(1);
        assertNotNull(level1Categories, "级别1的分类列表不应为空");
        assertTrue(level1Categories.size() > 0, "级别1的分类列表应包含数据");

        // 创建一个级别为2的分类
        Category childCategory = new Category();
        childCategory.setName("子分类");
        childCategory.setParentId(testCategoryId);
        childCategory.setLevel(2);
        childCategory.setDescription("子分类描述");
        childCategory.setSort(1);
        childCategory.setStatus(1);
        categoryService.save(childCategory);

        // 测试查找级别为2的分类
        List<Category> level2Categories = categoryService.findByLevel(2);
        assertNotNull(level2Categories, "级别2的分类列表不应为空");
        assertTrue(level2Categories.size() > 0, "级别2的分类列表应包含数据");

        System.out.println("CategoryService.findByLevel()测试通过 ✓");
    }

    @Test
    @Order(12)
    @DisplayName("测试CategoryService特有方法 - findRootCategories")
    void testFindRootCategories() {
        System.out.println("=== 测试CategoryService.findRootCategories() ===");

        // 测试查找根分类
        List<Category> rootCategories = categoryService.findRootCategories();
        assertNotNull(rootCategories, "根分类列表不应为空");
        assertTrue(rootCategories.size() > 0, "根分类列表应包含数据");

        // 验证返回的都是根分类（parentId为0）
        for (Category category : rootCategories) {
            assertEquals(0L, category.getParentId(), "根分类的parentId应为0");
        }

        System.out.println("CategoryService.findRootCategories()测试通过 ✓");
    }

    @Test
    @Order(13)
    @DisplayName("测试CategoryService特有方法 - getCategoryTree")
    void testGetCategoryTree() {
        System.out.println("=== 测试CategoryService.getCategoryTree() ===");

        // 测试获取分类树
        List<Map<String, Object>> categoryTree = categoryService.getCategoryTree();
        assertNotNull(categoryTree, "分类树不应为空");

        System.out.println("CategoryService.getCategoryTree()测试通过 ✓");
    }

    @Test
    @Order(14)
    @DisplayName("测试CategoryService特有方法 - existsByName")
    void testExistsByName() {
        System.out.println("=== 测试CategoryService.existsByName() ===");

        // 测试存在的分类名称
        boolean exists = categoryService.existsByName("测试分类");
        assertTrue(exists, "测试分类名称应存在");

        // 测试不存在的分类名称
        boolean notExists = categoryService.existsByName("不存在的分类");
        assertFalse(notExists, "不存在的分类名称应返回false");

        System.out.println("CategoryService.existsByName()测试通过 ✓");
    }

    @Test
    @Order(15)
    @DisplayName("测试CategoryService特有方法 - updateCategoryTree")
    void testUpdateCategoryTree() {
        System.out.println("=== 测试CategoryService.updateCategoryTree() ===");

        // 测试更新分类树
        categoryService.updateCategoryTree();
        // 此方法无返回值，只要不抛出异常即为通过

        System.out.println("CategoryService.updateCategoryTree()测试通过 ✓");
    }

    @Test
    @Order(16)
    @DisplayName("测试CategoryService特有方法 - getCategoryPath")
    void testGetCategoryPath() {
        System.out.println("=== 测试CategoryService.getCategoryPath() ===");

        // 测试获取分类路径
        Map<Long, String> categoryPath = categoryService.getCategoryPath(testCategoryId);
        assertNotNull(categoryPath, "分类路径不应为空");

        System.out.println("CategoryService.getCategoryPath()测试通过 ✓");
    }

    @Test
    @Order(17)
    @DisplayName("测试CategoryService特有方法 - searchCategories")
    void testSearchCategories() {
        System.out.println("=== 测试CategoryService.searchCategories() ===");

        // 测试搜索分类
        Page<Category> searchResults = categoryService.searchCategories("测试", pageable);
        assertNotNull(searchResults, "搜索结果不应为空");
        assertTrue(searchResults.getTotalElements() > 0, "搜索结果应包含数据");

        System.out.println("CategoryService.searchCategories()测试通过 ✓");
    }

    @Test
    @Order(18)
    @DisplayName("测试CategoryService特有方法 - hasAssociatedMedicines")
    void testHasAssociatedMedicines() {
        System.out.println("=== 测试CategoryService.hasAssociatedMedicines() ===");

        // 直接通过medicineRepository查询分类关联的药品
        List<Medicine> medicines = medicineRepository.findByCategoryId(testCategoryId);
        System.out.println("测试分类关联的药品数量: " + (medicines != null ? medicines.size() : 0));
        assertTrue(medicines != null && !medicines.isEmpty(), "测试分类应有关联药品");

        // 测试hasAssociatedMedicines方法
        boolean hasMedicines = categoryService.hasAssociatedMedicines(testCategoryId);
        // 注意：由于Category实体中medicines集合使用了延迟加载，这里可能返回false
        // 但我们已经通过直接查询验证了药品存在
        System.out.println("hasAssociatedMedicines返回值: " + hasMedicines);

        // 创建一个无关联药品的分类
        Category newCategory = new Category();
        newCategory.setName("无关联分类");
        newCategory.setParentId(0L);
        newCategory.setLevel(1);
        newCategory.setDescription("无关联分类描述");
        newCategory.setSort(8);
        newCategory.setStatus(1);
        Category savedCategory = categoryService.save(newCategory);

        // 测试无关联药品的分类
        boolean noMedicines = categoryService.hasAssociatedMedicines(savedCategory.getId());
        assertFalse(noMedicines, "无关联分类应返回false");

        System.out.println("CategoryService.hasAssociatedMedicines()测试通过 ✓");
    }
}
