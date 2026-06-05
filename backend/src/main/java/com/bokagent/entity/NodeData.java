package com.bokagent.entity;

import lombok.Data;
import java.util.Map;

/**
 * 节点数据
 */
@Data
public class NodeData {
    private String label;
    private String prompt;
    private Map<String, Object> config;
}
