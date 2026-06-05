package com.bokagent.entity;

import lombok.Data;

/**
 * 工作流节点
 */
@Data
public class Node {
    private String id;
    private String type; // start, llm, end
    private Position position;
    private NodeData data;
}
