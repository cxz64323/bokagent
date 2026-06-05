package com.bokagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bokagent.entity.Workflow;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工作流Mapper
 */
@Mapper
public interface WorkflowMapper extends BaseMapper<Workflow> {
}
