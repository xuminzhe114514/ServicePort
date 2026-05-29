package com.example.demo.runner;

import com.example.demo.entity.Medicine;
import com.example.demo.entity.SaleRecord;
import com.example.demo.entity.Stock;
import com.example.demo.entity.User;
import com.example.demo.repository.SaleRecordRepository;
import com.example.demo.repository.StockRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Random;
@Deprecated
public class SaleRecordDataRunner implements CommandLineRunner {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private SaleRecordRepository saleRecordRepository;

    @Autowired
    private UserRepository userRepository;

    private final Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        List<Stock> availableStocks = stockRepository.findByStatusWithMedicine(1);
        if (availableStocks.isEmpty()) {
            System.out.println("[SaleRecordDataRunner] 没有可用库存，跳过销售记录生成");
            return;
        }

        User operator = userRepository.findById(1L).orElse(null);
        if (operator == null) {
            System.out.println("[SaleRecordDataRunner] 没有ID为1的用户，跳过销售记录生成");
            return;
        }

        int count = 3;
        int created = 0;

        for (int i = 0; i < count; i++) {
            if (availableStocks.isEmpty()) break;

            int index = random.nextInt(availableStocks.size());
            Stock stock = availableStocks.get(index);
            Medicine medicine = stock.getMedicine();

            SaleRecord record = generateSaleRecord(medicine, operator);
            saleRecordRepository.save(record);

            stock.setQuantity(stock.getQuantity() - 1);
            stockRepository.save(stock);

            if (stock.getQuantity() <= 0) {
                availableStocks.remove(index);
            }
            created++;
        }

        System.out.println("[SaleRecordDataRunner] 成功生成 " + created + " 条销售记录");
    }

    private SaleRecord generateSaleRecord(Medicine medicine, User operator) {
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

    private String generateRecordNo() {
        return "S" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
               String.format("%06d", random.nextInt(1000000));
    }
}