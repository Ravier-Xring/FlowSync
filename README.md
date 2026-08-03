<div align="center">

# FlowSync

**小组任务协同管理系统** — Vue 3 + Spring Boot + 阿里云千问 AI

![Vue](https://img.shields.io/badge/Vue-3.x-4FC08D?logo=vue.js&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.5-6DB33F?logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.x-4479A1?logo=mysql&logoColor=white)
![Qwen](https://img.shields.io/badge/AI-千问_Plus-FF6A00?logo=alibabacloud&logoColor=white)

</div>

---

## 系统架构

```mermaid
graph TB
    Browser["🖥 浏览器 :8081<br/>Vue 3 + Element Plus<br/>13 个视图 · 路由守卫 · Vuex"] -->|"REST JSON"| API

    subgraph Backend["Spring Boot :8080"]
        API["Controller<br/>REST API 接口"] --> Svc["Service<br/>业务逻辑层"]
        Svc --> Mapper["Mapper<br/>MyBatis-Plus"]
        Auth["JWT 拦截器<br/>认证 · 鉴权"]
    end

    Mapper --> DB[("MySQL 8.x<br/>flowsync_simple<br/>6 张核心表")]
    Svc -.->|"AI 调用"| Qwen["阿里云千问<br/>qwen-plus<br/>智能任务拆解"]
    Auth -.-> Browser

    style Browser fill:#e3f2fd,stroke:#1976d2,color:#0d47a1
    style API fill:#e8f5e9,stroke:#4caf50,color:#1b5e20
    style Svc fill:#e8f5e9,stroke:#4caf50,color:#1b5e20
    style Mapper fill:#e8f5e9,stroke:#4caf50,color:#1b5e20
    style Auth fill:#fff3e0,stroke:#f57c00,color:#e65100
    style DB fill:#f3e5f5,stroke:#7b1fa2,color:#4a148c
    style Qwen fill:#fce4ec,stroke:#c2185b,color:#880e4f
    style Backend fill:#fafafa,stroke:#e0e0e0,color:#333
```

## 用户工作流

```mermaid
graph LR
    A[创建项目] --> B[AI 任务拆解]
    B --> C[分配执行]
    C --> D[进度追踪]
    D --> E[项目总结]

    C -..-> F[AI 建议]
    A -..-> G[(操作日志)]
    C -..-> G
    D -..-> G
    E -..-> G

    style A fill:#e3f2fd,stroke:#1976d2,color:#0d47a1
    style B fill:#f3e5f5,stroke:#7b1fa2,color:#4a148c
    style C fill:#e8f5e9,stroke:#4caf50,color:#1b5e20
    style D fill:#fff3e0,stroke:#f57c00,color:#e65100
    style E fill:#e0f2f1,stroke:#00897b,color:#004d40
    style F fill:#fce4ec,stroke:#c2185b,color:#880e4f
    style G fill:#eceff1,stroke:#78909c,color:#37474f
```

---

## 功能模块

| 模块 | 说明 |
|------|------|
| 🔐 **登录认证** | JWT Token · 路由拦截 · 三种角色（负责人/成员） |
| 📊 **工作台** | 成员 · 项目 · 任务 · 总结 一站总览 |
| 📁 **项目管理** | CRUD · 负责人 · 状态/优先级 · 时间范围 |
| ✅ **任务管理** | 父子任务 · 筛选 · 甘特图 · AI 建议 |
| 🤖 **AI 拆解** | 千问智能分解 · 一键导入 · 无 Key 自动兜底 |
| 📈 **进度跟踪** | 实时进度 · 按任务筛选 · 修改记录 |
| 📝 **总结中心** | 项目/任务/阶段 多类型总结 |
| 👥 **成员管理** | 成员列表 · 个人信息 · 密码修改 |
| 📋 **操作日志** | 关键操作审计 · 全程可追溯 |

---

## 技术栈

| 层级 | 技术 | 端口 |
|------|------|:--:|
| 前端 | Vue 3 + Element Plus + Vue Router + Axios | 8081 |
| 后端 | Spring Boot 3.3.5 + MyBatis-Plus 3.5.8 | 8080 |
| 数据库 | MySQL 8.x | 3306 |
| API 文档 | SpringDoc OpenAPI (Swagger) | — |
| AI | 阿里云千问 qwen-plus | — |

---

## 快速开始

### 1. 初始化数据库

```sql
source backend/database/flowsync_simple.sql;
```

### 2. 启动后端

```bash
cd backend
./mvnw spring-boot:run
# Swagger: http://localhost:8080/swagger-ui/index.html
```

### 3. 启动前端

```bash
cd frontend
npm install && npm run serve
# 访问: http://localhost:8081
```

### 4. 测试账号

| 角色 | 账号 | 密码 |
|------|------|------|
| 负责人 | `leader` | `123456` |
| 成员 | `member1` | `123456` |
| 成员 | `member2` | `123456` |

### 5. AI 配置（可选）

```powershell
$env:DASHSCOPE_API_KEY="sk-your-api-key"
```

> 未配置时 AI 接口自动返回教学兜底结果，其他模块不受影响。

---

## 目录结构

```
FlowSync/
├── frontend/                    Vue 3 前端
│   └── src/
│       ├── api/        🌐 Axios 请求层
│       ├── router/     🧭 路由配置
│       ├── store/      🗃️ Vuex 状态管理
│       ├── views/      🎨 13 个页面组件
│       └── utils/      🔧 认证 · 分页 · 工具
│
├── backend/                     Spring Boot 后端
│   └── src/main/java/hgc/flowsyncapi/
│       ├── controller/ 📡 REST 控制器
│       ├── service/    ⚙️ 业务逻辑
│       ├── mapper/     🗄️ MyBatis-Plus
│       ├── entity/     📦 数据库实体
│       ├── dto/        📋 数据传输对象
│       ├── common/     🧩 统一响应 · 异常
│       └── config/     🔐 拦截器 · Web · OpenAPI
│
├── backend/database/            📜 flowsync_simple.sql
├── docs/                        📖 需求文档 · 启动说明 · 实现对照
└── README.md
```

---

## 数据库表

| 表名 | 用途 |
|------|------|
| `sys_user` | 用户（账号/密码/角色/联系方式） |
| `project_info` | 项目（名称/状态/优先级/负责人/时间） |
| `task_info` | 任务（标题/描述/指派人/状态/优先级/AI建议） |
| `task_log` | 任务进度记录 |
| `task_summary` | 任务总结（项目/任务/阶段） |
| `operation_log` | 操作审计日志 |

---

## 协作者

<table>
<tr>
<td align="center"><b>Ravier-Xring</b><br/>全栈开发 · 50%</td>
<td align="center"><b>Kayblis576</b><br/>全栈开发 · 50%</td>
</tr>
</table>

---

## License

仅供学习交流使用。
