# AI Agent工作流编排系统实施计划 (Spring AI + LangGraph4J版)

## 技术栈总览

**前端**: React 18 + TypeScript + Vite + React Flow (@xyflow/react) + Ant Design 5 + Monaco Editor  
**后端**: Spring Boot 3.5 + JDK 21 + Spring AI 1.1 + LangGraph4J + MyBatis-Plus 3.5 + WebSocket  
**数据库**: PostgreSQL 15+ (工作流数据存储) + MySQL 8+ (业务数据存储)  
**缓存**: Redis 7 (分布式缓存、会话管理)  
**对象存储**: MinIO (音频文件存储)  
**工作流引擎**: LangGraph4J (Agent编排框架) + Spring AI (LLM统一抽象层)  
**部署架构**: 前后端分离，前端静态资源独立部署，后端提供REST API + WebSocket

---

## 项目目录结构

```
bokagent/
├── frontend/                    # React前端项目
│   ├── src/
│   │   ├── components/
│   │   │   ├── WorkflowEditor/  # 工作流编辑器核心组件
│   │   │   │   ├── Canvas.tsx          # 主画板组件(React Flow)
│   │   │   │   ├── NodeLibrary.tsx     # 左侧节点库
│   │   │   │   ├── CustomNodes/        # 自定义节点类型
│   │   │   │   │   ├── StartNode.tsx
│   │   │   │   │   ├── LLMNode.tsx
│   │   │   │   │   ├── TTSNode.tsx
│   │   │   │   │   └── EndNode.tsx
│   │   │   │   ├── ConfigPanel.tsx     # 节点配置面板(Ant Design表单)
│   │   │   │   └── Toolbar.tsx         # 工具栏（保存/加载/执行）
│   │   │   ├── DebugDrawer/           # 调试抽屉组件
│   │   │   │   ├── DebugPanel.tsx
│   │   │   │   ├── LogViewer.tsx       # 实时状态流显示
│   │   │   │   └── TestInput.tsx       # 测试输入框
│   │   │   └── AudioPlayer/           # AI播客播放器
│   │   │       └── PodcastPlayer.tsx   # HTML5 Audio播放器
│   │   ├── services/
│   │   │   ├── api.ts                  # Axios API调用封装
│   │   │   └── websocket.ts            # STOMP WebSocket连接管理
│   │   ├── types/
│   │   │   ├── workflow.ts             # 工作流类型定义
│   │   │   └── node.ts                 # 节点类型定义
│   │   ├── hooks/
│   │   │   ├── useWorkflowExecution.ts # 工作流执行Hook
│   │   │   └── useDebugStream.ts       # 调试流Hook(STOMP订阅)
│   │   └── App.tsx
│   ├── package.json
│   └── vite.config.ts
│
├── backend/                     # Spring Boot后端项目
│   ├── src/main/java/com/bokagent/
│   │   ├── controller/
│   │   │   ├── WorkflowController.java      # 工作流CRUD API
│   │   │   ├── ExecutionController.java     # 工作流执行API
│   │   │   └── DebugWebSocketController.java # WebSocket调试接口
│   │   ├── service/
│   │   │   ├── WorkflowService.java         # 工作流管理服务(MyBatis-Plus)
│   │   │   ├── WorkflowExecutionService.java # 工作流执行服务
│   │   │   └── AdapterService.java          # React Flow → LangGraph转换服务
│   │   ├── engine/
│   │   │   ├── LangGraphExecutor.java       # LangGraph执行器核心(虚拟线程)
│   │   │   ├── StateGraphBuilder.java       # 状态图构建器
│   │   │   ├── NodeRegistry.java            # 节点注册中心
│   │   │   └── ExecutionContext.java        # 执行上下文(Record类型)
│   │   ├── nodes/
│   │   │   ├── BaseNode.java                # 节点基类
│   │   │   ├── StartNode.java               # 用户输入节点
│   │   │   ├── LLMNode.java                 # 大模型处理节点(Spring AI ChatClient)
│   │   │   ├── TTSNode.java                 # 音频合成节点(MinIO上传)
│   │   │   └── EndNode.java                 # 结束节点
│   │   ├── llm/
│   │   │   ├── SpringAiConfig.java          # Spring AI配置类
│   │   │   ├── OpenAiConfig.java            # OpenAI ChatModel Bean
│   │   │   ├── DeepseekConfig.java          # Deepseek ChatModel Bean
│   │   │   └── QwenConfig.java              # 通义千问ChatModel Bean
│   │   ├── tts/
│   │   │   ├── TtsService.java              # TTS服务接口
│   │   │   └── ElevenLabsTtsService.java    # ElevenLabs实现
│   │   ├── storage/
│   │   │   ├── MinioService.java            # MinIO对象存储服务
│   │   │   └── AudioStorageService.java     # 音频文件管理服务(预签名URL)
│   │   ├── model/
│   │   │   ├── Workflow.java                # 工作流实体(MyBatis-Plus @TableName)
│   │   │   ├── WorkflowNode.java            # 节点实体
│   │   │   ├── WorkflowEdge.java            # 边实体
│   │   │   └── ExecutionRecord.java         # 执行记录实体
│   │   ├── mapper/
│   │   │   ├── WorkflowMapper.java          # MyBatis-Plus BaseMapper
│   │   │   └── ExecutionRecordMapper.java
│   │   ├── dto/
│   │   │   ├── WorkflowDTO.java             # React Flow格式DTO
│   │   │   ├── LangGraphDefinition.java     # LangGraph内部格式(Record)
│   │   │   └── ExecutionResult.java         # 执行结果DTO
│   │   └── config/
│   │       ├── WebSocketConfig.java         # WebSocket配置
│   │       ├── RedisConfig.java             # Redis配置(RedisTemplate)
│   │       ├── MyBatisPlusConfig.java       # MyBatis-Plus分页插件
│   │       └── MinioConfig.java             # MinIO客户端配置
│   ├── src/main/resources/
│   │   ├── application.yml                  # 双数据源、Redis、MinIO、LLM配置
│   │   └── db/migration/                    # Flyway迁移脚本
│   │       ├── V1__create_workflow_tables.sql
│   │       └── V2__create_execution_records.sql
│   └── pom.xml
│
└── README.md
```

