# Vehicle Rental System (车辆租赁系统)

这是一个基于 **Spring Boot** 和 **React** 的全栈车辆租赁管理系统，作为大学 Java-DB 课程设计的项目。该系统提供了一个完整的平台，允许用户浏览、租赁车辆，并为管理员提供后台管理功能来维护车辆、订单和用户信息。

## 🛠 技术栈

### 后端 (Backend)
- **语言**: Java 21
- **框架**: Spring Boot
- **数据库交互**: Spring Data JPA
- **数据库**: PostgreSQL
- **安全认证**: Spring Security Crypto + JWT (JSON Web Token)
- **API 文档**: SpringDoc OpenAPI (Swagger UI)
- **工具库**: Lombok, Validation

### 前端 (Frontend)
- **框架**: React 18
- **构建工具**: Vite
- **语言**: TypeScript
- **路由**: React Router DOM
- **HTTP 客户端**: Axios
- **日期处理**: Dayjs
- **样式**: CSS Modules (根据文件结构推断)

## ✨ 功能特性

### 👤 用户端
- **用户认证**：注册、登录 (基于 JWT)。
- **车辆浏览**：查看可用车辆列表，按类别筛选。
- **车辆详情**：查看车辆详细信息（价格、状态、描述）。
- **租赁服务**：创建租赁订单，选择取车/还车门店及时间。
- **订单管理**：查看“我的订单”列表及订单详情。
- **支付功能**：模拟支付流程。

### 🛡 管理员后台
- **仪表盘**：系统概览。
- **车辆管理**：添加、修改、删除车辆信息。
- **订单管理**：查看和管理所有用户订单。
- **门店管理**：管理租赁门店信息。
- **维护管理**：记录和查看车辆维护/维修记录。
- **支付管理**：查看支付记录。

## 📂 项目结构

```
Java-DB/demo/
├── src/main/java/       # 后端 Java 源代码
│   ├── config/          # 配置类 (Security, Swagger等)
│   ├── controller/      # REST API 控制器
│   ├── entity/          # 数据库实体类
│   ├── repository/      # JPA 仓库接口
│   ├── service/         # 业务逻辑层
│   └── ...
├── src/main/resources/  # 后端资源文件
│   ├── application.properties # 配置文件
│   └── schema.sql       # 数据库初始化脚本
├── frontend/            # 前端 React 项目
│   ├── src/
│   │   ├── api/         # API 请求封装
│   │   ├── components/  # 公共组件
│   │   ├── context/     # React Context (如 AuthContext)
│   │   ├── pages/       # 页面组件 (含 admin 后台)
│   │   └── ...
│   └── package.json
├── init_database.sh     # 数据库初始化 Shell 脚本
├── pom.xml              # Maven 依赖配置
└── README.md            # 项目说明文档
```

## 🚀 快速开始

### 1. 环境准备
确保你的开发环境已安装以下工具：
- **Java JDK 21**
- **Node.js** (推荐 v18+)
- **PostgreSQL** (推荐 v14+)
- **Maven** (项目包含 mvnw，可选)

### 2. 数据库配置
1. 创建一个名为 `vehicle_rental` 的 PostgreSQL 数据库。
2. 修改 `src/main/resources/application.properties` 中的数据库连接信息：
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/vehicle_rental
   spring.datasource.username=你的数据库用户名 (默认: postgres)
   spring.datasource.password=你的数据库密码 (默认: 114514)
   ```
3. 系统启动时会自动根据 `schema.sql` 初始化表结构（配置了 `spring.sql.init.mode=always`）。

### 3. 启动后端
在项目根目录下运行：
```bash
# 使用 Maven Wrapper 运行
./mvnw spring-boot:run
```
后端服务将在 `http://localhost:8080` 启动。

### 4. 启动前端
进入 `frontend` 目录并启动开发服务器：
```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```
前端页面将在 `http://localhost:5173` (默认) 启动。

## 📖 API 文档
后端启动后，可以访问 Swagger UI 查看和测试 API：
- 地址: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## 📝 课程设计说明
本项目旨在演示一个典型的分层架构应用程序：
- **Controller 层**: 处理 HTTP 请求。
- **Service 层**: 包含业务逻辑。
- **Repository 层**: 处理数据持久化。
- **Entity 层**: 定义数据模型。
- **Frontend**: 现代化的单页应用 (SPA) 架构。

---
*Created by Imicola for Java-DB Course Project.*
