# AI Agent工作流编排系统实施计划 (企业级增强版)

## 技术栈总览

**前端**: React 18 + TypeScript + Vite + React Flow (@xyflow/react) + Ant Design 5 + Monaco Editor  
**后端**: Spring Boot 3.5 + JDK 21 + Spring AI 1.1 + LangGraph4J + MyBatis-Plus 3.5 + WebSocket  
**数据库**: PostgreSQL 15+ (工作流数据存储) + MySQL 8+ (业务数据存储)  
**缓存**: Redis 7 (分布式缓存、会话管理、工具结果缓存)  
**对象存储**: MinIO (音频文件存储)  
**工作流引擎**: LangGraph4J (Agent编排框架) + Spring AI (LLM统一抽象层)  
**MCP协议**: Model Context Protocol (双向支持: Server + Client)  
**工具系统**: 工具注册中心 + 工作流节点工具 + LLM Function Calling工具 + 重试回退机制  
**插件系统**: Java SPI + 动态类加载 + 插件市场API + 版本依赖管理  
**容器化**: Docker + Docker Compose (一键部署全栈服务)  
**部署架构**: 前后端分离，Docker Compose编排所有服务

---

## 项目目录结构 (企业级完整版)

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
│   │   │   │   │   ├── ToolNode.tsx
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
│   │   │   └── ToolMarket/
│   │   │       ├── ToolList.tsx
│   │   │       ├── ToolDetail.tsx
│   │   │       └── ToolConfigurator.tsx
│   │   ├── services/
│   │   │   ├── api.ts
│   │   │   ├── websocket.ts
│   │   │   ├── pluginApi.ts
│   │   │   └── toolApi.ts
│   │   ├── types/
│   │   │   ├── workflow.ts
│   │   │   ├── node.ts
│   │   │   ├── plugin.ts
│   │   │   └── tool.ts
│   │   ├── hooks/
│   │   │   ├── useWorkflowExecution.ts
│   │   │   ├── useDebugStream.ts
│   │   │   ├── usePluginLoader.ts
│   │   │   └── useToolRegistry.ts
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
│   │   │   ├── ToolController.java
│   │   │   └── McpController.java
│   │   ├── service/
│   │   │   ├── WorkflowService.java
│   │   │   ├── WorkflowExecutionService.java
│   │   │   ├── AdapterService.java
│   │   │   ├── PluginService.java
│   │   │   └── ToolService.java
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
│   │   │   ├── ToolNode.java
│   │   │   ├── EndNode.java
│   │   │   └── PluginNodeWrapper.java
│   │   ├── plugin/
│   │   │   ├── PluginManager.java
│   │   │   ├── PluginRegistry.java
│   │   │   ├── BokPlugin.java
│   │   │   ├── PluginClassLoader.java
│   │   │   ├── PluginMetadata.java
│   │   │   └── VersionResolver.java        # 版本依赖解析器(新增)
│   │   ├── tools/
│   │   │   ├── ToolRegistry.java
│   │   │   ├── ToolDefinition.java
│   │   │   ├── ToolExecutor.java           # 工具执行器(增强版)
│   │   │   ├── RetryHandler.java           # 重试处理器(新增)
│   │   │   ├── FallbackStrategy.java       # 回退策略(新增)
│   │   │   ├── TimeoutController.java      # 超时控制器(新增)
│   │   │   ├── ToolCacheManager.java       # 工具缓存管理器(新增)
│   │   │   ├── BuiltinTools/
│   │   │   │   ├── WebSearchTool.java
│   │   │   │   ├── CalculatorTool.java
│   │   │   │   ├── DateTimeTool.java
│   │   │   │   └── HttpApiTool.java
│   │   │   └── FunctionCallingAdapter.java
│   │   ├── mcp/
│   │   │   ├── McpServer.java
│   │   │   ├── McpClient.java
│   │   │   ├── McpResource.java
│   │   │   ├── McpTool.java
│   │   │   ├── McpPrompt.java
│   │   │   ├── transport/
│   │   │   │   ├── StdioTransport.java
│   │   │   │   ├── SseTransport.java
│   │   │   │   └── WebSocketTransport.java
│   │   │   └── protocol/
│   │   │       ├── McpMessage.java
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
│   │   ├── cache/                           # 缓存模块(新增)
│   │   │   ├── CacheService.java           # 统一缓存服务
│   │   │   ├── CacheConfig.java            # 缓存配置
│   │   │   └── CacheKeyGenerator.java      # 缓存键生成器
│   │   ├── async/                           # 异步执行模块(新增)
│   │   │   ├── AsyncExecutor.java          # 异步执行器
│   │   │   ├── AsyncConfig.java            # 异步配置
│   │   │   └── TaskMonitor.java            # 任务监控
│   │   ├── compatibility/                   # 兼容性测试模块(新增)
│   │   │   ├── CompatibilityTester.java    # 兼容性测试器
│   │   │   ├── VersionChecker.java         # 版本检查器
│   │   │   └── TestReport.java             # 测试报告
│   │   ├── model/
│   │   │   ├── Workflow.java
│   │   │   ├── WorkflowNode.java
│   │   │   ├── WorkflowEdge.java
│   │   │   ├── ExecutionRecord.java
│   │   │   ├── PluginInfo.java
│   │   │   └── ToolInfo.java
│   │   ├── mapper/
│   │   │   ├── WorkflowMapper.java
│   │   │   ├── ExecutionRecordMapper.java
│   │   │   ├── PluginInfoMapper.java
│   │   │   └── ToolInfoMapper.java
│   │   ├── dto/
│   │   │   ├── WorkflowDTO.java
│   │   │   ├── LangGraphDefinition.java
│   │   │   ├── ExecutionResult.java
│   │   │   ├── PluginDTO.java
│   │   │   └── ToolDTO.java
│   │   └── config/
│   │       ├── WebSocketConfig.java
│   │       ├── RedisConfig.java
│   │       ├── MyBatisPlusConfig.java
│   │       ├── MinioConfig.java
│   │       ├── McpConfig.java
│   │       ├── RetryConfig.java            # 重试配置(新增)
│   │       └── CacheConfig.java            # 缓存配置(新增)
│   ├── tools/
│   │   ├── python/
│   │   │   ├── data_analysis.py
│   │   │   └── image_processing.py
│   │   └── shell/
│   │       ├── system_info.sh
│   │       └── file_operations.sh
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/
│   │       ├── V1__create_workflow_tables.sql
│   │       ├── V2__create_execution_records.sql
│   │       ├── V3__create_plugin_info_table.sql
│   │       ├── V4__create_tool_info_table.sql
│   │       └── V5__create_cache_tables.sql  # 缓存表(新增)
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
│   │   ├── PluginVersion.java              # 插件版本信息(新增)
│   │   └── annotations/
│   ├── pom.xml
│
├── tool-sdk/
│   ├── src/main/java/com/bokagent/tool/sdk/
│   │   ├── BokTool.java
│   │   ├── ToolContext.java
│   │   ├── ToolResult.java
│   │   ├── RetryPolicy.java                # 重试策略(新增)
│   │   ├── FallbackHandler.java            # 回退处理器(新增)
│   │   └── annotations/
│   ├── pom.xml
│
├── sample-plugins/
│   ├── azure-tts-plugin/
│   ├── baidu-llm-plugin/
│   └── README.md
│
├── sample-tools/
│   ├── web-search-tool/
│   ├── calculator-tool/
│   ├── weather-api-tool/
│   └── README.md
│
├── compatibility-tests/                   # 兼容性测试套件(新增)
│   ├── src/test/java/com/bokagent/compat/
│   │   ├── PluginCompatibilityTest.java
│   │   ├── ToolCompatibilityTest.java
│   │   └── McpProtocolTest.java
│   └── README.md
│
└── README.md
```

---

## 核心模块设计 (增强部分)

### 1. 工具调用重试和回退机制

#### 1.1 重试处理器 (`backend/src/main/java/com/bokagent/tools/RetryHandler.java`)

```java
@Component
@Slf4j
public class RetryHandler {
    
