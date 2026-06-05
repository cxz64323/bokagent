package com.bokagent.controller;

import com.bokagent.common.Result;
import com.bokagent.entity.Workflow;
import com.bokagent.mapper.WorkflowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 工作流控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/workflows")
@CrossOrigin(origins = "*")
public class WorkflowController {

    @Autowired
    private WorkflowMapper workflowMapper;

    /**
     * 获取所有工作流列表
     */
    @GetMapping
    public Result<List<Workflow>> listWorkflows() {
        log.info("获取工作流列表");
        List<Workflow> workflows = workflowMapper.selectList(null);
        return Result.success(workflows);
    }

    /**
     * 根据ID获取工作流
     */
    @GetMapping("/{id}")
    public Result<Workflow> getWorkflow(@PathVariable Long id) {
        log.info("获取工作流: {}", id);
        Workflow workflow = workflowMapper.selectById(id);
        if (workflow == null) {
            return Result.error(404, "工作流不存在");
        }
        return Result.success(workflow);
    }

    /**
     * 创建工作流
     */
    @PostMapping
    public Result<Workflow> createWorkflow(@RequestBody Workflow workflow) {
        log.info("创建工作流: {}", workflow.getName());
        workflow.setCreatedAt(LocalDateTime.now());
        workflow.setUpdatedAt(LocalDateTime.now());
        workflowMapper.insert(workflow);
        return Result.success(workflow);
    }

    /**
     * 更新工作流
     */
    @PutMapping("/{id}")
    public Result<Workflow> updateWorkflow(@PathVariable Long id, @RequestBody Workflow workflow) {
        log.info("更新工作流: {}", id);
        Workflow existingWorkflow = workflowMapper.selectById(id);
        if (existingWorkflow == null) {
            return Result.error(404, "工作流不存在");
        }
        
        workflow.setId(id);
        workflow.setCreatedAt(existingWorkflow.getCreatedAt());
        workflow.setUpdatedAt(LocalDateTime.now());
        workflowMapper.updateById(workflow);
        return Result.success(workflow);
    }

    /**
     * 删除工作流
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteWorkflow(@PathVariable Long id) {
        log.info("删除工作流: {}", id);
        Workflow workflow = workflowMapper.selectById(id);
        if (workflow == null) {
            return Result.error(404, "工作流不存在");
        }
        workflowMapper.deleteById(id);
        return Result.success();
    }
}
