package com.bokagent.entity;

import lombok.Data;

/**
 * 工作流边（连接线）
 */
@Data
public class Edge {
    private String id;
    private String source;
    private String target;
}
