# SpringBoot服务端帮助文档

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

**属性列表**：

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
          
## 数据访问层 (Repository Layer) 文档

### 1. 接口：CategoryRepository（药品分类数据访问接口）

**位置**：`com.example.demo.repository`

**继承关系**：`extends JpaRepository<Category, Long>`

**类注解说明**：
- `@Repository`：Spring注解，标识为数据访问组件

**描述**：药品分类实体的数据访问接口，提供分类数据的CRUD操作及自定义查询方法

**方法列表**：

| 方法签名                                                                                                                                                                                                                                                                        | 返回类型             | 参数                                 | 描述                                             | 返回值说明                                                                 | 失败情况返回值       |
| ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------- | ------------------------------------ | ------------------------------------------------ | -------------------------------------------------------------------------- | -------------------- |
| `List<Category> findByParentId(Long parentId)`                                                                                                                                                                                                                                  | `List<Category>`     | `parentId`: 父分类ID                 | 根据父分类ID查找子分类                           | 返回指定父分类下的所有子分类列表，若不存在则返回空列表                     | 返回空列表           |
| `List<Category> findByLevel(Integer level)`                                                                                                                                                                                                                                     | `List<Category>`     | `level`: 分类级别                    | 根据分类级别查找                                 | 返回指定级别的所有分类列表，若不存在则返回空列表                           | 返回空列表           |
| `List<Category> findByStatus(Integer status)`                                                                                                                                                                                                                                   | `List<Category>`     | `status`: 状态                       | 根据状态查找分类                                 | 返回指定状态的所有分类列表，若不存在则返回空列表                           | 返回空列表           |
| `List<Category> findByStatusOrderBySortAsc(Integer status)`                                                                                                                                                                                                                      | `List<Category>`     | `status`: 状态                       | 查找所有启用状态的分类，按排序值升序             | 返回指定状态的所有分类列表，按排序值升序，若不存在则返回空列表             | 返回空列表           |
| `List<Category> findByParentIdAndStatusOrderBySortAsc(Long parentId, Integer status)`                                                                                                                                                                                            | `List<Category>`     | `parentId`: 父分类ID<br>`status`: 状态 | 查找一级分类（parentId = 0），按排序值升序       | 返回指定父分类和状态的所有分类列表，按排序值升序，若不存在则返回空列表     | 返回空列表           |
| `Optional<Category> findByName(String name)`                                                                                                                                                                                                                                     | `Optional<Category>` | `name`: 分类名称                     | 根据名称查找分类                                 | 返回指定名称的分类对象，若不存在则返回Optional.empty()                     | 返回Optional.empty() |
| `@Query("SELECT c FROM Category c WHERE c.parentId = :parentId")`<br>`List<Category> findDescendantsByParentId(@Param("parentId") Long parentId)`                                                                                                                                | `List<Category>`     | `parentId`: 父分类ID                 | 查找某个分类的所有子孙分类                       | 返回指定父分类的所有子孙分类列表，若不存在则返回空列表                     | 返回空列表           |
| `@Query("SELECT m FROM Medicine m WHERE m.category.id = :categoryId")`<br>`List<Medicine> findMedicinesByCategoryId(@Param("categoryId") Long categoryId)`                                                                                                                         | `List<Medicine>`     | `categoryId`: 分类ID                 | 根据分类ID查询关联的药品                         | 返回指定分类下的所有药品列表，若不存在则返回空列表                         | 返回空列表           |
| `@Query("SELECT sr FROM SaleRecord sr JOIN sr.medicine m WHERE m.category.id = :categoryId")`<br>`List<SaleRecord> findSaleRecordsByCategoryId(@Param("categoryId") Long categoryId)`                                                                                              | `List<SaleRecord>`   | `categoryId`: 分类ID                 | 根据分类ID查询关联的销售记录                     | 返回指定分类下的所有销售记录列表，若不存在则返回空列表                     | 返回空列表           |
| `@Query("SELECT po FROM PurchaseOrder po JOIN po.medicine m WHERE m.category.id = :categoryId")`<br>`List<PurchaseOrder> findPurchaseOrdersByCategoryId(@Param("categoryId") Long categoryId)`                                                                                     | `List<PurchaseOrder>` | `categoryId`: 分类ID                 | 根据分类ID查询关联的采购订单                     | 返回指定分类下的所有采购订单列表，若不存在则返回空列表                     | 返回空列表           |
| `@Query("SELECT s FROM Stock s JOIN s.medicine m WHERE m.category.id = :categoryId")`<br>`List<Stock> findStocksByCategoryId(@Param("categoryId") Long categoryId)`                                                                                                             | `List<Stock>`        | `categoryId`: 分类ID                 | 根据分类ID查询关联的库存                         | 返回指定分类下的所有库存列表，若不存在则返回空列表                         | 返回空列表           |
| `@Query("SELECT COUNT(m) FROM Medicine m WHERE m.category.id = :categoryId AND m.status = 1")`<br>`long countMedicinesByCategoryId(@Param("categoryId") Long categoryId)`                                                                                                         | `long`               | `categoryId`: 分类ID                 | 统计分类下的药品数量                             | 返回指定分类下的药品数量，若不存在则返回0                                  | 返回0                |
| `@Query("SELECT COALESCE(SUM(sr.totalAmount), CAST(0 AS BigDecimal)) FROM SaleRecord sr JOIN sr.medicine m WHERE m.category.id = :categoryId")`<br>`BigDecimal sumSaleAmountByCategoryId(@Param("categoryId") Long categoryId)`                                                        | `BigDecimal`         | `categoryId`: 分类ID                 | 统计分类下的销售总额                             | 返回指定分类下的销售总额，若不存在则返回0                                  | 返回0                |

---

### 2. 接口：MedicineRepository（药品信息数据访问接口）

**位置**：`com.example.demo.repository`

**继承关系**：`extends JpaRepository<Medicine, Long>`

**类注解说明**：
- `@Repository`：Spring注解，标识为数据访问组件

**描述**：药品信息实体的数据访问接口，提供药品数据的CRUD操作及自定义查询方法

**方法列表**：

| 方法签名                                                                                                                                                                                                                                                                        | 返回类型             | 参数                                 | 描述                                             | 返回值说明                                                                 | 失败情况返回值       |
| ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------- | ------------------------------------ | ------------------------------------------------ | -------------------------------------------------------------------------- | -------------------- |
| `Optional<Medicine> findByMedicineCode(String medicineCode)`                                                                                                                                                                                                                     | `Optional<Medicine>` | `medicineCode`: 药品编码             | 根据药品编码查找药品                             | 返回指定编码的药品对象，若不存在则返回Optional.empty()                     | 返回Optional.empty() |
| `boolean existsByMedicineCode(String medicineCode)`                                                                                                                                                                                                                              | `boolean`            | `medicineCode`: 药品编码             | 检查药品编码是否存在                             | 返回药品编码是否存在的布尔值                                               | 返回false            |
| `List<Medicine> findByNameContaining(String name)`                                                                                                                                                                                                                               | `List<Medicine>`     | `name`: 药品名称关键字               | 根据名称模糊查询药品                             | 返回名称包含指定关键字的药品列表，若不存在则返回空列表                     | 返回空列表           |
| `@Query("SELECT m FROM Medicine m WHERE m.category.id = :categoryId")`<br>`List<Medicine> findByCategoryId(@Param("categoryId") Long categoryId)`                                                                                                                                 | `List<Medicine>`     | `categoryId`: 分类ID                 | 根据分类查找药品                                 | 返回指定分类下的所有药品列表，若不存在则返回空列表                         | 返回空列表           |
| `List<Medicine> findByStatus(Integer status)`                                                                                                                                                                                                                                    | `List<Medicine>`     | `status`: 状态                       | 根据状态查找药品                                 | 返回指定状态的所有药品列表，若不存在则返回空列表                           | 返回空列表           |
| `List<Medicine> findByManufacturerContaining(String manufacturer)`                                                                                                                                                                                                               | `List<Medicine>`     | `manufacturer`: 生产厂家关键字       | 根据生产厂家查找                                 | 返回生产厂家包含指定关键字的药品列表，若不存在则返回空列表                 | 返回空列表           |
| `Page<Medicine> findAll(Pageable pageable)`                                                                                                                                                                                                                                      | `Page<Medicine>`     | `pageable`: 分页参数                 | 分页查询                                         | 返回分页后的药品列表                                                       | 返回空页             |
| `Page<Medicine> findByStatus(Integer status, Pageable pageable)`                                                                                                                                                                                                                 | `Page<Medicine>`     | `status`: 状态<br>`pageable`: 分页参数 | 分页查询（按状态）                               | 返回指定状态的分页药品列表                                                 | 返回空页             |
| `@Query("SELECT m FROM Medicine m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')) AND m.category.id = :categoryId")`<br>`List<Medicine> findByNameContainingAndCategoryId(@Param("name") String name, @Param("categoryId") Long categoryId)`                                 | `List<Medicine>`     | `name`: 药品名称关键字<br>`categoryId`: 分类ID | 多条件查询：按名称和分类                         | 返回名称包含指定关键字且属于指定分类的药品列表，若不存在则返回空列表       | 返回空列表           |
| `@Query("SELECT m FROM Medicine m WHERE " + "(LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " + "LOWER(m.genericName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " + "LOWER(m.manufacturer) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " + "m.status = :status")`<br>`List<Medicine> searchMedicines(@Param("keyword") String keyword, @Param("status") Integer status)` | `List<Medicine>`     | `keyword`: 搜索关键字<br>`status`: 状态 | 自定义查询：搜索药品（名称、通用名、生产厂家）   | 返回包含指定关键字且状态为指定值的药品列表，若不存在则返回空列表           | 返回空列表           |
| `@Query("SELECT COUNT(m) FROM Medicine m WHERE m.status = :status")`<br>`long countByStatus(@Param("status") Integer status)`                                                                                                                                                     | `long`               | `status`: 状态                       | 统计药品数量                                     | 返回指定状态的药品数量，若不存在则返回0                                    | 返回0                |
| `@Query("SELECT m.category.id, COUNT(m) FROM Medicine m WHERE m.status = 1 GROUP BY m.category.id")`<br>`List<Object[]> countByCategory()`                                                                                                                                       | `List<Object[]>`     | 无                                   | 获取所有药品的分类统计                           | 返回每个分类及其对应的药品数量列表                                         | 返回空列表           |
| `@Query("SELECT m FROM Medicine m JOIN m.symptoms s WHERE s.id = :symptomId")`<br>`List<Medicine> findBySymptomId(@Param("symptomId") Integer symptomId)`                                                                                                                          | `List<Medicine>`     | `symptomId`: 症状ID                  | 根据症状ID查找药品                               | 返回关联指定症状的药品列表，若不存在则返回空列表                           | 返回空列表           |
| `@Query("SELECT m FROM Medicine m JOIN m.symptoms s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :symptomName, '%'))")`<br>`List<Medicine> findBySymptomName(@Param("symptomName") String symptomName)`                                                                              | `List<Medicine>`     | `symptomName`: 症状名称关键字         | 根据症状名称查找药品                             | 返回关联包含指定症状名称的药品列表，若不存在则返回空列表                   | 返回空列表           |
| `@Query("SELECT s.name, COUNT(m) FROM Medicine m JOIN m.symptoms s WHERE m.status = 1 GROUP BY s.id, s.name")`<br>`List<Object[]> countMedicinesBySymptom()`                                                                                                                       | `List<Object[]>`     | 无                                   | 统计药品按症状分类                               | 返回每个症状及其对应的药品数量列表                                         | 返回空列表           |
| `@Query("SELECT sr FROM SaleRecord sr WHERE sr.medicine.id = :medicineId ORDER BY sr.saleTime DESC")`<br>`List<SaleRecord> findSaleRecordsByMedicineId(@Param("medicineId") Long medicineId)`                                                                                         | `List<SaleRecord>`   | `medicineId`: 药品ID                 | 根据药品ID查询关联的销售记录（包含详细信息）     | 返回指定药品的所有销售记录列表，按销售时间降序，若不存在则返回空列表       | 返回空列表           |
| `@Query("SELECT po FROM PurchaseOrder po WHERE po.medicine.id = :medicineId ORDER BY po.orderTime DESC")`<br>`List<PurchaseOrder> findPurchaseOrdersByMedicineId(@Param("medicineId") Long medicineId)`                                                                             | `List<PurchaseOrder>` | `medicineId`: 药品ID                 | 根据药品ID查询关联的采购订单（包含详细信息）     | 返回指定药品的所有采购订单列表，按下单时间降序，若不存在则返回空列表       | 返回空列表           |
| `@Query("SELECT s FROM Stock s WHERE s.medicine.id = :medicineId ORDER BY s.expirationDate ASC")`<br>`List<Stock> findStocksByMedicineId(@Param("medicineId") Long medicineId)`                                                                                                     | `List<Stock>`        | `medicineId`: 药品ID                 | 根据药品ID查询关联的库存（包含详细信息）         | 返回指定药品的所有库存列表，按有效期升序，若不存在则返回空列表             | 返回空列表           |
| `@Query("SELECT pr FROM PredictionResult pr WHERE pr.medicine.id = :medicineId ORDER BY pr.predictionDate DESC")`<br>`List<PredictionResult> findPredictionResultsByMedicineId(@Param("medicineId") Long medicineId)`                                                               | `List<PredictionResult>` | `medicineId`: 药品ID                 | 根据药品ID查询关联的预测结果（包含详细信息）     | 返回指定药品的所有预测结果列表，按预测日期降序，若不存在则返回空列表       | 返回空列表           |
| `@Query("SELECT m.symptoms FROM Medicine m WHERE m.id = :medicineId")`<br>`List<Symptom> findSymptomsByMedicineId(@Param("medicineId") Long medicineId)`                                                                                                                           | `List<Symptom>`      | `medicineId`: 药品ID                 | 根据药品ID查询关联的症状（包含详细信息）         | 返回指定药品的所有关联症状列表，若不存在则返回空列表                       | 返回空列表           |
| `@Query("SELECT COALESCE(SUM(sr.quantity), 0) FROM SaleRecord sr WHERE sr.medicine.id = :medicineId")`<br>`Long sumSaleQuantityByMedicineId(@Param("medicineId") Long medicineId)`                                                                                                   | `Long`               | `medicineId`: 药品ID                 | 统计药品的销售总量                               | 返回指定药品的销售总量，若不存在则返回0                                    | 返回0                |
| `@Query("SELECT COALESCE(SUM(po.quantity), 0) FROM PurchaseOrder po WHERE po.medicine.id = :medicineId AND po.orderStatus = 2")`<br>`Long sumPurchaseQuantityByMedicineId(@Param("medicineId") Long medicineId)`                                                                   | `Long`               | `medicineId`: 药品ID                 | 统计药品的采购总量                               | 返回指定药品的采购总量，若不存在则返回0                                    | 返回0                |
| `@Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Stock s WHERE s.medicine.id = :medicineId AND s.status = 1")`<br>`Long sumCurrentStockByMedicineId(@Param("medicineId") Long medicineId)`                                                                                         | `Long`               | `medicineId`: 药品ID                 | 统计药品的当前库存量                             | 返回指定药品的当前库存量，若不存在则返回0                                  | 返回0                |
| `@Query("SELECT YEAR(sr.saleTime), MONTH(sr.saleTime), SUM(sr.quantity), SUM(sr.totalAmount) " + "FROM SaleRecord sr WHERE sr.medicine.id = :medicineId " + "GROUP BY YEAR(sr.saleTime), MONTH(sr.saleTime) " + "ORDER BY YEAR(sr.saleTime), MONTH(sr.saleTime)")`<br>`List<Object[]> findSaleTrendByMedicineId(@Param("medicineId") Long medicineId)` | `List<Object[]>`     | `medicineId`: 药品ID                 | 查询药品的销售趋势（按月份）                     | 返回指定药品的月度销售趋势数据列表，若不存在则返回空列表                   | 返回空列表           |

---

### 3. 接口：PredictionResultRepository（预测结果数据访问接口）

**位置**：`com.example.demo.repository`

**继承关系**：`extends JpaRepository<PredictionResult, Long>`

**类注解说明**：
- `@Repository`：Spring注解，标识为数据访问组件

**描述**：预测结果实体的数据访问接口，提供预测结果数据的CRUD操作及自定义查询方法

**方法列表**：

| 方法签名                                                                                                                                                                                                                                                                        | 返回类型             | 参数                                 | 描述                                             | 返回值说明                                                                 | 失败情况返回值       |
| ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------- | ------------------------------------ | ------------------------------------------------ | -------------------------------------------------------------------------- | -------------------- |
| `@Query("SELECT pr FROM PredictionResult pr WHERE pr.medicine.id = :medicineId")`<br>`List<PredictionResult> findByMedicineId(@Param("medicineId") Long medicineId)`                                                                                                               | `List<PredictionResult>` | `medicineId`: 药品ID                 | 根据药品ID查找预测结果                           | 返回指定药品的所有预测结果列表，若不存在则返回空列表                       | 返回空列表           |
| `List<PredictionResult> findByPredictionDate(LocalDate predictionDate)`                                                                                                                                                                                                          | `List<PredictionResult>` | `predictionDate`: 预测日期           | 根据预测日期查找                                 | 返回指定预测日期的所有预测结果列表，若不存在则返回空列表                   | 返回空列表           |
| `List<PredictionResult> findByModelType(String modelType)`                                                                                                                                                                                                                       | `List<PredictionResult>` | `modelType`: 模型类型                | 根据模型类型查找                                 | 返回指定模型类型的所有预测结果列表，若不存在则返回空列表                   | 返回空列表           |
| `Optional<PredictionResult> findFirstByMedicineIdOrderByPredictionDateDesc(Long medicineId)`                                                                                                                                                                                      | `Optional<PredictionResult>` | `medicineId`: 药品ID                 | 查找某个药品最新的预测结果                       | 返回指定药品的最新预测结果对象，若不存在则返回Optional.empty()             | 返回Optional.empty() |
| `List<PredictionResult> findByPredictionDateBetween(LocalDate startDate, LocalDate endDate)`                                                                                                                                                                                     | `List<PredictionResult>` | `startDate`: 开始日期<br>`endDate`: 结束日期 | 查找未来某段时间的预测结果                       | 返回指定日期范围内的所有预测结果列表，若不存在则返回空列表                 | 返回空列表           |
| `List<PredictionResult> findByAccuracyRateGreaterThanEqual(Double minAccuracyRate)`                                                                                                                                                                                               | `List<PredictionResult>` | `minAccuracyRate`: 最低准确率        | 查找准确率高于某个值的预测结果                   | 返回准确率高于指定值的所有预测结果列表，若不存在则返回空列表               | 返回空列表           |
| `@Query("SELECT pr.modelType, AVG(pr.accuracyRate) FROM PredictionResult pr GROUP BY pr.modelType")`<br>`List<Object[]> findAverageAccuracyByModel()`                                                                                                                              | `List<Object[]>`     | 无                                   | 统计各个模型的平均准确率                         | 返回每个模型及其平均准确率的列表                                           | 返回空列表           |
| `@Query("SELECT pr FROM PredictionResult pr WHERE (pr.accuracyRate IS NULL OR pr.accuracyRate < :threshold) OR pr.predictionDate < CURRENT_DATE ORDER BY pr.predictionDate DESC")`<br>`List<PredictionResult> findNeedReprediction(@Param("threshold") BigDecimal threshold)`          | `List<PredictionResult>` | `threshold`: 准确率阈值              | 查找需要重新预测的记录                           | 返回需要重新预测的所有预测结果列表，按预测日期降序，若不存在则返回空列表   | 返回空列表           |
| `@Query("SELECT pr FROM PredictionResult pr WHERE pr.medicine.id IN :medicineIds AND pr.predictionDate = (SELECT MAX(pr2.predictionDate) FROM PredictionResult pr2 WHERE pr2.medicine.id = pr.medicine.id)")`<br>`List<PredictionResult> findLatestByMedicineIds(@Param("medicineIds") List<Long> medicineIds)` | `List<PredictionResult>` | `medicineIds`: 药品ID列表            | 查找部分药品最新的预测结果                       | 返回指定药品列表中每个药品的最新预测结果列表，若不存在则返回空列表         | 返回空列表           |

