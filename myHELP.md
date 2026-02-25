# 帮助文档

---

## 实体层 (Entity Layer) 文档

### 1. 类：Category（药品分类）

**位置**：`com.example.demo.entity`

**继承关系**：无

**类注解说明**：
- `@Entity`：标识为JPA实体类
- `@Table(name = "category")`：映射到数据库表category
- `@Data`：Lombok注解，自动生成getter/setter等方法
- `@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")`：Jackson注解，处理序列化时的循环引用
- `@JsonInclude(JsonInclude.Include.NON_NULL)`：序列化时忽略null值
- `@DynamicUpdate`：Hibernate注解，动态生成update语句

**属性列表**：v

| 字段名      | 数据类型       | 可为空 | 默认值            | 描述                      | 可见性权限 | 注解/备注                                                                                                                                                                                                                                |
| ----------- | -------------- | ------ | ----------------- | ------------------------- | ---------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| id          | Long           | 否     | 自增              | 主键ID                    | Public     | `@Id` `@GeneratedValue(strategy = GenerationType.IDENTITY)` `@JsonView(Views.Public.class)`                                                                                                                                              |
| name        | String         | 否     | -                 | 分类名称                  | Public     | `@Column(name = "name", nullable = false, length = 50)` `@JsonView(Views.Public.class)`                                                                                                                                                  |
| parentId    | Long           | 是     | 0L                | 父分类ID（0表示一级分类） | Internal   | `@Column(name = "parent_id")` `@JsonView(Views.Internal.class)`                                                                                                                                                                          |
| level       | Integer        | 是     | 1                 | 分类级别                  | Internal   | `@Column(name = "level")` `@JsonView(Views.Internal.class)`                                                                                                                                                                              |
| description | String         | 是     | -                 | 分类描述                  | Detail     | `@Column(name = "description", length = 500)` `@JsonView(Views.Detail.class)`                                                                                                                                                            |
| sort        | Integer        | 是     | 0                 | 排序值                    | Internal   | `@Column(name = "sort")` `@JsonView(Views.Internal.class)`                                                                                                                                                                               |
| status      | Integer        | 是     | 1                 | 状态：0-禁用，1-启用      | Admin      | `@Column(name = "status")` `@JsonView(Views.Admin.class)`                                                                                                                                                                                |
| createTime  | LocalDateTime  | 是     | 当前时间          | 创建时间                  | Admin      | `@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")` `@Column(name = "create_time")` `@JsonView(Views.Admin.class)`                                                                                                                            |
| medicines   | List<Medicine> | 是     | new ArrayList<>() | 关联药品列表              | Detail     | `@OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)` `@JsonIgnoreProperties({"category", "stocks", "saleRecords", "purchaseOrders", "predictionResults", "symptoms"})` `@JsonView(Views.Detail.class)` |

**方法**：无自定义业务方法

**关联关系**：
- 与Medicine：一对多关系，一个分类包含多个药品
- 在Medicine类中通过`@ManyToOne`关联

**注意事项**：
- 所有字段都有对应的`@JsonView`注解，用于控制不同权限下的字段可见性
- medicines字段设置了`@JsonIgnoreProperties`，避免序列化时的循环引用问题

---

### 2. 类：Medicine（药品信息）

**位置**：`com.example.demo.entity`

**继承关系**：无

**类注解说明**：
- `@Entity`：标识为JPA实体类
- `@Table(name = "medicine")`：映射到数据库表medicine
- `@Data`：Lombok注解，自动生成getter/setter等方法
- `@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")`：Jackson注解，处理序列化时的循环引用
- `@JsonInclude(JsonInclude.Include.NON_NULL)`：序列化时忽略null值
- `@DynamicUpdate`：Hibernate注解，动态生成update语句

**属性列表**：

| 字段名             | 数据类型            | 可为空 | 默认值   | 描述                 | 可见性权限 | 注解/备注                                                                                                                                                                                                                                                      |
| ------------------ | ------------------- | ------ | -------- | -------------------- | ---------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| id                 | Long                | 否     | 自增     | 主键ID               | Public     | `@Id` `@GeneratedValue(strategy = GenerationType.IDENTITY)` `@JsonView(Views.Public.class)`                                                                                                                                                                    |
| medicineCode       | String              | 否     | -        | 药品编码（唯一）     | Public     | `@Column(name = "medicine_code", nullable = false, unique = true, length = 50)` `@JsonView(Views.Public.class)`                                                                                                                                                |
| name               | String              | 否     | -        | 药品名称             | Public     | `@Column(name = "name", nullable = false, length = 100)` `@JsonView(Views.Public.class)`                                                                                                                                                                       |
| genericName        | String              | 是     | -        | 通用名称             | Public     | `@Column(name = "generic_name", length = 100)` `@JsonView(Views.Public.class)`                                                                                                                                                                                 |
| category           | Category            | 是     | -        | 分类信息             | Internal   | `@ManyToOne(fetch = FetchType.LAZY)` `@JoinColumn(name = "category_id")` `@JsonIgnoreProperties({"medicines"})` `@JsonView(Views.Internal.class)`                                                                                                              |
| specification      | String              | 是     | -        | 药品规格             | Public     | `@Column(name = "specification", length = 200)` `@JsonView(Views.Public.class)`                                                                                                                                                                                |
| unit               | String              | 是     | -        | 单位（盒、瓶、支等） | Public     | `@Column(name = "unit", length = 20)` `@JsonView(Views.Public.class)`                                                                                                                                                                                          |
| manufacturer       | String              | 是     | -        | 生产厂家             | Internal   | `@Column(name = "manufacturer", length = 200)` `@JsonView(Views.Internal.class)`                                                                                                                                                                               |
| approvalNumber     | String              | 是     | -        | 批准文号             | Detail     | `@Column(name = "approval_number", length = 100)` `@JsonView(Views.Detail.class)`                                                                                                                                                                              |
| description        | String              | 是     | -        | 药品描述             | Detail     | `@Column(name = "description", columnDefinition = "TEXT")` `@JsonView(Views.Detail.class)`                                                                                                                                                                     |
| retailPrice        | BigDecimal          | 是     | -        | 零售价               | Public     | `@JsonFormat(shape = JsonFormat.Shape.STRING)` `@Column(name = "retail_price", precision = 10, scale = 2)` `@JsonView(Views.Public.class)`                                                                                                                     |
| purchasePrice      | BigDecimal          | 是     | -        | 采购价               | Detail     | `@JsonFormat(shape = JsonFormat.Shape.STRING)` `@Column(name = "purchase_price", precision = 10, scale = 2)` `@JsonView(Views.Detail.class)`                                                                                                                   |
| status             | Integer             | 是     | 1        | 状态：0-停用，1-启用 | Internal   | `@Column(name = "status")` `@JsonView(Views.Internal.class)`                                                                                                                                                                                                   |
| isSeasonal         | boolean             | 是     | false    | 是否为季节性药品     | Detail     | `@Column(name = "is_seasonal")` `@JsonView(Views.Detail.class)`                                                                                                                                                                                                |
| isPrescription     | boolean             | 是     | false    | 是否为处方药         | Detail     | `@Column(name = "is_prescription")` `@JsonView(Views.Detail.class)`                                                                                                                                                                                            |
| storageRequirement | Integer             | 是     | -        | 存储要求             | Detail     | `@Column(name = "storage_requirement")` `@JsonView(Views.Detail.class)`                                                                                                                                                                                        |
| createTime         | LocalDateTime       | 是     | 当前时间 | 创建时间             | Admin      | `@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")` `@Column(name = "create_time")` `@JsonView(Views.Admin.class)`                                                                                                                                                  |
| updateTime         | LocalDateTime       | 是     | 当前时间 | 更新时间             | Admin      | `@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")` `@Column(name = "update_time")` `@JsonView(Views.Admin.class)`                                                                                                                                                  |
| stocks             | List<Stock>         | 是     | -        | 库存记录             | Detail     | `@OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL, fetch = FetchType.LAZY)` `@JsonIgnoreProperties({"medicine"})` `@JsonView(Views.Detail.class)`                                                                                                   |
| saleRecords        | List<SaleRecord>    | 是     | -        | 销售记录             | Detail     | `@OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL, fetch = FetchType.LAZY)` `@JsonIgnoreProperties({"medicine", "operator"})` `@JsonView(Views.Detail.class)`                                                                                       |
| purchaseOrders     | List<PurchaseOrder> | 是     | -        | 采购订单             | Detail     | `@OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL, fetch = FetchType.LAZY)` `@JsonIgnoreProperties({"medicine", "operator"})` `@JsonView(Views.Detail.class)`                                                                                       |
| symptoms           | List<Symptom>       | 是     | -        | 适应症状             | Detail     | `@ManyToMany(fetch = FetchType.LAZY)` `@JoinTable(name = "medicine_symptom", joinColumns = @JoinColumn(name = "medicine_id"), inverseJoinColumns = @JoinColumn(name = "symptom_id"))` `@JsonIgnoreProperties({"description"})` `@JsonView(Views.Detail.class)` |

**方法**：
- `preUpdate()`：在更新前自动设置updateTime为当前时间（`@PreUpdate`注解）
  - **功能**：在实体更新前被调用，自动更新updateTime字段为当前时间
  - **参数**：无
  - **返回值**：无

**关联关系**：
- 与Category：多对一关系，属于一个分类
- 与Stock：一对多关系，有多个库存批次
- 与SaleRecord：一对多关系，有多条销售记录
- 与PurchaseOrder：一对多关系，有多个采购订单
- 与Symptom：多对多关系，通过medicine_symptom中间表关联

---

### 3. 类：PredictionResult（预测结果）

**位置**：`com.example.demo.entity`

**继承关系**：无

**类注解说明**：
- `@Entity`：标识为JPA实体类
- `@Table(name = "prediction_result")`：映射到数据库表prediction_result
- `@Data`：Lombok注解，自动生成getter/setter等方法
- `@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")`：Jackson注解，处理序列化时的循环引用
- `@JsonInclude(JsonInclude.Include.NON_NULL)`：序列化时忽略null值
- `@DynamicUpdate`：Hibernate注解，动态生成update语句

**属性列表**：

| 字段名                   | 数据类型      | 可为空 | 默认值   | 描述                                       | 可见性权限 | 注解/备注                                                                                                                                                                                                              |
| ------------------------ | ------------- | ------ | -------- | ------------------------------------------ | ---------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| id                       | Long          | 否     | 自增     | 主键ID                                     | Public     | `@Id` `@GeneratedValue(strategy = GenerationType.IDENTITY)` `@JsonView(Views.Public.class)`                                                                                                                            |
| medicine                 | Medicine      | 否     | -        | 关联药品                                   | Internal   | `@ManyToOne(fetch = FetchType.LAZY)` `@JoinColumn(name = "medicine_id", nullable = false)` `@JsonIgnoreProperties({"predictionResults", "purchaseOrders", "saleRecords", "stocks"})` `@JsonView(Views.Internal.class)` |
| predictionDate           | LocalDate     | 否     | -        | 预测日期                                   | Public     | `@JsonFormat(pattern = "yyyy-MM-dd")` `@Column(name = "prediction_date", nullable = false)` `@JsonView(Views.Public.class)`                                                                                            |
| predictedQuantity        | Integer       | 否     | -        | 预测需求量                                 | Public     | `@Column(name = "predicted_quantity", nullable = false)` `@JsonView(Views.Public.class)`                                                                                                                               |
| confidenceIntervalLower  | Integer       | 是     | -        | 置信区间下限                               | Detail     | `@Column(name = "confidence_interval_lower")` `@JsonView(Views.Detail.class)`                                                                                                                                          |
| confidenceIntervalUpper  | Integer       | 是     | -        | 置信区间上限                               | Detail     | `@Column(name = "confidence_interval_upper")` `@JsonView(Views.Detail.class)`                                                                                                                                          |
| modelType                | String        | 是     | -        | 预测模型类型（Prophet, ARIMA, LightGBM等） | Internal   | `@Column(name = "model_type", length = 50)` `@JsonView(Views.Internal.class)`                                                                                                                                          |
| accuracyRate             | BigDecimal    | 是     | -        | 预测准确率（百分比）                       | Detail     | `@JsonFormat(shape = JsonFormat.Shape.STRING)` `@Column(name = "accuracy_rate", precision = 5, scale = 2)` `@JsonView(Views.Detail.class)`                                                                             |
| recommendedOrderQuantity | Integer       | 是     | -        | 建议订购数量                               | Detail     | `@Column(name = "recommended_order_quantity")` `@JsonView(Views.Detail.class)`                                                                                                                                         |
| createTime               | LocalDateTime | 是     | 当前时间 | 创建时间                                   | Admin      | `@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")` `@Column(name = "create_time")` `@JsonView(Views.Admin.class)`                                                                                                          |
| updateTime               | LocalDateTime | 是     | 当前时间 | 更新时间                                   | Admin      | `@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")` `@Column(name = "update_time")` `@JsonView(Views.Admin.class)`                                                                                                          |

**方法**：
- `preUpdate()`：在更新前自动设置updateTime为当前时间（`@PreUpdate`注解）
  - **功能**：在实体更新前被调用，自动更新updateTime字段为当前时间
  - **参数**：无
  - **返回值**：无

**关联关系**：
- 与Medicine：多对一关系，关联一个药品

---

### 4. 类：PurchaseOrder（采购订单）

**位置**：`com.example.demo.entity`

**继承关系**：无

**类注解说明**：
- `@Entity`：标识为JPA实体类
- `@Table(name = "purchase_order")`：映射到数据库表purchase_order
- `@Data`：Lombok注解，自动生成getter/setter等方法
- `@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")`：Jackson注解，处理序列化时的循环引用
- `@JsonInclude(JsonInclude.Include.NON_NULL)`：序列化时忽略null值
- `@DynamicUpdate`：Hibernate注解，动态生成update语句

**属性列表**：

| 字段名          | 数据类型      | 可为空 | 默认值   | 描述                                             | 可见性权限 | 注解/备注                                                                                                                                                                                                                          |
| --------------- | ------------- | ------ | -------- | ------------------------------------------------ | ---------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| id              | Long          | 否     | 自增     | 主键ID                                           | Public     | `@Id` `@GeneratedValue(strategy = GenerationType.IDENTITY)` `@JsonView(Views.Public.class)`                                                                                                                                        |
| orderNo         | String        | 否     | -        | 订单编号（唯一）                                 | Public     | `@Column(name = "order_no", nullable = false, unique = true, length = 50)` `@JsonView(Views.Public.class)`                                                                                                                         |
| medicine        | Medicine      | 否     | -        | 关联药品                                         | Internal   | `@ManyToOne(fetch = FetchType.LAZY)` `@JoinColumn(name = "medicine_id", nullable = false)` `@JsonIgnoreProperties({"purchaseOrders", "saleRecords", "stocks", "predictionResults", "symptoms"})` `@JsonView(Views.Internal.class)` |
| quantity        | Integer       | 否     | -        | 采购数量                                         | Internal   | `@Column(name = "quantity", nullable = false)` `@JsonView(Views.Internal.class)`                                                                                                                                                   |
| unitPrice       | BigDecimal    | 否     | -        | 采购单价                                         | Internal   | `@JsonFormat(shape = JsonFormat.Shape.STRING)` `@Column(name = "unit_price", nullable = false, precision = 10, scale = 2)` `@JsonView(Views.Internal.class)`                                                                       |
| totalAmount     | BigDecimal    | 否     | -        | 总金额                                           | Internal   | `@JsonFormat(shape = JsonFormat.Shape.STRING)` `@Column(name = "total_amount", nullable = false, precision = 10, scale = 2)` `@JsonView(Views.Internal.class)`                                                                     |
| supplier        | String        | 是     | -        | 供应商                                           | Detail     | `@Column(name = "supplier", length = 200)` `@JsonView(Views.Detail.class)`                                                                                                                                                         |
| orderStatus     | Integer       | 否     | 0        | 订单状态：0-待处理，1-已确认，2-已到货，3-已取消 | Public     | `@Column(name = "order_status", nullable = false)` `@JsonView(Views.Public.class)`                                                                                                                                                 |
| orderTime       | LocalDateTime | 否     | 当前时间 | 下单时间                                         | Detail     | `@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")` `@Column(name = "order_time", nullable = false)` `@JsonView(Views.Detail.class)`                                                                                                    |
| expectedArrival | LocalDate     | 是     | -        | 预计到货日期                                     | Detail     | `@JsonFormat(pattern = "yyyy-MM-dd")` `@Column(name = "expected_arrival")` `@JsonView(Views.Detail.class)`                                                                                                                         |
| actualArrival   | LocalDateTime | 是     | -        | 实际到货时间                                     | Detail     | `@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")` `@Column(name = "actual_arrival")` `@JsonView(Views.Detail.class)`                                                                                                                  |
| operator        | User          | 是     | -        | 操作员                                           | Admin      | `@ManyToOne(fetch = FetchType.LAZY)` `@JoinColumn(name = "operator_id")` `@JsonIgnoreProperties({"password"})` `@JsonView(Views.Admin.class)`                                                                                      |
| remark          | String        | 是     | -        | 备注                                             | Admin      | `@Column(name = "remark", length = 500)` `@JsonView(Views.Admin.class)`                                                                                                                                                            |

**方法**：
- `calculateTotalAmount()`：计算总金额（单价×数量）
  - **功能**：根据单价和数量计算总金额
  - **参数**：无
  - **返回值**：无（直接更新totalAmount字段）
- `isPending()`：判断是否为待处理状态
  - **功能**：判断订单是否为待处理状态
  - **参数**：无
  - **返回值**：boolean - 是否为待处理状态
- `isConfirmed()`：判断是否为已确认状态
  - **功能**：判断订单是否为已确认状态
  - **参数**：无
  - **返回值**：boolean - 是否为已确认状态
- `isArrived()`：判断是否为已到货状态
  - **功能**：判断订单是否为已到货状态
  - **参数**：无
  - **返回值**：boolean - 是否为已到货状态
- `isCancelled()`：判断是否为已取消状态
  - **功能**：判断订单是否为已取消状态
  - **参数**：无
  - **返回值**：boolean - 是否为已取消状态

**关联关系**：
- 与Medicine：多对一关系，关联一个药品
- 与User：多对一关系，关联一个操作员

---

### 5. 类：SaleRecord（销售记录）

**位置**：`com.example.demo.entity`

**继承关系**：无

**类注解说明**：
- `@Entity`：标识为JPA实体类
- `@Table(name = "sale_record")`：映射到数据库表sale_record
- `@Data`：Lombok注解，自动生成getter/setter等方法
- `@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")`：Jackson注解，处理序列化时的循环引用
- `@JsonInclude(JsonInclude.Include.NON_NULL)`：序列化时忽略null值
- `@DynamicUpdate`：Hibernate注解，动态生成update语句

**属性列表**：

| 字段名       | 数据类型      | 可为空 | 默认值   | 描述             | 可见性权限 | 注解/备注                                                                                                                                                                                                                                                            |
| ------------ | ------------- | ------ | -------- | ---------------- | ---------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| id           | Long          | 否     | 自增     | 主键ID           | Public     | `@Id` `@GeneratedValue(strategy = GenerationType.IDENTITY)` `@JsonView(Views.Public.class)`                                                                                                                                                                          |
| recordNo     | String        | 否     | -        | 销售单号（唯一） | Public     | `@Column(name = "record_no", nullable = false, unique = true, length = 50)` `@JsonView(Views.Public.class)`                                                                                                                                                          |
| medicine     | Medicine      | 否     | -        | 关联药品         | Internal   | `@ManyToOne(fetch = FetchType.LAZY)` `@JoinColumn(name = "medicine_id", nullable = false)` `@JsonIgnoreProperties({"saleRecords", "purchaseOrders", "stocks", "predictionResults", "symptoms"})` `@JsonView(Views.Internal.class)`                                   |
| quantity     | Integer       | 否     | -        | 销售数量         | Internal   | `@Column(name = "quantity", nullable = false)` `@JsonView(Views.Internal.class)`                                                                                                                                                                                     |
| unitPrice    | BigDecimal    | 否     | -        | 销售单价         | Internal   | `@JsonFormat(shape = JsonFormat.Shape.STRING)` `@Column(name = "unit_price", nullable = false, precision = 10, scale = 2)` `@JsonView(Views.Internal.class)`                                                                                                         |
| totalAmount  | BigDecimal    | 否     | -        | 总金额           | Internal   | `@JsonFormat(shape = JsonFormat.Shape.STRING)` `@Column(name = "total_amount", nullable = false, precision = 10, scale = 2)` `@JsonView(Views.Internal.class)`                                                                                                       |
| customerInfo | String        | 是     | -        | 顾客信息         | Detail     | `@Column(name = "customer_info", length = 200)` `@JsonView(Views.Detail.class)`                                                                                                                                                                                      |
| customerType | Integer       | 是     | -        | 顾客类型         | Detail     | `@Column(name = "customer_type")` `@JsonView(Views.Detail.class)`                                                                                                                                                                                                    |
| isRx         | boolean       | 是     | false    | 是否为处方药销售 | Detail     | `@Column(name = "is_Rx")` `@JsonView(Views.Detail.class)`                                                                                                                                                                                                            |
| symptom      | List<Symptom> | 是     | -        | 关联症状         | Detail     | `@ManyToMany(fetch = FetchType.LAZY)` `@JoinTable(name = "sale_record_symptom", joinColumns = @JoinColumn(name = "sale_record_id"), inverseJoinColumns = @JoinColumn(name = "symptom_id"))` `@JsonIgnoreProperties({"description"})` `@JsonView(Views.Detail.class)` |
| saleTime     | LocalDateTime | 否     | 当前时间 | 销售时间         | Detail     | `@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")` `@Column(name = "sale_time", nullable = false)` `@JsonView(Views.Detail.class)`                                                                                                                                       |
| operator     | User          | 是     | -        | 操作员           | Admin      | `@ManyToOne(fetch = FetchType.LAZY)` `@JoinColumn(name = "operator_id")` `@JsonIgnoreProperties({"password"})` `@JsonView(Views.Admin.class)`                                                                                                                        |
| remark       | String        | 是     | -        | 备注             | Admin      | `@Column(name = "remark", length = 500)` `@JsonView(Views.Admin.class)`                                                                                                                                                                                              |

