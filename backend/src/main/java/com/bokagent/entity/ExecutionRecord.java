package com.bokagent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bokagent.handler.JsonbTypeHandler;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 执行记录实体
 */
@Data
@TableName("execution_records")
public class ExecutionRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long workflowId;
    
    @TableField(typeHandler = JsonbTypeHandler.class)
    private Map<String, Object> inputData;
    
    @TableField(typeHandler = JsonbTypeHandler.class)
    private Map<String, Object> outputData;
    
    private String status; // RUNNING, SUCCESS, FAILED
    
    private String error;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private LocalDateTime createdAt;
}
