package com.bokagent.controller;

import com.bokagent.common.Result;
import com.bokagent.entity.ExecutionRecord;
import com.bokagent.mapper.ExecutionRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 执行记录控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/executions")
@CrossOrigin(origins = "*")
public class ExecutionController {

    @Autowired
    private ExecutionRecordMapper executionRecordMapper;

    /**
     * 获取工作流的所有执行记录
     */
    @GetMapping("/workflow/{workflowId}")
    public Result<List<ExecutionRecord>> listExecutions(@PathVariable Long workflowId) {
        log.info("获取工作流 {} 的执行记录", workflowId);
        // TODO: 添加查询条件
        List<ExecutionRecord> records = executionRecordMapper.selectList(null);
        return Result.success(records);
    }

    /**
     * 获取执行记录详情
     */
    @GetMapping("/{id}")
    public Result<ExecutionRecord> getExecution(@PathVariable Long id) {
        log.info("获取执行记录: {}", id);
        ExecutionRecord record = executionRecordMapper.selectById(id);
        if (record == null) {
            return Result.error(404, "执行记录不存在");
        }
        return Result.success(record);
    }

    /**
     * 创建执行记录
     */
    @PostMapping
    public Result<ExecutionRecord> createExecution(@RequestBody ExecutionRecord record) {
        log.info("创建执行记录，工作流ID: {}", record.getWorkflowId());
        record.setStatus("RUNNING");
        record.setStartTime(LocalDateTime.now());
        record.setCreatedAt(LocalDateTime.now());
        executionRecordMapper.insert(record);
        return Result.success(record);
    }

    /**
     * 更新执行记录
     */
    @PutMapping("/{id}")
    public Result<ExecutionRecord> updateExecution(@PathVariable Long id, @RequestBody ExecutionRecord record) {
        log.info("更新执行记录: {}", id);
        ExecutionRecord existingRecord = executionRecordMapper.selectById(id);
        if (existingRecord == null) {
            return Result.error(404, "执行记录不存在");
        }
        
        record.setId(id);
        if ("SUCCESS".equals(record.getStatus()) || "FAILED".equals(record.getStatus())) {
            record.setEndTime(LocalDateTime.now());
        }
        executionRecordMapper.updateById(record);
        return Result.success(record);
    }
}
