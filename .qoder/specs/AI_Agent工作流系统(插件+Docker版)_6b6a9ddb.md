# AI Agent工作流编排系统实施计划 (插件生态 + Docker部署版)

## 技术栈总览

**前端**: React 18 + TypeScript + Vite + React Flow (@xyflow/react) + Ant Design 5 + Monaco Editor  
**后端**: Spring Boot 3.5 + JDK 21 + Spring AI 1.1 + LangGraph4J + MyBatis-Plus 3.5 + WebSocket  
**数据库**: PostgreSQL 15+ (工作流数据存储) + MySQL 8+ (业务数据存储)  
**缓存**: Redis 7 (分布式缓存、会话管理)  
**对象存储**: MinIO (音频文件存储)  
**工作流引擎**: LangGraph4J (Agent编排框架) + Spring AI (LLM统一抽象层)  
**插件系统**: Java SPI + 动态类加载 + 插件市场API  
**容器化**: Docker + Docker Compose (一键部署全栈服务)  
**部署架构**: 前后端分离，Docker Compose编排所有服务

---

## 项目目录结构 (含插件和Docker)

```
bokagent/
├── frontend/                    # React前端项目
│   ├── src/
│   │   ├── components/
│   │   │   ├── WorkflowEditor/
│   │   │   │   ├── Canvas.tsx
│   │   │   │   ├── NodeLibrary.tsx
│   │   │   │   ├── CustomNodes/
│   │   │   │   │   ├── StartNode.tsx
│   │   │   │   │   ├── LLMNode.tsx
│   │   │   │   │   ├── TTSNode.tsx
│   │   │   │   │   └── EndNode.tsx
│   │   │   │   ├── PluginNode.tsx      # 插件节点(动态渲染)
│   │   │   │   ├── ConfigPanel.tsx
│   │   │   │   └── Toolbar.tsx
│   │   │   ├── DebugDrawer/
│   │   │   │   ├── DebugPanel.tsx
│   │   │   │   ├── LogViewer.tsx
│   │   │   │   └── TestInput.tsx
│   │   │   ├── AudioPlayer/
│   │   │   │   └── PodcastPlayer.tsx
│   │   │   └── PluginMarket/           # 插件市场组件
│   │   │       ├── PluginList.tsx
│   │   │       ├── PluginDetail.tsx
│   │   │       └── PluginInstaller.tsx
│   │   ├── services/
│   │   │   ├── api.ts
│   │   │   ├── websocket.ts
│   │   │   └── pluginApi.ts            # 插件市场API
│   │   ├── types/
│   │   │   ├── workflow.ts
│   │   │   ├── node.ts
│   │   │   └── plugin.ts               # 插件类型定义
│   │   ├── hooks/
│   │   │   ├── useWorkflowExecution.ts
│   │   │   ├── useDebugStream.ts
│   │   │   └── usePluginLoader.ts      # 插件动态加载Hook
│   │   └── App.tsx
│   ├── package.json
│   └── vite.config.ts
│
├── backend/                     # Spring Boot后端项目
│   ├── src/main/java/com/bokagent/
│   │   ├── controller/
│   │   │   ├── WorkflowController.java
│   │   │   ├── ExecutionController.java
│   │   │   ├── DebugWebSocketController.java
│   │   │   └── PluginController.java   # 插件管理API
│   │   ├── service/
│   │   │   ├── WorkflowService.java
│   │   │   ├── WorkflowExecutionService.java
│   │   │   ├── AdapterService.java
│   │   │   └── PluginService.java      # 插件管理服务
│   │   ├── engine/
│   │   │   ├── LangGraphExecutor.java
│   │   │   ├── StateGraphBuilder.java
│   │   │   ├── NodeRegistry.java
│   │   │   └── ExecutionContext.java
│   │   ├── nodes/
│   │   │   ├── BaseNode.java
│   │   │   ├── StartNode.java
│   │   │   ├── LLMNode.java
│   │   │   ├── TTSNode.java
│   │   │   ├── EndNode.java
│   │   │   └── PluginNodeWrapper.java  # 插件节点包装器
│   │   ├── plugin/                      # 插件系统核心
│   │   │   ├── PluginManager.java      # 插件管理器(动态加载)
│   │   │   ├── PluginRegistry.java     # 插件注册中心
│   │   │   ├── BokPlugin.java          # 插件接口(@FunctionalInterface)
│   │   │   ├── PluginClassLoader.java  # 自定义类加载器
│   │   │   └── PluginMetadata.java     # 插件元数据(Record)
│   │   ├── llm/
│   │   │   ├── SpringAiConfig.java
│   │   │   ├── OpenAiConfig.java
│   │   │   ├── DeepseekConfig.java
│   │   │   └── QwenConfig.java
│   │   ├── tts/
│   │   │   ├── TtsService.java
│   │   │   └── ElevenLabsTtsService.java
│   │   ├── storage/
│   │   │   ├── MinioService.java
│   │   │   └── AudioStorageService.java
│   │   ├── model/
│   │   │   ├── Workflow.java
│   │   │   ├── WorkflowNode.java
│   │   │   ├── WorkflowEdge.java
│   │   │   ├── ExecutionRecord.java
│   │   │   └── PluginInfo.java         # 插件信息实体
│   │   ├── mapper/
│   │   │   ├── WorkflowMapper.java
│   │   │   ├── ExecutionRecordMapper.java
│   │   │   └── PluginInfoMapper.java
│   │   ├── dto/
│   │   │   ├── WorkflowDTO.java
│   │   │   ├── LangGraphDefinition.java
│   │   │   ├── ExecutionResult.java
│   │   │   └── PluginDTO.java          # 插件DTO
│   │   └── config/
│   │       ├── WebSocketConfig.java
│   │       ├── RedisConfig.java
│   │       ├── MyBatisPlusConfig.java
│   │       └── MinioConfig.java
│   ├── plugins/                         # 插件目录(热加载)
│   │   ├── example-tts-plugin/
│   │   │   ├── plugin.json             # 插件元数据
│   │   │   └── example-tts-plugin.jar  # 插件JAR包
│   │   └── custom-llm-plugin/
│   │       ├── plugin.json
│   │       └── custom-llm-plugin.jar
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/
│   │       ├── V1__create_workflow_tables.sql
│   │       ├── V2__create_execution_records.sql
│   │       └── V3__create_plugin_info_table.sql
│   └── pom.xml
│
├── docker/                      # Docker配置
│   ├── Dockerfile.backend       # 后端镜像构建
│   ├── Dockerfile.frontend      # 前端镜像构建(Nginx)
│   └── docker-compose.yml       # 一键部署编排文件
│
├── plugin-sdk/                  # 插件开发SDK
│   ├── src/main/java/com/bokagent/plugin/sdk/
│   │   ├── BokPlugin.java       # 插件接口
│   │   ├── PluginContext.java   # 插件上下文
│   │   └── annotations/
│   │       ├── @PluginDef       # 插件定义注解
│   │       └── @NodeProvider    # 节点提供者注解
│   └── pom.xml
│
├── sample-plugins/              # 示例插件
│   ├── azure-tts-plugin/        # Azure TTS插件示例
│   ├── baidu-llm-plugin/        # 百度文心一言插件示例
│   └── README.md                # 插件开发指南
│
└── README.md
```

