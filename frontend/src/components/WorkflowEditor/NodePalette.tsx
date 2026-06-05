import React from 'react';
import { Card } from 'antd';
import { PlayCircleOutlined, RobotOutlined, StopOutlined } from '@ant-design/icons';

const nodeTypes = [
  { type: 'start', label: '开始节点', icon: <PlayCircleOutlined />, color: '#52c41a' },
  { type: 'llm', label: 'LLM节点', icon: <RobotOutlined />, color: '#1890ff' },
  { type: 'end', label: '结束节点', icon: <StopOutlined />, color: '#f5222d' },
];

const NodePalette: React.FC = () => {
  const onDragStart = (event: React.DragEvent, nodeType: string) => {
    event.dataTransfer.setData('application/reactflow', nodeType);
    event.dataTransfer.effectAllowed = 'move';
  };

  return (
    <div style={{ width: 200, borderRight: '1px solid #e8e8e8', background: '#fff', padding: '16px' }}>
      <h3 style={{ marginBottom: 16 }}>节点库</h3>
      
      {nodeTypes.map((node) => (
        <Card
          key={node.type}
          size="small"
          draggable
          onDragStart={(e) => onDragStart(e, node.type)}
          style={{ 
            marginBottom: 8, 
            cursor: 'move',
            borderColor: node.color,
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <span style={{ color: node.color, fontSize: 16 }}>{node.icon}</span>
            <span>{node.label}</span>
          </div>
        </Card>
      ))}
      
      <div style={{ marginTop: 16, fontSize: 12, color: '#999' }}>
        提示：拖拽节点到右侧画布
      </div>
    </div>
  );
};

export default NodePalette;
