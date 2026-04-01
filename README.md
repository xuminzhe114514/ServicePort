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

---
          
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

### 2. 类：BaseServiceImpl（基础服务实现）

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

### 3. 接口：CategoryService（分类服务接口）

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

### 4. 类：CategoryServiceImpl（分类服务实现）

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

### 5. 接口：MedicineService（药品服务接口）

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

### 6. 类：MedicineServiceImpl（药品服务实现）

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

---

### 7. 接口：PredictionResultService（预测结果服务接口）

**位置**：`com.example.demo.service`

**继承关系**：`extends BaseService<PredictionResult, Long>`

**描述**：预测结果服务接口，提供预测结果相关的业务操作，包括与外部AI模型服务的交互、库存优化、补货建议等功能。

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 业务逻辑 |
|---------|---------|------|------|----------|
| `findByMedicineId(Long medicineId)` | `List<PredictionResult>` | `medicineId`: 药品ID | 根据药品ID查找预测结果 | 调用PredictionResultRepository的findByMedicineId方法，返回指定药品的预测结果列表 |
| `findByPredictionDate(LocalDate predictionDate)` | `List<PredictionResult>` | `predictionDate`: 预测日期 | 根据预测日期查找预测结果 | 调用PredictionResultRepository的findByPredictionDate方法，返回指定预测日期的预测结果列表 |
| `findLatestByMedicineId(Long medicineId)` | `PredictionResult` | `medicineId`: 药品ID | 查找药品的最新预测结果 | 调用PredictionResultRepository的findFirstByMedicineIdOrderByPredictionDateDesc方法，返回药品的最新预测结果 |
| `findByPredictionDateRange(LocalDate startDate, LocalDate endDate)` | `List<PredictionResult>` | `startDate`: 开始日期<br>`endDate`: 结束日期 | 查找指定日期范围内的预测结果 | 调用PredictionResultRepository的findByPredictionDateBetween方法，返回指定日期范围内的预测结果列表 |
| `findNeedReprediction(Double threshold)` | `List<PredictionResult>` | `threshold`: 准确率阈值 | 查找需要重新预测的结果 | 1. 将Double类型的阈值转换为BigDecimal类型<br>2. 调用PredictionResultRepository的findNeedReprediction方法，返回需要重新预测的结果列表 |
| `getAverageAccuracyByModel()` | `Map<String, Double>` | 无 | 获取各模型的平均准确率 | 1. 调用PredictionResultRepository的findAverageAccuracyByModel方法获取各模型的平均准确率数据<br>2. 转换为模型类型到平均准确率的映射，返回映射 |
| `generatePrediction(Long medicineId, String modelType, LocalDate predictionDate)` | `PredictionResult` | `medicineId`: 药品ID<br>`modelType`: 模型类型<br>`predictionDate`: 预测日期 | 生成预测结果 | 调用重载方法generatePrediction，使用默认预测天数 |
| `generatePrediction(Long medicineId, String modelType, LocalDate predictionDate, int predictionDays)` | `PredictionResult` | `medicineId`: 药品ID<br>`modelType`: 模型类型<br>`predictionDate`: 预测日期<br>`predictionDays`: 预测天数 | 生成预测结果（指定天数） | 1. 调用MedicineRepository查找指定ID的药品<br>2. 调用模型端API进行预测<br>3. 如果API调用失败，使用本地算法生成预测<br>4. 保存并返回预测结果 |
| `generateBatchPredictions(List<Long> medicineIds, String modelType, LocalDate startDate, LocalDate endDate)` | `List<PredictionResult>` | `medicineIds`: 药品ID列表<br>`modelType`: 模型类型<br>`startDate`: 开始日期<br>`endDate`: 结束日期 | 批量生成预测结果（日期范围） | 计算日期差作为预测天数，调用重载方法 |
| `generateBatchPredictions(List<Long> medicineIds, String modelType, int predictionDays)` | `List<PredictionResult>` | `medicineIds`: 药品ID列表<br>`modelType`: 模型类型<br>`predictionDays`: 预测天数 | 批量生成预测结果（指定天数） | 1. 调用模型端批量预测API<br>2. 如果API调用失败，使用本地批量预测算法<br>3. 保存并返回预测结果列表 |
| `getModelPerformance(Long medicineId, int testPeriods)` | `Map<String, Object>` | `medicineId`: 药品ID<br>`testPeriods`: 测试周期数 | 获取模型性能评估 | 1. 调用模型端API获取模型性能评估<br>2. 如果失败返回默认错误信息 |
| `calculateABCClassification(List<Long> medicineIds)` | `Map<String, Object>` | `medicineIds`: 药品ID列表 | 计算药品ABC分类 | 1. 调用模型端ABC分类API<br>2. 如果失败返回默认错误信息 |
| `detectSlowMovingItems(int thresholdDays)` | `Map<String, Object>` | `thresholdDays`: 阈值天数 | 检测滞销药品 | 1. 调用模型端滞销品检测API<br>2. 如果失败返回默认错误信息 |
| `calculateExpiryRisk()` | `Map<String, Object>` | 无 | 计算效期风险 | 1. 调用模型端效期风险计算API<br>2. 如果失败返回默认错误信息 |
| `getInventoryStatusSummary()` | `Map<String, Object>` | 无 | 获取库存状态汇总 | 1. 调用模型端库存状态汇总API<br>2. 如果失败返回默认错误信息 |
| `calculateDynamicSafetyStock(Long medicineId, int predictionDays)` | `Map<String, Object>` | `medicineId`: 药品ID<br>`predictionDays`: 预测天数 | 计算动态安全库存 | 1. 调用模型端动态安全库存计算API<br>2. 如果失败返回默认错误信息 |
| `generateReplenishmentSuggestions(List<Long> medicineIds)` | `Map<String, Object>` | `medicineIds`: 药品ID列表 | 生成补货建议 | 1. 调用模型端补货建议生成API<br>2. 如果失败返回默认错误信息 |
| `generateReplenishmentSuggestionsWithInTransit(List<Map<String, Object>> inTransitOrders)` | `Map<String, Object>` | `inTransitOrders`: 在途订单列表 | 生成带在途订单的补货建议 | 1. 调用模型端带在途订单的补货建议API<br>2. 如果失败返回默认错误信息 |
| `getCurrentInventoryStatus()` | `Map<String, Object>` | 无 | 获取当前库存状态 | 1. 调用模型端当前库存状态API<br>2. 如果失败返回默认错误信息 |
| `getInventoryWithExpiry()` | `Map<String, Object>` | 无 | 获取带效期的库存数据 | 1. 调用模型端带效期的库存数据API<br>2. 如果失败返回默认错误信息 |
| `getMedicineInventoryStatus(Long medicineId)` | `Map<String, Object>` | `medicineId`: 药品ID | 获取单个药品的库存状态 | 1. 调用模型端单个药品库存状态API<br>2. 如果失败返回默认错误信息 |
| `calculateSafetyStock(Long medicineId, int leadTimeDays, double serviceLevel)` | `Map<String, Object>` | `medicineId`: 药品ID<br>`leadTimeDays`: 前置时间（天）<br>`serviceLevel`: 服务水平 | 计算单个药品的安全库存和补货点 | 1. 调用模型端单个药品安全库存计算API<br>2. 如果失败返回默认错误信息 |
| `calculateBatchSafetyStock(int leadTimeDays, double serviceLevel)` | `Map<String, Object>` | `leadTimeDays`: 前置时间（天）<br>`serviceLevel`: 服务水平 | 批量计算所有药品的安全库存和补货点 | 1. 调用模型端批量安全库存计算API<br>2. 如果失败返回默认错误信息 |
| `getRecommendedOrderQuantities(LocalDate targetDate)` | `Map<Long, Integer>` | `targetDate`: 目标日期 | 获取建议订购数量（所有药品） | 1. 获取所有启用状态的药品<br>2. 调用重载方法获取建议订购数量 |
| `getRecommendedOrderQuantities(LocalDate targetDate, List<Long> medicineIds)` | `Map<Long, Integer>` | `targetDate`: 目标日期<br>`medicineIds`: 药品ID列表 | 获取建议订购数量（指定药品） | 1. 查找指定药品的最新预测结果<br>2. 如果预测结果不存在或过期，重新生成预测<br>3. 构建药品ID到建议订购数量的映射并返回 |
| `pushSalesData(List<Map<String, Object>> salesData)` | `Map<String, Object>` | `salesData`: 销售数据列表 | 推送销售数据到模型端 | 1. 调用模型端数据推送API<br>2. 返回推送结果 |
| `pushMedicineData(List<Map<String, Object>> medicineData)` | `Map<String, Object>` | `medicineData`: 药品数据列表 | 推送药品数据到模型端 | 1. 调用模型端数据推送API<br>2. 返回推送结果 |
| `pushStockData(List<Map<String, Object>> stockData)` | `Map<String, Object>` | `stockData`: 库存数据列表 | 推送库存数据到模型端 | 1. 调用模型端数据推送API<br>2. 返回推送结果 |
| `pushPurchaseOrderData(List<Map<String, Object>> purchaseOrderData)` | `Map<String, Object>` | `purchaseOrderData`: 采购订单数据列表 | 推送采购订单数据到模型端 | 1. 调用模型端数据推送API<br>2. 返回推送结果 |
| `pushCategoryData(List<Map<String, Object>> categoryData)` | `Map<String, Object>` | `categoryData`: 分类数据列表 | 推送分类数据到模型端 | 1. 调用模型端数据推送API<br>2. 返回推送结果 |
| `pushSymptomData(List<Map<String, Object>> symptomData)` | `Map<String, Object>` | `symptomData`: 症状数据列表 | 推送症状数据到模型端 | 1. 调用模型端数据推送API<br>2. 返回推送结果 |
| `pushDailySalesData(List<Map<String, Object>> dailySalesData)` | `Map<String, Object>` | `dailySalesData`: 每日销售数据列表 | 推送每日销售数据到模型端 | 1. 调用模型端数据推送API<br>2. 返回推送结果 |
| `pushSalesBySymptomData(List<Map<String, Object>> salesBySymptomData)` | `Map<String, Object>` | `salesBySymptomData`: 按症状分类的销售数据列表 | 推送按症状分类的销售数据到模型端 | 1. 调用模型端数据推送API<br>2. 返回推送结果 |
| `pushStockTurnoverData(List<Map<String, Object>> stockTurnoverData)` | `Map<String, Object>` | `stockTurnoverData`: 库存周转率数据列表 | 推送库存周转率数据到模型端 | 1. 调用模型端数据推送API<br>2. 返回推送结果 |
| `pushStockValueData(List<Map<String, Object>> stockValueData)` | `Map<String, Object>` | `stockValueData`: 库存价值数据列表 | 推送库存价值数据到模型端 | 1. 调用模型端数据推送API<br>2. 返回推送结果 |
| `pushExpiringStockData(List<Map<String, Object>> expiringStockData)` | `Map<String, Object>` | `expiringStockData`: 即将过期的库存数据列表 | 推送即将过期的库存数据到模型端 | 1. 调用模型端数据推送API<br>2. 返回推送结果 |
| `pushLowStockData(List<Map<String, Object>> lowStockData)` | `Map<String, Object>` | `lowStockData`: 库存不足的药品数据列表 | 推送库存不足的药品数据到模型端 | 1. 调用模型端数据推送API<br>2. 返回推送结果 |
| `getDataSummary()` | `Map<String, Object>` | 无 | 获取数据摘要 | 1. 调用模型端数据摘要API<br>2. 如果失败返回默认错误信息 |
| `checkModelServiceHealth()` | `boolean` | 无 | 检查模型端服务是否可用 | 1. 调用模型端健康检查端点<br>2. 返回服务可用状态 |
| `getModelServiceInfo()` | `Map<String, Object>` | 无 | 获取模型端服务信息 | 1. 构建包含服务基础URL、API版本、健康检查URL和状态的信息映射<br>2. 返回服务信息 |

---

### 8. 类：PredictionResultServiceImpl（预测结果服务实现）

**位置**：`com.example.demo.service.impl`

**继承关系**：`extends BaseServiceImpl<PredictionResult, Long, PredictionResultRepository> implements PredictionResultService`

**类注解说明**：
- `@Service`：Spring注解，标识为服务组件
- `@Transactional`：Spring注解，声明事务支持
- `@Slf4j`：Lombok注解，提供日志记录功能

**描述**：预测结果服务的具体实现，提供预测结果相关的业务操作。当前实现通过调用外部模型服务获取预测结果，支持单个和批量预测、库存优化、补货建议、ABC分类、滞销品检测、效期风险评估等功能。所有与模型端的交互均通过REST API完成，当API调用失败时会降级使用本地算法。

**属性列表**：

