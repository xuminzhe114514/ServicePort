package com.example.demo.entity;

import com.example.demo.views.Views;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "prediction_result")
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonInclude(JsonInclude.Include.NON_NULL)
@DynamicUpdate
public class PredictionResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private Long id;//主键ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    @JsonIgnoreProperties({"predictionResults", "purchaseOrders", "saleRecords", "stocks"})
    @JsonView(Views.Internal.class)
    private Medicine medicine;//关联药品

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "prediction_date", nullable = false)
    @JsonView(Views.Public.class)
    private LocalDate predictionDate;//预测日期

    @Column(name = "predicted_quantity", nullable = false)
    @JsonView(Views.Public.class)
    private Integer predictedQuantity;//预测需求量

    @Column(name = "confidence_interval_lower")
    @JsonView(Views.Detail.class)
    private Integer confidenceIntervalLower;//置信区间下限

    @Column(name = "confidence_interval_upper")
    @JsonView(Views.Detail.class)
    private Integer confidenceIntervalUpper;//置信区间上限

    @Column(name = "model_type", length = 50)
    @JsonView(Views.Internal.class)
    private String modelType;//预测模型类型

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "accuracy_rate", precision = 5, scale = 2)
    @JsonView(Views.Detail.class)
    private BigDecimal accuracyRate;//预测准确率（百分比）

    @Column(name = "recommended_order_quantity")
    @JsonView(Views.Detail.class)
    private Integer recommendedOrderQuantity;//建议订购数量

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    @JsonView(Views.Admin.class)
    private LocalDateTime createTime = LocalDateTime.now();//创建时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    @JsonView(Views.Admin.class)
    private LocalDateTime updateTime = LocalDateTime.now();//更新时间

    @PrePersist
    public void prePersist() {
        if (this.predictedQuantity == null) {
            this.predictedQuantity = 0;
        }
        if (this.confidenceIntervalLower == null) {
            this.confidenceIntervalLower = 0;
        }
        if (this.confidenceIntervalUpper == null) {
            this.confidenceIntervalUpper = 0;
        }
        if (this.modelType == null) {
            this.modelType = "";
        }
        if (this.accuracyRate == null) {
            this.accuracyRate = BigDecimal.ZERO;
        }
        if (this.recommendedOrderQuantity == null) {
            this.recommendedOrderQuantity = 0;
        }
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
        if (this.updateTime == null) {
            this.updateTime = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}