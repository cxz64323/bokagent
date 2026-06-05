package com.bokagent.service;

import com.bokagent.engine.ExecutionResult;
import com.bokagent.engine.WorkflowEngineSelector;
import com.bokagent.engine.WorkflowExecutor;
import com.bokagent.entity.ExecutionRecord;
import com.bokagent.entity.Workflow;
import com.bokagent.mapper.ExecutionRecordMapper;
import com.bokagent.mapper.WorkflowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 执行服务 - 管理工作流的执行和记录
 */
@Slf4j
@Service
public class ExecutionService {

    @Autowired
    private WorkflowMapper workflowMapper;

    @Autowired
    private ExecutionRecordMapper executionRecordMapper;

    @Autowired
    private WorkflowEngineSelector engineSelector;

    /**
     * 执行工作流并记录执行历史
     * @param workflowId 工作流ID
     * @param inputData 输入数据
     * @return 执行记录
     */
    public ExecutionRecord executeAndRecord(Long workflowId, Map<String, Object> inputData) {
        log.info("开始执行工作流 ID: {}", workflowId);

        // 查询工作流
        Workflow workflow = workflowMapper.selectById(workflowId);
        if (workflow == null) {
            throw new IllegalArgumentException("工作流不存在: " + workflowId);
        }

        // 创建执行记录
        ExecutionRecord record = new ExecutionRecord();
        record.setWorkflowId(workflowId);
        record.setInputData(inputData);
        record.setStatus("RUNNING");
        record.setStartTime(LocalDateTime.now());
        record.setCreatedAt(LocalDateTime.now());

        executionRecordMapper.insert(record);
        log.info("创建执行记录 ID: {}", record.getId());

        try {
            // 执行工作流
            WorkflowExecutor engine = engineSelector.getEngine();
            log.info("使用引擎: {}", engine.getEngineName());
            ExecutionResult result = engine.execute(workflow, inputData);

            // 更新执行记录
            if (result.isSuccess()) {
                record.setOutputData(result.getOutput());
                record.setStatus("SUCCESS");
                log.info("工作流执行成功，耗时: {}ms", result.getExecutionTime());
            } else {
                record.setStatus("FAILED");
                record.setError(result.getError());
                log.error("工作流执行失败: {}", result.getError());
            }

            record.setEndTime(LocalDateTime.now());
            executionRecordMapper.updateById(record);

            return record;

        } catch (Exception e) {
            log.error("工作流执行异常", e);

            // 更新执行记录为失败
            record.setStatus("FAILED");
            record.setError(e.getMessage());
            record.setEndTime(LocalDateTime.now());
            executionRecordMapper.updateById(record);

            throw new RuntimeException("工作流执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取执行记录
     * @param executionId 执行记录ID
     * @return 执行记录
     */
    public ExecutionRecord getExecutionRecord(Long executionId) {
        return executionRecordMapper.selectById(executionId);
    }

    /**
     * 获取工作流的所有执行记录
     * @param workflowId 工作流ID
     * @return 执行记录列表
     */
    public java.util.List<ExecutionRecord> getExecutionRecords(Long workflowId) {
        // TODO: 添加按工作流ID查询的条件
        return executionRecordMapper.selectList(null);
    }
}