---

## 核心模块设计

### 1. 前端工作流编辑器 (React Flow)

#### 1.1 节点类型定义 (`frontend/src/types/node.ts`)

```typescript
export enum NodeType {
  START = 'start',      // 用户输入节点
  LLM = 'llm',          // 大模型处理节点
  TTS = 'tts',          // 音频合成节点
  END = 'end'           // 结束节点
}

export interface NodeData {
  label: string;
  config: Record<string, any>;
  output?: any;
}

export interface WorkflowGraph {
  nodes: Array<{
    id: string;
    type: NodeType;
    position: { x: number; y: number };
    data: NodeData;
  }>;
  edges: Array<{
    id: string;
    source: string;
    target: string;
    sourceHandle?: string;
    targetHandle?: string;
  }>;
}
```

#### 1.2 自定义LLM节点组件 (`frontend/src/components/WorkflowEditor/CustomNodes/LLMNode.tsx`)

```typescript
import { Handle, Position } from '@xyflow/react';
import { Card, Tag } from 'antd';

export const LLMNode = ({ data }: { data: NodeData }) => {
  return (
    <Card size="small" className="llm-node">
      <Handle type="target" position={Position.Top} />
      <div className="node-header">
        <Tag color="blue">LLM</Tag>
        {data.label}
      </div>
      <div className="node-body">
        <div>模型: {data.config.model}</div>
        <div>提供商: {data.config.provider}</div>
      </div>
      <Handle type="source" position={Position.Bottom} />
    </Card>
  );
};
```

#### 1.3 主画板组件 (`frontend/src/components/WorkflowEditor/Canvas.tsx`)

```typescript
import { ReactFlow, useNodesState, useEdgesState, addEdge } from '@xyflow/react';
import { nodeTypes } from './CustomNodes';

export const Canvas = () => {
  const [nodes, setNodes, onNodesChange] = useNodesState([]);
  const [edges, setEdges, onEdgesChange] = useEdgesState([]);

  const onConnect = (params) => {
    setEdges((eds) => addEdge(params, eds));
  };

  return (
    <ReactFlow
      nodes={nodes}
      edges={edges}
      onNodesChange={onNodesChange}
      onEdgesChange={onEdgesChange}
      onConnect={onConnect}
      nodeTypes={nodeTypes}
      fitView
    >
      <Background />
      <Controls />
      <MiniMap />
    </ReactFlow>
  );
};
```

### 2. 后端LangGraph执行引擎

