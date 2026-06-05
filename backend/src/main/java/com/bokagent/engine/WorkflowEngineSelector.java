package com.bokagent.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 工作流引擎选择器
 * 根据配置动态选择使用哪个引擎实现
 */
@Slf4j
@Component
public class WorkflowEngineSelector {

    @Autowired
    @Qualifier("customWorkflowEngine")
    private WorkflowExecutor customEngine;

    @Autowired(required = false)
    @Qualifier("langGraphWorkflowEngine")
    private WorkflowExecutor langGraphEngine;

    @Value("${workflow.engine:custom}")
    private String engineType;

    /**
     * 获取当前配置的引擎实例
     * @return 工作流执行器
     */
    public WorkflowExecutor getEngine() {
        if ("langgraph4j".equalsIgnoreCase(engineType) && langGraphEngine != null) {
            log.info("使用LangGraph4J引擎");
            return langGraphEngine;
        } else {
            if ("langgraph4j".equalsIgnoreCase(engineType) && langGraphEngine == null) {
                log.warn("配置为LangGraph4J引擎但未找到实现，回退到Custom引擎");
            }
            log.debug("使用Custom自定义引擎");
            return customEngine;
        }
    }

    /**
     * 获取当前使用的引擎名称
     * @return 引擎名称
     */
    public String getCurrentEngineName() {
        return getEngine().getEngineName();
    }
}
