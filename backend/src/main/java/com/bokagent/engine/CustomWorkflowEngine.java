package com.bokagent.engine;

import com.bokagent.entity.Edge;
import com.bokagent.entity.GraphData;
import com.bokagent.entity.Node;
import com.bokagent.entity.Workflow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 自定义工作流引擎 - 基于拓扑排序的实现
 * 这是BokAgent的默认引擎，简单高效
 */
@Slf4j
@Service("customWorkflowEngine")
public class CustomWorkflowEngine implements WorkflowExecutor {

    @Autowired
    private StartNodeExecutor startNodeExecutor;

    @Autowired
    private LLMNodeExecutor llmNodeExecutor;

    @Autowired
    private EndNodeExecutor endNodeExecutor;

    private final Map<String, NodeExecutor> executorMap = new HashMap<>();

    public CustomWorkflowEngine() {
        // 注册节点执行器
        executorMap.put("start", startNodeExecutor);
        executorMap.put("llm", llmNodeExecutor);
        executorMap.put("end", endNodeExecutor);
    }

    @Override
    public ExecutionResult execute(Workflow workflow, Map<String, Object> inputData) {
        long startTime = System.currentTimeMillis();
        
        log.info("开始执行工作流: {} (ID: {}) [CustomEngine]", workflow.getName(), workflow.getId());
        
        try {
            GraphData graphData = workflow.getGraphData();
            if (graphData == null || graphData.getNodes() == null || graphData.getNodes().isEmpty()) {
                return ExecutionResult.failure("工作流图为空", System.currentTimeMillis() - startTime);
            }

            // 构建执行图
            Map<String, Node> nodeMap = buildNodeMap(graphData.getNodes());
            Map<String, List<String>> adjacencyList = buildAdjacencyList(graphData.getEdges());

            // 找到起始节点
            String startNodeId = findStartNode(graphData.getNodes());
            if (startNodeId == null) {
                return ExecutionResult.failure("未找到起始节点", System.currentTimeMillis() - startTime);
            }

            // 执行工作流（拓扑排序）
            Map<String, Object> context = new HashMap<>(inputData != null ? inputData : new HashMap<>());
            Map<String, Object> finalOutput = executeWorkflow(nodeMap, adjacencyList, startNodeId, context);

            long executionTime = System.currentTimeMillis() - startTime;
            log.info("工作流执行完成，耗时: {}ms", executionTime);

            return ExecutionResult.success(finalOutput, executionTime);

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("工作流执行失败", e);
            return ExecutionResult.failure("执行失败: " + e.getMessage(), executionTime);
        }
    }

    @Override
    public String getEngineName() {
        return "Custom";
    }

    /**
     * 构建节点映射
     */
    private Map<String, Node> buildNodeMap(List<Node> nodes) {
        return nodes.stream()
                .collect(Collectors.toMap(Node::getId, node -> node));
    }

    /**
     * 构建邻接表（用于确定执行顺序）
     */
    private Map<String, List<String>> buildAdjacencyList(List<Edge> edges) {
        Map<String, List<String>> adjacencyList = new HashMap<>();
        
        for (Edge edge : edges) {
            adjacencyList.computeIfAbsent(edge.getSource(), k -> new ArrayList<>())
                    .add(edge.getTarget());
        }
        
        return adjacencyList;
    }

    /**
     * 找到起始节点
     */
    private String findStartNode(List<Node> nodes) {
        return nodes.stream()
                .filter(node -> "start".equals(node.getType()))
                .map(Node::getId)
                .findFirst()
                .orElse(null);
    }

    /**
     * 执行工作流（按拓扑顺序执行节点）
     */
    private Map<String, Object> executeWorkflow(
            Map<String, Node> nodeMap,
            Map<String, List<String>> adjacencyList,
            String startNodeId,
            Map<String, Object> initialContext) {

        Map<String, Object> context = new HashMap<>(initialContext);
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        
        queue.offer(startNodeId);

        while (!queue.isEmpty()) {
            String currentNodeId = queue.poll();
            
            if (visited.contains(currentNodeId)) {
                continue;
            }
            
            visited.add(currentNodeId);
            
            Node currentNode = nodeMap.get(currentNodeId);
            if (currentNode == null) {
                log.warn("节点不存在: {}", currentNodeId);
                continue;
            }

            log.debug("执行节点: {} (类型: {})", currentNodeId, currentNode.getType());

            // 获取对应的执行器
            NodeExecutor executor = executorMap.get(currentNode.getType());
            if (executor == null) {
                log.warn("未找到节点类型的执行器: {}", currentNode.getType());
                continue;
            }

            // 执行节点
            Map<String, Object> nodeOutput = executor.execute(currentNode, context);
            
            // 更新上下文
            context.putAll(nodeOutput);
            context.put("lastNodeOutput", nodeOutput);

            // 将下一个节点加入队列
            List<String> nextNodes = adjacencyList.getOrDefault(currentNodeId, new ArrayList<>());
            queue.addAll(nextNodes);
        }

        return context;
    }
}