---

## 核心模块设计

### 1. 插件系统设计

#### 1.1 插件接口定义 (`plugin-sdk/src/main/java/com/bokagent/plugin/sdk/BokPlugin.java`)

```java
/**
 * 插件核心接口 - 所有插件必须实现此接口
 */
public interface BokPlugin {
    
    /**
     * 获取插件元数据
     */
    PluginMetadata getMetadata();
    
    /**
     * 初始化插件
     */
    void initialize(PluginContext context);
    
    /**
     * 销毁插件
     */
    void destroy();
    
    /**
     * 获取插件提供的节点类型
     */
    List<NodeProvider> getNodeProviders();
}
```

#### 1.2 插件元数据 (`plugin-sdk/src/main/java/com/bokagent/plugin/sdk/PluginMetadata.java`)

```java
public record PluginMetadata(
    String id,              // 插件唯一ID (如: "azure-tts")
    String name,            // 插件名称 (如: "Azure TTS")
    String version,         // 版本号 (如: "1.0.0")
    String description,     // 描述
    String author,          // 作者
    List<String> tags,      // 标签 (如: ["tts", "audio"])
    Map<String, String> configSchema  // 配置项JSON Schema
) {}
```

#### 1.3 节点提供者接口 (`plugin-sdk/src/main/java/com/bokagent/plugin/sdk/NodeProvider.java`)

