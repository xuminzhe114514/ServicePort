package com.example.demo.entity;

import com.example.demo.views.Views;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medicine")
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonInclude(JsonInclude.Include.NON_NULL)
@DynamicUpdate
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private Long id;//主键ID

    @Column(name = "medicine_code", nullable = false, unique = true, length = 50)
    @JsonView(Views.Public.class)
    private String medicineCode;//药品编码（唯一）

    @Column(name = "name", nullable = false, length = 100)
    @JsonView(Views.Public.class)
    private String name;//药品名称

    @Column(name = "generic_name", length = 100)
    @JsonView(Views.Public.class)
    private String genericName;//通用名称

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties({"medicines"})
    @JsonView(Views.Internal.class)
    private Category category;//分类信息

    @Column(name = "specification", length = 200)
    @JsonView(Views.Public.class)
    private String specification;//药品规格

    @Column(name = "unit", length = 20)
    @JsonView(Views.Public.class)
    private String unit;//单位（盒、瓶、支等）

    @Column(name = "manufacturer", length = 200)
    @JsonView(Views.Internal.class)
    private String manufacturer;//生产厂家

    @Column(name = "approval_number", length = 100)
    @JsonView(Views.Detail.class)
    private String approvalNumber;//批准文号

    @Column(name = "description", columnDefinition = "TEXT")
    @JsonView(Views.Detail.class)
    private String description;//药品描述

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "retail_price", precision = 10, scale = 2)
    @JsonView(Views.Public.class)
    private BigDecimal retailPrice;//零售价

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "purchase_price", precision = 10, scale = 2)
    @JsonView(Views.Detail.class)
    private BigDecimal purchasePrice;//采购价

    @Column(name = "status")
    @JsonView(Views.Internal.class)
    private Integer status = 1;//状态：0-停用，1-启用

    @Column(name = "is_seasonal")
    @JsonView(Views.Detail.class)
    private boolean isSeasonal;//是否为季节性药品

    @Column(name = "is_prescription")
    @JsonView(Views.Detail.class)
    private boolean isPrescription;//是否为处方药

    @Column(name = "storage_requirement")
    @JsonView(Views.Detail.class)
    private Integer storageRequirement;//存储要求

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    @JsonView(Views.Admin.class)
    private LocalDateTime createTime = LocalDateTime.now();//创建时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    @JsonView(Views.Admin.class)
    private LocalDateTime updateTime = LocalDateTime.now();//更新时间

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"medicine"})
    @JsonView(Views.Detail.class)
    private List<Stock> stocks;//库存记录

    @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"medicine", "operator"})
    @JsonView(Views.Detail.class)
    private List<SaleRecord> saleRecords = new ArrayList<>();//销售记录

    @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"medicine", "operator"})
    @JsonView(Views.Detail.class)
    private List<PurchaseOrder> purchaseOrders = new ArrayList<>();//采购订单

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "medicine_symptom",
            joinColumns = @JoinColumn(name = "medicine_id"),
            inverseJoinColumns = @JoinColumn(name = "symptom_id")
    )
    @JsonIgnoreProperties({"description"})
    @JsonView(Views.Detail.class)
    private List<Symptom> symptoms = new ArrayList<>();//适应症状
}