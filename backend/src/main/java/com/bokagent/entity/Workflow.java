package com.bokagent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bokagent.handler.JsonbTypeHandler;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 工作流定义实体
 */
@Data
@TableName("workflows")
public class Workflow {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String description;
    
    @TableField(typeHandler = JsonbTypeHandler.class)
    private GraphData graphData;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