---

### 4. 接口：PurchaseOrderRepository（采购订单数据访问接口）

**位置**：`com.example.demo.repository`

**继承关系**：`extends JpaRepository<PurchaseOrder, Long>`

**类注解说明**：
- `@Repository`：Spring注解，标识为数据访问组件

**描述**：采购订单实体的数据访问接口，提供采购订单数据的CRUD操作及自定义查询方法

**方法列表**：

| 方法签名                                                                                                                                                                                                                                                                        | 返回类型             | 参数                                 | 描述                                             | 返回值说明                                                                 | 失败情况返回值       |
| ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------- | ------------------------------------ | ------------------------------------------------ | -------------------------------------------------------------------------- | -------------------- |
| `Optional<PurchaseOrder> findByOrderNo(String orderNo)`                                                                                                                                                                                                                          | `Optional<PurchaseOrder>` | `orderNo`: 订单编号                  | 根据订单号查找                                   | 返回指定编号的订单对象，若不存在则返回Optional.empty()                     | 返回Optional.empty() |
| `@Query("SELECT po FROM PurchaseOrder po WHERE po.medicine.id = :medicineId")`<br>`List<PurchaseOrder> findByMedicineId(@Param("medicineId") Long medicineId)`                                                                                                                     | `List<PurchaseOrder>` | `medicineId`: 药品ID                 | 根据药品ID查找采购订单                           | 返回指定药品的所有采购订单列表，若不存在则返回空列表                       | 返回空列表           |
| `List<PurchaseOrder> findByOrderStatus(Integer orderStatus)`                                                                                                                                                                                                                     | `List<PurchaseOrder>` | `orderStatus`: 订单状态              | 根据订单状态查找                                 | 返回指定状态的所有订单列表，若不存在则返回空列表                           | 返回空列表           |
| `@Query("SELECT po FROM PurchaseOrder po WHERE po.operator.id = :operatorId")`<br>`List<PurchaseOrder> findByOperatorId(@Param("operatorId") Long operatorId)`                                                                                                                     | `List<PurchaseOrder>` | `operatorId`: 操作员ID               | 根据操作员ID查找                                 | 返回指定操作员创建的所有订单列表，若不存在则返回空列表                     | 返回空列表           |
| `List<PurchaseOrder> findBySupplierContaining(String supplier)`                                                                                                                                                                                                                   | `List<PurchaseOrder>` | `supplier`: 供应商关键字             | 根据供应商查找                                   | 返回供应商包含指定关键字的所有订单列表，若不存在则返回空列表               | 返回空列表           |
| `List<PurchaseOrder> findByOrderTimeBetween(LocalDateTime startTime, LocalDateTime endTime)`                                                                                                                                                                                     | `List<PurchaseOrder>` | `startTime`: 开始时间<br>`endTime`: 结束时间 | 根据下单时间范围查找                             | 返回指定时间范围内的所有订单列表，若不存在则返回空列表                     | 返回空列表           |
| `Page<PurchaseOrder> findAll(Pageable pageable)`                                                                                                                                                                                                                                 | `Page<PurchaseOrder>` | `pageable`: 分页参数                 | 分页查询                                         | 返回分页后的订单列表                                                       | 返回空页             |
| `Page<PurchaseOrder> findByOrderStatus(Integer orderStatus, Pageable pageable)`                                                                                                                                                                                                  | `Page<PurchaseOrder>` | `orderStatus`: 订单状态<br>`pageable`: 分页参数 | 根据状态分页查询                                 | 返回指定状态的分页订单列表                                                 | 返回空页             |
| `@Query("SELECT COALESCE(SUM(po.quantity), 0) FROM PurchaseOrder po WHERE po.medicine.id = :medicineId AND po.orderStatus = 2")`<br>`Long sumPurchasedQuantityByMedicineId(@Param("medicineId") Long medicineId)`                                                                 | `Long`               | `medicineId`: 药品ID                 | 统计某个药品的采购总量                           | 返回指定药品的采购总量，若不存在则返回0                                    | 返回0                |
| `@Query("SELECT COALESCE(SUM(po.totalAmount), CAST(0 AS BigDecimal)) FROM PurchaseOrder po WHERE po.orderTime BETWEEN :startTime AND :endTime AND po.orderStatus = 2")`<br>`BigDecimal sumTotalAmountByPeriod(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime)` | `BigDecimal`         | `startTime`: 开始时间<br>`endTime`: 结束时间 | 统计某个时间段的采购总额                         | 返回指定时间段的采购总额，若不存在则返回0                                  | 返回0                |
| `@Query("SELECT po FROM PurchaseOrder po WHERE po.orderStatus = 0 ORDER BY po.orderTime ASC")`<br>`List<PurchaseOrder> findPendingOrders()`                                                                                                                                      | `List<PurchaseOrder>` | 无                                   | 查找待处理的采购订单                             | 返回所有待处理的订单列表，按下单时间升序，若不存在则返回空列表             | 返回空列表           |
| `@Query("SELECT po FROM PurchaseOrder po WHERE po.expectedArrival < CURRENT_DATE AND po.orderStatus IN (0, 1) ORDER BY po.expectedArrival ASC")`<br>`List<PurchaseOrder> findOverdueOrders()`                                                                                      | `List<PurchaseOrder>` | 无                                   | 查找过期的采购订单（预计到货日期已过但未到货）   | 返回所有过期的订单列表，按预计到货日期升序，若不存在则返回空列表           | 返回空列表           |
| `@Query("SELECT po FROM PurchaseOrder po " + "LEFT JOIN po.medicine m " + "WHERE (:keyword IS NULL OR :keyword = '' OR " + "       LOWER(po.orderNo) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " + "       LOWER(po.supplier) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " + "       LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")`<br>`List<PurchaseOrder> findByKeywordContaining(@Param("keyword") String keyword)` | `List<PurchaseOrder>` | `keyword`: 关键字                    | 根据供应商或药品名称或订单编号进行模糊字段搜索   | 返回包含指定关键字的所有订单列表，若不存在则返回空列表                     | 返回空列表           |
| `@Query("SELECT po.medicine FROM PurchaseOrder po WHERE po.id = :purchaseOrderId")`<br>`Medicine findMedicineByPurchaseOrderId(@Param("purchaseOrderId") Long purchaseOrderId)`                                                                                                     | `Medicine`           | `purchaseOrderId`: 采购订单ID        | 根据采购订单ID查询关联的药品详情                 | 返回指定采购订单关联的药品对象，若不存在则返回null                        | 返回null             |
| `@Query("SELECT po.operator FROM PurchaseOrder po WHERE po.id = :purchaseOrderId")`<br>`User findOperatorByPurchaseOrderId(@Param("purchaseOrderId") Long purchaseOrderId)`                                                                                                         | `User`               | `purchaseOrderId`: 采购订单ID        | 根据采购订单ID查询关联的操作员详情               | 返回指定采购订单关联的操作员对象，若不存在则返回null                      | 返回null             |
| `@Query("SELECT po FROM PurchaseOrder po WHERE po.supplier LIKE LOWER(CONCAT('%', :supplier, '%')) ORDER BY po.orderTime DESC")`<br>`Page<PurchaseOrder> findBySupplierWithPagination(@Param("supplier") String supplier, Pageable pageable)`                                      | `Page<PurchaseOrder>` | `supplier`: 供应商关键字<br>`pageable`: 分页参数 | 根据供应商查询采购订单（带分页）                 | 返回指定供应商的分页订单列表，按下单时间降序，若不存在则返回空页           | 返回空页             |
| `@Query("SELECT po.supplier, COUNT(po) as orderCount, SUM(po.totalAmount) as totalAmount " + "FROM PurchaseOrder po WHERE po.orderStatus = 2 " + "GROUP BY po.supplier " + "ORDER BY totalAmount DESC")`<br>`List<Object[]> findSupplierPurchaseStatistics()`                         | `List<Object[]>`     | 无                                   | 统计供应商的采购总额                             | 返回每个供应商的订单数量和采购总额列表，按采购总额降序                     | 返回空列表           |
| `@Query("SELECT po.orderStatus, COUNT(po) as orderCount " + "FROM PurchaseOrder po " + "GROUP BY po.orderStatus")`<br>`List<Object[]> findOrderStatusStatistics()`                                                                                                               | `List<Object[]>`     | 无                                   | 统计每个状态的采购订单数量                       | 返回每个订单状态及其对应的订单数量列表                                     | 返回空列表           |
| `@Query(value = "SELECT * FROM purchase_order po WHERE po.expected_arrival BETWEEN CURRENT_DATE() AND DATE_ADD(CURRENT_DATE(), INTERVAL 7 DAY) AND po.order_status IN (0, 1)", nativeQuery = true)`<br>`List<PurchaseOrder> findUpcomingOrders()`                                   | `List<PurchaseOrder>` | 无                                   | 查询即将到期的采购订单（7天内）                  | 返回未来7天内的待处理和已确认订单列表，若不存在则返回空列表               | 返回空列表           |
| `@Query("SELECT COUNT(po) as orderCount, COALESCE(SUM(po.totalAmount), CAST(0 AS BigDecimal)) as totalAmount FROM PurchaseOrder po WHERE po.orderTime BETWEEN :startTime AND :endTime AND po.orderStatus = 2")`<br>`Object[] countAndSumByPeriod(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime)` | `Object[]`           | `startTime`: 开始时间<br>`endTime`: 结束时间 | 统计某个时间段的采购订单数量和总金额             | 返回指定时间段的订单数量和总金额，若不存在则返回[0, 0]                     | 返回[0, 0]           |

**注意**：`findUpcomingOrders()`方法使用了MySQL语法的`DATE_ADD`函数，确保在MySQL数据库上正常运行。

---

### 5. 接口：SaleRecordRepository（销售记录数据访问接口）

**位置**：`com.example.demo.repository`

**继承关系**：`extends JpaRepository<SaleRecord, Long>`

**类注解说明**：
- `@Repository`：Spring注解，标识为数据访问组件

**描述**：销售记录实体的数据访问接口，提供销售记录数据的CRUD操作及自定义查询方法

**方法列表**：

| 方法签名                                                                                                                                                                                                                                                                        | 返回类型             | 参数                                 | 描述                                             | 返回值说明                                                                 | 失败情况返回值       |
| ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------- | ------------------------------------ | ------------------------------------------------ | -------------------------------------------------------------------------- | -------------------- |
| `Optional<SaleRecord> findByRecordNo(String recordNo)`                                                                                                                                                                                                                            | `Optional<SaleRecord>` | `recordNo`: 销售单号                 | 根据销售单号查找                                 | 返回指定编号的销售记录对象，若不存在则返回Optional.empty()                 | 返回Optional.empty() |
| `@Query("SELECT sr FROM SaleRecord sr WHERE sr.medicine.id = :medicineId")`<br>`List<SaleRecord> findByMedicineId(@Param("medicineId") Long medicineId)`                                                                                                                           | `List<SaleRecord>`   | `medicineId`: 药品ID                 | 根据药品ID查找销售记录（使用关联对象）           | 返回指定药品的所有销售记录列表，若不存在则返回空列表                       | 返回空列表           |
| `@Query("SELECT sr FROM SaleRecord sr WHERE sr.operator.id = :operatorId")`<br>`List<SaleRecord> findByOperatorId(@Param("operatorId") Long operatorId)`                                                                                                                           | `List<SaleRecord>`   | `operatorId`: 操作员ID               | 根据操作员ID查找销售记录（使用关联对象）         | 返回指定操作员创建的所有销售记录列表，若不存在则返回空列表                 | 返回空列表           |
| `List<SaleRecord> findBySaleTimeBetween(LocalDateTime startTime, LocalDateTime endTime)`                                                                                                                                                                                           | `List<SaleRecord>`   | `startTime`: 开始时间<br>`endTime`: 结束时间 | 根据时间段查找销售记录                           | 返回指定时间范围内的所有销售记录列表，若不存在则返回空列表                 | 返回空列表           |
| `Page<SaleRecord> findAll(Pageable pageable)`                                                                                                                                                                                                                                    | `Page<SaleRecord>`   | `pageable`: 分页参数                 | 分页查询销售记录                                 | 返回分页后的销售记录列表                                                   | 返回空页             |
| `Page<SaleRecord> findBySaleTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable)`                                                                                                                                                                      | `Page<SaleRecord>`   | `startTime`: 开始时间<br>`endTime`: 结束时间<br>`pageable`: 分页参数 | 根据时间段分页查询                               | 返回指定时间范围内的分页销售记录列表                                       | 返回空页             |
| `@Query("SELECT COALESCE(SUM(sr.quantity), 0) FROM SaleRecord sr WHERE sr.medicine.id = :medicineId")`<br>`Long sumQuantityByMedicineId(@Param("medicineId") Long medicineId)`                                                                                                   | `Long`               | `medicineId`: 药品ID                 | 统计某个药品的销售总量 - 添加COALESCE处理null    | 返回指定药品的销售总量，若不存在则返回0                                    | 返回0                |
| `@Query("SELECT COALESCE(SUM(sr.totalAmount), CAST(0 AS BigDecimal)) FROM SaleRecord sr WHERE sr.saleTime BETWEEN :startTime AND :endTime")`<br>`BigDecimal sumTotalAmountByPeriod(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime)`           | `BigDecimal`         | `startTime`: 开始时间<br>`endTime`: 结束时间 | 统计某个时间段的销售总额 - 添加COALESCE处理null  | 返回指定时间段的销售总额，若不存在则返回0                                  | 返回0                |
| `@Query(value = "SELECT CAST(sr.sale_time AS DATE), sr.medicine_id, SUM(sr.quantity) " + "FROM sale_record sr " + "WHERE sr.sale_time BETWEEN :startDate AND :endDate " + "GROUP BY CAST(sr.sale_time AS DATE), sr.medicine_id " + "ORDER BY CAST(sr.sale_time AS DATE)", nativeQuery = true)`<br>`List<Object[]> findDailySales(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate)` | `List<Object[]>`     | `startDate`: 开始日期<br>`endDate`: 结束日期 | 统计每天的销售数据（用于需求预测）               | 返回每天的销售数据列表，每个元素包含日期、药品ID和销售数量                 | 返回空列表           |
| `@Query("SELECT sr.medicine.id, SUM(sr.quantity) as totalQuantity " + "FROM SaleRecord sr " + "WHERE sr.saleTime BETWEEN :startDate AND :endDate " + "GROUP BY sr.medicine.id " + "ORDER BY totalQuantity DESC")`<br>`List<Object[]> findTopSellingMedicines(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable)` | `List<Object[]>`     | `startDate`: 开始日期<br>`endDate`: 结束日期<br>`pageable`: 分页参数 | 查找最畅销的药品                                 | 返回指定时间范围内最畅销的药品列表，每个元素包含药品ID和销售总量           | 返回空列表           |
| `@Query("SELECT sr FROM SaleRecord sr JOIN sr.symptom s WHERE s.id = :symptomId")`<br>`List<SaleRecord> findBySymptomId(@Param("symptomId") Integer symptomId)`                                                                                                                    | `List<SaleRecord>`   | `symptomId`: 症状ID                  | 根据症状ID查找销售记录                           | 返回关联指定症状的销售记录列表，若不存在则返回空列表                       | 返回空列表           |
| `@Query("SELECT sr FROM SaleRecord sr JOIN sr.symptom s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :symptomName, '%'))")`<br>`List<SaleRecord> findBySymptomName(@Param("symptomName") String symptomName)`                                                                          | `List<SaleRecord>`   | `symptomName`: 症状名称关键字         | 根据症状名称查找销售记录                         | 返回关联包含指定症状名称的销售记录列表，若不存在则返回空列表               | 返回空列表           |
| `@Query("SELECT s.name, SUM(sr.quantity) as totalQuantity, SUM(sr.totalAmount) as totalAmount " + "FROM SaleRecord sr JOIN sr.symptom s " + "WHERE sr.saleTime BETWEEN :startDate AND :endDate " + "GROUP BY s.id, s.name " + "ORDER BY totalQuantity DESC")`<br>`List<Object[]> findSalesBySymptom(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate)` | `List<Object[]>`     | `startDate`: 开始日期<br>`endDate`: 结束日期 | 统计按症状分类的销售数据                         | 返回每个症状的销售数量和销售总额列表，按销售数量降序                       | 返回空列表           |
| `@Query("SELECT sr.medicine FROM SaleRecord sr WHERE sr.id = :saleRecordId")`<br>`Medicine findMedicineBySaleRecordId(@Param("saleRecordId") Long saleRecordId)`                                                                                                                     | `Medicine`           | `saleRecordId`: 销售记录ID            | 根据销售记录ID查询关联的药品详情                 | 返回指定销售记录关联的药品对象，若不存在则返回null                        | 返回null             |
| `@Query("SELECT sr.operator FROM SaleRecord sr WHERE sr.id = :saleRecordId")`<br>`User findOperatorBySaleRecordId(@Param("saleRecordId") Long saleRecordId)`                                                                                                                       | `User`               | `saleRecordId`: 销售记录ID            | 根据销售记录ID查询关联的操作员详情               | 返回指定销售记录关联的操作员对象，若不存在则返回null                      | 返回null             |
| `@Query("SELECT sr.symptom FROM SaleRecord sr WHERE sr.id = :saleRecordId")`<br>`List<Symptom> findSymptomsBySaleRecordId(@Param("saleRecordId") Long saleRecordId)`                                                                                                                 | `List<Symptom>`      | `saleRecordId`: 销售记录ID            | 根据销售记录ID查询关联的症状列表                 | 返回指定销售记录关联的症状列表，若不存在则返回空列表                       | 返回空列表           |
| `@Query("SELECT sr FROM SaleRecord sr WHERE sr.operator.id = :operatorId ORDER BY sr.saleTime DESC")`<br>`Page<SaleRecord> findByOperatorIdWithPagination(@Param("operatorId") Long operatorId, Pageable pageable)`                                                                 | `Page<SaleRecord>`   | `operatorId`: 操作员ID<br>`pageable`: 分页参数 | 根据操作员ID查询销售记录（带分页）               | 返回指定操作员的分页销售记录列表，按销售时间降序，若不存在则返回空页       | 返回空页             |
| `@Query("SELECT sr FROM SaleRecord sr WHERE sr.customerType = :customerType ORDER BY sr.saleTime DESC")`<br>`List<SaleRecord> findByCustomerType(@Param("customerType") Integer customerType)`                                                                                        | `List<SaleRecord>`   | `customerType`: 顾客类型              | 根据顾客类型查询销售记录                         | 返回指定顾客类型的销售记录列表，按销售时间降序，若不存在则返回空列表       | 返回空列表           |
| `@Query("SELECT sr.operator.id, sr.operator.realName, COUNT(sr) as recordCount, SUM(sr.totalAmount) as totalAmount " + "FROM SaleRecord sr WHERE sr.saleTime BETWEEN :startDate AND :endDate " + "GROUP BY sr.operator.id, sr.operator.realName " + "ORDER BY totalAmount DESC")`<br>`List<Object[]> findOperatorSalesPerformance(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate)` | `List<Object[]>`     | `startDate`: 开始日期<br>`endDate`: 结束日期 | 统计操作员的销售业绩                             | 返回每个操作员的销售记录数量和销售总额列表，按销售总额降序                 | 返回空列表           |
| `@Query(value = "SELECT CAST(sr.sale_time AS DATE), SUM(sr.total_amount) as dailyAmount " + "FROM sale_record sr WHERE sr.sale_time BETWEEN :startDate AND :endDate " + "GROUP BY CAST(sr.sale_time AS DATE) " + "ORDER BY CAST(sr.sale_time AS DATE)", nativeQuery = true)`<br>`List<Object[]> findDailySalesAmount(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate)` | `List<Object[]>`     | `startDate`: 开始日期<br>`endDate`: 结束日期 | 统计每天的销售总额                               | 返回每天的销售总额列表，每个元素包含日期和销售总额                         | 返回空列表           |
| `@Query("SELECT m.id, m.name, SUM(sr.totalAmount) as totalSales " + "FROM SaleRecord sr JOIN sr.medicine m " + "WHERE sr.saleTime BETWEEN :startDate AND :endDate " + "GROUP BY m.id, m.name " + "ORDER BY totalSales DESC")`<br>`List<Object[]> findTopSellingMedicinesByAmount(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable)` | `List<Object[]>`     | `startDate`: 开始日期<br>`endDate`: 结束日期<br>`pageable`: 分页参数 | 查询销售额最高的前N个药品                       | 返回指定时间范围内销售额最高的药品列表，每个元素包含药品ID、名称和销售额   | 返回空列表           |

