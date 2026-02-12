package com.example.demo.entity;

import com.example.demo.views.Views;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_order")
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonInclude(JsonInclude.Include.NON_NULL)
@DynamicUpdate
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private Long id;//主键ID

    @Column(name = "order_no", nullable = false, unique = true, length = 50)
    @JsonView(Views.Public.class)
    private String orderNo;//订单编号（唯一）

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    @JsonIgnoreProperties({"purchaseOrders", "saleRecords", "stocks", "predictionResults", "symptoms"})
    @JsonView(Views.Internal.class)
    private Medicine medicine;//关联药品

    @Column(name = "quantity", nullable = false)
    @JsonView(Views.Internal.class)
    private Integer quantity;//采购数量

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    @JsonView(Views.Internal.class)
    private BigDecimal unitPrice;//采购单价

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    @JsonView(Views.Internal.class)
    private BigDecimal totalAmount;//总金额

    @Column(name = "supplier", length = 200)
    @JsonView(Views.Detail.class)
    private String supplier;//供应商

    @Column(name = "order_status", nullable = false)
    @JsonView(Views.Public.class)
    private Integer orderStatus = 0;//订单状态：0-待处理，1-已确认，2-已到货，3-已取消

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "order_time", nullable = false)
    @JsonView(Views.Detail.class)
    private LocalDateTime orderTime = LocalDateTime.now();//下单时间

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "expected_arrival")
    @JsonView(Views.Detail.class)
    private LocalDate expectedArrival;//预计到货日期

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "actual_arrival")
    @JsonView(Views.Detail.class)
    private LocalDateTime actualArrival;//实际到货时间

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id")
    @JsonIgnoreProperties({"password"})
    @JsonView(Views.Admin.class)
    private User operator;//操作员

    @Column(name = "remark", length = 500)
    @JsonView(Views.Admin.class)
    private String remark;//备注

    public void calculateTotalAmount() {
        if (unitPrice != null && quantity != null) {
            this.totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public boolean isPending() {
        return orderStatus == 0;
    }

    public boolean isConfirmed() {
        return orderStatus == 1;
    }

    public boolean isArrived() {
        return orderStatus == 2;
    }

    public boolean isCancelled() {
        return orderStatus == 3;
    }
}