    private final ObjectMapper objectMapper;
    
    /**
     * 带重试的执行
     * @param callable 可执行的任务
     * @param retryPolicy 重试策略
     * @return 执行结果
     */
    public <T> T executeWithRetry(Callable<T> callable, RetryPolicy retryPolicy) {
        int maxRetries = retryPolicy.getMaxRetries();
        long initialDelay = retryPolicy.getInitialDelayMs();
        double backoffMultiplier = retryPolicy.getBackoffMultiplier();
        
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                log.debug("执行尝试 {}/{}", attempt, maxRetries);
                return callable.call();
                
            } catch (Exception e) {
                lastException = e;
                log.warn("执行失败 (尝试 {}/{}): {}", attempt, maxRetries, e.getMessage());
                
                // 检查是否应该重试
                if (!shouldRetry(e, retryPolicy)) {
                    throw new RuntimeException("不可重试的错误: " + e.getMessage(), e);
                }
                
                // 计算退避延迟
                if (attempt < maxRetries) {
                    long delay = (long) (initialDelay * Math.pow(backoffMultiplier, attempt - 1));
                    log.info("等待 {}ms 后重试...", delay);
                    
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("重试被中断", ie);
                    }
                }
            }
        }
        
        throw new RuntimeException("达到最大重试次数 (" + maxRetries + ")", lastException);
    }
    
    /**
     * 判断是否应该重试
     */
    private boolean shouldRetry(Exception exception, RetryPolicy policy) {
        // 检查异常类型是否在重试列表中
        for (Class<? extends Exception> retryableException : policy.getRetryableExceptions()) {
            if (retryableException.isInstance(exception)) {
                return true;
            }
        }
        
        // 默认不重试
        return false;
    }
}
```

#### 1.2 重试策略定义 (`tool-sdk/src/main/java/com/bokagent/tool/sdk/RetryPolicy.java`)

```java
@Data
@Builder
public class RetryPolicy {
    
