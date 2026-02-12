package com.example.demo.entity;

import com.example.demo.views.Views;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sale_record")
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonInclude(JsonInclude.Include.NON_NULL)
@DynamicUpdate
public class SaleRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private Long id;//主键ID

    @Column(name = "record_no", nullable = false, unique = true, length = 50)
    @JsonView(Views.Public.class)
    private String recordNo;//销售单号（唯一）

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    @JsonIgnoreProperties({"saleRecords", "purchaseOrders", "stocks", "predictionResults", "symptoms"})
    @JsonView(Views.Internal.class)
    private Medicine medicine;//关联药品

    @Column(name = "quantity", nullable = false)
    @JsonView(Views.Internal.class)
    private Integer quantity;//销售数量

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    @JsonView(Views.Internal.class)
    private BigDecimal unitPrice;//销售单价

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    @JsonView(Views.Internal.class)
    private BigDecimal totalAmount;//总金额

    @Column(name = "customer_info", length = 200)
    @JsonView(Views.Detail.class)
    private String customerInfo;//顾客信息

    @Column(name = "customer_type")
    @JsonView(Views.Detail.class)
    private Integer customerType;//顾客类型

    @Column(name = "is_Rx")
    @JsonView(Views.Detail.class)
    private boolean isRx;//是否为处方药销售

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "sale_record_symptom",
            joinColumns = @JoinColumn(name = "sale_record_id"),
            inverseJoinColumns = @JoinColumn(name = "symptom_id")
    )
    @JsonIgnoreProperties({"description"})
    @JsonView(Views.Detail.class)
    private List<Symptom> symptom;//关联症状

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "sale_time", nullable = false)
    @JsonView(Views.Detail.class)
    private LocalDateTime saleTime = LocalDateTime.now();//销售时间

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
}