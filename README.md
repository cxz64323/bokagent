# BokAgent - AI Agent工作流编排系统

一个基于React 18、Spring Boot 3.5、Spring AI和LangGraph4J的企业级AI Agent可视化编排系统。

## 核心特性

- 🎨 **可视化工作流编辑器** - 基于React Flow的拖拽式工作流编排
- 🤖 **多LLM支持** - OpenAI、Deepseek、通义千问等多厂商集成
- 🔧 **工具注册系统** - 支持工作流节点工具和LLM Function Calling
- 🔌 **插件生态系统** - 热插拔插件架构，支持动态扩展
- 📡 **MCP协议支持** - 双向MCP协议（Server + Client）
- 🎵 **TTS音频合成** - 集成ElevenLabs等TTS服务
- 🚀 **企业级特性** - 重试机制、超时控制、缓存、异步执行
- 🐳 **Docker一键部署** - 完整的UTF-8和中文支持

## 技术栈

### 前端
- React 18 + TypeScript
- Vite + Ant Design 5
- React Flow (@xyflow/react)
- Monaco Editor

### 后端
- Spring Boot 3.5 + JDK 21
- Spring AI 1.1
- MyBatis-Plus 3.5
- PostgreSQL + MySQL + Redis + MinIO

## 快速开始

### Docker一键部署（推荐）

```bash
# 1. 克隆项目
git clone https://github.com/your-org/bokagent.git
cd bokagent

# 2. 配置环境变量
cp .env.example .env
# 编辑.env文件，填入你的API密钥

# 3. 一键启动所有服务
docker-compose up -d

# 4. 访问应用
# 前端: http://localhost
# 后端API: http://localhost:8080
# MinIO控制台: http://localhost:9001
```

### 本地开发

#### 后端

```bash
cd backend
mvn spring-boot:run
```

#### 前端

```bash
cd frontend
npm install
npm run dev
```

## UTF-8和中文支持

本项目已完整配置UTF-8编码支持，确保中文和Emoji不会乱码：

- ✅ Docker容器locale设置为C.UTF-8
- ✅ JVM file.encoding设置为UTF-8
- ✅ PostgreSQL使用UTF8编码
- ✅ MySQL使用utf8mb4编码
- ✅ Nginx设置charset utf-8
- ✅ 前端HTML meta charset=UTF-8
- ✅ 日志输出UTF-8编码

## 项目结构

```
bokagent/
├── backend/           # Spring Boot后端
├── frontend/          # React前端
├── docker/            # Docker配置文件
├── plugin-sdk/        # 插件开发SDK
├── tool-sdk/          # 工具开发SDK
├── sample-plugins/    # 示例插件
└── sample-tools/      # 示例工具
```

## 文档

- [用户手册](docs/user-guide.md)
- [插件开发指南](docs/plugin-development.md)
- [工具开发指南](docs/tool-development.md)
- [API文档](docs/api-reference.md)
- [Docker部署指南](docs/docker-deployment.md)
- [UTF-8配置说明](docs/utf8-config.md)

## 许可证

MIT License