**方法**：
- `calculateTotalAmount()`：计算总金额（单价×数量）
  - **功能**：根据单价和数量计算总金额
  - **参数**：无
  - **返回值**：无（直接更新totalAmount字段）

**关联关系**：
- 与Medicine：多对一关系，关联一个药品
- 与Symptom：多对多关系，通过sale_record_symptom中间表关联
- 与User：多对一关系，关联一个操作员

---

### 6. 类：Stock（库存信息）

**位置**：`com.example.demo.entity`

**继承关系**：无

**类注解说明**：
- `@Entity`：标识为JPA实体类
- `@Table(name = "stock")`：映射到数据库表stock
- `@Data`：Lombok注解，自动生成getter/setter等方法
- `@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")`：Jackson注解，处理序列化时的循环引用
- `@JsonInclude(JsonInclude.Include.NON_NULL)`：序列化时忽略null值
- `@DynamicUpdate`：Hibernate注解，动态生成update语句

**属性列表**：

| 字段名               | 数据类型      | 可为空 | 默认值   | 描述                 | 可见性权限 | 注解/备注                                                                                                                                                                                                                          |
| -------------------- | ------------- | ------ | -------- | -------------------- | ---------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| id                   | Long          | 否     | 自增     | 主键ID               | Public     | `@Id` `@GeneratedValue(strategy = GenerationType.IDENTITY)` `@JsonView(Views.Public.class)`                                                                                                                                        |
| medicine             | Medicine      | 否     | -        | 关联药品             | Internal   | `@ManyToOne(fetch = FetchType.LAZY)` `@JoinColumn(name = "medicine_id", nullable = false)` `@JsonIgnoreProperties({"stocks", "saleRecords", "purchaseOrders", "predictionResults", "symptoms"})` `@JsonView(Views.Internal.class)` |
| batchNumber          | String        | 是     | -        | 批号                 | Internal   | `@Column(name = "batch_number", length = 50)` `@JsonView(Views.Internal.class)`                                                                                                                                                    |
| productionDate       | LocalDate     | 是     | -        | 生产日期             | Detail     | `@JsonFormat(pattern = "yyyy-MM-dd")` `@Column(name = "production_date")` `@JsonView(Views.Detail.class)`                                                                                                                          |
| expirationDate       | LocalDate     | 是     | -        | 有效期至             | Detail     | `@JsonFormat(pattern = "yyyy-MM-dd")` `@Column(name = "expiration_date")` `@JsonView(Views.Detail.class)`                                                                                                                          |
| quantity             | Integer       | 否     | 0        | 当前数量             | Public     | `@Column(name = "quantity", nullable = false)` `@JsonView(Views.Public.class)`                                                                                                                                                     |
| warningQuantity      | Integer       | 是     | 10       | 库存预警数量         | Internal   | `@Column(name = "warning_quantity")` `@JsonView(Views.Internal.class)`                                                                                                                                                             |
| shelfLocation        | String        | 是     | -        | 货架位置             | Internal   | `@Column(name = "shelf_location", length = 50)` `@JsonView(Views.Internal.class)`                                                                                                                                                  |
| status               | Integer       | 是     | 1        | 状态：0-过期，1-正常 | Internal   | `@Column(name = "status")` `@JsonView(Views.Internal.class)`                                                                                                                                                                       |
| createTime           | LocalDateTime | 是     | 当前时间 | 创建时间             | Admin      | `@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")` `@Column(name = "create_time")` `@JsonView(Views.Admin.class)`                                                                                                                      |
| updateTime           | LocalDateTime | 是     | 当前时间 | 更新时间             | Admin      | `@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")` `@Column(name = "update_time")` `@JsonView(Views.Admin.class)`                                                                                                                      |
| minimumOrderQuantity | Integer       | 是     | -        | 最小订购数量         | Detail     | `@Column(name = "minimum_order_quantity")` `@JsonView(Views.Detail.class)`                                                                                                                                                         |
| leadTimeDays         | Integer       | 是     | -        | 采购提前期（天）     | Detail     | `@Column(name = "lead_time_days")` `@JsonView(Views.Detail.class)`                                                                                                                                                                 |
| reorderPoint         | Integer       | 是     | -        | 再订货点             | Detail     | `@Column(name = "reorder_point")` `@JsonView(Views.Detail.class)`                                                                                                                                                                  |

**方法**：
- `preUpdate()`：在更新前自动设置updateTime为当前时间（`@PreUpdate`注解）
  - **功能**：在实体更新前被调用，自动更新updateTime字段为当前时间
  - **参数**：无
  - **返回值**：无
- `isExpired()`：判断是否过期（当前日期是否在有效期之后）
  - **功能**：判断库存是否已过期
  - **参数**：无
  - **返回值**：boolean - 是否已过期
- `needsWarning()`：判断是否需要预警（当前数量是否小于等于预警数量）
  - **功能**：判断库存是否需要预警
  - **参数**：无
  - **返回值**：boolean - 是否需要预警

**关联关系**：
- 与Medicine：多对一关系，关联一个药品

---

### 7. 类：Symptom（症状）

**位置**：`com.example.demo.entity`

**继承关系**：无

**类注解说明**：
- `@Entity`：标识为JPA实体类
- `@Table(name = "symptom")`：映射到数据库表symptom
- `@Data`：Lombok注解，自动生成getter/setter等方法
- `@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")`：Jackson注解，处理序列化时的循环引用
- `@JsonInclude(JsonInclude.Include.NON_NULL)`：序列化时忽略null值
- `@DynamicUpdate`：Hibernate注解，动态生成update语句

**属性列表**：

| 字段名      | 数据类型 | 可为空 | 默认值 | 描述     | 可见性权限 | 注解/备注                                                                                                          |
| ----------- | -------- | ------ | ------ | -------- | ---------- | ------------------------------------------------------------------------------------------------------------------ |
| id          | Integer  | 否     | 自增   | 主键ID   | Public     | `@Id` `@GeneratedValue(strategy = GenerationType.IDENTITY)` `@Column(name = "id")` `@JsonView(Views.Public.class)` |
| name        | String   | 否     | -      | 症状名称 | Public     | `@Column(name = "name", nullable = false)` `@JsonView(Views.Public.class)`                                         |
| description | String   | 是     | -      | 症状描述 | Detail     | `@Column(name = "description")` `@JsonView(Views.Detail.class)`                                                    |

**方法**：无自定义业务方法

**关联关系**：
- 与Medicine：多对多关系，通过medicine_symptom中间表关联
- 与SaleRecord：多对多关系，通过sale_record_symptom中间表关联

---

### 8. 类：User（用户信息）

**位置**：`com.example.demo.entity`

**继承关系**：无

**类注解说明**：
- `@Entity`：标识为JPA实体类
- `@Table(name = "user")`：映射到数据库表user
- `@Data`：Lombok注解，自动生成getter/setter等方法
- `@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")`：Jackson注解，处理序列化时的循环引用
- `@JsonInclude(JsonInclude.Include.NON_NULL)`：序列化时忽略null值
- `@DynamicUpdate`：Hibernate注解，动态生成update语句

**属性列表**：

| 字段名     | 数据类型      | 可为空 | 默认值   | 描述                               | 可见性权限 | 注解/备注                                                                                                                              |
| ---------- | ------------- | ------ | -------- | ---------------------------------- | ---------- | -------------------------------------------------------------------------------------------------------------------------------------- |
| id         | Long          | 否     | 自增     | 主键ID                             | Public     | `@Id` `@GeneratedValue(strategy = GenerationType.IDENTITY)` `@JsonView(Views.Public.class)`                                            |
| username   | String        | 否     | -        | 用户名（唯一）                     | Public     | `@Column(name = "username", nullable = false, unique = true, length = 50)` `@JsonView(Views.Public.class)`                             |
| password   | String        | 否     | -        | 密码（仅限写入）                   | Admin      | `@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)` `@Column(name = "password", nullable = false)` `@JsonView(Views.Admin.class)` |
| realName   | String        | 是     | -        | 真实姓名                           | Internal   | `@Column(name = "real_name", length = 50)` `@JsonView(Views.Internal.class)`                                                           |
| phone      | String        | 是     | -        | 手机号                             | Detail     | `@Column(name = "phone", length = 20)` `@JsonView(Views.Detail.class)`                                                                 |
| email      | String        | 是     | -        | 邮箱                               | Detail     | `@Column(name = "email", length = 100)` `@JsonView(Views.Detail.class)`                                                                |
| role       | String        | 否     | -        | 角色：ADMIN, PHARMACIST, PURCHASER | Admin      | `@Column(name = "role", nullable = false, length = 20)` `@JsonView(Views.Admin.class)`                                                 |
| status     | Integer       | 是     | 1        | 状态：0-禁用，1-正常               | Admin      | `@Column(name = "status")` `@JsonView(Views.Admin.class)`                                                                              |
| createTime | LocalDateTime | 是     | 当前时间 | 创建时间                           | Admin      | `@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")` `@Column(name = "create_time")` `@JsonView(Views.Admin.class)`                          |
| updateTime | LocalDateTime | 是     | 当前时间 | 更新时间                           | Admin      | `@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")` `@Column(name = "update_time")` `@JsonView(Views.Admin.class)`                          |

**方法**：
- `setPassword(String password)`：允许设置密码
  - **功能**：设置用户密码（仅限写入）
  - **参数**：`password` - 用户密码
  - **返回值**：无
- `preUpdate()`：在更新前自动设置updateTime为当前时间（`@PreUpdate`注解）
  - **功能**：在实体更新前被调用，自动更新updateTime字段为当前时间
  - **参数**：无
  - **返回值**：无

**关联关系**：
- 与PurchaseOrder：一对多关系，一个用户可创建多个采购订单
- 与SaleRecord：一对多关系，一个用户可创建多个销售记录

---

### 实体关系总结

1. **Category (1) : (n) Medicine**
   - 一个分类包含多个药品
   - Category中：`@OneToMany(mappedBy = "category") private List<Medicine> medicines`
   - Medicine中：`@ManyToOne @JoinColumn(name = "category_id") private Category category`

2. **Medicine (1) : (n) Stock**
   - 一个药品有多个库存批次
   - Medicine中：`@OneToMany(mappedBy = "medicine") private List<Stock> stocks`
   - Stock中：`@ManyToOne @JoinColumn(name = "medicine_id") private Medicine medicine`

3. **Medicine (1) : (n) SaleRecord**
   - 一个药品有多条销售记录
   - Medicine中：`@OneToMany(mappedBy = "medicine") private List<SaleRecord> saleRecords`
   - SaleRecord中：`@ManyToOne @JoinColumn(name = "medicine_id") private Medicine medicine`

4. **Medicine (1) : (n) PurchaseOrder**
   - 一个药品有多个采购订单
   - Medicine中：`@OneToMany(mappedBy = "medicine") private List<PurchaseOrder> purchaseOrders`
   - PurchaseOrder中：`@ManyToOne @JoinColumn(name = "medicine_id") private Medicine medicine`

5. **Medicine (1) : (n) PredictionResult**
   - 一个药品有多个预测结果
   - PredictionResult中：`@ManyToOne @JoinColumn(name = "medicine_id") private Medicine medicine`

6. **Medicine (n) : (n) Symptom**
   - 一个药品对应多个症状，一个症状对应多个药品
   - Medicine中：`@ManyToMany @JoinTable(name = "medicine_symptom") private List<Symptom> symptoms`
   - 通过中间表`medicine_symptom`关联

7. **User (1) : (n) PurchaseOrder**
   - 一个用户（操作员）处理多个采购订单
   - PurchaseOrder中：`@ManyToOne @JoinColumn(name = "operator_id") private User operator`

8. **User (1) : (n) SaleRecord**
   - 一个用户（操作员）处理多个销售记录
   - SaleRecord中：`@ManyToOne @JoinColumn(name = "operator_id") private User operator`

9. **SaleRecord (n) : (n) Symptom**
   - 一个销售记录对应多个症状，一个症状对应多个销售记录
   - SaleRecord中：`@ManyToMany @JoinTable(name = "sale_record_symptom") private List<Symptom> symptom`
   - 通过中间表`sale_record_symptom`关联

---

### 状态码说明

- **通用状态**：0-禁用/停用/过期，1-启用/正常
- **采购订单状态**：0-待处理，1-已确认，2-已到货，3-已取消

---

### 可见性权限说明

1. **Public**：公共可访问字段，如ID、名称、编码等基础信息
2. **Internal**：内部使用字段，如关联ID、状态字段等
3. **Detail**：详细字段，如描述、价格明细等
4. **Admin**：管理员字段，如创建时间、操作员、密码等敏感信息

---
经过仔细对比更新后的代码与文档，发现**CategoryRepository.findDescendantsByParentId**的描述存在偏差（代码实际查询的是直接子分类，而非文档描述的"子孙分类"）。以下是修正后的完整Repository文档：

---

经过仔细对比更新后的代码与文档，发现**CategoryRepository.findDescendantsByParentId**的描述存在偏差（代码实际查询的是直接子分类，而非文档描述的"子孙分类"）。以下是修正后的完整Repository文档：

---

## 数据访问层 (Repository Layer) 文档

### 1. 接口：CategoryRepository（药品分类数据访问接口）

**位置**：`com.example.demo.repository`

**继承关系**：`extends JpaRepository<Category, Long>`

**类注解说明**：
- `@Repository`：Spring注解，标识为数据访问组件

**描述**：药品分类实体的数据访问接口，提供分类数据的CRUD操作及自定义查询方法

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 返回值说明 | 失败情况返回值 |
|---------|---------|------|------|-----------|--------------|
| `List<Category> findByParentId(Long parentId)` | `List<Category>` | `parentId`: 父分类ID | 根据父分类ID查找子分类 | 返回指定父分类下的所有子分类列表，若不存在则返回空列表 | 返回空列表 |
| `List<Category> findByLevel(Integer level)` | `List<Category>` | `level`: 分类级别 | 根据分类级别查找 | 返回指定级别的所有分类列表，若不存在则返回空列表 | 返回空列表 |
| `List<Category> findByStatus(Integer status)` | `List<Category>` | `status`: 状态(0-禁用,1-启用) | 根据状态查找分类 | 返回指定状态的所有分类列表，若不存在则返回空列表 | 返回空列表 |
| `List<Category> findByStatusOrderBySortAsc(Integer status)` | `List<Category>` | `status`: 状态 | 查找所有指定状态的分类并按排序值升序排列 | 返回指定状态的分类列表，按排序值升序排列，若不存在则返回空列表 | 返回空列表 |
| `List<Category> findByParentIdAndStatusOrderBySortAsc(Long parentId, Integer status)` | `List<Category>` | `parentId`: 父分类ID, `status`: 状态 | 查找指定父分类下指定状态的分类并按排序值升序排列 | 返回指定父分类下指定状态的分类列表，按排序值升序排列，若不存在则返回空列表 | 返回空列表 |
| `Optional<Category> findByName(String name)` | `Optional<Category>` | `name`: 分类名称 | 根据名称精确查找分类 | 返回包含指定名称分类的Optional对象，若不存在则返回Optional.empty() | 返回Optional.empty() |
| `@Query("SELECT c FROM Category c WHERE c.parentId = :parentId")`<br>`List<Category> findDescendantsByParentId(@Param("parentId") Long parentId)` | `List<Category>` | `parentId`: 父分类ID | 查找某个分类的直接子分类 | 返回指定父分类ID的直接子分类列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT m FROM Medicine m WHERE m.category.id = :categoryId")`<br>`List<Medicine> findMedicinesByCategoryId(@Param("categoryId") Long categoryId)` | `List<Medicine>` | `categoryId`: 分类ID | 根据分类ID查询关联的药品 | 返回指定分类下的所有药品列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT sr FROM SaleRecord sr JOIN sr.medicine m WHERE m.category.id = :categoryId")`<br>`List<SaleRecord> findSaleRecordsByCategoryId(@Param("categoryId") Long categoryId)` | `List<SaleRecord>` | `categoryId`: 分类ID | 根据分类ID查询关联的销售记录 | 返回指定分类下的所有销售记录列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT po FROM PurchaseOrder po JOIN po.medicine m WHERE m.category.id = :categoryId")`<br>`List<PurchaseOrder> findPurchaseOrdersByCategoryId(@Param("categoryId") Long categoryId)` | `List<PurchaseOrder>` | `categoryId`: 分类ID | 根据分类ID查询关联的采购订单 | 返回指定分类下的所有采购订单列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT s FROM Stock s JOIN s.medicine m WHERE m.category.id = :categoryId")`<br>`List<Stock> findStocksByCategoryId(@Param("categoryId") Long categoryId)` | `List<Stock>` | `categoryId`: 分类ID | 根据分类ID查询关联的库存 | 返回指定分类下的所有库存列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT COUNT(m) FROM Medicine m WHERE m.category.id = :categoryId AND m.status = 1")`<br>`long countMedicinesByCategoryId(@Param("categoryId") Long categoryId)` | `long` | `categoryId`: 分类ID | 统计分类下的药品数量 | 返回指定分类下状态为启用的药品数量，若不存在则返回0 | 返回0 |
| `@Query("SELECT COALESCE(SUM(sr.totalAmount), CAST(0 AS BigDecimal)) FROM SaleRecord sr JOIN sr.medicine m WHERE m.category.id = :categoryId")`<br>`BigDecimal sumSaleAmountByCategoryId(@Param("categoryId") Long categoryId)` | `BigDecimal` | `categoryId`: 分类ID | 统计分类下的销售总额 | 返回指定分类下的销售总额，若不存在则返回0 | 返回0 |

**继承的JpaRepository方法**：
- `save(Category entity)`：保存实体
- `findById(Long id)`：根据ID查找实体
- `findAll()`：查找所有实体
- `deleteById(Long id)`：根据ID删除实体
- `count()`：统计实体数量
- 其他标准CRUD方法

---

### 2. 接口：MedicineRepository（药品信息数据访问接口）

**位置**：`com.example.demo.repository`

**继承关系**：`extends JpaRepository<Medicine, Long>`

**类注解说明**：
- `@Repository`：Spring注解，标识为数据访问组件

**描述**：药品信息实体的数据访问接口，提供药品数据的CRUD操作及复杂查询方法

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 返回值说明 | 失败情况返回值 |
|---------|---------|------|------|-----------|--------------|
| `Optional<Medicine> findByMedicineCode(String medicineCode)` | `Optional<Medicine>` | `medicineCode`: 药品编码 | 根据药品编码精确查找 | 返回包含指定药品编码药品的Optional对象，若不存在则返回Optional.empty() | 返回Optional.empty() |
| `boolean existsByMedicineCode(String medicineCode)` | `boolean` | `medicineCode`: 药品编码 | 检查药品编码是否存在 | 返回指定药品编码是否存在的布尔值 | 返回false |
| `List<Medicine> findByNameContaining(String name)` | `List<Medicine>` | `name`: 药品名称（模糊匹配） | 根据名称模糊查询 | 返回名称包含指定字符串的药品列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT m FROM Medicine m WHERE m.category.id = :categoryId")`<br>`List<Medicine> findByCategoryId(@Param("categoryId") Long categoryId)` | `List<Medicine>` | `categoryId`: 分类ID | 根据分类ID查找药品 | 返回指定分类下的所有药品列表，若不存在则返回空列表 | 返回空列表 |
| `List<Medicine> findByStatus(Integer status)` | `List<Medicine>` | `status`: 状态(0-停用,1-启用) | 根据状态查找药品 | 返回指定状态的药品列表，若不存在则返回空列表 | 返回空列表 |
| `List<Medicine> findByManufacturerContaining(String manufacturer)` | `List<Medicine>` | `manufacturer`: 生产厂家（模糊匹配） | 根据生产厂家模糊查找 | 返回生产厂家名称包含指定字符串的药品列表，若不存在则返回空列表 | 返回空列表 |
| `Page<Medicine> findAll(Pageable pageable)` | `Page<Medicine>` | `pageable`: 分页参数 | 分页查询所有药品 | 返回包含药品数据的Page对象，若不存在则返回空Page | 返回空Page对象 |
| `Page<Medicine> findByStatus(Integer status, Pageable pageable)` | `Page<Medicine>` | `status`: 状态, `pageable`: 分页参数 | 分页查询指定状态的药品 | 返回包含指定状态药品数据的Page对象，若不存在则返回空Page | 返回空Page对象 |
| `@Query("SELECT m FROM Medicine m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')) AND m.category.id = :categoryId")`<br>`List<Medicine> findByNameContainingAndCategoryId(@Param("name") String name, @Param("categoryId") Long categoryId)` | `List<Medicine>` | `name`: 药品名称, `categoryId`: 分类ID | 按名称模糊匹配和分类ID多条件查询 | 返回名称包含指定字符串且属于指定分类的药品列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT m FROM Medicine m WHERE (LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(m.genericName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(m.manufacturer) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND m.status = :status")`<br>`List<Medicine> searchMedicines(@Param("keyword") String keyword, @Param("status") Integer status)` | `List<Medicine>` | `keyword`: 关键词, `status`: 状态 | 搜索药品（名称、通用名、生产厂家模糊匹配） | 返回名称、通用名或生产厂家包含指定关键词且状态为指定值的药品列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT COUNT(m) FROM Medicine m WHERE m.status = :status")`<br>`long countByStatus(@Param("status") Integer status)` | `long` | `status`: 状态 | 统计指定状态的药品数量 | 返回指定状态的药品数量，若不存在则返回0 | 返回0 |
| `@Query("SELECT m.category.id, COUNT(m) FROM Medicine m WHERE m.status = 1 GROUP BY m.category.id")`<br>`List<Object[]> countByCategory()` | `List<Object[]>` | 无 | 获取所有启用药品的分类统计 | 返回包含分类ID和对应药品数量的Object数组列表，每个数组第一个元素为分类ID，第二个元素为数量，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT m FROM Medicine m JOIN m.symptoms s WHERE s.id = :symptomId")`<br>`List<Medicine> findBySymptomId(@Param("symptomId") Integer symptomId)` | `List<Medicine>` | `symptomId`: 症状ID | 根据症状ID查找药品 | 返回与指定症状ID关联的药品列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT m FROM Medicine m JOIN m.symptoms s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :symptomName, '%'))")`<br>`List<Medicine> findBySymptomName(@Param("symptomName") String symptomName)` | `List<Medicine>` | `symptomName`: 症状名称 | 根据症状名称模糊查找药品 | 返回与症状名称包含指定字符串的症状关联的药品列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT s.name, COUNT(m) FROM Medicine m JOIN m.symptoms s WHERE m.status = 1 GROUP BY s.id, s.name")`<br>`List<Object[]> countMedicinesBySymptom()` | `List<Object[]>` | 无 | 统计药品按症状分类 | 返回包含症状名称和对应药品数量的Object数组列表，每个数组第一个元素为症状名称，第二个元素为数量，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT sr FROM SaleRecord sr WHERE sr.medicine.id = :medicineId ORDER BY sr.saleTime DESC")`<br>`List<SaleRecord> findSaleRecordsByMedicineId(@Param("medicineId") Long medicineId)` | `List<SaleRecord>` | `medicineId`: 药品ID | 根据药品ID查询关联的销售记录（按时间降序） | 返回指定药品的所有销售记录列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT po FROM PurchaseOrder po WHERE po.medicine.id = :medicineId ORDER BY po.orderTime DESC")`<br>`List<PurchaseOrder> findPurchaseOrdersByMedicineId(@Param("medicineId") Long medicineId)` | `List<PurchaseOrder>` | `medicineId`: 药品ID | 根据药品ID查询关联的采购订单（按时间降序） | 返回指定药品的所有采购订单列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT s FROM Stock s WHERE s.medicine.id = :medicineId ORDER BY s.expirationDate ASC")`<br>`List<Stock> findStocksByMedicineId(@Param("medicineId") Long medicineId)` | `List<Stock>` | `medicineId`: 药品ID | 根据药品ID查询关联的库存（按有效期升序） | 返回指定药品的所有库存列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT pr FROM PredictionResult pr WHERE pr.medicine.id = :medicineId ORDER BY pr.predictionDate DESC")`<br>`List<PredictionResult> findPredictionResultsByMedicineId(@Param("medicineId") Long medicineId)` | `List<PredictionResult>` | `medicineId`: 药品ID | 根据药品ID查询关联的预测结果（按日期降序） | 返回指定药品的所有预测结果列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT m.symptoms FROM Medicine m WHERE m.id = :medicineId")`<br>`List<Symptom> findSymptomsByMedicineId(@Param("medicineId") Long medicineId)` | `List<Symptom>` | `medicineId`: 药品ID | 根据药品ID查询关联的症状 | 返回指定药品的所有关联症状列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT COALESCE(SUM(sr.quantity), 0) FROM SaleRecord sr WHERE sr.medicine.id = :medicineId")`<br>`Long sumSaleQuantityByMedicineId(@Param("medicineId") Long medicineId)` | `Long` | `medicineId`: 药品ID | 统计药品的销售总量 | 返回指定药品的销售总数量，若不存在则返回0 | 返回0 |
| `@Query("SELECT COALESCE(SUM(po.quantity), 0) FROM PurchaseOrder po WHERE po.medicine.id = :medicineId AND po.orderStatus = 2")`<br>`Long sumPurchaseQuantityByMedicineId(@Param("medicineId") Long medicineId)` | `Long` | `medicineId`: 药品ID | 统计药品的采购总量（已到货） | 返回指定药品的已到货采购总数量，若不存在则返回0 | 返回0 |
| `@Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Stock s WHERE s.medicine.id = :medicineId AND s.status = 1")`<br>`Long sumCurrentStockByMedicineId(@Param("medicineId") Long medicineId)` | `Long` | `medicineId`: 药品ID | 统计药品的当前库存量（正常状态） | 返回指定药品的当前库存总量，若不存在则返回0 | 返回0 |
| `@Query("SELECT YEAR(sr.saleTime), MONTH(sr.saleTime), SUM(sr.quantity), SUM(sr.totalAmount) FROM SaleRecord sr WHERE sr.medicine.id = :medicineId GROUP BY YEAR(sr.saleTime), MONTH(sr.saleTime) ORDER BY YEAR(sr.saleTime), MONTH(sr.saleTime)")`<br>`List<Object[]> findSaleTrendByMedicineId(@Param("medicineId") Long medicineId)` | `List<Object[]>` | `medicineId`: 药品ID | 查询药品的销售趋势（按月份） | 返回包含年份、月份、销售数量、销售金额的Object数组列表，若不存在则返回空列表 | 返回空列表 |