| 字段名 | 数据类型 | 描述 | 注解 |
|--------|----------|------|------|
| `restTemplate` | `RestTemplate` | 用于调用模型端API的HTTP客户端 | `@Autowired` |
| `objectMapper` | `ObjectMapper` | JSON对象映射器 | `@Autowired` |
| `modelServiceBaseUrl` | `String` | 模型端服务基础URL | `@Value("${model.service.base-url:http://localhost:5101}")` |
| `modelApiVersion` | `String` | 模型端API版本路径 | `@Value("${model.service.api-version:/api/v1}")` |
| `defaultPredictionDays` | `int` | 默认预测天数 | `@Value("${prediction.default-days:7}")` |
| `confidenceIntervalFactor` | `double` | 置信区间因子 | `@Value("${prediction.confidence-interval-factor:0.2}")` |
| `safetyStockFactor` | `double` | 安全库存因子 | `@Value("${inventory.safety-stock-factor:0.5}")` |
| `medicineRepository` | `MedicineRepository` | 药品数据访问接口 | `@Autowired` |
| `stockRepository` | `StockRepository` | 库存数据访问接口 | `@Autowired` |

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 业务逻辑 |
|---------|---------|------|------|----------|
| `findByMedicineId(Long medicineId)` | `List<PredictionResult>` | `medicineId`: 药品ID | 根据药品ID查找预测结果 | 调用PredictionResultRepository的findByMedicineId方法，初始化关联药品对象，返回预测结果列表 |
| `findByPredictionDate(LocalDate predictionDate)` | `List<PredictionResult>` | `predictionDate`: 预测日期 | 根据预测日期查找预测结果 | 调用PredictionResultRepository的findByPredictionDate方法，初始化关联药品对象，返回预测结果列表 |
| `findLatestByMedicineId(Long medicineId)` | `PredictionResult` | `medicineId`: 药品ID | 查找药品的最新预测结果 | 调用PredictionResultRepository的findFirstByMedicineIdOrderByPredictionDateDesc方法，初始化关联药品对象，返回最新预测结果 |
| `findByPredictionDateRange(LocalDate startDate, LocalDate endDate)` | `List<PredictionResult>` | `startDate`: 开始日期<br>`endDate`: 结束日期 | 查找指定日期范围内的预测结果 | 调用PredictionResultRepository的findByPredictionDateBetween方法，初始化关联药品对象，返回预测结果列表 |
| `findNeedReprediction(Double threshold)` | `List<PredictionResult>` | `threshold`: 准确率阈值 | 查找需要重新预测的结果 | 1. 将Double类型的阈值转换为BigDecimal类型<br>2. 调用PredictionResultRepository的findNeedReprediction方法，初始化关联药品对象，返回需要重新预测的结果列表 |
| `getAverageAccuracyByModel()` | `Map<String, Double>` | 无 | 获取各模型的平均准确率 | 1. 调用PredictionResultRepository的findAverageAccuracyByModel方法获取各模型的平均准确率数据<br>2. 处理不同类型（BigDecimal/Double/Long/Integer）的准确率值<br>3. 转换为模型类型到平均准确率的映射，返回映射 |
| `generatePrediction(Long medicineId, String modelType, LocalDate predictionDate)` | `PredictionResult` | `medicineId`: 药品ID<br>`modelType`: 模型类型<br>`predictionDate`: 预测日期 | 生成预测结果 | 调用重载方法generatePrediction，使用默认预测天数（defaultPredictionDays） |
| `generatePrediction(Long medicineId, String modelType, LocalDate predictionDate, int predictionDays)` | `PredictionResult` | `medicineId`: 药品ID<br>`modelType`: 模型类型<br>`predictionDate`: 预测日期<br>`predictionDays`: 预测天数 | 生成预测结果（指定天数） | 1. 调用MedicineRepository查找指定ID的药品<br>2. 调用callModelPredictionApi方法调用模型端API<br>3. 如果API调用成功，调用createPredictionResultFromModelResponse创建预测结果<br>4. 如果API调用失败，调用generateLocalPrediction使用本地算法生成预测<br>5. 保存并返回预测结果 |
| `generateBatchPredictions(List<Long> medicineIds, String modelType, LocalDate startDate, LocalDate endDate)` | `List<PredictionResult>` | `medicineIds`: 药品ID列表<br>`modelType`: 模型类型<br>`startDate`: 开始日期<br>`endDate`: 结束日期 | 批量生成预测结果（日期范围） | 1. 计算开始日期和结束日期之间的天数差<br>2. 调用重载方法generateBatchPredictions，传入预测天数 |
| `generateBatchPredictions(List<Long> medicineIds, String modelType, int predictionDays)` | `List<PredictionResult>` | `medicineIds`: 药品ID列表<br>`modelType`: 模型类型<br>`predictionDays`: 预测天数 | 批量生成预测结果（指定天数） | 1. 调用callBatchPredictionApi方法调用模型端批量预测API<br>2. 如果API调用成功，调用createBatchPredictionsFromModelResponse创建批量预测结果<br>3. 如果API调用失败，调用generateLocalBatchPredictions使用本地批量预测算法<br>4. 如果结果非空，保存并返回预测结果列表 |
| `getModelPerformance(Long medicineId, int testPeriods)` | `Map<String, Object>` | `medicineId`: 药品ID<br>`testPeriods`: 测试周期数 | 获取模型性能评估 | 1. 调用getModelPerformanceApi方法调用模型端API<br>2. 如果API调用成功，返回性能评估数据<br>3. 如果失败，返回包含错误信息的默认映射 |
| `calculateABCClassification(List<Long> medicineIds)` | `Map<String, Object>` | `medicineIds`: 药品ID列表 | 计算药品ABC分类 | 1. 调用calculateABCClassificationApi方法调用模型端API<br>2. 如果API调用成功，返回ABC分类数据<br>3. 如果失败，返回包含错误信息的默认映射 |
| `detectSlowMovingItems(int thresholdDays)` | `Map<String, Object>` | `thresholdDays`: 阈值天数 | 检测滞销药品 | 1. 调用detectSlowMovingItemsApi方法调用模型端API<br>2. 如果API调用成功，返回滞销品检测数据<br>3. 如果失败，返回包含错误信息的默认映射 |
| `calculateExpiryRisk()` | `Map<String, Object>` | 无 | 计算效期风险 | 1. 调用calculateExpiryRiskApi方法调用模型端API<br>2. 如果API调用成功，返回效期风险数据<br>3. 如果失败，返回包含错误信息的默认映射 |
| `getInventoryStatusSummary()` | `Map<String, Object>` | 无 | 获取库存状态汇总 | 1. 调用getInventoryStatusSummaryApi方法调用模型端API<br>2. 如果API调用成功，返回库存状态汇总数据<br>3. 如果失败，返回包含错误信息的默认映射 |
| `calculateDynamicSafetyStock(Long medicineId, int predictionDays)` | `Map<String, Object>` | `medicineId`: 药品ID<br>`predictionDays`: 预测天数 | 计算动态安全库存 | 1. 调用calculateDynamicSafetyStockApi方法调用模型端API<br>2. 如果API调用成功，返回动态安全库存数据<br>3. 如果失败，返回包含错误信息的默认映射 |
| `generateReplenishmentSuggestions(List<Long> medicineIds)` | `Map<String, Object>` | `medicineIds`: 药品ID列表 | 生成补货建议 | 1. 调用generateReplenishmentSuggestionsApi方法调用模型端API<br>2. 如果API调用成功，返回补货建议数据<br>3. 如果失败，返回包含错误信息的默认映射 |
| `generateReplenishmentSuggestionsWithInTransit(List<Map<String, Object>> inTransitOrders)` | `Map<String, Object>` | `inTransitOrders`: 在途订单列表 | 生成带在途订单的补货建议 | 1. 调用generateReplenishmentSuggestionsWithInTransitApi方法调用模型端API<br>2. 如果API调用成功，返回带在途订单的补货建议数据<br>3. 如果失败，返回包含错误信息的默认映射 |
| `getCurrentInventoryStatus()` | `Map<String, Object>` | 无 | 获取当前库存状态 | 1. 调用getCurrentInventoryStatusApi方法调用模型端API<br>2. 如果API调用成功，返回当前库存状态数据<br>3. 如果失败，返回包含错误信息的默认映射 |
| `getInventoryWithExpiry()` | `Map<String, Object>` | 无 | 获取带效期的库存数据 | 1. 调用getInventoryWithExpiryApi方法调用模型端API<br>2. 如果API调用成功，返回带效期的库存数据<br>3. 如果失败，返回包含错误信息的默认映射 |
| `getMedicineInventoryStatus(Long medicineId)` | `Map<String, Object>` | `medicineId`: 药品ID | 获取单个药品的库存状态 | 1. 调用getMedicineInventoryStatusApi方法调用模型端API<br>2. 如果API调用成功，返回单个药品库存状态数据<br>3. 如果失败，返回包含错误信息的默认映射 |
| `calculateSafetyStock(Long medicineId, int leadTimeDays, double serviceLevel)` | `Map<String, Object>` | `medicineId`: 药品ID<br>`leadTimeDays`: 前置时间（天）<br>`serviceLevel`: 服务水平 | 计算单个药品的安全库存和补货点 | 1. 调用calculateSafetyStockApi方法调用模型端API<br>2. 如果API调用成功，返回单个药品安全库存数据<br>3. 如果失败，返回包含错误信息的默认映射 |
| `calculateBatchSafetyStock(int leadTimeDays, double serviceLevel)` | `Map<String, Object>` | `leadTimeDays`: 前置时间（天）<br>`serviceLevel`: 服务水平 | 批量计算所有药品的安全库存和补货点 | 1. 调用calculateBatchSafetyStockApi方法调用模型端API<br>2. 如果API调用成功，返回批量安全库存数据<br>3. 如果失败，返回包含错误信息的默认映射 |
| `getRecommendedOrderQuantities(LocalDate targetDate)` | `Map<Long, Integer>` | `targetDate`: 目标日期 | 获取建议订购数量（所有药品） | 1. 调用MedicineRepository查找所有启用状态的药品<br>2. 提取药品ID列表<br>3. 调用重载方法getRecommendedOrderQuantities获取建议订购数量 |
| `getRecommendedOrderQuantities(LocalDate targetDate, List<Long> medicineIds)` | `Map<Long, Integer>` | `targetDate`: 目标日期<br>`medicineIds`: 药品ID列表 | 获取建议订购数量（指定药品） | 1. 调用MedicineRepository查找指定ID的药品列表<br>2. 调用repository.findLatestByMedicineIds获取最新的预测结果映射<br>3. 遍历药品列表，如果预测结果不存在或已过期，调用generatePrediction生成新预测<br>4. 构建并返回药品ID到建议订购数量的映射 |
| `checkModelServiceHealth()` | `boolean` | 无 | 检查模型端服务是否可用 | 1. 构造测试URL（modelServiceBaseUrl + "/api/data/summary"）<br>2. 调用restTemplate.getForEntity发送GET请求<br>3. 如果返回状态码为200，返回true，否则返回false |
| `getModelServiceInfo()` | `Map<String, Object>` | 无 | 获取模型端服务信息 | 1. 构建包含baseUrl、apiVersion、healthCheckUrl（baseUrl + "/health"）和isHealthy（调用checkModelServiceHealth的结果）的信息映射<br>2. 返回服务信息映射 |
| `pushSalesData(List<Map<String, Object>> salesData)` | `Map<String, Object>` | `salesData`: 销售数据列表 | 推送销售数据到模型端 | 调用pushData方法，端点为"sales" |
| `pushMedicineData(List<Map<String, Object>> medicineData)` | `Map<String, Object>` | `medicineData`: 药品数据列表 | 推送药品数据到模型端 | 调用pushData方法，端点为"medicines" |
| `pushStockData(List<Map<String, Object>> stockData)` | `Map<String, Object>` | `stockData`: 库存数据列表 | 推送库存数据到模型端 | 调用pushData方法，端点为"stocks" |
| `pushPurchaseOrderData(List<Map<String, Object>> purchaseOrderData)` | `Map<String, Object>` | `purchaseOrderData`: 采购订单数据列表 | 推送采购订单数据到模型端 | 调用pushData方法，端点为"purchase-orders" |
| `pushCategoryData(List<Map<String, Object>> categoryData)` | `Map<String, Object>` | `categoryData`: 分类数据列表 | 推送分类数据到模型端 | 调用pushData方法，端点为"categories" |
| `pushSymptomData(List<Map<String, Object>> symptomData)` | `Map<String, Object>` | `symptomData`: 症状数据列表 | 推送症状数据到模型端 | 调用pushData方法，端点为"symptoms" |
| `pushDailySalesData(List<Map<String, Object>> dailySalesData)` | `Map<String, Object>` | `dailySalesData`: 每日销售数据列表 | 推送每日销售数据到模型端 | 调用pushData方法，端点为"daily-sales" |
| `pushSalesBySymptomData(List<Map<String, Object>> salesBySymptomData)` | `Map<String, Object>` | `salesBySymptomData`: 按症状分类的销售数据列表 | 推送按症状分类的销售数据到模型端 | 调用pushData方法，端点为"sales-by-symptom" |
| `pushStockTurnoverData(List<Map<String, Object>> stockTurnoverData)` | `Map<String, Object>` | `stockTurnoverData`: 库存周转率数据列表 | 推送库存周转率数据到模型端 | 调用pushData方法，端点为"stock-turnover" |
| `pushStockValueData(List<Map<String, Object>> stockValueData)` | `Map<String, Object>` | `stockValueData`: 库存价值数据列表 | 推送库存价值数据到模型端 | 调用pushData方法，端点为"stock-value" |
| `pushExpiringStockData(List<Map<String, Object>> expiringStockData)` | `Map<String, Object>` | `expiringStockData`: 即将过期的库存数据列表 | 推送即将过期的库存数据到模型端 | 调用pushData方法，端点为"expiring-stock" |
| `pushLowStockData(List<Map<String, Object>> lowStockData)` | `Map<String, Object>` | `lowStockData`: 库存不足的药品数据列表 | 推送库存不足的药品数据到模型端 | 调用pushData方法，端点为"low-stock" |
| `getDataSummary()` | `Map<String, Object>` | 无 | 获取数据摘要 | 1. 调用getDataSummaryApi方法调用模型端API<br>2. 如果API调用成功，返回数据摘要<br>3. 如果失败，返回包含错误信息的默认映射 |

