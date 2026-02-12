package com.example.demo.entity;

import com.example.demo.views.Views;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;


@Entity
@Table(name = "symptom")
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonInclude(JsonInclude.Include.NON_NULL)
@DynamicUpdate
public class Symptom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonView(Views.Public.class)
    private Integer id;//主键ID

    @Column(name = "name", nullable = false)
    @JsonView(Views.Public.class)
    private String name;//症状名称

    @Column(name = "description")
    @JsonView(Views.Detail.class)
    private String description;//症状描述
}