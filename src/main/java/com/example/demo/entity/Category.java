package com.example.demo.entity;

import com.example.demo.views.Views;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonInclude(JsonInclude.Include.NON_NULL)
@DynamicUpdate
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private Long id;//主键ID

    @Column(name = "name", nullable = false, length = 50)
    @JsonView(Views.Public.class)
    private String name;//分类名称

    @Column(name = "parent_id")
    @JsonView(Views.Internal.class)
    private Long parentId = 0L;//父分类ID（0表示一级分类）

    @Column(name = "level")
    @JsonView(Views.Internal.class)
    private Integer level = 1;//分类级别

    @Column(name = "description", length = 500)
    @JsonView(Views.Detail.class)
    private String description = "";//分类描述

    @Column(name = "sort")
    @JsonView(Views.Internal.class)
    private Integer sort = 0;//排序值

    @Column(name = "status")
    @JsonView(Views.Admin.class)
    private Integer status = 1;//状态：0-禁用，1-启用

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    @JsonView(Views.Admin.class)
    private LocalDateTime createTime = LocalDateTime.now();//创建时间

    @PrePersist
    public void prePersist() {
        if (this.parentId == null) {
            this.parentId = 0L;
        }
        if (this.level == null) {
            this.level = 1;
        }
        if (this.description == null) {
            this.description = "";
        }
        if (this.sort == null) {
            this.sort = 0;
        }
        if (this.status == null) {
            this.status = 1;
        }
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
    }

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"category", "stocks", "saleRecords", "purchaseOrders", "predictionResults", "symptoms"})
    @JsonView(Views.Detail.class)
    @ToString.Exclude
    private List<Medicine> medicines = new ArrayList<>();//关联药品列表
}