```java
@FunctionalInterface
public interface NodeProvider {
    
    /**
     * 创建节点实例
     * @param config 节点配置
     * @return 节点执行函数
     */
    BiFunction<Map<String, Object>, Map<String, Object>, NodeOutput> createNode(Map<String, Object> config);
    
    /**
     * 节点类型标识
     */
    String getNodeType();
    
    /**
     * 节点显示名称
     */
    String getDisplayName();
}
```

#### 1.4 插件管理器 (`backend/src/main/java/com/bokagent/plugin/PluginManager.java`)

```java
@Component
@Slf4j
public class PluginManager {
    
    private final Map<String, BokPlugin> loadedPlugins = new ConcurrentHashMap<>();
    private final Path pluginsDir = Paths.get("plugins");
    
    /**
     * 启动时扫描并加载所有插件
     */
    @PostConstruct
    public void loadAllPlugins() {
        if (!Files.exists(pluginsDir)) {
            Files.createDirectories(pluginsDir);
            return;
        }
        
        try (var stream = Files.list(pluginsDir)) {
            stream.filter(Files::isDirectory)
                .forEach(this::loadPluginFromDirectory);
        } catch (IOException e) {
            log.error("加载插件失败", e);
        }
    }
    
    /**
     * 从目录加载插件
     */
    private void loadPluginFromDirectory(Path pluginDir) {
        try {
            // 1. 读取plugin.json元数据
            PluginMetadata metadata = readPluginMetadata(pluginDir);
            
            // 2. 加载JAR包
            Path jarFile = pluginDir.resolve(metadata.id() + ".jar");
            PluginClassLoader classLoader = new PluginClassLoader(
                jarFile.toUri().toURL(),
                getClass().getClassLoader()
            );
            
            // 3. 通过SPI加载插件实现
            ServiceLoader<BokPlugin> loader = ServiceLoader.load(
                BokPlugin.class, classLoader
            );
            
            for (BokPlugin plugin : loader) {
                plugin.initialize(new PluginContext());
                loadedPlugins.put(metadata.id(), plugin);
                
                // 4. 注册插件提供的节点
                registerPluginNodes(plugin);
                
                log.info("插件加载成功: {} v{}", metadata.name(), metadata.version());
            }
        } catch (Exception e) {
            log.error("加载插件失败: {}", pluginDir, e);
        }
    }
    
    /**
     * 注册插件节点到NodeRegistry
     */
    private void registerPluginNodes(BokPlugin plugin) {
        for (NodeProvider provider : plugin.getNodeProviders()) {
            nodeRegistry.registerPluginNode(
                provider.getNodeType(),
                provider.getDisplayName(),
                provider::createNode
            );
        }
    }
    
    /**
     * 热加载单个插件（无需重启）
     */
    public void hotReloadPlugin(String pluginId) {
        unloadPlugin(pluginId);
        loadPluginFromDirectory(pluginsDir.resolve(pluginId));
    }
    
    /**
     * 卸载插件
     */
    public void unloadPlugin(String pluginId) {
        BokPlugin plugin = loadedPlugins.remove(pluginId);
        if (plugin != null) {
            plugin.destroy();
            nodeRegistry.unregisterPluginNodes(pluginId);
        }
    }
}
```

#### 1.5 插件节点包装器 (`backend/src/main/java/com/bokagent/nodes/PluginNodeWrapper.java`)

```java
@Component
public class PluginNodeWrapper extends BaseNode {
    
    private final PluginManager pluginManager;
    private final String pluginId;
    private final String nodeType;
    
    public PluginNodeWrapper(PluginManager pluginManager, 
                             String pluginId, 
                             String nodeType) {
        this.pluginManager = pluginManager;
        this.pluginId = pluginId;
        this.nodeType = nodeType;
    }
    
    @Override
    public NodeType getType() {
        return NodeType.PLUGIN; // 新增PLUGIN类型
    }
    
    @Override
    public NodeOutput execute(Map<String, Object> config, 
                              Map<String, Object> state) {
        // 从插件管理器获取节点执行函数
        var nodeFunction = pluginManager.getNodeFunction(pluginId, nodeType);
        
        if (nodeFunction == null) {
            throw new IllegalStateException(
                "插件节点未找到: " + pluginId + "/" + nodeType
            );
        }
        
        // 执行插件节点
        return nodeFunction.apply(config, state);
    }
}
```