#### 2.1 LangGraph图定义 (`backend/src/main/java/com/bokagent/dto/LangGraphDefinition.java`)

```java
public record LangGraphDefinition(
    List<NodeDefinition> nodes,
    List<EdgeDefinition> edges,
    Map<String, Object> initialState
) {
    public record NodeDefinition(
        String id,
        String type, // START, LLM, TTS, END
        Map<String, Object> config
    ) {}
    
    public record EdgeDefinition(
        String source,
        String target,
        String condition // 可选，用于条件分支
    ) {}
}
```

#### 2.2 状态图构建器 (`backend/src/main/java/com/bokagent/engine/StateGraphBuilder.java`)

```java
@Component
@RequiredArgsConstructor
public class StateGraphBuilder {
    
    private final NodeRegistry nodeRegistry;
    
    /**
     * 将LangGraphDefinition转换为可执行的状态图
     */
    public CompiledGraph build(LangGraphDefinition definition) {
        // 使用LangGraph4J的StateGraph API
        StateGraph<Map<String, Object>> graph = new StateGraph<>();
        
        // 注册所有节点
        for (NodeDefinition nodeDef : definition.nodes()) {
            BaseNode node = nodeRegistry.getNode(nodeDef.type());
            graph.addNode(nodeDef.id(), (state) -> {
                NodeOutput output = node.execute(nodeDef.config(), state);
                state.put(nodeDef.id(), output);
                return state;
            });
        }
        
        // 添加边（连接）
        for (EdgeDefinition edge : definition.edges()) {
            if (edge.condition() != null) {
                graph.addConditionalEdges(edge.source(), Map.of(
                    edge.condition(), edge.target()
                ));
            } else {
                graph.addEdge(edge.source(), edge.target());
            }
        }
        
        // 设置入口和出口
        graph.setEntryPoint(findNodeByType(definition, "START"));
        graph.setFinishPoint(findNodeByType(definition, "END"));
        
        return graph.compile();
    }
}
```

#### 2.3 LangGraph执行器 (`backend/src/main/java/com/bokagent/engine/LangGraphExecutor.java`)

```java
@Service
@RequiredArgsConstructor
public class LangGraphExecutor {
    
    private final StateGraphBuilder graphBuilder;
    private final SimpMessagingTemplate messagingTemplate;
    private final ExecutorService virtualThreadExecutor; // JDK 21虚拟线程
    
    /**
     * 异步执行工作流（利用JDK 21虚拟线程）
     */
    @Async
    public CompletableFuture<ExecutionResult> execute(
            LangGraphDefinition definition, 
            Map<String, Object> input) {
        
        return CompletableFuture.supplyAsync(() -> {
            // 1. 构建状态图
            CompiledGraph graph = graphBuilder.build(definition);
            
            // 2. 初始化状态
            Map<String, Object> initialState = new HashMap<>(input);
            initialState.put("executionId", UUID.randomUUID().toString());
            
            // 3. 执行图并流式获取中间状态
            Stream<Map<String, Object>> stateStream = graph.stream(initialState);
            
            // 4. 监听状态变化并推送到前端
            Map<String, Object> finalState = null;
            for (Map<String, Object> state : stateStream.toList()) {
                sendDebugUpdate(state);
                finalState = state;
            }
            
            // 5. 提取最终结果
            return extractResult(finalState);
        }, virtualThreadExecutor);
    }
    
    private void sendDebugUpdate(Map<String, Object> state) {
        messagingTemplate.convertAndSend("/topic/debug/state", 
            new DebugStateUpdate(state, Instant.now()));
    }
}
```

### 3. 节点实现

#### 3.1 节点基类 (`backend/src/main/java/com/bokagent/nodes/BaseNode.java`)

```java
public abstract class BaseNode {
    
    public abstract NodeType getType();
    
    /**
     * 执行节点逻辑
     * @param config 节点配置
     * @param state 当前执行状态(包含上游节点输出)
     */
    public abstract NodeOutput execute(Map<String, Object> config, 
                                       Map<String, Object> state);
}
```

#### 3.2 LLM节点实现 (Spring AI集成) (`backend/src/main/java/com/bokagent/nodes/LLMNode.java`)

