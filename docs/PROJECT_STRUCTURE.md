# 项目结构说明

本文档详细说明BokAgent项目的目录结构和各文件的用途。

## 根目录

```
bokagent/
├── .env.example              # 环境变量模板文件
├── .gitignore                # Git忽略文件配置
├── README.md                 # 项目主文档
├── QUICKSTART.md             # 快速开始指南
├── PROJECT_INIT_STATUS.md    # 项目初始化状态报告
├── start.sh                  # Linux/Mac启动脚本
├── start.ps1                 # Windows PowerShell启动脚本
├── backend/                  # Spring Boot后端项目
├── frontend/                 # React前端项目
├── docker/                   # Docker配置文件
├── plugin-sdk/               # 插件开发SDK（待创建）
├── tool-sdk/                 # 工具开发SDK（待创建）
├── sample-plugins/           # 示例插件（待创建）
└── sample-tools/             # 示例工具（待创建）
```

## 后端项目 (backend/)

```
backend/
├── pom.xml                   # Maven依赖配置文件
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── bokagent/
        │           ├── BokAgentApplication.java  # Spring Boot主应用类
        │           ├── controller/               # REST控制器（待创建）
        │           ├── service/                  # 业务逻辑服务（待创建）
        │           ├── engine/                   # 工作流引擎（待创建）
        │           ├── nodes/                    # 节点实现（待创建）
        │           ├── plugin/                   # 插件系统（待创建）
        │           ├── tools/                    # 工具系统（待创建）
        │           ├── mcp/                      # MCP协议（待创建）
        │           ├── llm/                      # LLM集成（待创建）
        │           ├── tts/                      # TTS服务（待创建）
        │           ├── storage/                  # 存储服务（待创建）
        │           ├── model/                    # 数据模型（待创建）
        │           ├── mapper/                   # MyBatis Mapper（待创建）
        │           ├── dto/                      # 数据传输对象（待创建）
        │           ├── cache/                    # 缓存服务（待创建）
        │           ├── async/                    # 异步执行（待创建）
        │           ├── compatibility/            # 兼容性测试（待创建）
        │           └── config/                   # 配置类（待创建）
        └── resources/
            ├── application.yml                   # 应用配置文件
            └── db/
                └── migration/                    # Flyway数据库迁移脚本
                    ├── V1__create_workflow_tables.sql      # 工作流表
                    ├── V2__create_execution_records.sql    # 执行记录表
                    ├── V3__create_plugin_info_table.sql    # 插件信息表（待创建）
                    ├── V4__create_tool_info_table.sql      # 工具信息表（待创建）
                    └── V5__create_cache_tables.sql         # 缓存表（待创建）
```

### 关键文件说明

#### `pom.xml`
Maven项目配置文件，包含：
- Spring Boot 3.5.0父POM
- Spring AI 1.1.0依赖管理
- MyBatis-Plus、PostgreSQL、MySQL、Redis、MinIO等依赖
- Java 21编译配置

#### `application.yml`
Spring Boot应用配置，包含：
- 服务器端口和UTF-8编码设置
- 双数据源配置（PostgreSQL + MySQL）
- Redis缓存配置
- Spring AI多LLM配置
- MinIO对象存储配置
- MCP协议配置
- 重试、超时、缓存策略配置
- 日志UTF-8编码配置

#### `BokAgentApplication.java`
Spring Boot主应用类，负责：
- 启动Spring应用
- 设置JVM UTF-8编码
- 输出编码验证信息

## 前端项目 (frontend/)

```
frontend/
├── package.json              # npm依赖配置
├── index.html                # HTML入口文件
├── tsconfig.json             # TypeScript配置
├── vite.config.ts            # Vite构建配置
└── src/
    ├── main.tsx              # React应用入口
    ├── App.tsx               # 根组件
    ├── components/           # React组件（待创建）
    │   ├── WorkflowEditor/   # 工作流编辑器
    │   ├── DebugDrawer/      # 调试抽屉
    │   ├── AudioPlayer/      # 音频播放器
    │   ├── PluginMarket/     # 插件市场
    │   └── ToolMarket/       # 工具市场
    ├── services/             # API服务（待创建）
    ├── types/                # TypeScript类型定义（待创建）
    └── hooks/                # React Hooks（待创建）
```

