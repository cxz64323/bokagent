package com.bokagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bokagent.entity.ExecutionRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 执行记录Mapper
 */
@Mapper
public interface ExecutionRecordMapper extends BaseMapper<ExecutionRecord> {
}