**私有方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 业务逻辑 |
|---------|---------|------|------|----------|
| `callModelPredictionApi(Long medicineId, LocalDate predictionDate, int predictionDays)` | `Map<String, Object>` | `medicineId`: 药品ID<br>`predictionDate`: 预测日期<br>`predictionDays`: 预测天数 | 调用模型端单个药品预测API | 1. 构造API URL（modelServiceBaseUrl + modelApiVersion + "/predict/single-medicine"）<br>2. 构建请求体（medicineId、predictionDays）<br>3. 设置请求头（Content-Type: application/json）<br>4. 发送POST请求<br>5. 如果响应状态为200且status为"success"，返回响应体<br>6. 否则记录错误并返回null |
| `callBatchPredictionApi(List<Long> medicineIds, int predictionDays)` | `List<Map<String, Object>>` | `medicineIds`: 药品ID列表<br>`predictionDays`: 预测天数 | 调用模型端批量预测API | 1. 构造API URL（modelServiceBaseUrl + modelApiVersion + "/predict/batch"）<br>2. 构建请求体（medicineIds、predictionDays）<br>3. 设置请求头（Content-Type: application/json）<br>4. 发送POST请求<br>5. 如果响应状态为200且status为"success"，返回data中的medicines列表<br>6. 否则记录错误并返回null |
| `getHistoricalPredictions(Long medicineId)` | `List<Map<String, Object>>` | `medicineId`: 药品ID | 调用模型端获取历史预测记录 | 1. 构造API URL（modelServiceBaseUrl + modelApiVersion + "/predict/history/" + medicineId）<br>2. 发送GET请求<br>3. 如果响应状态为200且status为"success"，返回data中的history列表<br>4. 否则返回null |
| `getModelPerformanceApi(Long medicineId, int testPeriods)` | `Map<String, Object>` | `medicineId`: 药品ID<br>`testPeriods`: 测试周期数 | 调用模型端获取模型性能评估 | 1. 构造API URL（modelServiceBaseUrl + modelApiVersion + "/predict/performance/" + medicineId + "?testPeriods=" + testPeriods）<br>2. 发送GET请求<br>3. 如果响应状态为200且status为"success"，返回响应体<br>4. 否则返回null |
| `createPredictionResultFromModelResponse(Medicine medicine, Map<String, Object> modelResponse, String modelType, LocalDate predictionDate)` | `PredictionResult` | `medicine`: 药品对象<br>`modelResponse`: 模型响应数据<br>`modelType`: 模型类型<br>`predictionDate`: 预测日期 | 从模型端响应创建预测结果 | 1. 创建PredictionResult对象<br>2. 从响应数据中提取predictions列表<br>3. 找到目标预测日期的预测数据<br>4. 设置预测数量、置信区间上下限、模型类型<br>5. 计算准确率（calculateAccuracyRate）<br>6. 计算建议订购数量（calculateRecommendedOrderQuantity）<br>7. 设置创建时间和更新时间<br>8. 保存并返回预测结果 |
| `createBatchPredictionsFromModelResponse(List<Long> medicineIds, List<Map<String, Object>> batchPredictions, String modelType, LocalDate startDate, LocalDate endDate)` | `List<PredictionResult>` | `medicineIds`: 药品ID列表<br>`batchPredictions`: 批量预测数据<br>`modelType`: 模型类型<br>`startDate`: 开始日期<br>`endDate`: 结束日期 | 从批量响应创建预测结果 | 1. 构建药品ID到Medicine对象的映射<br>2. 遍历批量预测数据<br>3. 对于每个药品，遍历其预测数据，如果在日期范围内，创建PredictionResult对象<br>4. 设置预测属性、计算准确率和建议订购数量<br>5. 收集所有预测结果并返回 |
| `calculateABCClassificationApi(List<Long> medicineIds)` | `Map<String, Object>` | `medicineIds`: 药品ID列表 | 调用模型端计算ABC分类 | 1. 构造API URL（modelServiceBaseUrl + modelApiVersion + "/classification/abc"）<br>2. 构建请求体（medicineIds）<br>3. 发送POST请求<br>4. 如果响应状态为200且status为"success"，返回响应体<br>5. 否则返回null |
| `detectSlowMovingItemsApi(int thresholdDays)` | `Map<String, Object>` | `thresholdDays`: 阈值天数 | 调用模型端检测滞销药品 | 1. 构造API URL（modelServiceBaseUrl + modelApiVersion + "/classification/slow-moving?thresholdDays=" + thresholdDays）<br>2. 发送GET请求<br>3. 如果响应状态为200且status为"success"，返回响应体<br>4. 否则返回null |
| `calculateExpiryRiskApi()` | `Map<String, Object>` | 无 | 调用模型端计算效期风险 | 1. 构造API URL（modelServiceBaseUrl + modelApiVersion + "/classification/expiry-risk"）<br>2. 构建空请求体<br>3. 发送POST请求<br>4. 如果响应状态为200且status为"success"，返回响应体<br>5. 否则返回null |
| `getInventoryStatusSummaryApi()` | `Map<String, Object>` | 无 | 调用模型端获取库存状态汇总 | 1. 构造API URL（modelServiceBaseUrl + modelApiVersion + "/classification/inventory-status"）<br>2. 发送GET请求<br>3. 如果响应状态为200且status为"success"，返回响应体<br>4. 否则返回null |
| `generateLocalPrediction(Medicine medicine, String modelType, LocalDate predictionDate, int predictionDays)` | `PredictionResult` | `medicine`: 药品对象<br>`modelType`: 模型类型<br>`predictionDate`: 预测日期<br>`predictionDays`: 预测天数 | 本地预测算法（模型端API失败时使用） | 1. 创建PredictionResult对象<br>2. 调用calculateLocalPredictedQuantity计算预测数量（基于药品季节性、处方药状态、分类等因素）<br>3. 根据confidenceIntervalFactor设置置信区间<br>4. 计算准确率和建议订购数量<br>5. 保存并返回预测结果 |
| `generateLocalBatchPredictions(List<Long> medicineIds, String modelType, LocalDate startDate, LocalDate endDate)` | `List<PredictionResult>` | `medicineIds`: 药品ID列表<br>`modelType`: 模型类型<br>`startDate`: 开始日期<br>`endDate`: 结束日期 | 本地批量预测算法 | 1. 构建药品ID到Medicine对象的映射<br>2. 计算日期范围内的每一天<br>3. 对每个药品的每一天调用generateLocalPrediction生成预测<br>4. 收集所有预测结果并返回 |
| `calculateAccuracyRate(Long medicineId, Integer predictedQuantity)` | `BigDecimal` | `medicineId`: 药品ID<br>`predictedQuantity`: 预测数量 | 计算预测准确率 | 1. 获取历史预测记录<br>2. 计算历史平均准确率<br>3. 添加随机波动（0.9-1.1倍）模拟真实情况<br>4. 返回最终准确率（默认85.5%） |
| `calculateLocalPredictedQuantity(Long medicineId, int predictionDays)` | `int` | `medicineId`: 药品ID<br>`predictionDays`: 预测天数 | 本地算法：计算预测数量 | 1. 获取药品信息<br>2. 基础预测值为20 * predictionDays<br>3. 如果是季节性药品，根据随机波动调整<br>4. 如果是处方药，根据随机波动调整<br>5. 根据分类调整（如感冒、咳嗽类药品增加）<br>6. 添加随机因子（0.7-1.3）<br>7. 返回最终预测数量 |
| `calculateRecommendedOrderQuantity(Long medicineId, int predictedQuantity)` | `int` | `medicineId`: 药品ID<br>`predictedQuantity`: 预测数量 | 计算建议订购数量 | 1. 获取当前有效库存<br>2. 计算安全库存（predictedQuantity * safetyStockFactor）<br>3. 获取再订货点和最小订购量<br>4. 如果当前库存小于等于再订货点，计算建议订购量<br>5. 确保不低于最小订购量，并按最小订购量倍数取整<br>6. 返回建议订购数量或0（如果库存充足） |
| `calculateDynamicSafetyStockApi(Long medicineId, int predictionDays)` | `Map<String, Object>` | `medicineId`: 药品ID<br>`predictionDays`: 预测天数 | 调用模型端计算动态安全库存 | 1. 构造API URL（modelServiceBaseUrl + modelApiVersion + "/inventory/dynamic-safety-stock"）<br>2. 构建请求体（medicineId、predictionDays）<br>3. 发送POST请求<br>4. 如果响应状态为200且status为"success"，返回响应体<br>5. 否则返回null |
| `generateReplenishmentSuggestionsApi(List<Long> medicineIds)` | `Map<String, Object>` | `medicineIds`: 药品ID列表 | 调用模型端生成补货建议 | 1. 构造API URL（modelServiceBaseUrl + modelApiVersion + "/inventory/replenishment-suggestion"）<br>2. 构建请求体（medicineIds）<br>3. 发送POST请求<br>4. 如果响应状态为200且status为"success"，返回响应体<br>5. 否则返回null |
| `generateReplenishmentSuggestionsWithInTransitApi(List<Map<String, Object>> inTransitOrders)` | `Map<String, Object>` | `inTransitOrders`: 在途订单列表 | 调用模型端生成带在途订单的补货建议 | 1. 构造API URL（modelServiceBaseUrl + modelApiVersion + "/inventory/replenishment"）<br>2. 构建请求体（inTransitOrders）<br>3. 发送POST请求<br>4. 如果响应状态为200且status为"success"，返回响应体<br>5. 否则返回null |
| `getCurrentInventoryStatusApi()` | `Map<String, Object>` | 无 | 调用模型端获取当前库存状态 | 1. 构造API URL（modelServiceBaseUrl + modelApiVersion + "/inventory/current"）<br>2. 发送GET请求<br>3. 如果响应状态为200且status为"success"，返回响应体<br>4. 否则返回null |
| `getInventoryWithExpiryApi()` | `Map<String, Object>` | 无 | 调用模型端获取带效期的库存数据 | 1. 构造API URL（modelServiceBaseUrl + modelApiVersion + "/inventory/with-expiry"）<br>2. 发送GET请求<br>3. 如果响应状态为200且status为"success"，返回响应体<br>4. 否则返回null |
| `getMedicineInventoryStatusApi(Long medicineId)` | `Map<String, Object>` | `medicineId`: 药品ID | 调用模型端获取单个药品的库存状态 | 1. 构造API URL（modelServiceBaseUrl + modelApiVersion + "/inventory/stock/" + medicineId）<br>2. 发送GET请求<br>3. 如果响应状态为200且status为"success"，返回响应体<br>4. 否则返回null |
| `calculateSafetyStockApi(Long medicineId, int leadTimeDays, double serviceLevel)` | `Map<String, Object>` | `medicineId`: 药品ID<br>`leadTimeDays`: 前置时间（天）<br>`serviceLevel`: 服务水平 | 调用模型端计算单个药品的安全库存和补货点 | 1. 构造API URL（modelServiceBaseUrl + modelApiVersion + "/inventory/safety-stock" + 查询参数）<br>2. 发送GET请求<br>3. 如果响应状态为200且status为"success"，返回响应体<br>4. 否则返回null |
| `calculateBatchSafetyStockApi(int leadTimeDays, double serviceLevel)` | `Map<String, Object>` | `leadTimeDays`: 前置时间（天）<br>`serviceLevel`: 服务水平 | 调用模型端批量计算所有药品的安全库存和补货点 | 1. 构造API URL（modelServiceBaseUrl + modelApiVersion + "/inventory/safety-stock/batch" + 查询参数）<br>2. 发送GET请求<br>3. 如果响应状态为200且status为"success"，返回响应体<br>4. 否则返回null |
| `pushData(String endpoint, Object requestBody)` | `Map<String, Object>` | `endpoint`: 端点路径<br>`requestBody`: 请求体 | 通用数据推送方法 | 1. 构造API URL（modelServiceBaseUrl + "/api/data/" + endpoint）<br>2. 设置请求头（Content-Type: application/json）<br>3. 发送POST请求<br>4. 如果响应状态为200，返回响应体<br>5. 否则返回包含错误信息的默认映射 |
| `getDataSummaryApi()` | `Map<String, Object>` | 无 | 调用模型端获取数据摘要 | 1. 构造API URL（modelServiceBaseUrl + "/api/data/summary"）<br>2. 发送GET请求<br>3. 如果响应状态为200，返回响应体<br>4. 否则返回null |

**配置属性说明**：

| 属性名 | 默认值 | 描述 |
|--------|--------|------|
| `model.service.base-url` | `http://localhost:5101` | 模型端服务的基础URL |
| `model.service.api-version` | `/api/v1` | 模型端API版本路径 |
| `prediction.default-days` | `7` | 默认预测天数 |
| `prediction.confidence-interval-factor` | `0.2` | 置信区间因子，用于计算置信区间上下限 |
| `inventory.safety-stock-factor` | `0.5` | 安全库存因子，用于计算建议订购数量时的安全库存 |

---

### 9. 接口：PurchaseOrderService（采购订单服务接口）

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

### 10. 类：PurchaseOrderServiceImpl（采购订单服务实现）

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

### 11. 接口：SaleRecordService（销售记录服务接口）

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

### 12. 类：SaleRecordServiceImpl（销售记录服务实现）

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

### 13. 接口：StockService（库存服务接口）

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

### 14. 类：StockServiceImpl（库存服务实现）

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

### 15. 接口：SymptomService（症状服务接口）

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

### 16. 类：SymptomServiceImpl（症状服务实现）

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

