# AI Agent工作流编排系统实施计划 (MCP协议 + 工具注册系统版)

## 技术栈总览

**前端**: React 18 + TypeScript + Vite + React Flow (@xyflow/react) + Ant Design 5 + Monaco Editor  
**后端**: Spring Boot 3.5 + JDK 21 + Spring AI 1.1 + LangGraph4J + MyBatis-Plus 3.5 + WebSocket  
**数据库**: PostgreSQL 15+ (工作流数据存储) + MySQL 8+ (业务数据存储)  
**缓存**: Redis 7 (分布式缓存、会话管理)  
**对象存储**: MinIO (音频文件存储)  
**工作流引擎**: LangGraph4J (Agent编排框架) + Spring AI (LLM统一抽象层)  
**MCP协议**: Model Context Protocol (双向支持: Server + Client)  
**工具系统**: 工具注册中心 + 工作流节点工具 + LLM Function Calling工具  
**插件系统**: Java SPI + 动态类加载 + 插件市场API  
**容器化**: Docker + Docker Compose (一键部署全栈服务)  
**部署架构**: 前后端分离，Docker Compose编排所有服务

---

## 项目目录结构 (完整版)

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
│   │   │   │   │   ├── ToolNode.tsx      # 工具调用节点
│   │   │   │   │   └── EndNode.tsx
│   │   │   │   ├── PluginNode.tsx
│   │   │   │   ├── ConfigPanel.tsx
│   │   │   │   └── Toolbar.tsx
│   │   │   ├── DebugDrawer/
│   │   │   │   ├── DebugPanel.tsx
│   │   │   │   ├── LogViewer.tsx
│   │   │   │   └── TestInput.tsx
│   │   │   ├── AudioPlayer/
│   │   │   │   └── PodcastPlayer.tsx
│   │   │   ├── PluginMarket/
│   │   │   │   ├── PluginList.tsx
│   │   │   │   ├── PluginDetail.tsx
│   │   │   │   └── PluginInstaller.tsx
│   │   │   └── ToolMarket/              # 工具市场组件(新增)
│   │   │       ├── ToolList.tsx
│   │   │       ├── ToolDetail.tsx
│   │   │       └── ToolConfigurator.tsx
│   │   ├── services/
│   │   │   ├── api.ts
│   │   │   ├── websocket.ts
│   │   │   ├── pluginApi.ts
│   │   │   └── toolApi.ts               # 工具管理API(新增)
│   │   ├── types/
│   │   │   ├── workflow.ts
│   │   │   ├── node.ts
│   │   │   ├── plugin.ts
│   │   │   └── tool.ts                  # 工具类型定义(新增)
│   │   ├── hooks/
│   │   │   ├── useWorkflowExecution.ts
│   │   │   ├── useDebugStream.ts
│   │   │   ├── usePluginLoader.ts
│   │   │   └── useToolRegistry.ts       # 工具注册Hook(新增)
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
│   │   │   ├── PluginController.java
│   │   │   ├── ToolController.java      # 工具管理API(新增)
│   │   │   └── McpController.java       # MCP协议端点(新增)
│   │   ├── service/
│   │   │   ├── WorkflowService.java
│   │   │   ├── WorkflowExecutionService.java
│   │   │   ├── AdapterService.java
│   │   │   ├── PluginService.java
│   │   │   └── ToolService.java         # 工具管理服务(新增)
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
│   │   │   ├── ToolNode.java            # 工具调用节点(新增)
│   │   │   ├── EndNode.java
│   │   │   └── PluginNodeWrapper.java
│   │   ├── plugin/
│   │   │   ├── PluginManager.java
│   │   │   ├── PluginRegistry.java
│   │   │   ├── BokPlugin.java
│   │   │   ├── PluginClassLoader.java
│   │   │   └── PluginMetadata.java
│   │   ├── tools/                       # 工具系统核心(新增)
│   │   │   ├── ToolRegistry.java        # 工具注册中心
│   │   │   ├── ToolDefinition.java      # 工具定义(Record)
│   │   │   ├── ToolExecutor.java        # 工具执行器
│   │   │   ├── BuiltinTools/            # 内置工具
│   │   │   │   ├── WebSearchTool.java   # 网络搜索
│   │   │   │   ├── CalculatorTool.java  # 计算器
│   │   │   │   ├── DateTimeTool.java    # 日期时间
│   │   │   │   └── HttpApiTool.java     # HTTP API调用
│   │   │   └── FunctionCallingAdapter.java # Function Calling适配器
│   │   ├── mcp/                         # MCP协议实现(新增)
│   │   │   ├── McpServer.java           # MCP Server实现
│   │   │   ├── McpClient.java           # MCP Client实现
│   │   │   ├── McpResource.java         # MCP资源定义
│   │   │   ├── McpTool.java             # MCP工具定义
│   │   │   ├── McpPrompt.java           # MCP提示词定义
│   │   │   ├── transport/               # 传输层
│   │   │   │   ├── StdioTransport.java  # STDIO传输
│   │   │   │   ├── SseTransport.java    # SSE传输
│   │   │   │   └── WebSocketTransport.java # WebSocket传输
│   │   │   └── protocol/                # 协议消息
│   │   │       ├── McpMessage.java      # MCP消息基类
│   │   │       ├── InitializeRequest.java
│   │   │       ├── ListToolsRequest.java
│   │   │       └── CallToolRequest.java
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
│   │   │   ├── PluginInfo.java
│   │   │   └── ToolInfo.java            # 工具信息实体(新增)
│   │   ├── mapper/
│   │   │   ├── WorkflowMapper.java
│   │   │   ├── ExecutionRecordMapper.java
│   │   │   ├── PluginInfoMapper.java
│   │   │   └── ToolInfoMapper.java      # 工具信息Mapper(新增)
│   │   ├── dto/
│   │   │   ├── WorkflowDTO.java
│   │   │   ├── LangGraphDefinition.java
│   │   │   ├── ExecutionResult.java
│   │   │   ├── PluginDTO.java
│   │   │   └── ToolDTO.java             # 工具DTO(新增)
│   │   └── config/
│   │       ├── WebSocketConfig.java
│   │       ├── RedisConfig.java
│   │       ├── MyBatisPlusConfig.java
│   │       ├── MinioConfig.java
│   │       └── McpConfig.java           # MCP配置(新增)
│   ├── tools/                           # 外部工具脚本目录(新增)
│   │   ├── python/                      # Python工具
│   │   │   ├── data_analysis.py
│   │   │   └── image_processing.py
│   │   └── shell/                       # Shell工具
│   │       ├── system_info.sh
│   │       └── file_operations.sh
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/
│   │       ├── V1__create_workflow_tables.sql
│   │       ├── V2__create_execution_records.sql
│   │       ├── V3__create_plugin_info_table.sql
│   │       └── V4__create_tool_info_table.sql  # 工具表(新增)
│   └── pom.xml
│
├── docker/
│   ├── Dockerfile.backend
│   ├── Dockerfile.frontend
│   └── docker-compose.yml
│
├── plugin-sdk/
│   ├── src/main/java/com/bokagent/plugin/sdk/
│   │   ├── BokPlugin.java
│   │   ├── PluginContext.java
│   │   └── annotations/
│   ├── pom.xml
│
├── tool-sdk/                    # 工具开发SDK(新增)
│   ├── src/main/java/com/bokagent/tool/sdk/
│   │   ├── BokTool.java         # 工具接口
│   │   ├── ToolContext.java     # 工具上下文
│   │   ├── ToolResult.java      # 工具结果
│   │   └── annotations/
│   │       ├── @ToolDef         # 工具定义注解
│   │       └── @ToolParam       # 工具参数注解
│   └── pom.xml
│
├── sample-plugins/
│   ├── azure-tts-plugin/
│   ├── baidu-llm-plugin/
│   └── README.md
│
├── sample-tools/                # 示例工具(新增)
│   ├── web-search-tool/         # 网络搜索工具
│   ├── calculator-tool/         # 计算器工具
│   ├── weather-api-tool/        # 天气API工具
│   └── README.md                # 工具开发指南
│
└── README.md
```

---

## 核心模块设计

### 1. MCP协议实现

#### 1.1 MCP协议概述

Model Context Protocol (MCP) 是Anthropic提出的开放协议，用于AI模型与外部数据源和工具的标准化交互。

**核心概念**:
- **Resources**: 数据源（文件、数据库、API等）
- **Tools**: 可执行的操作（搜索、计算、API调用等）
- **Prompts**: 预定义的提示词模板

**传输层支持**:
- STDIO: 标准输入输出（本地进程通信）
- SSE: Server-Sent Events（HTTP单向推送）
- WebSocket: 双向实时通信

#### 1.2 MCP Server实现 (`backend/src/main/java/com/bokagent/mcp/McpServer.java`)

```java
@Component
@Slf4j
public class McpServer {
    
