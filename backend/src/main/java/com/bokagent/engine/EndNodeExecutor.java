package com.bokagent.engine;

import com.bokagent.entity.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 结束节点执行器
 */
@Slf4j
@Component
public class EndNodeExecutor implements NodeExecutor {

    @Override
    public Map<String, Object> execute(Node node, Map<String, Object> context) {
        log.info("执行结束节点: {}", node.getId());
        
        Map<String, Object> result = new HashMap<>();
        result.put("nodeId", node.getId());
        result.put("nodeType", "end");
        result.put("status", "completed");
        result.put("timestamp", System.currentTimeMillis());
        
        // 传递最终的输出结果
        if (context != null) {
            result.put("finalOutput", context);
        }
        
        log.debug("结束节点执行完成");
        return result;
    }

    @Override
    public String getNodeType() {
        return "end";
    }
}
