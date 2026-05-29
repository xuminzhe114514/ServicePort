package com.example.demo.runner;

import com.example.demo.entity.Medicine;
import com.example.demo.entity.PurchaseOrder;
import com.example.demo.entity.Stock;
import com.example.demo.repository.MedicineRepository;
import com.example.demo.repository.PurchaseOrderRepository;
import com.example.demo.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Deprecated
public class PurchaseOrderDataRunner implements CommandLineRunner {

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private StockRepository stockRepository;

    private final Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        List<Medicine> medicines = medicineRepository.findAll();
        if (medicines.size() < 15) {
            System.out.println("[PurchaseOrderDataRunner] 药品数量不足15种，跳过采购订单生成");
            return;
        }

        int count = 5;
        int stockCount = 0;

        for (int i = 0; i < count; i++) {
            PurchaseOrder order = generatePurchaseOrder(medicines);
            purchaseOrderRepository.save(order);

            if (order.getOrderStatus() == 2) {
                stockRepository.save(generateStockForOrder(order));
                stockCount++;
            }
        }

        System.out.println("[PurchaseOrderDataRunner] 成功生成 " + count + " 条采购订单，其中 " + stockCount + " 条已到货并生成库存");
    }

    private PurchaseOrder generatePurchaseOrder(List<Medicine> medicines) {
        PurchaseOrder order = new PurchaseOrder();
        Medicine medicine = medicines.get(random.nextInt(medicines.size()));
        order.setMedicine(medicine);
        order.setUnitPrice(medicine.getPurchasePrice());
        order.setQuantity(25);
        order.calculateTotalAmount();
        order.setOrderNo(generateOrderNo());
        order.setSupplier(null);
        order.setOperator(null);

        LocalDateTime orderTime = generateOrderTime();
        order.setOrderTime(orderTime);
        order.setExpectedArrival(generateExpectedArrival(orderTime));

        int status = random.nextInt(3);
        order.setOrderStatus(status);
        if (status == 2) {
            order.setActualArrival(generateActualArrival(order.getExpectedArrival()));
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
        stock.setReorderPoint(Math.max(0, warningQuantity - (random.nextInt(6) + 10)));
        stock.setMinimumOrderQuantity(20);
        stock.setLeadTimeDays(random.nextInt(15) + 5);
        stock.setShelfLocation(generateShelfLocation());
        stock.setStatus(1);

        return stock;
    }

    private String generateOrderNo() {
        return "O" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
               String.format("%06d", random.nextInt(1000000));
    }

    private String generateBatchNumber() {
        return "t" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
               String.format("%06d", random.nextInt(1000000));
    }

    private LocalDateTime generateOrderTime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoDaysAgo = now.minusDays(2);
        return twoDaysAgo.plusSeconds(random.nextLong(java.time.temporal.ChronoUnit.SECONDS.between(twoDaysAgo, now) + 1));
    }

    private LocalDate generateExpectedArrival(LocalDateTime orderTime) {
        return orderTime.toLocalDate().plusDays(random.nextInt(6) + 7);
    }

    private LocalDateTime generateActualArrival(LocalDate expectedArrival) {
        LocalDate actualDate = expectedArrival.plusDays(random.nextInt(5) - 2);
        return LocalDateTime.of(actualDate.getYear(), actualDate.getMonthValue(), actualDate.getDayOfMonth(),
                random.nextInt(24), random.nextInt(60));
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