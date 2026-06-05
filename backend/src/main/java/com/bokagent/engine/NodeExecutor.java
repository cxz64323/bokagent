package com.bokagent.engine;

import com.bokagent.entity.Node;
import java.util.Map;

/**
 * 节点执行器接口
 */
public interface NodeExecutor {
    
    /**
     * 执行节点
     * @param node 节点定义
     * @param context 执行上下文（包含前面节点的输出）
     * @return 节点执行结果
     */
    Map<String, Object> execute(Node node, Map<String, Object> context);
    
    /**
     * 获取节点类型
     */
    String getNodeType();
}
