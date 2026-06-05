package com.bokagent.engine;

import com.bokagent.entity.Node;
import com.bokagent.service.LLMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * LLM节点执行器
 */
@Slf4j
@Component
public class LLMNodeExecutor implements NodeExecutor {

    @Autowired
    private LLMService llmService;

    @Override
    public Map<String, Object> execute(Node node, Map<String, Object> context) {
        log.info("执行LLM节点: {}", node.getId());
        
        String prompt = node.getData() != null ? node.getData().getPrompt() : null;
        
        if (prompt == null || prompt.isEmpty()) {
            prompt = "请根据上下文信息生成回复";
        }
        
        try {
            // 调用LLM服务
            String response = llmService.chat(prompt, context);
            
            Map<String, Object> result = new HashMap<>();
            result.put("nodeId", node.getId());
            result.put("nodeType", "llm");
            result.put("status", "completed");
            result.put("output", response);
            result.put("timestamp", System.currentTimeMillis());
            
            // 将LLM的输出添加到上下文
            result.putAll(context != null ? context : new HashMap<>());
            result.put("llmResponse", response);
            
            log.debug("LLM节点执行完成，回复长度: {}", response.length());
            return result;
            
        } catch (Exception e) {
            log.error("LLM节点执行失败", e);
            
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("nodeId", node.getId());
            errorResult.put("nodeType", "llm");
            errorResult.put("status", "failed");
            errorResult.put("error", e.getMessage());
            errorResult.put("timestamp", System.currentTimeMillis());
            
            return errorResult;
        }
    }

    @Override
    public String getNodeType() {
        return "llm";
    }
}