---

### 6. 接口：StockRepository（库存信息数据访问接口）

**位置**：`com.example.demo.repository`

**继承关系**：`extends JpaRepository<Stock, Long>`

**类注解说明**：
- `@Repository`：Spring注解，标识为数据访问组件

**描述**：库存信息实体的数据访问接口，提供库存数据的CRUD操作及自定义查询方法

**方法列表**：

| 方法签名                                                                                                                                                                                                                                                                        | 返回类型             | 参数                                 | 描述                                             | 返回值说明                                                                 | 失败情况返回值       |
| ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------- | ------------------------------------ | ------------------------------------------------ | -------------------------------------------------------------------------- | -------------------- |
| `@Query("SELECT s FROM Stock s WHERE s.medicine.id = :medicineId")`<br>`List<Stock> findByMedicineId(@Param("medicineId") Long medicineId)`                                                                                                                                     | `List<Stock>`        | `medicineId`: 药品ID                 | 根据药品ID查找库存（使用关联对象）               | 返回指定药品的所有库存列表，若不存在则返回空列表                           | 返回空列表           |
| `@Query("SELECT s FROM Stock s WHERE s.medicine.id = :medicineId AND s.status = :status")`<br>`List<Stock> findByMedicineIdAndStatus(@Param("medicineId") Long medicineId, @Param("status") Integer status)`                                                                       | `List<Stock>`        | `medicineId`: 药品ID<br>`status`: 状态 | 根据药品ID和状态查找库存                         | 返回指定药品和状态的库存列表，若不存在则返回空列表                         | 返回空列表           |
| `List<Stock> findByExpirationDateBeforeAndStatus(LocalDate date, Integer status)`                                                                                                                                                                                               | `List<Stock>`        | `date`: 日期<br>`status`: 状态        | 查找过期库存                                     | 返回在指定日期之前过期且状态为指定值的库存列表，若不存在则返回空列表       | 返回空列表           |
| `@Query("SELECT s FROM Stock s WHERE s.expirationDate BETWEEN :startDate AND :endDate AND s.status = 1")`<br>`List<Stock> findExpiringStock(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate)`                                                         | `List<Stock>`        | `startDate`: 开始日期<br>`endDate`: 结束日期 | 查找即将过期的库存                               | 返回指定日期范围内即将过期的库存列表，若不存在则返回空列表                 | 返回空列表           |
| `@Query("SELECT s FROM Stock s WHERE s.quantity <= s.warningQuantity AND s.status = 1")`<br>`List<Stock> findLowStock()`                                                                                                                                                        | `List<Stock>`        | 无                                   | 查找库存不足的药品（数量小于等于预警数量）       | 返回所有库存不足的库存列表，若不存在则返回空列表                           | 返回空列表           |
| `List<Stock> findByBatchNumber(String batchNumber)`                                                                                                                                                                                                                              | `List<Stock>`        | `batchNumber`: 批号                  | 根据批号查找库存                                 | 返回指定批号的库存列表，若不存在则返回空列表                               | 返回空列表           |
| `@Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Stock s WHERE s.medicine.id = :medicineId AND s.status = 1")`<br>`Long sumQuantityByMedicineId(@Param("medicineId") Long medicineId)`                                                                                           | `Long`               | `medicineId`: 药品ID                 | 计算某个药品的总库存量                           | 返回指定药品的总库存量，若不存在则返回0                                    | 返回0                |
| `@Query("SELECT s.medicine.id, SUM(s.quantity) as totalQuantity, MIN(s.warningQuantity) as warningQuantity " + "FROM Stock s WHERE s.status = 1 GROUP BY s.medicine.id HAVING SUM(s.quantity) <= MIN(s.warningQuantity)")`<br>`List<Object[]> findLowStockSummary()`              | `List<Object[]>`     | 无                                   | 查找所有库存不足的药品                           | 返回所有库存不足的药品列表，每个元素包含药品ID、总库存量和预警数量         | 返回空列表           |
| `List<Stock> findByShelfLocation(String shelfLocation)`                                                                                                                                                                                                                          | `List<Stock>`        | `shelfLocation`: 货架位置            | 根据货架位置查找库存                             | 返回指定货架位置的库存列表，若不存在则返回空列表                           | 返回空列表           |
| `List<Stock> findByStatus(Integer status)`                                                                                                                                                                                                                                      | `List<Stock>`        | `status`: 状态                       | 根据货品状态查找库存                             | 返回指定状态的库存列表，若不存在则返回空列表                               | 返回空列表           |
| `@Query("SELECT s.medicine FROM Stock s WHERE s.id = :stockId")`<br>`Medicine findMedicineByStockId(@Param("stockId") Long stockId)`                                                                                                                                             | `Medicine`           | `stockId`: 库存ID                    | 根据库存ID查询关联的药品详情                     | 返回指定库存关联的药品对象，若不存在则返回null                            | 返回null             |
| `@Query("SELECT s.status, SUM(s.quantity) as totalQuantity " + "FROM Stock s WHERE s.medicine.id = :medicineId " + "GROUP BY s.status")`<br>`List<Object[]> findStatusStatisticsByMedicineId(@Param("medicineId") Long medicineId)`                                                 | `List<Object[]>`     | `medicineId`: 药品ID                 | 根据药品ID查询库存状态统计                       | 返回指定药品的库存状态统计列表，每个元素包含状态和对应数量                 | 返回空列表           |
| `@Query("SELECT s FROM Stock s WHERE s.shelfLocation LIKE LOWER(CONCAT('%', :location, '%')) AND s.status = 1")`<br>`List<Stock> findByShelfLocationContaining(@Param("location") String location)`                                                                                 | `List<Stock>`        | `location`: 货架位置关键字            | 根据货架位置查询库存                             | 返回货架位置包含指定关键字的库存列表，若不存在则返回空列表                 | 返回空列表           |
| `@Query("SELECT s FROM Stock s WHERE s.status = 0 ORDER BY s.expirationDate ASC")`<br>`List<Stock> findExpiredStock()`                                                                                                                                                          | `List<Stock>`        | 无                                   | 查询过期库存（状态为0）                          | 返回所有过期库存列表，按有效期升序，若不存在则返回空列表                   | 返回空列表           |
| `@Query("SELECT COALESCE(SUM(s.quantity * m.retailPrice), CAST(0 AS BigDecimal)) FROM Stock s JOIN s.medicine m WHERE s.status = 1")`<br>`BigDecimal calculateTotalStockValue()`                                                                                                   | `BigDecimal`         | 无                                   | 统计库存总价值                                   | 返回所有有效库存的总价值，若不存在则返回0                                  | 返回0                |
| `@Query("SELECT c.name, COALESCE(SUM(s.quantity * m.retailPrice), CAST(0 AS BigDecimal)) as totalValue FROM Stock s JOIN s.medicine m JOIN m.category c WHERE s.status = 1 GROUP BY c.id, c.name ORDER BY totalValue DESC")`<br>`List<Object[]> calculateStockValueByCategory()`     | `List<Object[]>`     | 无                                   | 统计每个分类的库存价值                           | 返回每个分类的库存价值列表，按价值降序，若不存在则返回空列表               | 返回空列表           |
| `@Query(value = "SELECT m.id, m.name, " + "COALESCE(stock.currentQty, 0) AS currentStock, " + "COALESCE(sold.soldQty, 0) AS soldQuantity, " + "CASE WHEN COALESCE(stock.currentQty, 0) > 0 " + "THEN COALESCE(sold.soldQty, 0) / stock.currentQty " + "ELSE 0 END AS turnoverRate " + "FROM medicine m " + "LEFT JOIN (SELECT s.medicine_id AS medId, SUM(s.quantity) AS currentQty " + "           FROM stock s WHERE s.status = 1 GROUP BY s.medicine_id) stock " + "ON m.id = stock.medId " + "LEFT JOIN (SELECT sr.medicine_id AS medId, SUM(sr.quantity) AS soldQty " + "           FROM sale_record sr " + "           WHERE sr.sale_time BETWEEN :startDate AND :endDate " + "           GROUP BY sr.medicine_id) sold " + "ON m.id = sold.medId " + "ORDER BY turnoverRate DESC", nativeQuery = true)`<br>`List<Object[]> calculateStockTurnoverRate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate)` | `List<Object[]>`     | `startDate`: 开始日期<br>`endDate`: 结束日期 | 查询库存周转率（基于销售记录）                   | 返回每个药品的库存周转率列表，按周转率降序，若不存在则返回空列表           | 返回空列表           |

---

### 7. 接口：SymptomRepository（症状数据访问接口）

**位置**：`com.example.demo.repository`

**继承关系**：`extends JpaRepository<Symptom, Integer>`

**类注解说明**：
- `@Repository`：Spring注解，标识为数据访问组件

**描述**：症状实体的数据访问接口，提供症状数据的CRUD操作及自定义查询方法

**方法列表**：

| 方法签名                                                                                                                                                                                                                                                                        | 返回类型             | 参数                                 | 描述                                             | 返回值说明                                                                 | 失败情况返回值       |
| ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------- | ------------------------------------ | ------------------------------------------------ | -------------------------------------------------------------------------- | -------------------- |
| `Optional<Symptom> findByName(String name)`                                                                                                                                                                                                                                       | `Optional<Symptom>`  | `name`: 症状名称                     | 根据名称精确查找症状                             | 返回指定名称的症状对象，若不存在则返回Optional.empty()                     | 返回Optional.empty() |
| `List<Symptom> findByNameContaining(String name)`                                                                                                                                                                                                                                | `List<Symptom>`      | `name`: 症状名称关键字               | 根据名称模糊查询                                 | 返回名称包含指定关键字的症状列表，若不存在则返回空列表                     | 返回空列表           |
| `boolean existsByName(String name)`                                                                                                                                                                                                                                              | `boolean`            | `name`: 症状名称                     | 检查症状名称是否存在                             | 返回症状名称是否存在的布尔值                                               | 返回false            |
| `List<Symptom> findByDescriptionContaining(String description)`                                                                                                                                                                                                                  | `List<Symptom>`      | `description`: 描述关键字             | 根据描述模糊查询                                 | 返回描述包含指定关键字的症状列表，若不存在则返回空列表                     | 返回空列表           |
| `@Query("SELECT s FROM Symptom s WHERE " + "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " + "LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")`<br>`List<Symptom> searchSymptoms(@Param("keyword") String keyword)`                                   | `List<Symptom>`      | `keyword`: 搜索关键字                 | 搜索症状（名称或描述模糊匹配）                   | 返回包含指定关键字的症状列表，若不存在则返回空列表                         | 返回空列表           |
| `@Query("SELECT m FROM Medicine m JOIN m.symptoms s WHERE s.id = :symptomId")`<br>`List<Medicine> findMedicinesBySymptomId(@Param("symptomId") Integer symptomId)`                                                                                                                | `List<Medicine>`     | `symptomId`: 症状ID                  | 根据症状ID查询关联的药品                         | 返回关联指定症状的药品列表，若不存在则返回空列表                           | 返回空列表           |
| `@Query("SELECT m FROM Medicine m JOIN m.symptoms s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :symptomName, '%'))")`<br>`List<Medicine> findMedicinesBySymptomName(@Param("symptomName") String symptomName)`                                                                      | `List<Medicine>`     | `symptomName`: 症状名称关键字         | 根据症状名称查询关联的药品                       | 返回关联包含指定症状名称的药品列表，若不存在则返回空列表                   | 返回空列表           |
| `@Query("SELECT sr FROM SaleRecord sr JOIN sr.symptom s WHERE s.id = :symptomId")`<br>`List<SaleRecord> findSaleRecordsBySymptomId(@Param("symptomId") Integer symptomId)`                                                                                                          | `List<SaleRecord>`   | `symptomId`: 症状ID                  | 根据症状ID查询关联的销售记录                     | 返回关联指定症状的销售记录列表，若不存在则返回空列表                       | 返回空列表           |
| `@Query("SELECT sr FROM SaleRecord sr JOIN sr.symptom s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :symptomName, '%'))")`<br>`List<SaleRecord> findSaleRecordsBySymptomName(@Param("symptomName") String symptomName)`                                                                | `List<SaleRecord>`   | `symptomName`: 症状名称关键字         | 根据症状名称查询关联的销售记录                   | 返回关联包含指定症状名称的销售记录列表，若不存在则返回空列表               | 返回空列表           |
| `@Query("SELECT COUNT(DISTINCT m) FROM Medicine m JOIN m.symptoms s WHERE s.id = :symptomId")`<br>`long countMedicinesBySymptomId(@Param("symptomId") Integer symptomId)`                                                                                                         | `long`               | `symptomId`: 症状ID                  | 统计症状关联的药品数量                           | 返回关联指定症状的药品数量，若不存在则返回0                                | 返回0                |
| `@Query("SELECT COUNT(sr) FROM SaleRecord sr JOIN sr.symptom s WHERE s.id = :symptomId")`<br>`long countSaleRecordsBySymptomId(@Param("symptomId") Integer symptomId)`                                                                                                           | `long`               | `symptomId`: 症状ID                  | 统计症状关联的销售记录数量                       | 返回关联指定症状的销售记录数量，若不存在则返回0                            | 返回0                |
| `@Query("SELECT COALESCE(SUM(sr.totalAmount), CAST(0 AS BigDecimal)) FROM SaleRecord sr JOIN sr.symptom s WHERE s.id = :symptomId")`<br>`BigDecimal sumSaleAmountBySymptomId(@Param("symptomId") Integer symptomId)`                                                                 | `BigDecimal`         | `symptomId`: 症状ID                  | 统计症状关联的销售总额                           | 返回关联指定症状的销售总额，若不存在则返回0                                | 返回0                |
| `@Query("SELECT s.id, COUNT(DISTINCT sr.id) as usageCount FROM SaleRecord sr JOIN sr.symptom s WHERE sr.saleTime BETWEEN :start AND :end GROUP BY s.id ORDER BY usageCount DESC")`<br>`List<Object[]> findMostCommonSymptoms(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable)` | `List<Object[]>`     | `start`: 开始时间<br>`end`: 结束时间<br>`pageable`: 分页参数 | 查找指定时间范围内最常见的症状                   | 返回指定时间范围内最常见的症状列表，每个元素包含症状ID和使用次数           | 返回空列表           |

---

### 8. 接口：UserRepository（用户信息数据访问接口）

**位置**：`com.example.demo.repository`

**继承关系**：`extends JpaRepository<User, Long>`

**类注解说明**：
- `@Repository`：Spring注解，标识为数据访问组件

**描述**：用户信息实体的数据访问接口，提供用户数据的CRUD操作及自定义查询方法

**方法列表**：

| 方法签名                                                                                                                                                                                                                                                                        | 返回类型             | 参数                                 | 描述                                             | 返回值说明                                                                 | 失败情况返回值       |
| ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------- | ------------------------------------ | ------------------------------------------------ | -------------------------------------------------------------------------- | -------------------- |
| `Optional<User> findByUsername(String username)`                                                                                                                                                                                                                                 | `Optional<User>`     | `username`: 用户名                   | 根据用户名查找用户                               | 返回指定用户名的用户对象，若不存在则返回Optional.empty()                   | 返回Optional.empty() |
| `boolean existsByUsername(String username)`                                                                                                                                                                                                                                      | `boolean`            | `username`: 用户名                   | 检查用户名是否存在                               | 返回用户名是否存在的布尔值                                                 | 返回false            |
| `List<User> findByRole(String role)`                                                                                                                                                                                                                                             | `List<User>`         | `role`: 角色                         | 根据角色查找用户                                 | 返回指定角色的所有用户列表，若不存在则返回空列表                           | 返回空列表           |
| `List<User> findByStatus(Integer status)`                                                                                                                                                                                                                                        | `List<User>`         | `status`: 状态                       | 根据状态查找用户                                 | 返回指定状态的所有用户列表，若不存在则返回空列表                           | 返回空列表           |
| `Optional<User> findByUsernameAndStatus(String username, Integer status)`                                                                                                                                                                                                         | `Optional<User>`     | `username`: 用户名<br>`status`: 状态 | 根据用户名和状态查找                             | 返回指定用户名和状态的用户对象，若不存在则返回Optional.empty()             | 返回Optional.empty() |
| `@Query("SELECT u FROM User u WHERE " + "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " + "LOWER(u.realName) LIKE LOWER(CONCAT('%', :keyword, '%'))")`<br>`List<User> searchUsers(@Param("keyword") String keyword)`                                               | `List<User>`         | `keyword`: 搜索关键字                 | 自定义查询：搜索用户（按用户名或真实姓名）       | 返回包含指定关键字的用户列表，若不存在则返回空列表                         | 返回空列表           |

---

### 数据访问层注意事项

1. **SQL语法兼容性**：所有Repository接口中的原生SQL查询都使用MySQL兼容的语法，确保在MySQL数据库上正常运行。

2. **日期函数**：使用MySQL的`DATE_ADD`函数进行日期计算，例如在`PurchaseOrderRepository.findUpcomingOrders()`方法中。

3. **参数绑定**：对于原生SQL查询，使用`@Param`注解进行参数绑定，提高代码可读性和安全性。

4. **性能优化**：对于可能返回大量数据的查询，使用分页或限制返回数量的方式优化性能。

5. **错误处理**：所有Repository方法在找不到数据时返回空列表或Optional.empty()，由调用方负责处理这些情况。

6. **事务管理**：Repository层的方法默认继承了JpaRepository的事务管理特性，确保数据操作的原子性。

7. **空值处理**：使用`COALESCE`函数处理可能的空值情况，确保查询结果的一致性。

8. **关联查询**：使用JPQL的JOIN语法进行关联查询，提高查询效率。

9. **分组统计**：使用GROUP BY子句进行分组统计，提供数据汇总功能。

10. **排序**：在查询中使用ORDER BY子句，确保结果按预期顺序返回。

---
## 服务层 (Service Layer) 文档

### 1. 接口：BaseService（基础服务接口）

**位置**：`com.example.demo.service`