    private final ToolRegistry toolRegistry;
    private final WorkflowService workflowService;
    private final Map<String, McpTransport> transports = new ConcurrentHashMap<>();
    
    /**
     * 初始化MCP Server，注册所有可用的工具和资源
     */
    @PostConstruct
    public void initialize() {
        // 注册工作流作为MCP Tools
        registerWorkflowsAsTools();
        
        // 注册内置工具
        registerBuiltinTools();
        
        // 启动各种传输层
        startTransports();
    }
    
    /**
     * 将工作流注册为MCP Tools
     */
    private void registerWorkflowsAsTools() {
        List<Workflow> workflows = workflowService.getAllWorkflows();
        
        for (Workflow workflow : workflows) {
            McpTool tool = new McpTool(
                "workflow_" + workflow.getId(),
                "执行工作流: " + workflow.getName(),
                buildJsonSchema(workflow),
                (arguments) -> executeWorkflow(workflow.getId(), arguments)
            );
            
            toolRegistry.registerMcpTool(tool);
        }
    }
    
    /**
     * 处理MCP客户端请求
     */
    public McpMessage handleRequest(McpMessage request) {
        return switch (request.getMethod()) {
            case "initialize" -> handleInitialize(request);
            case "tools/list" -> handleListTools(request);
            case "tools/call" -> handleCallTool(request);
            case "resources/list" -> handleListResources(request);
            case "resources/read" -> handleReadResource(request);
            case "prompts/list" -> handleListPrompts(request);
            default -> McpMessage.error("Method not supported");
        };
    }
    