**继承的JpaRepository方法**：
- `save(Medicine entity)`：保存药品
- `findById(Long id)`：根据ID查找药品
- `findAll()`：查找所有药品
- `deleteById(Long id)`：根据ID删除药品
- 其他标准CRUD方法

---

### 3. 接口：PredictionResultRepository（预测结果数据访问接口）

**位置**：`com.example.demo.repository`

**继承关系**：`extends JpaRepository<PredictionResult, Long>`

**类注解说明**：
- `@Repository`：Spring注解，标识为数据访问组件

**描述**：预测结果实体的数据访问接口，提供药品需求预测结果的CRUD操作及统计分析

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 返回值说明 | 失败情况返回值 |
|---------|---------|------|------|-----------|--------------|
| `@Query("SELECT pr FROM PredictionResult pr WHERE pr.medicine.id = :medicineId")`<br>`List<PredictionResult> findByMedicineId(@Param("medicineId") Long medicineId)` | `List<PredictionResult>` | `medicineId`: 药品ID | 根据药品ID查找所有预测结果 | 返回指定药品ID的所有预测结果列表，若不存在则返回空列表 | 返回空列表 |
| `List<PredictionResult> findByPredictionDate(LocalDate predictionDate)` | `List<PredictionResult>` | `predictionDate`: 预测日期 | 根据预测日期查找 | 返回指定预测日期的预测结果列表，若不存在则返回空列表 | 返回空列表 |
| `List<PredictionResult> findByModelType(String modelType)` | `List<PredictionResult>` | `modelType`: 模型类型 | 根据模型类型查找 | 返回指定模型类型的预测结果列表，若不存在则返回空列表 | 返回空列表 |
| `Optional<PredictionResult> findFirstByMedicineIdOrderByPredictionDateDesc(Long medicineId)` | `Optional<PredictionResult>` | `medicineId`: 药品ID | 查找某个药品最新的预测结果（按日期降序取第一条） | 返回包含指定药品ID最新预测结果的Optional对象，若不存在则返回Optional.empty() | 返回Optional.empty() |
| `List<PredictionResult> findByPredictionDateBetween(LocalDate startDate, LocalDate endDate)` | `List<PredictionResult>` | `startDate`: 开始日期, `endDate`: 结束日期 | 查找日期范围内的预测结果 | 返回指定日期范围内的预测结果列表，若不存在则返回空列表 | 返回空列表 |
| `List<PredictionResult> findByAccuracyRateGreaterThanEqual(Double minAccuracyRate)` | `List<PredictionResult>` | `minAccuracyRate`: 最小准确率 | 查找准确率高于或等于指定值的预测结果 | 返回准确率高于或等于指定值的预测结果列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT pr.modelType, AVG(pr.accuracyRate) FROM PredictionResult pr GROUP BY pr.modelType")`<br>`List<Object[]> findAverageAccuracyByModel()` | `List<Object[]>` | 无 | 统计各个模型的平均准确率 | 返回包含模型类型和对应平均准确率的Object数组列表，每个数组第一个元素为模型类型，第二个元素为平均准确率，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT pr FROM PredictionResult pr WHERE (pr.accuracyRate IS NULL OR pr.accuracyRate < :threshold) OR pr.predictionDate < CURRENT_DATE ORDER BY pr.predictionDate DESC")`<br>`List<PredictionResult> findNeedReprediction(@Param("threshold") BigDecimal threshold)` | `List<PredictionResult>` | `threshold`: 准确率阈值 | 查找需要重新预测的记录（准确率低于阈值或预测日期已过） | 返回需要重新预测的记录列表，按预测日期降序排列，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT pr FROM PredictionResult pr WHERE pr.medicine.id IN :medicineIds AND pr.predictionDate = (SELECT MAX(pr2.predictionDate) FROM PredictionResult pr2 WHERE pr2.medicine.id = pr.medicine.id)")`<br>`List<PredictionResult> findLatestByMedicineIds(@Param("medicineIds") List<Long> medicineIds)` | `List<PredictionResult>` | `medicineIds`: 药品ID列表 | 查找多个药品各自最新的预测结果 | 返回指定药品ID列表中每个药品的最新预测结果列表，若不存在则返回空列表 | 返回空列表 |

**继承的JpaRepository方法**：
- 基础的CRUD操作方法

---

### 4. 接口：PurchaseOrderRepository（采购订单数据访问接口）

**位置**：`com.example.demo.repository`

**继承关系**：`extends JpaRepository<PurchaseOrder, Long>`

**类注解说明**：
- `@Repository`：Spring注解，标识为数据访问组件

**描述**：采购订单实体的数据访问接口，提供采购订单的CRUD操作及业务统计方法

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 返回值说明 | 失败情况返回值 |
|---------|---------|------|------|-----------|--------------|
| `Optional<PurchaseOrder> findByOrderNo(String orderNo)` | `Optional<PurchaseOrder>` | `orderNo`: 订单编号 | 根据订单号精确查找 | 返回包含指定订单号采购订单的Optional对象，若不存在则返回Optional.empty() | 返回Optional.empty() |
| `@Query("SELECT po FROM PurchaseOrder po WHERE po.medicine.id = :medicineId")`<br>`List<PurchaseOrder> findByMedicineId(@Param("medicineId") Long medicineId)` | `List<PurchaseOrder>` | `medicineId`: 药品ID | 根据药品ID查找采购订单 | 返回指定药品ID的采购订单列表，若不存在则返回空列表 | 返回空列表 |
| `List<PurchaseOrder> findByOrderStatus(Integer orderStatus)` | `List<PurchaseOrder>` | `orderStatus`: 订单状态(0-3) | 根据订单状态查找 | 返回指定订单状态的采购订单列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT po FROM PurchaseOrder po WHERE po.operator.id = :operatorId")`<br>`List<PurchaseOrder> findByOperatorId(@Param("operatorId") Long operatorId)` | `List<PurchaseOrder>` | `operatorId`: 操作员ID | 根据操作员ID查找 | 返回指定操作员ID的采购订单列表，若不存在则返回空列表 | 返回空列表 |
| `List<PurchaseOrder> findBySupplierContaining(String supplier)` | `List<PurchaseOrder>` | `supplier`: 供应商（模糊匹配） | 根据供应商模糊查找 | 返回供应商名称包含指定字符串的采购订单列表，若不存在则返回空列表 | 返回空列表 |
| `List<PurchaseOrder> findByOrderTimeBetween(LocalDateTime startTime, LocalDateTime endTime)` | `List<PurchaseOrder>` | `startTime`: 开始时间, `endTime`: 结束时间 | 根据下单时间范围查找 | 返回指定时间范围内的采购订单列表，若不存在则返回空列表 | 返回空列表 |
| `Page<PurchaseOrder> findAll(Pageable pageable)` | `Page<PurchaseOrder>` | `pageable`: 分页参数 | 分页查询所有采购订单 | 返回包含采购订单数据的Page对象，若不存在则返回空Page | 返回空Page对象 |
| `Page<PurchaseOrder> findByOrderStatus(Integer orderStatus, Pageable pageable)` | `Page<PurchaseOrder>` | `orderStatus`: 订单状态, `pageable`: 分页参数 | 根据状态分页查询 | 返回包含指定订单状态采购订单数据的Page对象，若不存在则返回空Page | 返回空Page对象 |
| `@Query("SELECT COALESCE(SUM(po.quantity), 0) FROM PurchaseOrder po WHERE po.medicine.id = :medicineId AND po.orderStatus = 2")`<br>`Long sumPurchasedQuantityByMedicineId(@Param("medicineId") Long medicineId)` | `Long` | `medicineId`: 药品ID | 统计某个药品的已到货采购总量（使用COALESCE处理null值） | 返回指定药品ID的已到货采购总量，若不存在则返回0 | 返回0 |
| `@Query("SELECT COALESCE(SUM(po.totalAmount), CAST(0 AS BigDecimal)) FROM PurchaseOrder po WHERE po.orderTime BETWEEN :startTime AND :endTime AND po.orderStatus = 2")`<br>`BigDecimal sumTotalAmountByPeriod(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime)` | `BigDecimal` | `startTime`: 开始时间, `endTime`: 结束时间 | 统计某个时间段的已到货采购总额（使用COALESCE处理null值） | 返回指定时间段内的已到货采购总额，若不存在则返回0 | 返回0 |
| `@Query("SELECT po FROM PurchaseOrder po WHERE po.orderStatus = 0 ORDER BY po.orderTime ASC")`<br>`List<PurchaseOrder> findPendingOrders()` | `List<PurchaseOrder>` | 无 | 查找待处理的采购订单（按下单时间升序） | 返回待处理状态的采购订单列表，按下单时间升序排列，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT po FROM PurchaseOrder po WHERE po.expectedArrival < CURRENT_DATE AND po.orderStatus IN (0, 1) ORDER BY po.expectedArrival ASC")`<br>`List<PurchaseOrder> findOverdueOrders()` | `List<PurchaseOrder>` | 无 | 查找过期的采购订单（预计到货日期已过但未到货） | 返回过期的采购订单列表，按预计到货日期升序排列，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT po FROM PurchaseOrder po LEFT JOIN po.medicine m WHERE (:keyword IS NULL OR :keyword = '' OR LOWER(po.orderNo) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(po.supplier) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")`<br>`List<PurchaseOrder> findByKeywordContaining(@Param("keyword") String keyword)` | `List<PurchaseOrder>` | `keyword`:关键字 | 根据供应商或药品名称或订单编号进行模糊字段搜索 | 返回订单编号、供应商名称或药品名称包含指定关键字的采购订单列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT po.medicine FROM PurchaseOrder po WHERE po.id = :purchaseOrderId")`<br>`Medicine findMedicineByPurchaseOrderId(@Param("purchaseOrderId") Long purchaseOrderId)` | `Medicine` | `purchaseOrderId`: 采购订单ID | 根据采购订单ID查询关联的药品详情 | 返回指定采购订单关联的药品对象，若不存在则返回null | 返回null |
| `@Query("SELECT po.operator FROM PurchaseOrder po WHERE po.id = :purchaseOrderId")`<br>`User findOperatorByPurchaseOrderId(@Param("purchaseOrderId") Long purchaseOrderId)` | `User` | `purchaseOrderId`: 采购订单ID | 根据采购订单ID查询关联的操作员详情 | 返回指定采购订单关联的操作员对象，若不存在则返回null | 返回null |
| `@Query("SELECT po FROM PurchaseOrder po WHERE po.supplier LIKE LOWER(CONCAT('%', :supplier, '%')) ORDER BY po.orderTime DESC")`<br>`Page<PurchaseOrder> findBySupplierWithPagination(@Param("supplier") String supplier, Pageable pageable)` | `Page<PurchaseOrder>` | `supplier`: 供应商, `pageable`: 分页参数 | 根据供应商查询采购订单（带分页） | 返回指定供应商的采购订单分页对象，若不存在则返回空Page | 返回空Page对象 |
| `@Query("SELECT po.supplier, COUNT(po) as orderCount, SUM(po.totalAmount) as totalAmount FROM PurchaseOrder po WHERE po.orderStatus = 2 GROUP BY po.supplier ORDER BY totalAmount DESC")`<br>`List<Object[]> findSupplierPurchaseStatistics()` | `List<Object[]>` | 无 | 统计供应商的采购总额 | 返回包含供应商名称、订单数量、采购总额的Object数组列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT po.orderStatus, COUNT(po) as orderCount FROM PurchaseOrder po GROUP BY po.orderStatus")`<br>`List<Object[]> findOrderStatusStatistics()` | `List<Object[]>` | 无 | 统计每个状态的采购订单数量 | 返回包含订单状态和对应数量的Object数组列表，若不存在则返回空列表 | 返回空列表 |
| `@Query(value = "SELECT * FROM purchase_order po WHERE po.expected_arrival BETWEEN CURRENT_DATE() AND DATEADD('DAY', 7, CURRENT_DATE()) AND po.order_status IN (0, 1)", nativeQuery = true)`<br>`List<PurchaseOrder> findUpcomingOrders()` | `List<PurchaseOrder>` | 无 | 查询即将到期的采购订单（7天内） | 返回预计7天内到货的待处理或已确认订单列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT COUNT(po) as orderCount, COALESCE(SUM(po.totalAmount), CAST(0 AS BigDecimal)) as totalAmount FROM PurchaseOrder po WHERE po.orderTime BETWEEN :startTime AND :endTime AND po.orderStatus = 2")`<br>`Object[] countAndSumByPeriod(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime)` | `Object[]` | `startTime`: 开始时间, `endTime`: 结束时间 | 统计某个时间段的采购订单数量和总金额 | 返回包含订单数量和总金额的Object数组，第一个元素为订单数量，第二个元素为总金额，若不存在则返回包含0的数组 | 返回包含0的数组 |

**继承的JpaRepository方法**：
- 基础的CRUD操作方法

---

### 5. 接口：SaleRecordRepository（销售记录数据访问接口）

**位置**：`com.example.demo.repository`

**继承关系**：`extends JpaRepository<SaleRecord, Long>`

**类注解说明**：
- `@Repository`：Spring注解，标识为数据访问组件

**描述**：销售记录实体的数据访问接口，提供销售记录的CRUD操作及销售统计分析

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 返回值说明 | 失败情况返回值 |
|---------|---------|------|------|-----------|--------------|
| `Optional<SaleRecord> findByRecordNo(String recordNo)` | `Optional<SaleRecord>` | `recordNo`: 销售单号 | 根据销售单号精确查找 | 返回包含指定销售单号销售记录的Optional对象，若不存在则返回Optional.empty() | 返回Optional.empty() |
| `@Query("SELECT sr FROM SaleRecord sr WHERE sr.medicine.id = :medicineId")`<br>`List<SaleRecord> findByMedicineId(@Param("medicineId") Long medicineId)` | `List<SaleRecord>` | `medicineId`: 药品ID | 根据药品ID查找销售记录 | 返回指定药品ID的销售记录列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT sr FROM SaleRecord sr WHERE sr.operator.id = :operatorId")`<br>`List<SaleRecord> findByOperatorId(@Param("operatorId") Long operatorId)` | `List<SaleRecord>` | `operatorId`: 操作员ID | 根据操作员ID查找销售记录 | 返回指定操作员ID的销售记录列表，若不存在则返回空列表 | 返回空列表 |
| `List<SaleRecord> findBySaleTimeBetween(LocalDateTime startTime, LocalDateTime endTime)` | `List<SaleRecord>` | `startTime`: 开始时间, `endTime`: 结束时间 | 根据时间段查找销售记录 | 返回指定时间范围内的销售记录列表，若不存在则返回空列表 | 返回空列表 |
| `Page<SaleRecord> findAll(Pageable pageable)` | `Page<SaleRecord>` | `pageable`: 分页参数 | 分页查询所有销售记录 | 返回包含销售记录数据的Page对象，若不存在则返回空Page | 返回空Page对象 |
| `Page<SaleRecord> findBySaleTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable)` | `Page<SaleRecord>` | `startTime`: 开始时间, `endTime`: 结束时间, `pageable`: 分页参数 | 根据时间段分页查询 | 返回包含指定时间范围内销售记录数据的Page对象，若不存在则返回空Page | 返回空Page对象 |
| `@Query("SELECT COALESCE(SUM(sr.quantity), 0) FROM SaleRecord sr WHERE sr.medicine.id = :medicineId")`<br>`Long sumQuantityByMedicineId(@Param("medicineId") Long medicineId)` | `Long` | `medicineId`: 药品ID | 统计某个药品的销售总量（使用COALESCE处理null值） | 返回指定药品ID的销售总量，若不存在则返回0 | 返回0 |
| `@Query("SELECT COALESCE(SUM(sr.totalAmount), CAST(0 AS BigDecimal)) FROM SaleRecord sr WHERE sr.saleTime BETWEEN :startTime AND :endTime")`<br>`BigDecimal sumTotalAmountByPeriod(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime)` | `BigDecimal` | `startTime`: 开始时间, `endTime`: 结束时间 | 统计某个时间段的销售总额（使用COALESCE处理null值） | 返回指定时间段内的销售总额，若不存在则返回0 | 返回0 |
| `@Query(value = "SELECT CAST(sr.sale_time AS DATE), sr.medicine_id, SUM(sr.quantity) FROM sale_record sr WHERE sr.sale_time BETWEEN :startDate AND :endDate GROUP BY CAST(sr.sale_time AS DATE), sr.medicine_id ORDER BY CAST(sr.sale_time AS DATE)", nativeQuery = true)`<br>`List<Object[]> findDailySales(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate)` | `List<Object[]>` | `startDate`: 开始日期, `endDate`: 结束日期 | 统计每天的销售数据（用于需求预测） | 返回包含销售日期、药品ID和销售数量的Object数组列表，每个数组第一个元素为销售日期，第二个元素为药品ID，第三个元素为销售数量，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT sr.medicine.id, SUM(sr.quantity) as totalQuantity FROM SaleRecord sr WHERE sr.saleTime BETWEEN :startDate AND :endDate GROUP BY sr.medicine.id ORDER BY totalQuantity DESC")`<br>`List<Object[]> findTopSellingMedicines(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable)` | `List<Object[]>` | `startDate`: 开始日期, `endDate`: 结束日期, `pageable`: 分页参数 | 查找最畅销的药品（按销售数量降序） | 返回包含药品ID和销售数量的Object数组列表，按销售数量降序排列，每个数组第一个元素为药品ID，第二个元素为销售数量，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT sr FROM SaleRecord sr JOIN sr.symptom s WHERE s.id = :symptomId")`<br>`List<SaleRecord> findBySymptomId(@Param("symptomId") Integer symptomId)` | `List<SaleRecord>` | `symptomId`: 症状ID | 根据症状ID查找销售记录 | 返回与指定症状ID关联的销售记录列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT sr FROM SaleRecord sr JOIN sr.symptom s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :symptomName, '%'))")`<br>`List<SaleRecord> findBySymptomName(@Param("symptomName") String symptomName)` | `List<SaleRecord>` | `symptomName`: 症状名称 | 根据症状名称模糊查找销售记录 | 返回与症状名称包含指定字符串的症状关联的销售记录列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT s.name, SUM(sr.quantity) as totalQuantity, SUM(sr.totalAmount) as totalAmount FROM SaleRecord sr JOIN sr.symptom s WHERE sr.saleTime BETWEEN :startDate AND :endDate GROUP BY s.id, s.name ORDER BY totalQuantity DESC")`<br>`List<Object[]> findSalesBySymptom(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate)` | `List<Object[]>` | `startDate`: 开始日期, `endDate`: 结束日期 | 统计按症状分类的销售数据 | 返回包含症状名称、销售数量和销售总额的Object数组列表，每个数组第一个元素为症状名称，第二个元素为销售数量，第三个元素为销售总额，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT sr.medicine FROM SaleRecord sr WHERE sr.id = :saleRecordId")`<br>`Medicine findMedicineBySaleRecordId(@Param("saleRecordId") Long saleRecordId)` | `Medicine` | `saleRecordId`: 销售记录ID | 根据销售记录ID查询关联的药品详情 | 返回指定销售记录关联的药品对象，若不存在则返回null | 返回null |
| `@Query("SELECT sr.operator FROM SaleRecord sr WHERE sr.id = :saleRecordId")`<br>`User findOperatorBySaleRecordId(@Param("saleRecordId") Long saleRecordId)` | `User` | `saleRecordId`: 销售记录ID | 根据销售记录ID查询关联的操作员详情 | 返回指定销售记录关联的操作员对象，若不存在则返回null | 返回null |
| `@Query("SELECT sr.symptom FROM SaleRecord sr WHERE sr.id = :saleRecordId")`<br>`List<Symptom> findSymptomsBySaleRecordId(@Param("saleRecordId") Long saleRecordId)` | `List<Symptom>` | `saleRecordId`: 销售记录ID | 根据销售记录ID查询关联的症状列表 | 返回指定销售记录关联的症状列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT sr FROM SaleRecord sr WHERE sr.operator.id = :operatorId ORDER BY sr.saleTime DESC")`<br>`Page<SaleRecord> findByOperatorIdWithPagination(@Param("operatorId") Long operatorId, Pageable pageable)` | `Page<SaleRecord>` | `operatorId`: 操作员ID, `pageable`: 分页参数 | 根据操作员ID查询销售记录（带分页） | 返回指定操作员ID的销售记录分页对象，按时间降序排列，若不存在则返回空Page | 返回空Page对象 |
| `@Query("SELECT sr FROM SaleRecord sr WHERE sr.customerType = :customerType ORDER BY sr.saleTime DESC")`<br>`List<SaleRecord> findByCustomerType(@Param("customerType") Integer customerType)` | `List<SaleRecord>` | `customerType`: 顾客类型 | 根据顾客类型查询销售记录 | 返回指定顾客类型的销售记录列表，按时间降序排列，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT sr.operator.id, sr.operator.realName, COUNT(sr) as recordCount, SUM(sr.totalAmount) as totalAmount FROM SaleRecord sr WHERE sr.saleTime BETWEEN :startDate AND :endDate GROUP BY sr.operator.id, sr.operator.realName ORDER BY totalAmount DESC")`<br>`List<Object[]> findOperatorSalesPerformance(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate)` | `List<Object[]>` | `startDate`: 开始日期, `endDate`: 结束日期 | 统计操作员的销售业绩 | 返回包含操作员ID、真实姓名、记录数量、销售总额的Object数组列表，若不存在则返回空列表 | 返回空列表 |
| `@Query(value = "SELECT CAST(sr.sale_time AS DATE), SUM(sr.total_amount) as dailyAmount FROM sale_record sr WHERE sr.sale_time BETWEEN :startDate AND :endDate GROUP BY CAST(sr.sale_time AS DATE) ORDER BY CAST(sr.sale_time AS DATE)", nativeQuery = true)`<br>`List<Object[]> findDailySalesAmount(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate)` | `List<Object[]>` | `startDate`: 开始日期, `endDate`: 结束日期 | 统计每天的销售总额 | 返回包含日期和日销售总额的Object数组列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT m.id, m.name, SUM(sr.totalAmount) as totalSales FROM SaleRecord sr JOIN sr.medicine m WHERE sr.saleTime BETWEEN :startDate AND :endDate GROUP BY m.id, m.name ORDER BY totalSales DESC")`<br>`List<Object[]> findTopSellingMedicinesByAmount(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable)` | `List<Object[]>` | `startDate`: 开始日期, `endDate`: 结束日期, `pageable`: 分页参数 | 查询销售额最高的前N个药品 | 返回包含药品ID、名称、销售总额的Object数组列表，按销售额降序排列，若不存在则返回空列表 | 返回空列表 |