**描述**：所有服务接口的基础接口，定义了通用的CRUD操作方法

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 业务逻辑 |
|---------|---------|------|------|----------|
| `save(T entity)` | `T` | `entity`: 实体对象 | 保存实体 | 调用repository的save方法保存实体对象，返回保存后的实体 |
| `update(T entity)` | `T` | `entity`: 实体对象 | 更新实体 | 调用repository的save方法更新实体对象，返回更新后的实体 |
| `delete(ID id)` | `void` | `id`: 实体ID | 删除实体 | 调用repository的deleteById方法删除指定ID的实体 |
| `findById(ID id)` | `T` | `id`: 实体ID | 根据ID查找实体 | 调用repository的findById方法查找指定ID的实体，返回实体对象或null |
| `findAll()` | `List<T>` | 无 | 查找所有实体 | 调用repository的findAll方法返回所有实体列表 |
| `findAll(Pageable pageable)` | `Page<T>` | `pageable`: 分页参数 | 分页查找所有实体 | 调用repository的findAll方法返回分页后的实体列表 |
| `saveAll(List<T> entities)` | `List<T>` | `entities`: 实体列表 | 批量保存实体 | 调用repository的saveAll方法批量保存实体列表，返回保存后的实体列表 |
| `deleteAll(List<ID> ids)` | `void` | `ids`: 实体ID列表 | 批量删除实体 | 遍历ID列表，调用repository的deleteById方法逐个删除指定ID的实体 |
| `exists(ID id)` | `boolean` | `id`: 实体ID | 检查实体是否存在 | 调用repository的existsById方法检查指定ID的实体是否存在，返回布尔值 |

---

### 2. 接口：CategoryService（分类服务接口）

**位置**：`com.example.demo.service`

**继承关系**：`extends BaseService<Category, Long>`

**描述**：分类服务接口，提供分类相关的业务操作

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 业务逻辑 |
|---------|---------|------|------|----------|
| `findByParentId(Long parentId)` | `List<Category>` | `parentId`: 父分类ID | 根据父分类ID查找子分类 | 调用CategoryRepository的findByParentId方法，返回指定父分类下的所有子分类列表 |
| `findByLevel(Integer level)` | `List<Category>` | `level`: 分类级别 | 根据分类级别查找 | 调用CategoryRepository的findByLevel方法，返回指定级别的所有分类列表 |
| `findRootCategories()` | `List<Category>` | 无 | 查找一级分类 | 调用CategoryRepository的findByParentIdAndStatusOrderBySortAsc方法，传入parentId=0和status=1，返回所有一级分类列表 |
| `findAll(Pageable pageable)` | `Page<Category>` | `pageable`: 分页参数 | 分页查找所有分类 | 调用CategoryRepository的findAll方法，返回分页后的分类列表 |
| `getCategoryTree()` | `List<Map<String, Object>>` | 无 | 获取分类树 | 1. 调用CategoryRepository的findByStatusOrderBySortAsc方法获取所有启用状态的分类<br>2. 构建父分类ID到子分类列表的映射<br>3. 递归构建分类树节点，包含id、name、parentId、level、description、sort、status和children字段 |
| `existsByName(String name)` | `boolean` | `name`: 分类名称 | 检查分类名称是否存在 | 调用CategoryRepository的findByName方法，检查返回的Optional是否存在，返回布尔值 |
| `updateCategoryTree()` | `void` | 无 | 更新分类树结构 | 1. 调用CategoryRepository的findAll方法获取所有分类<br>2. 构建父分类ID到子分类列表的映射<br>3. 使用队列进行广度优先遍历，更新每个分类的level字段<br>4. 调用CategoryRepository的saveAll方法保存更新后的分类列表 |
| `getCategoryPath(Long categoryId)` | `Map<Long, String>` | `categoryId`: 分类ID | 获取分类路径 | 1. 调用CategoryRepository的findById方法查找指定ID的分类<br>2. 循环查找父分类，构建分类路径映射<br>3. 反转路径顺序，返回从根分类到目标分类的路径 |
| `searchCategories(String keyword, Pageable pageable)` | `Page<Category>` | `keyword`: 搜索关键字<br>`pageable`: 分页参数 | 搜索分类 | 1. 调用CategoryRepository的findByStatusOrderBySortAsc方法获取所有启用状态的分类<br>2. 过滤名称或描述包含关键字的分类<br>3. 进行分页处理，返回分页后的分类列表 |
| `hasAssociatedMedicines(Long categoryId)` | `boolean` | `categoryId`: 分类ID | 检查分类是否有关联药品 | 1. 调用findById方法查找指定ID的分类<br>2. 检查分类的medicines属性是否非空，返回布尔值 |
| `delete(Long id)` | `void` | `id`: 分类ID | 删除分类 | 1. 调用hasAssociatedMedicines方法检查分类是否有关联药品<br>2. 如果有关联药品，抛出IllegalStateException异常<br>3. 调用父类的delete方法删除分类 |
| `findActiveCategories(Pageable pageable)` | `Page<Category>` | `pageable`: 分页参数 | 查找启用状态的分类 | 1. 调用CategoryRepository的findByStatusOrderBySortAsc方法获取所有启用状态的分类<br>2. 进行分页处理，返回分页后的分类列表 |
| `findSubcategoriesByParentId(Long parentId, Pageable pageable)` | `Page<Category>` | `parentId`: 父分类ID<br>`pageable`: 分页参数 | 查找指定父分类的子分类 | 1. 调用CategoryRepository的findByParentId方法获取指定父分类的子分类<br>2. 进行分页处理，返回分页后的分类列表 |
| `getCategoryHierarchy()` | `Map<Long, List<Category>>` | 无 | 获取分类层次结构 | 1. 调用CategoryRepository的findAll方法获取所有分类<br>2. 使用Stream API按父分类ID分组，返回父分类ID到子分类列表的映射 |
| `findCategoriesWithMedicines(Pageable pageable)` | `Page<Category>` | `pageable`: 分页参数 | 查找有关联药品的分类 | 1. 调用CategoryRepository的findAll方法获取所有分类<br>2. 过滤有关联药品的分类<br>3. 进行分页处理，返回分页后的分类列表 |
| `getCategoryStatistics(Long categoryId)` | `Map<String, Object>` | `categoryId`: 分类ID | 获取分类统计信息 | 1. 调用CategoryRepository的findById方法查找指定ID的分类<br>2. 调用CategoryRepository的countMedicinesByCategoryId方法统计分类下的药品数量<br>3. 调用CategoryRepository的sumSaleAmountByCategoryId方法统计分类下的销售总额<br>4. 调用CategoryRepository的findByParentId方法获取分类的子分类数量<br>5. 构建并返回统计信息映射 |

---

### 3. 接口：MedicineService（药品服务接口）

**位置**：`com.example.demo.service`

**继承关系**：`extends BaseService<Medicine, Long>`

**描述**：药品服务接口，提供药品相关的业务操作

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 业务逻辑 |
|---------|---------|------|------|----------|
| `findByMedicineCode(String medicineCode)` | `Medicine` | `medicineCode`: 药品编码 | 根据药品编码查找药品 | 调用MedicineRepository的findByMedicineCode方法，返回药品对象或null |
| `findAll(Pageable pageable)` | `Page<Medicine>` | `pageable`: 分页参数 | 分页查找所有药品 | 调用MedicineRepository的findAll方法，返回分页后的药品列表 |
| `findByStatus(Integer status, Pageable pageable)` | `Page<Medicine>` | `status`: 状态<br>`pageable`: 分页参数 | 根据状态分页查找药品 | 调用MedicineRepository的findByStatus方法，返回指定状态的分页药品列表 |
| `searchMedicines(String keyword)` | `List<Medicine>` | `keyword`: 搜索关键字 | 搜索药品 | 调用MedicineRepository的searchMedicines方法，返回包含关键字的药品列表 |
| `findByCategoryId(Long categoryId)` | `List<Medicine>` | `categoryId`: 分类ID | 根据分类ID查找药品 | 调用MedicineRepository的findByCategoryId方法，返回指定分类下的药品列表 |
| `updatePrice(Long id, BigDecimal retailPrice, BigDecimal purchasePrice)` | `Medicine` | `id`: 药品ID<br>`retailPrice`: 零售价<br>`purchasePrice`: 采购价 | 更新药品价格 | 1. 调用findById方法查找指定ID的药品<br>2. 如果药品存在，更新零售价和采购价<br>3. 调用save方法保存更新后的药品，返回更新后的药品对象 |
| `countByStatus(Integer status)` | `long` | `status`: 状态 | 统计指定状态的药品数量 | 调用MedicineRepository的countByStatus方法，返回指定状态的药品数量 |
| `findSeasonalMedicines(Pageable pageable)` | `Page<Medicine>` | `pageable`: 分页参数 | 查找季节性药品 | 1. 调用MedicineRepository的findByStatus方法获取所有启用状态的药品<br>2. 过滤出季节性药品<br>3. 进行分页处理，返回分页后的药品列表 |
| `findPrescriptionMedicines(Pageable pageable)` | `Page<Medicine>` | `pageable`: 分页参数 | 查找处方药 | 1. 调用MedicineRepository的findByStatus方法获取所有启用状态的药品<br>2. 过滤出处方药<br>3. 进行分页处理，返回分页后的药品列表 |
| `findByManufacturer(String manufacturer, Pageable pageable)` | `Page<Medicine>` | `manufacturer`: 生产厂家<br>`pageable`: 分页参数 | 根据生产厂家查找药品 | 1. 调用MedicineRepository的findByManufacturerContaining方法获取指定生产厂家的药品<br>2. 进行分页处理，返回分页后的药品列表 |
| `findLowStockMedicines(Pageable pageable)` | `Page<Medicine>` | `pageable`: 分页参数 | 查找库存不足的药品 | 1. 调用MedicineRepository的findByStatus方法获取所有启用状态的药品<br>2. 对每个药品，调用MedicineRepository的sumCurrentStockByMedicineId方法获取当前库存量<br>3. 调用MedicineRepository的findStocksByMedicineId方法获取药品的库存记录，计算最小预警阈值<br>4. 过滤出库存量小于等于预警阈值的药品<br>5. 进行分页处理，返回分页后的药品列表 |
| `getMedicineSalesStatistics(Long medicineId)` | `Map<String, Object>` | `medicineId`: 药品ID | 获取药品销售统计信息 | 1. 调用MedicineRepository的findById方法查找指定ID的药品<br>2. 调用MedicineRepository的sumSaleQuantityByMedicineId方法统计药品的销售总量<br>3. 调用MedicineRepository的findSaleTrendByMedicineId方法获取药品的销售趋势<br>4. 构建并返回统计信息映射 |
| `getMedicinePurchaseStatistics(Long medicineId)` | `Map<String, Object>` | `medicineId`: 药品ID | 获取药品采购统计信息 | 1. 调用MedicineRepository的findById方法查找指定ID的药品<br>2. 调用MedicineRepository的sumPurchaseQuantityByMedicineId方法统计药品的采购总量<br>3. 构建并返回统计信息映射 |
| `batchUpdateStatus(List<Long> medicineIds, Integer status)` | `void` | `medicineIds`: 药品ID列表<br>`status`: 状态 | 批量更新药品状态 | 1. 调用MedicineRepository的findAllById方法获取指定ID的药品列表<br>2. 对每个药品设置新状态<br>3. 调用MedicineRepository的saveAll方法保存更新后的药品列表 |
| `findExpiringMedicines(int daysThreshold, Pageable pageable)` | `Page<Medicine>` | `daysThreshold`: 天数阈值<br>`pageable`: 分页参数 | 查找即将过期的药品 | 1. 计算当前日期和阈值日期<br>2. 调用StockRepository的findExpiringStock方法获取即将过期的库存<br>3. 提取不重复的药品<br>4. 进行分页处理，返回分页后的药品列表 |
| `findByStorageRequirement(Integer storageRequirement, Pageable pageable)` | `Page<Medicine>` | `storageRequirement`: 存储要求<br>`pageable`: 分页参数 | 根据存储要求查找药品 | 1. 调用MedicineRepository的findByStatus方法获取所有启用状态的药品<br>2. 过滤出存储要求匹配的药品<br>3. 进行分页处理，返回分页后的药品列表 |
| `searchMedicinesWithPagination(String keyword, Pageable pageable)` | `Page<Medicine>` | `keyword`: 搜索关键字<br>`pageable`: 分页参数 | 分页搜索药品 | 1. 调用MedicineRepository的searchMedicines方法获取包含关键字的药品列表<br>2. 进行分页处理，返回分页后的药品列表 |

---

### 4. 接口：PredictionResultService（预测结果服务接口）

**位置**：`com.example.demo.service`

**继承关系**：`extends BaseService<PredictionResult, Long>`

**描述**：预测结果服务接口，提供预测结果相关的业务操作

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 业务逻辑 |
|---------|---------|------|------|----------|
| `findByMedicineId(Long medicineId)` | `List<PredictionResult>` | `medicineId`: 药品ID | 根据药品ID查找预测结果 | 调用PredictionResultRepository的findByMedicineId方法，返回指定药品的预测结果列表 |
| `findByPredictionDate(LocalDate predictionDate)` | `List<PredictionResult>` | `predictionDate`: 预测日期 | 根据预测日期查找预测结果 | 调用PredictionResultRepository的findByPredictionDate方法，返回指定预测日期的预测结果列表 |
| `findLatestByMedicineId(Long medicineId)` | `PredictionResult` | `medicineId`: 药品ID | 查找药品的最新预测结果 | 调用PredictionResultRepository的findFirstByMedicineIdOrderByPredictionDateDesc方法，返回药品的最新预测结果 |
| `findByPredictionDateRange(LocalDate startDate, LocalDate endDate)` | `List<PredictionResult>` | `startDate`: 开始日期<br>`endDate`: 结束日期 | 查找指定日期范围内的预测结果 | 调用PredictionResultRepository的findByPredictionDateBetween方法，返回指定日期范围内的预测结果列表 |
| `findNeedReprediction(Double threshold)` | `List<PredictionResult>` | `threshold`: 准确率阈值 | 查找需要重新预测的结果 | 1. 将Double类型的阈值转换为BigDecimal类型<br>2. 调用PredictionResultRepository的findNeedReprediction方法，返回需要重新预测的结果列表 |
| `getAverageAccuracyByModel()` | `Map<String, Double>` | 无 | 获取各模型的平均准确率 | 1. 调用PredictionResultRepository的findAverageAccuracyByModel方法获取各模型的平均准确率数据<br>2. 转换为模型类型到平均准确率的映射，返回映射 |
| `generatePrediction(Long medicineId, String modelType, LocalDate predictionDate)` | `PredictionResult` | `medicineId`: 药品ID<br>`modelType`: 模型类型<br>`predictionDate`: 预测日期 | 生成预测结果 | 1. 调用MedicineService的findById方法查找指定ID的药品<br>2. 如果药品存在，创建预测结果对象，设置药品、模型类型、预测日期等属性<br>3. 模拟预测过程，生成预测需求量和建议订购数量<br>4. 调用save方法保存预测结果，返回保存后的预测结果 |
| `generateBatchPredictions(List<Long> medicineIds, String modelType, LocalDate startDate, LocalDate endDate)` | `List<PredictionResult>` | `medicineIds`: 药品ID列表<br>`modelType`: 模型类型<br>`startDate`: 开始日期<br>`endDate`: 结束日期 | 批量生成预测结果 | 1. 遍历药品ID列表<br>2. 对每个药品ID，调用generatePrediction方法生成预测结果<br>3. 收集所有预测结果，返回预测结果列表 |
| `getRecommendedOrderQuantities(LocalDate targetDate)` | `Map<Long, Integer>` | `targetDate`: 目标日期 | 获取建议订购数量 | 1. 调用PredictionResultRepository的findByPredictionDate方法获取指定目标日期的预测结果列表<br>2. 构建药品ID到建议订购数量的映射，返回映射 |

---

### 5. 接口：PurchaseOrderService（采购订单服务接口）

**位置**：`com.example.demo.service`

**继承关系**：`extends BaseService<PurchaseOrder, Long>`