### 2. 插件市场API

#### 2.1 插件控制器 (`backend/src/main/java/com/bokagent/controller/PluginController.java`)

```java
@RestController
@RequestMapping("/api/plugins")
@RequiredArgsConstructor
public class PluginController {
    
    private final PluginService pluginService;
    
    /**
     * 获取已安装的插件列表
     */
    @GetMapping("/installed")
    public List<PluginDTO> getInstalledPlugins() {
        return pluginService.getInstalledPlugins();
    }
    
    /**
     * 从插件市场获取可用插件
     */
    @GetMapping("/marketplace")
    public List<PluginDTO> getMarketplacePlugins(
            @RequestParam(required = false) String category) {
        return pluginService.fetchFromMarketplace(category);
    }
    
    /**
     * 安装插件
     */
    @PostMapping("/{pluginId}/install")
    public ResponseEntity<Void> installPlugin(@PathVariable String pluginId) {
        pluginService.installPlugin(pluginId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 卸载插件
     */
    @DeleteMapping("/{pluginId}")
    public ResponseEntity<Void> uninstallPlugin(@PathVariable String pluginId) {
        pluginService.uninstallPlugin(pluginId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 上传自定义插件
     */
    @PostMapping("/upload")
    public ResponseEntity<Void> uploadPlugin(
            @RequestParam("file") MultipartFile pluginJar,
            @RequestParam("metadata") String metadataJson) {
        pluginService.uploadCustomPlugin(pluginJar, metadataJson);
        return ResponseEntity.ok().build();
    }
}
```

#### 2.2 插件服务 (`backend/src/main/java/com/bokagent/service/PluginService.java`)

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class PluginService {
    
    private final PluginManager pluginManager;
    private final PluginInfoMapper pluginInfoMapper;
    private final RestTemplate restTemplate;
    
    @Value("${plugin.marketplace.url:https://plugins.bokagent.com}")
    private String marketplaceUrl;
    
    /**
     * 从插件市场获取插件列表
     */
    public List<PluginDTO> fetchFromMarketplace(String category) {
        String url = marketplaceUrl + "/api/plugins";
        if (category != null) {
            url += "?category=" + category;
        }
        
        return restTemplate.getForObject(url, List.class);
    }
    
    /**
     * 安装插件（从市场下载）
     */
    @Transactional
    public void installPlugin(String pluginId) {
        // 1. 从市场下载插件JAR和元数据
        PluginDTO pluginInfo = fetchPluginDetails(pluginId);
        
        // 2. 保存到本地plugins目录
        Path pluginDir = Paths.get("plugins", pluginId);
        Files.createDirectories(pluginDir);
        
        // 保存JAR
        byte[] jarData = downloadPluginJar(pluginId);
        Files.write(pluginDir.resolve(pluginId + ".jar"), jarData);
        
        // 保存元数据
        Files.writeString(
            pluginDir.resolve("plugin.json"),
            JsonUtils.toJson(pluginInfo)
        );
        
        // 3. 热加载插件
        pluginManager.hotReloadPlugin(pluginId);
        
        // 4. 记录到数据库
        savePluginInfo(pluginInfo);
        
        log.info("插件安装成功: {}", pluginId);
    }
    
    /**
     * 上传自定义插件
     */
    @Transactional
    public void uploadCustomPlugin(MultipartFile file, String metadataJson) {
        PluginMetadata metadata = JsonUtils.fromJson(
            metadataJson, PluginMetadata.class
        );
        
        // 保存到plugins目录
        Path pluginDir = Paths.get("plugins", metadata.id());
        Files.createDirectories(pluginDir);
        
        file.transferTo(pluginDir.resolve(metadata.id() + ".jar"));
        Files.writeString(
            pluginDir.resolve("plugin.json"),
            metadataJson
        );
        
        // 热加载
        pluginManager.hotReloadPlugin(metadata.id());
        
        // 记录到数据库
        savePluginInfo(convertToDTO(metadata));
    }
}
```

### 3. 前端插件市场组件

#### 3.1 插件市场列表 (`frontend/src/components/PluginMarket/PluginList.tsx`)

```typescript
import { List, Card, Button, Tag, message } from 'antd';
import { DownloadOutlined } from '@ant-design/icons';