### 关键文件说明

#### `package.json`
npm依赖配置，包含：
- React 18核心库
- Ant Design 5 UI组件库
- React Flow工作流可视化库
- Monaco Editor代码编辑器
- STOMP WebSocket客户端
- Vite构建工具

#### `index.html`
HTML入口文件，包含：
- UTF-8字符集声明
- 中文lang属性
- root挂载点

#### `main.tsx`
React应用入口，负责：
- 配置Ant Design中文locale
- 配置dayjs中文locale
- 渲染根组件

#### `vite.config.ts`
Vite构建配置，包含：
- React插件
- 开发服务器端口3000
- API代理到后端8080端口
- WebSocket代理配置

## Docker配置 (docker/)

```
docker/
├── docker-compose.yml        # Docker Compose编排文件
├── Dockerfile.backend        # 后端Docker镜像构建文件
├── Dockerfile.frontend       # 前端Docker镜像构建文件
├── nginx.conf                # Nginx配置文件
├── init-postgres.sql         # PostgreSQL初始化脚本
└── init-mysql.sql            # MySQL初始化脚本
```

### 关键文件说明

#### `docker-compose.yml`
Docker服务编排文件，定义6个服务：
1. **postgres** - PostgreSQL 15数据库（工作流数据）
2. **mysql** - MySQL 8.0数据库（业务数据）
3. **redis** - Redis 7缓存
4. **minio** - MinIO对象存储
5. **backend** - Spring Boot后端服务
6. **frontend** - Nginx前端服务

每个服务都配置了：
- UTF-8编码环境变量
- 健康检查
- 数据卷持久化
- 端口映射

#### `Dockerfile.backend`
后端Docker镜像构建文件：
- 多阶段构建（Maven构建 -> JRE运行）
- Alpine Linux轻量级基础镜像
- 设置UTF-8环境变量
- 安装时区数据
- JVM启动参数包含UTF-8编码设置
- 启用虚拟线程
- 健康检查配置

#### `Dockerfile.frontend`
前端Docker镜像构建文件：
- 多阶段构建（Node构建 -> Nginx运行）
- Alpine Linux轻量级基础镜像
- 设置UTF-8环境变量
- 安装时区数据
- Nginx配置挂载

#### `nginx.conf`
Nginx服务器配置：
- charset utf-8设置
- 前端静态文件服务
- API反向代理（含UTF-8 Accept-Charset头）
- WebSocket代理配置
- MCP SSE端点代理

#### `init-postgres.sql`
PostgreSQL初始化脚本：
- 创建UTF8编码的workflow_db数据库
- 安装uuid-ossp和pg_trgm扩展
- 验证编码设置

#### `init-mysql.sql`
MySQL初始化脚本：
- 创建utf8mb4编码的business_db数据库
- 验证编码设置

## 待创建的模块

以下模块将在后续开发阶段创建：

### plugin-sdk/
插件开发SDK，提供给插件开发者：
- BokPlugin接口
- PluginMetadata元数据
- PluginContext上下文
- 注解定义

### tool-sdk/
工具开发SDK，提供给工具开发者：
- BokTool接口
- ToolDefinition定义
- ToolResult结果
- RetryPolicy重试策略
- FallbackHandler回退处理器

### sample-plugins/
示例插件：
- azure-tts-plugin - Azure TTS插件
- baidu-llm-plugin - 百度文心一言插件

### sample-tools/
示例工具：
- web-search-tool - 网络搜索工具
- calculator-tool - 计算器工具
- weather-api-tool - 天气API工具

## 文件命名规范

- **Java文件**: PascalCase.java（如：BokAgentApplication.java）
- **TypeScript文件**: camelCase.tsx或PascalCase.tsx（如：main.tsx, App.tsx）
- **配置文件**: kebab-case.yml（如：application.yml）
- **SQL文件**: V{version}__{description}.sql（Flyway规范）
- **文档文件**: UPPER_CASE.md（如：README.md）

## 编码规范

所有文本文件统一使用UTF-8编码，包括：
- 源代码文件
- 配置文件
- 文档文件
- SQL脚本
- 日志文件

这确保了中文和Emoji在所有环境中都能正确显示。