    private McpMessage handleCallTool(McpMessage request) {
        String toolName = request.getParams().get("name").asText();
        JsonNode arguments = request.getParams().get("arguments");
        
        try {
            McpTool tool = toolRegistry.getMcpTool(toolName);
            if (tool == null) {
                return McpMessage.error("Tool not found: " + toolName);
            }
            
            Object result = tool.execute(arguments);
            return McpMessage.success(result);
            
        } catch (Exception e) {
            log.error("工具执行失败", e);
            return McpMessage.error("Execution failed: " + e.getMessage());
        }
    }
}
```

#### 1.3 MCP Client实现 (`backend/src/main/java/com/bokagent/mcp/McpClient.java`)

```java
@Component
@Slf4j
public class McpClient {
    
    private final Map<String, McpTransport> connectedServers = new ConcurrentHashMap<>();
    
    /**
     * 连接到外部MCP Server
     */
    public void connect(String serverUrl, McpTransport transport) {
        transport.connect(serverUrl);
        connectedServers.put(serverUrl, transport);
        
        // 获取远程服务器的工具列表
        List<McpTool> remoteTools = fetchRemoteTools(transport);
        
        // 注册到本地工具注册中心
        for (McpTool tool : remoteTools) {
            toolRegistry.registerRemoteTool(tool, transport);
        }
        
        log.info("已连接到MCP Server: {}", serverUrl);
    }
    
    /**
     * 调用远程MCP工具
     */
    public Object callRemoteTool(String serverUrl, String toolName, 
                                  Map<String, Object> arguments) {
        McpTransport transport = connectedServers.get(serverUrl);
        if (transport == null) {
            throw new IllegalStateException("未连接到服务器: " + serverUrl);
        }
        
        McpMessage request = McpMessage.callTool(toolName, arguments);
        McpMessage response = transport.sendRequest(request);
        
        if (response.isError()) {
            throw new RuntimeException(response.getErrorMessage());
        }
        
        return response.getResult();
    }
    
    /**
     * 读取远程MCP资源
     */
    public String readRemoteResource(String serverUrl, String resourceUri) {
        McpTransport transport = connectedServers.get(serverUrl);
        McpMessage request = McpMessage.readResource(resourceUri);
        McpMessage response = transport.sendRequest(request);
        
        return response.getContent();
    }
}
```

#### 1.4 MCP传输层 - WebSocket (`backend/src/main/java/com/bokagent/mcp/transport/WebSocketTransport.java`)

```java
@Component
public class WebSocketTransport implements McpTransport {
    
    private final SimpMessagingTemplate messagingTemplate;
    private WebSocketSession session;
    
    @Override
    public void connect(String url) {
        // 建立WebSocket连接
        WebSocketClient client = new StandardWebSocketClient();
        client.execute(new McpWebSocketHandler(), url);
    }
    
    @Override
    public McpMessage sendRequest(McpMessage request) {
        try {
            String json = JsonUtils.toJson(request);
            session.sendMessage(new TextMessage(json));
            
            // 等待响应（带超时）
            return waitForResponse(Duration.ofSeconds(30));
            
        } catch (Exception e) {
            throw new RuntimeException("发送请求失败", e);
        }
    }
    
    @ServerEndpoint("/mcp")
    public static class McpWebSocketHandler extends TextWebSocketHandler {
        
        @Autowired
        private McpServer mcpServer;
        
        @Override
        protected void handleTextMessage(WebSocketSession session, 
                                         TextMessage message) {
            McpMessage request = JsonUtils.fromJson(
                message.getPayload(), McpMessage.class
            );
            
            McpMessage response = mcpServer.handleRequest(request);
            
            session.sendMessage(new TextMessage(JsonUtils.toJson(response)));
        }
    }
}
```

#### 1.5 MCP Controller (`backend/src/main/java/com/bokagent/controller/McpController.java`)

```java
@RestController
@RequestMapping("/mcp")
@RequiredArgsConstructor
public class McpController {
    
    private final McpServer mcpServer;
    
    /**
     * SSE传输端点（供MCP Client连接）
     */
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sseEndpoint() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        
        // 注册SSE传输
        SseTransport transport = new SseTransport(emitter);
        mcpServer.registerTransport(transport);
        
        return emitter;
    }
    
    /**
     * POST端点接收MCP消息
     */
    @PostMapping("/message")
    public ResponseEntity<McpMessage> handleMessage(
            @RequestBody McpMessage request) {
        McpMessage response = mcpServer.handleRequest(request);
        return ResponseEntity.ok(response);
    }
}
```

### 2. 工具注册系统

#### 2.1 工具接口定义 (`tool-sdk/src/main/java/com/bokagent/tool/sdk/BokTool.java`)

```java
/**
 * 工具核心接口 - 所有工具必须实现此接口
 */
public interface BokTool {
    
    /**
     * 获取工具元数据
     */
    ToolDefinition getDefinition();
    
    /**
     * 执行工具
     * @param parameters 工具参数
     * @param context 工具上下文
     * @return 执行结果
     */
    ToolResult execute(Map<String, Object> parameters, ToolContext context);
    
    /**
     * 验证参数
     */
    default ValidationResult validate(Map<String, Object> parameters) {
        return ValidationResult.success();
    }
}
```

#### 2.2 工具定义 (`tool-sdk/src/main/java/com/bokagent/tool/sdk/ToolDefinition.java`)

```java
public record ToolDefinition(
    String id,              // 工具唯一ID (如: "web-search")
    String name,            // 工具名称 (如: "Web Search")
    String description,     // 描述
    String category,        // 分类 (search, calculation, api, etc.)
    Map<String, Object> parametersSchema,  // JSON Schema格式的参数定义
    List<String> tags,      // 标签
    boolean requiresAuth,   // 是否需要认证
    Map<String, String> authConfig  // 认证配置
) {}
```

#### 2.3 工具注册中心 (`backend/src/main/java/com/bokagent/tools/ToolRegistry.java`)

```java
@Component
@Slf4j
public class ToolRegistry {
    
