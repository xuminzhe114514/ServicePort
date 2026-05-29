package com.example.demo.faker;

import com.example.demo.entity.Medicine;
import com.example.demo.entity.Stock;
import com.example.demo.repository.MedicineRepository;
import com.example.demo.repository.StockRepository;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@SpringBootTest
public class StockDataFakerTest {

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private StockRepository stockRepository;

    private final Faker faker = new Faker();
    private final Random random = new Random();

    @Test
    public void generateStockData() {
        List<Medicine> medicines = medicineRepository.findAll();
        if (medicines.size() <= 10) {
            System.out.println("数据库中药品数量不足10种，请先添加足够的药品数据");
            return;
        }

        int stockCount = 50;
        for (int i = 0; i < stockCount; i++) {
            Stock stock = generateStock(medicines);
            stockRepository.save(stock);
        }

        System.out.println("成功生成并保存了 " + stockCount + " 条库存模拟数据");
    }

    private Stock generateStock(List<Medicine> medicines) {
        Stock stock = new Stock();

        Medicine randomMedicine = medicines.get(random.nextInt(medicines.size()));
        stock.setMedicine(randomMedicine);

        stock.setBatchNumber(generateBatchNumber());

        LocalDate productionDate = generateProductionDate();
        stock.setProductionDate(productionDate);

        stock.setExpirationDate(generateExpirationDate(productionDate));

        int quantity = random.nextInt(66) + 5;
        stock.setQuantity(quantity);

        int warningQuantity = random.nextInt(21) + 30;
        stock.setWarningQuantity(warningQuantity);

        int reorderPoint = warningQuantity - (random.nextInt(6) + 10);
        stock.setReorderPoint(Math.max(0, reorderPoint));

        stock.setMinimumOrderQuantity(20);

        int leadTimeDays = random.nextInt(15) + 5;
        stock.setLeadTimeDays(leadTimeDays);

        stock.setShelfLocation(generateShelfLocation());

        stock.setStatus(1);

        return stock;
    }

    private String generateBatchNumber() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = String.format("%06d", random.nextInt(1000000));
        return "t" + dateStr + randomStr;
    }

    private LocalDate generateProductionDate() {
        LocalDate startDate = LocalDate.of(2026, 3, 1);
        LocalDate endDate = LocalDate.now();
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
        return startDate.plusDays(random.nextLong(daysBetween + 1));
    }

    private LocalDate generateExpirationDate(LocalDate productionDate) {
        int monthsToAdd = random.nextInt(25) + 12;
        return productionDate.plusMonths(monthsToAdd);
    }

    private String generateShelfLocation() {
        char zone = (char) ('A' + random.nextInt(5));
        int row = random.nextInt(10) + 1;
        int shelf = random.nextInt(5) + 1;
        return String.format("%c-%02d-%02d", zone, row, shelf);
    }
}