**描述**：采购订单服务接口，提供采购订单相关的业务操作

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 业务逻辑 |
|---------|---------|------|------|----------|
| `findByOrderNo(String orderNo)` | `PurchaseOrder` | `orderNo`: 订单编号 | 根据订单编号查找订单 | 调用PurchaseOrderRepository的findByOrderNo方法，返回订单对象或null |
| `findAll(Pageable pageable)` | `Page<PurchaseOrder>` | `pageable`: 分页参数 | 分页查找所有订单 | 调用PurchaseOrderRepository的findAll方法，返回分页后的订单列表 |
| `findByOrderStatus(Integer orderStatus, Pageable pageable)` | `Page<PurchaseOrder>` | `orderStatus`: 订单状态<br>`pageable`: 分页参数 | 根据订单状态分页查找订单 | 1. 调用PurchaseOrderRepository的findByOrderStatus方法获取指定状态的订单列表<br>2. 进行分页处理，返回分页后的订单列表 |
| `findByMedicineId(Long medicineId, Pageable pageable)` | `Page<PurchaseOrder>` | `medicineId`: 药品ID<br>`pageable`: 分页参数 | 根据药品ID分页查找订单 | 1. 调用PurchaseOrderRepository的findByMedicineId方法获取指定药品的订单列表<br>2. 进行分页处理，返回分页后的订单列表 |
| `findPendingOrders(Pageable pageable)` | `Page<PurchaseOrder>` | `pageable`: 分页参数 | 查找待处理的订单 | 1. 调用PurchaseOrderRepository的findPendingOrders方法获取待处理的订单列表<br>2. 进行分页处理，返回分页后的订单列表 |
| `findOverdueOrders(Pageable pageable)` | `Page<PurchaseOrder>` | `pageable`: 分页参数 | 查找过期的订单 | 1. 调用PurchaseOrderRepository的findOverdueOrders方法获取过期的订单列表<br>2. 进行分页处理，返回分页后的订单列表 |
| `searchOrders(String keyword, Pageable pageable)` | `Page<PurchaseOrder>` | `keyword`: 搜索关键字<br>`pageable`: 分页参数 | 搜索订单 | 1. 调用PurchaseOrderRepository的findByKeywordContaining方法获取包含关键字的订单列表<br>2. 进行分页处理，返回分页后的订单列表 |
| `findBySupplierContaining(String supplier, Pageable pageable)` | `Page<PurchaseOrder>` | `supplier`: 供应商<br>`pageable`: 分页参数 | 根据供应商查找订单 | 1. 调用PurchaseOrderRepository的findBySupplierContaining方法获取指定供应商的订单列表<br>2. 进行分页处理，返回分页后的订单列表 |
| `findByOrderTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable)` | `Page<PurchaseOrder>` | `startTime`: 开始时间<br>`endTime`: 结束时间<br>`pageable`: 分页参数 | 根据下单时间范围查找订单 | 1. 调用PurchaseOrderRepository的findByOrderTimeBetween方法获取指定时间范围内的订单列表<br>2. 进行分页处理，返回分页后的订单列表 |
| `getTotalPurchasedQuantity(Long medicineId)` | `Integer` | `medicineId`: 药品ID | 获取药品的采购总量 | 调用PurchaseOrderRepository的sumPurchasedQuantityByMedicineId方法，返回采购总量 |
| `getTotalPurchaseAmountByPeriod(LocalDateTime startTime, LocalDateTime endTime)` | `Double` | `startTime`: 开始时间<br>`endTime`: 结束时间 | 获取指定时间段的采购总额 | 调用PurchaseOrderRepository的sumTotalAmountByPeriod方法，返回采购总额 |
| `createOrder(PurchaseOrder order, Long operatorId)` | `PurchaseOrder` | `order`: 订单对象<br>`operatorId`: 操作员ID | 创建订单 | 1. 调用UserService的findById方法查找操作员<br>2. 如果操作员存在，设置订单的操作员<br>3. 如果订单编号为空，生成订单编号<br>4. 如果单价和数量不为空，计算总金额<br>5. 调用PurchaseOrderRepository的save方法保存订单，返回保存后的订单 |
| `confirmOrder(Long orderId)` | `PurchaseOrder` | `orderId`: 订单ID | 确认订单 | 1. 调用findById方法查找指定ID的订单<br>2. 如果订单存在且为待处理状态，将订单状态设置为已确认<br>3. 调用PurchaseOrderRepository的save方法保存订单，返回保存后的订单 |
| `markAsArrived(Long orderId)` | `PurchaseOrder` | `orderId`: 订单ID | 标记订单为已到货 | 1. 调用findById方法查找指定ID的订单<br>2. 如果订单存在且为待处理或已确认状态，将订单状态设置为已到货<br>3. 设置实际到货时间为当前时间<br>4. 调用PurchaseOrderRepository的save方法保存订单，返回保存后的订单 |
| `cancelOrder(Long orderId)` | `PurchaseOrder` | `orderId`: 订单ID | 取消订单 | 1. 调用findById方法查找指定ID的订单<br>2. 如果订单存在且未到货且未取消，将订单状态设置为已取消<br>3. 调用PurchaseOrderRepository的save方法保存订单，返回保存后的订单 |
| `getOrderStatistics()` | `Map<String, Object>` | 无 | 获取订单统计信息 | 1. 调用findByOrderStatus方法获取各状态的订单数量<br>2. 调用getTotalPurchaseAmountByPeriod方法获取最近一个月的采购总额<br>3. 调用findOverdueOrders方法获取过期订单数量<br>4. 构建并返回统计信息映射 |
| `countByStatus()` | `Map<String, Long>` | 无 | 统计各状态的订单数量 | 1. 遍历订单状态（0-3）<br>2. 调用PurchaseOrderRepository的findByOrderStatus方法获取每个状态的订单列表<br>3. 统计每个状态的订单数量，构建并返回映射 |
| `getMonthlyStatistics(LocalDate startDate, LocalDate endDate)` | `Map<String, Object>` | `startDate`: 开始日期<br>`endDate`: 结束日期 | 获取月度统计信息 | 1. 将LocalDate转换为LocalDateTime<br>2. 调用PurchaseOrderRepository的findByOrderTimeBetween方法获取指定时间范围内的订单列表<br>3. 按状态分类订单<br>4. 计算各状态订单数量、采购金额统计、按药品统计、按供应商统计、按月份统计<br>5. 构建并返回统计信息映射 |
| `getSupplierPurchaseStatistics(Pageable pageable)` | `Page<Map<String, Object>>` | `pageable`: 分页参数 | 获取供应商采购统计信息 | 1. 调用PurchaseOrderRepository的findSupplierPurchaseStatistics方法获取供应商采购统计数据<br>2. 转换为Map列表<br>3. 进行分页处理，返回分页后的统计信息列表 |
| `findUpcomingOrders(Pageable pageable)` | `Page<PurchaseOrder>` | `pageable`: 分页参数 | 查找即将到期的订单 | 1. 调用PurchaseOrderRepository的findUpcomingOrders方法获取即将到期的订单列表<br>2. 进行分页处理，返回分页后的订单列表 |
| `batchConfirmOrders(List<Long> orderIds)` | `void` | `orderIds`: 订单ID列表 | 批量确认订单 | 1. 调用PurchaseOrderRepository的findAllById方法获取指定ID的订单列表<br>2. 对每个订单，如果存在且为待处理状态，将状态设置为已确认<br>3. 调用PurchaseOrderRepository的save方法保存更新后的订单 |
| `batchCancelOrders(List<Long> orderIds)` | `void` | `orderIds`: 订单ID列表 | 批量取消订单 | 1. 调用PurchaseOrderRepository的findAllById方法获取指定ID的订单列表<br>2. 对每个订单，如果存在且未到货且未取消，将状态设置为已取消<br>3. 调用PurchaseOrderRepository的save方法保存更新后的订单 |
| `getPurchaseSuggestions(Pageable pageable)` | `Page<Map<String, Object>>` | `pageable`: 分页参数 | 获取采购建议 | 1. 调用MedicineRepository的findByStatus方法获取所有启用状态的药品<br>2. 计算最近30天的销售数据<br>3. 为每种药品计算当前库存量和平均日销售量<br>4. 如果当前库存低于安全库存，生成采购建议<br>5. 进行分页处理，返回分页后的采购建议列表 |
| `getPurchaseByCategory(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable)` | `Page<Map<String, Object>>` | `startDate`: 开始时间<br>`endDate`: 结束时间<br>`pageable`: 分页参数 | 按分类获取采购信息 | 1. 调用PurchaseOrderRepository的findByOrderTimeBetween方法获取指定时间范围内的订单列表<br>2. 按分类分组，计算每个分类的采购数量和金额<br>3. 进行分页处理，返回分页后的分类采购信息列表 |
| `getOrderDetailsWithMedicine(Long orderId)` | `Map<String, Object>` | `orderId`: 订单ID | 获取订单详情（包含药品信息） | 1. 调用PurchaseOrderRepository的findById方法查找指定ID的订单<br>2. 如果订单存在，构建订单详情映射，包含订单基本信息和药品信息<br>3. 返回订单详情映射 |

---

### 6. 接口：SaleRecordService（销售记录服务接口）

**位置**：`com.example.demo.service`

**继承关系**：`extends BaseService<SaleRecord, Long>`

**描述**：销售记录服务接口，提供销售记录相关的业务操作

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 业务逻辑 |
|---------|---------|------|------|----------|
| `findByRecordNo(String recordNo)` | `SaleRecord` | `recordNo`: 销售单号 | 根据销售单号查找销售记录 | 调用SaleRecordRepository的findByRecordNo方法，返回销售记录对象或null |
| `findAll(Pageable pageable)` | `Page<SaleRecord>` | `pageable`: 分页参数 | 分页查找所有销售记录 | 调用SaleRecordRepository的findAll方法，返回分页后的销售记录列表 |
| `findBySaleTimeBetween(LocalDateTime startTime, LocalDateTime endTime)` | `List<SaleRecord>` | `startTime`: 开始时间<br>`endTime`: 结束时间 | 根据销售时间范围查找销售记录 | 调用SaleRecordRepository的findBySaleTimeBetween方法，返回指定时间范围内的销售记录列表 |
| `findByMedicineId(Long medicineId)` | `List<SaleRecord>` | `medicineId`: 药品ID | 根据药品ID查找销售记录 | 调用SaleRecordRepository的findByMedicineId方法，返回指定药品的销售记录列表 |
| `findByOperatorId(Long operatorId)` | `List<SaleRecord>` | `operatorId`: 操作员ID | 根据操作员ID查找销售记录 | 调用SaleRecordRepository的findByOperatorId方法，返回指定操作员的销售记录列表 |
| `getTotalSalesByPeriod(LocalDateTime startTime, LocalDateTime endTime)` | `Double` | `startTime`: 开始时间<br>`endTime`: 结束时间 | 获取指定时间段的销售总额 | 调用SaleRecordRepository的sumTotalAmountByPeriod方法，返回销售总额 |
| `getTotalQuantityByMedicineId(Long medicineId)` | `Integer` | `medicineId`: 药品ID | 获取药品的销售总量 | 调用SaleRecordRepository的sumQuantityByMedicineId方法，返回销售总量 |
| `getDailySalesReport(LocalDateTime startDate, LocalDateTime endDate)` | `List<Map<String, Object>>` | `startDate`: 开始日期<br>`endDate`: 结束日期 | 获取每日销售报告 | 调用SaleRecordRepository的findDailySales方法，返回每日销售数据列表 |
| `getTopSellingMedicines(int limit, LocalDateTime startDate, LocalDateTime endDate)` | `List<Map<String, Object>>` | `limit`: 限制数量<br>`startDate`: 开始日期<br>`endDate`: 结束日期 | 获取最畅销的药品 | 1. 调用SaleRecordRepository的findTopSellingMedicines方法获取最畅销的药品数据<br>2. 转换为Map列表，返回指定数量的最畅销药品 |
| `createSaleRecord(SaleRecord saleRecord, Long operatorId)` | `SaleRecord` | `saleRecord`: 销售记录对象<br>`operatorId`: 操作员ID | 创建销售记录 | 1. 调用UserService的findById方法查找操作员<br>2. 如果操作员存在，设置销售记录的操作员<br>3. 如果销售单号为空，生成销售单号<br>4. 如果单价和数量不为空，计算总金额<br>5. 调用SaleRecordRepository的save方法保存销售记录，返回保存后的销售记录 |
| `findByCustomerType(Integer customerType, Pageable pageable)` | `Page<SaleRecord>` | `customerType`: 顾客类型<br>`pageable`: 分页参数 | 根据顾客类型查找销售记录 | 1. 调用SaleRecordRepository的findByCustomerType方法获取指定顾客类型的销售记录列表<br>2. 进行分页处理，返回分页后的销售记录列表 |
| `getSalesByCategory(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable)` | `Page<Map<String, Object>>` | `startDate`: 开始时间<br>`endDate`: 结束时间<br>`pageable`: 分页参数 | 按分类获取销售信息 | 1. 调用SaleRecordRepository的findBySaleTimeBetween方法获取指定时间范围内的销售记录列表<br>2. 按分类分组，计算每个分类的销售数量和金额<br>3. 进行分页处理，返回分页后的分类销售信息列表 |
| `getSalesBySymptom(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable)` | `Page<Map<String, Object>>` | `startDate`: 开始时间<br>`endDate`: 结束时间<br>`pageable`: 分页参数 | 按症状获取销售信息 | 1. 调用SaleRecordRepository的findSalesBySymptom方法获取指定时间范围内的按症状销售数据<br>2. 进行分页处理，返回分页后的症状销售信息列表 |
| `getOperatorSalesPerformance(LocalDateTime startDate, LocalDateTime endDate)` | `Map<String, Object>` | `startDate`: 开始时间<br>`endDate`: 结束时间 | 获取操作员销售业绩 | 1. 调用SaleRecordRepository的findOperatorSalesPerformance方法获取操作员销售业绩数据<br>2. 构建并返回销售业绩映射 |
| `getMonthlySalesTrend(int months, Pageable pageable)` | `Page<Map<String, Object>>` | `months`: 月数<br>`pageable`: 分页参数 | 获取月度销售趋势 | 1. 计算开始日期（当前日期减去指定月数）<br>2. 调用SaleRecordRepository的findDailySalesAmount方法获取指定时间范围内的每日销售数据<br>3. 按月份分组，计算每月销售总额<br>4. 进行分页处理，返回分页后的月度销售趋势列表 |
| `getSalesPrediction(int days, Pageable pageable)` | `Page<Map<String, Object>>` | `days`: 天数<br>`pageable`: 分页参数 | 获取销售预测 | 1. 计算开始日期（当前日期减去90天）和结束日期（当前日期加上指定天数）<br>2. 调用SaleRecordRepository的findDailySales方法获取历史销售数据<br>3. 基于历史数据生成未来销售预测<br>4. 进行分页处理，返回分页后的销售预测列表 |
| `findPrescriptionSales(Pageable pageable)` | `Page<SaleRecord>` | `pageable`: 分页参数 | 查找处方药销售记录 | 1. 调用SaleRecordRepository的findAll方法获取所有销售记录<br>2. 过滤出处方药销售记录<br>3. 进行分页处理，返回分页后的处方药销售记录列表 |
| `getSalesStatisticsByPeriod(LocalDateTime startDate, LocalDateTime endDate)` | `Map<String, Object>` | `startDate`: 开始时间<br>`endDate`: 结束时间 | 获取指定时间段的销售统计信息 | 1. 调用SaleRecordRepository的findBySaleTimeBetween方法获取指定时间范围内的销售记录列表<br>2. 计算销售总额、销售总量、平均客单价<br>3. 按药品、症状、操作员分组统计<br>4. 构建并返回统计信息映射 |

---

### 7. 接口：StockService（库存服务接口）

**位置**：`com.example.demo.service`

**继承关系**：`extends BaseService<Stock, Long>`

**描述**：库存服务接口，提供库存相关的业务操作

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 业务逻辑 |
|---------|---------|------|------|----------|
| `findByMedicineId(Long medicineId, Pageable pageable)` | `Page<Stock>` | `medicineId`: 药品ID<br>`pageable`: 分页参数 | 根据药品ID分页查找库存 | 1. 调用StockRepository的findByMedicineId方法获取指定药品的库存列表<br>2. 进行分页处理，返回分页后的库存列表 |
| `getTotalStock(Long medicineId)` | `Integer` | `medicineId`: 药品ID | 获取药品的总库存量 | 调用StockRepository的sumQuantityByMedicineId方法，返回总库存量 |
| `getExpiringStock(LocalDate startDate, LocalDate endDate, Pageable pageable)` | `Page<Stock>` | `startDate`: 开始日期<br>`endDate`: 结束日期<br>`pageable`: 分页参数 | 查找即将过期的库存 | 1. 调用StockRepository的findExpiringStock方法获取指定日期范围内即将过期的库存列表<br>2. 进行分页处理，返回分页后的库存列表 |
| `getLowStock(Pageable pageable)` | `Page<Stock>` | `pageable`: 分页参数 | 查找库存不足的库存 | 1. 调用StockRepository的findLowStock方法获取库存不足的库存列表<br>2. 进行分页处理，返回分页后的库存列表 |
| `getLowStockSummary()` | `Map<Long, Integer>` | 无 | 获取库存不足的药品摘要 | 1. 调用StockRepository的findLowStockSummary方法获取库存不足的药品数据<br>2. 转换为药品ID到库存量的映射，返回映射 |
| `reduceStock(Long medicineId, Integer quantity)` | `void` | `medicineId`: 药品ID<br>`quantity`: 减少数量 | 减少库存 | 1. 调用StockRepository的findByMedicineIdAndStatus方法获取指定药品的有效库存列表<br>2. 按有效期升序排序库存列表<br>3. 遍历库存列表，减少库存数量，直到达到指定减少数量<br>4. 调用StockRepository的save方法保存更新后的库存 |
| `increaseStock(Long medicineId, Integer quantity, String batchNumber, LocalDate expirationDate)` | `void` | `medicineId`: 药品ID<br>`quantity`: 增加数量<br>`batchNumber`: 批号<br>`expirationDate`: 有效期 | 增加库存 | 1. 调用StockRepository的findByMedicineIdAndBatchNumber方法查找指定药品和批号的库存<br>2. 如果存在，增加库存数量；如果不存在，创建新库存记录<br>3. 调用StockRepository的save方法保存更新或新创建的库存 |
| `checkStockAvailability(Long medicineId, Integer requiredQuantity)` | `boolean` | `medicineId`: 药品ID<br>`requiredQuantity`: 所需数量 | 检查库存是否充足 | 1. 调用StockRepository的sumQuantityByMedicineId方法获取指定药品的总库存量<br>2. 比较总库存量与所需数量，返回布尔值 |
| `findByStatus(Integer status, Pageable pageable)` | `Page<Stock>` | `status`: 状态<br>`pageable`: 分页参数 | 根据状态分页查找库存 | 1. 调用StockRepository的findByStatus方法获取指定状态的库存列表<br>2. 进行分页处理，返回分页后的库存列表 |
| `findByBatchNumber(String batchNumber, Pageable pageable)` | `Page<Stock>` | `batchNumber`: 批号<br>`pageable`: 分页参数 | 根据批号分页查找库存 | 1. 调用StockRepository的findByBatchNumber方法获取指定批号的库存列表<br>2. 进行分页处理，返回分页后的库存列表 |
| `findByShelfLocation(String shelfLocation, Pageable pageable)` | `Page<Stock>` | `shelfLocation`: 货架位置<br>`pageable`: 分页参数 | 根据货架位置分页查找库存 | 1. 调用StockRepository的findByShelfLocation方法获取指定货架位置的库存列表<br>2. 进行分页处理，返回分页后的库存列表 |
| `findExpiringWithinDays(int days)` | `List<Stock>` | `days`: 天数 | 查找指定天数内过期的库存 | 1. 计算开始日期（当前日期）和结束日期（当前日期加上指定天数）<br>2. 调用StockRepository的findExpiringStock方法获取指定日期范围内即将过期的库存列表 |
| `getStockStatisticsByMedicine(Long medicineId)` | `Map<String, Object>` | `medicineId`: 药品ID | 获取药品的库存统计信息 | 1. 调用StockRepository的findByMedicineId方法获取指定药品的库存列表<br>2. 计算总库存量、过期库存量、即将过期库存量、库存价值<br>3. 构建并返回统计信息映射 |
| `findByMedicineIdAndBatchNumber(Long medicineId, String batchNumber, Pageable pageable)` | `Page<Stock>` | `medicineId`: 药品ID<br>`batchNumber`: 批号<br>`pageable`: 分页参数 | 根据药品ID和批号分页查找库存 | 1. 调用StockRepository的findByMedicineId和findByBatchNumber方法获取指定药品和批号的库存列表<br>2. 进行分页处理，返回分页后的库存列表 |
| `findExpiredStock(Pageable pageable)` | `Page<Stock>` | `pageable`: 分页参数 | 查找过期的库存 | 1. 调用StockRepository的findAll方法获取所有库存记录<br>2. 在内存中过滤出过期（有效期在当前日期之前）且状态为1的库存<br>3. 进行分页处理，返回分页后的库存列表 |
| `findNearExpiryStock(int days, Pageable pageable)` | `Page<Stock>` | `days`: 天数<br>`pageable`: 分页参数 | 查找即将过期的库存 | 1. 计算开始日期（当前日期）和结束日期（当前日期加上指定天数）<br>2. 调用StockRepository的findExpiringStock方法获取指定日期范围内即将过期的库存列表<br>3. 进行分页处理，返回分页后的库存列表 |
| `calculateStockTurnoverRate(String period)` | `Double` | `period`: 时间段 | 计算库存周转率 | 1. 根据时间段参数计算开始日期<br>2. 调用StockRepository的calculateStockTurnoverRate方法获取库存周转率数据<br>3. 计算平均库存周转率，返回结果 |
| `calculateTotalStockValue()` | `Double` | 无 | 计算库存总价值 | 调用StockRepository的calculateTotalStockValue方法，返回库存总价值 |
| `getStockValueByCategory()` | `Map<String, Object>` | 无 | 按分类获取库存价值 | 1. 调用StockRepository的calculateStockValueByCategory方法获取按分类的库存价值数据<br>2. 构建并返回分类库存价值映射 |
| `getStockAlerts(Pageable pageable)` | `Page<Stock>` | `pageable`: 分页参数 | 获取库存预警 | 1. 调用StockRepository的findLowStock方法获取库存不足的库存列表<br>2. 调用StockRepository的findExpiringStock方法获取即将过期的库存列表<br>3. 合并两个列表，去重<br>4. 进行分页处理，返回分页后的库存预警列表 |
| `transferStock(Long fromStockId, Long toStockId, Integer quantity)` | `void` | `fromStockId`: 源库存ID<br>`toStockId`: 目标库存ID<br>`quantity`: 转移数量 | 转移库存 | 1. 调用findById方法查找源库存和目标库存<br>2. 如果两个库存都存在，且源库存数量大于等于转移数量，减少源库存数量，增加目标库存数量<br>3. 调用StockRepository的save方法保存更新后的库存 |
| `setMinimumStockLevel(Long medicineId, Integer minLevel)` | `void` | `medicineId`: 药品ID<br>`minLevel`: 最小库存水平 | 设置最小库存水平 | 1. 调用StockRepository的findByMedicineId方法获取指定药品的库存列表<br>2. 对每个库存记录，设置最小订购数量为最小库存水平<br>3. 调用StockRepository的saveAll方法保存更新后的库存列表 |
| `getStockInventoryReport()` | `Map<Long, Object>` | 无 | 获取库存库存报告 | 1. 调用StockRepository的findAll方法获取所有库存记录<br>2. 遍历库存记录，为每个库存创建包含数量、批号、有效期、货架位置和状态的信息映射<br>3. 以库存记录ID为键构建并返回报告映射 |
| `findByStorageCondition(Integer condition, Pageable pageable)` | `Page<Stock>` | `condition`: 存储条件<br>`pageable`: 分页参数 | 根据存储条件分页查找库存 | 1. 调用StockRepository的findAll方法获取所有库存记录<br>2. 过滤出存储条件匹配的库存记录<br>3. 进行分页处理，返回分页后的库存列表 |
| `findByShelfLocationContaining(String location, Pageable pageable)` | `Page<Stock>` | `location`: 货架位置关键字<br>`pageable`: 分页参数 | 根据货架位置关键字分页查找库存 | 1. 调用StockRepository的findByShelfLocationContaining方法获取货架位置包含关键字的库存列表<br>2. 进行分页处理，返回分页后的库存列表 |