**继承的JpaRepository方法**：
- 基础的CRUD操作方法

---

### 6. 接口：StockRepository（库存信息数据访问接口）

**位置**：`com.example.demo.repository`

**继承关系**：`extends JpaRepository<Stock, Long>`

**类注解说明**：
- `@Repository`：Spring注解，标识为数据访问组件

**描述**：库存信息实体的数据访问接口，提供库存数据的CRUD操作及库存管理相关查询

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 返回值说明 | 失败情况返回值 |
|---------|---------|------|------|-----------|--------------|
| `@Query("SELECT s FROM Stock s WHERE s.medicine.id = :medicineId")`<br>`List<Stock> findByMedicineId(@Param("medicineId") Long medicineId)` | `List<Stock>` | `medicineId`: 药品ID | 根据药品ID查找库存 | 返回指定药品ID的库存列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT s FROM Stock s WHERE s.medicine.id = :medicineId AND s.status = :status")`<br>`List<Stock> findByMedicineIdAndStatus(@Param("medicineId") Long medicineId, @Param("status") Integer status)` | `List<Stock>` | `medicineId`: 药品ID, `status`: 状态(0-过期,1-正常) | 根据药品ID和状态查找库存 | 返回指定药品ID和状态的库存列表，若不存在则返回空列表 | 返回空列表 |
| `List<Stock> findByExpirationDateBeforeAndStatus(LocalDate date, Integer status)` | `List<Stock>` | `date`: 截止日期, `status`: 状态 | 查找过期库存（在指定日期前过期且状态匹配） | 返回在指定日期前过期且状态匹配的库存列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT s FROM Stock s WHERE s.expirationDate BETWEEN :startDate AND :endDate AND s.status = 1")`<br>`List<Stock> findExpiringStock(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate)` | `List<Stock>` | `startDate`: 开始日期, `endDate`: 结束日期 | 查找即将过期的库存 | 返回在指定日期范围内过期且状态正常的库存列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT s FROM Stock s WHERE s.quantity <= s.warningQuantity AND s.status = 1")`<br>`List<Stock> findLowStock()` | `List<Stock>` | 无 | 查找库存不足的药品（数量≤预警数量且状态正常） | 返回数量≤预警数量且状态正常的库存列表，若不存在则返回空列表 | 返回空列表 |
| `List<Stock> findByBatchNumber(String batchNumber)` | `List<Stock>` | `batchNumber`: 批号 | 根据批号查找库存 | 返回指定批号的库存列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Stock s WHERE s.medicine.id = :medicineId AND s.status = 1")`<br>`Long sumQuantityByMedicineId(@Param("medicineId") Long medicineId)` | `Long` | `medicineId`: 药品ID | 计算某个药品的正常状态总库存量（使用COALESCE处理null值） | 返回指定药品ID的正常状态总库存量，若不存在则返回0 | 返回0 |
| `@Query("SELECT s.medicine.id, SUM(s.quantity) as totalQuantity, MIN(s.warningQuantity) as warningQuantity FROM Stock s WHERE s.status = 1 GROUP BY s.medicine.id HAVING SUM(s.quantity) <= MIN(s.warningQuantity)")`<br>`List<Object[]> findLowStockSummary()` | `List<Object[]>` | 无 | 查找所有库存不足的药品汇总信息 | 返回包含药品ID、总库存量和预警数量的Object数组列表，每个数组第一个元素为药品ID，第二个元素为总库存量，第三个元素为预警数量，若不存在则返回空列表 | 返回空列表 |
| `List<Stock> findByShelfLocation(String shelfLocation)` | `List<Stock>` | `shelfLocation`: 货架位置 | 根据货架位置查找库存 | 返回指定货架位置的库存列表，若不存在则返回空列表 | 返回空列表 |
| `List<Stock> findByStatus(Integer status)` | `List<Stock>` | `status`: 货品状态状态(0-过期,1-正常) | 根据货品状态查找库存 | 返回指定状态的库存列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT s.medicine FROM Stock s WHERE s.id = :stockId")`<br>`Medicine findMedicineByStockId(@Param("stockId") Long stockId)` | `Medicine` | `stockId`: 库存ID | 根据库存ID查询关联的药品详情 | 返回指定库存记录关联的药品对象，若不存在则返回null | 返回null |
| `@Query("SELECT s.status, SUM(s.quantity) as totalQuantity FROM Stock s WHERE s.medicine.id = :medicineId GROUP BY s.status")`<br>`List<Object[]> findStatusStatisticsByMedicineId(@Param("medicineId") Long medicineId)` | `List<Object[]>` | `medicineId`: 药品ID | 根据药品ID查询库存状态统计 | 返回包含状态和各状态数量的Object数组列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT s FROM Stock s WHERE s.shelfLocation LIKE LOWER(CONCAT('%', :location, '%')) AND s.status = 1")`<br>`List<Stock> findByShelfLocationContaining(@Param("location") String location)` | `List<Stock>` | `location`: 货架位置 | 根据货架位置模糊查询库存 | 返回货架位置包含指定字符串且状态正常的库存列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT s FROM Stock s WHERE s.status = 0 ORDER BY s.expirationDate ASC")`<br>`List<Stock> findExpiredStock()` | `List<Stock>` | 无 | 查询过期库存（状态为0） | 返回状态为过期的库存列表，按有效期升序排列，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT COALESCE(SUM(s.quantity * m.retailPrice), CAST(0 AS BigDecimal)) FROM Stock s JOIN s.medicine m WHERE s.status = 1")`<br>`BigDecimal calculateTotalStockValue()` | `BigDecimal` | 无 | 统计库存总价值 | 返回所有正常状态库存的总价值（数量×零售价），若不存在则返回0 | 返回0 |
| `@Query("SELECT c.name, COALESCE(SUM(s.quantity * m.retailPrice), CAST(0 AS BigDecimal)) as totalValue FROM Stock s JOIN s.medicine m JOIN m.category c WHERE s.status = 1 GROUP BY c.id, c.name ORDER BY totalValue DESC")`<br>`List<Object[]> calculateStockValueByCategory()` | `List<Object[]>` | 无 | 统计每个分类的库存价值 | 返回包含分类名称和库存价值的Object数组列表，按价值降序排列，若不存在则返回空列表 | 返回空列表 |
| `@Query(value = "SELECT m.id, m.name, COALESCE(stock.currentQty, 0) AS currentStock, COALESCE(sold.soldQty, 0) AS soldQuantity, CASE WHEN COALESCE(stock.currentQty, 0) > 0 THEN COALESCE(sold.soldQty, 0) / stock.currentQty ELSE 0 END AS turnoverRate FROM medicine m LEFT JOIN (SELECT s.medicine_id AS medId, SUM(s.quantity) AS currentQty FROM stock s WHERE s.status = 1 GROUP BY s.medicine_id) stock ON m.id = stock.medId LEFT JOIN (SELECT sr.medicine_id AS medId, SUM(sr.quantity) AS soldQty FROM sale_record sr WHERE sr.sale_time BETWEEN :startDate AND :endDate GROUP BY sr.medicine_id) sold ON m.id = sold.medId ORDER BY turnoverRate DESC", nativeQuery = true)`<br>`List<Object[]> calculateStockTurnoverRate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate)` | `List<Object[]>` | `startDate`: 开始日期, `endDate`: 结束日期 | 查询库存周转率（基于销售记录） | 返回包含药品ID、名称、当前库存、销售数量、周转率的Object数组列表，按周转率降序排列，若不存在则返回空列表 | 返回空列表 |

**继承的JpaRepository方法**：
- 基础的CRUD操作方法

---

### 7. 接口：SymptomRepository（症状数据访问接口）

**位置**：`com.example.demo.repository`

**继承关系**：`extends JpaRepository<Symptom, Integer>`

**类注解说明**：
- `@Repository`：Spring注解，标识为数据访问组件

**描述**：症状实体的数据访问接口，提供症状数据的CRUD操作及查询方法

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 返回值说明 | 失败情况返回值 |
|---------|---------|------|------|-----------|--------------|
| `Optional<Symptom> findByName(String name)` | `Optional<Symptom>` | `name`: 症状名称 | 根据名称精确查找症状 | 返回包含指定名称症状的Optional对象，若不存在则返回Optional.empty() | 返回Optional.empty() |
| `List<Symptom> findByNameContaining(String name)` | `List<Symptom>` | `name`: 症状名称（模糊匹配） | 根据名称模糊查询 | 返回名称包含指定字符串的症状列表，若不存在则返回空列表 | 返回空列表 |
| `boolean existsByName(String name)` | `boolean` | `name`: 症状名称 | 检查症状名称是否存在 | 返回指定症状名称是否存在的布尔值 | 返回false |
| `List<Symptom> findByDescriptionContaining(String description)` | `List<Symptom>` | `description`: 描述（模糊匹配） | 根据描述模糊查询 | 返回描述包含指定字符串的症状列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT s FROM Symptom s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")`<br>`List<Symptom> searchSymptoms(@Param("keyword") String keyword)` | `List<Symptom>` | `keyword`: 关键词 | 搜索症状（名称或描述模糊匹配） | 返回名称或描述包含指定关键词的症状列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT m FROM Medicine m JOIN m.symptoms s WHERE s.id = :symptomId")`<br>`List<Medicine> findMedicinesBySymptomId(@Param("symptomId") Integer symptomId)` | `List<Medicine>` | `symptomId`: 症状ID | 根据症状ID查询关联的药品 | 返回与指定症状关联的所有药品列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT m FROM Medicine m JOIN m.symptoms s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :symptomName, '%'))")`<br>`List<Medicine> findMedicinesBySymptomName(@Param("symptomName") String symptomName)` | `List<Medicine>` | `symptomName`: 症状名称 | 根据症状名称查询关联的药品 | 返回与症状名称包含指定字符串的症状关联的药品列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT sr FROM SaleRecord sr JOIN sr.symptom s WHERE s.id = :symptomId")`<br>`List<SaleRecord> findSaleRecordsBySymptomId(@Param("symptomId") Integer symptomId)` | `List<SaleRecord>` | `symptomId`: 症状ID | 根据症状ID查询关联的销售记录 | 返回与指定症状关联的所有销售记录列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT sr FROM SaleRecord sr JOIN sr.symptom s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :symptomName, '%'))")`<br>`List<SaleRecord> findSaleRecordsBySymptomName(@Param("symptomName") String symptomName)` | `List<SaleRecord>` | `symptomName`: 症状名称 | 根据症状名称查询关联的销售记录 | 返回与症状名称包含指定字符串的症状关联的销售记录列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT COUNT(DISTINCT m) FROM Medicine m JOIN m.symptoms s WHERE s.id = :symptomId")`<br>`long countMedicinesBySymptomId(@Param("symptomId") Integer symptomId)` | `long` | `symptomId`: 症状ID | 统计症状关联的药品数量 | 返回与指定症状关联的药品数量，若不存在则返回0 | 返回0 |
| `@Query("SELECT COUNT(sr) FROM SaleRecord sr JOIN sr.symptom s WHERE s.id = :symptomId")`<br>`long countSaleRecordsBySymptomId(@Param("symptomId") Integer symptomId)` | `long` | `symptomId`: 症状ID | 统计症状关联的销售记录数量 | 返回与指定症状关联的销售记录数量，若不存在则返回0 | 返回0 |
| `@Query("SELECT COALESCE(SUM(sr.totalAmount), CAST(0 AS BigDecimal)) FROM SaleRecord sr JOIN sr.symptom s WHERE s.id = :symptomId")`<br>`BigDecimal sumSaleAmountBySymptomId(@Param("symptomId") Integer symptomId)` | `BigDecimal` | `symptomId`: 症状ID | 统计症状关联的销售总额 | 返回与指定症状关联的销售总额，若不存在则返回0 | 返回0 |
| `@Query("SELECT s.id, COUNT(DISTINCT sr.id) as usageCount FROM SaleRecord sr JOIN sr.symptom s WHERE sr.saleTime BETWEEN :start AND :end GROUP BY s.id ORDER BY usageCount DESC")`<br>`List<Object[]> findMostCommonSymptoms(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable)` | `List<Object[]>` | `start`: 开始时间, `end`: 结束时间, `pageable`: 分页参数 | 查找指定时间范围内最常见的症状 | 返回包含症状ID和使用次数的Object数组列表，按使用次数降序排列，若不存在则返回空列表 | 返回空列表 |

**继承的JpaRepository方法**：
- 基础的CRUD操作方法

---

### 8. 接口：UserRepository（用户信息数据访问接口）

**位置**：`com.example.demo.repository`

**继承关系**：`extends JpaRepository<User, Long>`

**类注解说明**：
- `@Repository`：Spring注解，标识为数据访问组件

**描述**：用户信息实体的数据访问接口，提供用户数据的CRUD操作及用户管理相关查询

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 返回值说明 | 失败情况返回值 |
|---------|---------|------|------|-----------|--------------|
| `Optional<User> findByUsername(String username)` | `Optional<User>` | `username`: 用户名 | 根据用户名精确查找用户 | 返回包含指定用户名用户的Optional对象，若不存在则返回Optional.empty() | 返回Optional.empty() |
| `boolean existsByUsername(String username)` | `boolean` | `username`: 用户名 | 检查用户名是否存在 | 返回指定用户名是否存在的布尔值 | 返回false |
| `List<User> findByRole(String role)` | `List<User>` | `role`: 角色(ADMIN,PHARMACIST,PURCHASER) | 根据角色查找用户 | 返回指定角色的用户列表，若不存在则返回空列表 | 返回空列表 |
| `List<User> findByStatus(Integer status)` | `List<User>` | `status`: 状态(0-禁用,1-正常) | 根据状态查找用户 | 返回指定状态的用户列表，若不存在则返回空列表 | 返回空列表 |
| `Optional<User> findByUsernameAndStatus(String username, Integer status)` | `Optional<User>` | `username`: 用户名, `status`: 状态 | 根据用户名和状态查找 | 返回包含指定用户名和状态用户的Optional对象，若不存在则返回Optional.empty() | 返回Optional.empty() |
| `@Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.realName) LIKE LOWER(CONCAT('%', :keyword, '%'))")`<br>`List<User> searchUsers(@Param("keyword") String keyword)` | `List<User>` | `keyword`: 关键词 | 搜索用户（按用户名或真实姓名模糊匹配） | 返回用户名或真实姓名包含指定关键词的用户列表，若不存在则返回空列表 | 返回空列表 |

**继承的JpaRepository方法**：
- 基础的CRUD操作方法

---

### 层次结构总结

1. 所有Repository接口均直接继承自Spring Data JPA的`JpaRepository<T, ID>`
2. `JpaRepository`继承自`PagingAndSortingRepository<T, ID>`
3. `PagingAndSortingRepository`继承自`CrudRepository<T, ID>`
4. `CrudRepository`继承自`Repository<T, ID>`

**继承链**：`Repository` → `CrudRepository` → `PagingAndSortingRepository` → `JpaRepository` → 具体Repository接口

**功能层级**：
- `Repository`：标记接口，标识为Spring Data Repository
- `CrudRepository`：提供基础的CRUD操作
- `PagingAndSortingRepository`：在CRUD基础上增加分页和排序功能
- `JpaRepository`：进一步扩展，提供JPA相关方法（如flush、批量删除等）

**查询方法命名约定**：
- `findBy[属性]`：根据属性精确匹配
- `findBy[属性]Containing`：字符串模糊匹配
- `findBy[属性]Between`：范围查询
- `findBy[属性1]And[属性2]`：多条件查询
- `findBy[属性]OrderBy[排序属性]Asc/Desc`：排序查询
- `countBy[属性]`：统计查询
- `existsBy[属性]`：存在性检查
- `@Query`：自定义JPQL查询

**事务管理**：
- 所有Repository方法默认都有事务支持
- 查询方法为只读事务
- 修改方法为读写事务
- 所有统计查询方法（如`sumPurchasedQuantityByMedicineId`、`sumTotalAmountByPeriod`、`sumQuantityByMedicineId`等）在代码中均使用了`COALESCE`函数处理null值，返回默认值0或0.0

---

**主要修正说明**：
1. **修正**`CategoryRepository.findDescendantsByParentId`**的描述**：将原文档中"查找某个分类的所有子孙分类（包含子分类和孙分类）"修正为"查找某个分类的直接子分类"，以准确反映代码逻辑（仅查询一级子分类）
2. **保留所有扩展方法**：包括各Repository中新增的关联查询、统计分析等方法（如库存周转率、供应商统计、操作员业绩等）
3. **统一返回类型**：确保`Long`、`BigDecimal`等包装类型与代码完全一致



