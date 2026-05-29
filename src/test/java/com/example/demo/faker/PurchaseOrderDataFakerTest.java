package com.example.demo.faker;

import com.example.demo.entity.Medicine;
import com.example.demo.entity.PurchaseOrder;
import com.example.demo.entity.Stock;
import com.example.demo.repository.MedicineRepository;
import com.example.demo.repository.PurchaseOrderRepository;
import com.example.demo.repository.StockRepository;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@SpringBootTest
public class PurchaseOrderDataFakerTest {

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private StockRepository stockRepository;

    private final Faker faker = new Faker();
    private final Random random = new Random();

    @Test
    public void generatePurchaseOrderData() {
        List<Medicine> medicines = medicineRepository.findAll();
        if (medicines.size() < 15) {
            System.out.println("数据库中药品数量不足15种，请先添加足够的药品数据");
            return;
        }

        int orderCount = 20;
        int stockCount = 0;

        for (int i = 0; i < orderCount; i++) {
            PurchaseOrder order = generatePurchaseOrder(medicines);
            purchaseOrderRepository.save(order);

            if (order.getOrderStatus() == 2) {
                Stock stock = generateStockForOrder(order);
                stockRepository.save(stock);
                stockCount++;
            }
        }

        System.out.println("成功生成并保存了 " + orderCount + " 条采购订单模拟数据");
        System.out.println("其中已到货订单生成了 " + stockCount + " 条库存记录");
    }

    private PurchaseOrder generatePurchaseOrder(List<Medicine> medicines) {
        PurchaseOrder order = new PurchaseOrder();

        Medicine randomMedicine = medicines.get(random.nextInt(medicines.size()));
        order.setMedicine(randomMedicine);
        order.setUnitPrice(randomMedicine.getPurchasePrice());

        order.setQuantity(25);
        order.calculateTotalAmount();

        order.setOrderNo(generateOrderNo());

        order.setSupplier(null);
        order.setOperator(null);

        LocalDateTime orderTime = generateOrderTime();
        order.setOrderTime(orderTime);

        LocalDate expectedArrival = generateExpectedArrival(orderTime);
        order.setExpectedArrival(expectedArrival);

        int status = random.nextInt(3);
        order.setOrderStatus(status);

        if (status == 2) {
            order.setActualArrival(generateActualArrival(expectedArrival));
        }

        return order;
    }

    private Stock generateStockForOrder(PurchaseOrder order) {
        Stock stock = new Stock();

        stock.setMedicine(order.getMedicine());
        stock.setBatchNumber(generateBatchNumber());

        LocalDate productionDate = generateProductionDate();
        stock.setProductionDate(productionDate);
        stock.setExpirationDate(generateExpirationDate(productionDate));

        stock.setQuantity(order.getQuantity());

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

    private String generateOrderNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = String.format("%06d", random.nextInt(1000000));
        return "O" + dateStr + randomStr;
    }

    private String generateBatchNumber() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = String.format("%06d", random.nextInt(1000000));
        return "t" + dateStr + randomStr;
    }

    private LocalDateTime generateOrderTime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoDaysAgo = now.minusDays(2);
        long secondsBetween = java.time.temporal.ChronoUnit.SECONDS.between(twoDaysAgo, now);
        return twoDaysAgo.plusSeconds(random.nextLong(secondsBetween + 1));
    }

    private LocalDate generateExpectedArrival(LocalDateTime orderTime) {
        int daysToAdd = random.nextInt(6) + 7;
        return orderTime.toLocalDate().plusDays(daysToAdd);
    }

    private LocalDateTime generateActualArrival(LocalDate expectedArrival) {
        int offsetDays = random.nextInt(5) - 2;
        LocalDate actualDate = expectedArrival.plusDays(offsetDays);
        int hour = random.nextInt(24);
        int minute = random.nextInt(60);
        return LocalDateTime.of(actualDate.getYear(), actualDate.getMonthValue(), actualDate.getDayOfMonth(), hour, minute);
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