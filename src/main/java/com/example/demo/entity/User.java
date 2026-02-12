package com.example.demo.entity;

import com.example.demo.views.Views;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonInclude(JsonInclude.Include.NON_NULL)
@DynamicUpdate
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private Long id;//主键ID

    @Column(name = "username", nullable = false, unique = true, length = 50)
    @JsonView(Views.Public.class)
    private String username;//用户名（唯一）

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", nullable = false)
    @JsonView(Views.Admin.class)
    private String password;//密码（仅限写入）

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "real_name", length = 50)
    @JsonView(Views.Internal.class)
    private String realName;//真实姓名

    @Column(name = "phone", length = 20)
    @JsonView(Views.Detail.class)
    private String phone;//手机号

    @Column(name = "email", length = 100)
    @JsonView(Views.Detail.class)
    private String email;//邮箱

    @Column(name = "role", nullable = false, length = 20)
    @JsonView(Views.Admin.class)
    private String role;//角色：ADMIN, PHARMACIST, PURCHASER

    @Column(name = "status")
    @JsonView(Views.Admin.class)
    private Integer status = 1;//状态：0-禁用，1-正常

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
}