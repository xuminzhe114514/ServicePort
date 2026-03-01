基于对现有Controller的分析和您的系统对象框架，以下是针对库存核心管理和流入流出管理强化的API提案：

## 一、现有Controller API总结

### 1. CategoryController
**核心功能：**
- 分类CRUD操作
- 树形结构管理
- 批量操作
- 统计信息
- **关联检查：**删除时检查是否有关联药品

**现有API：**
- GET /api/categories - 分页查询
- GET /api/categories/{id} - 按ID查询
- POST /api/categories - 创建
- PUT /api/categories/{id} - 更新
- DELETE /api/categories/{id} - 删除（带关联检查）
- GET /api/categories/parent/{parentId} - 按父分类查询
- GET /api/categories/roots - 获取根分类
- GET /api/categories/tree - 获取树形结构
- 等其他辅助API

### 2. SymptomController
**核心功能：**
- 症状CRUD操作
- 搜索功能
- 批量操作
- 统计信息
- **缺失：**删除时缺少关联检查

**现有API：**
- GET /api/symptoms - 分页查询
- GET /api/symptoms/{id} - 按ID查询
- POST /api/symptoms - 创建
- PUT /api/symptoms/{id} - 更新
- DELETE /api/symptoms/{id} - 删除（无关联检查）
- GET /api/symptoms/name/{name} - 按名称查询
- GET /api/symptoms/search - 搜索
- 等其他辅助API

### 3. MedicineController
**核心功能：**
- 药品CRUD操作
- 价格管理
- 状态管理
- 批量操作
- 统计信息
- **关联查询：**支持按Category查询

**现有API：**
- GET /api/medicines - 分页查询
- GET /api/medicines/{id} - 按ID查询
- POST /api/medicines - 创建
- PUT /api/medicines/{id} - 更新
- DELETE /api/medicines/{id} - 删除（带库存检查）
- GET /api/medicines/category/{categoryId} - 按分类查询
- GET /api/medicines/status/{status} - 按状态查询
- GET /api/medicines/search - 搜索
- 等其他辅助API

### 4. StockController
**核心功能：**
- 库存CRUD操作
- 库存变动（增加/减少）
- 库存检查
- 预警管理
- 批量操作
- 统计信息
- **关联查询：**支持按Medicine查询

**现有API：**
- GET /api/stocks - 分页查询
- GET /api/stocks/{id} - 按ID查询
- POST /api/stocks - 创建
- PUT /api/stocks/{id} - 更新
- DELETE /api/stocks/{id} - 删除
- GET /api/stocks/medicine/{medicineId} - 按药品查询
- GET /api/stocks/medicine/{medicineId}/total - 药品总库存
- PUT /api/stocks/increase - 增加库存
- PUT /api/stocks/reduce - 减少库存
- GET /api/stocks/check-availability - 库存检查
- GET /api/stocks/low-stock - 低库存预警
- GET /api/stocks/expiring - 近效期预警
- 等其他辅助API

### 5. PurchaseOrderController
**核心功能：**
- 采购订单CRUD操作
- 状态管理
- 到货管理
- 批量操作
- 统计信息
- **关联查询：**支持按Medicine查询

**现有API：**
- GET /api/purchase-orders - 分页查询
- GET /api/purchase-orders/{id} - 按ID查询
- POST /api/purchase-orders - 创建
- PUT /api/purchase-orders/{id} - 更新
- DELETE /api/purchase-orders/{id} - 删除
- GET /api/purchase-orders/medicine/{medicineId} - 按药品查询
- PUT /api/purchase-orders/{id}/arrive - 标记到货
- 等其他辅助API

### 6. SaleRecordController
**核心功能：**
- 销售记录CRUD操作
- 销售分析
- 统计信息
- **关联查询：**支持按Medicine查询

**现有API：**
- GET /api/sale-records - 分页查询
- GET /api/sale-records/{id} - 按ID查询
- POST /api/sale-records - 创建
- PUT /api/sale-records/{id} - 更新
- DELETE /api/sale-records/{id} - 删除
- GET /api/sale-records/medicine/{medicineId} - 按药品查询
- GET /api/sale-records/statistics - 销售统计
- 等其他辅助API

## 二、需要添加的API建议

### 1. 虚拟层强化API

#### MedicineController扩展
- **GET /api/medicines/symptom/{symptomId}** - 按症状查询药品（缺失）
- **GET /api/medicines/stock-status** - 药品库存状态批量查询（新增）
- **GET /api/medicines/expiry-warning** - 近效期药品查询（新增）
- **GET /api/medicines/stock-value** - 药品库存价值评估（新增）

#### SymptomController扩展
- **GET /api/symptoms/medicines/{symptomId}** - 症状关联的药品列表（新增）
- **DELETE /api/symptoms/{id}** - 增强删除检查（添加关联检查）

### 2. 现实层强化API

