package com.example.demo.service.impl;

import com.example.demo.entity.Medicine;
import com.example.demo.entity.Stock;
import com.example.demo.repository.MedicineRepository;
import com.example.demo.repository.StockRepository;
import com.example.demo.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.support.PageableExecutionUtils.getPage;

@Service
@Transactional
public class StockServiceImpl extends BaseServiceImpl<Stock, Long, StockRepository>
        implements StockService {

    protected StockServiceImpl(StockRepository repository) {
        super(repository);
    }

    @Autowired
    private MedicineRepository medicineRepository;

    @Override
    public Page<Stock> findByMedicineId(Long medicineId, Pageable pageable) {
        List<Stock> allStocks = repository.findByMedicineId(medicineId);

        List<Stock> filtered = allStocks.stream().
                filter(
                        stock -> (stock.getStatus() == 1)
                ).collect(Collectors.toList());

        int total = filtered.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        List<Stock> content;
        if (start >= total) {
            content = Collections.emptyList();
        } else {
            content = filtered.subList(start, Math.min(end, total));
        }

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Integer getTotalStock(Long medicineId) {
        Object totalObj = repository.sumQuantityByMedicineId(medicineId);
        return totalObj != null ? (totalObj instanceof Long ? ((Long)totalObj).intValue() : totalObj instanceof Integer ? (Integer)totalObj : 0) : 0;
    }

    @Override
    public Page<Stock> getExpiringStock(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        List<Stock> expiringStocks = repository.findExpiringStock(startDate, endDate);

        List<Stock> filtered = expiringStocks.stream().
                filter(
                        stock -> (stock.getStatus() == 1)
                ).collect(Collectors.toList());

        int total = filtered.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        List<Stock> content;
        if (start >= total) {
            content = Collections.emptyList();
        } else {
            content = filtered.subList(start, Math.min(end, total));
        }
        return new PageImpl<>(content, pageable, total);

    }

    @Override
    public Page<Stock> getLowStock(Pageable pageable) {
        List<Stock> lowStocks = repository.findLowStock();

        List<Stock> filtered = lowStocks.stream().
                filter(
                        stock -> (stock.getStatus() == 1)
                ).collect(Collectors.toList());

        int total = filtered.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        List<Stock> content;
        if (start >= total) {
            content = Collections.emptyList();
        } else {
            content = filtered.subList(start, Math.min(end, total));
        }
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Map<Long, Integer> getLowStockSummary() {
        List<Object[]> results = repository.findLowStockSummary();
        return results.stream()
                .collect(Collectors.toMap(
                        obj -> (Long) obj[0],
                        obj -> (Integer) obj[1]
                ));
    }

    @Override
    public void reduceStock(Long medicineId, Integer quantity) {
        List<Stock> stocks = repository.findByMedicineId(medicineId);
        stocks.sort(Comparator.comparing(Stock::getExpirationDate));
        int remaining = quantity;
        for (Stock stock : stocks) {
            if (remaining <= 0) break;
            if (stock.getStatus() == 1 && stock.getQuantity() > 0) {
                int available = stock.getQuantity();
                if (available >= remaining) {
                    stock.setQuantity(available - remaining);
                    remaining = 0;
                } else {
                    stock.setQuantity(0);
                    remaining -= available;
                }
                repository.save(stock);
            }
        }
        if (remaining > 0) {
            throw new IllegalArgumentException("stocks are not enough");
        }
    }

    @Override
    public void increaseStock(Long medicineId, Integer quantity, String batchNumber,
                              LocalDate expirationDate) {
        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new IllegalArgumentException("药品不存在: " + medicineId));
        List<Stock> existingStocks = repository.findByBatchNumber(batchNumber)
                .stream()
                .filter(stock -> stock.getMedicine().getId().equals(medicineId))
                .filter(stock -> stock.getStatus() == 1)
                .collect(Collectors.toList());

        if (!existingStocks.isEmpty()) {
            Stock existingStock = existingStocks.get(0);
            existingStock.setQuantity(existingStock.getQuantity() + quantity);
            repository.save(existingStock);
        } else {
            Stock stock = new Stock();
            stock.setMedicine(medicine);
            stock.setBatchNumber(batchNumber);
            stock.setExpirationDate(expirationDate);
            stock.setQuantity(quantity);
            stock.setStatus(1);
            stock.setWarningQuantity(10);
            stock.setProductionDate(LocalDate.now());

            repository.save(stock);
        }
    }

    @Override
    public boolean checkStockAvailability(Long medicineId, Integer requiredQuantity) {
        Integer totalStock = getTotalStock(medicineId);
        return totalStock != null && totalStock >= requiredQuantity;
    }

    @Override
    public Page<Stock> findByStatus(Integer status, Pageable pageable) {
        List<Stock> allStocks = repository.findByStatus(status);

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allStocks.size());
        int total = allStocks.size();

        List<Stock> content;
        if (start >= total) {
            content = Collections.emptyList();
        } else {
            content = allStocks.subList(start, Math.min(end, total));
        }
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Stock> findByShelfLocation(String shelfLocation, Pageable pageable) {
        List<Stock> allStocks = repository.findByShelfLocation(shelfLocation);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allStocks.size());
        int total = allStocks.size();
        List<Stock> content;
        if (start >= total) {
            content = Collections.emptyList();
        } else {
            content = allStocks.subList(start, Math.min(end, total));
        }
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Stock> findByBatchNumber(String batchNumber, Pageable pageable) {
        List<Stock> byBatchNumberStocks = repository.findByBatchNumber(batchNumber);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), byBatchNumberStocks.size());
        int total = byBatchNumberStocks.size();
        List<Stock> content;
        if (start >= total) {
            content = Collections.emptyList();
        } else {
            content = byBatchNumberStocks.subList(start, Math.min(end, total));
        }
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<Stock> findExpiringWithinDays(int days) {
        LocalDate now = LocalDate.now();
        LocalDate endDate = now.plusDays(days);
        return repository.findExpiringStock(now, endDate).stream()
                .filter(stock -> stock.getStatus() == 1)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getStockStatisticsByMedicine(Long medicineId) {
        // 获取总库存
        Object totalStockObj = repository.sumQuantityByMedicineId(medicineId);
        Integer totalStock = totalStockObj != null ? (totalStockObj instanceof Long ? ((Long)totalStockObj).intValue() : totalStockObj instanceof Integer ? (Integer)totalStockObj : 0) : 0;

        // 获取低库存信息（需要在内存中过滤）
        List<Stock> allStocks = repository.findByMedicineId(medicineId);
        long lowStockCount = allStocks.stream()
                .filter(stock -> stock.getStatus() == 1)
                .filter(stock -> stock.getQuantity() <= stock.getWarningQuantity())
                .count();

        // 获取即将过期库存
        LocalDate now = LocalDate.now();
        LocalDate thirtyDaysLater = now.plusDays(30);
        long expiringStockCount = repository.findExpiringStock(now, thirtyDaysLater).stream()
                .filter(stock -> stock.getMedicine().getId().equals(medicineId))
                .filter(stock -> stock.getStatus() == 1)
                .count();

        // 批次数量
        long batchCount = allStocks.stream()
                .filter(stock -> stock.getStatus() == 1)
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStock", totalStock != null ? totalStock : 0);
        stats.put("lowStockCount", lowStockCount);
        stats.put("expiringStockCount", expiringStockCount);
        stats.put("batchCount", batchCount);

        return stats;
    }

    @Override
    public Page<Stock> findByMedicineIdAndBatchNumber(Long medicineId, String batchNumber, Pageable pageable) {
        List<Stock> allStocks = repository.findByBatchNumber(batchNumber);

        List<Stock> filtered = allStocks.stream()
                .filter(stock -> stock.getMedicine().getId().equals(medicineId))
                .filter(stock -> stock.getStatus() == 1)
                .collect(Collectors.toList());

        return getPage(filtered, pageable, filtered::size);
    }

    @Override
    public Page<Stock> findExpiredStock(Pageable pageable) {
        LocalDate now = LocalDate.now();
        List<Stock> allStocks = repository.findAll();

        List<Stock> expiredStocks = allStocks.stream()
                .filter(stock -> stock.getExpirationDate() != null)
                .filter(stock -> stock.getExpirationDate().isBefore(now))
                .filter(stock -> stock.getStatus() == 1)
                .collect(Collectors.toList());

        return getPage(expiredStocks, pageable, expiredStocks::size);
    }

    @Override
    public Page<Stock> findNearExpiryStock(int days, Pageable pageable) {
        LocalDate now = LocalDate.now();
        LocalDate endDate = now.plusDays(days);

        List<Stock> allStocks = repository.findExpiringStock(now, endDate);

        List<Stock> filtered = allStocks.stream()
                .filter(stock -> stock.getStatus() == 1)
                .collect(Collectors.toList());

        return getPage(filtered, pageable, filtered::size);
    }

    // 新增方法实现
    @Override
    public Double calculateStockTurnoverRate(String period) {
        // 根据时间段计算库存周转率
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate;
    
        // 根据period参数设置开始日期
        switch (period) {
            case "week":
                startDate = endDate.minusWeeks(1);
                break;
            case "month":
                startDate = endDate.minusMonths(1);
                break;
            case "quarter":
                startDate = endDate.minusMonths(3);
                break;
            case "year":
                startDate = endDate.minusYears(1);
                break;
            default:
                startDate = endDate.minusMonths(1);
        }
        
        // 使用仓库方法计算库存周转率
        List<Object[]> turnoverData = repository.calculateStockTurnoverRate(startDate, endDate);
        
        // 计算平均周转率
        return turnoverData.stream()
                .mapToDouble(obj -> {
                    if (obj.length > 3) {
                        Double turnoverRate = (Double) obj[3];
                        return turnoverRate != null ? turnoverRate : 0;
                    }
                    return 0;
                })
                .average()
                .orElse(0.0);
    }

    @Override
    public Double calculateTotalStockValue() {
        Object valueObj = repository.calculateTotalStockValue();
        return valueObj != null ? (valueObj instanceof BigDecimal ? ((BigDecimal)valueObj).doubleValue() : valueObj instanceof Double ? (Double)valueObj : 0.0) : 0.0;
    }

    @Override
    public Map<String, Object> getStockValueByCategory() {
        List<Object[]> results = repository.calculateStockValueByCategory();
        Map<String, Object> valueByCategory = new HashMap<>();
        results.forEach(result -> {
            String categoryName = result[0].toString();
            Double value = Double.parseDouble(result[1].toString());
            valueByCategory.put(categoryName, value);
        });
        return valueByCategory;
    }

    @Override
    public Page<Stock> getStockAlerts(Pageable pageable) {
        List<Stock> alerts = new ArrayList<>();
        
        // 低库存预警
        List<Stock> lowStock = repository.findLowStock();
        alerts.addAll(lowStock);
        
        // 即将过期预警
        LocalDate now = LocalDate.now();
        LocalDate thirtyDaysLater = now.plusDays(30);
        List<Stock> expiringStock = repository.findExpiringStock(now, thirtyDaysLater);
        alerts.addAll(expiringStock);
        
        int total = alerts.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        List<Stock> content;
        if (start >= total) {
            content = Collections.emptyList();
        } else {
            content = alerts.subList(start, end);
        }

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public void transferStock(Long fromStockId, Long toStockId, Integer quantity) {
        Stock fromStock = repository.findById(fromStockId).orElse(null);
        Stock toStock = repository.findById(toStockId).orElse(null);
        
        if (fromStock == null || toStock == null) {
            throw new IllegalArgumentException("库存记录不存在");
        }
        
        if (fromStock.getQuantity() < quantity) {
            throw new IllegalArgumentException("转出库存不足");
        }
        
        // 减少转出库存
        fromStock.setQuantity(fromStock.getQuantity() - quantity);
        
        // 增加转入库存
        toStock.setQuantity(toStock.getQuantity() + quantity);
        
        repository.save(fromStock);
        repository.save(toStock);
    }

    @Override
    public void setMinimumStockLevel(Long medicineId, Integer minLevel) {
        List<Stock> stocks = repository.findByMedicineId(medicineId);
        stocks.forEach(stock -> {
            stock.setWarningQuantity(minLevel);
        });
        repository.saveAll(stocks);
    }

    @Override
    public Map<Long, Object> getStockInventoryReport() {
        List<Stock> allStocks = repository.findAll();
        Map<Long, Object> report = new HashMap<>();
        
        allStocks.forEach(stock -> {
            Map<String, Object> stockInfo = new HashMap<>();
            stockInfo.put("quantity", stock.getQuantity());
            stockInfo.put("batchNumber", stock.getBatchNumber());
            stockInfo.put("expirationDate", stock.getExpirationDate());
            stockInfo.put("shelfLocation", stock.getShelfLocation());
            stockInfo.put("status", stock.getStatus());
            report.put(stock.getId(), stockInfo);
        });
        
        return report;
    }

    @Override
    public Page<Stock> findByStorageCondition(Integer condition, Pageable pageable) {
        List<Stock> allStocks = repository.findAll().stream()
                .filter(stock -> stock.getStatus() == 1)
                .filter(stock -> {
                    Medicine medicine = stock.getMedicine();
                    return medicine != null && medicine.getStorageRequirement() != null && 
                            medicine.getStorageRequirement().equals(condition);
                })
                .collect(Collectors.toList());

        int total = allStocks.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        List<Stock> content;
        if (start >= total) {
            content = Collections.emptyList();
        } else {
            content = allStocks.subList(start, end);
        }

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Stock> findByShelfLocationContaining(String location, Pageable pageable) {
        List<Stock> stocks = repository.findByShelfLocationContaining(location);
        return getPage(stocks, pageable, stocks::size);
    }
}