---

### 8. 接口：SymptomService（症状服务接口）

**位置**：`com.example.demo.service`

**继承关系**：`extends BaseService<Symptom, Integer>`

**描述**：症状服务接口，提供症状相关的业务操作

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 业务逻辑 |
|---------|---------|------|------|----------|
| `findByName(String name)` | `Symptom` | `name`: 症状名称 | 根据名称查找症状 | 调用SymptomRepository的findByName方法，返回症状对象或null |
| `findByNameContaining(String name)` | `List<Symptom>` | `name`: 症状名称关键字 | 根据名称模糊查找症状 | 调用SymptomRepository的findByNameContaining方法，返回名称包含关键字的症状列表 |
| `existsByName(String name)` | `boolean` | `name`: 症状名称 | 检查症状名称是否存在 | 调用SymptomRepository的existsByName方法，返回布尔值 |
| `findByDescriptionContaining(String description)` | `List<Symptom>` | `description`: 描述关键字 | 根据描述模糊查找症状 | 调用SymptomRepository的findByDescriptionContaining方法，返回描述包含关键字的症状列表 |
| `searchSymptoms(String keyword)` | `List<Symptom>` | `keyword`: 搜索关键字 | 搜索症状 | 调用SymptomRepository的searchSymptoms方法，返回包含关键字的症状列表 |
| `findAll(Pageable pageable)` | `Page<Symptom>` | `pageable`: 分页参数 | 分页查找所有症状 | 调用SymptomRepository的findAll方法，返回分页后的症状列表 |
| `searchSymptoms(String keyword, Pageable pageable)` | `Page<Symptom>` | `keyword`: 搜索关键字<br>`pageable`: 分页参数 | 分页搜索症状 | 1. 调用SymptomRepository的searchSymptoms方法获取包含关键字的症状列表<br>2. 进行分页处理，返回分页后的症状列表 |
| `saveAll(List<Symptom> symptoms)` | `List<Symptom>` | `symptoms`: 症状列表 | 批量保存症状 | 调用SymptomRepository的saveAll方法，返回保存后的症状列表 |
| `countAll()` | `long` | 无 | 统计所有症状数量 | 调用SymptomRepository的count方法，返回症状总数量 |
| `findByMedicineId(Long medicineId, Pageable pageable)` | `Page<Symptom>` | `medicineId`: 药品ID<br>`pageable`: 分页参数 | 根据药品ID分页查找症状 | 1. 调用SymptomRepository的findMedicinesBySymptomId方法获取关联指定药品的症状列表<br>2. 进行分页处理，返回分页后的症状列表 |
| `findMostCommonSymptoms(int limit, LocalDateTime start, LocalDateTime end, Pageable pageable)` | `Page<Symptom>` | `limit`: 限制数量<br>`start`: 开始时间<br>`end`: 结束时间<br>`pageable`: 分页参数 | 查找最常见的症状 | 1. 调用SymptomRepository的findMostCommonSymptoms方法获取指定时间范围内最常见的症状数据<br>2. 根据症状ID查找症状对象<br>3. 进行分页处理，返回分页后的症状列表 |
| `getSymptomUsageStatistics(Integer symptomId)` | `Map<String, Object>` | `symptomId`: 症状ID | 获取症状使用统计信息 | 1. 调用SymptomRepository的findById方法查找指定ID的症状<br>2. 调用SymptomRepository的countMedicinesBySymptomId方法统计关联的药品数量<br>3. 调用SymptomRepository的countSaleRecordsBySymptomId方法统计关联的销售记录数量<br>4. 调用SymptomRepository的sumSaleAmountBySymptomId方法统计关联的销售总额<br>5. 构建并返回统计信息映射 |
| `findSymptomsWithMedicines(Pageable pageable)` | `Page<Symptom>` | `pageable`: 分页参数 | 查找有关联药品的症状 | 1. 调用SymptomRepository的findAll方法获取所有症状<br>2. 过滤有关联药品的症状<br>3. 进行分页处理，返回分页后的症状列表 |
| `findBySaleRecordId(Long saleRecordId, Pageable pageable)` | `Page<Symptom>` | `saleRecordId`: 销售记录ID<br>`pageable`: 分页参数 | 根据销售记录ID分页查找症状 | 1. 调用SymptomRepository的findSaleRecordsBySymptomId方法获取关联指定销售记录的症状列表<br>2. 进行分页处理，返回分页后的症状列表 |

---

### 9. 接口：UserService（用户服务接口）

**位置**：`com.example.demo.service`

**继承关系**：`extends BaseService<User, Long>`

**描述**：用户服务接口，提供用户相关的业务操作

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 业务逻辑 |
|---------|---------|------|------|----------|
| `findByUsername(String username)` | `User` | `username`: 用户名 | 根据用户名查找用户 | 调用UserRepository的findByUsername方法，返回用户对象或null |
| `login(String username, String password)` | `User` | `username`: 用户名<br>`password`: 密码 | 用户登录 | 1. 调用findByUsername方法查找用户<br>2. 如果用户存在，调用matchPassword方法验证密码<br>3. 返回验证通过的用户对象或null |
| `matchPassword(User user, String password)` | `User` | `user`: 用户对象<br>`password`: 密码 | 验证密码 | 1. 使用PasswordEncoder验证密码是否匹配<br>2. 如果匹配成功返回用户对象，否则返回null |
| `updatePassword(User user)` | `User` | `user`: 用户对象 | 更新密码 | 1. 检查密码是否已加密，如未加密则使用PasswordEncoder进行加密<br>2. 调用父类save方法保存用户对象，返回更新后的用户对象 |
| `findAll(Pageable pageable)` | `Page<User>` | `pageable`: 分页参数 | 分页查找所有用户 | 调用UserRepository的findAll方法，返回分页后的用户列表 |
| `findByRole(String role)` | `List<User>` | `role`: 角色 | 根据角色查找用户 | 调用UserRepository的findByRole方法，返回指定角色的用户列表 |
| `changeStatus(Long id, Integer status)` | `User` | `id`: 用户ID<br>`status`: 状态 | 更改用户状态 | 1. 调用findById方法查找指定ID的用户<br>2. 如果用户存在，更新状态<br>3. 调用save方法保存更新后的用户，返回更新后的用户对象 |
| `existsByUsername(String username)` | `boolean` | `username`: 用户名 | 检查用户名是否存在 | 调用UserRepository的existsByUsername方法，返回布尔值 |
| `findByKeyword(String keyword, Pageable pageable)` | `Page<User>` | `keyword`: 搜索关键字<br>`pageable`: 分页参数 | 根据关键字分页查找用户 | 1. 调用UserRepository的searchUsers方法获取包含关键字的用户列表<br>2. 进行分页处理，返回分页后的用户列表 |
| `countByUserStatus(Integer userStatus)` | `int` | `userStatus`: 用户状态 | 统计指定状态的用户数量 | 1. 调用UserRepository的findByStatus方法获取指定状态的用户列表<br>2. 返回列表大小 |
| `countByRole(String role)` | `int` | `role`: 角色 | 统计指定角色的用户数量 | 1. 调用UserRepository的findByRole方法获取指定角色的用户列表<br>2. 返回列表大小 |
| `countAll()` | `int` | 无 | 统计所有用户数量 | 调用UserRepository的count方法，返回用户总数量 |

---
## 服务实现层 (Service Implementation Layer) 文档 - 

### 1. 类：BaseServiceImpl（基础服务实现）

**位置**：`com.example.demo.service.impl`

**继承关系**：`implements BaseService<T, ID>`

**类注解说明**：
- `@Transactional`：Spring注解，声明事务支持

**描述**：所有服务实现的基类，提供通用的CRUD操作实现

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 业务逻辑 |
|---------|---------|------|------|----------|
| `save(T entity)` | `T` | `entity`: 实体对象 | 保存实体 | 调用repository的save方法保存实体对象，返回保存后的实体 |
| `update(T entity)` | `T` | `entity`: 实体对象 | 更新实体 | 调用repository的save方法更新实体对象，返回更新后的实体 |
| `delete(ID id)` | `void` | `id`: 实体ID | 删除实体 | 调用repository的deleteById方法删除指定ID的实体 |
| `findById(ID id)` | `T` | `id`: 实体ID | 根据ID查找实体 | 调用repository的findById方法查找指定ID的实体，返回实体对象或null |
| `findAll()` | `List<T>` | 无 | 查找所有实体 | 调用repository的findAll方法返回所有实体列表 |
| `findAll(Pageable pageable)` | `Page<T>` | `pageable`: 分页参数 | 分页查找所有实体 | 调用repository的findAll方法返回分页后的实体列表 |
| `saveAll(List<T> entities)` | `List<T>` | `entities`: 实体列表 | 批量保存实体 | 调用repository的saveAll方法批量保存实体列表，返回保存后的实体列表 |
| `deleteAll(List<ID> ids)` | `void` | `ids`: 实体ID列表 | 批量删除实体 | 遍历ID列表，调用repository的deleteById方法逐个删除指定ID的实体 |
| `exists(ID id)` | `boolean` | `id`: 实体ID | 检查实体是否存在 | 调用repository的existsById方法检查指定ID的实体是否存在，返回布尔值 |

---

### 2. 类：CategoryServiceImpl（分类服务实现）

**位置**：`com.example.demo.service.impl`

**继承关系**：`extends BaseServiceImpl<Category, Long, CategoryRepository> implements CategoryService`

**类注解说明**：
- `@Service`：Spring注解，标识为服务组件
- `@Transactional`：Spring注解，声明事务支持

**描述**：分类服务的具体实现，提供分类相关的业务操作

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 业务逻辑 |
|---------|---------|------|------|----------|
| `findByParentId(Long parentId)` | `List<Category>` | `parentId`: 父分类ID | 根据父分类ID查找子分类 | 调用CategoryRepository的findByParentId方法，返回指定父分类下的所有子分类列表 |
| `findByLevel(Integer level)` | `List<Category>` | `level`: 分类级别 | 根据分类级别查找 | 调用CategoryRepository的findByLevel方法，返回指定级别的所有分类列表 |
| `findRootCategories()` | `List<Category>` | 无 | 查找一级分类 | 调用CategoryRepository的findByParentIdAndStatusOrderBySortAsc方法，传入parentId=0和status=1，返回所有一级分类列表 |
| `findAll(Pageable pageable)` | `Page<Category>` | `pageable`: 分页参数 | 分页查找所有分类 | 调用CategoryRepository的findAll方法，返回分页后的分类列表 |
| `getCategoryTree()` | `List<Map<String, Object>>` | 无 | 获取分类树 | 1. 调用CategoryRepository的findByStatusOrderBySortAsc方法获取所有启用状态的分类<br>2. 构建父分类ID到子分类列表的映射<br>3. 递归构建分类树节点，包含id、name、parentId、level、description、sort、status和children字段 |
| `existsByName(String name)` | `boolean` | `name`: 分类名称 | 检查分类名称是否存在 | 调用CategoryRepository的findByName方法，检查返回的Optional是否存在，返回布尔值 |
| `updateCategoryTree()` | `void` | 无 | 更新分类树结构 | 1. 调用CategoryRepository的findAll方法获取所有分类<br>2. 构建父分类ID到子分类列表的映射<br>3. 使用队列进行广度优先遍历，更新每个分类的level字段<br>4. 调用CategoryRepository的saveAll方法保存更新后的分类列表 |
| `getCategoryPath(Long categoryId)` | `Map<Long, String>` | `categoryId`: 分类ID | 获取分类路径 | 1. 调用CategoryRepository的findById方法查找指定ID的分类<br>2. 循环查找父分类，构建分类路径映射<br>3. 反转路径顺序，返回从根分类到目标分类的路径 |
| `searchCategories(String keyword, Pageable pageable)` | `Page<Category>` | `keyword`: 搜索关键字<br>`pageable`: 分页参数 | 搜索分类 | 1. 调用CategoryRepository的findByStatusOrderBySortAsc方法获取所有启用状态的分类<br>2. 过滤名称或描述包含关键字的分类<br>3. 进行分页处理，返回分页后的分类列表 |
| `hasAssociatedMedicines(Long categoryId)` | `boolean` | `categoryId`: 分类ID | 检查分类是否有关联药品 | 1. 调用findById方法查找指定ID的分类<br>2. 检查分类的medicines属性是否非空，返回布尔值 |
| `delete(Long id)` | `void` | `id`: 分类ID | 删除分类 | 1. 调用hasAssociatedMedicines方法检查分类是否有关联药品<br>2. 如果有关联药品，抛出IllegalStateException异常<br>3. 调用父类的delete方法删除分类 |
| `findActiveCategories(Pageable pageable)` | `Page<Category>` | `pageable`: 分页参数 | 查找启用状态的分类 | 1. 调用CategoryRepository的findByStatusOrderBySortAsc方法获取所有启用状态的分类<br>2. 进行分页处理，返回分页后的分类列表 |
| `findSubcategoriesByParentId(Long parentId, Pageable pageable)` | `Page<Category>` | `parentId`: 父分类ID<br>`pageable`: 分页参数 | 查找指定父分类的子分类 | 1. 调用CategoryRepository的findByParentId方法获取指定父分类的子分类<br>2. 进行分页处理，返回分页后的分类列表 |
| `getCategoryHierarchy()` | `Map<Long, List<Category>>` | 无 | 获取分类层次结构 | 1. 调用CategoryRepository的findAll方法获取所有分类<br>2. 使用Stream API按父分类ID分组，返回父分类ID到子分类列表的映射 |
| `findCategoriesWithMedicines(Pageable pageable)` | `Page<Category>` | `pageable`: 分页参数 | 查找有关联药品的分类 | 1. 调用CategoryRepository的findAll方法获取所有分类<br>2. 过滤有关联药品的分类<br>3. 进行分页处理，返回分页后的分类列表 |
| `getCategoryStatistics(Long categoryId)` | `Map<String, Object>` | `categoryId`: 分类ID | 获取分类统计信息 | 1. 调用CategoryRepository的findById方法查找指定ID的分类<br>2. 调用CategoryRepository的countMedicinesByCategoryId方法统计分类下的药品数量<br>3. 调用CategoryRepository的sumSaleAmountByCategoryId方法统计分类下的销售总额<br>4. 调用CategoryRepository的findByParentId方法获取分类的子分类数量<br>5. 构建并返回统计信息映射 |

---

### 3. 类：MedicineServiceImpl（药品服务实现）

**位置**：`com.example.demo.service.impl`

**继承关系**：`extends BaseServiceImpl<Medicine, Long, MedicineRepository> implements MedicineService`

**类注解说明**：
- `@Service`：Spring注解，标识为服务组件
- `@Transactional`：Spring注解，声明事务支持

**描述**：药品服务的具体实现，提供药品相关的业务操作

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 业务逻辑 |
|---------|---------|------|------|----------|
| `findByMedicineCode(String medicineCode)` | `Medicine` | `medicineCode`: 药品编码 | 根据药品编码查找药品 | 调用MedicineRepository的findByMedicineCode方法，返回药品对象或null |
| `findAll(Pageable pageable)` | `Page<Medicine>` | `pageable`: 分页参数 | 分页查找所有药品 | 调用MedicineRepository的findAll方法，返回分页后的药品列表 |
| `findByStatus(Integer status, Pageable pageable)` | `Page<Medicine>` | `status`: 状态<br>`pageable`: 分页参数 | 根据状态分页查找药品 | 调用MedicineRepository的findByStatus方法，返回指定状态的分页药品列表 |
| `searchMedicines(String keyword)` | `List<Medicine>` | `keyword`: 搜索关键字 | 搜索药品 | 调用MedicineRepository的searchMedicines方法，返回包含关键字的药品列表 |
| `findByCategoryId(Long categoryId)` | `List<Medicine>` | `categoryId`: 分类ID | 根据分类ID查找药品 | 调用MedicineRepository的findByCategoryId方法，返回指定分类下的药品列表 |
| `updatePrice(Long id, BigDecimal retailPrice, BigDecimal purchasePrice)` | `Medicine` | `id`: 药品ID<br>`retailPrice`: 零售价<br>`purchasePrice`: 采购价 | 更新药品价格 | 1. 调用findById方法查找指定ID的药品<br>2. 如果药品存在，更新零售价和采购价<br>3. 调用save方法保存更新后的药品，返回更新后的药品对象 |
| `countByStatus(Integer status)` | `long` | `status`: 状态 | 统计指定状态的药品数量 | 调用MedicineRepository的countByStatus方法，返回指定状态的药品数量 |
| `findSeasonalMedicines(Pageable pageable)` | `Page<Medicine>` | `pageable`: 分页参数 | 查找季节性药品 | 1. 调用MedicineRepository的findByStatus方法获取所有启用状态的药品<br>2. 过滤出季节性药品<br>3. 进行分页处理，返回分页后的药品列表 |
| `findPrescriptionMedicines(Pageable pageable)` | `Page<Medicine>` | `pageable`: 分页参数 | 查找处方药 | 1. 调用MedicineRepository的findByStatus方法获取所有启用状态的药品<br>2. 过滤出处方药<br>3. 进行分页处理，返回分页后的药品列表 |
| `findByManufacturer(String manufacturer, Pageable pageable)` | `Page<Medicine>` | `manufacturer`: 生产厂家<br>`pageable`: 分页参数 | 根据生产厂家查找药品 | 1. 调用MedicineRepository的findByManufacturerContaining方法获取指定生产厂家的药品<br>2. 进行分页处理，返回分页后的药品列表 |
| `findLowStockMedicines(Pageable pageable)` | `Page<Medicine>` | `pageable`: 分页参数 | 查找库存不足的药品 | 1. 调用MedicineRepository的findByStatus方法获取所有启用状态的药品<br>2. 对每个药品，调用MedicineRepository的sumCurrentStockByMedicineId方法获取当前库存量<br>3. 调用MedicineRepository的findStocksByMedicineId方法获取药品的库存记录，计算最小预警阈值<br>4. 过滤出库存量小于等于预警阈值的药品<br>5. 进行分页处理，返回分页后的药品列表 |
| `getMedicineSalesStatistics(Long medicineId)` | `Map<String, Object>` | `medicineId`: 药品ID | 获取药品销售统计信息 | 1. 调用MedicineRepository的findById方法查找指定ID的药品<br>2. 调用MedicineRepository的sumSaleQuantityByMedicineId方法统计药品的销售总量<br>3. 调用MedicineRepository的findSaleTrendByMedicineId方法获取药品的销售趋势<br>4. 构建并返回统计信息映射 |
| `getMedicinePurchaseStatistics(Long medicineId)` | `Map<String, Object>` | `medicineId`: 药品ID | 获取药品采购统计信息 | 1. 调用MedicineRepository的findById方法查找指定ID的药品<br>2. 调用MedicineRepository的sumPurchaseQuantityByMedicineId方法统计药品的采购总量<br>3. 构建并返回统计信息映射 |
| `batchUpdateStatus(List<Long> medicineIds, Integer status)` | `void` | `medicineIds`: 药品ID列表<br>`status`: 状态 | 批量更新药品状态 | 1. 调用MedicineRepository的findAllById方法获取指定ID的药品列表<br>2. 对每个药品设置新状态<br>3. 调用MedicineRepository的saveAll方法保存更新后的药品列表 |
| `findExpiringMedicines(int daysThreshold, Pageable pageable)` | `Page<Medicine>` | `daysThreshold`: 天数阈值<br>`pageable`: 分页参数 | 查找即将过期的药品 | 1. 计算当前日期和阈值日期<br>2. 调用StockRepository的findExpiringStock方法获取即将过期的库存<br>3. 提取不重复的药品<br>4. 进行分页处理，返回分页后的药品列表 |
| `findByStorageRequirement(Integer storageRequirement, Pageable pageable)` | `Page<Medicine>` | `storageRequirement`: 存储要求<br>`pageable`: 分页参数 | 根据存储要求查找药品 | 1. 调用MedicineRepository的findByStatus方法获取所有启用状态的药品<br>2. 过滤出存储要求匹配的药品<br>3. 进行分页处理，返回分页后的药品列表 |
| `searchMedicinesWithPagination(String keyword, Pageable pageable)` | `Page<Medicine>` | `keyword`: 搜索关键字<br>`pageable`: 分页参数 | 分页搜索药品 | 1. 调用MedicineRepository的searchMedicines方法获取包含关键字的药品列表<br>2. 进行分页处理，返回分页后的药品列表 |

