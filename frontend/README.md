# FlowSync 小组任务协同管理系统

## 技术栈
- 前端：Vue 3、Vue Router 4、Element Plus、Axios，端口 8081
- 后端：Spring Boot 3.3.5、MyBatis-Plus 3.5.8，端口 8080
- 数据库：MySQL 8.x，数据库名 `flowsync_simple`
- API 文档：SpringDoc OpenAPI
- AI：阿里云千问 `qwen-plus`，未配置 Key 时自动返回兜底方案

## 1. 初始化数据库
在 MySQL 中执行：

```sql
source demo/database/flowsync_simple.sql;
```

默认后端数据库账号为 `root / 123456`。账号不同可设置：

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="你的MySQL密码"
```

## 2. 启动后端
```powershell
cd demo
.\mvnw.cmd spring-boot:run
```

Swagger：
`http://localhost:8080/swagger-ui/index.html`

## 3. 启动前端
新开终端：

```powershell
npm install
npm run serve
```

访问：
`http://localhost:8081`

## 测试账号
- leader / 123456（负责人）
- member1 / 123456（成员）
- member2 / 123456（成员）

## 千问配置（可选）
```powershell
$env:DASHSCOPE_API_KEY="sk-你的API-Key"
```

未配置时，AI 建议与任务拆解接口会使用教学兜底结果，其他模块正常运行。