```java
@Component
@RequiredArgsConstructor
public class LLMNode extends BaseNode {
    
    private final ChatClient chatClient; // Spring AI的ChatClient
    
    @Override
    public NodeType getType() {
        return NodeType.LLM;
    }
    
    @Override
    public NodeOutput execute(Map<String, Object> config, Map<String, Object> state) {
        String promptTemplate = (String) config.get("prompt");
        
        // 从状态中获取上游输入
        String userInput = (String) state.get("user_input");
        
        // 构建完整提示词
        String fullPrompt = promptTemplate.replace("{input}", userInput);
        
        // 使用Spring AI调用LLM（自动处理多厂商适配）
        Prompt prompt = new Prompt(fullPrompt);
        ChatResponse response = chatClient.call(prompt);
        String result = response.getResult().getOutput().getContent();
        
        // 将结果存入状态
        state.put("llm_output", result);
        
        return new NodeOutput(result);
    }
}
```

#### 3.3 TTS节点实现 (MinIO存储) (`backend/src/main/java/com/bokagent/nodes/TTSNode.java`)

```java
@Component
@RequiredArgsConstructor
public class TTSNode extends BaseNode {
    
    private final TtsService ttsService;
    private final AudioStorageService storageService;
    
    @Override
    public NodeType getType() {
        return NodeType.TTS;
    }
    
    @Override
    public NodeOutput execute(Map<String, Object> config, Map<String, Object> state) {
        // 获取上游LLM的输出
        String text = (String) state.get("llm_output");
        
        // 调用TTS服务生成音频
        byte[] audioData = ttsService.synthesize(text, config);
        
        // 上传到MinIO对象存储
        String objectKey = storageService.uploadAudio(audioData, "mp3");
        
        // 返回预签名URL（有效期1小时）
        String audioUrl = storageService.getPresignedUrl(objectKey, Duration.ofHours(1));
        
        state.put("audio_url", audioUrl);
        
        return new NodeOutput(audioUrl, "audio/mp3");
    }
}
```

### 4. Spring AI多厂商LLM配置

#### 4.1 Spring AI配置类 (`backend/src/main/java/com/bokagent/llm/SpringAiConfig.java`)

```java
@Configuration
public class SpringAiConfig {
    
    @Bean
    @Primary
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
    
    /**
     * OpenAI ChatModel
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.ai.openai")
    public OpenAiChatModel openAiChatModel() {
        return new OpenAiChatModel();
    }
    
    /**
     * Deepseek ChatModel (通过OpenAI兼容接口)
     */
    @Bean
    public OpenAiChatModel deepseekChatModel(
            @Value("${spring.ai.deepseek.api-key}") String apiKey) {
        return OpenAiChatModel.builder()
            .apiKey(apiKey)
            .baseUrl("https://api.deepseek.com/v1")
            .modelName("deepseek-chat")
            .build();
    }
    
    /**
     * 通义千问 ChatModel (通过OpenAI兼容接口)
     */
    @Bean
    public OpenAiChatModel qwenChatModel(
            @Value("${spring.ai.qwen.api-key}") String apiKey) {
        return OpenAiChatModel.builder()
            .apiKey(apiKey)
            .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
            .modelName("qwen-plus")
            .build();
    }
}
```

#### 4.2 application.yml配置示例

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      base-url: https://api.openai.com/v1
      chat:
        options:
          model: gpt-4
    
    deepseek:
      api-key: ${DEEPSEEK_API_KEY}
    
    qwen:
      api-key: ${QWEN_API_KEY}

minio:
  endpoint: http://localhost:9000
  access-key: ${MINIO_ACCESS_KEY}
  secret-key: ${MINIO_SECRET_KEY}
  bucket-name: audio-files

redis:
  host: localhost
  port: 6379

datasource:
  workflow:
    url: jdbc:postgresql://localhost:5432/workflow_db
    username: ${PG_USER}
    password: ${PG_PASSWORD}
  business:
    url: jdbc:mysql://localhost:3306/business_db
    username: ${MYSQL_USER}
    password: ${MYSQL_PASSWORD}
```

### 5. React Flow → LangGraph适配层

#### 5.1 转换服务 (`backend/src/main/java/com/bokagent/service/AdapterService.java`)

```java
@Service
public class AdapterService {
    