    private final Map<String, BokTool> registeredTools = new ConcurrentHashMap<>();
    private final Map<String, McpTool> mcpTools = new ConcurrentHashMap<>();
    private final Map<String, FunctionDefinition> functionCallingTools = new ConcurrentHashMap<>();
    
    /**
     * 注册工作流节点工具
     */
    public void registerTool(BokTool tool) {
        registeredTools.put(tool.getDefinition().id(), tool);
        log.info("工具注册成功: {}", tool.getDefinition().name());
    }
    
    /**
     * 注册MCP工具（供外部AI助手调用）
     */
    public void registerMcpTool(McpTool tool) {
        mcpTools.put(tool.name(), tool);
    }
    
    /**
     * 注册Function Calling工具（供LLM自主调用）
     */
    public void registerFunctionCallingTool(BokTool tool) {
        FunctionDefinition funcDef = convertToFunctionDefinition(tool);
        functionCallingTools.put(tool.getDefinition().id(), funcDef);
    }
    
    /**
     * 获取所有可用于Function Calling的工具定义
     */
    public List<FunctionDefinition> getFunctionCallingTools() {
        return functionCallingTools.values().stream().toList();
    }
    
    /**
     * 执行工具
     */
    public ToolResult executeTool(String toolId, Map<String, Object> parameters, 
                                   ToolContext context) {
        BokTool tool = registeredTools.get(toolId);
        if (tool == null) {
            return ToolResult.error("Tool not found: " + toolId);
        }
        
        // 验证参数
        ValidationResult validation = tool.validate(parameters);
        if (!validation.isValid()) {
            return ToolResult.error(validation.getErrorMessage());
        }
        
        // 执行工具
        try {
            return tool.execute(parameters, context);
        } catch (Exception e) {
            log.error("工具执行失败: {}", toolId, e);
            return ToolResult.error("Execution failed: " + e.getMessage());
        }
    }
    
    /**
     * 转换为Spring AI的FunctionDefinition
     */
    private FunctionDefinition convertToFunctionDefinition(BokTool tool) {
        ToolDefinition def = tool.getDefinition();
        
        return FunctionDefinition.builder()
            .name(def.id())
            .description(def.description())
            .parameters(def.parametersSchema())
            .build();
    }
}
```

#### 2.4 内置工具示例 - 网络搜索 (`backend/src/main/java/com/bokagent/tools/BuiltinTools/WebSearchTool.java`)

```java
@Component
@ToolDef(
    id = "web-search",
    name = "Web Search",
    description = "使用搜索引擎搜索网页内容",
    category = "search",
    tags = {"search", "web", "internet"}
)
public class WebSearchTool implements BokTool {
    
    @Value("${tools.web-search.api-key}")
    private String apiKey;
    
    @Override
    public ToolDefinition getDefinition() {
        Map<String, Object> schema = Map.of(
            "type", "object",
            "properties", Map.of(
                "query", Map.of(
                    "type", "string",
                    "description", "搜索关键词"
                ),
                "numResults", Map.of(
                    "type", "integer",
                    "description", "返回结果数量",
                    "default", 5
                )
            ),
            "required", List.of("query")
        );
        
        return new ToolDefinition(
            "web-search",
            "Web Search",
            "使用搜索引擎搜索网页内容",
            "search",
            schema,
            List.of("search", "web", "internet"),
            true,
            Map.of("apiKey", "Google Custom Search API Key")
        );
    }
    
    @Override
    public ToolResult execute(Map<String, Object> parameters, ToolContext context) {
        String query = (String) parameters.get("query");
        int numResults = (int) parameters.getOrDefault("numResults", 5);
        
        // 调用Google Custom Search API
        String url = "https://www.googleapis.com/customsearch/v1?" +
            "key=" + apiKey + "&cx=YOUR_CX&q=" + query + "&num=" + numResults;
        
        WebClient client = WebClient.create();
        String response = client.get()
            .uri(url)
            .retrieve()
            .bodyToMono(String.class)
            .block();
        
        // 解析搜索结果
        List<Map<String, String>> results = parseSearchResults(response);
        
        return ToolResult.success(results);
    }
}
```

#### 2.5 内置工具示例 - 计算器 (`backend/src/main/java/com/bokagent/tools/BuiltinTools/CalculatorTool.java`)

```java
@Component
@ToolDef(
    id = "calculator",
    name = "Calculator",
    description = "执行数学表达式计算",
    category = "calculation",
    tags = {"math", "calculation"}
)
public class CalculatorTool implements BokTool {
    
    @Override
    public ToolDefinition getDefinition() {
        Map<String, Object> schema = Map.of(
            "type", "object",
            "properties", Map.of(
                "expression", Map.of(
                    "type", "string",
                    "description", "数学表达式，如: 2 + 3 * 4"
                )
            ),
            "required", List.of("expression")
        );
        
        return new ToolDefinition(
            "calculator",
            "Calculator",
            "执行数学表达式计算",
            "calculation",
            schema,
            List.of("math", "calculation"),
            false,
            Map.of()
        );
    }
    