### 17. 接口：UserService（用户服务接口）

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
| `countByRole(String role)` | `int` | `role`: 角色 | 统计指定角色的用户数量 | 1. 调用UserRepository的findByRole方法获取指定角色的用户列表<br>2. 返回列表大小 |
| `countByUserStatus(Integer userStatus)` | `int` | `userStatus`: 用户状态 | 统计指定状态的用户数量 | 1. 调用UserRepository的findByStatus方法获取指定状态的用户列表<br>2. 返回列表大小 |
| `countAll()` | `int` | 无 | 统计所有用户数量 | 调用UserRepository的count方法，返回用户总数量 |

---

### 18. 类：UserServiceImpl（用户服务实现）

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

5. **返回值处理**：
   - 成功：返回操作后的实体对象或列表，使用Hibernate.initialize初始化关联属性
   - 失败：抛出异常，由全局异常处理器处理
---


---

---

## 控制器层 (Controller Layer) 文档

### API基本路径

**baseURL**: `/api`  
**端口号**：`8081`

---

### 1. 类：CategoryController（药品分类控制器）

**位置**：`com.example.demo.controller`

**类注解说明**：
- `@RestController`：Spring注解，标识为REST风格的控制器
- `@RequestMapping("/categories")`：设置请求路径前缀
- `@CrossOrigin(origins = "*")`：允许跨域请求

**方法列表**：

| 方法签名 | HTTP方法 | API路径 | 参数 | 描述 | 返回值说明 | 失败情况返回值 |
|----------|----------|---------|------|------|------------|----------------|
| `test()` | GET | `/categories/test` | 无 | 测试接口 | `{"success": true, "message": "CategoryController is working!"}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getAllCategories(int page, int size, String sortBy, String direction)` | GET | `/categories` | `page`: 页码(默认0)<br>`size`: 每页大小(默认10)<br>`sortBy`: 排序字段(默认sort)<br>`direction`: 排序方向(默认asc) | 获取所有分类（分页） | `{"success": true, "currentPage": 页码, "totalItems": 总记录数, "totalPages": 总页数, "data": [分类列表]}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getCategoryById(Long id)` | GET | `/categories/{id}` | `id`: 分类ID | 根据ID获取分类 | `{"success": true, "data": {分类详情}}` | `{"success": false, "message": "分类不存在"}` |
| `createCategory(Category category)` | POST | `/categories` | `category`: 分类对象（JSON） | 创建新分类 | `{"success": true, "message": "分类创建成功", "data": {分类详情}}` | `{"success": false, "message": "分类名称已存在"}` |
| `updateCategory(Long id, Category category)` | PUT | `/categories/{id}` | `id`: 分类ID<br>`category`: 分类对象（JSON） | 更新分类信息 | `{"success": true, "message": "分类更新成功", "data": {分类详情}}` | `{"success": false, "message": "分类不存在"}` |
| `deleteCategory(Long id)` | DELETE | `/categories/{id}` | `id`: 分类ID | 删除分类 | `{"success": true, "message": "分类删除成功"}` | `{"success": false, "message": "该分类下存在药品，无法删除"}` |
| `getCategoriesByParentId(Long parentId)` | GET | `/categories/parent/{parentId}` | `parentId`: 父分类ID | 根据父分类ID获取子分类列表 | `{"success": true, "data": [子分类列表]}` | `{"success": false, "message":"未找到符合条件的分类"}` |
| `getRootCategories()` | GET | `/categories/roots` | 无 | 获取所有一级分类（根分类） | `{"success": true, "data": [根分类列表]}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getCategoryTree()` | GET | `/categories/tree` | 无 | 获取分类树形结构 | `{"success": true, "data": [分类树结构]}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getCategoryPath(Long id)` | GET | `/categories/{id}/path` | `id`: 分类ID | 获取分类路径映射 | `{"success": true, "data": {路径映射}}` | `{"success": false, "message": "分类不存在"}` |
| `getCategoriesByLevel(Integer level)` | GET | `/categories/level/{level}` | `level`: 分类级别 | 根据分类级别查找分类 | `{"success": true, "data": [分类列表]}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `checkCategoryNameExists(String name)` | GET | `/categories/check-name/{name}` | `name`: 分类名称 | 检查分类名称是否存在 | `{"success": true, "exists": true/false}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `updateCategoryTree()` | POST | `/categories/update-tree` | 无 | 更新分类树（重新计算所有分类的级别） | `{"success": true, "message": "分类树更新成功"}` | `{"success": false, "message": "分类树更新失败: 错误信息"}` |
| `deleteCategories(List<Long> ids)` | DELETE | `/categories/batch` | `ids`: 分类ID列表（JSON） | 批量删除分类 | `{"success": true, "message": "批量删除成功，共删除 N 个分类"}` | `{"success": false, "message": "ID为 X 的分类不存在"}` |
| `createCategories(List<Category> categories)` | POST | `/categories/batch` | `categories`: 分类列表（JSON） | 批量保存分类 | `{"success": true, "message": "批量创建成功，共创建 N 个分类", "data": [分类列表]}` | `{"success": false, "message": "分类名称 'X' 已存在"}` |
| `checkCategoryExists(Long id)` | GET | `/categories/{id}/exists` | `id`: 分类ID | 检查分类是否存在 | `{"success": true, "exists": true/false}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `searchCategories(String keyword, int page, int size)` | GET | `/categories/search` | `keyword`: 关键词<br>`page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 根据关键词搜索分类 | `{"success": true, "message": "搜索成功", "data": {分页数据}}` | `{"success": false, "message": "关键词不能为空"}` |
| `changeCategoryStatus(Long id, Integer status)` | PUT | `/categories/{id}/status` | `id`: 分类ID<br>`status`: 状态(0-禁用,1-启用) | 修改分类状态 | `{"success": true, "message": "分类状态更新成功", "data": {分类详情}}` | `{"success": false, "message": "状态值无效"}` |
| `getCategoryStatistics()` | GET | `/categories/statistics` | 无 | 获取分类统计信息 | `{"success": true, "data": {统计信息}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getCategoryStockSummary()` | GET | `/categories/stock-summary` | 无 | 分类库存汇总 | `{"success": true, "message": "分类库存汇总成功", "data": {汇总信息}}` | `{"success": false, "message": "操作失败: 错误信息"}` |

**返回体结构**：

| 字段名 | 类型 | 描述 |
|--------|------|------|
| success | boolean | 操作是否成功 |
| message | string | 操作结果消息 |
| data | object/array | 响应数据 |
| currentPage | number | 当前页码（分页接口） |
| totalItems | number | 总记录数（分页接口） |
| totalPages | number | 总页数（分页接口） |
| exists | boolean | 检查存在性接口的结果 |

**分类响应对象结构**：

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 分类ID |
| name | String | 分类名称 |
| parentId | Long | 父分类ID |
| level | Integer | 分类级别 |
| description | String | 分类描述 |
| sort | Integer | 排序值 |
| status | Integer | 状态：0-禁用，1-启用 |
| createTime | LocalDateTime | 创建时间 |

---

### 2. 类：MedicineController（药品控制器）

**位置**：`com.example.demo.controller`

**类注解说明**：
- `@RestController`：Spring注解，标识为REST风格的控制器
- `@RequestMapping("/medicines")`：设置请求路径前缀
- `@CrossOrigin(origins = "*")`：允许跨域请求

**方法列表**：