---

### 4. 类：PredictionResultServiceImpl（预测结果服务实现）

**位置**：`com.example.demo.service.impl`

**继承关系**：`extends BaseServiceImpl<PredictionResult, Long, PredictionResultRepository> implements PredictionResultService`

**类注解说明**：
- `@Service`：Spring注解，标识为服务组件
- `@Transactional`：Spring注解，声明事务支持
- `@Slf4j`：Lombok注解，提供日志记录功能
- `@Deprecated`：标注该类为已废弃，建议使用模型端服务进行预测

**描述**：预测结果服务的具体实现，提供预测结果相关的业务操作。当前实现已废弃，建议通过调用外部模型服务获取预测结果。

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 业务逻辑 |
|---------|---------|------|------|----------|
| `findByMedicineId(Long medicineId)` | `List<PredictionResult>` | `medicineId`: 药品ID | 根据药品ID查找预测结果 | 调用PredictionResultRepository的findByMedicineId方法，返回指定药品的预测结果列表 |
| `findByPredictionDate(LocalDate predictionDate)` | `List<PredictionResult>` | `predictionDate`: 预测日期 | 根据预测日期查找预测结果 | 调用PredictionResultRepository的findByPredictionDate方法，返回指定预测日期的预测结果列表 |
| `findLatestByMedicineId(Long medicineId)` | `PredictionResult` | `medicineId`: 药品ID | 查找药品的最新预测结果 | 调用PredictionResultRepository的findFirstByMedicineIdOrderByPredictionDateDesc方法，返回药品的最新预测结果 |
| `findByPredictionDateRange(LocalDate startDate, LocalDate endDate)` | `List<PredictionResult>` | `startDate`: 开始日期<br>`endDate`: 结束日期 | 查找指定日期范围内的预测结果 | 调用PredictionResultRepository的findByPredictionDateBetween方法，返回指定日期范围内的预测结果列表 |
| `findNeedReprediction(Double threshold)` | `List<PredictionResult>` | `threshold`: 准确率阈值 | 查找需要重新预测的结果 | 1. 将Double类型的阈值转换为BigDecimal类型<br>2. 调用PredictionResultRepository的findNeedReprediction方法，返回需要重新预测的结果列表 |
| `getAverageAccuracyByModel()` | `Map<String, Double>` | 无 | 获取各模型的平均准确率 | 1. 调用PredictionResultRepository的findAverageAccuracyByModel方法获取各模型的平均准确率数据<br>2. 转换为模型类型到平均准确率的映射，返回映射 |
| `generatePrediction(Long medicineId, String modelType, LocalDate predictionDate)` | `PredictionResult` | `medicineId`: 药品ID<br>`modelType`: 模型类型<br>`predictionDate`: 预测日期 | 生成预测结果 | 1. 调用MedicineService的findById方法查找指定ID的药品<br>2. 如果药品存在，创建预测结果对象，设置药品、模型类型、预测日期等属性<br>3. 模拟预测过程，生成预测需求量和建议订购数量<br>4. 调用save方法保存预测结果，返回保存后的预测结果 |
| `generateBatchPredictions(List<Long> medicineIds, String modelType, LocalDate startDate, LocalDate endDate)` | `List<PredictionResult>` | `medicineIds`: 药品ID列表<br>`modelType`: 模型类型<br>`startDate`: 开始日期<br>`endDate`: 结束日期 | 批量生成预测结果 | 1. 遍历药品ID列表<br>2. 对每个药品ID，调用generatePrediction方法生成预测结果<br>3. 收集所有预测结果，返回预测结果列表 |
| `getRecommendedOrderQuantities(LocalDate targetDate)` | `Map<Long, Integer>` | `targetDate`: 目标日期 | 获取建议订购数量 | 1. 调用PredictionResultRepository的findByPredictionDate方法获取指定目标日期的预测结果列表<br>2. 构建药品ID到建议订购数量的映射，返回映射 |

**注意**：该类及所有public方法均已标记为`@Deprecated`，建议使用外部模型服务替代本地预测算法。

---

### 5. 类：PurchaseOrderServiceImpl（采购订单服务实现）

**位置**：`com.example.demo.service.impl`

**继承关系**：`extends BaseServiceImpl<PurchaseOrder, Long, PurchaseOrderRepository> implements PurchaseOrderService`

**类注解说明**：
- `@Service`：Spring注解，标识为服务组件
- `@Transactional`：Spring注解，声明事务支持

**描述**：采购订单服务的具体实现，提供采购订单相关的业务操作

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 业务逻辑 |
|---------|---------|------|------|----------|
| `findByOrderNo(String orderNo)` | `PurchaseOrder` | `orderNo`: 订单编号 | 根据订单编号查找订单 | 调用PurchaseOrderRepository的findByOrderNo方法，返回订单对象或null |
| `findAll(Pageable pageable)` | `Page<PurchaseOrder>` | `pageable`: 分页参数 | 分页查找所有订单 | 调用PurchaseOrderRepository的findAll方法，返回分页后的订单列表 |
| `findByOrderStatus(Integer orderStatus, Pageable pageable)` | `Page<PurchaseOrder>` | `orderStatus`: 订单状态<br>`pageable`: 分页参数 | 根据订单状态分页查找订单 | 1. 调用PurchaseOrderRepository的findByOrderStatus方法获取指定状态的订单列表<br>2. 进行分页处理，返回分页后的订单列表 |
| `findByMedicineId(Long medicineId, Pageable pageable)` | `Page<PurchaseOrder>` | `medicineId`: 药品ID<br>`pageable`: 分页参数 | 根据药品ID分页查找订单 | 1. 调用PurchaseOrderRepository的findByMedicineId方法获取指定药品的订单列表<br>2. 进行分页处理，返回分页后的订单列表 |
| `findPendingOrders(Pageable pageable)` | `Page<PurchaseOrder>` | `pageable`: 分页参数 | 查找待处理的订单 | 1. 调用PurchaseOrderRepository的findPendingOrders方法获取待处理的订单列表<br>2. 进行分页处理，返回分页后的订单列表 |
| `findOverdueOrders(Pageable pageable)` | `Page<PurchaseOrder>` | `pageable`: 分页参数 | 查找过期的订单 | 1. 调用PurchaseOrderRepository的findOverdueOrders方法获取过期的订单列表<br>2. 进行分页处理，返回分页后的订单列表 |
| `searchOrders(String keyword, Pageable pageable)` | `Page<PurchaseOrder>` | `keyword`: 搜索关键字<br>`pageable`: 分页参数 | 搜索订单 | 1. 调用PurchaseOrderRepository的findByKeywordContaining方法获取包含关键字的订单列表<br>2. 进行分页处理，返回分页后的订单列表 |
| `findBySupplierContaining(String supplier, Pageable pageable)` | `Page<PurchaseOrder>` | `supplier`: 供应商<br>`pageable`: 分页参数 | 根据供应商查找订单 | 1. 调用PurchaseOrderRepository的findBySupplierContaining方法获取指定供应商的订单列表<br>2. 进行分页处理，返回分页后的订单列表 |
| `findByOrderTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable)` | `Page<PurchaseOrder>` | `startTime`: 开始时间<br>`endTime`: 结束时间<br>`pageable`: 分页参数 | 根据下单时间范围查找订单 | 1. 调用PurchaseOrderRepository的findByOrderTimeBetween方法获取指定时间范围内的订单列表<br>2. 进行分页处理，返回分页后的订单列表 |
| `getTotalPurchasedQuantity(Long medicineId)` | `Integer` | `medicineId`: 药品ID | 获取药品的采购总量 | 调用PurchaseOrderRepository的sumPurchasedQuantityByMedicineId方法，返回采购总量 |
| `getTotalPurchaseAmountByPeriod(LocalDateTime startTime, LocalDateTime endTime)` | `Double` | `startTime`: 开始时间<br>`endTime`: 结束时间 | 获取指定时间段的采购总额 | 调用PurchaseOrderRepository的sumTotalAmountByPeriod方法，返回采购总额 |
| `createOrder(PurchaseOrder order, Long operatorId)` | `PurchaseOrder` | `order`: 订单对象<br>`operatorId`: 操作员ID | 创建订单 | 1. 调用UserService的findById方法查找操作员<br>2. 如果操作员存在，设置订单的操作员<br>3. 如果订单编号为空，生成订单编号<br>4. 如果单价和数量不为空，计算总金额<br>5. 调用PurchaseOrderRepository的save方法保存订单，返回保存后的订单 |
| `confirmOrder(Long orderId)` | `PurchaseOrder` | `orderId`: 订单ID | 确认订单 | 1. 调用findById方法查找指定ID的订单<br>2. 如果订单存在且为待处理状态，将订单状态设置为已确认<br>3. 调用PurchaseOrderRepository的save方法保存订单，返回保存后的订单 |
| `markAsArrived(Long orderId)` | `PurchaseOrder` | `orderId`: 订单ID | 标记订单为已到货 | 1. 调用findById方法查找指定ID的订单<br>2. 如果订单存在且为待处理或已确认状态，将订单状态设置为已到货<br>3. 设置实际到货时间为当前时间<br>4. 调用PurchaseOrderRepository的save方法保存订单，返回保存后的订单 |
| `cancelOrder(Long orderId)` | `PurchaseOrder` | `orderId`: 订单ID | 取消订单 | 1. 调用findById方法查找指定ID的订单<br>2. 如果订单存在且未到货且未取消，将订单状态设置为已取消<br>3. 调用PurchaseOrderRepository的save方法保存订单，返回保存后的订单 |
| `getOrderStatistics()` | `Map<String, Object>` | 无 | 获取订单统计信息 | 1. 调用findByOrderStatus方法获取各状态的订单数量<br>2. 调用getTotalPurchaseAmountByPeriod方法获取最近一个月的采购总额<br>3. 调用findOverdueOrders方法获取过期订单数量<br>4. 构建并返回统计信息映射 |
| `countByStatus()` | `Map<String, Long>` | 无 | 统计各状态的订单数量 | 1. 遍历订单状态（0-3）<br>2. 调用PurchaseOrderRepository的findByOrderStatus方法获取每个状态的订单列表<br>3. 统计每个状态的订单数量，构建并返回映射 |
| `getMonthlyStatistics(LocalDate startDate, LocalDate endDate)` | `Map<String, Object>` | `startDate`: 开始日期<br>`endDate`: 结束日期 | 获取月度统计信息 | 1. 将LocalDate转换为LocalDateTime<br>2. 调用PurchaseOrderRepository的findByOrderTimeBetween方法获取指定时间范围内的订单列表<br>3. 按状态分类订单<br>4. 计算各状态订单数量、采购金额统计、按药品统计、按供应商统计、按月份统计<br>5. 构建并返回统计信息映射 |
| `getSupplierPurchaseStatistics(Pageable pageable)` | `Page<Map<String, Object>>` | `pageable`: 分页参数 | 获取供应商采购统计信息 | 1. 调用PurchaseOrderRepository的findSupplierPurchaseStatistics方法获取供应商采购统计数据<br>2. 转换为Map列表<br>3. 进行分页处理，返回分页后的统计信息列表 |
| `findUpcomingOrders(Pageable pageable)` | `Page<PurchaseOrder>` | `pageable`: 分页参数 | 查找即将到期的订单 | 1. 调用PurchaseOrderRepository的findUpcomingOrders方法获取即将到期的订单列表<br>2. 进行分页处理，返回分页后的订单列表 |
| `batchConfirmOrders(List<Long> orderIds)` | `void` | `orderIds`: 订单ID列表 | 批量确认订单 | 1. 调用PurchaseOrderRepository的findAllById方法获取指定ID的订单列表<br>2. 对每个订单，如果存在且为待处理状态，将状态设置为已确认<br>3. 调用PurchaseOrderRepository的save方法保存更新后的订单 |
| `batchCancelOrders(List<Long> orderIds)` | `void` | `orderIds`: 订单ID列表 | 批量取消订单 | 1. 调用PurchaseOrderRepository的findAllById方法获取指定ID的订单列表<br>2. 对每个订单，如果存在且未到货且未取消，将状态设置为已取消<br>3. 调用PurchaseOrderRepository的save方法保存更新后的订单 |
| `getPurchaseSuggestions(Pageable pageable)` | `Page<Map<String, Object>>` | `pageable`: 分页参数 | 获取采购建议 | 1. 调用MedicineRepository的findByStatus方法获取所有启用状态的药品<br>2. 计算最近30天的销售数据<br>3. 为每种药品计算当前库存量和平均日销售量<br>4. 如果当前库存低于安全库存，生成采购建议<br>5. 进行分页处理，返回分页后的采购建议列表 |
| `getPurchaseByCategory(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable)` | `Page<Map<String, Object>>` | `startDate`: 开始时间<br>`endDate`: 结束时间<br>`pageable`: 分页参数 | 按分类获取采购信息 | 1. 调用PurchaseOrderRepository的findByOrderTimeBetween方法获取指定时间范围内的订单列表<br>2. 按分类分组，计算每个分类的采购数量和金额<br>3. 进行分页处理，返回分页后的分类采购信息列表 |
| `getOrderDetailsWithMedicine(Long orderId)` | `Map<String, Object>` | `orderId`: 订单ID | 获取订单详情（包含药品信息） | 1. 调用PurchaseOrderRepository的findById方法查找指定ID的订单<br>2. 如果订单存在，构建订单详情映射，包含订单基本信息和药品信息<br>3. 返回订单详情映射 |

**注意**：`getPurchaseSuggestions`方法已标记为`@Deprecated`，建议使用PredictionResultService替代。

---

### 6. 类：SaleRecordServiceImpl（销售记录服务实现）

**位置**：`com.example.demo.service.impl`

**继承关系**：`extends BaseServiceImpl<SaleRecord, Long, SaleRecordRepository> implements SaleRecordService`

**类注解说明**：
- `@Service`：Spring注解，标识为服务组件
- `@Transactional`：Spring注解，声明事务支持

**描述**：销售记录服务的具体实现，提供销售记录相关的业务操作

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 业务逻辑 |
|---------|---------|------|------|----------|
| `findByRecordNo(String recordNo)` | `SaleRecord` | `recordNo`: 销售单号 | 根据销售单号查找销售记录 | 调用SaleRecordRepository的findByRecordNo方法，返回销售记录对象或null |
| `findAll(Pageable pageable)` | `Page<SaleRecord>` | `pageable`: 分页参数 | 分页查找所有销售记录 | 调用SaleRecordRepository的findAll方法，返回分页后的销售记录列表 |
| `findBySaleTimeBetween(LocalDateTime startTime, LocalDateTime endTime)` | `List<SaleRecord>` | `startTime`: 开始时间<br>`endTime`: 结束时间 | 根据销售时间范围查找销售记录 | 调用SaleRecordRepository的findBySaleTimeBetween方法，返回指定时间范围内的销售记录列表 |
| `findByMedicineId(Long medicineId)` | `List<SaleRecord>` | `medicineId`: 药品ID | 根据药品ID查找销售记录 | 调用SaleRecordRepository的findByMedicineId方法，返回指定药品的销售记录列表 |
| `findByOperatorId(Long operatorId)` | `List<SaleRecord>` | `operatorId`: 操作员ID | 根据操作员ID查找销售记录 | 调用SaleRecordRepository的findByOperatorId方法，返回指定操作员的销售记录列表 |
| `getTotalSalesByPeriod(LocalDateTime startTime, LocalDateTime endTime)` | `Double` | `startTime`: 开始时间<br>`endTime`: 结束时间 | 获取指定时间段的销售总额 | 调用SaleRecordRepository的sumTotalAmountByPeriod方法，返回销售总额 |
| `getTotalQuantityByMedicineId(Long medicineId)` | `Integer` | `medicineId`: 药品ID | 获取药品的销售总量 | 调用SaleRecordRepository的sumQuantityByMedicineId方法，返回销售总量 |
| `getDailySalesReport(LocalDateTime startDate, LocalDateTime endDate)` | `List<Map<String, Object>>` | `startDate`: 开始日期<br>`endDate`: 结束日期 | 获取每日销售报告 | 调用SaleRecordRepository的findDailySales方法，返回每日销售数据列表 |
| `getTopSellingMedicines(int limit, LocalDateTime startDate, LocalDateTime endDate)` | `List<Map<String, Object>>` | `limit`: 限制数量<br>`startDate`: 开始日期<br>`endDate`: 结束日期 | 获取最畅销的药品 | 1. 调用SaleRecordRepository的findTopSellingMedicines方法获取最畅销的药品数据<br>2. 转换为Map列表，返回指定数量的最畅销药品 |
| `createSaleRecord(SaleRecord saleRecord, Long operatorId)` | `SaleRecord` | `saleRecord`: 销售记录对象<br>`operatorId`: 操作员ID | 创建销售记录 | 1. 调用UserService的findById方法查找操作员<br>2. 如果操作员存在，设置销售记录的操作员<br>3. 如果销售单号为空，生成销售单号<br>4. 如果单价和数量不为空，计算总金额<br>5. 调用SaleRecordRepository的save方法保存销售记录，返回保存后的销售记录 |
| `findByCustomerType(Integer customerType, Pageable pageable)` | `Page<SaleRecord>` | `customerType`: 顾客类型<br>`pageable`: 分页参数 | 根据顾客类型查找销售记录 | 1. 调用SaleRecordRepository的findByCustomerType方法获取指定顾客类型的销售记录列表<br>2. 进行分页处理，返回分页后的销售记录列表 |
| `getSalesByCategory(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable)` | `Page<Map<String, Object>>` | `startDate`: 开始时间<br>`endDate`: 结束时间<br>`pageable`: 分页参数 | 按分类获取销售信息 | 1. 调用SaleRecordRepository的findBySaleTimeBetween方法获取指定时间范围内的销售记录列表<br>2. 按分类分组，计算每个分类的销售数量和金额<br>3. 进行分页处理，返回分页后的分类销售信息列表 |
| `getSalesBySymptom(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable)` | `Page<Map<String, Object>>` | `startDate`: 开始时间<br>`endDate`: 结束时间<br>`pageable`: 分页参数 | 按症状获取销售信息 | 1. 调用SaleRecordRepository的findSalesBySymptom方法获取指定时间范围内的按症状销售数据<br>2. 进行分页处理，返回分页后的症状销售信息列表 |
| `getOperatorSalesPerformance(LocalDateTime startDate, LocalDateTime endDate)` | `Map<String, Object>` | `startDate`: 开始时间<br>`endDate`: 结束时间 | 获取操作员销售业绩 | 1. 调用SaleRecordRepository的findOperatorSalesPerformance方法获取操作员销售业绩数据<br>2. 构建并返回销售业绩映射 |
| `getMonthlySalesTrend(int months, Pageable pageable)` | `Page<Map<String, Object>>` | `months`: 月数<br>`pageable`: 分页参数 | 获取月度销售趋势 | 1. 计算开始日期（当前日期减去指定月数）<br>2. 调用SaleRecordRepository的findDailySalesAmount方法获取指定时间范围内的每日销售数据<br>3. 按月份分组，计算每月销售总额<br>4. 进行分页处理，返回分页后的月度销售趋势列表 |
| `getSalesPrediction(int days, Pageable pageable)` | `Page<Map<String, Object>>` | `days`: 天数<br>`pageable`: 分页参数 | 获取销售预测 | 1. 计算开始日期（当前日期减去90天）和结束日期（当前日期加上指定天数）<br>2. 调用SaleRecordRepository的findDailySales方法获取历史销售数据<br>3. 基于历史数据生成未来销售预测<br>4. 进行分页处理，返回分页后的销售预测列表 |
| `findPrescriptionSales(Pageable pageable)` | `Page<SaleRecord>` | `pageable`: 分页参数 | 查找处方药销售记录 | 1. 调用SaleRecordRepository的findAll方法获取所有销售记录<br>2. 过滤出处方药销售记录<br>3. 进行分页处理，返回分页后的处方药销售记录列表 |
| `getSalesStatisticsByPeriod(LocalDateTime startDate, LocalDateTime endDate)` | `Map<String, Object>` | `startDate`: 开始时间<br>`endDate`: 结束时间 | 获取指定时间段的销售统计信息 | 1. 调用SaleRecordRepository的findBySaleTimeBetween方法获取指定时间范围内的销售记录列表<br>2. 计算销售总额、销售总量、平均客单价<br>3. 按药品、症状、操作员分组统计<br>4. 构建并返回统计信息映射 |

