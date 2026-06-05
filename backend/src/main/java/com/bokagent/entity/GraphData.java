package com.bokagent.entity;

import lombok.Data;
import java.util.List;

/**
 * 工作流图数据结构
 */
@Data
public class GraphData {
    private List<Node> nodes;
    private List<Edge> edges;
    private Viewport viewport;
}