**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 返回值说明 | 失败情况返回值 |
|---------|---------|------|------|-----------|--------------|
| `Optional<SaleRecord> findByRecordNo(String recordNo)` | `Optional<SaleRecord>` | `recordNo`: 销售单号 | 根据销售单号精确查找 | 返回包含指定销售单号销售记录的Optional对象，若不存在则返回Optional.empty() | 返回Optional.empty() |
| `@Query("SELECT sr FROM SaleRecord sr WHERE sr.medicine.id = :medicineId")`<br>`List<SaleRecord> findByMedicineId(@Param("medicineId") Long medicineId)` | `List<SaleRecord>` | `medicineId`: 药品ID | 根据药品ID查找销售记录 | 返回指定药品ID的销售记录列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT sr FROM SaleRecord sr WHERE sr.operator.id = :operatorId")`<br>`List<SaleRecord> findByOperatorId(@Param("operatorId") Long operatorId)` | `List<SaleRecord>` | `operatorId`: 操作员ID | 根据操作员ID查找销售记录 | 返回指定操作员ID的销售记录列表，若不存在则返回空列表 | 返回空列表 |
| `List<SaleRecord> findBySaleTimeBetween(LocalDateTime startTime, LocalDateTime endTime)` | `List<SaleRecord>` | `startTime`: 开始时间, `endTime`: 结束时间 | 根据时间段查找销售记录 | 返回指定时间范围内的销售记录列表，若不存在则返回空列表 | 返回空列表 |
| `Page<SaleRecord> findAll(Pageable pageable)` | `Page<SaleRecord>` | `pageable`: 分页参数 | 分页查询所有销售记录 | 返回包含销售记录数据的Page对象，若不存在则返回空Page | 返回空Page对象 |
| `Page<SaleRecord> findBySaleTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable)` | `Page<SaleRecord>` | `startTime`: 开始时间, `endTime`: 结束时间, `pageable`: 分页参数 | 根据时间段分页查询 | 返回包含指定时间范围内销售记录数据的Page对象，若不存在则返回空Page | 返回空Page对象 |
| `@Query("SELECT COALESCE(SUM(sr.quantity), 0) FROM SaleRecord sr WHERE sr.medicine.id = :medicineId")`<br>`Long sumQuantityByMedicineId(@Param("medicineId") Long medicineId)` | `Long` | `medicineId`: 药品ID | 统计某个药品的销售总量（使用COALESCE处理null值） | 返回指定药品ID的销售总量，若不存在则返回0 | 返回0 |
| `@Query("SELECT COALESCE(SUM(sr.totalAmount), CAST(0 AS BigDecimal)) FROM SaleRecord sr WHERE sr.saleTime BETWEEN :startTime AND :endTime")`<br>`BigDecimal sumTotalAmountByPeriod(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime)` | `BigDecimal` | `startTime`: 开始时间, `endTime`: 结束时间 | 统计某个时间段的销售总额（使用COALESCE处理null值） | 返回指定时间段内的销售总额，若不存在则返回0 | 返回0 |
| `@Query(value = "SELECT CAST(sr.sale_time AS DATE), sr.medicine_id, SUM(sr.quantity) FROM sale_record sr WHERE sr.sale_time BETWEEN :startDate AND :endDate GROUP BY CAST(sr.sale_time AS DATE), sr.medicine_id ORDER BY CAST(sr.sale_time AS DATE)", nativeQuery = true)`<br>`List<Object[]> findDailySales(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate)` | `List<Object[]>` | `startDate`: 开始日期, `endDate`: 结束日期 | 统计每天的销售数据（用于需求预测） | 返回包含销售日期、药品ID和销售数量的Object数组列表，每个数组第一个元素为销售日期，第二个元素为药品ID，第三个元素为销售数量，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT sr.medicine.id, SUM(sr.quantity) as totalQuantity FROM SaleRecord sr WHERE sr.saleTime BETWEEN :startDate AND :endDate GROUP BY sr.medicine.id ORDER BY totalQuantity DESC")`<br>`List<Object[]> findTopSellingMedicines(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable)` | `List<Object[]>` | `startDate`: 开始日期, `endDate`: 结束日期, `pageable`: 分页参数 | 查找最畅销的药品（按销售数量降序） | 返回包含药品ID和销售数量的Object数组列表，按销售数量降序排列，每个数组第一个元素为药品ID，第二个元素为销售数量，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT sr FROM SaleRecord sr JOIN sr.symptom s WHERE s.id = :symptomId")`<br>`List<SaleRecord> findBySymptomId(@Param("symptomId") Integer symptomId)` | `List<SaleRecord>` | `symptomId`: 症状ID | 根据症状ID查找销售记录 | 返回与指定症状ID关联的销售记录列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT sr FROM SaleRecord sr JOIN sr.symptom s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :symptomName, '%'))")`<br>`List<SaleRecord> findBySymptomName(@Param("symptomName") String symptomName)` | `List<SaleRecord>` | `symptomName`: 症状名称 | 根据症状名称模糊查找销售记录 | 返回与症状名称包含指定字符串的症状关联的销售记录列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT s.name, SUM(sr.quantity) as totalQuantity, SUM(sr.totalAmount) as totalAmount FROM SaleRecord sr JOIN sr.symptom s WHERE sr.saleTime BETWEEN :startDate AND :endDate GROUP BY s.id, s.name ORDER BY totalQuantity DESC")`<br>`List<Object[]> findSalesBySymptom(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate)` | `List<Object[]>` | `startDate`: 开始日期, `endDate`: 结束日期 | 统计按症状分类的销售数据 | 返回包含症状名称、销售数量和销售总额的Object数组列表，每个数组第一个元素为症状名称，第二个元素为销售数量，第三个元素为销售总额，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT sr.medicine FROM SaleRecord sr WHERE sr.id = :saleRecordId")`<br>`Medicine findMedicineBySaleRecordId(@Param("saleRecordId") Long saleRecordId)` | `Medicine` | `saleRecordId`: 销售记录ID | 根据销售记录ID查询关联的药品详情 | 返回指定销售记录关联的药品对象，若不存在则返回null | 返回null |
| `@Query("SELECT sr.operator FROM SaleRecord sr WHERE sr.id = :saleRecordId")`<br>`User findOperatorBySaleRecordId(@Param("saleRecordId") Long saleRecordId)` | `User` | `saleRecordId`: 销售记录ID | 根据销售记录ID查询关联的操作员详情 | 返回指定销售记录关联的操作员对象，若不存在则返回null | 返回null |
| `@Query("SELECT sr.symptom FROM SaleRecord sr WHERE sr.id = :saleRecordId")`<br>`List<Symptom> findSymptomsBySaleRecordId(@Param("saleRecordId") Long saleRecordId)` | `List<Symptom>` | `saleRecordId`: 销售记录ID | 根据销售记录ID查询关联的症状列表 | 返回指定销售记录关联的症状列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT sr FROM SaleRecord sr WHERE sr.operator.id = :operatorId ORDER BY sr.saleTime DESC")`<br>`Page<SaleRecord> findByOperatorIdWithPagination(@Param("operatorId") Long operatorId, Pageable pageable)` | `Page<SaleRecord>` | `operatorId`: 操作员ID, `pageable`: 分页参数 | 根据操作员ID查询销售记录（带分页） | 返回指定操作员ID的销售记录分页对象，按时间降序排列，若不存在则返回空Page | 返回空Page对象 |
| `@Query("SELECT sr FROM SaleRecord sr WHERE sr.customerType = :customerType ORDER BY sr.saleTime DESC")`<br>`List<SaleRecord> findByCustomerType(@Param("customerType") Integer customerType)` | `List<SaleRecord>` | `customerType`: 顾客类型 | 根据顾客类型查询销售记录 | 返回指定顾客类型的销售记录列表，按时间降序排列，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT sr.operator.id, sr.operator.realName, COUNT(sr) as recordCount, SUM(sr.totalAmount) as totalAmount FROM SaleRecord sr WHERE sr.saleTime BETWEEN :startDate AND :endDate GROUP BY sr.operator.id, sr.operator.realName ORDER BY totalAmount DESC")`<br>`List<Object[]> findOperatorSalesPerformance(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate)` | `List<Object[]>` | `startDate`: 开始日期, `endDate`: 结束日期 | 统计操作员的销售业绩 | 返回包含操作员ID、真实姓名、记录数量、销售总额的Object数组列表，若不存在则返回空列表 | 返回空列表 |
| `@Query(value = "SELECT CAST(sr.sale_time AS DATE), SUM(sr.total_amount) as dailyAmount FROM sale_record sr WHERE sr.sale_time BETWEEN :startDate AND :endDate GROUP BY CAST(sr.sale_time AS DATE) ORDER BY CAST(sr.sale_time AS DATE)", nativeQuery = true)`<br>`List<Object[]> findDailySalesAmount(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate)` | `List<Object[]>` | `startDate`: 开始日期, `endDate`: 结束日期 | 统计每天的销售总额 | 返回包含日期和日销售总额的Object数组列表，若不存在则返回空列表 | 返回空列表 |
| `@Query("SELECT m.id, m.name, SUM(sr.totalAmount) as totalSales FROM SaleRecord sr JOIN sr.medicine m WHERE sr.saleTime BETWEEN :startDate AND :endDate GROUP BY m.id, m.name ORDER BY totalSales DESC")`<br>`List<Object[]> findTopSellingMedicinesByAmount(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable)` | `List<Object[]>` | `startDate`: 开始日期, `endDate`: 结束日期, `pageable`: 分页参数 | 查询销售额最高的前N个药品 | 返回包含药品ID、名称、销售总额的Object数组列表，按销售额降序排列，若不存在则返回空列表 | 返回空列表 |

**继承的JpaRepository方法**：
- 基础的CRUD操作方法

---


## 业务逻辑层 (Service Layer) 文档

### 1. 接口：BaseService（基础服务接口）

**位置**：`com.example.demo.service`

**继承关系**：无

**类注解说明**：
- `@Transactional`：Spring注解，标识为事务管理接口

**描述**：定义所有服务层接口的基础CRUD操作，提供标准的数据访问方法

**方法列表**：

| 方法签名                             | 返回类型  | 参数                 | 描述                   | 返回值说明                                              | 失败情况返回值             |
| ------------------------------------ | --------- | -------------------- | ---------------------- | ------------------------------------------------------- | -------------------------- |
| `T save(T entity)`                   | `T`       | `entity`: 实体对象   | 保存实体（新增或更新） | 返回保存后的实体对象                                    | 保存失败时抛出异常         |
| `T update(T entity)`                 | `T`       | `entity`: 实体对象   | 更新实体               | 返回更新后的实体对象                                    | 更新失败时抛出异常         |
| `void delete(ID id)`                 | `void`    | `id`: 实体ID         | 根据ID删除实体         | 无返回值                                                | 删除失败时抛出异常         |
| `T findById(ID id)`                  | `T`       | `id`: 实体ID         | 根据ID查找实体         | 返回指定ID的实体对象                                    | 找不到时返回null           |
| `List<T> findAll()`                  | `List<T>` | 无                   | 查找所有实体           | 返回所有实体的列表                                      | 无数据时返回空列表         |
| `Page<T> findAll(Pageable pageable)` | `Page<T>` | `pageable`: 分页参数 | 分页查询所有实体       | 返回所有实体的分页对象，包含分页信息和数据列表          | 无数据时返回空Page对象     |
| `List<T> saveAll(List<T> entities)`  | `List<T>` | `entities`: 实体列表 | 批量保存实体           | 返回批量保存后的实体列表                                | 保存失败时抛出异常         |
| `void deleteAll(List<ID> ids)`       | `void`    | `ids`: ID列表        | 批量删除实体           | 无返回值                                                | 删除失败时抛出异常         |
| `boolean exists(ID id)`              | `boolean` | `id`: 实体ID         | 检查实体是否存在       | 返回实体是否存在的布尔值，true表示存在，false表示不存在 | 无特殊情况，始终返回布尔值 |

---

### 2. 抽象类：BaseServiceImpl（基础服务实现）

**位置**：`com.example.demo.service.impl`

**继承关系**：`implements BaseService<T, ID>`

**类注解说明**：
- `@Transactional`：Spring注解，标识为事务管理实现类

**描述**：所有服务实现类的抽象基类，提供通用CRUD操作的默认实现

**字段说明**：
| 字段名       | 类型 | 描述                             |
| ------------ | ---- | -------------------------------- |
| `repository` | `R`  | JPA Repository实例，用于数据访问 |

**构造方法**：
- `protected BaseServiceImpl(R repository)`：注入Repository依赖

**方法实现**：

| 方法签名                             | 返回类型  | 描述                                                 | 注解/事务                         |
| ------------------------------------ | --------- | ---------------------------------------------------- | --------------------------------- |
| `T save(T entity)`                   | `T`       | 保存实体，调用`repository.save()`                    | `@Transactional`                  |
| `T update(T entity)`                 | `T`       | 更新实体，调用`repository.save()`                    | `@Transactional`                  |
| `void delete(ID id)`                 | `void`    | 删除实体，调用`repository.deleteById()`              | `@Transactional`                  |
| `T findById(ID id)`                  | `T`       | 查找实体，调用`repository.findById()`                | `@Transactional(readOnly = true)` |
| `List<T> findAll()`                  | `List<T>` | 查询所有实体，调用`repository.findAll()`             | `@Transactional(readOnly = true)` |
| `Page<T> findAll(Pageable pageable)` | `Page<T>` | 分页查询所有实体，调用`repository.findAll(pageable)` | `@Transactional(readOnly = true)` |
| `List<T> saveAll(List<T> entities)`  | `List<T>` | 批量保存实体，调用`repository.saveAll()`             | `@Transactional`                  |
| `void deleteAll(List<ID> ids)`       | `void`    | 批量删除实体，循环调用`repository.deleteById()`      | `@Transactional`                  |
| `boolean exists(ID id)`              | `boolean` | 检查实体是否存在，调用`repository.existsById()`      | `@Transactional(readOnly = true)` |

---

### 3. 接口：CategoryService（分类管理服务）

**位置**：`com.example.demo.service`

**继承关系**：`extends BaseService<Category, Long>`

**类注解说明**：
- `@Transactional`：Spring注解，标识为事务管理接口

**描述**：药品分类业务逻辑服务接口，提供分类数据的CRUD操作及树形结构管理

**方法列表**：

| 方法签名                                                             | 返回类型            | 参数                                    | 描述                                 | 返回值说明                                                                                           | 失败情况返回值                                  |
| -------------------------------------------------------------------- | ------------------- | --------------------------------------- | ------------------------------------ | ---------------------------------------------------------------------------------------------------- | ----------------------------------------------- |
| `List<Category> findByParentId(Long parentId)`                       | `List<Category>`    | `parentId`: 父分类ID                    | 根据父分类ID查找子分类               | 返回指定父分类下的所有子分类列表，包含所有状态的分类                                                 | 返回空列表                                      |
| `List<Category> findByLevel(Integer level)`                          | `List<Category>`    | `level`: 分类级别                       | 根据分类级别查找                     | 返回指定级别的所有分类列表，包含所有状态的分类                                                       | 返回空列表                                      |
| `List<Category> findRootCategories()`                                | `List<Category>`    | 无                                      | 查找所有一级分类（启用状态）         | 返回父ID为0且状态为启用(1)的所有分类列表，按排序字段升序排列                                         | 返回空列表                                      |
| `Page<Category> findAll(Pageable pageable)`                          | `Page<Category>`    | `pageable`: 分页参数                    | 分页查询所有分类                     | 返回包含所有分类的分页对象，支持排序和分页                                                           | 返回空Page对象                                  |
| `List<Map<String, Object>> getCategoryTree()`                        | `List<Map>`         | 无                                      | 获取分类树形结构（只包含启用分类）   | 返回分类树形结构列表，每个节点包含id、name、parentId、level、description、sort、status和children字段 | 返回空列表                                      |
| `boolean existsByName(String name)`                                  | `boolean`           | `name`: 分类名称                        | 检查分类名称是否存在                 | 返回指定分类名称是否存在的布尔值                                                                     | 返回false                                       |
| `void updateCategoryTree()`                                          | `void`              | 无                                      | 更新分类树（重新计算所有分类的级别） | 无返回值，执行后会更新所有分类的级别字段                                                             | 无                                              |
| `Map<Long, String> getCategoryPath(Long categoryId)`                 | `Map<Long, String>` | `categoryId`: 分类ID                    | 获取分类路径映射（从根到当前分类）   | 返回从根分类到当前分类的路径映射，键为分类ID，值为分类名称，按层级顺序排列                           | 返回空Map                                       |
| `Page<Category> searchCategories(String keyword, Pageable pageable)` | `Page<Category>`    | `keyword`: 关键词, `pageable`: 分页参数 | 搜索分类（按名称或描述）             | 返回名称或描述包含关键词的启用状态分类的分页对象                                                     | 返回空Page对象                                  |
| `boolean hasAssociatedMedicines(Long categoryId)`                    | `boolean`           | `categoryId`: 分类ID                    | 检查分类是否有关联的药品             | 返回分类是否有关联药品的布尔值，分类不存在则返回false                                                | 返回false                                       |
| `void delete(Long id)`                                               | `void`              | `id`: 分类ID                            | 删除分类（检查关联药品）             | 无返回值，执行后会删除指定分类                                                                       | 分类下有关联药品时抛出IllegalStateException异常 |
| `Page<Category> findActiveCategories(Pageable pageable)`             | `Page<Category>`    | `pageable`: 分页参数                    | 查找启用的分类（分页）               | 返回状态为启用的分类分页对象                                                                         | 返回空Page对象                                  |
| `Page<Category> findSubcategoriesByParentId(Long parentId, Pageable pageable)` | `Page<Category>` | `parentId`: 父分类ID, `pageable`: 分页参数 | 根据父分类ID查找子分类（分页）    | 返回指定父分类下的子分类分页对象                                                                     | 返回空Page对象                                  |
| `Map<Long, List<Category>> getCategoryHierarchy()`                   | `Map<Long, List<Category>>` | 无                            | 获取分类层级结构                     | 返回按父分类ID分组的分类映射                                                                         | 返回空Map                                       |
| `Page<Category> findCategoriesWithMedicines(Pageable pageable)`      | `Page<Category>`    | `pageable`: 分页参数                    | 查找有关联药品的分类（分页）         | 返回包含至少一个药品的分类分页对象                                                                   | 返回空Page对象                                  |
| `Map<String, Object> getCategoryStatistics(Long categoryId)`         | `Map<String, Object>` | `categoryId`: 分类ID                  | 获取分类统计信息                     | 返回分类的统计信息，包含药品数量、销售金额、子分类数量等                                             | 返回空Map                                       |

**实现类**：`CategoryServiceImpl`

---

### 4. 类：CategoryServiceImpl（分类管理服务实现）

**位置**：`com.example.demo.service.impl`

**继承关系**：`extends BaseServiceImpl<Category, Long, CategoryRepository> implements CategoryService`

**类注解说明**：
- `@Service`：Spring注解，标识为服务组件
- `@Transactional`：类级别事务注解

**主要方法实现**：

| 方法签名                                              | 实现描述                                                      | 业务逻辑                                                  |
| ----------------------------------------------------- | ------------------------------------------------------------- | --------------------------------------------------------- |
| `findByParentId(Long parentId)`                       | 调用`repository.findByParentId()`                             | 直接查询                                                  |
| `findByLevel(Integer level)`                          | 调用`repository.findByLevel()`                                | 直接查询                                                  |
| `findRootCategories()`                                | 调用`repository.findByParentIdAndStatusOrderBySortAsc(0L, 1)` | 查找父ID为0且状态为启用的分类                             |
| `findAll(Pageable pageable)`                          | 调用`repository.findAll(pageable)`                            | 分页查询                                                  |
| `getCategoryTree()`                                   | 获取所有启用分类，构建分类树形结构                            | 1. 查询所有启用分类<br>2. 构建分类映射<br>3. 构建父子关系 |
| `existsByName(String name)`                           | 调用`repository.findByName()`检查是否存在                     | 直接查询                                                  |
| `updateCategoryTree()`                                | 更新所有分类的级别                                            | 递归更新分类级别                                          |
| `getCategoryPath(Long categoryId)`                    | 获取分类路径映射                                              | 从当前分类向上追溯到根分类                                |
| `searchCategories(String keyword, Pageable pageable)` | 搜索分类（名称或描述模糊匹配）                                | 内存分页处理                                              |
| `hasAssociatedMedicines(Long categoryId)`             | 检查分类是否有关联的药品                                      | 检查分类的`medicines`列表                                 |
| `delete(Long id)`                                     | 删除分类（检查关联药品）                                      | 如果有关联药品则抛出异常                                  |
| `findActiveCategories(Pageable pageable)`             | 查询启用状态的分类                                            | 调用`repository.findByStatusOrderBySortAsc(1)`，内存分页  |
| `findSubcategoriesByParentId(Long parentId, Pageable pageable)` | 查询指定父分类的子分类                              | 调用`repository.findByParentId()`，内存分页               |
| `getCategoryHierarchy()`                              | 获取分类层级结构                                              | 使用Stream按parentId分组                                  |
| `findCategoriesWithMedicines(Pageable pageable)`      | 查找有关联药品的分类                                          | 遍历所有分类，检查关联药品数量，内存分页                  |
| `getCategoryStatistics(Long categoryId)`              | 获取分类统计信息                                              | 统计药品数量、销售金额、子分类数量等                      |

---

### 5. 接口：MedicineService（药品管理服务）

**位置**：`com.example.demo.service`

**继承关系**：`extends BaseService<Medicine, Long>`

**类注解说明**：
- `@Transactional`：Spring注解，标识为事务管理接口

**描述**：药品信息业务逻辑服务接口，提供药品数据的CRUD操作及复杂查询

**方法列表**：

