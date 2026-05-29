package com.example.demo.runner;

import com.example.demo.entity.Medicine;
import com.example.demo.entity.Stock;
import com.example.demo.repository.MedicineRepository;
import com.example.demo.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
@Deprecated
public class StockDataRunner implements CommandLineRunner {

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private StockRepository stockRepository;

    private final Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        List<Medicine> medicines = medicineRepository.findAll();
        if (medicines.size() <= 10) {
            System.out.println("[StockDataRunner] 药品数量不足10种，跳过库存数据生成");
            return;
        }

        int count = 10;
        for (int i = 0; i < count; i++) {
            Stock stock = generateStock(medicines);
            stockRepository.save(stock);
        }

        System.out.println("[StockDataRunner] 成功生成 " + count + " 条库存记录");
    }

    private Stock generateStock(List<Medicine> medicines) {
        Stock stock = new Stock();
        stock.setMedicine(medicines.get(random.nextInt(medicines.size())));
        stock.setBatchNumber(generateBatchNumber());

        LocalDate productionDate = generateProductionDate();
        stock.setProductionDate(productionDate);
        stock.setExpirationDate(generateExpirationDate(productionDate));

        stock.setQuantity(random.nextInt(66) + 5);

        int warningQuantity = random.nextInt(21) + 30;
        stock.setWarningQuantity(warningQuantity);
        stock.setReorderPoint(Math.max(0, warningQuantity - (random.nextInt(6) + 10)));

        stock.setMinimumOrderQuantity(20);
        stock.setLeadTimeDays(random.nextInt(15) + 5);
        stock.setShelfLocation(generateShelfLocation());
        stock.setStatus(1);

        return stock;
    }

    private String generateBatchNumber() {
        return "t" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
               String.format("%06d", random.nextInt(1000000));
    }

    private LocalDate generateProductionDate() {
        LocalDate startDate = LocalDate.of(2026, 3, 1);
        return startDate.plusDays(random.nextLong(java.time.temporal.ChronoUnit.DAYS.between(startDate, LocalDate.now()) + 1));
    }

    private LocalDate generateExpirationDate(LocalDate productionDate) {
        return productionDate.plusMonths(random.nextInt(25) + 12);
    }

    private String generateShelfLocation() {
        return String.format("%c-%02d-%02d", 
                (char) ('A' + random.nextInt(5)), 
                random.nextInt(10) + 1, 
                random.nextInt(5) + 1);
    }
}