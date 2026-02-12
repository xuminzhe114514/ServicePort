package com.example.demo.entity;

import com.example.demo.views.Views;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock")
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonInclude(JsonInclude.Include.NON_NULL)
@DynamicUpdate
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private Long id;//主键ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    @JsonIgnoreProperties({"stocks", "saleRecords", "purchaseOrders", "predictionResults", "symptoms"})
    @JsonView(Views.Internal.class)
    private Medicine medicine;//关联药品

    @Column(name = "batch_number", length = 50)
    @JsonView(Views.Internal.class)
    private String batchNumber;//批号

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "production_date")
    @JsonView(Views.Detail.class)
    private LocalDate productionDate;//生产日期

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "expiration_date")
    @JsonView(Views.Detail.class)
    private LocalDate expirationDate;//有效期至

    @Column(name = "quantity", nullable = false)
    @JsonView(Views.Public.class)
    private Integer quantity = 0;//当前数量

    @Column(name = "warning_quantity")
    @JsonView(Views.Internal.class)
    private Integer warningQuantity = 10;//库存预警数量

    @Column(name = "shelf_location", length = 50)
    @JsonView(Views.Internal.class)
    private String shelfLocation;//货架位置

    @Column(name = "status")
    @JsonView(Views.Internal.class)
    private Integer status = 1;//状态：0-过期，1-正常

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    @JsonView(Views.Admin.class)
    private LocalDateTime createTime = LocalDateTime.now();//创建时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    @JsonView(Views.Admin.class)
    private LocalDateTime updateTime = LocalDateTime.now();//更新时间

    @Column(name = "minimum_order_quantity")
    @JsonView(Views.Detail.class)
    private Integer minimumOrderQuantity;//最小订购数量

    @Column(name = "lead_time_days")
    @JsonView(Views.Detail.class)
    private Integer leadTimeDays;//采购提前期（天）

    @Column(name = "reorder_point")
    @JsonView(Views.Detail.class)
    private Integer reorderPoint;//再订货点

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(expirationDate);
    }

    public boolean needsWarning() {
        return quantity <= warningQuantity;
    }
}