| 方法签名                                                                          | 返回类型         | 参数                                                         | 描述                               | 返回值说明                                             | 失败情况返回值 |
| --------------------------------------------------------------------------------- | ---------------- | ------------------------------------------------------------ | ---------------------------------- | ------------------------------------------------------ | -------------- |
| `Medicine findByMedicineCode(String medicineCode)`                                | `Medicine`       | `medicineCode`: 药品编码                                     | 根据药品编码查找药品               | 返回指定药品编码的药品对象，不存在则返回null           | 返回null       |
| `Page<Medicine> findAll(Pageable pageable)`                                       | `Page<Medicine>` | `pageable`: 分页参数                                         | 分页查询所有药品                   | 返回包含所有药品的分页对象，支持排序和分页             | 返回空Page对象 |
| `Page<Medicine> findByStatus(Integer status, Pageable pageable)`                  | `Page<Medicine>` | `status`: 状态, `pageable`: 分页参数                         | 按状态分页查询药品                 | 返回指定状态药品的分页对象                             | 返回空Page对象 |
| `List<Medicine> searchMedicines(String keyword)`                                  | `List<Medicine>` | `keyword`: 搜索关键词                                        | 搜索药品（名称、通用名、生产厂家） | 返回名称、通用名或生产厂家包含关键词的启用状态药品列表 | 返回空列表     |
| `List<Medicine> findByCategoryId(Long categoryId)`                                | `List<Medicine>` | `categoryId`: 分类ID                                         | 根据分类ID查找药品                 | 返回指定分类ID的所有药品列表                           | 返回空列表     |
| `Medicine updatePrice(Long id, BigDecimal retailPrice, BigDecimal purchasePrice)` | `Medicine`       | `id`: 药品ID, `retailPrice`: 零售价, `purchasePrice`: 采购价 | 更新药品价格                       | 返回更新后的药品对象，药品不存在则返回null             | 返回null       |
| `long countByStatus(Integer status)`                                              | `long`           | `status`: 状态                                               | 统计指定状态的药品数量             | 返回指定状态的药品数量，无匹配则返回0                  | 返回0          |
| `Page<Medicine> findSeasonalMedicines(Pageable pageable)`                         | `Page<Medicine>` | `pageable`: 分页参数                                         | 查找季节性药品（分页）             | 返回季节性药品的分页对象                               | 返回空Page对象 |
| `Page<Medicine> findPrescriptionMedicines(Pageable pageable)`                     | `Page<Medicine>` | `pageable`: 分页参数                                         | 查找处方药（分页）                 | 返回处方药的分页对象                                   | 返回空Page对象 |
| `Page<Medicine> findByManufacturer(String manufacturer, Pageable pageable)`       | `Page<Medicine>` | `manufacturer`: 生产厂家, `pageable`: 分页参数               | 根据生产厂家查找药品（分页）       | 返回指定生产厂家的药品分页对象                         | 返回空Page对象 |
| `Page<Medicine> findLowStockMedicines(Pageable pageable)`                         | `Page<Medicine>` | `pageable`: 分页参数                                         | 查找库存不足的药品（分页）         | 返回库存低于预警阈值的药品分页对象                     | 返回空Page对象 |
| `Map<String, Object> getMedicineSalesStatistics(Long medicineId)`                 | `Map<String, Object>` | `medicineId`: 药品ID                                    | 获取药品销售统计信息               | 返回药品的销售总量、销售趋势等统计信息                 | 返回空Map      |
| `Map<String, Object> getMedicinePurchaseStatistics(Long medicineId)`              | `Map<String, Object>` | `medicineId`: 药品ID                                    | 获取药品采购统计信息               | 返回药品的采购总量等统计信息                           | 返回空Map      |
| `void batchUpdateStatus(List<Long> medicineIds, Integer status)`                  | `void`           | `medicineIds`: 药品ID列表, `status`: 状态                    | 批量更新药品状态                   | 无返回值                                               | 抛出异常       |
| `Page<Medicine> findExpiringMedicines(int daysThreshold, Pageable pageable)`      | `Page<Medicine>` | `daysThreshold`: 天数阈值, `pageable`: 分页参数              | 查找即将过期的药品（分页）         | 返回指定天数内即将过期的药品分页对象                   | 返回空Page对象 |
| `Page<Medicine> findByStorageRequirement(Integer storageRequirement, Pageable pageable)` | `Page<Medicine>` | `storageRequirement`: 存储要求, `pageable`: 分页参数 | 根据存储要求查找药品（分页）   | 返回指定存储要求的药品分页对象                         | 返回空Page对象 |
| `Page<Medicine> searchMedicinesWithPagination(String keyword, Pageable pageable)` | `Page<Medicine>` | `keyword`: 关键词, `pageable`: 分页参数                      | 搜索药品（分页）                   | 返回关键词搜索结果的药品分页对象                       | 返回空Page对象 |

**实现类**：`MedicineServiceImpl`

**依赖注入**：
- `StockRepository`：用于库存数据访问

---

### 6. 类：MedicineServiceImpl（药品管理服务实现）

**位置**：`com.example.demo.service.impl`

**继承关系**：`extends BaseServiceImpl<Medicine, Long, MedicineRepository> implements MedicineService`

**类注解说明**：
- `@Service`：Spring注解，标识为服务组件
- `@Transactional`：类级别事务注解

**依赖注入**：
- `@Autowired private StockRepository stockRepository`

**主要方法实现**：

| 方法签名                                                                 | 实现描述                                        | 业务逻辑                                            |
| ------------------------------------------------------------------------ | ----------------------------------------------- | --------------------------------------------------- |
| `findByMedicineCode(String medicineCode)`                                | 调用`repository.findByMedicineCode()`           | 直接查询                                            |
| `findAll(Pageable pageable)`                                             | 调用`repository.findAll(pageable)`              | 分页查询                                            |
| `findByStatus(Integer status, Pageable pageable)`                        | 调用`repository.findByStatus(status, pageable)` | 按状态分页查询                                      |
| `searchMedicines(String keyword)`                                        | 调用`repository.searchMedicines(keyword, 1)`    | 搜索启用状态的药品                                  |
| `findByCategoryId(Long categoryId)`                                      | 调用`repository.findByCategoryId()`             | 直接查询                                            |
| `updatePrice(Long id, BigDecimal retailPrice, BigDecimal purchasePrice)` | 更新药品价格                                    | 1. 根据ID查找药品<br>2. 更新价格字段<br>3. 保存药品 |
| `countByStatus(Integer status)`                                          | 调用`repository.countByStatus()`                | 统计查询                                            |
| `findSeasonalMedicines(Pageable pageable)`                               | 查找季节性药品                                  | 过滤`isSeasonal=true`的药品，内存分页               |
| `findPrescriptionMedicines(Pageable pageable)`                           | 查找处方药                                      | 过滤`isPrescription=true`的药品，内存分页           |
| `findByManufacturer(String manufacturer, Pageable pageable)`             | 根据生产厂家查找                                | 调用`repository.findByManufacturerContaining()`，内存分页 |
| `findLowStockMedicines(Pageable pageable)`                               | 查找库存不足的药品                              | 比较库存总量与预警阈值，内存分页                    |
| `getMedicineSalesStatistics(Long medicineId)`                            | 获取药品销售统计                                | 统计销售总量、销售趋势等                            |
| `getMedicinePurchaseStatistics(Long medicineId)`                         | 获取药品采购统计                                | 统计采购总量                                        |
| `batchUpdateStatus(List<Long> medicineIds, Integer status)`              | 批量更新药品状态                                | 批量查询并更新状态                                  |
| `findExpiringMedicines(int daysThreshold, Pageable pageable)`            | 查找即将过期的药品                              | 查询即将过期的库存关联的药品，内存分页              |
| `findByStorageRequirement(Integer storageRequirement, Pageable pageable)` | 根据存储要求查找药品                           | 过滤指定存储要求的药品，内存分页                    |
| `searchMedicinesWithPagination(String keyword, Pageable pageable)`       | 搜索药品（分页）                                | 调用`repository.searchMedicines()`，内存分页        |

---

### 7. 接口：PredictionResultService（预测管理服务）

**位置**：`com.example.demo.service`

**继承关系**：`extends BaseService<PredictionResult, Long>`

**类注解说明**：
- `@Transactional`：Spring注解，标识为事务管理接口

**描述**：预测结果业务逻辑服务接口，提供药品需求预测结果的CRUD操作及统计分析

**方法列表**：

| 方法签名                                                                                                                            | 返回类型                 | 参数                                                                                         | 描述                       | 返回值说明                                                                 | 失败情况返回值                                    |
| ----------------------------------------------------------------------------------------------------------------------------------- | ------------------------ | -------------------------------------------------------------------------------------------- | -------------------------- | -------------------------------------------------------------------------- | ------------------------------------------------- |
| `List<PredictionResult> findByMedicineId(Long medicineId)`                                                                          | `List<PredictionResult>` | `medicineId`: 药品ID                                                                         | 根据药品ID查找预测结果     | 返回指定药品的所有预测结果列表，若不存在则返回空列表                       | 返回空列表                                        |
| `List<PredictionResult> findByPredictionDate(LocalDate predictionDate)`                                                             | `List<PredictionResult>` | `predictionDate`: 预测日期                                                                   | 根据预测日期查找           | 返回指定日期的所有预测结果列表，若不存在则返回空列表                       | 返回空列表                                        |
| `PredictionResult findLatestByMedicineId(Long medicineId)`                                                                          | `PredictionResult`       | `medicineId`: 药品ID                                                                         | 查找某个药品最新的预测结果 | 返回指定药品的最新预测结果，按预测日期降序排列                             | 找不到时返回null                                  |
| `List<PredictionResult> findByPredictionDateRange(LocalDate startDate, LocalDate endDate)`                                          | `List<PredictionResult>` | `startDate`: 开始日期, `endDate`: 结束日期                                                   | 查找日期范围内的预测结果   | 返回指定日期范围内的所有预测结果列表，若不存在则返回空列表                 | 返回空列表                                        |
| `List<PredictionResult> findNeedReprediction(Double threshold)`                                                                     | `List<PredictionResult>` | `threshold`: 准确率阈值                                                                      | 查找需要重新预测的记录     | 返回准确率低于阈值或预测日期已过的预测结果列表，若不存在则返回空列表       | 返回空列表                                        |
| `Map<String, Double> getAverageAccuracyByModel()`                                                                                   | `Map<String, Double>`    | 无                                                                                           | 获取各模型的平均准确率     | 返回各预测模型的平均准确率映射，键为模型类型，值为平均准确率               | 无数据时返回空Map                                 |
| `PredictionResult generatePrediction(Long medicineId, String modelType, LocalDate predictionDate)`                                  | `PredictionResult`       | `medicineId`: 药品ID, `modelType`: 模型类型, `predictionDate`: 预测日期                      | 生成单个药品的预测结果     | 返回生成的预测结果对象，包含预测数量、置信区间、准确率和建议订购数量等信息 | 药品不存在返回null；API调用失败时使用本地算法生成 |
| `List<PredictionResult> generateBatchPredictions(List<Long> medicineIds, String modelType, LocalDate startDate, LocalDate endDate)` | `List<PredictionResult>` | `medicineIds`: 药品ID列表, `modelType`: 模型类型, `startDate`: 开始日期, `endDate`: 结束日期 | 批量生成预测结果           | 返回批量生成的预测结果列表，包含所有指定药品在指定日期范围内的预测结果     | 日期范围无效或无数据时返回空列表                  |
| `Map<Long, Integer> getRecommendedOrderQuantities(LocalDate targetDate)`                                                            | `Map<Long, Integer>`     | `targetDate`: 目标日期                                                                       | 获取所有药品的建议订购数量 | 返回所有启用药品的建议订购数量映射，键为药品ID，值为建议订购数量           | 无预测数据时尝试生成，生成失败时对应药品值为0     |

**实现类**：`PredictionResultServiceImpl`

**依赖注入**：
- `MedicineRepository`：用于药品数据访问
- `StockRepository`：用于库存数据访问

---

### 8. 类：PredictionResultServiceImpl（预测管理服务实现）

**位置**：`com.example.demo.service.impl`

**继承关系**：`extends BaseServiceImpl<PredictionResult, Long, PredictionResultRepository> implements PredictionResultService`

**类注解说明**：
- `@Service`：Spring注解，标识为服务组件
- `@Transactional`：类级别事务注解
- `@Slf4j`：Lombok日志注解
- `@Deprecated`：标记该类已弃用

**配置属性**：
| 属性名 | 默认值 | 描述 |
| ------ | ------ | ---- |
| `model.service.base-url` | `http://localhost:5101` | 模型服务端基础URL |
| `model.service.api-version` | `/api/v1` | 模型服务API版本 |
| `prediction.default-days` | `7` | 默认预测天数 |
| `prediction.confidence-interval-factor` | `0.2` | 置信区间因子 |
| `inventory.safety-stock-factor` | `0.5` | 安全库存因子 |

**依赖注入**：
- `@Autowired private RestTemplate restTemplate`
- `@Autowired private ObjectMapper objectMapper`
- `@Autowired private MedicineRepository medicineRepository`
- `@Autowired private StockRepository stockRepository`

**主要方法实现**：

| 方法签名                                                                                                     | 实现描述                                                          | 业务逻辑                                                                 | 备注                           |
| ------------------------------------------------------------------------------------------------------------ | ----------------------------------------------------------------- | ------------------------------------------------------------------------ | ------------------------------ |
| `findByMedicineId(Long medicineId)`                                                                          | 调用`repository.findByMedicineId()`                               | 直接查询                                                                 |                                |
| `findByPredictionDate(LocalDate predictionDate)`                                                             | 调用`repository.findByPredictionDate()`                           | 直接查询                                                                 |                                |
| `findLatestByMedicineId(Long medicineId)`                                                                    | 调用`repository.findFirstByMedicineIdOrderByPredictionDateDesc()` | 按日期降序取第一条                                                       |                                |
| `findByPredictionDateRange(LocalDate startDate, LocalDate endDate)`                                          | 调用`repository.findByPredictionDateBetween()`                    | 日期范围查询                                                             |                                |
| `findNeedReprediction(Double threshold)`                                                                     | 调用`repository.findNeedReprediction()`                           | 查找准确率低于阈值或预测日期已过的记录                                   |                                |
| `getAverageAccuracyByModel()`                                                                                | 调用`repository.findAverageAccuracyByModel()`，转换为Map          | 统计各模型平均准确率                                                     |                                |
| `generatePrediction(Long medicineId, String modelType, LocalDate predictionDate)`                            | 生成单个药品的预测结果                                            | 1. 查找药品<br>2. 调用模型端API<br>3. 失败时使用本地算法 | **已弃用** |
| `generateBatchPredictions(List<Long> medicineIds, String modelType, LocalDate startDate, LocalDate endDate)` | 批量生成预测结果                                                  | 调用模型端批量预测API，失败时使用本地算法                                | **已弃用** |
| `getRecommendedOrderQuantities(LocalDate targetDate)`                                                        | 获取所有药品的建议订购数量                                        | 1. 获取所有启用药品<br>2. 批量查询最新预测<br>3. 组装推荐数量映射        | **已弃用** |
| `checkModelServiceHealth()`                                                                                  | 检查模型端服务健康状态                                            | 发送健康检查请求                                                         |                                |
| `getModelServiceInfo()`                                                                                      | 获取模型端服务信息                                                | 返回服务URL、版本、健康状态等信息                                        |                                |

**辅助方法**：
- `callModelPredictionApi(Long medicineId, LocalDate predictionDate)`：调用模型端单个预测API
- `callBatchPredictionApi(List<Long> medicineIds, LocalDate startDate, LocalDate endDate)`：调用模型端批量预测API
- `getHistoricalPredictions(Long medicineId)`：获取历史预测记录
- `createPredictionResultFromModelResponse(...)`：从模型响应创建预测结果
- `createBatchPredictionsFromModelResponse(...)`：从批量响应创建预测结果
- `generateLocalPrediction(...)`：本地预测算法（API失败时使用）
- `generateLocalBatchPredictions(...)`：本地批量预测算法
- `calculateAccuracyRate(...)`：计算预测准确率
- `calculateLocalPredictedQuantity(...)`：本地算法计算预测数量
- `calculateRecommendedOrderQuantity(...)`：计算建议订购数量

---

### 9. 接口：PurchaseOrderService（采购管理服务）

**位置**：`com.example.demo.service`

**继承关系**：`extends BaseService<PurchaseOrder, Long>`

**类注解说明**：
- `@Transactional`：Spring注解，标识为事务管理接口

**描述**：采购订单业务逻辑服务接口，提供采购订单的CRUD操作及业务统计方法

**方法列表**：

| 方法签名                                                                                                        | 返回类型              | 参数                                                             | 描述                         | 返回值说明                                                                 | 失败情况返回值                       |
| --------------------------------------------------------------------------------------------------------------- | --------------------- | ---------------------------------------------------------------- | ---------------------------- | -------------------------------------------------------------------------- | ------------------------------------ |
| `PurchaseOrder findByOrderNo(String orderNo)`                                                                   | `PurchaseOrder`       | `orderNo`: 订单编号                                              | 根据订单号查找采购订单       | 返回指定订单号的采购订单对象                                               | 找不到时返回null                     |
| `Page<PurchaseOrder> findAll(Pageable pageable)`                                                                | `Page<PurchaseOrder>` | `pageable`: 分页参数                                             | 分页查询所有采购订单         | 返回包含所有采购订单的分页对象，包含分页信息和数据列表                     | 无数据时返回空Page对象               |
| `Page<PurchaseOrder> findByOrderStatus(Integer orderStatus, Pageable pageable)`                                 | `Page<PurchaseOrder>` | `orderStatus`: 订单状态, `pageable`: 分页参数                    | 根据订单状态分页查找         | 返回指定订单状态的采购订单分页对象，包含分页信息和数据列表                 | 无数据时返回空Page对象               |
| `Page<PurchaseOrder> findByMedicineId(Long medicineId, Pageable pageable)`                                      | `Page<PurchaseOrder>` | `medicineId`: 药品ID, `pageable`: 分页参数                       | 根据药品ID分页查找采购订单   | 返回指定药品的采购订单分页对象，包含分页信息和数据列表                     | 无数据时返回空Page对象               |
| `Page<PurchaseOrder> findPendingOrders(Pageable pageable)`                                                      | `Page<PurchaseOrder>` | `pageable`: 分页参数                                             | 查找待处理的采购订单         | 返回状态为0（待处理）的采购订单分页对象，包含分页信息和数据列表            | 无数据时返回空Page对象               |
| `Page<PurchaseOrder> findOverdueOrders(Pageable pageable)`                                                      | `Page<PurchaseOrder>` | `pageable`: 分页参数                                             | 查找过期的采购订单           | 返回预计到货日期已过且状态未完成的采购订单分页对象，包含分页信息和数据列表 | 无数据时返回空Page对象               |
| `Page<PurchaseOrder> searchOrders(String keyword, Pageable pageable)`                                           | `Page<PurchaseOrder>` | `keyword`: 关键词, `pageable`: 分页参数                          | 搜索采购订单                 | 返回根据关键词搜索的采购订单分页对象，包含分页信息和数据列表               | 无匹配数据时返回空Page对象           |
| `Page<PurchaseOrder> findBySupplierContaining(String supplier, Pageable pageable)`                              | `Page<PurchaseOrder>` | `supplier`: 供应商, `pageable`: 分页参数                         | 根据供应商模糊查找           | 返回供应商名称包含指定字符串的采购订单分页对象，包含分页信息和数据列表     | 无匹配数据时返回空Page对象           |
| `Page<PurchaseOrder> findByOrderTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable)` | `Page<PurchaseOrder>` | `startTime`: 开始时间, `endTime`: 结束时间, `pageable`: 分页参数 | 根据下单时间范围查找         | 返回指定时间范围内的采购订单分页对象，包含分页信息和数据列表               | 无数据时返回空Page对象               |
| `Integer getTotalPurchasedQuantity(Long medicineId)`                                                            | `Integer`             | `medicineId`: 药品ID                                             | 获取某个药品的已到货采购总量 | 返回指定药品的已到货采购总数量                                             | 无数据时返回0                        |
| `Double getTotalPurchaseAmountByPeriod(LocalDateTime startTime, LocalDateTime endTime)`                         | `Double`              | `startTime`: 开始时间, `endTime`: 结束时间                       | 获取时间段内的已到货采购总额 | 返回指定时间段内已到货采购订单的总金额                                     | 无数据时返回0.0                      |
| `PurchaseOrder createOrder(PurchaseOrder order, Long operatorId)`                                               | `PurchaseOrder`       | `order`: 采购订单, `operatorId`: 操作员ID                        | 创建采购订单                 | 返回创建成功的采购订单对象，包含生成的订单编号和状态                       | 订单参数无效或操作员不存在时返回null |
| `PurchaseOrder confirmOrder(Long orderId)`                                                                      | `PurchaseOrder`       | `orderId`: 订单ID                                                | 确认采购订单                 | 返回确认成功的采购订单对象，状态更新为1（已确认）                          | 订单不存在或状态不允许确认时返回null |
| `PurchaseOrder markAsArrived(Long orderId)`                                                                     | `PurchaseOrder`       | `orderId`: 订单ID                                                | 标记采购订单为已到货         | 返回标记成功的采购订单对象，状态更新为2（已到货），并记录实际到货时间      | 订单不存在或状态不允许标记时返回null |
| `PurchaseOrder cancelOrder(Long orderId)`                                                                       | `PurchaseOrder`       | `orderId`: 订单ID                                                | 取消采购订单                 | 返回取消成功的采购订单对象，状态更新为3（已取消）                          | 订单不存在或状态不允许取消时返回null |
| `Map<String, Object> getOrderStatistics()`                                                                      | `Map<String, Object>` | 无                                                               | 获取采购订单统计信息         | 返回采购订单的统计信息，包含总订单数、各状态订单数、总金额等数据           | 无数据时返回包含默认值的Map          |
| `Map<String, Long> countByStatus()`                                                                             | `Map<String, Long>`   | 无                                                               | 按状态统计采购订单数量       | 返回各状态的采购订单数量映射，键为状态值，值为对应状态的订单数             | 无数据时返回空Map                    |
| `Map<String, Object> getMonthlyStatistics(LocalDate startDate, LocalDate endDate)`                              | `Map<String, Object>` | `startDate`: 开始日期, `endDate`: 结束日期                       | 获取月度统计信息             | 返回指定日期范围内的月度采购统计信息，包含每月订单数、金额等数据           | 无数据时返回包含默认值的Map          |
| `Page<Map<String, Object>> getSupplierPurchaseStatistics(Pageable pageable)`                                    | `Page<Map>`           | `pageable`: 分页参数                                             | 获取供应商采购统计（分页）   | 返回各供应商的采购统计信息分页对象                                         | 返回空Page对象                       |
| `Page<PurchaseOrder> findUpcomingOrders(Pageable pageable)`                                                     | `Page<PurchaseOrder>` | `pageable`: 分页参数                                             | 查找即将到来的采购订单       | 返回预计到货日期临近的采购订单分页对象                                     | 返回空Page对象                       |
| `void batchConfirmOrders(List<Long> orderIds)`                                                                  | `void`                | `orderIds`: 订单ID列表                                           | 批量确认采购订单             | 无返回值                                                                   | 无                                   |
| `void batchCancelOrders(List<Long> orderIds)`                                                                   | `void`                | `orderIds`: 订单ID列表                                           | 批量取消采购订单             | 无返回值                                                                   | 无                                   |
| `Page<Map<String, Object>> getPurchaseSuggestions(Pageable pageable)`                                           | `Page<Map>`           | `pageable`: 分页参数                                             | 获取采购建议（分页）         | 返回基于库存和销售数据的采购建议分页对象                                   | 返回空Page对象                       |
| `Page<Map<String, Object>> getPurchaseByCategory(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable)` | `Page<Map>`      | `startDate`: 开始时间, `endDate`: 结束时间, `pageable`: 分页参数 | 按分类统计采购数据（分页）   | 返回各分类的采购统计信息分页对象                                           | 返回空Page对象                       |
| `Map<String, Object> getOrderDetailsWithMedicine(Long orderId)`                                                 | `Map<String, Object>` | `orderId`: 订单ID                                             | 获取订单详情（含药品信息）   | 返回包含订单和药品详细信息的映射                                           | 返回空Map                            |

