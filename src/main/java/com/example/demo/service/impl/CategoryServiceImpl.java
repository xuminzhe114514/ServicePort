package com.example.demo.service.impl;

import com.example.demo.entity.Category;
import com.example.demo.entity.Medicine;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl extends BaseServiceImpl<Category, Long, CategoryRepository>
        implements CategoryService {

    public CategoryServiceImpl(CategoryRepository repository) {
        super(repository);
    }

    @Override
    public List<Category> findByParentId(Long parentId) {
        return repository.findByParentId(parentId);
    }

    @Override
    public List<Category> findByLevel(Integer level) {
        return repository.findByLevel(level);
    }

    @Override
    public List<Category> findRootCategories() {
        return repository.findByParentIdAndStatusOrderBySortAsc(0L, 1);
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public List<Map<String, Object>> getCategoryTree() {
        List<Category> allCategories = repository.findByStatusOrderBySortAsc(1);
        if (allCategories.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, List<Category>> parentToChildren = new HashMap<>();
        for (Category category : allCategories) {
            Long parentId = (category.getParentId() == null) ? 0L : category.getParentId();
            parentToChildren.computeIfAbsent(parentId, k -> new ArrayList<>()).add(category);
        }
        List<Category> rootCategories = parentToChildren.getOrDefault(0L, Collections.emptyList());
        return buildTreeNodes(rootCategories, parentToChildren);
    }

    private List<Map<String, Object>> buildTreeNodes(List<Category> categories,
                                                     Map<Long, List<Category>> parentToChildren) {
        List<Map<String, Object>> nodes = new ArrayList<>();
        for (Category category : categories) {
            Map<String, Object> node = new LinkedHashMap<>();
            node.put("id", category.getId());
            node.put("name", category.getName());
            node.put("parentId", category.getParentId());
            node.put("level", category.getLevel());
            node.put("description", category.getDescription());
            node.put("sort", category.getSort());
            node.put("status", category.getStatus());
            List<Category> children = parentToChildren.get(category.getId());
            if (children != null && !children.isEmpty()) {
                node.put("children", buildTreeNodes(children, parentToChildren));
            } else {
                node.put("children", Collections.emptyList());
            }
            nodes.add(node);
        }
        return nodes;
    }

    @Override
    public boolean existsByName(String name) {
        return repository.findByName(name).isPresent();
    }

    @Override
    public void updateCategoryTree() {
        List<Category> allCategories = repository.findAll();
        Map<Long, List<Category>> parentToChildren = new HashMap<>();
        for (Category category : allCategories) {
            Long parentId = (category.getParentId() == null) ? 0L : category.getParentId();
            parentToChildren.computeIfAbsent(parentId, k -> new ArrayList<>()).add(category);
        }
        Queue<Category> queue = new LinkedList<>();
        List<Category> updatedCategories = new ArrayList<>();
        List<Category> roots = parentToChildren.getOrDefault(0L, Collections.emptyList());
        for (Category root : roots) {
            root.setLevel(1);
            queue.offer(root);
            updatedCategories.add(root);
        }
        while (!queue.isEmpty()) {
            Category parent = queue.poll();
            List<Category> children = parentToChildren.get(parent.getId());
            if (children != null && !children.isEmpty()) {
                for (Category child : children) {
                    child.setLevel(parent.getLevel() + 1);
                    queue.offer(child);
                    updatedCategories.add(child);
                }
            }
        }
        if (!updatedCategories.isEmpty()) {
            repository.saveAll(updatedCategories);
        }
    }

    @Override
    public Map<Long, String> getCategoryPath(Long categoryId) {
        Map<Long, String> pathMap = new LinkedHashMap<>();
        Category category = repository.findById(categoryId).orElse(null);

        while (category != null) {
            pathMap.put(category.getId(), category.getName());
            if (category.getParentId() != null && category.getParentId() > 0) {
                category = repository.findById(category.getParentId()).orElse(null);
            } else {
                category = null;
            }
        }

        Map<Long, String> reversed = new LinkedHashMap<>();
        List<Long> keys = new ArrayList<>(pathMap.keySet());
        Collections.reverse(keys);
        for (Long key : keys) {
            reversed.put(key, pathMap.get(key));
        }

        return reversed;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Category> searchCategories(String keyword, Pageable pageable) {
        List<Category> allCategories = repository.findByStatusOrderBySortAsc(1);

        String lowerKeyword = keyword.toLowerCase();
        List<Category> filtered = allCategories.stream()
                .filter(category ->
                        (category.getName() != null && category.getName().toLowerCase().contains(lowerKeyword)) ||
                                (category.getDescription() != null && category.getDescription().toLowerCase().contains(lowerKeyword))
                )
                .collect(Collectors.toList());

        int total = filtered.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        List<Category> content;
        if (start >= total) {
            content = Collections.emptyList();
        } else {
            content = filtered.subList(start, end);
        }

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAssociatedMedicines(Long categoryId) {
        Category category = findById(categoryId);
        if (category == null) {
            return false;
        }
        List<Medicine> medicines = category.getMedicines();
        return medicines != null && !medicines.isEmpty();
    }

    @Override
    public void delete(Long id) {
        if (hasAssociatedMedicines(id)) {
            throw new IllegalStateException("该分类下存在药品，无法删除");
        }
        super.delete(id);
    }
}