export const PluginList = () => {
  const [plugins, setPlugins] = useState<Plugin[]>([]);
  
  useEffect(() => {
    fetchMarketplacePlugins().then(setPlugins);
  }, []);
  
  const handleInstall = async (pluginId: string) => {
    try {
      await installPlugin(pluginId);
      message.success('插件安装成功');
    } catch (error) {
      message.error('插件安装失败');
    }
  };
  
  return (
    <List
      grid={{ gutter: 16, column: 3 }}
      dataSource={plugins}
      renderItem={(plugin) => (
        <List.Item>
          <Card
            title={plugin.name}
            extra={<Tag>{plugin.version}</Tag>}
            actions={[
              <Button
                type="primary"
                icon={<DownloadOutlined />}
                onClick={() => handleInstall(plugin.id)}
              >
                安装
              </Button>
            ]}
          >
            <p>{plugin.description}</p>
            <div>
              {plugin.tags.map(tag => (
                <Tag key={tag}>{tag}</Tag>
              ))}
            </div>
          </Card>
        </List.Item>
      )}
    />
  );
};
```

#### 3.2 插件节点动态渲染 (`frontend/src/components/WorkflowEditor/PluginNode.tsx`)

```typescript
import { Handle, Position } from '@xyflow/react';
import { Card, Badge } from 'antd';

export const PluginNode = ({ data }: { data: NodeData }) => {
  return (
    <Badge count="Plugin" offset={[10, 10]}>
      <Card size="small" className="plugin-node">
        <Handle type="target" position={Position.Top} />
        <div className="node-header">
          🔌 {data.label}
        </div>
        <div className="node-body">
          <div>插件: {data.config.pluginId}</div>
          <div>类型: {data.config.nodeType}</div>
        </div>
        <Handle type="source" position={Position.Bottom} />
      </Card>
    </Badge>
  );
};
```

### 4. Docker一键部署配置

#### 4.1 Docker Compose编排 (`docker/docker-compose.yml`)

```yaml
version: '3.8'

services:
  # PostgreSQL - 工作流数据库
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: workflow_db
      POSTGRES_USER: ${PG_USER:-bokagent}
      POSTGRES_PASSWORD: ${PG_PASSWORD:-secret}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U bokagent"]
      interval: 10s
      timeout: 5s
      retries: 5

  # MySQL - 业务数据库
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: business_db
      MYSQL_USER: ${MYSQL_USER:-bokagent}
      MYSQL_PASSWORD: ${MYSQL_PASSWORD:-secret}
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD:-rootsecret}
    volumes:
      - mysql_data:/var/lib/mysql
    ports:
      - "3306:3306"
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Redis - 缓存
  redis:
    image: redis:7-alpine
    command: redis-server --appendonly yes
    volumes:
      - redis_data:/data
    ports:
      - "6379:6379"
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  # MinIO - 对象存储
  minio:
    image: minio/minio:latest
    command: server /data --console-address ":9001"
    environment:
      MINIO_ROOT_USER: ${MINIO_ACCESS_KEY:-minioadmin}
      MINIO_ROOT_PASSWORD: ${MINIO_SECRET_KEY:-miniosecret}
    volumes:
      - minio_data:/data
    ports:
      - "9000:9000"
      - "9001:9001"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9000/minio/health/live"]
      interval: 30s
      timeout: 20s
      retries: 3

  # 后端服务
  backend:
    build:
      context: ..
      dockerfile: docker/Dockerfile.backend
    environment:
      SPRING_PROFILES_ACTIVE: docker
      PG_HOST: postgres
      MYSQL_HOST: mysql
      REDIS_HOST: redis
      MINIO_ENDPOINT: http://minio:9000
      OPENAI_API_KEY: ${OPENAI_API_KEY}
      DEEPSEEK_API_KEY: ${DEEPSEEK_API_KEY}
      QWEN_API_KEY: ${QWEN_API_KEY}
    ports:
      - "8080:8080"
    volumes:
      - ../backend/plugins:/app/plugins  # 插件热挂载
    depends_on:
      postgres:
        condition: service_healthy
      mysql:
        condition: service_healthy
      redis:
        condition: service_healthy
      minio:
        condition: service_healthy

  # 前端服务 (Nginx)
  frontend:
    build:
      context: ..
      dockerfile: docker/Dockerfile.frontend
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  postgres_data:
  mysql_data:
  redis_data:
  minio_data:
```

#### 4.2 后端Dockerfile (`docker/Dockerfile.backend`)

```dockerfile
# 构建阶段
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY backend/pom.xml .
COPY backend/src ./src
RUN mvn clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 安装必要工具
RUN apk add --no-cache curl

# 复制JAR包
COPY --from=builder /app/target/*.jar app.jar

# 创建插件目录
RUN mkdir -p /app/plugins

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["java", \
  "-XX:+UseVirtualThreads", \
  "-jar", "app.jar"]
```

#### 4.3 前端Dockerfile (`docker/Dockerfile.frontend`)

```dockerfile
# 构建阶段
FROM node:20-alpine AS builder
WORKDIR /app
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ .
RUN npm run build

# 运行阶段 (Nginx)
FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY docker/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

#### 4.4 Nginx配置 (`docker/nginx.conf`)

```nginx
server {
    listen 80;
    server_name localhost;
    
    root /usr/share/nginx/html;
    index index.html;
    
    # 前端静态资源
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    # API代理到后端
    location /api/ {
        proxy_pass http://backend:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
    }
    
    # WebSocket代理
    location /ws {
        proxy_pass http://backend:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
    }
}
```

### 5. 插件开发示例

#### 5.1 Azure TTS插件示例 (`sample-plugins/azure-tts-plugin/src/AzureTtsPlugin.java`)

```java
@PluginDef(
    id = "azure-tts",
    name = "Azure TTS",
    version = "1.0.0",
    description = "微软Azure文本转语音服务",
    author = "BokAgent Team",
    tags = {"tts", "audio", "microsoft"}
)
public class AzureTtsPlugin implements BokPlugin {
    
    private String apiKey;
    private String region;
    
    @Override
    public PluginMetadata getMetadata() {
        return new PluginMetadata(
            "azure-tts",
            "Azure TTS",
            "1.0.0",
            "微软Azure文本转语音服务",
            "BokAgent Team",
            List.of("tts", "audio", "microsoft"),
            Map.of(
                "apiKey", "{\"type\": \"string\", \"required\": true}",
                "region", "{\"type\": \"string\", \"default\": \"eastus\"}"
            )
        );
    }
    
    @Override
    public void initialize(PluginContext context) {
        this.apiKey = context.getConfig("apiKey");
        this.region = context.getConfig("region");
    }
    
    @Override
    public List<NodeProvider> getNodeProviders() {
        return List.of(
            new NodeProvider() {
                @Override
                public BiFunction<Map<String, Object>, Map<String, Object>, NodeOutput> createNode(
                        Map<String, Object> config) {
                    return (nodeConfig, state) -> {
                        String text = (String) state.get("llm_output");
                        
                        // 调用Azure TTS API
                        byte[] audioData = callAzureTts(text, nodeConfig);
                        
                        // 上传到MinIO
                        String audioUrl = uploadToMinio(audioData);
                        
                        state.put("audio_url", audioUrl);
                        return new NodeOutput(audioUrl, "audio/mp3");
                    };
                }
                
                @Override
                public String getNodeType() {
                    return "azure-tts";
                }
                
                @Override
                public String getDisplayName() {
                    return "Azure TTS";
                }
            }
        );
    }
    
    @Override
    public void destroy() {
        // 清理资源
    }
}
```

#### 5.2 插件元数据文件 (`sample-plugins/azure-tts-plugin/plugin.json`)

```json
{
  "id": "azure-tts",
  "name": "Azure TTS",
  "version": "1.0.0",
  "description": "微软Azure文本转语音服务",
  "author": "BokAgent Team",
  "tags": ["tts", "audio", "microsoft"],
  "configSchema": {
    "apiKey": {
      "type": "string",
      "required": true,
      "description": "Azure API密钥"
    },
    "region": {
      "type": "string",
      "default": "eastus",
      "description": "Azure区域"
    }
  }
}
```

---

## 实施步骤

### 阶段1: 项目初始化 (1-2天)

**Task 1.1: 创建Spring Boot后端项目**
- 使用Spring Initializr生成项目骨架 (Spring Boot 3.5 + JDK 21)
- 配置pom.xml依赖: Spring Web, Spring AI, WebSocket, MyBatis-Plus, PostgreSQL驱动, Redis, MinIO
- 配置application.yml (双数据源、Redis、MinIO、LLM API密钥)
- 设置Flyway数据库迁移
- 配置虚拟线程池

**Task 1.2: 创建React前端项目**
- 使用Vite + TypeScript模板初始化
- 安装依赖: @xyflow/react, antd, axios, @stomp/stompjs, monaco-editor
- 配置TypeScript路径别名和ESLint规则
- 搭建基础路由和布局

**Task 1.3: 创建插件SDK模块**
- 创建独立的plugin-sdk Maven模块
- 定义BokPlugin接口、PluginMetadata、NodeProvider
- 发布到本地Maven仓库供插件开发者使用

### 阶段2: 核心引擎开发 (3-4天)

**Task 2.1: 集成LangGraph4J执行引擎**
- 实现StateGraphBuilder状态图构建器
- 开发LangGraphExecutor执行器（虚拟线程）
- 实现节点注册中心NodeRegistry
- 编写单元测试验证图的构建和执行流程

**Task 2.2: 实现插件管理系统**
- 开发PluginManager插件管理器（动态类加载）
- 实现PluginClassLoader自定义类加载器
- 开发PluginNodeWrapper插件节点包装器
- 实现插件热加载和卸载机制
- 创建plugins目录并实现启动时自动扫描

**Task 2.3: 实现React Flow → LangGraph适配层**
- 定义WorkflowDTO数据格式
- 实现AdapterService转换逻辑
- 支持PLUGIN节点类型的转换

### 阶段3: LLM和TTS集成 (2-3天)

**Task 3.1: 集成Spring AI和多厂商LLM**
- 配置Spring AI依赖
- 实现SpringAiConfig配置类
- 注册OpenAI/Deepseek/Qwen的ChatModel Bean
- 实现LLMNode节点

**Task 3.2: 集成TTS服务和MinIO对象存储**
- 实现TtsService接口和ElevenLabsTtsService
- 配置MinIO客户端
- 实现AudioStorageService
- 开发TTSNode节点

### 阶段4: 插件市场API (2天)

**Task 4.1: 实现插件管理API**
- 开发PluginController（安装/卸载/查询）
- 实现PluginService（从市场下载、上传自定义插件）
- 创建PluginInfo实体和Mapper
- 实现插件元数据持久化

**Task 4.2: 开发插件市场前端**
- 实现PluginList组件（展示可用插件）
- 实现PluginDetail组件（插件详情）
- 实现PluginInstaller组件（安装进度显示）
- 集成到主应用的侧边栏菜单

### 阶段5: 前端工作流编辑器 (3-4天)

**Task 5.1: 搭建React Flow画板**
- 配置React Flow基础组件
- 实现Canvas主画板组件
- 添加Background、Controls、MiniMap插件

**Task 5.2: 开发自定义节点组件**
- 实现StartNode、LLMNode、TTSNode、EndNode
- 实现PluginNode（插件节点动态渲染）
- 为每个节点添加输入/输出Handle

**Task 5.3: 实现节点配置面板**
- 开发ConfigPanel侧边栏组件
- 根据选中节点类型动态渲染配置表单
- 插件节点显示插件特定的配置项

### 阶段6: 调试和执行功能 (2-3天)

**Task 6.1: 实现WebSocket实时通信**
- 配置Spring Boot WebSocket
- 实现LangGraph状态流的实时推送
- 前端集成STOMP客户端

**Task 6.2: 开发调试抽屉**
- 实现DebugDrawer组件
- 开发LogViewer实时状态流显示
- 实现TestInput测试输入框

**Task 6.3: 实现工作流执行API**
- 开发ExecutionController
- 实现WorkflowExecutionService
- 异步执行工作流（虚拟线程）
- 保存执行记录

### 阶段7: Docker部署配置 (1-2天)

**Task 7.1: 编写Docker配置文件**
- 创建Dockerfile.backend（多阶段构建）
- 创建Dockerfile.frontend（Nginx托管）
- 编写docker-compose.yml编排所有服务
- 配置Nginx反向代理和WebSocket支持

**Task 7.2: 环境变量和配置管理**
- 创建.env.example模板文件
- 配置Spring Boot的docker profile
- 实现配置的外部化（数据库连接、API密钥等）

**Task 7.3: 健康检查和日志**
- 添加Spring Boot Actuator健康检查端点
- 配置Docker健康检查
- 设置日志滚动策略

### 阶段8: 示例插件开发 (2天)

**Task 8.1: 开发Azure TTS插件示例**
- 创建azure-tts-plugin项目
- 实现AzureTtsPlugin类
- 编写plugin.json元数据
- 测试插件加载和执行

**Task 8.2: 开发百度文心一言插件示例**
- 创建baidu-llm-plugin项目
- 实现百度LLM适配器
- 编写插件文档

**Task 8.3: 编写插件开发指南**
- 编写sample-plugins/README.md
- 提供快速开始模板
- 说明插件打包和发布流程

### 阶段9: 完善和优化 (2-3天)

**Task 9.1: 错误处理和用户反馈**
- 统一异常处理
- 前端友好的错误提示
- 加载状态和Skeleton屏

**Task 9.2: Redis缓存优化**
- 缓存LLM响应
- 缓存工作流配置
- 缓存插件列表

**Task 9.3: 文档和测试**
- 编写README.md（包含Docker一键部署指南）
- 补充单元测试
- 编写用户使用手册
- 编写插件开发者指南

---

## 关键技术点总结

1. **插件系统**: Java SPI + 动态类加载 + 热插拔，支持运行时安装/卸载插件
2. **LangGraph4J状态图**: 基于状态机的图编排，天然支持条件分支和循环
3. **Spring AI统一抽象**: 通过ChatClient屏蔽不同LLM厂商的API差异
4. **JDK 21虚拟线程**: 高并发执行多个工作流，无需复杂的线程池配置
5. **MinIO对象存储**: 音频文件独立存储，返回预签名URL
6. **Docker Compose**: 一键部署PostgreSQL、MySQL、Redis、MinIO、后端、前端6个服务
7. **插件市场**: 中央化插件分发，支持在线安装和自定义上传
8. **实时调试**: WebSocket推送LangGraph的中间状态

---

## 潜在风险和解决方案

| 风险 | 影响 | 解决方案 |
|------|------|----------|
| 插件安全性 | 恶意插件可能破坏系统 | 沙箱隔离、权限控制、代码签名验证 |
| 插件兼容性 | 不同插件版本冲突 | 版本依赖管理、兼容性测试 |
| Docker资源占用 | 6个容器消耗较多内存 | 限制容器资源、提供轻量模式 |
| 插件市场可用性 | 中央服务器宕机 | 本地插件缓存、离线安装支持 |
| 热加载类泄漏 | 频繁加载卸载导致PermGen溢出 | JDK 21 Metaspace自动管理、监控内存 |
| LangGraph4J成熟度 | API可能不稳定 | 准备回退到自研StateGraph方案 |

---

## 验收标准

1. ✅ 可在画板上拖拽添加4种标准节点和插件节点并连线
2. ✅ 可配置LLM节点的提供商、模型和提示词
3. ✅ 可从插件市场安装新节点类型（如Azure TTS）
4. ✅ 点击执行按钮后，工作流按LangGraph顺序执行
5. ✅ 调试抽屉实时显示每个节点的状态更新
6. ✅ 执行完成后，音频URL自动在播放器中加载并播放
7. ✅ 工作流可保存到PostgreSQL并重新加载
8. ✅ 支持切换不同的LLM提供商
9. ✅ 执行历史记录可查看和回放
10. ✅ 音频文件存储在MinIO，返回预签名URL
11. ✅ 通过`docker-compose up -d`一键启动所有服务
12. ✅ 插件支持热加载，无需重启后端服务
13. ✅ 提供至少2个示例插件（Azure TTS、百度LLM）

---

## 一键部署命令

```bash
# 1. 克隆项目
git clone https://github.com/your-org/bokagent.git
cd bokagent

# 2. 配置环境变量
cp .env.example .env
# 编辑.env文件，填入API密钥

# 3. 一键启动所有服务
docker-compose up -d

# 4. 访问应用
# 前端: http://localhost
# 后端API: http://localhost:8080
# MinIO控制台: http://localhost:9001

# 5. 查看日志
docker-compose logs -f backend

# 6. 停止服务
docker-compose down
```

---

## 下一步行动

等待您确认此计划后，我将按照以下顺序开始实施：
1. 初始化前后端项目结构和插件SDK
2. 集成LangGraph4J和Spring AI
3. 实现插件管理系统和热加载机制
4. 开发前端可视化编辑器和插件市场
5. 配置Docker Compose一键部署
6. 开发示例插件
7. 完善测试和文档

请确认是否开始执行，或提出需要调整的地方。