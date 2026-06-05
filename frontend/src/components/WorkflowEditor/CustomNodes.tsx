import React from 'react';
import { Handle, Position } from '@xyflow/react';
import { PlayCircleOutlined, RobotOutlined, StopOutlined } from '@ant-design/icons';
import { Input } from 'antd';

// 开始节点
export const StartNode: React.FC<any> = ({ data }) => {
  return (
    <div style={{
      padding: '10px 20px',
      borderRadius: '8px',
      background: '#f6ffed',
      border: '2px solid #52c41a',
      color: '#52c41a',
      minWidth: 120,
      textAlign: 'center',
    }}>
      <Handle type="source" position={Position.Right} />
      <PlayCircleOutlined style={{ fontSize: 24 }} />
      <div style={{ marginTop: 8 }}>{data.label}</div>
    </div>
  );
};

// LLM节点
export const LLMNode: React.FC<any> = ({ data, id }) => {
  return (
    <div style={{
      padding: '15px',
      borderRadius: '8px',
      background: '#e6f7ff',
      border: '2px solid #1890ff',
      minWidth: 200,
    }}>
      <Handle type="target" position={Position.Left} />
      <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 10, color: '#1890ff' }}>
        <RobotOutlined style={{ fontSize: 20 }} />
        <strong>{data.label}</strong>
      </div>
      <Input.TextArea
        placeholder="输入提示词..."
        value={data.prompt || ''}
        onChange={(e) => {
          data.prompt = e.target.value;
        }}
        rows={3}
        size="small"
      />
      <Handle type="source" position={Position.Right} />
    </div>
  );
};

// 结束节点
export const EndNode: React.FC<any> = ({ data }) => {
  return (
    <div style={{
      padding: '10px 20px',
      borderRadius: '8px',
      background: '#fff1f0',
      border: '2px solid #f5222d',
      color: '#f5222d',
      minWidth: 120,
      textAlign: 'center',
    }}>
      <Handle type="target" position={Position.Left} />
      <StopOutlined style={{ fontSize: 24 }} />
      <div style={{ marginTop: 8 }}>{data.label}</div>
    </div>
  );
};

// 导出节点类型映射
export const nodeTypes = {
  start: StartNode,
  llm: LLMNode,
  end: EndNode,
};

export default { nodeTypes };
