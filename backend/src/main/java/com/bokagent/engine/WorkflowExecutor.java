package com.bokagent.engine;

import com.bokagent.entity.Workflow;
import java.util.Map;

/**
 * 工作流执行器统一接口
 * 所有引擎实现都必须遵循此契约
 */
public interface WorkflowExecutor {
    
    /**
     * 执行工作流
     * @param workflow 工作流定义
     * @param inputData 输入数据
     * @return 执行结果
     */
    ExecutionResult execute(Workflow workflow, Map<String, Object> inputData);
    
    /**
     * 获取引擎名称
     * @return 引擎名称标识
     */
    String getEngineName();
}
