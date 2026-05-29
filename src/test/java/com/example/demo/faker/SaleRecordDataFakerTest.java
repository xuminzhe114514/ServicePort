package com.example.demo.faker;

import com.example.demo.entity.Medicine;
import com.example.demo.entity.SaleRecord;
import com.example.demo.entity.Stock;
import com.example.demo.entity.User;
import com.example.demo.repository.MedicineRepository;
import com.example.demo.repository.SaleRecordRepository;
import com.example.demo.repository.StockRepository;
import com.example.demo.repository.UserRepository;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@SpringBootTest
public class SaleRecordDataFakerTest {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private SaleRecordRepository saleRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    private final Faker faker = new Faker();
    private final Random random = new Random();

    /**
     * 为指定药品生成5月19号的销售记录
     * 每笔记录销售数量为，只生成记录不修改库存
     * 
     * 使用方式：右键运行此测试方法即可
     */
    @Test
    public void generateWeeklySalesRecordForMedicine() {
        Long medicineId = 554L; // 指定药品ID
        int saleQuantity = 1; // 指定每笔销售数量
        int recordCount = 1; // 生成记录数量
        LocalDate targetDate = LocalDate.of(2026, 5, 28); // 指定生成日期
        
        System.out.println("\n========== 开始为药品ID=" + medicineId + "生成5月19号销售记录 ==========");
        
        // 获取药品信息
        Medicine medicine = medicineRepository.findById(medicineId).orElse(null);
        if (medicine == null) {
            System.out.println(" 未找到ID为 " + medicineId + " 的药品");
            return;
        }
        System.out.println("药品名称: " + medicine.getName());
        System.out.println("药品规格: " + medicine.getSpecification());
        System.out.println("零售价格: ¥" + medicine.getRetailPrice());
        System.out.println("每笔销售数量: " + saleQuantity);
        System.out.println("生成日期: " + targetDate);
        
        // 获取操作员
        User operator = userRepository.findById(1L).orElse(null);
        if (operator == null) {
            System.out.println("❌ 未找到ID为1的操作员");
            return;
        }
        
        int totalCreated = 0;
        
        // 生成指定数量的指定日期销售记录
        for (int i = 0; i < recordCount; i++) {
            // 设置时间为当天的随机时刻（9:00-22:00之间）
            int randomHour = 9 + random.nextInt(14); // 9-22
            int randomMinute = random.nextInt(60);
            int randomSecond = random.nextInt(60);
            LocalDateTime saleDateTime = LocalDateTime.of(targetDate, LocalTime.of(randomHour, randomMinute, randomSecond));
            
            // 生成销售记录（不修改库存，指定quantity）
            SaleRecord record = generateSaleRecordForDateWithoutStockWithQuantity(medicine, operator, saleDateTime, saleQuantity);
            saleRecordRepository.save(record);
            
            totalCreated++;
            System.out.println(String.format("✅ 第%d条 | 销售时间: %s | 数量: %d | 单价: ¥%s | 总金额: ¥%s",
                    totalCreated, saleDateTime, record.getQuantity(), medicine.getRetailPrice(), record.getTotalAmount()));
        }
        
        System.out.println("\n========== 成功生成 " + totalCreated + " 条销售记录 ==========");
    }

    @Test
    public void generateSaleRecordData() {
        List<Stock> availableStocks = stockRepository.findByStatusWithMedicine(1);
        if (availableStocks.isEmpty()) {
            System.out.println("数据库中没有库存数量大于0的库存记录，请先添加库存数据");
            return;
        }

        User operator = userRepository.findById(1L).orElse(null);
        if (operator == null) {
            System.out.println("数据库中没有ID为1的用户，请先添加用户数据");
            return;
        }

        int recordCount = 15;
        int createdCount = 0;

        for (int i = 0; i < recordCount; i++) {
            if (availableStocks.isEmpty()) {
                System.out.println("可用库存已用完，共生成了 " + createdCount + " 条销售记录");
                break;
            }

            int stockIndex = random.nextInt(availableStocks.size());
            Stock stock = availableStocks.get(stockIndex);

            Medicine medicine = stock.getMedicine();

            SaleRecord record = generateSaleRecord(stock, medicine, operator);
            saleRecordRepository.save(record);

            stock.setQuantity(stock.getQuantity() - 1);
            stockRepository.save(stock);

            if (stock.getQuantity() <= 0) {
                availableStocks.remove(stockIndex);
            }

            createdCount++;
        }

        System.out.println("成功生成并保存了 " + createdCount + " 条销售记录");
    }

    private SaleRecord generateSaleRecord(Stock stock, Medicine medicine, User operator) {
        SaleRecord record = new SaleRecord();

        record.setRecordNo(generateRecordNo());
        record.setMedicine(medicine);
        record.setUnitPrice(medicine.getRetailPrice());
        record.setQuantity(1);
        record.calculateTotalAmount();

        record.setCustomerInfo(null);
        record.setCustomerType(null);
        record.setRx(medicine.isPrescription());

        record.setSymptom(null);
        record.setSaleTime(LocalDateTime.now());

        record.setOperator(operator);
        record.setRemark(null);

        return record;
    }

    /**
     * 生成指定日期的销售记录
     * @param stock 库存记录
     * @param medicine 药品信息
     * @param operator 操作员
     * @param saleDateTime 销售时间
     * @return 销售记录
     */
    private SaleRecord generateSaleRecordForDate(Stock stock, Medicine medicine, User operator, LocalDateTime saleDateTime) {
        SaleRecord record = new SaleRecord();

        record.setRecordNo(generateRecordNo());
        record.setMedicine(medicine);
        record.setUnitPrice(medicine.getRetailPrice());
        record.setQuantity(1);
        record.calculateTotalAmount();

        record.setCustomerInfo(null);
        record.setCustomerType(null);
        record.setRx(medicine.isPrescription());

        record.setSymptom(null);
        record.setSaleTime(saleDateTime);

        record.setOperator(operator);
        record.setRemark(null);

        return record;
    }

