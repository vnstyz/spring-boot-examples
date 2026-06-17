# 07-taobao-cloud（仿淘宝微服务示例）

该项目实现了一个简化版淘宝交易链路，包含：

- 登录验证（`auth` 服务，JWT + Redis）
- 商品详情（`product` 服务，MySQL + Redis 缓存）
- 购物车（`cart` 服务，增删改查 + 结算）
- 下单并发保护（`order` 服务，Redis 分布式锁 + MySQL 原子扣库存）
- 购物车结算与清空（下单后调用购物车内部接口）
- 消息队列异步流程（RabbitMQ 订单创建事件）

## 1. 微服务模块

- `taobao-common`：公共 DTO / 异常 / 返回体
- `taobao-auth-service`：登录、发放 JWT、校验 JWT
- `taobao-product-service`：商品详情查询、库存扣减/回补
- `taobao-cart-service`：购物车管理、结算、内部勾选项接口
- `taobao-order-service`：并发下单、订单落库、消息发布消费

## 2. 环境要求

- JDK 21
- Maven 3.9+
- Docker（可选，推荐）

## 3. 启动中间件（推荐）

在 `07-taobao-cloud` 目录执行：

```bash
docker compose up -d
```

会拉起：

- MySQL: `localhost:3306`（root/root）
- Redis: `localhost:6379`
- RabbitMQ: `localhost:5672`（管理台 `http://localhost:15672`）

> 数据库初始化脚本：`docs/taobao_cloud_init.sql`

## 4. 编译打包

```bash
mvn -DskipTests clean package
```

## 5. 启动服务

可以分别启动每个服务（建议按顺序）：

1. `taobao-auth-service`（8081）
2. `taobao-product-service`（8082）
3. `taobao-cart-service`（8083）
4. `taobao-order-service`（8084）

示例（在 `07-taobao-cloud` 目录）：

```bash
mvn -pl taobao-auth-service spring-boot:run
mvn -pl taobao-product-service spring-boot:run
mvn -pl taobao-cart-service spring-boot:run
mvn -pl taobao-order-service spring-boot:run
```

## 6. 快速联调 API

### 6.1 登录

```http
POST http://localhost:8081/api/auth/login
Content-Type: application/json

{
  "username": "demo",
  "password": "123456"
}
```

返回 `token` 后，后续请求头加：

```text
Authorization: Bearer <token>
```

### 6.2 商品详情

```http
GET http://localhost:8082/api/products/1
```

### 6.3 购物车

- 加入购物车

```http
POST http://localhost:8083/api/cart/items
Authorization: Bearer <token>
Content-Type: application/json

{
  "productId": 1,
  "quantity": 2
}
```

- 查询购物车

```http
GET http://localhost:8083/api/cart/items
Authorization: Bearer <token>
```

- 结算购物车

```http
POST http://localhost:8083/api/cart/settle
Authorization: Bearer <token>
```

### 6.4 下单（并发保护）

```http
POST http://localhost:8084/api/orders/from-cart
Authorization: Bearer <token>
```

下单成功后：

- 订单状态先写入 `CREATED`
- 发送 RabbitMQ 消息
- 消费后推进到 `WAIT_PAY`
- 购物车勾选项会被清空

### 6.5 查询订单详情

```http
GET http://localhost:8084/api/orders/{orderNo}
Authorization: Bearer <token>
```

## 7. 并发下单设计说明

订单服务在 `createFromCart` 中做了两层保护：

- Redis 分布式锁（按用户维度，防止同一用户重复并发下单）
- 商品服务库存原子 SQL：`stock >= quantity` 才能扣减

并且在下单异常时会尝试库存补偿（回补已扣库存），避免库存不一致。

## 8. 中文注释说明

已按你的要求对：

- 每个类
- 关键业务方法
- 复杂流程分支

补充中文注释，方便你继续扩展和答辩讲解。