    /**
     * 最大重试次数
     */
    @Builder.Default
    private int maxRetries = 3;
    
    /**
     * 初始延迟（毫秒）
     */
    @Builder.Default
    private long initialDelayMs = 1000;
    
    /**
     * 退避倍数（指数退避）
     */
    @Builder.Default
    private double backoffMultiplier = 2.0;
    
    /**
     * 最大延迟（毫秒）
     */
    @Builder.Default
    private long maxDelayMs = 30000;
    
    /**
     * 可重试的异常类型
     */
    @Builder.Default
    private List<Class<? extends Exception>> retryableExceptions = List.of(
        java.net.SocketTimeoutException.class,
        java.io.IOException.class,
        org.springframework.web.client.ResourceAccessException.class
    );
    
    /**
     * 创建默认重试策略
     */
    public static RetryPolicy defaultPolicy() {
        return RetryPolicy.builder().build();
    }
    
    /**
     * 创建无重试策略
     */
    public static RetryPolicy noRetry() {
        return RetryPolicy.builder().maxRetries(0).build();
    }
}
```

#### 1.3 回退策略 (`backend/src/main/java/com/bokagent/tools/FallbackStrategy.java`)

```java
@Component
@Slf4j
public class FallbackStrategy {
    
    private final ToolRegistry toolRegistry;
    
    /**
     * 执行带fallback的工具调用
     * @param primaryToolId 主工具ID
     * @param fallbackToolId 备用工具ID
     * @param parameters 参数
     * @param context 上下文
     * @return 执行结果
     */
    public ToolResult executeWithFallback(String primaryToolId, 
                                           String fallbackToolId,
                                           Map<String, Object> parameters,
                                           ToolContext context) {
        try {
            // 尝试执行主工具
            log.info("执行主工具: {}", primaryToolId);
            return toolRegistry.executeTool(primaryToolId, parameters, context);
            
        } catch (Exception e) {
            log.warn("主工具执行失败: {}, 切换到备用工具: {}", 
                     primaryToolId, fallbackToolId, e);
            
            // 执行备用工具
            if (fallbackToolId != null && !fallbackToolId.isEmpty()) {
                try {
                    return toolRegistry.executeTool(fallbackToolId, parameters, context);
                } catch (Exception fallbackError) {
                    log.error("备用工具也执行失败: {}", fallbackToolId, fallbackError);
                    return ToolResult.error(
                        "主工具和备用工具均执行失败: " + e.getMessage() + 
                        ", " + fallbackError.getMessage()
                    );
                }
            } else {
                return ToolResult.error("工具执行失败: " + e.getMessage());
            }
        }
    }
    
    /**
     * 执行带多个fallback的工具调用
     */
    public ToolResult executeWithMultipleFallbacks(String primaryToolId,
                                                    List<String> fallbackToolIds,
                                                    Map<String, Object> parameters,
                                                    ToolContext context) {
        // 首先尝试主工具
        try {
            return toolRegistry.executeTool(primaryToolId, parameters, context);
        } catch (Exception e) {
            log.warn("主工具失败: {}", primaryToolId, e);
        }
        
        // 依次尝试备用工具
        for (String fallbackId : fallbackToolIds) {
            try {
                log.info("尝试备用工具: {}", fallbackId);
                return toolRegistry.executeTool(fallbackId, parameters, context);
            } catch (Exception e) {
                log.warn("备用工具失败: {}", fallbackId, e);
            }
        }
        
        return ToolResult.error("所有工具均执行失败");
    }
}
```

#### 1.4 超时控制器 (`backend/src/main/java/com/bokagent/tools/TimeoutController.java`)

```java
@Component
@Slf4j
public class TimeoutController {
    
    private final ExecutorService timeoutExecutor = Executors.newCachedThreadPool();
    