#### StockController核心强化
- **POST /api/stocks/inventory/initiate** - 库存盘点启动（新增）
- **POST /api/stocks/inventory/record** - 盘点结果记录（新增）
- **GET /api/stocks/inventory/history** - 盘点历史查询（新增）
- **GET /api/stocks/transactions** - 库存交易记录（整合入库出库）（新增）
- **GET /api/stocks/valuation** - 库存价值评估（新增）
- **GET /api/stocks/location/{location}** - 按库位查询库存（新增）

#### PurchaseOrderController强化
- **POST /api/purchases/receipts** - 入库单创建（新增）
- **GET /api/purchases/receipts** - 入库单查询（新增）
- **POST /api/purchases/returns** - 采购退货处理（新增）
- **GET /api/purchases/returns** - 采购退货查询（新增）
- **PUT /api/purchases/{id}/receive** - 收货并更新库存（增强）

#### SaleRecordController强化
- **POST /api/sales/issuances** - 出库单创建（新增）
- **GET /api/sales/issuances** - 出库单查询（新增）
- **POST /api/sales/returns** - 销售退货处理（新增）
- **GET /api/sales/returns** - 销售退货查询（新增）
- **GET /api/sales/symptom/{symptomId}** - 按症状查询销售记录（新增）
- **PUT /api/sales/{id}/process** - 处理销售并更新库存（增强）

### 3. 库存分析强化API

#### 库存核心分析
- **GET /api/analytics/inventory/turnover** - 库存周转率分析（新增）
- **GET /api/analytics/inventory/age** - 库存年龄分析（新增）
- **GET /api/analytics/inventory/abc** - 库存ABC分类分析（新增）
- **GET /api/analytics/inventory/trends** - 库存趋势分析（新增）

#### 流入流出分析
- **GET /api/analytics/flows/in** - 入库流量分析（新增）
- **GET /api/analytics/flows/out** - 出库流量分析（新增）
- **GET /api/analytics/flows/balance** - 库存余额分析（新增）
- **GET /api/analytics/profit** - 库存盈亏分析（新增）

## 三、按类为单位的设计方案

### 1. CategoryController设计
**核心原则：**
- 保持现有CRUD功能
- 强化关联检查
- 支持树形结构管理

**API设计：**
- 保留所有现有API
- **新增：**GET /api/categories/stock-summary - 分类库存汇总
- **新增：**GET /api/categories/turnover-rate - 分类周转率

### 2. SymptomController设计
**核心原则：**
- 保持现有CRUD功能
- 强化关联检查
- 支持多对多关联查询

**API设计：**
- 保留所有现有API
- **增强：**DELETE /api/symptoms/{id} - 添加关联药品检查
- **新增：**GET /api/symptoms/medicines/{symptomId} - 症状关联药品列表
- **新增：**GET /api/symptoms/sales/{symptomId} - 症状相关销售记录

### 3. MedicineController设计
**核心原则：**
- 保持现有CRUD功能
- 强化库存关联
- 支持多维度查询

**API设计：**
- 保留所有现有API
- **新增：**GET /api/medicines/symptom/{symptomId} - 按症状查询
- **新增：**GET /api/medicines/stock-status - 库存状态批量查询
- **新增：**GET /api/medicines/expiry-warning - 近效期预警
- **新增：**GET /api/medicines/stock-value - 库存价值评估

### 4. StockController设计
**核心原则：**
- 强化库存核心管理
- 支持专业库存操作
- 提供全面库存分析

**API设计：**
- 保留所有现有API
- **新增：**库存盘点相关API
- **新增：**库存交易记录API
- **新增：**库存价值评估API
- **新增：**库位管理API
- **新增：**库存分析API

### 5. PurchaseOrderController设计
**核心原则：**
- 强化入库流程管理
- 支持专业采购操作
- 与库存联动

**API设计：**
- 保留所有现有API
- **新增：**入库单管理API
- **新增：**采购退货API
- **增强：**入库操作与库存联动

### 6. SaleRecordController设计
**核心原则：**
- 强化出库流程管理
- 支持专业销售操作
- 与库存联动
- 支持症状关联

**API设计：**
- 保留所有现有API
- **新增：**出库单管理API
- **新增：**销售退货API
- **新增：**按症状查询销售API
- **增强：**出库操作与库存联动

## 四、核心强化点

### 1. 库存核心管理强化
- **库存盘点系统**：完整的盘点流程管理
- **库存交易记录**：统一的入库出库交易管理
- **库存价值评估**：基于成本的库存价值计算
- **库位管理**：精细化的库存位置管理
- **库存预警体系**：多维度的库存预警机制

### 2. 库存流入流出管理强化
- **入库单管理**：标准化的入库流程
- **出库单管理**：标准化的出库流程
- **退货管理**：完整的退货处理流程
- **交易联动**：操作与库存的实时同步
- **流程追踪**：完整的操作记录与追踪

### 3. 库存数据分析强化
- **周转率分析**：库存周转效率评估
- **价值分析**：库存价值波动分析
- **趋势分析**：库存变化趋势预测
- **盈亏分析**：基于库存的盈亏计算
- **ABC分析**：库存分类管理