    /**
     * 将前端React Flow格式转换为LangGraph定义
     */
    public LangGraphDefinition convertFromReactFlow(WorkflowDTO workflowDTO) {
        // 转换节点
        List<NodeDefinition> nodes = workflowDTO.getNodes().stream()
            .map(node -> new NodeDefinition(
                node.getId(),
                node.getType().toUpperCase(),
                node.getData().getConfig()
            ))
            .toList();
        
        // 转换边
        List<EdgeDefinition> edges = workflowDTO.getEdges().stream()
            .map(edge -> new EdgeDefinition(
                edge.getSource(),
                edge.getTarget(),
                null // 暂不支持条件边
            ))
            .toList();
        
        return new LangGraphDefinition(nodes, edges, Map.of());
    }
}
```

### 6. WebSocket实时调试

#### 6.1 前端WebSocket Hook (`frontend/src/hooks/useDebugStream.ts`)

```typescript
import { useEffect, useState } from 'react';
import { Client } from '@stomp/stompjs';

export const useDebugStream = () => {
  const [stateUpdates, setStateUpdates] = useState<any[]>([]);
  
  useEffect(() => {
    const client = new Client({
      brokerURL: 'ws://localhost:8080/ws',
      onConnect: () => {
        client.subscribe('/topic/debug/state', (message) => {
          const update = JSON.parse(message.body);
          setStateUpdates(prev => [...prev, update]);
        });
      }
    });
    
    client.activate();
    return () => client.deactivate();
  }, []);
  
  return stateUpdates;
};
```

### 7. 数据库设计

#### 7.1 工作流表 (PostgreSQL) (`V1__create_workflow_tables.sql`)

```sql
CREATE TABLE workflows (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    graph_data JSONB NOT NULL,  -- 存储React Flow格式的JSON
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE workflow_nodes (
    id BIGSERIAL PRIMARY KEY,
    workflow_id BIGINT REFERENCES workflows(id) ON DELETE CASCADE,
    node_id VARCHAR(100) NOT NULL,
    node_type VARCHAR(50) NOT NULL,
    position_x DOUBLE PRECISION,
    position_y DOUBLE PRECISION,
    config JSONB
);

CREATE TABLE workflow_edges (
    id BIGSERIAL PRIMARY KEY,
    workflow_id BIGINT REFERENCES workflows(id) ON DELETE CASCADE,
    edge_id VARCHAR(100) NOT NULL,
    source_node_id VARCHAR(100) NOT NULL,
    target_node_id VARCHAR(100) NOT NULL
);

CREATE INDEX idx_workflows_created_at ON workflows(created_at DESC);
```

#### 7.2 执行记录表 (PostgreSQL) (`V2__create_execution_records.sql`)

```sql
CREATE TABLE execution_records (
    id BIGSERIAL PRIMARY KEY,
    workflow_id BIGINT REFERENCES workflows(id),
    status VARCHAR(20) NOT NULL,  -- RUNNING, SUCCESS, FAILED
    input_data JSONB,
    output_data JSONB,
    audio_url TEXT,  -- MinIO存储的音频URL
    error_message TEXT,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    duration_ms BIGINT  -- 执行耗时（毫秒）
);

CREATE INDEX idx_execution_records_workflow_id ON execution_records(workflow_id);
CREATE INDEX idx_execution_records_started_at ON execution_records(started_at DESC);
```

### 8. API接口设计

#### 8.1 工作流管理API

```
POST   /api/workflows          - 创建工作流
GET    /api/workflows          - 获取工作流列表（分页）
GET    /api/workflows/{id}     - 获取工作流详情
PUT    /api/workflows/{id}     - 更新工作流
DELETE /api/workflows/{id}     - 删除工作流
```

#### 8.2 工作流执行API

```
POST   /api/executions         - 执行工作流
GET    /api/executions/{id}    - 获取执行结果
GET    /api/executions         - 获取执行历史（分页）
```

请求示例:
```json
POST /api/executions
{
  "workflowId": 1,
  "input": {
    "user_input": "请帮我生成一段关于AI的介绍"
  }
}
```

响应示例:
```json
{
  "executionId": "exec-20260605-001",
  "status": "SUCCESS",
  "output": {
    "audioUrl": "https://minio.example.com/audio/exec-001.mp3?X-Amz-...",
    "contentType": "audio/mp3",
    "durationMs": 3500
  }
}
```

---

## 实施步骤

### 阶段1: 项目初始化 (1-2天)

**Task 1.1: 创建Spring Boot后端项目**
- 使用Spring Initializr生成项目骨架 (Spring Boot 3.5 + JDK 21)
- 配置pom.xml依赖: Spring Web, Spring AI (openai-spring-boot-starter), WebSocket, MyBatis-Plus, PostgreSQL驱动, Redis, MinIO
- 配置application.yml (双数据源、Redis、MinIO、LLM API密钥)
- 设置Flyway数据库迁移
- 配置虚拟线程池 (`spring.task.execution.executor-type=virtual`)

**Task 1.2: 创建React前端项目**
- 使用Vite + TypeScript模板初始化
- 安装依赖: @xyflow/react, antd, axios, @stomp/stompjs, monaco-editor
- 配置TypeScript路径别名和ESLint规则
- 搭建基础路由和布局（Ant Design Layout）

### 阶段2: 核心引擎开发 (3-4天)

**Task 2.1: 集成LangGraph4J执行引擎**
- 引入LangGraph4J依赖（或手动实现StateGraph核心逻辑）
- 实现StateGraphBuilder状态图构建器
- 开发LangGraphExecutor执行器（利用JDK 21虚拟线程）
- 实现节点注册中心NodeRegistry
- 编写单元测试验证图的构建和执行流程

**Task 2.2: 实现节点注册系统**
- 创建BaseNode基类和NodeType枚举
- 开发StartNode、EndNode基础节点
- 实现节点执行的函数式接口

**Task 2.3: 实现React Flow → LangGraph适配层**
- 定义WorkflowDTO数据格式
- 实现AdapterService转换逻辑
- 编写转换规则的单元测试
- 处理边界情况（孤立节点检测）

### 阶段3: LLM和TTS集成 (2-3天)

**Task 3.1: 集成Spring AI和多厂商LLM**
- 配置Spring AI依赖 (spring-ai-openai-spring-boot-starter)
- 实现SpringAiConfig配置类，注册OpenAI/Deepseek/Qwen的ChatModel Bean
- 在application.yml中配置各厂商API密钥和baseUrl
- 测试Spring AI的ChatClient调用各提供商
- 实现LLMNode节点，注入ChatClient执行LLM调用

**Task 3.2: 实现LLM节点配置面板**
- 前端开发LLM节点配置表单（Ant Design Form）
- 支持下拉选择提供商（OpenAI/Deepseek/Qwen）
- 支持输入模型名称、温度、提示词模板
- 使用Monaco Editor编辑复杂提示词

**Task 3.3: 集成TTS服务和MinIO对象存储**
- 选择TTS提供商（推荐ElevenLabs或Azure TTS）
- 实现TtsService接口和ElevenLabsTtsService实现类
- 配置MinIO客户端 (MinioService)
- 实现AudioStorageService，支持音频上传和预签名URL生成
- 开发TTSNode节点，上传音频到MinIO并返回URL

### 阶段4: 前端工作流编辑器 (3-4天)

**Task 4.1: 搭建React Flow画板**
- 配置React Flow基础组件
- 实现Canvas主画板组件
- 添加Background、Controls、MiniMap插件
- 实现节点的拖拽添加功能

**Task 4.2: 开发自定义节点组件**
- 实现StartNode（用户输入节点）
- 实现LLMNode（大模型节点，显示模型配置）
- 实现TTSNode（音频合成节点，显示音色配置）
- 实现EndNode（结束节点）
- 为每个节点添加输入/输出Handle

**Task 4.3: 实现节点配置面板**
- 开发ConfigPanel侧边栏组件（Ant Design Drawer）
- 根据选中节点类型动态渲染配置表单
- LLM节点配置：选择提供商、模型、温度、提示词模板（Monaco Editor）
- TTS节点配置：选择音色、语速、音量
- 实现配置的实时更新和验证

**Task 4.4: 实现左侧节点库**
- 开发NodeLibrary组件（Ant Design Menu）
- 实现从库中拖拽节点到画板的功能
- 添加节点分类和搜索功能
- 美化节点图标和描述

### 阶段5: 调试和执行功能 (2-3天)

**Task 5.1: 实现WebSocket实时通信**
- 配置Spring Boot WebSocket (@EnableWebSocketMessageBroker)
- 创建DebugWebSocketController
- 实现LangGraph状态流的实时推送
- 前端集成STOMP客户端

**Task 5.2: 开发调试抽屉**
- 实现DebugDrawer组件（Ant Design Drawer右侧滑出）
- 开发LogViewer实时状态流显示组件
- 实现TestInput测试输入框
- 添加执行按钮触发工作流运行
- 显示执行状态和耗时统计

**Task 5.3: 实现工作流执行API**
- 开发ExecutionController
- 实现WorkflowExecutionService
- 异步执行工作流（@Async + 虚拟线程）
- 保存执行记录到PostgreSQL（MyBatis-Plus）
- 返回执行结果（包含MinIO音频URL）

### 阶段6: AI播客播放器 (1-2天)

**Task 6.1: 开发音频播放器组件**
- 实现PodcastPlayer组件（HTML5 Audio标签）
- 支持播放MinIO预签名URL的音频
- 添加播放/暂停/停止控制
- 显示播放进度条和时间戳
- 美化播放器UI（Ant Design Slider）

**Task 6.2: 集成到调试流程**
- 在工作流执行完成后自动加载音频URL并播放
- 支持手动重播
- 添加音频下载功能（a标签download属性）

### 阶段7: 完善和优化 (2-3天)

**Task 7.1: 工作流持久化**
- 实现工作流的保存和加载功能（MyBatis-Plus CRUD）
- 优化JSONB存储格式
- 添加执行历史查询（分页）

**Task 7.2: Redis缓存优化**
- 缓存LLM响应（避免重复调用）
- 缓存工作流配置（减少数据库查询）
- 实现会话状态管理

**Task 7.3: 错误处理和用户反馈**
- 统一异常处理（@ControllerAdvice）
- 前端添加友好的错误提示（Ant Design message.error）
- 实现加载状态和Skeleton屏
- 添加操作成功Toast通知（Ant Design message.success）

**Task 7.4: 文档和测试**
- 编写README.md（项目介绍、启动指南、API文档）
- 补充关键模块的单元测试（JUnit 5 + Mockito）
- 编写用户使用手册（截图+操作步骤）

---

## 关键技术点总结

1. **LangGraph4J状态图**: 基于状态机的图编排，天然支持条件分支和循环，比DAG更灵活
2. **Spring AI统一抽象**: 通过ChatClient屏蔽不同LLM厂商的API差异，配置化切换
3. **JDK 21虚拟线程**: 高并发执行多个工作流，无需复杂的线程池配置
4. **MinIO对象存储**: 音频文件独立存储，返回预签名URL，避免Base64传输开销
5. **MyBatis-Plus**: 简化CRUD操作，支持PostgreSQL的JSONB字段查询
6. **Redis缓存**: 缓存LLM响应、会话状态，提升性能
7. **实时调试**: WebSocket推送LangGraph的中间状态，前端实时显示执行进度

---

## 潜在风险和解决方案

| 风险 | 影响 | 解决方案 |
|------|------|----------|
| LangGraph4J成熟度 | API可能不稳定 | 关注官方版本，准备回退到自研StateGraph方案 |
| Spring AI版本兼容 | Spring Boot 3.5可能需要最新Spring AI | 锁定兼容版本组合，参考官方兼容性矩阵 |
| TTS服务成本 | 音频生成长文本费用高 | 添加配额管理，支持本地TTS引擎备选，音频缓存 |
| MinIO存储成本 | 大量音频文件占用空间 | 设置生命周期策略，定期清理过期音频 |
| 虚拟线程调试困难 | 传统线程dump不适用 | 使用JDK 21新的调试工具，添加详细日志 |
| WebSocket连接断开 | 调试信息丢失 | 实现断线重连，前端缓冲未接收的状态更新 |

---

## 验收标准

1. ✅ 可在画板上拖拽添加4种标准节点并连线
2. ✅ 可配置LLM节点的提供商、模型和提示词（Monaco Editor）
3. ✅ 可配置TTS节点的音色参数
4. ✅ 点击执行按钮后，工作流按LangGraph顺序执行
5. ✅ 调试抽屉实时显示每个节点的状态更新
6. ✅ 执行完成后，音频URL自动在播放器中加载并播放
7. ✅ 工作流可保存到PostgreSQL并重新加载
8. ✅ 支持切换不同的LLM提供商（OpenAI/Deepseek/通义千问）
9. ✅ 执行历史记录可查看和回放
10. ✅ 音频文件存储在MinIO，返回预签名URL

---

## 下一步行动

等待您确认此计划后，我将按照以下顺序开始实施：
1. 初始化前后端项目结构
2. 集成LangGraph4J和Spring AI
3. 实现节点系统和MinIO存储
4. 开发前端可视化编辑器
5. 实现调试和播放功能
6. 完善测试和文档

请确认是否开始执行，或提出需要调整的地方。