    private String generateRecordNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = String.format("%06d", random.nextInt(1000000));
        return "S" + dateStr + randomStr;
    }

    /**
     * 生成指定日期的recordNo
     * @param targetDate 目标日期
     * @return recordNo
     */
    private String generateRecordNoForDate(LocalDate targetDate) {
        String dateStr = targetDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = String.format("%06d", random.nextInt(1000000));
        return "S" + dateStr + randomStr;
    }

    /**
     * 为指定药品生成指定日期的销售记录（不修改库存）
     * @param medicine 药品信息
     * @param operator 操作员
     * @param saleDateTime 销售时间
     * @param t 第几天 (0 ~ 14)
     * @return 销售记录
     */
    private SaleRecord generateSaleRecordForDateWithoutStock(Medicine medicine, User operator, LocalDateTime saleDateTime, int t) {
        SaleRecord record = new SaleRecord();

        record.setRecordNo(generateRecordNoForDate(saleDateTime.toLocalDate()));
        record.setMedicine(medicine);
        record.setUnitPrice(medicine.getRetailPrice());
        
        // 计算quantity：线性趋势 + 季节性波动 + 高斯噪声
        double linear = 18.0 + 0.3 * t;
        double seasonal = 5.0 * Math.sin(2 * Math.PI * t / 7.0 + Math.PI / 4.0);
        double noise = 1.5 * random.nextGaussian();
        int quantity = Math.max(0, (int) Math.round(linear + seasonal + noise));
        
        record.setQuantity(quantity);
        record.calculateTotalAmount();

        record.setCustomerInfo(null);
        record.setCustomerType(null);
        record.setRx(medicine.isPrescription());

        record.setSymptom(null);
        record.setSaleTime(saleDateTime);

        record.setOperator(operator);
        record.setRemark(null);

        return record;
    }

    /**
     * 为指定药品生成指定日期的销售记录（不修改库存，指定quantity）
     * @param medicine 药品信息
     * @param operator 操作员
     * @param saleDateTime 销售时间
     * @param quantity 指定的销售数量
     * @return 销售记录
     */
    private SaleRecord generateSaleRecordForDateWithoutStockWithQuantity(Medicine medicine, User operator, LocalDateTime saleDateTime, int quantity) {
        SaleRecord record = new SaleRecord();

        record.setRecordNo(generateRecordNoForDate(saleDateTime.toLocalDate()));
        record.setMedicine(medicine);
        record.setUnitPrice(medicine.getRetailPrice());
        
        record.setQuantity(quantity);
        record.calculateTotalAmount();

        record.setCustomerInfo(null);
        record.setCustomerType(null);
        record.setRx(medicine.isPrescription());

        record.setSymptom(null);
        record.setSaleTime(saleDateTime);

        record.setOperator(operator);
        record.setRemark(null);

        return record;
    }

    /**
     * 为指定药品生成5月1日至5月15日的连续销售记录
     * 指定药品：苯唑西林、布洛芬、维生素D、糠酸莫米松、天王补心丸（片）、肾炎康复片
     * 每天生成一条记录，不修改库存
     */
    @Test
    public void generateMaySalesRecords() {
        System.out.println("\n========== 开始生成5月1日-5月15日销售记录 ==========");

        User operator = userRepository.findById(1L).orElse(null);
        if (operator == null) {
            System.out.println("❌ 未找到ID为1的操作员");
            return;
        }

        String[] medicineNames = {

                "糠酸莫米松",

        };

        int totalCreated = 0;
        LocalDate startDate = LocalDate.of(2026, 5, 1);
        LocalDate endDate = LocalDate.of(2026, 5, 15);

        // 遍历每个药品
        for (String medicineName : medicineNames) {
            System.out.println("\n========== 处理药品: " + medicineName + " ==========");

            // 查找药品
            List<Medicine> medicines = medicineRepository.findByNameContaining(medicineName);
            if (medicines.isEmpty()) {
                System.out.println("⚠️  未找到名称包含'" + medicineName + "'的药品，跳过");
                continue;
            }

            Medicine medicine = medicines.get(0); // 取第一个匹配的药品
            System.out.println("✅ 找到药品: " + medicine.getName() + " (ID=" + medicine.getId() + ")");

            // 遍历5月1日到5月15日，t从0到14
            LocalDate currentDate = startDate;
            int t = 0;
            while (!currentDate.isAfter(endDate)) {
                // 生成当天的随机时刻（早9点到晚10点之间）
                int randomHour = 9 + random.nextInt(14); // 9-22
                int randomMinute = random.nextInt(60);
                int randomSecond = random.nextInt(60);
                LocalDateTime saleDateTime = LocalDateTime.of(currentDate, LocalTime.of(randomHour, randomMinute, randomSecond));

                // 生成销售记录
                SaleRecord record = generateSaleRecordForDateWithoutStock(medicine, operator, saleDateTime, t);
                saleRecordRepository.save(record);

                System.out.println(String.format("✅ %s (t=%d) | 销售时间: %s | 数量: %d | 单价: ¥%s",
                        currentDate, t, saleDateTime, record.getQuantity(), medicine.getRetailPrice()));

                totalCreated++;
                currentDate = currentDate.plusDays(1);
                t++;
            }
        }

        System.out.println("\n========== 成功生成 " + totalCreated + " 条销售记录 ==========");
    }
}