    /**
     * 带超时的执行
     * @param callable 可执行任务
     * @param timeoutMs 超时时间（毫秒）
     * @return 执行结果
     */
    public <T> T executeWithTimeout(Callable<T> callable, long timeoutMs) {
        Future<T> future = timeoutExecutor.submit(callable);
        
        try {
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new RuntimeException("执行超时 (" + timeoutMs + "ms)", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("执行被中断", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("执行失败", e.getCause());
        }
    }
    
    /**
     * 批量执行带总超时控制
     */
    public <T> List<T> executeBatchWithTimeout(List<Callable<T>> callables, 
                                                long totalTimeoutMs) {
        ExecutorService executor = Executors.newFixedThreadPool(callables.size());
        
        try {
            List<Future<T>> futures = executor.invokeAll(callables, 
                                                         totalTimeoutMs, 
                                                         TimeUnit.MILLISECONDS);
            
            List<T> results = new ArrayList<>();
            for (Future<T> future : futures) {
                if (!future.isCancelled()) {
                    results.add(future.get());
                }
            }
            
            return results;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("批量执行被中断", e);
        } finally {
            executor.shutdownNow();
        }
    }
}
```

#### 1.5 工具缓存管理器 (`backend/src/main/java/com/bokagent/tools/ToolCacheManager.java`)

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class ToolCacheManager {
    
    private final CacheService cacheService;
    
    @Value("${tools.cache.ttl:3600}")
    private long cacheTtlSeconds;
    
    @Value("${tools.cache.enabled:true}")
    private boolean cacheEnabled;
    
    /**
     * 从缓存获取或执行工具
     */
    public ToolResult getOrExecute(String toolId, 
                                    Map<String, Object> parameters,
                                    ToolContext context,
                                    Callable<ToolResult> executor) {
        if (!cacheEnabled) {
            try {
                return executor.call();
            } catch (Exception e) {
                throw new RuntimeException("工具执行失败", e);
            }
        }
        
        // 生成缓存键
        String cacheKey = generateCacheKey(toolId, parameters);
        
        // 尝试从缓存获取
        ToolResult cachedResult = cacheService.get(cacheKey, ToolResult.class);
        if (cachedResult != null) {
            log.debug("缓存命中: {}", cacheKey);
            return cachedResult;
        }
        
        // 执行工具
        try {
            ToolResult result = executor.call();
            
            // 如果成功，存入缓存
            if (result.isSuccess()) {
                cacheService.put(cacheKey, result, cacheTtlSeconds, TimeUnit.SECONDS);
                log.debug("缓存已设置: {}", cacheKey);
            }
            
            return result;
            
        } catch (Exception e) {
            throw new RuntimeException("工具执行失败", e);
        }
    }
    
    /**
     * 生成缓存键
     */
    private String generateCacheKey(String toolId, Map<String, Object> parameters) {
        String paramsHash = Integer.toHexString(
            Objects.hash(JsonUtils.toJson(parameters))
        );
        return String.format("tool:%s:%s", toolId, paramsHash);
    }
    
    /**
     * 清除工具缓存
     */
    public void invalidateToolCache(String toolId) {
        String pattern = String.format("tool:%s:*", toolId);
        cacheService.deleteByPattern(pattern);
        log.info("已清除工具缓存: {}", toolId);
    }
}
```

#### 1.6 增强的工具执行器 (`backend/src/main/java/com/bokagent/tools/ToolExecutor.java`)

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class ToolExecutor {
    
    private final ToolRegistry toolRegistry;
    private final RetryHandler retryHandler;
    private final FallbackStrategy fallbackStrategy;
    private final TimeoutController timeoutController;
    private final ToolCacheManager cacheManager;
    
    /**
     * 执行工具（包含重试、回退、超时、缓存）
     */
    public ToolResult execute(String toolId, 
                              Map<String, Object> parameters,
                              ToolContext context,
                              ExecutionConfig config) {
        
        // 1. 检查缓存
        return cacheManager.getOrExecute(toolId, parameters, context, () -> {
            
            // 2. 构建执行逻辑
            Callable<ToolResult> executionLogic = () -> {
                
                // 3. 带超时控制的执行
                return timeoutController.executeWithTimeout(() -> {
                    
                    // 4. 如果有备用工具，使用fallback策略
                    if (config.getFallbackToolId() != null) {
                        return fallbackStrategy.executeWithFallback(
                            toolId,
                            config.getFallbackToolId(),
                            parameters,
                            context
                        );
                    } else {
                        // 直接执行
                        return toolRegistry.executeTool(toolId, parameters, context);
                    }
                    
                }, config.getTimeoutMs());
            };
            
            // 5. 带重试的执行
            return retryHandler.executeWithRetry(executionLogic, config.getRetryPolicy());
        });
    }
    
    /**
     * 执行配置
     */
    @Data
    @Builder
    public static class ExecutionConfig {
        @Builder.Default
        private long timeoutMs = 30000; // 默认30秒超时
        
        @Builder.Default
        private RetryPolicy retryPolicy = RetryPolicy.defaultPolicy();
        
        private String fallbackToolId;
        
        private boolean enableCache = true;
    }
}
```

### 2. 版本依赖管理和兼容性测试

#### 2.1 版本依赖解析器 (`backend/src/main/java/com/bokagent/plugin/VersionResolver.java`)

```java
@Component
@Slf4j
public class VersionResolver {
    
    private final Map<String, List<DependencyConstraint>> dependencyGraph = new ConcurrentHashMap<>();
    
    /**
     * 解析插件依赖
     */
    public ResolutionResult resolveDependencies(PluginMetadata plugin) {
        List<DependencyConstraint> dependencies = plugin.getDependencies();
        List<VersionConflict> conflicts = new ArrayList<>();
        List<String> resolvedVersions = new ArrayList<>();
        
        for (DependencyConstraint dep : dependencies) {
            // 检查依赖是否满足版本约束
            if (!isVersionCompatible(dep.getRequiredVersion(), dep.getInstalledVersion())) {
                conflicts.add(new VersionConflict(
                    dep.getName(),
                    dep.getRequiredVersion(),
                    dep.getInstalledVersion()
                ));
            } else {
                resolvedVersions.add(dep.getName() + "@" + dep.getInstalledVersion());
            }
        }
        
        return new ResolutionResult(conflicts.isEmpty(), conflicts, resolvedVersions);
    }
    
    /**
     * 检查版本兼容性（语义化版本）
     */
    private boolean isVersionCompatible(String required, String installed) {
        // 解析语义化版本 (major.minor.patch)
        String[] requiredParts = required.split("\\.");
        String[] installedParts = installed.split("\\.");
        
        int requiredMajor = Integer.parseInt(requiredParts[0]);
        int installedMajor = Integer.parseInt(installedParts[0]);
        
        // 主版本号必须匹配
        if (requiredMajor != installedMajor) {
            return false;
        }
        
        // 次版本号：安装的必须 >= 要求的
        if (requiredParts.length > 1 && installedParts.length > 1) {
            int requiredMinor = Integer.parseInt(requiredParts[1]);
            int installedMinor = Integer.parseInt(installedParts[1]);
            
            if (installedMinor < requiredMinor) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 检查插件与系统的兼容性
     */
    public CompatibilityCheckResult checkCompatibility(PluginMetadata plugin) {
        CompatibilityCheckResult result = new CompatibilityCheckResult();
        
        // 检查JDK版本
        String requiredJdk = plugin.getRequiredJdk();
        String currentJdk = System.getProperty("java.version");
        if (!isJdkCompatible(requiredJdk, currentJdk)) {
            result.addIssue("JDK版本不兼容: 要求 " + requiredJdk + ", 当前 " + currentJdk);
        }
        
        // 检查Spring Boot版本
        String requiredSpringBoot = plugin.getRequiredSpringBoot();
        String currentSpringBoot = SpringVersion.getVersion();
        if (!isVersionCompatible(requiredSpringBoot, currentSpringBoot)) {
            result.addIssue("Spring Boot版本不兼容");
        }
        
        // 检查依赖冲突
        ResolutionResult depResult = resolveDependencies(plugin);
        if (!depResult.isSuccess()) {
            result.addIssues(depResult.getConflicts().stream()
                .map(c -> c.toString())
                .toList());
        }
        
        return result;
    }
}
```

#### 2.2 兼容性测试器 (`backend/src/main/java/com/bokagent/compatibility/CompatibilityTester.java`)

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class CompatibilityTester {
    
    private final VersionResolver versionResolver;
    private final ToolRegistry toolRegistry;
    
    /**
     * 运行插件兼容性测试
     */
    public TestReport testPluginCompatibility(String pluginId) {
        TestReport report = new TestReport(pluginId, "Plugin Compatibility Test");
        
        try {
            // 1. 版本兼容性检查
            log.info("检查版本兼容性...");
            PluginMetadata metadata = loadPluginMetadata(pluginId);
            CompatibilityCheckResult versionCheck = versionResolver.checkCompatibility(metadata);
            
            if (!versionCheck.isCompatible()) {
                report.addFailure("版本兼容性检查失败", versionCheck.getIssues());
                return report;
            }
            report.addSuccess("版本兼容性检查通过");
            
            // 2. API兼容性测试
            log.info("测试API兼容性...");
            boolean apiCompatible = testApiCompatibility(pluginId);
            if (!apiCompatible) {
                report.addFailure("API兼容性测试失败");
                return report;
            }
            report.addSuccess("API兼容性测试通过");
            
            // 3. 功能测试
            log.info("运行功能测试...");
            List<TestResult> functionalTests = runFunctionalTests(pluginId);
            report.addTestResults(functionalTests);
            
            // 4. 性能测试
            log.info("运行性能测试...");
            PerformanceMetrics metrics = runPerformanceTest(pluginId);
            report.addPerformanceMetrics(metrics);
            
            report.setOverallStatus(report.allPassed() ? "PASSED" : "FAILED");
            
        } catch (Exception e) {
            log.error("兼容性测试失败", e);
            report.addFailure("测试执行异常", List.of(e.getMessage()));
            report.setOverallStatus("ERROR");
        }
        
        return report;
    }
    
    /**
     * 运行工具兼容性测试
     */
    public TestReport testToolCompatibility(String toolId) {
        TestReport report = new TestReport(toolId, "Tool Compatibility Test");
        
        try {
            // 1. 检查工具定义
            ToolDefinition definition = toolRegistry.getToolDefinition(toolId);
            if (definition == null) {
                report.addFailure("工具未找到");
                return report;
            }
            report.addSuccess("工具定义验证通过");
            
            // 2. 参数验证测试
            log.info("测试参数验证...");
            boolean paramValidationPassed = testParameterValidation(toolId);
            if (!paramValidationPassed) {
                report.addFailure("参数验证测试失败");
                return report;
            }
            report.addSuccess("参数验证测试通过");
            
            // 3. 执行测试
            log.info("运行执行测试...");
            ToolResult result = executeToolTest(toolId);
            if (!result.isSuccess()) {
                report.addFailure("工具执行测试失败", List.of(result.getErrorMessage()));
                return report;
            }
            report.addSuccess("工具执行测试通过");
            
            report.setOverallStatus("PASSED");
            
        } catch (Exception e) {
            log.error("工具兼容性测试失败", e);
            report.addFailure("测试执行异常", List.of(e.getMessage()));
            report.setOverallStatus("ERROR");
        }
        
        return report;
    }
}
```

#### 2.3 测试报告 (`backend/src/main/java/com/bokagent/compatibility/TestReport.java`)

```java
@Data
public class TestReport {
    
    private String targetId;
    private String testName;
    private String overallStatus; // PASSED, FAILED, ERROR
    private List<TestResult> testResults = new ArrayList<>();
    private List<String> failures = new ArrayList<>();
    private PerformanceMetrics performanceMetrics;
    private LocalDateTime executedAt;
    
    public void addSuccess(String testName) {
        testResults.add(TestResult.success(testName));
    }
    
    public void addFailure(String testName, List<String> errors) {
        failures.add(testName + ": " + String.join(", ", errors));
        testResults.add(TestResult.failure(testName, errors));
    }
    
    public boolean allPassed() {
        return failures.isEmpty() && testResults.stream().allMatch(TestResult::isSuccess);
    }
}
```

### 3. 异步调用优化

#### 3.1 异步执行器 (`backend/src/main/java/com/bokagent/async/AsyncExecutor.java`)

```java
@Component
@Slf4j
public class AsyncExecutor {
    
    @Autowired
    private ThreadPoolTaskExecutor virtualThreadExecutor; // JDK 21虚拟线程池
    
    /**
     * 异步执行工作流
     */
    public CompletableFuture<ExecutionResult> executeWorkflowAsync(
            Long workflowId, Map<String, Object> input) {
        
        return CompletableFuture.supplyAsync(() -> {
            log.info("开始异步执行工作流: {}", workflowId);
            
            // 执行工作流逻辑
            return workflowExecutionService.execute(workflowId, input);
            
        }, virtualThreadExecutor).exceptionally(ex -> {
            log.error("工作流异步执行失败", ex);
            return ExecutionResult.failure(ex.getMessage());
        });
    }
    
    /**
     * 并行执行多个工具
     */
    public CompletableFuture<List<ToolResult>> executeToolsParallel(
            List<ToolExecutionRequest> requests) {
        
        List<CompletableFuture<ToolResult>> futures = requests.stream()
            .map(request -> CompletableFuture.supplyAsync(() -> {
                return toolExecutor.execute(
                    request.getToolId(),
                    request.getParameters(),
                    request.getContext(),
                    request.getConfig()
                );
            }, virtualThreadExecutor))
            .toList();
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .toList());
    }
    
    /**
     * 异步调用远程MCP工具
     */
    public CompletableFuture<Object> callRemoteMcpToolAsync(
            String serverUrl, String toolName, Map<String, Object> arguments) {
        
        return CompletableFuture.supplyAsync(() -> {
            return mcpClient.callRemoteTool(serverUrl, toolName, arguments);
        }, virtualThreadExecutor);
    }
}
```

#### 3.2 异步配置 (`backend/src/main/java/com/bokagent/async/AsyncConfig.java`)

```java
@Configuration
@EnableAsync
public class AsyncConfig {
    
    /**
     * 虚拟线程池配置 (JDK 21)
     */
    @Bean(name = "virtualThreadExecutor")
    public Executor virtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
    
    /**
     * 传统线程池（用于兼容）
     */
    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("bokagent-async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
}
```

### 4. 缓存服务

#### 4.1 统一缓存服务 (`backend/src/main/java/com/bokagent/cache/CacheService.java`)

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Value("${cache.default-ttl:3600}")
    private long defaultTtlSeconds;
    
    /**
     * 获取缓存
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                log.debug("缓存命中: {}", key);
                return (T) value;
            }
        } catch (Exception e) {
            log.warn("缓存读取失败: {}", key, e);
        }
        return null;
    }
    
    /**
     * 设置缓存
     */
    public void put(String key, Object value, long ttl, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl, unit);
            log.debug("缓存已设置: {}", key);
        } catch (Exception e) {
            log.warn("缓存设置失败: {}", key, e);
        }
    }
    
    /**
     * 删除缓存
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }
    
    /**
     * 按模式删除缓存
     */
    public void deleteByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
    
    /**
     * 获取或计算缓存
     */
    public <T> T getOrCompute(String key, Callable<T> loader, long ttl, TimeUnit unit) {
        T value = get(key, (Class<T>) Object.class);
        if (value != null) {
            return value;
        }
        
        try {
            value = loader.call();
            put(key, value, ttl, unit);
            return value;
        } catch (Exception e) {
            throw new RuntimeException("缓存计算失败", e);
        }
    }
}
```

### 5. 配置文件更新

#### 5.1 application.yml (增强配置)

```yaml
# 重试配置
retry:
  default:
    max-attempts: 3
    initial-delay-ms: 1000
    backoff-multiplier: 2.0
    max-delay-ms: 30000
    retryable-exceptions:
      - java.net.SocketTimeoutException
      - java.io.IOException

# 超时配置
timeout:
  tool-execution: 30000      # 工具执行超时30秒
  llm-call: 60000            # LLM调用超时60秒
  tts-synthesis: 120000      # TTS合成超时120秒
  mcp-request: 10000         # MCP请求超时10秒
  workflow-execution: 300000 # 工作流执行超时5分钟

# 缓存配置
cache:
  enabled: true
  default-ttl: 3600          # 默认缓存1小时
  tool-result-ttl: 1800      # 工具结果缓存30分钟
  llm-response-ttl: 7200     # LLM响应缓存2小时
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    database: 0

# 异步执行配置
spring:
  task:
    execution:
      executor-type: virtual  # 使用JDK 21虚拟线程
      pool:
        core-size: 10
        max-size: 100
        queue-capacity: 1000

# 版本兼容性检查
compatibility:
  check-on-install: true     # 安装时检查兼容性
  strict-mode: false         # 严格模式（阻止不兼容的安装）
  auto-update: false         # 自动更新依赖
```

---

## 实施步骤 (增强部分)

### 阶段2: MCP协议实现 (3-4天)

**Task 2.1-2.3**: [保持不变]

### 阶段3: 工具注册系统 (4-5天) ⚠️ 增加

**Task 3.1: 实现工具注册中心**
- [保持不变]

**Task 3.2: 实现重试和回退机制** ⭐ 新增
- 开发RetryHandler重试处理器（指数退避算法）
- 实现FallbackStrategy回退策略（主备切换）
- 开发TimeoutController超时控制器
- 实现ToolCacheManager工具缓存管理器
- 编写单元测试验证重试、回退、超时、缓存功能

**Task 3.3: 开发内置工具**
- [保持不变]

**Task 3.4: 实现Function Calling集成**
- [保持不变]

**Task 3.5: 实现增强的工具执行器** ⭐ 新增
- 开发ToolExecutor整合重试、回退、超时、缓存
- 实现ExecutionConfig执行配置类
- 在ToolNode中集成增强的执行器
- 添加执行监控和日志

### 阶段4: 核心引擎开发 (3-4天)

**Task 4.1-4.3**: [保持不变]

### 阶段5: LLM和TTS集成 (2-3天)

**Task 5.1-5.2**: [保持不变]

### 阶段6: 工具市场和插件市场 (3-4天) ⚠️ 增加

**Task 6.1: 实现工具管理API**
- [保持不变]

**Task 6.2: 实现版本依赖管理** ⭐ 新增
- 开发VersionResolver版本依赖解析器
- 实现语义化版本比较逻辑
- 在PluginService中添加依赖检查
- 在ToolService中添加版本兼容性验证

**Task 6.3: 实现兼容性测试框架** ⭐ 新增
- 开发CompatibilityTester兼容性测试器
- 实现TestReport测试报告生成
- 创建自动化测试套件（插件、工具、MCP协议）
- 在CI/CD中集成兼容性测试

**Task 6.4: 开发工具市场前端**
- [保持不变]

**Task 6.5: 实现插件市场API和前端**
- [保持不变]

### 阶段7: 前端工作流编辑器 (3-4天)

**Task 7.1-7.3**: [保持不变]

### 阶段8: 调试和执行功能 (3-4天) ⚠️ 增加

**Task 8.1: 实现WebSocket实时通信**
- [保持不变]

**Task 8.2: 开发调试抽屉**
- [保持不变]

**Task 8.3: 实现异步执行和缓存** ⭐ 新增
- 开发AsyncExecutor异步执行器（虚拟线程）
- 实现CacheService统一缓存服务
- 在工作流执行中集成异步调用
- 实现LLM响应缓存和工具结果缓存

**Task 8.4: 实现工作流执行API**
- [保持不变，但需集成异步和缓存]

### 阶段9: Docker部署配置 (1-2天)

**Task 9.1-9.3**: [保持不变]

### 阶段10: 示例插件和工具开发 (2-3天)

**Task 10.1-10.2**: [保持不变]

### 阶段11: 完善和优化 (3-4天) ⚠️ 增加

**Task 11.1: 错误处理和用户反馈**
- [保持不变]

**Task 11.2: Redis缓存优化**
- 实现LLM响应缓存策略
- 实现工具结果缓存策略
- 实现工作流配置缓存
- 添加缓存预热和清理机制

**Task 11.3: 性能监控和优化**
- 添加工具执行耗时统计
- 实现缓存命中率监控
- 添加异步任务队列监控
- 实现性能指标导出（Prometheus格式）

**Task 11.4: 文档和测试**
- 编写README.md（包含重试、缓存、异步使用说明）
- 补充单元测试（重试机制、缓存、异步）
- 编写兼容性测试指南
- 编写用户使用手册
- 编写插件/工具开发者指南

---

## 关键技术点总结

1. **重试机制**: 指数退避算法，可配置重试次数、延迟、可重试异常类型
2. **回退策略**: 主备工具切换，多级fallback支持
3. **超时控制**: 细粒度超时配置（工具、LLM、TTS、MCP、工作流）
4. **本地缓存**: Redis缓存工具结果、LLM响应，可配置TTL
5. **异步调用**: JDK 21虚拟线程，高并发执行工作流和工具
6. **版本管理**: 语义化版本解析，依赖冲突检测
7. **兼容性测试**: 自动化测试框架，版本/API/功能/性能全面测试
8. **MCP协议双向支持**: Server + Client模式
9. **工具注册系统三重集成**: 工作流节点 + Function Calling + MCP Tools
10. **插件系统**: Java SPI + 动态类加载 + 热插拔

---

## 潜在风险和解决方案

| 风险 | 影响 | 解决方案 |
|------|------|----------|
| 重试导致雪崩 | 大量重试加剧系统负载 | 限制最大重试次数，添加熔断器，设置合理退避时间 |
| 缓存不一致 | 返回过期数据 | 设置合理TTL，提供缓存失效API，监听数据变更 |
| 虚拟线程资源泄漏 | 内存溢出 | 监控虚拟线程数量，设置上限，及时清理完成任务 |
| 版本冲突 | 插件/工具无法加载 | 严格的版本检查，依赖隔离（独立ClassLoader） |
| 兼容性测试覆盖不全 | 生产环境出现问题 | 建立全面的测试用例库，CI/CD自动运行，灰度发布 |
| 异步任务丢失 | 任务执行结果丢失 | 持久化任务状态到数据库，实现任务恢复机制 |
| 超时设置不当 | 过早中断或长时间等待 | 根据历史数据动态调整超时，提供配置接口 |
| 回退工具也不可用 | 最终仍失败 | 多级fallback，最后返回友好错误提示 |

---

## 验收标准

1. ✅ 可在画板上拖拽添加5种标准节点和插件节点并连线
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
19. ✅ 工具调用支持重试机制（可配置重试次数和退避策略）
20. ✅ 工具调用支持回退机制（主备工具切换）
21. ✅ 所有工具调用有超时控制（可配置超时时间）
22. ✅ 工具结果和LLM响应有缓存机制（Redis，可配置TTL）
23. ✅ 工作流和工具支持异步执行（JDK 21虚拟线程）
24. ✅ 插件和工具有版本依赖管理和兼容性检查
25. ✅ 提供兼容性测试框架和自动化测试套件
26. ✅ 缓存命中率、执行耗时等性能指标可监控

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

# 6. 运行兼容性测试
docker-compose exec backend java -jar /app.jar --run-compatibility-tests

# 7. 停止服务
docker-compose down
```

---

## 下一步行动

等待您确认此计划后，我将按照以下顺序开始实施：
1. 初始化前后端项目结构和SDK模块
2. 实现MCP协议（Server + Client + 传输层）
3. 实现工具注册系统和重试/回退/超时/缓存机制
4. 实现版本依赖管理和兼容性测试框架
5. 集成LangGraph4J和Spring AI
6. 实现异步执行和缓存服务
7. 实现插件管理系统和热加载机制
8. 开发前端可视化编辑器、工具市场和插件市场
9. 配置Docker Compose一键部署
10. 开发示例插件和工具
11. 完善测试和文档

请确认是否开始执行，或提出需要调整的地方。