**实现类**：`PurchaseOrderServiceImpl`

**依赖注入**：`UserService`：用于获取操作员信息

---

### 10. 类：PurchaseOrderServiceImpl（采购管理服务实现）

**位置**：`com.example.demo.service.impl`

**继承关系**：`extends BaseServiceImpl<PurchaseOrder, Long, PurchaseOrderRepository> implements PurchaseOrderService`

**类注解说明**：
- `@Service`：Spring注解，标识为服务组件
- `@Transactional`：类级别事务注解

**依赖注入**：
- `@Autowired private UserService userService`
- `@Autowired private MedicineRepository medicineRepository`
- `@Autowired private SaleRecordRepository saleRepository`
- `@Autowired private StockRepository stockRepository`

**主要方法实现**：

| 方法签名                                                                                    | 实现描述                                            | 业务逻辑                                                       |
| ------------------------------------------------------------------------------------------- | --------------------------------------------------- | -------------------------------------------------------------- |
| `findByOrderNo(String orderNo)`                                                             | 调用`repository.findByOrderNo()`                    | 直接查询                                                       |
| `findAll(Pageable pageable)`                                                                | 调用`repository.findAll(pageable)`                  | 分页查询                                                       |
| `findByOrderStatus(Integer orderStatus, Pageable pageable)`                                 | 内存分页处理                                        | 从查询结果中手动分页                                           |
| `findByMedicineId(Long medicineId, Pageable pageable)`                                      | 内存分页处理                                        | 从查询结果中手动分页                                           |
| `findPendingOrders(Pageable pageable)`                                                      | 内存分页处理                                        | 从查询结果中手动分页                                           |
| `findOverdueOrders(Pageable pageable)`                                                      | 内存分页处理                                        | 从查询结果中手动分页                                           |
| `getTotalPurchasedQuantity(Long medicineId)`                                                | 调用`repository.sumPurchasedQuantityByMedicineId()` | 统计已到货采购总量                                             |
| `getTotalPurchaseAmountByPeriod(LocalDateTime startTime, LocalDateTime endTime)`            | 调用`repository.sumTotalAmountByPeriod()`           | 统计时间段内已到货采购总额                                     |
| `createOrder(PurchaseOrder order, Long operatorId)`                                         | 创建采购订单                                        | 1. 设置操作员<br>2. 生成订单号<br>3. 计算总金额<br>4. 保存订单 |
| `confirmOrder(Long orderId)`                                                                | 确认采购订单                                        | 将待处理订单状态改为已确认                                     |
| `markAsArrived(Long orderId)`                                                               | 标记采购订单为已到货                                | 将订单状态改为已到货，设置实际到货时间                         |
| `cancelOrder(Long orderId)`                                                                 | 取消采购订单                                        | 将未到货订单状态改为已取消                                     |
| `getOrderStatistics()`                                                                      | 获取采购订单统计信息                                | 统计各状态订单数量、月度采购总额、过期订单数量                 |
| `searchOrders(String keyword, Pageable pageable)`                                           | 内存分页处理                                        | 从查询结果中手动分页                                           |
| `findBySupplierContaining(String supplier, Pageable pageable)`                              | 内存分页处理                                        | 从查询结果中手动分页                                           |
| `findByOrderTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable)` | 内存分页处理                                        | 从查询结果中手动分页                                           |
| `countByStatus()`                                                                           | 统计各状态订单数量                                  | 调用`repository.findByOrderStatus()`并计数                     |
| `getMonthlyStatistics(LocalDate startDate, LocalDate endDate)`                              | 获取月度统计信息                                    | 多维度统计：状态、金额、药品、供应商、月份等                   |
| `getSupplierPurchaseStatistics(Pageable pageable)`                                          | 获取供应商采购统计                                  | 调用`repository.findSupplierPurchaseStatistics()`，内存分页    |
| `findUpcomingOrders(Pageable pageable)`                                                     | 查找即将到来的订单                                  | 调用`repository.findUpcomingOrders()`，内存分页                |
| `batchConfirmOrders(List<Long> orderIds)`                                                   | 批量确认订单                                        | 遍历订单ID列表，确认待处理订单                                 |
| `batchCancelOrders(List<Long> orderIds)`                                                    | 批量取消订单                                        | 遍历订单ID列表，取消未到货订单                                 |
| `getPurchaseSuggestions(Pageable pageable)`                                                 | 获取采购建议                                        | 基于库存和销售数据生成采购建议，内存分页                       |
| `getPurchaseByCategory(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable)`  | 按分类统计采购                                      | 汇总各分类的采购数量和金额，内存分页                           |
| `getOrderDetailsWithMedicine(Long orderId)`                                                 | 获取订单详情                                        | 返回包含订单和药品详细信息的映射                               |

---

### 11. 接口：SaleRecordService（销售管理服务）

**位置**：`com.example.demo.service`

**继承关系**：`extends BaseService<SaleRecord, Long>`

**类注解说明**：
- `@Transactional`：Spring注解，标识为事务管理接口

**描述**：销售记录业务逻辑服务接口，提供销售记录的CRUD操作及销售统计分析

**方法列表**：

| 方法签名                                                                                        | 返回类型                    | 参数                                           | 描述                       | 返回值说明                                                                        | 失败情况返回值                           |
| ----------------------------------------------------------------------------------------------- | --------------------------- | ---------------------------------------------- | -------------------------- | --------------------------------------------------------------------------------- | ---------------------------------------- |
| `SaleRecord findByRecordNo(String recordNo)`                                                    | `SaleRecord`                | `recordNo`: 销售单号                           | 根据销售单号查找销售记录   | 返回指定销售单号的销售记录对象                                                    | 找不到时返回null                         |
| `Page<SaleRecord> findAll(Pageable pageable)`                                                   | `Page<SaleRecord>`          | `pageable`: 分页参数                           | 分页查询所有销售记录       | 返回包含所有销售记录的分页对象，包含分页信息和数据列表                            | 无数据时返回空Page对象                   |
| `List<SaleRecord> findBySaleTimeBetween(LocalDateTime startTime, LocalDateTime endTime)`        | `List<SaleRecord>`          | `startTime`: 开始时间, `endTime`: 结束时间     | 根据时间段查找销售记录     | 返回指定时间段内的销售记录列表，按销售时间排序                                    | 无数据时返回空列表                       |
| `List<SaleRecord> findByMedicineId(Long medicineId)`                                            | `List<SaleRecord>`          | `medicineId`: 药品ID                           | 根据药品ID查找销售记录     | 返回指定药品的所有销售记录列表，按销售时间降序排列                                | 无数据时返回空列表                       |
| `List<SaleRecord> findByOperatorId(Long operatorId)`                                            | `List<SaleRecord>`          | `operatorId`: 操作员ID                         | 根据操作员ID查找销售记录   | 返回指定操作员的所有销售记录列表，按销售时间降序排列                              | 无数据时返回空列表                       |
| `Double getTotalSalesByPeriod(LocalDateTime startTime, LocalDateTime endTime)`                  | `Double`                    | `startTime`: 开始时间, `endTime`: 结束时间     | 获取时间段内的销售总额     | 返回指定时间段内的销售总金额                                                      | 无数据时返回0.0                          |
| `Integer getTotalQuantityByMedicineId(Long medicineId)`                                         | `Integer`                   | `medicineId`: 药品ID                           | 获取某个药品的销售总量     | 返回指定药品的销售总数量                                                          | 无数据时返回0                            |
| `List<Map<String, Object>> getDailySalesReport(LocalDateTime startDate, LocalDateTime endDate)` | `List<Map<String, Object>>` | `startDate`: 开始日期, `endDate`: 结束日期     | 获取每日销售报表           | 返回指定日期范围内的每日销售报表，每个Map包含日期、销售额、销售数量等信息         | 无数据时返回空列表                       |
| `List<Map<String, Object>> getTopSellingMedicines(int limit, LocalDateTime startDate, LocalDateTime endDate)` | `List<Map>`     | `limit`: 限制数量, `startDate`: 开始日期, `endDate`: 结束日期 | 获取最畅销药品 | 返回指定日期范围内最畅销的药品列表，每个Map包含药品ID、销售数量、销售金额等信息 | 无数据时返回空列表                       |
| `SaleRecord createSaleRecord(SaleRecord saleRecord, Long operatorId)`                           | `SaleRecord`                | `saleRecord`: 销售记录, `operatorId`: 操作员ID | 创建销售记录               | 返回创建成功的销售记录对象，包含生成的销售单号和状态                              | 销售记录参数无效或操作员不存在时返回null |
| `Page<SaleRecord> findByCustomerType(Integer customerType, Pageable pageable)`                  | `Page<SaleRecord>`          | `customerType`: 顾客类型, `pageable`: 分页参数 | 根据顾客类型查找销售记录   | 返回指定顾客类型的销售记录分页对象                                                | 返回空Page对象                           |
| `Page<Map<String, Object>> getSalesByCategory(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable)` | `Page<Map>` | `startDate`: 开始日期, `endDate`: 结束日期, `pageable`: 分页参数 | 按分类统计销售数据 | 返回各分类的销售统计信息分页对象                                          | 返回空Page对象                           |
| `Page<Map<String, Object>> getSalesBySymptom(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable)` | `Page<Map>` | `startDate`: 开始日期, `endDate`: 结束日期, `pageable`: 分页参数 | 按症状统计销售数据 | 返回各症状的销售统计信息分页对象                                          | 返回空Page对象                           |
| `Map<String, Object> getOperatorSalesPerformance(LocalDateTime startDate, LocalDateTime endDate)` | `Map<String, Object>`   | `startDate`: 开始日期, `endDate`: 结束日期     | 获取操作员销售业绩         | 返回各操作员的销售业绩统计信息                                                    | 返回空Map                                |
| `Page<Map<String, Object>> getMonthlySalesTrend(int months, Pageable pageable)`                 | `Page<Map>`                 | `months`: 月份数, `pageable`: 分页参数         | 获取月度销售趋势           | 返回最近N个月的销售趋势数据分页对象                                               | 返回空Page对象                           |
| `Page<Map<String, Object>> getSalesPrediction(int days, Pageable pageable)`                     | `Page<Map>`                 | `days`: 天数, `pageable`: 分页参数             | 获取销售预测               | 返回未来N天的销售预测数据分页对象                                                 | 返回空Page对象                           |
| `Page<SaleRecord> findPrescriptionSales(Pageable pageable)`                                     | `Page<SaleRecord>`          | `pageable`: 分页参数                           | 查找处方药销售记录         | 返回处方药销售记录分页对象                                                        | 返回空Page对象                           |
| `Map<String, Object> getSalesStatisticsByPeriod(LocalDateTime startDate, LocalDateTime endDate)` | `Map<String, Object>`      | `startDate`: 开始日期, `endDate`: 结束日期     | 获取销售统计信息           | 返回指定日期范围内的销售统计信息，包含总额、记录数、平均金额等                    | 返回空Map                                |

**实现类**：`SaleRecordServiceImpl`

**依赖注入**：`UserService`：用于获取操作员信息

---

### 12. 类：SaleRecordServiceImpl（销售管理服务实现）

**位置**：`com.example.demo.service.impl`

**继承关系**：`extends BaseServiceImpl<SaleRecord, Long, SaleRecordRepository> implements SaleRecordService`

**类注解说明**：
- `@Service`：Spring注解，标识为服务组件
- `@Transactional`：类级别事务注解

**依赖注入**：`@Autowired private UserService userService`

**主要方法实现**：

| 方法签名                                                                | 实现描述                                      | 业务逻辑                                                         |
| ----------------------------------------------------------------------- | --------------------------------------------- | ---------------------------------------------------------------- |
| `findByRecordNo(String recordNo)`                                       | 调用`repository.findByRecordNo()`             | 直接查询                                                         |
| `findAll(Pageable pageable)`                                            | 调用`repository.findAll(pageable)`            | 分页查询                                                         |
| `findBySaleTimeBetween(LocalDateTime startTime, LocalDateTime endTime)` | 调用`repository.findBySaleTimeBetween()`      | 时间段查询                                                       |
| `findByMedicineId(Long medicineId)`                                     | 调用`repository.findByMedicineId()`           | 直接查询                                                         |
| `findByOperatorId(Long operatorId)`                                     | 调用`repository.findByOperatorId()`           | 直接查询                                                         |
| `getTotalSalesByPeriod(LocalDateTime startTime, LocalDateTime endTime)` | 调用`repository.sumTotalAmountByPeriod()`     | 统计时间段销售总额                                               |
| `getTotalQuantityByMedicineId(Long medicineId)`                         | 调用`repository.sumQuantityByMedicineId()`    | 统计药品销售总量                                                 |
| `getDailySalesReport(LocalDateTime startDate, LocalDateTime endDate)`   | 调用`repository.findDailySales()`，格式化日期 | 统计每天的销售数据                                               |
| `getTopSellingMedicines(int limit, LocalDateTime startDate, LocalDateTime endDate)` | 调用`repository.findTopSellingMedicines()` | 获取最畅销药品                                                   |
| `createSaleRecord(SaleRecord saleRecord, Long operatorId)`              | 创建销售记录                                  | 1. 设置操作员<br>2. 生成销售单号<br>3. 计算总金额<br>4. 保存记录 |
| `findByCustomerType(Integer customerType, Pageable pageable)`           | 根据顾客类型查找                              | 调用`repository.findByCustomerType()`，内存分页                  |
| `getSalesByCategory(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable)` | 按分类统计销售数据                  | 汇总各分类的销售数据，内存分页                                   |
| `getSalesBySymptom(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable)` | 按症状统计销售数据                   | 调用`repository.findSalesBySymptom()`，内存分页                  |
| `getOperatorSalesPerformance(LocalDateTime startDate, LocalDateTime endDate)` | 获取操作员销售业绩                      | 调用`repository.findOperatorSalesPerformance()`                  |
| `getMonthlySalesTrend(int months, Pageable pageable)`                   | 获取月度销售趋势                              | 调用`repository.findDailySalesAmount()`，内存分页                |
| `getSalesPrediction(int days, Pageable pageable)`                       | 获取销售预测                                  | 简化实现，返回模拟预测数据，内存分页                             |
| `findPrescriptionSales(Pageable pageable)`                              | 查找处方药销售                                | 过滤`isRx=true`的销售记录，内存分页                              |
| `getSalesStatisticsByPeriod(LocalDateTime startDate, LocalDateTime endDate)` | 获取销售统计信息                         | 统计销售总额、记录数、平均金额等                                 |

---

### 13. 接口：StockService（库存管理服务）

**位置**：`com.example.demo.service`

**继承关系**：`extends BaseService<Stock, Long>`

**类注解说明**：
- `@Transactional`：Spring注解，标识为事务管理接口

**描述**：库存信息业务逻辑服务接口，提供库存数据的CRUD操作及库存管理相关查询

**方法列表**：

| 方法签名                                                                                              | 返回类型              | 参数                                                                                      | 描述                         | 返回值说明                                                             | 失败情况返回值                  |
| ----------------------------------------------------------------------------------------------------- | --------------------- | ----------------------------------------------------------------------------------------- | ---------------------------- | ---------------------------------------------------------------------- | ------------------------------- |
| `Page<Stock> findByMedicineId(Long medicineId, Pageable pageable)`                                    | `Page<Stock>`         | `medicineId`: 药品ID, `pageable`: 分页参数                                                | 根据药品ID分页查找库存       | 返回指定药品的库存分页对象，包含分页信息和数据列表                     | 无数据时返回空Page对象          |
| `Integer getTotalStock(Long medicineId)`                                                              | `Integer`             | `medicineId`: 药品ID                                                                      | 获取某个药品的总库存量       | 返回指定药品的总库存量，包括所有批次的库存数量总和                     | 药品不存在或无库存时返回0       |
| `Page<Stock> getExpiringStock(LocalDate startDate, LocalDate endDate, Pageable pageable)`             | `Page<Stock>`         | `startDate`: 开始日期, `endDate`: 结束日期, `pageable`: 分页参数                          | 获取即将过期的库存           | 返回指定日期范围内即将过期的库存分页对象，包含分页信息和数据列表       | 无数据时返回空Page对象          |
| `Page<Stock> getLowStock(Pageable pageable)`                                                          | `Page<Stock>`         | `pageable`: 分页参数                                                                      | 获取库存不足的药品           | 返回库存数量低于预警阈值的库存分页对象，包含分页信息和数据列表         | 无数据时返回空Page对象          |
| `Map<Long, Integer> getLowStockSummary()`                                                             | `Map<Long, Integer>`  | 无                                                                                        | 获取库存不足药品的汇总信息   | 返回库存不足药品的汇总映射，键为药品ID，值为当前库存数量               | 无库存不足药品时返回空Map       |
| `void reduceStock(Long medicineId, Integer quantity)`                                                 | `void`                | `medicineId`: 药品ID, `quantity`: 减少数量                                                | 减少库存（先进先出）         | 无返回值，使用先进先出原则减少库存                                     | 库存不足时抛出异常              |
| `void increaseStock(Long medicineId, Integer quantity, String batchNumber, LocalDate expirationDate)` | `void`                | `medicineId`: 药品ID, `quantity`: 增加数量, `batchNumber`: 批号, `expirationDate`: 有效期 | 增加库存                     | 无返回值，根据批次号和有效期增加库存                                   | 药品不存在时抛出异常            |
| `boolean checkStockAvailability(Long medicineId, Integer requiredQuantity)`                           | `boolean`             | `medicineId`: 药品ID, `requiredQuantity`: 需求数量                                        | 检查库存是否足够             | 返回库存是否足够的布尔值，true表示库存充足，false表示库存不足          | 药品不存在时返回false           |
| `Page<Stock> findByStatus(Integer status, Pageable pageable)`                                         | `Page<Stock>`         | `status`: 状态, `pageable`: 分页参数                                                      | 根据状态分页查找库存         | 返回指定状态的库存分页对象，包含分页信息和数据列表                     | 无数据时返回空Page对象          |
| `Page<Stock> findByBatchNumber(String batchNumber, Pageable pageable)`                                | `Page<Stock>`         | `batchNumber`: 批号, `pageable`: 分页参数                                                 | 根据批号分页查找库存         | 返回指定批号的库存分页对象，包含分页信息和数据列表                     | 无数据时返回空Page对象          |
| `Page<Stock> findByShelfLocation(String shelfLocation, Pageable pageable)`                            | `Page<Stock>`         | `shelfLocation`: 货架位置, `pageable`: 分页参数                                           | 根据货架位置分页查找库存     | 返回指定货架位置的库存分页对象，包含分页信息和数据列表                 | 无数据时返回空Page对象          |
| `List<Stock> findExpiringWithinDays(int days)`                                                        | `List<Stock>`         | `days`: 天数                                                                              | 查找指定天数内即将过期的库存 | 返回指定天数内即将过期的库存列表，按有效期排序                         | 无数据时返回空列表              |
| `Map<String, Object> getStockStatisticsByMedicine(Long medicineId)`                                   | `Map<String, Object>` | `medicineId`: 药品ID                                                                      | 获取药品库存统计信息         | 返回指定药品的库存统计信息，包含总库存量、批次数量、即将过期数量等数据 | 药品不存在时返回包含默认值的Map |
| `Page<Stock> findByMedicineIdAndBatchNumber(Long medicineId, String batchNumber, Pageable pageable)`  | `Page<Stock>`         | `medicineId`: 药品ID, `batchNumber`: 批号, `pageable`: 分页参数                           | 根据药品ID和批号联合查找     | 返回指定药品ID和批号的库存分页对象，包含分页信息和数据列表             | 无数据时返回空Page对象          |
| `Page<Stock> findExpiredStock(Pageable pageable)`                                                     | `Page<Stock>`         | `pageable`: 分页参数                                                                      | 查找已过期的库存             | 返回已过期的库存分页对象，包含分页信息和数据列表                       | 无数据时返回空Page对象          |
| `Page<Stock> findNearExpiryStock(int days, Pageable pageable)`                                        | `Page<Stock>`         | `days`: 天数, `pageable`: 分页参数                                                        | 查找即将过期的库存           | 返回指定天数内即将过期的库存分页对象，包含分页信息和数据列表           | 无数据时返回空Page对象          |
| `Double calculateStockTurnoverRate(String period)`                                                    | `Double`              | `period`: 时间段(week/month/quarter/year)                                                 | 计算库存周转率               | 返回指定时间段内的库存周转率                                           | 无数据时返回0.0                 |
| `Double calculateTotalStockValue()`                                                                   | `Double`              | 无                                                                                        | 计算库存总价值               | 返回所有库存的总价值                                                   | 无数据时返回0.0                 |
| `Map<String, Object> getStockValueByCategory()`                                                       | `Map<String, Object>` | 无                                                                                        | 按分类获取库存价值           | 返回各分类的库存价值映射                                               | 返回空Map                       |
| `Page<Stock> getStockAlerts(Pageable pageable)`                                                       | `Page<Stock>`         | `pageable`: 分页参数                                                                      | 获取库存预警信息             | 返回低库存和即将过期库存的预警分页对象                                 | 返回空Page对象                  |
| `void transferStock(Long fromStockId, Long toStockId, Integer quantity)`                              | `void`                | `fromStockId`: 转出库存ID, `toStockId`: 转入库存ID, `quantity`: 数量                      | 库存调拨                     | 无返回值，将库存从一个批次转移到另一个批次                             | 库存不足时抛出异常              |
| `void setMinimumStockLevel(Long medicineId, Integer minLevel)`                                        | `void`                | `medicineId`: 药品ID, `minLevel`: 最小库存级别                                            | 设置最小库存级别             | 无返回值，设置药品的最小预警库存级别                                   | 无                              |
| `Map<Long, Object> getStockInventoryReport()`                                                         | `Map<Long, Object>`   | 无                                                                                        | 获取库存盘点报告             | 返回所有库存的盘点信息映射                                             | 返回空Map                       |
| `Page<Stock> findByStorageCondition(Integer condition, Pageable pageable)`                            | `Page<Stock>`         | `condition`: 存储条件, `pageable`: 分页参数                                               | 根据存储条件查找库存         | 返回指定存储条件的库存分页对象                                         | 返回空Page对象                  |
| `Page<Stock> findByShelfLocationContaining(String location, Pageable pageable)`                       | `Page<Stock>`         | `location`: 货架位置关键词, `pageable`: 分页参数                                          | 根据货架位置模糊查找库存     | 返回货架位置包含关键词的库存分页对象                                   | 返回空Page对象                  |