    @Override
    public ToolResult execute(Map<String, Object> parameters, ToolContext context) {
        String expression = (String) parameters.get("expression");
        
        // 使用ScriptEngine安全地计算表达式
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        
        try {
            Object result = engine.eval(expression);
            return ToolResult.success(Map.of("result", result));
        } catch (ScriptException e) {
            return ToolResult.error("计算错误: " + e.getMessage());
        }
    }
}
```

#### 2.6 Function Calling适配器 (`backend/src/main/java/com/bokagent/tools/FunctionCallingAdapter.java`)

```java
@Component
@RequiredArgsConstructor
public class FunctionCallingAdapter {
    
    private final ToolRegistry toolRegistry;
    
    /**
     * 将工具注册到Spring AI的ChatClient
     */
    public void registerToolsToChatClient(ChatClient.Builder chatClientBuilder) {
        List<FunctionDefinition> functions = toolRegistry.getFunctionCallingTools();
        
        chatClientBuilder.defaultFunctions(functions);
    }
    
    /**
     * 处理LLM的Function Call请求
     */
    public ToolResult handleFunctionCall(String functionName, 
                                          Map<String, Object> arguments,
                                          ToolContext context) {
        return toolRegistry.executeTool(functionName, arguments, context);
    }
}
```

#### 2.7 LLM节点集成Function Calling (`backend/src/main/java/com/bokagent/nodes/LLMNode.java`)

```java
@Component
@RequiredArgsConstructor
public class LLMNode extends BaseNode {
    
    private final ChatClient chatClient;
    private final FunctionCallingAdapter functionCallingAdapter;
    
    @Override
    public NodeOutput execute(Map<String, Object> config, Map<String, Object> state) {
        String promptTemplate = (String) config.get("prompt");
        Boolean enableFunctionCalling = (Boolean) config.getOrDefault(
            "enableFunctionCalling", true
        );
        
        String userInput = (String) state.get("user_input");
        String fullPrompt = promptTemplate.replace("{input}", userInput);
        
        Prompt prompt = new Prompt(fullPrompt);
        
        ChatResponse response;
        if (enableFunctionCalling) {
            // 启用Function Calling
            response = chatClient.prompt(prompt)
                .functions(functionCallingAdapter.getAvailableFunctions())
                .call();
            
            // 检查是否有Function Call
            if (response.hasFunctionCall()) {
                FunctionCall functionCall = response.getFunctionCall();
                
                // 执行工具
                ToolResult toolResult = functionCallingAdapter.handleFunctionCall(
                    functionCall.getName(),
                    functionCall.getArguments(),
                    new ToolContext(state)
                );
                
                // 将工具结果返回给LLM进行二次推理
                response = chatClient.prompt(prompt)
                    .functions(functionCallingAdapter.getAvailableFunctions())
                    .functionResponse(toolResult)
                    .call();
            }
        } else {
            // 普通对话
            response = chatClient.call(prompt);
        }
        
        String result = response.getResult().getOutput().getContent();
        state.put("llm_output", result);
        
        return new NodeOutput(result);
    }
}
```

#### 2.8 工具节点实现 (`backend/src/main/java/com/bokagent/nodes/ToolNode.java`)

```java
@Component
@RequiredArgsConstructor
public class ToolNode extends BaseNode {
    
    private final ToolRegistry toolRegistry;
    
    @Override
    public NodeType getType() {
        return NodeType.TOOL;
    }
    
    @Override
    public NodeOutput execute(Map<String, Object> config, Map<String, Object> state) {
        String toolId = (String) config.get("toolId");
        Map<String, Object> toolParams = (Map<String, Object>) config.get("parameters");
        
        // 替换参数中的变量引用（如 ${llm_output}）
        toolParams = resolveVariables(toolParams, state);
        
        // 执行工具
        ToolContext context = new ToolContext(state);
        ToolResult result = toolRegistry.executeTool(toolId, toolParams, context);
        
        if (result.isSuccess()) {
            state.put("tool_output", result.getData());
            return new NodeOutput(result.getData());
        } else {
            throw new RuntimeException("工具执行失败: " + result.getErrorMessage());
        }
    }
    
    /**
     * 解析参数中的变量引用
     */
    private Map<String, Object> resolveVariables(Map<String, Object> params, 
                                                  Map<String, Object> state) {
        Map<String, Object> resolved = new HashMap<>();
        
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            
            if (value instanceof String str && str.startsWith("${") && str.endsWith("}")) {
                // 提取变量名
                String varName = str.substring(2, str.length() - 1);
                value = state.get(varName);
            }
            
            resolved.put(entry.getKey(), value);
        }
        
        return resolved;
    }
}
```

### 3. 工具管理API

#### 3.1 工具控制器 (`backend/src/main/java/com/bokagent/controller/ToolController.java`)

```java
@RestController
@RequestMapping("/api/tools")
@RequiredArgsConstructor
public class ToolController {
    
    private final ToolService toolService;
    
    /**
     * 获取已安装的工具列表
     */
    @GetMapping("/installed")
    public List<ToolDTO> getInstalledTools(
            @RequestParam(required = false) String category) {
        return toolService.getInstalledTools(category);
    }
    
    /**
     * 从工具市场获取可用工具
     */
    @GetMapping("/marketplace")
    public List<ToolDTO> getMarketplaceTools(
            @RequestParam(required = false) String category) {
        return toolService.fetchFromMarketplace(category);
    }
    
