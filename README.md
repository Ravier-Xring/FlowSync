# FlowSync 小组任务协同管理系统

> 基于 Vue 3 + Spring Boot 的全栈小组任务协同管理平台，集成阿里云千问 AI 实现智能任务拆解。

## 项目简介

FlowSync 是一个面向小组协作的任务管理系统，支持项目管理、任务分配、进度跟踪、总结回顾和 AI 辅助任务拆解。系统涵盖从任务创建到完成总结的完整工作流，适合课程项目、团队协作等场景。

## 技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| 前端 | Vue 3 + Vue Router 4 + Element Plus + Axios | 端口 8081 |
| 后端 | Spring Boot 3.3.5 + MyBatis-Plus 3.5.8 | 端口 8080 |
| 数据库 | MySQL 8.x | 数据库名 `flowsync_simple` |
| API 文档 | SpringDoc OpenAPI | Swagger UI |
| AI | 阿里云千问 DashScope | 模型 `qwen-plus`，未配置 Key 时自动返回兜底方案 |

## 目录结构

```
FlowSync/
├── frontend/                    # Vue 3 前端
│   ├── public/                  # 静态资源
│   ├── src/
│   │   ├── api/                 # Axios 封装与请求层
│   │   ├── router/              # 路由配置
│   │   ├── store/               # Vuex 状态管理
│   │   ├── styles/              # 全局样式
│   │   ├── utils/               # 工具函数（认证、分页等）
│   │   ├── views/               # 页面组件
│   │   ├── App.vue              # 根组件
│   │   └── main.js              # 入口文件
│   ├── babel.config.js
│   ├── vue.config.js
│   ├── package.json
│   └── .gitignore
│
├── backend/                     # Spring Boot 后端
│   ├── src/main/java/hgc/flowsyncapi/
│   │   ├── common/              # 统一响应、异常处理、上下文
│   │   ├── config/              # 拦截器、Web 配置、OpenAPI 配置
│   │   ├── controller/          # REST 控制器
│   │   ├── dto/                 # 数据传输对象
│   │   ├── entity/              # 数据库实体
│   │   ├── mapper/              # MyBatis-Plus Mapper 接口
│   │   ├── service/             # 业务逻辑层
│   │   └── FlowSyncApiApplication.java  # 启动类
│   ├── src/main/resources/
│   │   └── application.yml      # 应用配置
│   ├── src/test/                # 测试
│   ├── database/
│   │   └── flowsync_simple.sql  # 建库脚本（含测试数据）
│   ├── pom.xml
│   ├── mvnw / mvnw.cmd          # Maven Wrapper
│   └── .mvn/
│
├── docs/                        # 项目文档
│   ├── FlowSync_需求规格说明书.docx
│   ├── 启动说明.txt
│   └── 项目实现对照说明.md
│
├── .gitignore
└── README.md
```

## 快速开始

### 1. 初始化数据库

在 MySQL 中执行建库脚本：

```sql
source backend/database/flowsync_simple.sql;
```

### 2. 启动后端

```bash
cd backend
.\mvnw.cmd spring-boot:run      # Windows
# 或
./mvnw spring-boot:run          # Linux/Mac
```

如果数据库账号不是默认值，设置环境变量：

```powershell
$env:DB_USERNAME="your_username"
$env:DB_PASSWORD="your_password"
```

Swagger UI：`http://localhost:8080/swagger-ui/index.html`

### 3. 启动前端

```bash
cd frontend
npm install
npm run serve
```

访问：`http://localhost:8081`

### 4. 测试账号

| 角色 | 账号 | 密码 |
|------|------|------|
| 负责人 | leader | 123456 |
| 成员 | member1 | 123456 |
| 成员 | member2 | 123456 |

### 5. AI 配置（可选）

```powershell
$env:DASHSCOPE_API_KEY="sk-your-api-key"
```

未配置时，AI 建议与任务拆解接口会使用教学兜底结果，其他模块正常运行。

## 功能模块

| 模块 | 说明 |
|------|------|
| 登录认证 | 登录/登出、登录态拦截、JWT |
| 工作台总览 | 成员数、项目数、任务数、总结数统计 |
| 项目管理 | 项目 CRUD、负责人/状态/优先级/时间范围 |
| 任务管理 | 任务 CRUD、父子任务、按项目/状态筛选、AI 建议 |
| AI 任务拆解 | 单任务建议、任务拆解、拆解结果导入 |
| 进度跟踪 | 进度记录列表、按任务筛选 |
| 总结中心 | 总结列表、多类型总结（项目/任务/阶段） |
| 成员管理 | 成员列表、个人信息、修改密码 |
| 操作日志 | 关键操作审计记录 |

## 数据库表结构

| 表名 | 用途 |
|------|------|
| `sys_user` | 用户信息（用户名、密码、角色、联系方式） |
| `project_info` | 项目信息（名称、状态、优先级、负责人、时间范围） |
| `task_info` | 任务信息（标题、描述、指派人、状态、优先级、截止日期、AI 建议） |
| `task_log` | 任务进度记录（进度百分比、内容） |
| `task_summary` | 任务总结（项目/任务/阶段总结） |
| `operation_log` | 操作日志（操作人、模块、动作、详情） |

## 协作者

本项目由以下两位成员共同开发，工作量各占 50%：

| 成员 | GitHub | 职责 |
|------|--------|------|
| Ravier-Xring | [@Ravier-Xring](https://github.com/Ravier-Xring) | 全栈开发 |
| Kayblis576 | [@Kayblis576](https://github.com/Kayblis576) | 全栈开发 |

## License

本项目仅供学习交流使用。