| 方法签名 | HTTP方法 | API路径 | 参数 | 描述 | 返回值说明 | 失败情况返回值 |
|----------|----------|---------|------|------|------------|----------------|
| `test()` | GET | `/medicines/test` | 无 | 测试接口 | `{"success": true, "message": "MedicineController is working!"}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getAllMedicines(int page, int size, String sortBy, String direction)` | GET | `/medicines` | `page`: 页码(默认0)<br>`size`: 每页大小(默认10)<br>`sortBy`: 排序字段(默认id)<br>`direction`: 排序方向(默认asc) | 获取所有药品（分页） | `{"success": true, "currentPage": 页码, "totalItems": 总记录数, "totalPages": 总页数, "data": [药品列表]}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getMedicineById(Long id)` | GET | `/medicines/{id}` | `id`: 药品ID | 根据ID获取药品 | `{"success": true, "data": {药品详情}}` | `{"success": false, "message": "药品不存在"}` |
| `createMedicine(Medicine medicine)` | POST | `/medicines` | `medicine`: 药品对象（JSON） | 创建新药品 | `{"success": true, "message": "药品创建成功", "data": {药品详情}}` | `{"success": false, "message": "药品编码已存在"}` |
| `updateMedicine(Long id, Medicine medicine)` | PUT | `/medicines/{id}` | `id`: 药品ID<br>`medicine`: 药品对象（JSON） | 更新药品信息 | `{"success": true, "message": "药品更新成功", "data": {药品详情}}` | `{"success": false, "message": "药品不存在"}` |
| `deleteMedicine(Long id)` | DELETE | `/medicines/{id}` | `id`: 药品ID | 删除药品 | `{"success": true, "message": "药品删除成功"}` | `{"success": false, "message": "药品存在采购订单/销售记录/库存记录"}` |
| `getMedicineByCode(String medicineCode)` | GET | `/medicines/code/{medicineCode}` | `medicineCode`: 药品编码 | 根据药品编码查找药品 | `{"success": true, "data": {药品详情}}` | `{"success": false, "message": "药品不存在"}` |
| `getMedicinesByStatus(Integer status, int page, int size)` | GET | `/medicines/status/{status}` | `status`: 状态(0-停用,1-启用)<br>`page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 根据状态分页查询药品 | `{"success": true, "currentPage": 页码, "totalItems": 总记录数, "totalPages": 总页数, "data": [药品列表]}` | `{"success": false, "message": "状态值无效"}` |
| `searchMedicines(String keyword, int page, int size)` | GET | `/medicines/search` | `keyword`: 关键词<br>`page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 搜索药品（名称、通用名、生产厂家） | `{"success": true, "message": "搜索成功", "data": {分页数据}}` | `{"success": false, "message": "关键词不能为空"}` |
| `getMedicinesByCategory(Long categoryId, int page, int size)` | GET | `/medicines/category/{categoryId}` | `categoryId`: 分类ID<br>`page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 根据分类ID查找药品 | `{"success": true, "data": {分页数据}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `updateMedicinePrice(Long id, BigDecimal retailPrice, BigDecimal purchasePrice)` | PUT | `/medicines/{id}/price` | `id`: 药品ID<br>`retailPrice`: 零售价（可选）<br>`purchasePrice`: 采购价（可选） | 更新药品价格 | `{"success": true, "message": "药品价格更新成功", "data": {药品详情}}` | `{"success": false, "message": "药品不存在或价格参数无效"}` |
| `checkMedicineCodeExists(String medicineCode)` | GET | `/medicines/check-code/{medicineCode}` | `medicineCode`: 药品编码 | 检查药品编码是否存在 | `{"success": true, "exists": true/false}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `deleteMedicines(List<Long> ids)` | DELETE | `/medicines/batch` | `ids`: 药品ID列表（JSON） | 批量删除药品 | `{"success": true, "message": "批量删除成功，共删除 N 个药品"}` | `{"success": false, "message": "ID为 X 的药品不存在"}` |
| `createMedicines(List<Medicine> medicines)` | POST | `/medicines/batch` | `medicines`: 药品列表（JSON） | 批量保存药品 | `{"success": true, "message": "批量创建成功，共创建 N 个药品", "data": [药品列表]}` | `{"success": false, "message": "药品编码 'X' 已存在"}` |
| `checkMedicineExists(Long id)` | GET | `/medicines/{id}/exists` | `id`: 药品ID | 检查药品是否存在 | `{"success": true, "exists": true/false}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getMedicineStatistics()` | GET | `/medicines/statistics` | 无 | 获取药品统计信息 | `{"success": true, "data": {统计信息}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `changeMedicineStatus(Long id, Integer status)` | PUT | `/medicines/{id}/status` | `id`: 药品ID<br>`status`: 状态(0-停用,1-启用) | 修改药品状态 | `{"success": true, "message": "药品状态更新成功", "data": {药品详情}}` | `{"success": false, "message": "药品不存在"}` |
| `getSimpleMedicineList(int page, int size)` | GET | `/medicines/simple` | `page`: 页码(默认0)<br>`size`: 每页大小(默认50) | 获取药品简要列表 | `{"success": true, "currentPage": 页码, "totalItems": 总记录数, "totalPages": 总页数, "data": [简要列表]}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getMedicinesBySymptom(Integer symptomId, int page, int size)` | GET | `/medicines/symptom/{symptomId}` | `symptomId`: 症状ID<br>`page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 根据症状查询药品 | `{"success": true, "message": "查询成功", "data": {分页数据}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getMedicinesStockStatus(List<Long> medicineIds)` | GET | `/medicines/stock-status` | `medicineIds`: 药品ID列表（请求参数） | 药品库存状态批量查询 | `{"success": true, "data": {药品ID: {totalStock, isLowStock}}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getExpiringMedicines(int daysThreshold, int page, int size)` | GET | `/medicines/expiry-warning` | `daysThreshold`: 阈值天数(默认30)<br>`page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 近效期药品查询 | `{"success": true, "data": {分页数据}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getMedicineStockValue()` | GET | `/medicines/stock-value` | 无 | 药品库存价值评估 | `{"success": true, "data": {统计信息}}` | `{"success": false, "message": "操作失败: 错误信息"}` |

**药品响应对象结构**：

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 药品ID |
| medicineCode | String | 药品编码 |
| name | String | 药品名称 |
| genericName | String | 通用名称 |
| category | object | 分类信息（id, name） |
| specification | String | 药品规格 |
| unit | String | 单位 |
| manufacturer | String | 生产厂家 |
| retailPrice | BigDecimal | 零售价 |
| purchasePrice | BigDecimal | 采购价 |
| status | Integer | 状态：0-停用，1-启用 |
| createTime | LocalDateTime | 创建时间 |
| symptoms | List | 症状列表 |

---

### 3. 类：PredictionResultController（预测结果控制器）

**位置**：`com.example.demo.controller`

**类注解说明**：
- `@RestController`：Spring注解，标识为REST风格的控制器
- `@RequestMapping("/api/predictions")`：设置请求路径前缀（注意：此处已包含`/api`，与其他控制器不同）
- `@CrossOrigin(origins = "*")`：允许跨域请求

**方法列表**：

| 方法签名 | HTTP方法 | API路径 | 参数 | 描述 | 返回值说明 | 失败情况返回值 |
|----------|----------|---------|------|------|------------|----------------|
| `test()` | GET | `/api/predictions/test` | 无 | 测试接口 | `{"success": true, "message": "PredictionResultController is working!"}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getAllPredictions(int page, int size, String sortBy, String direction)` | GET | `/api/predictions` | `page`: 页码(默认0)<br>`size`: 每页大小(默认10)<br>`sortBy`: 排序字段(默认predictionDate)<br>`direction`: 排序方向(默认desc) | 获取所有预测结果（分页） | `{"success": true, "currentPage": 页码, "totalItems": 总记录数, "totalPages": 总页数, "data": [预测列表]}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getPredictionById(Long id)` | GET | `/api/predictions/{id}` | `id`: 预测结果ID | 根据ID获取预测结果 | `{"success": true, "data": {预测详情}}` | `{"success": false, "message": "预测结果不存在"}` |
| `createPrediction(PredictionResult predictionResult)` | POST | `/api/predictions` | `predictionResult`: 预测对象（JSON） | 创建新预测结果 | `{"success": true, "message": "预测结果创建成功", "data": {预测详情}}` | `{"success": false, "message": "创建失败: 错误信息"}` |
| `updatePrediction(Long id, PredictionResult predictionResult)` | PUT | `/api/predictions/{id}` | `id`: 预测结果ID<br>`predictionResult`: 预测对象（JSON） | 更新预测结果 | `{"success": true, "message": "预测结果更新成功", "data": {预测详情}}` | `{"success": false, "message": "预测结果不存在"}` |
| `deletePrediction(Long id)` | DELETE | `/api/predictions/{id}` | `id`: 预测结果ID | 删除预测结果 | `{"success": true, "message": "预测结果删除成功"}` | `{"success": false, "message": "预测结果不存在"}` |
| `getPredictionsByMedicineId(Long medicineId, int page, int size)` | GET | `/api/predictions/medicine/{medicineId}` | `medicineId`: 药品ID<br>`page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 根据药品ID获取预测结果 | `{"success": true, "data": {分页数据}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getPredictionsByDate(LocalDate date, int page, int size)` | GET | `/api/predictions/date/{date}` | `date`: 预测日期<br>`page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 根据日期获取预测结果 | `{"success": true, "data": {分页数据}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getPredictionsByDateRange(LocalDate startDate, LocalDate endDate, int page, int size)` | GET | `/api/predictions/date/range` | `startDate`: 开始日期<br>`endDate`: 结束日期<br>`page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 根据日期范围获取预测结果 | `{"success": true, "data": {分页数据}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getNeedReprediction(Double threshold, int page, int size)` | GET | `/api/predictions/need-reprediction` | `threshold`: 准确率阈值(默认80)<br>`page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 获取需要重新预测的结果 | `{"success": true, "data": {分页数据}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getLatestPredictionByMedicineId(Long medicineId)` | GET | `/api/predictions/latest/{medicineId}` | `medicineId`: 药品ID | 获取药品的最新预测结果 | `{"success": true, "data": {预测详情}}` | `{"success": false, "message": "该药品暂无预测结果"}` |
| `generatePrediction(Long medicineId, String modelType, LocalDate predictionDate, int predictionDays)` | POST | `/api/predictions/generate/{medicineId}` | `medicineId`: 药品ID<br>`modelType`: 模型类型(可选)<br>`predictionDate`: 预测日期(可选)<br>`predictionDays`: 预测天数(默认30) | 生成预测结果 | `{"success": true, "message": "预测生成成功", "data": {预测详情}}` | `{"success": false, "message": "预测生成失败: 错误信息"}` |
| `generateBatchPredictions(List<Long> medicineIds, String modelType, LocalDate startDate, LocalDate endDate, int predictionDays)` | POST | `/api/predictions/generate/batch` | `medicineIds`: 药品ID列表(JSON)<br>`modelType`: 模型类型(可选)<br>`startDate`: 开始日期(可选)<br>`endDate`: 结束日期(可选)<br>`predictionDays`: 预测天数(默认30) | 批量生成预测结果 | `{"success": true, "message": "批量预测生成成功，共生成 N 个预测结果", "data": [预测列表]}` | `{"success": false, "message": "批量预测生成失败: 错误信息"}` |
| `regeneratePrediction(Long id, String modelType)` | POST | `/api/predictions/regenerate/{id}` | `id`: 预测结果ID<br>`modelType`: 模型类型(可选) | 重新生成预测结果 | `{"success": true, "message": "预测重新生成成功", "data": {预测详情}}` | `{"success": false, "message": "预测重新生成失败: 错误信息"}` |
| `getAccuracyStatistics()` | GET | `/api/predictions/statistics/accuracy` | 无 | 获取预测准确率统计 | `{"success": true, "data": {模型平均准确率}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getRecommendedOrderQuantities(LocalDate targetDate, List<Long> medicineIds)` | GET | `/api/predictions/recommendations` | `targetDate`: 目标日期(可选)<br>`medicineIds`: 药品ID列表(可选) | 获取推荐订单数量 | `{"success": true, "data": {药品ID: 推荐数量}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getRecommendedQuantityForMedicine(Long medicineId, LocalDate targetDate)` | GET | `/api/predictions/recommendations/{medicineId}` | `medicineId`: 药品ID<br>`targetDate`: 目标日期(可选) | 获取特定药品的推荐订单数量 | `{"success": true, "data": 推荐数量}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `checkHealth()` | GET | `/api/predictions/health` | 无 | 检查健康状态 | `{"success": true, "message": "Prediction service is healthy", "modelServiceHealthy": true/false}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `checkModelServiceHealth()` | GET | `/api/predictions/model-service/health` | 无 | 检查模型服务健康状态 | `{"success": true, "healthy": true/false, "info": {服务信息}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `deletePredictions(List<Long> ids)` | DELETE | `/api/predictions/batch` | `ids`: 预测结果ID列表(JSON) | 批量删除预测结果 | `{"success": true, "message": "批量删除成功，共删除 N 个预测结果"}` | `{"success": false, "message": "ID为 X 的预测结果不存在"}` |
| `createPredictions(List<PredictionResult> predictionResults)` | POST | `/api/predictions/batch` | `predictionResults`: 预测结果列表(JSON) | 批量创建预测结果 | `{"success": true, "message": "批量创建成功，共创建 N 个预测结果", "data": [预测列表]}` | `{"success": false, "message": "批量创建失败: 错误信息"}` |
| `pushSalesData(List<Map<String, Object>> salesData)` | POST | `/api/predictions/push/sales` | `salesData`: 销售数据列表(JSON) | 推送销售数据到模型 | `{"success": true, "message": "销售数据推送成功", "data": {结果}}` | `{"success": false, "message": "数据推送失败: 错误信息"}` |
| `pushMedicineData(List<Map<String, Object>> medicineData)` | POST | `/api/predictions/push/medicine` | `medicineData`: 药品数据列表(JSON) | 推送药品数据到模型 | `{"success": true, "message": "药品数据推送成功", "data": {结果}}` | `{"success": false, "message": "数据推送失败: 错误信息"}` |
| `pushStockData(List<Map<String, Object>> stockData)` | POST | `/api/predictions/push/stock` | `stockData`: 库存数据列表(JSON) | 推送库存数据到模型 | `{"success": true, "message": "库存数据推送成功", "data": {结果}}` | `{"success": false, "message": "数据推送失败: 错误信息"}` |
| `calculateABCClassification(List<Long> medicineIds)` | GET | `/api/predictions/analysis/abc` | `medicineIds`: 药品ID列表(可选) | 获取ABC分类 | `{"success": true, "data": {分类结果}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `detectSlowMovingItems(int thresholdDays)` | GET | `/api/predictions/analysis/slow-moving` | `thresholdDays`: 阈值天数(默认90) | 检测滞销品 | `{"success": true, "data": {检测结果}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `calculateExpiryRisk()` | GET | `/api/predictions/analysis/expiry-risk` | 无 | 计算过期风险 | `{"success": true, "data": {风险结果}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getInventoryStatusSummary()` | GET | `/api/predictions/inventory/summary` | 无 | 获取库存状态摘要 | `{"success": true, "data": {摘要信息}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `calculateDynamicSafetyStock(Long medicineId, int predictionDays)` | GET | `/api/predictions/inventory/safety-stock/dynamic` | `medicineId`: 药品ID<br>`predictionDays`: 预测天数(默认30) | 计算动态安全库存 | `{"success": true, "data": {计算结果}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `generateReplenishmentSuggestions(List<Long> medicineIds)` | GET | `/api/predictions/inventory/replenishment` | `medicineIds`: 药品ID列表(可选) | 生成补货建议 | `{"success": true, "data": {建议结果}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getCurrentInventoryStatus()` | GET | `/api/predictions/inventory/current` | 无 | 获取当前库存状态 | `{"success": true, "data": {状态信息}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getInventoryWithExpiry()` | GET | `/api/predictions/inventory/expiry` | 无 | 获取带过期信息的库存 | `{"success": true, "data": {库存信息}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getMedicineInventoryStatus(Long medicineId)` | GET | `/api/predictions/inventory/medicine/{medicineId}` | `medicineId`: 药品ID | 获取药品库存状态 | `{"success": true, "data": {状态信息}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `calculateSafetyStock(Long medicineId, int leadTimeDays, double serviceLevel)` | GET | `/api/predictions/inventory/safety-stock` | `medicineId`: 药品ID<br>`leadTimeDays`: 采购提前期(默认14)<br>`serviceLevel`: 服务水平(默认0.95) | 计算安全库存 | `{"success": true, "data": {计算结果}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `calculateBatchSafetyStock(int leadTimeDays, double serviceLevel)` | GET | `/api/predictions/inventory/safety-stock/batch` | `leadTimeDays`: 采购提前期(默认14)<br>`serviceLevel`: 服务水平(默认0.95) | 批量计算安全库存 | `{"success": true, "data": {计算结果}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getDataSummary()` | GET | `/api/predictions/data/summary` | 无 | 获取数据摘要 | `{"success": true, "data": {摘要信息}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getModelPerformance(Long medicineId, int testPeriods)` | GET | `/api/predictions/model/performance` | `medicineId`: 药品ID<br>`testPeriods`: 测试周期数(默认3) | 获取模型性能 | `{"success": true, "data": {性能指标}}` | `{"success": false, "message": "操作失败: 错误信息"}` |

**预测结果响应对象结构**：

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 预测ID |
| medicine | object | 药品简要信息（id, medicineCode, name, specification, unit） |
| predictionDate | LocalDate | 预测日期 |
| predictedQuantity | Integer | 预测需求量 |
| confidenceIntervalLower | Integer | 置信区间下限 |
| confidenceIntervalUpper | Integer | 置信区间上限 |
| modelType | String | 模型类型 |
| accuracyRate | BigDecimal | 准确率 |
| recommendedOrderQuantity | Integer | 建议订购数量 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

---

### 4. 类：PurchaseOrderController（采购订单控制器）

**位置**：`com.example.demo.controller`

**类注解说明**：
- `@RestController`：Spring注解，标识为REST风格的控制器
- `@RequestMapping("/purchase-orders")`：设置请求路径前缀
- `@CrossOrigin(origins = "*")`：允许跨域请求

**方法列表**：

| 方法签名 | HTTP方法 | API路径 | 参数 | 描述 | 返回值说明 | 失败情况返回值 |
|----------|----------|---------|------|------|------------|----------------|
| `test()` | GET | `/purchase-orders/test` | 无 | 测试接口 | `{"success": true, "message": "PurchaseOrderController is working!"}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getAllPurchaseOrders(int page, int size, String sortBy, String direction)` | GET | `/purchase-orders` | `page`: 页码(默认0)<br>`size`: 每页大小(默认10)<br>`sortBy`: 排序字段(默认id)<br>`direction`: 排序方向(默认desc) | 获取所有采购订单（分页） | `{"success": true, "currentPage": 页码, "totalItems": 总记录数, "totalPages": 总页数, "data": [订单列表]}` | `{"success": false, "message": "获取采购订单失败: 错误信息"}` |
| `getPurchaseOrderById(Long id)` | GET | `/purchase-orders/{id}` | `id`: 订单ID | 根据ID获取采购订单 | `{"success": true, "data": {订单详情}}` | `{"success": false, "message": "采购订单不存在"}` |
| `getPurchaseOrderByOrderNo(String orderNo)` | GET | `/purchase-orders/orderNo/{orderNo}` | `orderNo`: 订单号 | 根据订单号获取采购订单 | `{"success": true, "data": {订单详情}}` | `{"success": false, "message": "采购订单不存在"}` |
| `createPurchaseOrder(PurchaseOrder order, Long operatorId)` | POST | `/purchase-orders` | `order`: 订单对象(JSON)<br>`operatorId`: 操作员ID(请求参数) | 创建新采购订单 | `{"success": true, "message": "采购订单创建成功", "data": {订单详情}}` | `{"success": false, "message": "订单号已存在"}` |
| `updatePurchaseOrder(Long id, PurchaseOrder order)` | PUT | `/purchase-orders/{id}` | `id`: 订单ID<br>`order`: 订单对象(JSON) | 更新采购订单信息 | `{"success": true, "message": "采购订单更新成功", "data": {订单详情}}` | `{"success": false, "message": "采购订单不存在"}` |
| `deletePurchaseOrder(Long id)` | DELETE | `/purchase-orders/{id}` | `id`: 订单ID | 删除采购订单 | `{"success": true, "message": "采购订单删除成功"}` | `{"success": false, "message": "已到货的订单不能删除"}` |
| `getPurchaseOrdersByStatus(Integer status, int page, int size)` | GET | `/purchase-orders/status/{status}` | `status`: 订单状态(0-3)<br>`page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 根据订单状态分页查询采购订单 | `{"success": true, "data": {分页数据}}` | `{"success": false, "message": "订单状态值无效"}` |
| `getPurchaseOrdersByMedicineId(Long medicineId, int page, int size)` | GET | `/purchase-orders/medicine/{medicineId}` | `medicineId`: 药品ID<br>`page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 根据药品ID分页查询采购订单 | `{"success": true, "data": {分页数据}}` | `{"success": false, "message": "获取采购订单失败: 错误信息"}` |
| `getPendingOrders(int page, int size)` | GET | `/purchase-orders/pending` | `page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 获取待处理的采购订单 | `{"success": true, "data": {分页数据}}` | `{"success": false, "message": "获取待处理订单失败: 错误信息"}` |
| `getOverdueOrders(int page, int size)` | GET | `/purchase-orders/overdue` | `page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 获取过期的采购订单 | `{"success": true, "data": {分页数据}}` | `{"success": false, "message": "获取过期订单失败: 错误信息"}` |
| `getTotalPurchasedQuantity(Long medicineId)` | GET | `/purchase-orders/medicine/{medicineId}/total-purchased` | `medicineId`: 药品ID | 获取某个药品的已到货采购总量 | `{"success": true, "data": {medicineId, totalPurchasedQuantity}}` | `{"success": false, "message": "获取采购总量失败: 错误信息"}` |
| `getTotalPurchaseAmountByPeriod(LocalDateTime startTime, LocalDateTime endTime)` | GET | `/purchase-orders/total-amount` | `startTime`: 开始时间<br>`endTime`: 结束时间 | 获取时间段内的已到货采购总额 | `{"success": true, "data": {startTime, endTime, totalAmount}}` | `{"success": false, "message": "获取采购总额失败: 错误信息"}` |
| `confirmOrder(Long id)` | PUT | `/purchase-orders/{id}/confirm` | `id`: 订单ID | 确认采购订单 | `{"success": true, "message": "采购订单确认成功", "data": {订单详情}}` | `{"success": false, "message": "确认失败: 错误信息"}` |
| `markAsArrived(Long id)` | PUT | `/purchase-orders/{id}/arrive` | `id`: 订单ID | 标记采购订单为已到货 | `{"success": true, "message": "采购订单已标记为已到货", "data": {订单详情}}` | `{"success": false, "message": "标记失败: 错误信息"}` |
| `cancelOrder(Long id)` | PUT | `/purchase-orders/{id}/cancel` | `id`: 订单ID | 取消采购订单 | `{"success": true, "message": "采购订单已取消", "data": {订单详情}}` | `{"success": false, "message": "取消失败: 错误信息"}` |
| `getOrderStatistics()` | GET | `/purchase-orders/statistics` | 无 | 获取采购订单统计信息 | `{"success": true, "data": {统计信息}}` | 无 |
| `deletePurchaseOrders(List<Long> ids)` | DELETE | `/purchase-orders/batch` | `ids`: 订单ID列表(JSON) | 批量删除采购订单 | `{"success": true, "message": "批量删除成功，共删除 N 个采购订单"}` | `{"success": false, "message": "ID为 X 的采购订单不存在"}` |
| `createPurchaseOrders(List<PurchaseOrder> orders, Long operatorId)` | POST | `/purchase-orders/batch` | `orders`: 订单列表(JSON)<br>`operatorId`: 操作员ID(请求参数) | 批量保存采购订单 | `{"success": true, "message": "批量创建成功，共创建 N 个采购订单", "data": [订单列表]}` | `{"success": false, "message": "订单号 'X' 已存在"}` |
| `searchPurchaseOrders(String keyword, int page, int size)` | GET | `/purchase-orders/search` | `keyword`: 关键词<br>`page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 搜索采购订单（供应商、订单号等） | `{"success": true, "message": "已找到符合条件的采购订单", "data": {分页数据}}` | `{"success": false, "message": "关键词不能为空"}` |
| `getPurchaseOrdersByTimeRange(LocalDateTime startTime, LocalDateTime endTime, int page, int size)` | GET | `/purchase-orders/time-range` | `startTime`: 开始时间<br>`endTime`: 结束时间<br>`page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 根据时间段查询采购订单 | `{"success": true, "message": "已找到符合条件的采购订单", "data": {分页数据}}` | `{"success": false, "message": "开始时间不能晚于结束时间"}` |

**采购订单响应对象结构**：

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 订单ID |
| orderNo | String | 订单号 |
| medicine | object | 药品简要信息（id, medicineCode, name, specification, unit） |
| quantity | Integer | 采购数量 |
| unitPrice | BigDecimal | 采购单价 |
| totalAmount | BigDecimal | 总金额 |
| supplier | String | 供应商 |
| orderStatus | Integer | 订单状态：0-待处理，1-已确认，2-已到货，3-已取消 |
| orderStatusText | String | 订单状态文本描述 |
| orderTime | LocalDateTime | 下单时间 |
| expectedArrival | LocalDate | 预计到货日期 |
| actualArrival | LocalDateTime | 实际到货时间 |
| operator | object | 操作员简要信息（id, username, realName） |
| remark | String | 备注 |
| isOverdue | boolean | 是否过期 |

---

### 5. 类：SaleRecordController（销售记录控制器）

**位置**：`com.example.demo.controller`

**类注解说明**：
- `@RestController`：Spring注解，标识为REST风格的控制器
- `@RequestMapping("/sale-records")`：设置请求路径前缀
- `@CrossOrigin(origins = "*")`：允许跨域请求

**方法列表**：

| 方法签名 | HTTP方法 | API路径 | 参数 | 描述 | 返回值说明 | 失败情况返回值 |
|----------|----------|---------|------|------|------------|----------------|
| `test()` | GET | `/sale-records/test` | 无 | 测试接口 | `"SaleRecordController is working!"` | `"操作失败: 错误信息"` |
| `getAllSaleRecords(int page, int size, String sortBy, String direction)` | GET | `/sale-records` | `page`: 页码(默认0)<br>`size`: 每页大小(默认10)<br>`sortBy`: 排序字段(默认saleTime)<br>`direction`: 排序方向(默认desc) | 获取所有销售记录（分页） | `{"success": true, "currentPage": 页码, "totalItems": 总记录数, "totalPages": 总页数, "data": [销售记录列表]}` | `{"success": false, "message": "获取销售记录失败: 错误信息"}` |
| `getSaleRecordById(Long id)` | GET | `/sale-records/{id}` | `id`: 销售记录ID | 根据ID获取销售记录 | `{"success": true, "data": {销售记录详情}}` | `{"success": false, "message": "销售记录不存在"}` |
| `getSaleRecordByRecordNo(String recordNo)` | GET | `/sale-records/record-no/{recordNo}` | `recordNo`: 销售单号 | 根据销售单号获取销售记录 | `{"success": true, "data": {销售记录详情}}` | `{"success": false, "message": "销售记录不存在"}` |
| `createSaleRecord(SaleRecord saleRecord)` | POST | `/sale-records` | `saleRecord`: 销售记录对象(JSON) | 创建销售记录 | `{"success": true, "message": "销售记录创建成功", "data": {销售记录详情}}` | `{"success": false, "message": "创建销售记录失败: 错误信息"}` |
| `updateSaleRecord(Long id, SaleRecord saleRecord)` | PUT | `/sale-records/{id}` | `id`: 销售记录ID<br>`saleRecord`: 销售记录对象(JSON) | 更新销售记录 | `{"success": true, "message": "销售记录更新成功", "data": {销售记录详情}}` | `{"success": false, "message": "销售记录不存在"}` |
| `deleteSaleRecord(Long id)` | DELETE | `/sale-records/{id}` | `id`: 销售记录ID | 删除销售记录 | `{"success": true, "message": "销售记录删除成功"}` | `{"success": false, "message": "销售记录不存在"}` |
| `getSaleRecordsByTimeRange(LocalDateTime startTime, LocalDateTime endTime, int page, int size)` | GET | `/sale-records/time-range` | `startTime`: 开始时间<br>`endTime`: 结束时间<br>`page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 根据时间段查询销售记录 | `{"success": true, "currentPage": 页码, "totalItems": 总记录数, "totalPages": 总页数, "data": [销售记录列表]}` | `{"success": false, "message": "获取销售记录失败: 错误信息"}` |
| `getSaleRecordsByMedicineId(Long medicineId, int page, int size)` | GET | `/sale-records/medicine/{medicineId}` | `medicineId`: 药品ID<br>`page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 根据药品ID查询销售记录 | `{"success": true, "currentPage": 页码, "totalItems": 总记录数, "totalPages": 总页数, "data": [销售记录列表]}` | `{"success": false, "message": "获取销售记录失败: 错误信息"}` |
| `getSaleRecordsByOperatorId(Long operatorId, int page, int size)` | GET | `/sale-records/operator/{operatorId}` | `operatorId`: 操作员ID<br>`page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 根据操作员ID查询销售记录 | `{"success": true, "currentPage": 页码, "totalItems": 总记录数, "totalPages": 总页数, "data": [销售记录列表]}` | `{"success": false, "message": "获取销售记录失败: 错误信息"}` |
| `getSaleStatistics(LocalDateTime startTime, LocalDateTime endTime)` | GET | `/sale-records/statistics` | `startTime`: 开始时间(可选)<br>`endTime`: 结束时间(可选) | 获取销售统计信息 | `{"success": true, "data": {统计信息}}` | `{"success": false, "message": "获取销售统计信息失败: 错误信息"}` |
| `getTopSellingMedicines(int limit, LocalDateTime startDate, LocalDateTime endDate)` | GET | `/sale-records/top-selling` | `limit`: 限制数量(默认10)<br>`startDate`: 开始日期(可选)<br>`endDate`: 结束日期(可选) | 获取最畅销药品 | `{"success": true, "period": {start, end}, "data": [畅销药品列表]}` | `{"success": false, "message": "获取畅销药品失败: 错误信息"}` |
| `getTotalQuantityByMedicineId(Long medicineId)` | GET | `/sale-records/medicine/{medicineId}/total-quantity` | `medicineId`: 药品ID | 获取某个药品的销售总量 | `{"success": true, "medicineId": 药品ID, "totalQuantity": 销售总量}` | `{"success": false, "message": "获取药品销售总量失败: 错误信息"}` |
| `getSalesBySymptom(Integer symptomId, int page, int size)` | GET | `/sale-records/symptom/{symptomId}` | `symptomId`: 症状ID<br>`page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 按症状查询销售记录 | `{"success": true, "message": "查询成功", "data": {分页数据}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `processSaleRecord(Long id)` | PUT | `/sale-records/{id}/process` | `id`: 销售记录ID | 处理销售并更新库存 | `{"success": true, "message": "销售处理成功，库存已更新", "data": {处理信息}}` | `{"success": false, "message": "库存不足"}` |
| `createSalesIssuance(Map<String, Object> issuanceData)` | POST | `/sale-records/issuances`（已废弃） | `issuanceData`: 出库单数据(JSON) | 出库单创建（暂未实现） | `{"success": false, "message": "出库单功能暂未实现"}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getSalesIssuances(int page, int size)` | GET | `/sale-records/issuances`（已废弃） | `page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 出库单查询（暂未实现） | `{"success": false, "message": "出库单功能暂未实现"}` | `{"success": false, "message": "操作失败: 错误信息"}` |

**销售记录响应对象结构**：

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 销售记录ID |
| recordNo | String | 销售单号 |
| medicine | object | 药品简要信息（id, medicineCode, name, specification, unit） |
| quantity | Integer | 销售数量 |
| unitPrice | BigDecimal | 销售单价 |
| totalAmount | BigDecimal | 总金额 |
| customerInfo | String | 顾客信息 |
| customerType | Integer | 顾客类型 |
| isRx | boolean | 是否为处方药销售 |
| saleTime | LocalDateTime | 销售时间 |
| symptoms | array | 症状信息列表（id, name） |
| operator | object | 操作员简要信息（id, username, realName） |
| remark | String | 备注 |

---

### 6. 类：StockController（库存控制器）

**位置**：`com.example.demo.controller`

**类注解说明**：
- `@RestController`：Spring注解，标识为REST风格的控制器
- `@RequestMapping("/stocks")`：设置请求路径前缀
- `@CrossOrigin(origins = "*")`：允许跨域请求

**方法列表**：

| 方法签名 | HTTP方法 | API路径 | 参数 | 描述 | 返回值说明 | 失败情况返回值 |
|----------|----------|---------|------|------|------------|----------------|
| `test()` | GET | `/stocks/test` | 无 | 测试接口 | `{"success": true, "message": "StockController is working!"}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getAllStocks(int page, int size, String sortBy, String direction)` | GET | `/stocks` | `page`: 页码(默认0)<br>`size`: 每页大小(默认10)<br>`sortBy`: 排序字段(默认id)<br>`direction`: 排序方向(默认asc) | 获取所有库存（分页） | `{"success": true, "currentPage": 页码, "totalItems": 总记录数, "totalPages": 总页数, "data": [库存列表]}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getStockById(Long id)` | GET | `/stocks/{id}` | `id`: 库存记录ID | 根据ID获取库存 | `{"success": true, "data": {库存详情}}` | `{"success": false, "message": "库存记录不存在"}` |
| `createStock(Stock stock)` | POST | `/stocks` | `stock`: 库存对象(JSON) | 创建新库存 | `{"success": true, "message": "库存创建成功", "data": {库存详情}}` | `{"success": false, "message": "创建失败: 错误信息"}` |
| `updateStock(Long id, Stock stock)` | PUT | `/stocks/{id}` | `id`: 库存记录ID<br>`stock`: 库存对象(JSON) | 更新库存信息 | `{"success": true, "message": "库存更新成功", "data": {库存详情}}` | `{"success": false, "message": "库存记录不存在"}` |
| `deleteStock(Long id)` | DELETE | `/stocks/{id}` | `id`: 库存记录ID | 删除库存 | `{"success": true, "message": "库存删除成功"}` | `{"success": false, "message": "库存记录不存在"}` |
| `getStocksByMedicine(Long medicineId, int page, int size)` | GET | `/stocks/medicine/{medicineId}` | `medicineId`: 药品ID<br>`page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 根据药品ID查找库存 | `{"success": true, "data": {分页数据}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getTotalStockByMedicine(Long medicineId)` | GET | `/stocks/medicine/{medicineId}/total` | `medicineId`: 药品ID | 获取某个药品的总库存量 | `{"success": true, "data": {medicineId, totalStock}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getExpiringStock(LocalDate startDate, LocalDate endDate, int page, int size)` | GET | `/stocks/expiring` | `startDate`: 开始日期(默认今天)<br>`endDate`: 结束日期(默认30天后)<br>`page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 获取即将过期的库存 | `{"success": true, "message": "查询成功", "data": {分页数据}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getLowStock(int page, int size)` | GET | `/stocks/low-stock` | `page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 获取库存不足的药品 | `{"success": true, "message": "查询成功", "data": {分页数据}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getLowStockSummary()` | GET | `/stocks/low-stock-summary` | 无 | 获取库存不足药品的汇总信息 | `{"success": true, "data": {药品ID: 库存量}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `reduceStock(Long medicineId, Integer quantity)` | PUT | `/stocks/reduce` | `medicineId`: 药品ID<br>`quantity`: 减少数量 | 减少库存（销售出库） | `{"success": true, "message": "库存减少成功", "data": {medicineId, reducedQuantity, remainingStock}}` | `{"success": false, "message": "库存不足"}` |
| `increaseStock(Long medicineId, Integer quantity, String batchNumber, LocalDate expirationDate)` | PUT | `/stocks/increase` | `medicineId`: 药品ID<br>`quantity`: 增加数量<br>`batchNumber`: 批号(可选)<br>`expirationDate`: 有效期(可选) | 增加库存（采购入库） | `{"success": true, "message": "库存增加成功", "data": {medicineId, addedQuantity, batchNumber, expirationDate, totalStock}}` | `{"success": false, "message": "库存增加失败: 错误信息"}` |
| `checkStockAvailability(Long medicineId, Integer requiredQuantity)` | GET | `/stocks/check-availability` | `medicineId`: 药品ID<br>`requiredQuantity`: 需求数量 | 检查库存是否足够 | `{"success": true, "data": {medicineId, requiredQuantity, currentStock, isAvailable, message}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getStockByBatchNumber(String batchNumber, int page, int size)` | GET | `/stocks/batch/{batchNumber}` | `batchNumber`: 批号<br>`page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 根据批号查找库存 | `{"success": true, "message": "查找成功", "data": {分页数据}}` | `{"success": false, "message": "批号不能为空"}` |
| `getExpiredStock(int page, int size)` | GET | `/stocks/expired` | `page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 获取过期库存 | `{"success": true, "message": "查询成功", "data": {分页数据}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `deleteStocks(List<Long> ids)` | DELETE | `/stocks/batch` | `ids`: 库存记录ID列表(JSON) | 批量删除库存 | `{"success": true, "message": "批量删除成功，共删除 N 个库存记录"}` | `{"success": false, "message": "ID为 X 的库存记录不存在"}` |
| `createStocks(List<Stock> stocks)` | POST | `/stocks/batch` | `stocks`: 库存列表(JSON) | 批量保存库存 | `{"success": true, "message": "批量创建成功，共创建 N 个库存记录", "data": [库存列表]}` | `{"success": false, "message": "批量保存失败: 错误信息"}` |
| `getStockStatistics()` | GET | `/stocks/statistics` | 无 | 获取库存统计信息 | `{"success": true, "message": "库存统计信息获取成功", "data": {统计信息}}` | `{"success": false, "message": "统计信息获取失败: 错误信息"}` |
| `initiateInventory()` | POST | `/stocks/inventory/initiate`（已废弃） | 无 | 库存盘点启动（暂未实现） | `{"success": true, "message": "库存盘点启动成功", "data": {盘点信息}}`（仅作示例） | `{"success": false, "message": "操作失败: 错误信息"}` |
| `recordInventory(Long inventoryId, Long stockId, Integer actualQuantity)` | POST | `/stocks/inventory/record`（已废弃） | `inventoryId`: 盘点ID<br>`stockId`: 库存ID<br>`actualQuantity`: 实际数量 | 盘点结果记录（暂未实现） | `{"success": true, "message": "盘点结果记录成功", "data": {记录信息}}`（仅作示例） | `{"success": false, "message": "库存记录不存在"}` |
| `getInventoryHistory(int page, int size)` | GET | `/stocks/inventory/history`（已废弃） | `page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 盘点历史查询（暂未实现） | `{"success": true, "message": "查询成功", "data": {分页数据}}`（仅作示例） | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getStockTransactions(int page, int size)` | GET | `/stocks/transactions` | `page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 库存交易记录 | `{"success": true, "message": "查询成功", "data": {分页数据}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getStockValuation()` | GET | `/stocks/valuation` | 无 | 库存价值评估 | `{"success": true, "message": "库存价值评估成功", "data": {valuation}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getStocksByLocation(String location, int page, int size)` | GET | `/stocks/location/{location}` | `location`: 库位<br>`page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 按库位查询库存 | `{"success": true, "data": {分页数据}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getStockTurnover(String period)` | GET | `/stocks/turnover` | `period`: 周期(默认month) | 库存周转率分析 | `{"success": true, "message": "库存周转率分析成功", "data": {period, turnoverRate}}` | `{"success": false, "message": "操作失败: 错误信息"}` |

**库存响应对象结构**：

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 库存记录ID |
| medicine | object | 药品简要信息（id, medicineCode, name, specification, unit） |
| batchNumber | String | 批号 |
| productionDate | LocalDate | 生产日期 |
| expirationDate | LocalDate | 有效期至 |
| quantity | Integer | 当前数量 |
| warningQuantity | Integer | 库存预警数量 |
| shelfLocation | String | 货架位置 |
| status | Integer | 状态：0-过期，1-正常 |
| minimumOrderQuantity | Integer | 最小订购数量 |
| leadTimeDays | Integer | 采购提前期（天） |
| reorderPoint | Integer | 再订货点 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |
| isExpired | boolean | 是否过期 |
| needsWarning | boolean | 是否需要预警 |

---

### 7. 类：SymptomController（症状控制器）

**位置**：`com.example.demo.controller`

**类注解说明**：
- `@RestController`：Spring注解，标识为REST风格的控制器
- `@RequestMapping("/symptoms")`：设置请求路径前缀
- `@CrossOrigin(origins = "*")`：允许跨域请求

**方法列表**：

| 方法签名 | HTTP方法 | API路径 | 参数 | 描述 | 返回值说明 | 失败情况返回值 |
|----------|----------|---------|------|------|------------|----------------|
| `test()` | GET | `/symptoms/test` | 无 | 测试接口 | `{"success": true, "message": "SymptomController is working!"}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getAllSymptoms(int page, int size, String sortBy, String direction)` | GET | `/symptoms` | `page`: 页码(默认0)<br>`size`: 每页大小(默认10)<br>`sortBy`: 排序字段(默认id)<br>`direction`: 排序方向(默认asc) | 获取所有症状 | `{"success": true, "currentPage": 页码, "totalItems": 总记录数, "totalPages": 总页数, "data": [症状列表]}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getSymptomById(Integer id)` | GET | `/symptoms/{id}` | `id`: 症状ID | 根据ID获取症状 | `{"success": true, "data": {症状详情}}` | `{"success": false, "message": "症状不存在"}` |
| `createSymptom(Symptom symptom)` | POST | `/symptoms` | `symptom`: 症状对象(JSON) | 创建新症状 | `{"success": true, "message": "症状创建成功", "data": {症状详情}}` | `{"success": false, "message": "症状名称已存在"}` |
| `updateSymptom(Integer id, Symptom symptom)` | PUT | `/symptoms/{id}` | `id`: 症状ID<br>`symptom`: 症状对象(JSON) | 更新症状信息 | `{"success": true, "message": "症状更新成功", "data": {症状详情}}` | `{"success": false, "message": "症状不存在"}` |
| `deleteSymptom(Integer id)` | DELETE | `/symptoms/{id}` | `id`: 症状ID | 删除症状 | `{"success": true, "message": "症状删除成功"}` | `{"success": false, "message": "症状不存在"}` |
| `getMedicinesBySymptom(Integer symptomId, int page, int size)` | GET | `/symptoms/medicines/{symptomId}` | `symptomId`: 症状ID<br>`page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 获取症状关联的药品列表 | `{"success": true, "message": "查询成功", "data": {分页数据}}` | `{"success": false, "message": "未找到关联的药品"}` |
| `getSalesBySymptom(Integer symptomId, int page, int size)` | GET | `/symptoms/sales/{symptomId}` | `symptomId`: 症状ID<br>`page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 获取症状相关的销售记录 | `{"success": true, "message": "查询成功", "data": {分页数据}}` | `{"success": false, "message": "未找到关联的销售记录"}` |
| `getSymptomByName(String name)` | GET | `/symptoms/name/{name}` | `name`: 症状名称 | 根据症状名称精确查找 | `{"success": true, "data": {症状详情}}` | `{"success": false, "message": "症状不存在"}` |
| `searchSymptomsByName(String keyword)` | GET | `/symptoms/search/name` | `keyword`: 关键词 | 根据症状名称模糊查询 | `{"success": true, "data": [症状列表]}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `searchSymptomsByDescription(String keyword)` | GET | `/symptoms/search/description` | `keyword`: 关键词 | 根据描述模糊查询症状 | `{"success": true, "data": [症状列表]}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `searchSymptoms(String keyword, int page, int size)` | GET | `/symptoms/search` | `keyword`: 关键词<br>`page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 根据关键词搜索症状（名称或描述） | `{"success": true, "message": "搜索成功", "data": {分页数据}}` | `{"success": false, "message": "关键词不能为空"}` |
| `checkSymptomNameExists(String name)` | GET | `/symptoms/check-name/{name}` | `name`: 症状名称 | 检查症状名称是否存在 | `{"success": true, "exists": true/false}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `deleteSymptoms(List<Integer> ids)` | DELETE | `/symptoms/batch` | `ids`: 症状ID列表(JSON) | 批量删除症状 | `{"success": true, "message": "批量删除成功，共删除 N 个症状"}` | `{"success": false, "message": "ID为 X 的症状不存在"}` |
| `createSymptoms(List<Symptom> symptoms)` | POST | `/symptoms/batch` | `symptoms`: 症状列表(JSON) | 批量保存症状 | `{"success": true, "message": "批量创建成功，共创建 N 个症状", "data": [症状列表]}` | `{"success": false, "message": "症状名称 'X' 已存在"}` |
| `checkSymptomExists(Integer id)` | GET | `/symptoms/{id}/exists` | `id`: 症状ID | 检查症状是否存在 | `{"success": true, "exists": true/false}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getSymptomStatistics()` | GET | `/symptoms/statistics` | 无 | 获取症状统计信息 | `{"success": true, "data": {统计信息}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getSimpleSymptomList()` | GET | `/symptoms/simple` | 无 | 获取症状简要列表（仅包含ID和名称） | `{"success": true, "data": [症状简要列表]}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getSymptomsByIds(List<Integer> ids)` | POST | `/symptoms/by-ids` | `ids`: 症状ID列表(JSON) | 根据ID列表获取症状 | `{"success": true, "data": [症状列表]}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `autocompleteSymptoms(String query, int limit)` | GET | `/symptoms/autocomplete` | `query`: 查询关键词<br>`limit`: 限制数量(默认10) | 快速搜索症状（用于自动完成） | `{"success": true, "data": [症状列表]}` | `{"success": false, "message": "操作失败: 错误信息"}` |

**症状响应对象结构**：

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | Integer | 症状ID |
| name | String | 症状名称 |
| description | String | 症状描述 |

---

### 8. 类：TestController（测试控制器）

**位置**：`com.example.demo.controller`

**类注解说明**：
- `@RestController`：Spring注解，标识为REST风格的控制器
- `@RequestMapping("/test")`：设置请求路径前缀

**方法列表**：

| 方法签名 | HTTP方法 | API路径 | 参数 | 描述 | 返回值说明 | 失败情况返回值 |
|----------|----------|---------|------|------|------------|----------------|
| `createTestUser()` | POST | `/test/user` | 无 | 创建测试用户 | `{"success": true, "message": "测试用户创建成功", "data": {用户对象}}` | `{"success": false, "message": "创建失败: 错误信息"}` |
| `getAllUsers()` | GET | `/test/users` | 无 | 获取所有用户 | `{"success": true, "data": [用户列表]}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `checkTables()` | GET | `/test/tables` | 无 | 检查数据库表结构 | `{"success": true, "message": "✅ 实体类创建成功！请检查数据库中的表结构。"}` | `{"success": false, "message": "操作失败: 错误信息"}` |

---

### 9. 类：UserController（用户控制器）

**位置**：`com.example.demo.controller`

**类注解说明**：
- `@RestController`：Spring注解，标识为REST风格的控制器
- `@RequestMapping("/users")`：设置请求路径前缀
- `@CrossOrigin(origins = "*")`：允许跨域请求

**方法列表**：

| 方法签名 | HTTP方法 | API路径 | 参数 | 描述 | 返回值说明 | 失败情况返回值 |
|----------|----------|---------|------|------|------------|----------------|
| `test()` | GET | `/users/test` | 无 | 测试接口 | `"UserController is working!"` | `"操作失败: 错误信息"` |
| `login(Map<String, String> loginRequest)` | POST | `/users/login` | `loginRequest`: 登录请求对象(JSON) | 用户登录 | `{"success": true, "message": "登录成功", "user": {用户信息}}` | `{"success": false, "message": "用户名或密码错误"}` |
| `getAllUsers(int page, int size, String sortBy, String direction)` | GET | `/users` | `page`: 页码(默认0)<br>`size`: 每页大小(默认10)<br>`sortBy`: 排序字段(默认id)<br>`direction`: 排序方向(默认asc) | 获取所有用户（分页） | `{"success": true, "currentPage": 页码, "totalItems": 总记录数, "totalPages": 总页数, "data": [用户列表]}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getUserById(Long id)` | GET | `/users/{id}` | `id`: 用户ID | 根据ID获取用户 | `{"success": true, "data": {用户详情}}` | `{"success": false, "message": "用户不存在"}` |
| `getUserByUsername(String username)` | GET | `/users/username/{username}` | `username`: 用户名 | 根据用户名获取用户 | `{"success": true, "data": {用户详情}}` | `{"success": false, "message": "用户不存在"}` |
| `createUser(User user)` | POST | `/users` | `user`: 用户对象(JSON) | 创建新用户 | `{"success": true, "message": "用户创建成功", "data": {用户详情}}` | `{"success": false, "message": "用户名已存在"}` |
| `updateUser(Long id, User user)` | PUT | `/users/{id}` | `id`: 用户ID<br>`user`: 用户对象(JSON) | 更新用户信息 | `{"success": true, "message": "用户更新成功", "data": {用户详情}}` | `{"success": false, "message": "用户不存在"}` |
| `deleteUser(Long id)` | DELETE | `/users/{id}` | `id`: 用户ID | 删除用户 | `{"success": true, "message": "用户删除成功"}` | `{"success": false, "message": "用户不存在"}` |
| `changeUserStatus(Long id, Integer status)` | PUT | `/users/{id}/status` | `id`: 用户ID<br>`status`: 状态(0-禁用,1-启用) | 修改用户状态 | `{"success": true, "message": "用户状态更新成功", "data": {用户详情}}` | `{"success": false, "message": "用户不存在"}` |
| `getUsersByRole(String role)` | GET | `/users/role/{role}` | `role`: 用户角色 | 根据角色获取用户 | `{"success": true, "data": [用户列表]}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `searchUsers(String keyword, int page, int size)` | GET | `/users/search` | `keyword`: 关键词<br>`page`: 页码(默认0)<br>`size`: 每页大小(默认10) | 用户名或真实名字模糊搜索用户 | `{"success": true, "message": "搜索成功", "data": {分页数据}}` | `{"success": false, "message": "关键词不能为空"}` |
| `checkUsernameExists(String username)` | GET | `/users/check-username/{username}` | `username`: 用户名 | 检查用户名是否存在 | `{"success": true, "exists": true/false}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `getUserStatistics()` | GET | `/users/statistics` | 无 | 获取用户统计信息 | `{"success": true, "data": {统计信息}}` | `{"success": false, "message": "操作失败: 错误信息"}` |
| `updatePassword(Long id, Map<String, String> passwordRequest)` | PUT | `/users/{id}/password` | `id`: 用户ID<br>`passwordRequest`: 密码更新请求(JSON) | 更新用户密码 | `{"success": true, "message": "密码更新成功"}` | `{"success": false, "message": "旧密码不正确"}` |

**用户响应对象结构**：

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 用户ID |
| username | String | 用户名 |
| realName | String | 真实姓名 |
| phone | String | 手机号 |
| email | String | 邮箱 |
| role | String | 角色：ADMIN, PHARMACIST, PURCHASER |
| status | Integer | 状态：0-禁用，1-正常 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

---

### 返回体结构统一说明

所有控制器的返回体均采用统一的Map<String, Object>结构，包含以下字段：

| 字段名 | 类型 | 描述 | 必需 |
|--------|------|------|------|
| success | boolean | 操作是否成功 | 是 |
| message | string | 操作结果消息 | 否 |
| data | object/array | 响应数据 | 否 |
| currentPage | number | 当前页码（分页接口） | 否 |
| totalItems | number | 总记录数（分页接口） | 否 |
| totalPages | number | 总页数（分页接口） | 否 |
| exists | boolean | 检查存在性接口的结果 | 否 |

**分页数据结构**（当返回`data`为分页对象时）：

| 字段名 | 类型 | 描述 |
|--------|------|------|
| content | array | 当前页数据列表 |
| currentPage | number | 当前页码 |
| pageSize | number | 每页大小 |
| totalItems | number | 总记录数 |
| totalPages | number | 总页数 |
| isFirst | boolean | 是否第一页 |
| isLast | boolean | 是否最后一页 |

**失败情况返回结构**：

| 字段名 | 类型 | 描述 |
|--------|------|------|
| success | boolean | false |
| message | string | 错误信息 |
| data | object/array | 可能的错误数据（可为空） |

---

### 控制器层注解说明

| 注解 | 说明 | 示例 |
|------|------|------|
| @RestController | 标识为REST风格的控制器，返回JSON数据 | @RestController |
| @RequestMapping | 设置请求路径前缀 | @RequestMapping("/categories") |
| @CrossOrigin | 允许跨域请求 | @CrossOrigin(origins = "*") |
| @GetMapping | 处理GET请求 | @GetMapping("/test") |
| @PostMapping | 处理POST请求 | @PostMapping |
| @PutMapping | 处理PUT请求 | @PutMapping("/{id}") |
| @DeleteMapping | 处理DELETE请求 | @DeleteMapping("/{id}") |
| @PathVariable | 获取路径参数 | @PathVariable Long id |
| @RequestParam | 获取查询参数 | @RequestParam(defaultValue = "0") int page |
| @RequestBody | 获取请求体数据 | @RequestBody Category category |
| @DateTimeFormat | 日期时间格式化 | @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) |
| @Deprecated | 标记方法已废弃 | @Deprecated |

### 状态码说明

| 状态码 | 含义 | 示例 |
|--------|------|------|
| 200 OK | 请求成功 | 获取数据、更新成功 |
| 201 Created | 创建成功 | 创建新资源 |
| 400 Bad Request | 请求参数错误 | 参数格式不正确、库存不足 |
| 404 Not Found | 资源不存在 | 找不到指定ID的资源 |
| 409 Conflict | 资源冲突 | 名称已存在 |
| 401 Unauthorized | 未授权 | 登录失败 |
| 500 Internal Server Error | 服务器内部错误 | 处理过程异常 |

---



## 配置层 (Config Layer) 文档
### 配置类使用

1. **HttpClientConfig**：
    - 在需要发送HTTP请求的服务中注入 `RestTemplate`
    - 在需要JSON序列化/反序列化的地方注入 `ObjectMapper`

2. **PasswordEncoderConfig**：
    - 在用户认证、注册、密码修改等场景中注入 `PasswordEncoder`
    - 使用其 `encode()` 方法加密密码，`matches()` 方法验证密码

3. **SecurityConfig**：
    - 当前配置为开发/测试环境专用
    - 生产环境应根据实际需求调整安全策略

### 1. 类：HttpClientConfig（HTTP客户端配置）

**位置**：`com.example.demo.config`

**继承关系**：无

**类注解说明**：
- `@Configuration`：标识为Spring配置类

**描述**：配置HTTP客户端相关的Bean，包括RestTemplate和ObjectMapper

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 可见性权限 | 注解/备注 |
| -------- | -------- | ---- | ---- | ---------- | ---------- |
| `restTemplate()` | `RestTemplate` | 无 | 创建并返回RestTemplate实例 | Public | `@Bean` 注解，注册为Spring Bean |
| `objectMapper()` | `ObjectMapper` | 无 | 创建并返回ObjectMapper实例 | Public | `@Bean` 注解，注册为Spring Bean |

**功能说明**：
- `RestTemplate`：用于发送HTTP请求，调用外部API
- `ObjectMapper`：用于JSON序列化和反序列化

---

### 2. 类：PasswordEncoderConfig（密码编码器配置）

**位置**：`com.example.demo.config`

**继承关系**：无

**类注解说明**：
- `@Configuration`：标识为Spring配置类

**描述**：配置密码编码器Bean，用于密码加密和验证

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 可见性权限 | 注解/备注 |
| -------- | -------- | ---- | ---- | ---------- | ---------- |
| `passwordEncoder()` | `PasswordEncoder` | 无 | 创建并返回BCryptPasswordEncoder实例 | Public | `@Bean` 注解，注册为Spring Bean |

**功能说明**：
- `BCryptPasswordEncoder`：使用BCrypt算法对密码进行加密和验证，提供安全的密码存储方案

---

### 3. 类：SecurityConfig（安全配置）

**位置**：`com.example.demo.config`

**继承关系**：无

**类注解说明**：
- `@Configuration`：标识为Spring配置类
- `@EnableWebSecurity`：启用Spring Security

**描述**：配置Spring Security，设置请求授权规则和安全策略

**方法列表**：

| 方法签名 | 返回类型 | 参数 | 描述 | 可见性权限 | 注解/备注 |
| -------- | -------- | ---- | ---- | ---------- | ---------- |
| `filterChain(HttpSecurity http)` | `SecurityFilterChain` | `http`：HttpSecurity对象 | 配置安全过滤链 | Public | `@Bean` 注解，注册为Spring Bean |

**配置详情**：
1. **禁用CSRF**：方便测试POST/PUT/DELETE请求
2. **允许所有请求**：无需登录即可访问所有接口
3. **禁用登录页**：避免浏览器弹窗
4. **禁用Basic认证**：简化测试流程

**适用场景**：
- 开发环境
- 测试环境
- 无需身份验证的公开API

---

## 视图层 (Views Layer) 文档

### 1. 类：Views（JSON视图定义）

**位置**：`com.example.demo.views`

**继承关系**：无

**类注解说明**：无

**描述**：定义JSON视图接口，用于控制API响应中字段的可见性

**接口列表**：

| 接口名 | 继承关系 | 描述 | 适用场景 |
| ------ | -------- | ---- | -------- |
| `Public` | 无 | 公共可访问字段 | 所有公开接口，返回基础信息 |
| `Internal` | `extends Public` | 内部使用字段 | 内部系统接口，返回更多信息 |
| `Detail` | `extends Internal` | 详细字段 | 需要详细信息的接口 |
| `Admin` | `extends Detail` | 管理员字段 | 管理员接口，返回所有信息 |
| `MLFeatures` | `extends Public` | 机器学习特征字段 | 机器学习相关接口 |
| `Create` | `extends Public` | 创建操作字段 | 创建资源的请求和响应 |
| `Update` | `extends Public` | 更新操作字段 | 更新资源的请求和响应 |

**使用方式**：
- 在实体类的属性上使用 `@JsonView(Views.XXX.class)` 注解
- 在控制器方法上使用 `@JsonView(Views.XXX.class)` 注解
- 序列化时，只有被指定视图包含的字段会被返回

**继承关系图**：
```
Public
├── Internal
│   └── Detail
│       └── Admin
├── MLFeatures
├── Create
└── Update
```

**作用**：
1. **控制响应字段**：根据不同接口需求返回不同级别的字段
2. **保护敏感信息**：确保敏感字段只在适当的接口中返回
3. **优化响应大小**：减少不必要字段的传输，提高API性能
4. **统一接口风格**：建立标准化的字段可见性规则


### 视图接口使用

1. **基础原则**：
    - 从 `Public` 开始，根据需要逐步使用更高级别的视图
    - 敏感字段应使用 `Admin` 视图保护
    - 详细信息使用 `Detail` 视图

2. **最佳实践**：
    - 为每个实体类的字段明确指定视图级别
    - 为每个控制器方法指定适当的返回视图
    - 保持视图使用的一致性和可预测性

3. **常见视图级别使用场景**：
    - `Public`：公开查询接口，如商品列表、基本信息查询
    - `Internal`：内部系统接口，如员工管理、订单处理
    - `Detail`：详细信息接口，如商品详情、订单详情
    - `Admin`：管理后台接口，如用户管理、系统设置
    - `Create/Update`：创建/更新操作的请求和响应
    - `MLFeatures`：机器学习数据采集和分析接口
