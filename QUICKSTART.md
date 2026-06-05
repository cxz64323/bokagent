# 快速开始指南

欢迎使用 BokAgent - AI Agent工作流编排系统！

## 前置要求

在开始之前，请确保你的系统已安装以下软件：

- **Docker** 20.10+ 和 **Docker Compose** 2.0+
- **Git**（可选，用于克隆项目）

## 第一步：获取项目代码

如果你还没有项目代码，可以通过以下方式获取：

```bash
git clone https://github.com/your-org/bokagent.git
cd bokagent
```

或者直接下载ZIP文件并解压。

## 第二步：配置环境变量

复制环境变量模板文件：

### Linux/Mac
```bash
cp .env.example .env
```

### Windows (PowerShell)
```powershell
Copy-Item .env.example .env
```

然后编辑 `.env` 文件，至少配置以下API密钥：

```bash
OPENAI_API_KEY=sk-your-openai-api-key
DEEPSEEK_API_KEY=sk-your-deepseek-api-key
QWEN_API_KEY=sk-your-qwen-api-key
```

> 💡 **提示**: 如果暂时没有API密钥，可以使用占位值，系统仍能启动，但LLM功能无法使用。

## 第三步：一键启动服务

### 方式1: 使用启动脚本（推荐）

#### Linux/Mac
```bash
chmod +x start.sh
./start.sh
```

#### Windows (PowerShell)
```powershell
.\start.ps1
```

### 方式2: 手动启动

```bash
docker-compose up -d
```

等待约30秒让所有服务启动完成。

## 第四步：验证部署

### 检查服务状态

```bash
docker-compose ps
```

应该看到所有服务都处于 `Up` 状态。

### 验证UTF-8编码

```bash
# 检查PostgreSQL编码
docker-compose exec postgres psql -U bokagent -d workflow_db -c "SHOW server_encoding;"
# 应该显示: UTF8

# 检查MySQL编码
docker-compose exec mysql mysql -u bokagent -psecret business_db -e "SHOW VARIABLES LIKE 'character_set_database';"
# 应该显示: utf8mb4
```

### 测试中文存储

```bash
docker-compose exec postgres psql -U bokagent -d workflow_db -c \
  "INSERT INTO workflows (name, description, graph_data) VALUES ('测试 🎉', 'Hello ✨', '{}');"

docker-compose exec postgres psql -U bokagent -d workflow_db -c \
  "SELECT * FROM workflows WHERE name LIKE '%测试%';"
```

应该能看到中文和Emoji正常显示。

## 第五步：访问应用

打开浏览器访问：

- **前端应用**: http://localhost
- **后端健康检查**: http://localhost:8080/actuator/health
- **MinIO控制台**: http://localhost:9001 (用户名/密码: minioadmin/miniosecret)

## 常见问题

### 1. 端口被占用

如果启动时提示端口被占用，可以修改 `docker/docker-compose.yml` 中的端口映射：

```yaml
ports:
  - "8081:8080"  # 将8080改为8081
```

### 2. 服务启动失败

查看日志排查问题：

```bash
docker-compose logs backend
docker-compose logs frontend
```

### 3. 数据库连接失败

确保数据库服务已启动：

```bash
docker-compose ps postgres mysql
```

如果未启动，重启服务：

```bash
docker-compose restart postgres mysql
```

### 4. 中文显示为问号或乱码

这通常不是本项目的问题，可能是你的终端或浏览器编码设置问题。确保：

- 终端使用UTF-8编码
- 浏览器自动检测UTF-8
- 操作系统语言支持UTF-8

## 停止服务

```bash
docker-compose down
```

如果要清除数据卷（慎用！）：

```bash
docker-compose down -v
```

## 本地开发模式

如果你想以开发模式运行项目：

### 后端

```bash
cd backend
mvn spring-boot:run
```

### 前端

```bash
cd frontend
npm install
npm run dev
```

前端将运行在 http://localhost:3000，并自动代理API请求到后端。

## 下一步

项目已成功启动！接下来你可以：

1. 📖 阅读 [用户手册](docs/user-guide.md) 了解如何使用工作流编辑器
2. 🔧 查看 [API文档](docs/api-reference.md) 了解后端接口
3. 🔌 学习 [插件开发](docs/plugin-development.md) 创建自定义插件
4. 🛠️ 学习 [工具开发](docs/tool-development.md) 创建自定义工具

## 获取帮助

如果遇到问题：

1. 查看 [PROJECT_INIT_STATUS.md](PROJECT_INIT_STATUS.md) 了解项目初始化详情
2. 查看 [README.md](README.md) 了解项目概览
3. 提交Issue到GitHub仓库

祝你使用愉快！🎉