**注意**：`getSalesPrediction`方法已标记为`@Deprecated`，建议使用PredictionResultService替代。

---

### 7. 类：StockServiceImpl（库存服务实现）

**位置**：`com.example.demo.service.impl`

**继承关系**：`extends BaseServiceImpl<Stock, Long, StockRepository> implements StockService`

**类注解说明**：
- `@Service`：Spring注解，标识为服务组件
- `@Transactional`：Spring注解，声明事务支持

**描述**：库存服务的具体实现，提供库存相关的业务操作

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 业务逻辑 |
|---------|---------|------|------|----------|
| `findByMedicineId(Long medicineId, Pageable pageable)` | `Page<Stock>` | `medicineId`: 药品ID<br>`pageable`: 分页参数 | 根据药品ID分页查找库存 | 1. 调用StockRepository的findByMedicineId方法获取指定药品的库存列表<br>2. 进行分页处理，返回分页后的库存列表 |
| `getTotalStock(Long medicineId)` | `Integer` | `medicineId`: 药品ID | 获取药品的总库存量 | 调用StockRepository的sumQuantityByMedicineId方法，返回总库存量 |
| `getExpiringStock(LocalDate startDate, LocalDate endDate, Pageable pageable)` | `Page<Stock>` | `startDate`: 开始日期<br>`endDate`: 结束日期<br>`pageable`: 分页参数 | 查找即将过期的库存 | 1. 调用StockRepository的findExpiringStock方法获取指定日期范围内即将过期的库存列表<br>2. 进行分页处理，返回分页后的库存列表 |
| `getLowStock(Pageable pageable)` | `Page<Stock>` | `pageable`: 分页参数 | 查找库存不足的库存 | 1. 调用StockRepository的findLowStock方法获取库存不足的库存列表<br>2. 进行分页处理，返回分页后的库存列表 |
| `getLowStockSummary()` | `Map<Long, Integer>` | 无 | 获取库存不足的药品摘要 | 1. 调用StockRepository的findLowStockSummary方法获取库存不足的药品数据<br>2. 转换为药品ID到库存量的映射，返回映射 |
| `reduceStock(Long medicineId, Integer quantity)` | `void` | `medicineId`: 药品ID<br>`quantity`: 减少数量 | 减少库存 | 1. 调用StockRepository的findByMedicineIdAndStatus方法获取指定药品的有效库存列表<br>2. 按有效期升序排序库存列表<br>3. 遍历库存列表，减少库存数量，直到达到指定减少数量<br>4. 调用StockRepository的save方法保存更新后的库存 |
| `increaseStock(Long medicineId, Integer quantity, String batchNumber, LocalDate expirationDate)` | `void` | `medicineId`: 药品ID<br>`quantity`: 增加数量<br>`batchNumber`: 批号<br>`expirationDate`: 有效期 | 增加库存 | 1. 调用StockRepository的findByMedicineIdAndBatchNumber方法查找指定药品和批号的库存<br>2. 如果存在，增加库存数量；如果不存在，创建新库存记录<br>3. 调用StockRepository的save方法保存更新或新创建的库存 |
| `checkStockAvailability(Long medicineId, Integer requiredQuantity)` | `boolean` | `medicineId`: 药品ID<br>`requiredQuantity`: 所需数量 | 检查库存是否充足 | 1. 调用StockRepository的sumQuantityByMedicineId方法获取指定药品的总库存量<br>2. 比较总库存量与所需数量，返回布尔值 |
| `findByStatus(Integer status, Pageable pageable)` | `Page<Stock>` | `status`: 状态<br>`pageable`: 分页参数 | 根据状态分页查找库存 | 1. 调用StockRepository的findByStatus方法获取指定状态的库存列表<br>2. 进行分页处理，返回分页后的库存列表 |
| `findByBatchNumber(String batchNumber, Pageable pageable)` | `Page<Stock>` | `batchNumber`: 批号<br>`pageable`: 分页参数 | 根据批号分页查找库存 | 1. 调用StockRepository的findByBatchNumber方法获取指定批号的库存列表<br>2. 进行分页处理，返回分页后的库存列表 |
| `findByShelfLocation(String shelfLocation, Pageable pageable)` | `Page<Stock>` | `shelfLocation`: 货架位置<br>`pageable`: 分页参数 | 根据货架位置分页查找库存 | 1. 调用StockRepository的findByShelfLocation方法获取指定货架位置的库存列表<br>2. 进行分页处理，返回分页后的库存列表 |
| `findExpiringWithinDays(int days)` | `List<Stock>` | `days`: 天数 | 查找指定天数内过期的库存 | 1. 计算开始日期（当前日期）和结束日期（当前日期加上指定天数）<br>2. 调用StockRepository的findExpiringStock方法获取指定日期范围内即将过期的库存列表 |
| `getStockStatisticsByMedicine(Long medicineId)` | `Map<String, Object>` | `medicineId`: 药品ID | 获取药品的库存统计信息 | 1. 调用StockRepository的findByMedicineId方法获取指定药品的库存列表<br>2. 计算总库存量、过期库存量、即将过期库存量、库存价值<br>3. 构建并返回统计信息映射 |
| `findByMedicineIdAndBatchNumber(Long medicineId, String batchNumber, Pageable pageable)` | `Page<Stock>` | `medicineId`: 药品ID<br>`batchNumber`: 批号<br>`pageable`: 分页参数 | 根据药品ID和批号分页查找库存 | 1. 调用StockRepository的findByMedicineId和findByBatchNumber方法获取指定药品和批号的库存列表<br>2. 进行分页处理，返回分页后的库存列表 |
| `findExpiredStock(Pageable pageable)` | `Page<Stock>` | `pageable`: 分页参数 | 查找过期的库存 | 1. 调用StockRepository的findAll方法获取所有库存记录<br>2. 在内存中过滤出过期（有效期在当前日期之前）且状态为1的库存<br>3. 进行分页处理，返回分页后的库存列表 |
| `findNearExpiryStock(int days, Pageable pageable)` | `Page<Stock>` | `days`: 天数<br>`pageable`: 分页参数 | 查找即将过期的库存 | 1. 计算开始日期（当前日期）和结束日期（当前日期加上指定天数）<br>2. 调用StockRepository的findExpiringStock方法获取指定日期范围内即将过期的库存列表<br>3. 进行分页处理，返回分页后的库存列表 |
| `calculateStockTurnoverRate(String period)` | `Double` | `period`: 时间段 | 计算库存周转率 | 1. 根据时间段参数计算开始日期<br>2. 调用StockRepository的calculateStockTurnoverRate方法获取库存周转率数据<br>3. 计算平均库存周转率，返回结果 |
| `calculateTotalStockValue()` | `Double` | 无 | 计算库存总价值 | 调用StockRepository的calculateTotalStockValue方法，返回库存总价值 |
| `getStockValueByCategory()` | `Map<String, Object>` | 无 | 按分类获取库存价值 | 1. 调用StockRepository的calculateStockValueByCategory方法获取按分类的库存价值数据<br>2. 构建并返回分类库存价值映射 |
| `getStockAlerts(Pageable pageable)` | `Page<Stock>` | `pageable`: 分页参数 | 获取库存预警 | 1. 调用StockRepository的findLowStock方法获取库存不足的库存列表<br>2. 调用StockRepository的findExpiringStock方法获取即将过期的库存列表<br>3. 合并两个列表，去重<br>4. 进行分页处理，返回分页后的库存预警列表 |
| `transferStock(Long fromStockId, Long toStockId, Integer quantity)` | `void` | `fromStockId`: 源库存ID<br>`toStockId`: 目标库存ID<br>`quantity`: 转移数量 | 转移库存 | 1. 调用findById方法查找源库存和目标库存<br>2. 如果两个库存都存在，且源库存数量大于等于转移数量，减少源库存数量，增加目标库存数量<br>3. 调用StockRepository的save方法保存更新后的库存 |
| `setMinimumStockLevel(Long medicineId, Integer minLevel)` | `void` | `medicineId`: 药品ID<br>`minLevel`: 最小库存水平 | 设置最小库存水平 | 1. 调用StockRepository的findByMedicineId方法获取指定药品的库存列表<br>2. 对每个库存记录，设置最小订购数量为最小库存水平<br>3. 调用StockRepository的saveAll方法保存更新后的库存列表 |
| `getStockInventoryReport()` | `Map<Long, Object>` | 无 | 获取库存库存报告 | 1. 调用StockRepository的findAll方法获取所有库存记录<br>2. 遍历库存记录，为每个库存创建包含数量、批号、有效期、货架位置和状态的信息映射<br>3. 以库存记录ID为键构建并返回报告映射 |
| `findByStorageCondition(Integer condition, Pageable pageable)` | `Page<Stock>` | `condition`: 存储条件<br>`pageable`: 分页参数 | 根据存储条件分页查找库存 | 1. 调用StockRepository的findAll方法获取所有库存记录<br>2. 过滤出存储条件匹配的库存记录<br>3. 进行分页处理，返回分页后的库存列表 |
| `findByShelfLocationContaining(String location, Pageable pageable)` | `Page<Stock>` | `location`: 货架位置关键字<br>`pageable`: 分页参数 | 根据货架位置关键字分页查找库存 | 1. 调用StockRepository的findByShelfLocationContaining方法获取货架位置包含关键字的库存列表<br>2. 进行分页处理，返回分页后的库存列表 |

---

### 8. 类：SymptomServiceImpl（症状服务实现）

**位置**：`com.example.demo.service.impl`

**继承关系**：`extends BaseServiceImpl<Symptom, Integer, SymptomRepository> implements SymptomService`

**类注解说明**：
- `@Service`：Spring注解，标识为服务组件
- `@Transactional`：Spring注解，声明事务支持

**描述**：症状服务的具体实现，提供症状相关的业务操作

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 业务逻辑 |
|---------|---------|------|------|----------|
| `findByName(String name)` | `Symptom` | `name`: 症状名称 | 根据名称查找症状 | 调用SymptomRepository的findByName方法，返回症状对象或null |
| `findByNameContaining(String name)` | `List<Symptom>` | `name`: 症状名称关键字 | 根据名称模糊查找症状 | 调用SymptomRepository的findByNameContaining方法，返回名称包含关键字的症状列表 |
| `existsByName(String name)` | `boolean` | `name`: 症状名称 | 检查症状名称是否存在 | 调用SymptomRepository的existsByName方法，返回布尔值 |
| `findByDescriptionContaining(String description)` | `List<Symptom>` | `description`: 描述关键字 | 根据描述模糊查找症状 | 调用SymptomRepository的findByDescriptionContaining方法，返回描述包含关键字的症状列表 |
| `searchSymptoms(String keyword)` | `List<Symptom>` | `keyword`: 搜索关键字 | 搜索症状 | 调用SymptomRepository的searchSymptoms方法，返回包含关键字的症状列表 |
| `findAll(Pageable pageable)` | `Page<Symptom>` | `pageable`: 分页参数 | 分页查找所有症状 | 调用SymptomRepository的findAll方法，返回分页后的症状列表 |
| `searchSymptoms(String keyword, Pageable pageable)` | `Page<Symptom>` | `keyword`: 搜索关键字<br>`pageable`: 分页参数 | 分页搜索症状 | 1. 调用SymptomRepository的searchSymptoms方法获取包含关键字的症状列表<br>2. 进行分页处理，返回分页后的症状列表 |
| `saveAll(List<Symptom> symptoms)` | `List<Symptom>` | `symptoms`: 症状列表 | 批量保存症状 | 调用SymptomRepository的saveAll方法，返回保存后的症状列表 |
| `countAll()` | `long` | 无 | 统计所有症状数量 | 调用SymptomRepository的count方法，返回症状总数量 |
| `findByMedicineId(Long medicineId, Pageable pageable)` | `Page<Symptom>` | `medicineId`: 药品ID<br>`pageable`: 分页参数 | 根据药品ID分页查找症状 | 1. 调用SymptomRepository的findMedicinesBySymptomId方法获取关联指定药品的症状列表<br>2. 进行分页处理，返回分页后的症状列表 |
| `findMostCommonSymptoms(int limit, LocalDateTime start, LocalDateTime end, Pageable pageable)` | `Page<Symptom>` | `limit`: 限制数量<br>`start`: 开始时间<br>`end`: 结束时间<br>`pageable`: 分页参数 | 查找最常见的症状 | 1. 调用SymptomRepository的findMostCommonSymptoms方法获取指定时间范围内最常见的症状数据<br>2. 根据症状ID查找症状对象<br>3. 进行分页处理，返回分页后的症状列表 |
| `getSymptomUsageStatistics(Integer symptomId)` | `Map<String, Object>` | `symptomId`: 症状ID | 获取症状使用统计信息 | 1. 调用SymptomRepository的findById方法查找指定ID的症状<br>2. 调用SymptomRepository的countMedicinesBySymptomId方法统计关联的药品数量<br>3. 调用SymptomRepository的countSaleRecordsBySymptomId方法统计关联的销售记录数量<br>4. 调用SymptomRepository的sumSaleAmountBySymptomId方法统计关联的销售总额<br>5. 构建并返回统计信息映射 |
| `findSymptomsWithMedicines(Pageable pageable)` | `Page<Symptom>` | `pageable`: 分页参数 | 查找有关联药品的症状 | 1. 调用SymptomRepository的findAll方法获取所有症状<br>2. 过滤有关联药品的症状<br>3. 进行分页处理，返回分页后的症状列表 |
| `findBySaleRecordId(Long saleRecordId, Pageable pageable)` | `Page<Symptom>` | `saleRecordId`: 销售记录ID<br>`pageable`: 分页参数 | 根据销售记录ID分页查找症状 | 1. 调用SymptomRepository的findSaleRecordsBySymptomId方法获取关联指定销售记录的症状列表<br>2. 进行分页处理，返回分页后的症状列表 |

---

### 9. 类：UserServiceImpl（用户服务实现）

**位置**：`com.example.demo.service.impl`

**继承关系**：`extends BaseServiceImpl<User, Long, UserRepository> implements UserService`

**类注解说明**：
- `@Service`：Spring注解，标识为服务组件
- `@Transactional`：Spring注解，声明事务支持

**描述**：用户服务的具体实现，提供用户相关的业务操作

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 业务逻辑 |
|---------|---------|------|------|----------|
| `findByUsername(String username)` | `User` | `username`: 用户名 | 根据用户名查找用户 | 调用UserRepository的findByUsername方法，返回用户对象或null |
| `login(String username, String password)` | `User` | `username`: 用户名<br>`password`: 密码 | 用户登录 | 1. 调用findByUsername方法查找用户<br>2. 如果用户存在，调用matchPassword方法验证密码<br>3. 返回验证通过的用户对象或null |
| `matchPassword(User user, String password)` | `User` | `user`: 用户对象<br>`password`: 密码 | 验证密码 | 1. 使用PasswordEncoder验证密码是否匹配<br>2. 如果匹配成功返回用户对象，否则返回null |
| `updatePassword(User user)` | `User` | `user`: 用户对象 | 更新密码 | 1. 检查密码是否已加密，如未加密则使用PasswordEncoder进行加密<br>2. 调用父类save方法保存用户对象，返回更新后的用户对象 |
| `findAll(Pageable pageable)` | `Page<User>` | `pageable`: 分页参数 | 分页查找所有用户 | 调用UserRepository的findAll方法，返回分页后的用户列表 |
| `findByRole(String role)` | `List<User>` | `role`: 角色 | 根据角色查找用户 | 调用UserRepository的findByRole方法，返回指定角色的用户列表 |
| `changeStatus(Long id, Integer status)` | `User` | `id`: 用户ID<br>`status`: 状态 | 更改用户状态 | 1. 调用findById方法查找指定ID的用户<br>2. 如果用户存在，更新状态<br>3. 调用save方法保存更新后的用户，返回更新后的用户对象 |
| `existsByUsername(String username)` | `boolean` | `username`: 用户名 | 检查用户名是否存在 | 调用UserRepository的existsByUsername方法，返回布尔值 |
| `findByKeyword(String keyword, Pageable pageable)` | `Page<User>` | `keyword`: 搜索关键字<br>`pageable`: 分页参数 | 根据关键字分页查找用户 | 1. 调用UserRepository的searchUsers方法获取包含关键字的用户列表<br>2. 进行分页处理，返回分页后的用户列表 |
| `countByRole(String role)` | `int` | `role`: 角色 | 统计指定角色的用户数量 | 1. 调用UserRepository的findByRole方法获取指定角色的用户列表<br>2. 返回列表大小 |
| `countByUserStatus(Integer userStatus)` | `int` | `userStatus`: 用户状态 | 统计指定状态的用户数量 | 1. 调用UserRepository的findByStatus方法获取指定状态的用户列表<br>2. 返回列表大小 |
| `countAll()` | `int` | 无 | 统计所有用户数量 | 调用UserRepository的count方法，返回用户总数量 |

---
