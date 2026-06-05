package com.bokagent.engine;

import com.bokagent.entity.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 开始节点执行器
 */
@Slf4j
@Component
public class StartNodeExecutor implements NodeExecutor {

    @Override
    public Map<String, Object> execute(Node node, Map<String, Object> context) {
        log.info("执行开始节点: {}", node.getId());
        
        Map<String, Object> result = new HashMap<>();
        result.put("nodeId", node.getId());
        result.put("nodeType", "start");
        result.put("status", "started");
        result.put("timestamp", System.currentTimeMillis());
        
        // 将输入数据传递到上下文
        if (context != null && !context.isEmpty()) {
            result.putAll(context);
        }
        
        log.debug("开始节点执行完成: {}", result);
        return result;
    }

    @Override
    public String getNodeType() {
        return "start";
    }
}