    /**
     * 安装工具
     */
    @PostMapping("/{toolId}/install")
    public ResponseEntity<Void> installTool(@PathVariable String toolId) {
        toolService.installTool(toolId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 卸载工具
     */
    @DeleteMapping("/{toolId}")
    public ResponseEntity<Void> uninstallTool(@PathVariable String toolId) {
        toolService.uninstallTool(toolId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 上传自定义工具
     */
    @PostMapping("/upload")
    public ResponseEntity<Void> uploadTool(
            @RequestParam("file") MultipartFile toolJar,
            @RequestParam("metadata") String metadataJson) {
        toolService.uploadCustomTool(toolJar, metadataJson);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 测试工具执行
     */
    @PostMapping("/{toolId}/test")
    public ResponseEntity<ToolResult> testTool(
            @PathVariable String toolId,
            @RequestBody Map<String, Object> parameters) {
        ToolResult result = toolService.testTool(toolId, parameters);
        return ResponseEntity.ok(result);
    }
}
```

### 4. 前端工具市场组件

#### 4.1 工具市场列表 (`frontend/src/components/ToolMarket/ToolList.tsx`)

```typescript
import { List, Card, Button, Tag, message } from 'antd';
import { DownloadOutlined, ExperimentOutlined } from '@ant-design/icons';

export const ToolList = () => {
  const [tools, setTools] = useState<Tool[]>([]);
  
  useEffect(() => {
    fetchMarketplaceTools().then(setTools);
  }, []);
  
  const handleInstall = async (toolId: string) => {
    try {
      await installTool(toolId);
      message.success('工具安装成功');
    } catch (error) {
      message.error('工具安装失败');
    }
  };
  
  const handleTest = async (toolId: string) => {
    // 打开测试对话框
  };
  
  return (
    <List
      grid={{ gutter: 16, column: 3 }}
      dataSource={tools}
      renderItem={(tool) => (
        <List.Item>
          <Card
            title={tool.name}
            extra={<Tag>{tool.category}</Tag>}
            actions={[
              <Button
                icon={<ExperimentOutlined />}
                onClick={() => handleTest(tool.id)}
              >
                测试
              </Button>,
              <Button
                type="primary"
                icon={<DownloadOutlined />}
                onClick={() => handleInstall(tool.id)}
              >
                安装
              </Button>
            ]}
          >
            <p>{tool.description}</p>
            <div>
              {tool.tags.map(tag => (
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

#### 4.2 工具节点配置面板 (`frontend/src/components/WorkflowEditor/ToolNodeConfig.tsx`)

```typescript
import { Form, Select, Input, Button } from 'antd';
import { useEffect, useState } from 'react';

export const ToolNodeConfig = ({ nodeData, onSave }: any) => {
  const [tools, setTools] = useState<Tool[]>([]);
  const [form] = Form.useForm();
  
  useEffect(() => {
    fetchInstalledTools().then(setTools);
  }, []);
  
  const handleSave = (values: any) => {
    onSave({
      ...nodeData,
      config: values
    });
  };
  
  return (
    <Form form={form} onFinish={handleSave} initialValues={nodeData.config}>
      <Form.Item name="toolId" label="选择工具" rules={[{ required: true }]}>
        <Select
          options={tools.map(t => ({
            label: t.name,
            value: t.id,
            description: t.description
          }))}
        />
      </Form.Item>
      
      {/* 动态渲染工具参数表单 */}
      <DynamicParameterFields toolId={form.getFieldValue('toolId')} />
      
      <Form.Item>
        <Button type="primary" htmlType="submit">保存</Button>
      </Form.Item>
    </Form>
  );
};
```

### 5. 数据库设计

#### 5.1 工具信息表 (`V4__create_tool_info_table.sql`)

```sql
CREATE TABLE tool_info (
    id BIGSERIAL PRIMARY KEY,
    tool_id VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(50),
    version VARCHAR(20),
    parameters_schema JSONB,  -- 参数JSON Schema
    tags TEXT[],
    requires_auth BOOLEAN DEFAULT FALSE,
    auth_config JSONB,
    jar_path VARCHAR(500),    -- JAR包路径
    is_builtin BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    installed_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_tool_info_category ON tool_info(category);
CREATE INDEX idx_tool_info_is_active ON tool_info(is_active);
```

### 6. MCP配置

#### 6.1 application.yml配置

```yaml
mcp:
  server:
    enabled: true
    name: "BokAgent MCP Server"
    version: "1.0.0"
    capabilities:
      tools: true
      resources: true
      prompts: true
    transports:
      sse:
        enabled: true
        path: /mcp/sse
      websocket:
        enabled: true
        path: /mcp/ws
  
  client:
    enabled: true
    servers:
      - name: "External MCP Server"
        url: "http://example.com/mcp"
        transport: websocket
        autoConnect: false

tools:
  web-search:
    api-key: ${GOOGLE_SEARCH_API_KEY}
  
  weather-api:
    api-key: ${WEATHER_API_KEY}
```

---

## 实施步骤

### 阶段1: 项目初始化 (1-2天)

**Task 1.1: 创建Spring Boot后端项目**
- 使用Spring Initializr生成项目骨架 (Spring Boot 3.5 + JDK 21)
- 配置pom.xml依赖: Spring Web, Spring AI, WebSocket, MyBatis-Plus, PostgreSQL驱动, Redis, MinIO
- 配置application.yml (双数据源、Redis、MinIO、LLM API密钥、MCP配置)
- 设置Flyway数据库迁移
- 配置虚拟线程池

**Task 1.2: 创建React前端项目**
- 使用Vite + TypeScript模板初始化
- 安装依赖: @xyflow/react, antd, axios, @stomp/stompjs, monaco-editor
- 配置TypeScript路径别名和ESLint规则
- 搭建基础路由和布局

**Task 1.3: 创建SDK模块**
- 创建plugin-sdk Maven模块
- 创建tool-sdk Maven模块
- 定义核心接口和注解

### 阶段2: MCP协议实现 (3-4天)

**Task 2.1: 实现MCP Server**
- 开发McpServer核心类
- 实现McpTool、McpResource、McpPrompt数据结构
- 实现工具注册和工作流暴露为MCP Tools
- 编写单元测试

**Task 2.2: 实现MCP传输层**
- 实现SseTransport（Server-Sent Events）
- 实现WebSocketTransport（双向通信）
- 实现StdioTransport（本地进程通信）
- 创建McpController暴露SSE和HTTP端点

**Task 2.3: 实现MCP Client**
- 开发McpClient核心类
- 实现连接外部MCP Server功能
- 实现远程工具调用和资源读取
- 将远程工具注册到本地ToolRegistry

### 阶段3: 工具注册系统 (3-4天)

**Task 3.1: 实现工具注册中心**
- 开发ToolRegistry核心类
- 实现三种工具注册方式：
  - 工作流节点工具（registerTool）
  - MCP工具（registerMcpTool）
  - Function Calling工具（registerFunctionCallingTool）
- 实现工具执行逻辑和参数验证

**Task 3.2: 开发内置工具**
- 实现WebSearchTool（网络搜索）
- 实现CalculatorTool（计算器）
- 实现DateTimeTool（日期时间）
- 实现HttpApiTool（通用HTTP API调用）
- 为每个工具编写单元测试

**Task 3.3: 实现Function Calling集成**
- 开发FunctionCallingAdapter
- 将工具转换为Spring AI的FunctionDefinition
- 修改LLMNode支持Function Calling
- 实现工具结果的二次推理流程

**Task 3.4: 实现工具节点**
- 开发ToolNode节点类
- 实现参数变量引用解析（${llm_output}）
- 实现工具执行和结果处理
- 前端开发ToolNode组件和配置面板

### 阶段4: 核心引擎开发 (3-4天)

**Task 4.1: 集成LangGraph4J执行引擎**
- 实现StateGraphBuilder状态图构建器
- 开发LangGraphExecutor执行器（虚拟线程）
- 实现节点注册中心NodeRegistry
- 支持TOOL节点类型的执行

**Task 4.2: 实现插件管理系统**
- 开发PluginManager插件管理器
- 实现PluginClassLoader自定义类加载器
- 开发PluginNodeWrapper插件节点包装器
- 实现插件热加载和卸载机制

**Task 4.3: 实现React Flow → LangGraph适配层**
- 定义WorkflowDTO数据格式
- 实现AdapterService转换逻辑
- 支持TOOL和PLUGIN节点类型的转换

### 阶段5: LLM和TTS集成 (2-3天)

**Task 5.1: 集成Spring AI和多厂商LLM**
- 配置Spring AI依赖
- 实现SpringAiConfig配置类
- 注册OpenAI/Deepseek/Qwen的ChatModel Bean
- 实现LLMNode节点（含Function Calling支持）

**Task 5.2: 集成TTS服务和MinIO对象存储**
- 实现TtsService接口和ElevenLabsTtsService
- 配置MinIO客户端
- 实现AudioStorageService
- 开发TTSNode节点

### 阶段6: 工具市场和插件市场 (2-3天)

**Task 6.1: 实现工具管理API**
- 开发ToolController（安装/卸载/查询/测试）
- 实现ToolService（从市场下载、上传自定义工具）
- 创建ToolInfo实体和Mapper
- 实现工具元数据持久化

**Task 6.2: 开发工具市场前端**
- 实现ToolList组件（展示可用工具）
- 实现ToolDetail组件（工具详情）
- 实现ToolConfigurator组件（工具配置）
- 集成到主应用的侧边栏菜单

**Task 6.3: 实现插件市场API和前端**
- 开发PluginController和PluginService
- 实现PluginList、PluginDetail、PluginInstaller组件

### 阶段7: 前端工作流编辑器 (3-4天)

**Task 7.1: 搭建React Flow画板**
- 配置React Flow基础组件
- 实现Canvas主画板组件
- 添加Background、Controls、MiniMap插件

**Task 7.2: 开发自定义节点组件**
- 实现StartNode、LLMNode、TTSNode、ToolNode、EndNode
- 实现PluginNode（插件节点动态渲染）
- 为每个节点添加输入/输出Handle

**Task 7.3: 实现节点配置面板**
- 开发ConfigPanel侧边栏组件
- 根据选中节点类型动态渲染配置表单
- LLM节点：支持Function Calling开关和工具选择
- Tool节点：工具选择和参数配置
- 插件节点：显示插件特定的配置项

### 阶段8: 调试和执行功能 (2-3天)

**Task 8.1: 实现WebSocket实时通信**
- 配置Spring Boot WebSocket
- 实现LangGraph状态流的实时推送
- 实现MCP消息的WebSocket传输
- 前端集成STOMP客户端

**Task 8.2: 开发调试抽屉**
- 实现DebugDrawer组件
- 开发LogViewer实时状态流显示
- 实现TestInput测试输入框

**Task 8.3: 实现工作流执行API**
- 开发ExecutionController
- 实现WorkflowExecutionService
- 异步执行工作流（虚拟线程）
- 保存执行记录

### 阶段9: Docker部署配置 (1-2天)

**Task 9.1: 编写Docker配置文件**
- 创建Dockerfile.backend（多阶段构建）
- 创建Dockerfile.frontend（Nginx托管）
- 编写docker-compose.yml编排所有服务
- 配置Nginx反向代理和WebSocket支持

**Task 9.2: 环境变量和配置管理**
- 创建.env.example模板文件
- 配置Spring Boot的docker profile
- 实现配置的外部化

**Task 9.3: 健康检查和日志**
- 添加Spring Boot Actuator健康检查端点
- 配置Docker健康检查
- 设置日志滚动策略

### 阶段10: 示例插件和工具开发 (2-3天)

**Task 10.1: 开发示例插件**
- Azure TTS插件
- 百度文心一言插件
- 编写插件开发文档

**Task 10.2: 开发示例工具**
- Web Search工具（Google Custom Search）
- Weather API工具（OpenWeatherMap）
- Calculator工具（内置）
- 编写工具开发文档

### 阶段11: 完善和优化 (2-3天)

**Task 11.1: 错误处理和用户反馈**
- 统一异常处理
- 前端友好的错误提示
- 加载状态和Skeleton屏

**Task 11.2: Redis缓存优化**
- 缓存LLM响应
- 缓存工具执行结果
- 缓存工作流配置

**Task 11.3: 文档和测试**
- 编写README.md（包含MCP协议使用说明）
- 补充单元测试
- 编写用户使用手册
- 编写插件/工具开发者指南

---

## 关键技术点总结

1. **MCP协议双向支持**: 
   - Server模式：将工作流和工具暴露给外部AI助手（Claude等）
   - Client模式：连接外部MCP Servers获取资源和工具
   - 支持SSE、WebSocket、STDIO三种传输层

2. **工具注册系统三重集成**:
   - 工作流节点工具：在画板中显式添加工具节点
   - LLM Function Calling：LLM自主决定调用工具
   - MCP Tools：通过MCP协议对外提供工具

3. **插件系统**: Java SPI + 动态类加载 + 热插拔

4. **LangGraph4J状态图**: 基于状态机的图编排

5. **Spring AI统一抽象**: 通过ChatClient屏蔽不同LLM厂商的API差异

6. **JDK 21虚拟线程**: 高并发执行多个工作流

7. **MinIO对象存储**: 音频文件独立存储

8. **Docker Compose**: 一键部署全栈服务

---

## 潜在风险和解决方案

| 风险 | 影响 | 解决方案 |
|------|------|----------|
| MCP协议成熟度 | 协议仍在演进中 | 关注官方更新，保持协议实现的兼容性 |
| 工具安全性 | 恶意工具可能破坏系统 | 沙箱隔离、权限控制、代码审计 |
| Function Calling稳定性 | LLM可能错误调用工具 | 添加工具调用的重试和回退机制 |
| 插件/工具兼容性 | 版本冲突 | 版本依赖管理、兼容性测试 |
| Docker资源占用 | 6个容器消耗较多内存 | 限制容器资源、提供轻量模式 |
| 远程MCP Server延迟 | 网络延迟影响性能 | 添加超时控制、本地缓存、异步调用 |

---

## 验收标准

1. ✅ 可在画板上拖拽添加5种标准节点（Start/LLM/TTS/Tool/End）和插件节点并连线
2. ✅ 可配置LLM节点的提供商、模型、提示词和Function Calling开关
3. ✅ 可在Tool节点中选择已安装的工具并配置参数
4. ✅ 可从插件市场安装新节点类型
5. ✅ 可从工具市场安装新工具
6. ✅ 点击执行按钮后，工作流按LangGraph顺序执行
7. ✅ 调试抽屉实时显示每个节点的状态更新
8. ✅ 执行完成后，音频URL自动在播放器中加载并播放
9. ✅ 工作流可保存到PostgreSQL并重新加载
10. ✅ 支持切换不同的LLM提供商
11. ✅ 执行历史记录可查看和回放
12. ✅ 音频文件存储在MinIO，返回预签名URL
13. ✅ 通过`docker-compose up -d`一键启动所有服务
14. ✅ 插件和工具支持热加载，无需重启后端服务
15. ✅ 提供至少2个示例插件和3个示例工具
16. ✅ MCP Server可通过SSE/WebSocket被外部AI助手连接
17. ✅ MCP Client可连接外部MCP Servers并调用远程工具
18. ✅ LLM节点支持Function Calling，可自主调用工具

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
# MCP SSE端点: http://localhost:8080/mcp/sse
# MCP WebSocket端点: ws://localhost:8080/mcp/ws
# MinIO控制台: http://localhost:9001

# 5. 查看日志
docker-compose logs -f backend

# 6. 停止服务
docker-compose down
```

---

## 下一步行动

等待您确认此计划后，我将按照以下顺序开始实施：
1. 初始化前后端项目结构和SDK模块
2. 实现MCP协议（Server + Client + 传输层）
3. 实现工具注册系统和内置工具
4. 集成LangGraph4J和Spring AI
5. 实现插件管理系统和热加载机制
6. 开发前端可视化编辑器、工具市场和插件市场
7. 配置Docker Compose一键部署
8. 开发示例插件和工具
9. 完善测试和文档

请确认是否开始执行，或提出需要调整的地方。