**实现类**：`StockServiceImpl`

**依赖注入**：
- `MedicineRepository`：用于药品数据访问

---

### 14. 类：StockServiceImpl（库存管理服务实现）

**位置**：`com.example.demo.service.impl`

**继承关系**：`extends BaseServiceImpl<Stock, Long, StockRepository> implements StockService`

**类注解说明**：
- `@Service`：Spring注解，标识为服务组件
- `@Transactional`：类级别事务注解

**依赖注入**：
- `@Autowired private MedicineRepository medicineRepository`

**主要方法实现**：

| 方法签名                                                                                         | 实现描述                                          | 业务逻辑                                   |
| ------------------------------------------------------------------------------------------------ | ------------------------------------------------- | ------------------------------------------ |
| `findByMedicineId(Long medicineId, Pageable pageable)`                                           | 内存分页处理                                      | 查询并过滤正常状态的库存                   |
| `getTotalStock(Long medicineId)`                                                                 | 调用`repository.sumQuantityByMedicineId()`        | 统计正常状态库存总量                       |
| `getExpiringStock(LocalDate startDate, LocalDate endDate, Pageable pageable)`                    | 内存分页处理                                      | 查询并过滤正常状态的即将过期库存           |
| `getLowStock(Pageable pageable)`                                                                 | 内存分页处理                                      | 查询并过滤正常状态的库存不足记录           |
| `getLowStockSummary()`                                                                           | 调用`repository.findLowStockSummary()`，转换为Map | 获取库存不足药品的汇总信息                 |
| `reduceStock(Long medicineId, Integer quantity)`                                                 | 减少库存                                          | 按批次减少库存数量，先进先出原则           |
| `increaseStock(Long medicineId, Integer quantity, String batchNumber, LocalDate expirationDate)` | 增加库存                                          | 创建新的库存批次或更新现有批次             |
| `checkStockAvailability(Long medicineId, Integer requiredQuantity)`                              | 检查库存是否足够                                  | 比较总库存和需求数量                       |
| `findByStatus(Integer status, Pageable pageable)`                                                | 内存分页处理                                      | 直接查询并分页                             |
| `findByShelfLocation(String shelfLocation, Pageable pageable)`                                   | 内存分页处理                                      | 直接查询并分页                             |
| `findByBatchNumber(String batchNumber, Pageable pageable)`                                       | 内存分页处理                                      | 直接查询并分页                             |
| `findExpiringWithinDays(int days)`                                                               | 查找指定天数内即将过期的库存                      | 调用`repository.findExpiringStock()`并过滤 |
| `getStockStatisticsByMedicine(Long medicineId)`                                                  | 获取药品库存统计信息                              | 多维度统计库存信息                         |
| `findByMedicineIdAndBatchNumber(Long medicineId, String batchNumber, Pageable pageable)`         | 内存分页处理                                      | 联合查询药品ID和批号                       |
| `findExpiredStock(Pageable pageable)`                                                            | 内存分页处理                                      | 查找已过期的库存                           |
| `findNearExpiryStock(int days, Pageable pageable)`                                               | 内存分页处理                                      | 查找即将过期的库存                         |
| `calculateStockTurnoverRate(String period)`                                                      | 计算库存周转率                                    | 根据时间段计算平均周转率                   |
| `calculateTotalStockValue()`                                                                     | 计算库存总价值                                    | 调用`repository.calculateTotalStockValue()` |
| `getStockValueByCategory()`                                                                      | 按分类获取库存价值                                | 调用`repository.calculateStockValueByCategory()` |
| `getStockAlerts(Pageable pageable)`                                                              | 获取库存预警信息                                  | 合并低库存和即将过期预警，内存分页         |
| `transferStock(Long fromStockId, Long toStockId, Integer quantity)`                              | 库存调拨                                          | 减少转出库存，增加转入库存                 |
| `setMinimumStockLevel(Long medicineId, Integer minLevel)`                                        | 设置最小库存级别                                  | 更新药品所有批次的预警数量                 |
| `getStockInventoryReport()`                                                                      | 获取库存盘点报告                                  | 返回所有库存的详细信息映射                 |
| `findByStorageCondition(Integer condition, Pageable pageable)`                                   | 根据存储条件查找库存                              | 根据药品存储要求过滤，内存分页             |
| `findByShelfLocationContaining(String location, Pageable pageable)`                              | 根据货架位置模糊查找                              | 调用`repository.findByShelfLocationContaining()` |

---

### 15. 接口：SymptomService（症状管理服务）

**位置**：`com.example.demo.service`

**继承关系**：`extends BaseService<Symptom, Integer>`

**类注解说明**：
- `@Transactional`：Spring注解，标识为事务管理接口

**描述**：症状业务逻辑服务接口，提供症状数据的CRUD操作及查询方法

**方法列表**：

| 方法签名                                                          | 返回类型        | 参数                                    | 描述                           | 返回值说明                                                  | 失败情况返回值             |
| ----------------------------------------------------------------- | --------------- | --------------------------------------- | ------------------------------ | ----------------------------------------------------------- | -------------------------- |
| `Symptom findByName(String name)`                                 | `Symptom`       | `name`: 症状名称                        | 根据名称查找症状               | 返回指定名称的症状对象                                      | 找不到时返回null           |
| `List<Symptom> findByNameContaining(String name)`                 | `List<Symptom>` | `name`: 症状名称                        | 根据名称模糊查询               | 返回名称包含指定字符串的症状列表                            | 无匹配时返回空列表         |
| `boolean existsByName(String name)`                               | `boolean`       | `name`: 症状名称                        | 检查症状名称是否存在           | 返回症状名称是否存在的布尔值，true表示存在，false表示不存在 | 无特殊情况，始终返回布尔值 |
| `List<Symptom> findByDescriptionContaining(String description)`   | `List<Symptom>` | `description`: 描述                     | 根据描述模糊查询               | 返回描述包含指定字符串的症状列表                            | 无匹配时返回空列表         |
| `List<Symptom> searchSymptoms(String keyword)`                    | `List<Symptom>` | `keyword`: 关键词                       | 搜索症状（名称或描述模糊匹配） | 返回名称或描述包含指定关键词的症状列表                      | 无匹配时返回空列表         |
| `Page<Symptom> findAll(Pageable pageable)`                        | `Page<Symptom>` | `pageable`: 分页参数                    | 分页查询所有症状               | 返回包含所有症状的分页对象，包含分页信息和数据列表          | 无数据时返回空Page对象     |
| `Page<Symptom> searchSymptoms(String keyword, Pageable pageable)` | `Page<Symptom>` | `keyword`: 关键词, `pageable`: 分页参数 | 搜索症状并分页                 | 返回根据关键词搜索的症状分页对象，包含分页信息和数据列表    | 无匹配时返回空Page对象     |
| `List<Symptom> saveAll(List<Symptom> symptoms)`                   | `List<Symptom>` | `symptoms`: 症状列表                    | 批量保存症状                   | 返回批量保存成功的症状列表                                  | 保存失败时抛出异常         |
| `long countAll()`                                                 | `long`          | 无                                      | 统计所有症状数量               | 返回所有症状的总数量                                        | 无数据时返回0              |
| `Page<Symptom> findByMedicineId(Long medicineId, Pageable pageable)` | `Page<Symptom>` | `medicineId`: 药品ID, `pageable`: 分页参数 | 根据药品ID查找关联症状    | 返回指定药品关联的症状分页对象                              | 返回空Page对象             |
| `Page<Symptom> findMostCommonSymptoms(int limit, LocalDateTime start, LocalDateTime end, Pageable pageable)` | `Page<Symptom>` | `limit`: 限制数量, `start`: 开始时间, `end`: 结束时间, `pageable`: 分页参数 | 查找最常见的症状 | 返回指定时间段内最常见的症状分页对象                      | 返回空Page对象             |
| `Map<String, Object> getSymptomUsageStatistics(Integer symptomId)` | `Map<String, Object>` | `symptomId`: 症状ID               | 获取症状使用统计信息           | 返回症状的药品关联数量、销售记录数量、销售金额等统计信息    | 返回空Map                  |
| `Page<Symptom> findSymptomsWithMedicines(Pageable pageable)`      | `Page<Symptom>` | `pageable`: 分页参数                    | 查找有关联药品的症状           | 返回至少关联一个药品的症状分页对象                          | 返回空Page对象             |
| `Page<Symptom> findBySaleRecordId(Long saleRecordId, Pageable pageable)` | `Page<Symptom>` | `saleRecordId`: 销售记录ID, `pageable`: 分页参数 | 根据销售记录查找关联症状 | 返回指定销售记录关联的症状分页对象                        | 返回空Page对象             |

**实现类**：`SymptomServiceImpl`

---

### 16. 类：SymptomServiceImpl（症状管理服务实现）

**位置**：`com.example.demo.service.impl`

**继承关系**：`extends BaseServiceImpl<Symptom, Integer, SymptomRepository> implements SymptomService`

**类注解说明**：
- `@Service`：Spring注解，标识为服务组件
- `@Transactional`：类级别事务注解

**依赖注入**：
- `@Autowired private MedicineRepository medicineRepository`
- `@Autowired private SaleRecordRepository saleRecordRepository`

**主要方法实现**：

| 方法签名                                            | 实现描述                                       | 业务逻辑             |
| --------------------------------------------------- | ---------------------------------------------- | -------------------- |
| `findByName(String name)`                           | 调用`repository.findByName()`                  | 直接查询             |
| `findByNameContaining(String name)`                 | 调用`repository.findByNameContaining()`        | 模糊查询             |
| `existsByName(String name)`                         | 调用`repository.existsByName()`                | 存在性检查           |
| `findByDescriptionContaining(String description)`   | 调用`repository.findByDescriptionContaining()` | 模糊查询             |
| `searchSymptoms(String keyword)`                    | 调用`repository.searchSymptoms()`              | 名称或描述模糊匹配   |
| `findAll(Pageable pageable)`                        | 调用`repository.findAll(pageable)`             | 分页查询             |
| `searchSymptoms(String keyword, Pageable pageable)` | 内存分页处理                                   | 手动分页             |
| `saveAll(List<Symptom> symptoms)`                   | 调用`repository.saveAll()`                     | 批量保存             |
| `countAll()`                                        | 调用`repository.count()`                       | 统计数量             |
| `save(Symptom symptom)`                             | 保存症状（名称唯一性检查）                     | 检查症状名称是否重复 |
| `deleteAll(List<Integer> ids)`                      | 批量删除症状                                   | 循环调用删除         |
| `findByMedicineId(Long medicineId, Pageable pageable)` | 根据药品ID查找关联症状                      | 查询药品关联的症状，内存分页 |
| `findMostCommonSymptoms(int limit, LocalDateTime start, LocalDateTime end, Pageable pageable)` | 查找最常见的症状 | 调用`repository.findMostCommonSymptoms()`，内存分页 |
| `getSymptomUsageStatistics(Integer symptomId)`      | 获取症状使用统计信息                           | 统计药品关联数量、销售记录数量、销售金额等 |
| `findSymptomsWithMedicines(Pageable pageable)`      | 查找有关联药品的症状                           | 检查每个症状的药品关联数量，内存分页 |
| `findBySaleRecordId(Long saleRecordId, Pageable pageable)` | 根据销售记录查找关联症状              | 查询销售记录关联的症状，内存分页 |

---

### 17. 接口：UserService（用户管理服务）

**位置**：`com.example.demo.service`

**继承关系**：`extends BaseService<User, Long>`

**类注解说明**：
- `@Transactional`：类级别事务注解

**描述**：用户信息业务逻辑服务接口，提供用户数据的CRUD操作及用户管理相关查询

**方法列表**：

| 方法签名                                                      | 返回类型     | 参数                                    | 描述                   | 返回值说明                                                | 失败情况返回值             |
| ------------------------------------------------------------- | ------------ | --------------------------------------- | ---------------------- | --------------------------------------------------------- | -------------------------- |
| `User findByUsername(String username)`                        | `User`       | `username`: 用户名                      | 根据用户名查找用户     | 返回指定用户名的用户对象                                  | 找不到时返回null           |
| `User login(String username, String password)`                | `User`       | `username`: 用户名, `password`: 密码    | 用户登录验证           | 登录成功返回用户对象，包含用户信息（不含密码）            | 用户名或密码错误时返回null |
| `User matchPassword(User user, String password)`              | `User`       | `user`: 用户对象, `password`: 密码      | 验证用户密码           | 密码验证成功返回用户对象，失败返回null                    | 密码错误时返回null         |
| `User updatePassword(User user)`                              | `User`       | `user`: 用户对象                        | 更新用户密码           | 返回更新密码后的用户对象                                  | 更新失败时抛出异常         |
| `Page<User> findAll(Pageable pageable)`                       | `Page<User>` | `pageable`: 分页参数                    | 分页查询所有用户       | 返回包含所有用户的分页对象，包含分页信息和数据列表        | 无数据时返回空Page对象     |
| `List<User> findByRole(String role)`                          | `List<User>` | `role`: 用户角色                        | 根据角色查找用户       | 返回指定角色的用户列表                                    | 无匹配时返回空列表         |
| `User changeStatus(Long id, Integer status)`                  | `User`       | `id`: 用户ID, `status`: 状态            | 修改用户状态           | 返回修改状态后的用户对象                                  | 用户不存在时返回null       |
| `boolean existsByUsername(String username)`                   | `boolean`    | `username`: 用户名                      | 检查用户名是否存在     | 返回用户名是否存在的布尔值，true表示存在，false表示不存在 | 无特殊情况，始终返回布尔值 |
| `Page<User> findByKeyword(String keyword, Pageable pageable)` | `Page<User>` | `keyword`: 关键词, `pageable`: 分页参数 | 根据关键词搜索用户     | 返回根据关键词搜索的用户分页对象，包含分页信息和数据列表  | 无匹配时返回空Page对象     |
| `int countByUserStatus(Integer userStatus)`                   | `int`        | `userStatus`: 用户状态                  | 统计指定状态的用户数量 | 返回指定状态的用户数量                                    | 无数据时返回0              |
| `int countByRole(String role)`                                | `int`        | `role`: 用户角色                        | 统计指定角色的用户数量 | 返回指定角色的用户数量                                    | 无数据时返回0              |
| `int countAll()`                                              | `int`        | 无                                      | 统计所有用户数量       | 返回所有用户的总数量                                      | 无数据时返回0              |

**实现类**：`UserServiceImpl`

**依赖注入**：`PasswordEncoder`：用于密码加密和验证

---

### 18. 类：UserServiceImpl（用户管理服务实现）

**位置**：`com.example.demo.service.impl`

**继承关系**：`extends BaseServiceImpl<User, Long, UserRepository> implements UserService`

**类注解说明**：
- `@Service`：Spring注解，标识为服务组件
- `@Transactional`：类级别事务注解

**依赖注入**：`@Autowired private PasswordEncoder passwordEncoder`

**主要方法实现**：

| 方法签名                                           | 实现描述                                 | 业务逻辑                              |
| -------------------------------------------------- | ---------------------------------------- | ------------------------------------- |
| `updatePassword(User user)`                        | 更新用户密码（加密）                     | 检查密码是否已加密，未加密则加密      |
| `save(User user)`                                  | 保存用户（密码加密）                     | 密码加密后保存                        |
| `matchPassword(User user, String password)`        | 验证用户密码                             | 使用`PasswordEncoder`验证密码         |
| `findByUsername(String username)`                  | 调用`repository.findByUsername()`        | 直接查询                              |
| `login(String username, String password)`          | 用户登录验证                             | 验证用户名、密码和状态                |
| `findAll(Pageable pageable)`                       | 调用`repository.findAll(pageable)`       | 分页查询                              |
| `findByRole(String role)`                          | 调用`repository.findByRole()`            | 直接查询                              |
| `changeStatus(Long id, Integer status)`            | 修改用户状态                             | 更新用户状态字段                      |
| `existsByUsername(String username)`                | 调用`repository.existsByUsername()`      | 存在性检查                            |
| `findByKeyword(String keyword, Pageable pageable)` | 调用`repository.searchUsers()`，手动分页 | 按用户名或真实姓名搜索                |
| `countByRole(String role)`                         | 统计指定角色的用户数量                   | 调用`repository.findByRole()`并计数   |
| `countByUserStatus(Integer userStatus)`            | 统计指定状态的用户数量                   | 调用`repository.findByStatus()`并计数 |
| `countAll()`                                       | 统计所有用户数量                         | 调用`repository.findAll()`并计数      |

**辅助方法**：
- `isEncrypted(String password)`：检查密码是否已加密（BCrypt格式）

---

### 事务管理说明

1. **类级别事务**：
   - 所有服务实现类都有`@Transactional`类级别注解
   - 默认使用读写事务

2. **方法级别事务**：
   - 查询方法：标记为`@Transactional(readOnly = true)`
   - 修改方法：标记为`@Transactional`
   - 批量操作：标记为`@Transactional`，确保原子性

3. **事务传播行为**：
   - 默认使用`REQUIRED`传播行为
   - 同一事务内的方法调用共享同一个事务

4. **异常处理**：
   - 运行时异常会触发事务回滚
   - 检查异常不会自动回滚事务

### 依赖注入说明

1. **构造函数注入**：
   - 所有服务实现类使用构造函数注入Repository
   - 基础服务实现类通过`super(repository)`传递依赖

2. **字段注入**：
   - 部分服务使用`@Autowired`字段注入额外依赖（如`PredictionResultServiceImpl`、`PurchaseOrderServiceImpl`等）
   - 推荐使用构造函数注入以提高可测试性

### 新增功能说明

根据代码分析，本次更新新增以下功能：

1. **分类管理**：
   - `findActiveCategories`：查找启用的分类（分页）
   - `findSubcategoriesByParentId`：根据父分类ID查找子分类（分页）
   - `getCategoryHierarchy`：获取分类层级结构
   - `findCategoriesWithMedicines`：查找有关联药品的分类
   - `getCategoryStatistics`：获取分类统计信息

2. **药品管理**：
   - `findSeasonalMedicines`：查找季节性药品
   - `findPrescriptionMedicines`：查找处方药
   - `findByManufacturer`：根据生产厂家查找
   - `findLowStockMedicines`：查找库存不足的药品
   - `getMedicineSalesStatistics`：获取药品销售统计
   - `getMedicinePurchaseStatistics`：获取药品采购统计
   - `batchUpdateStatus`：批量更新药品状态
   - `findExpiringMedicines`：查找即将过期的药品
   - `findByStorageRequirement`：根据存储要求查找
   - `searchMedicinesWithPagination`：搜索药品（分页）

3. **预测管理**：
   - 添加了模型端API调用支持
   - 添加了配置属性支持
   - 添加了健康检查方法
   - 标记为`@Deprecated`，表示该实现将被替换

4. **采购管理**：
   - `getSupplierPurchaseStatistics`：获取供应商采购统计
   - `findUpcomingOrders`：查找即将到来的订单
   - `batchConfirmOrders`：批量确认订单
   - `batchCancelOrders`：批量取消订单
   - `getPurchaseSuggestions`：获取采购建议
   - `getPurchaseByCategory`：按分类统计采购
   - `getOrderDetailsWithMedicine`：获取订单详情（含药品信息）

5. **销售管理**：
   - `findByCustomerType`：根据顾客类型查找
   - `getSalesByCategory`：按分类统计销售
   - `getSalesBySymptom`：按症状统计销售
   - `getOperatorSalesPerformance`：获取操作员销售业绩
   - `getMonthlySalesTrend`：获取月度销售趋势
   - `getSalesPrediction`：获取销售预测
   - `findPrescriptionSales`：查找处方药销售
   - `getSalesStatisticsByPeriod`：获取销售统计信息

6. **库存管理**：
   - `calculateStockTurnoverRate`：计算库存周转率
   - `calculateTotalStockValue`：计算库存总价值
   - `getStockValueByCategory`：按分类获取库存价值
   - `getStockAlerts`：获取库存预警信息
   - `transferStock`：库存调拨
   - `setMinimumStockLevel`：设置最小库存级别
   - `getStockInventoryReport`：获取库存盘点报告
   - `findByStorageCondition`：根据存储条件查找
   - `findByShelfLocationContaining`：根据货架位置模糊查找

7. **症状管理**：
   - `findByMedicineId`：根据药品ID查找关联症状
   - `findMostCommonSymptoms`：查找最常见的症状
   - `getSymptomUsageStatistics`：获取症状使用统计
   - `findSymptomsWithMedicines`：查找有关联药品的症状
   - `findBySaleRecordId`：根据销售记录查找关联症状

### 业务逻辑说明

1. **分类管理**：
   - 支持树形结构分类
   - 删除分类前检查关联药品

2. **药品管理**：
   - 支持多条件搜索
   - 药品编码唯一性检查
   - 支持季节性、处方药等特殊属性筛选

3. **预测管理**：
   - 支持多种预测模型
   - 批量生成预测结果
   - 智能推荐订购数量
   - **注意**：该实现已标记为`@Deprecated`，将被新的实现替换

4. **采购管理**：
   - 订单状态流转控制
   - 自动生成订单号
   - 采购统计功能
   - 批量操作支持
   - 采购建议生成

5. **销售管理**：
   - 自动生成销售单号
   - 销售统计分析
   - 畅销药品排名
   - 多维度销售统计（分类、症状、操作员等）

6. **库存管理**：
   - 库存预警功能
   - 先进先出库存减少
   - 批次库存管理
   - 库存周转率计算
   - 库存调拨功能

7. **用户管理**：
   - 密码加密存储
   - 登录验证
   - 用户状态管理

8. **症状管理**：
   - 症状名称唯一性检查
   - 多条件搜索
   - 批量操作支持
   - 症状使用统计分析
