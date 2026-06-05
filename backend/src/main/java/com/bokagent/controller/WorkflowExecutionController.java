package com.bokagent.controller;

import com.bokagent.common.Result;
import com.bokagent.entity.ExecutionRecord;
import com.bokagent.service.ExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 工作流执行控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/workflow")
@CrossOrigin(origins = "*")
public class WorkflowExecutionController {

    @Autowired
    private ExecutionService executionService;

    /**
     * 执行工作流
     * @param workflowId 工作流ID
     * @param inputData 输入数据
     * @return 执行结果
     */
    @PostMapping("/{workflowId}/execute")
    public Result<ExecutionRecord> executeWorkflow(
            @PathVariable Long workflowId,
            @RequestBody(required = false) Map<String, Object> inputData) {
        
        log.info("收到工作流执行请求，工作流ID: {}", workflowId);
        
        try {
            ExecutionRecord record = executionService.executeAndRecord(workflowId, inputData);
            return Result.success(record);
        } catch (Exception e) {
            log.error("工作流执行失败", e);
            return Result.error("执行失败: " + e.getMessage());
        }
    }

    /**
     * 获取执行记录详情
     * @param executionId 执行记录ID
     * @return 执行记录
     */
    @GetMapping("/execution/{executionId}")
    public Result<ExecutionRecord> getExecutionRecord(@PathVariable Long executionId) {
        log.info("获取执行记录: {}", executionId);
        
        ExecutionRecord record = executionService.getExecutionRecord(executionId);
        if (record == null) {
            return Result.error(404, "执行记录不存在");
        }
        
        return Result.success(record);
    }

    /**
     * 获取工作流的所有执行记录
     * @param workflowId 工作流ID
     * @return 执行记录列表
     */
    @GetMapping("/{workflowId}/executions")
    public Result<java.util.List<ExecutionRecord>> getExecutionRecords(@PathVariable Long workflowId) {
        log.info("获取工作流 {} 的执行记录", workflowId);
        
        java.util.List<ExecutionRecord> records = executionService.getExecutionRecords(workflowId);
        return Result.success(records);
    }
}
