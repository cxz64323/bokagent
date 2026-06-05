import React, { useCallback, useState } from 'react';
import { ReactFlowProvider, ReactFlow, Background, Controls, MiniMap, useNodesState, useEdgesState, addEdge } from '@xyflow/react';
import '@xyflow/react/dist/style.css';
import { Button, Space, message } from 'antd';
import { SaveOutlined, PlayCircleOutlined } from '@ant-design/icons';
import NodePalette from './NodePalette';
import CustomNodes, { nodeTypes } from './CustomNodes';
import DebugDrawer from '../DebugDrawer';
import { useWorkflow } from '../../hooks/useWorkflow';

const WorkflowEditor: React.FC = () => {
  const [nodes, setNodes, onNodesChange] = useNodesState([]);
  const [edges, setEdges, onEdgesChange] = useEdgesState([]);
  const [reactFlowInstance, setReactFlowInstance] = useState<any>(null);
  const [debugVisible, setDebugVisible] = useState(false);
  const { saveWorkflow, workflowId } = useWorkflow();

  const onConnect = useCallback(
    (params: any) => setEdges((eds) => addEdge(params, eds)),
    [setEdges]
  );

  const onDragOver = useCallback((event: React.DragEvent) => {
    event.preventDefault();
    event.dataTransfer.dropEffect = 'move';
  }, []);

  const onDrop = useCallback(
    (event: React.DragEvent) => {
      event.preventDefault();

      if (!reactFlowInstance) return;

      const type = event.dataTransfer.getData('application/reactflow');
      if (!type) return;

      const position = reactFlowInstance.screenToFlowPosition({
        x: event.clientX,
        y: event.clientY,
      });

      const newNode = {
        id: `node-${Date.now()}`,
        type,
        position,
        data: { label: `${type} 节点` },
      };

      setNodes((nds) => nds.concat(newNode));
    },
    [reactFlowInstance, setNodes]
  );

  const handleSave = async () => {
    try {
      await saveWorkflow(nodes, edges);
      message.success('工作流保存成功！✨');
    } catch (error) {
      message.error('保存失败');
      console.error(error);
    }
  };

  return (
    <div style={{ display: 'flex', height: '100vh' }}>
      <NodePalette />
      
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
        {/* 工具栏 */}
        <div style={{ padding: '10px', borderBottom: '1px solid #e8e8e8', background: '#fff' }}>
          <Space>
            <Button type="primary" icon={<SaveOutlined />} onClick={handleSave}>
              保存
            </Button>
            <Button icon={<PlayCircleOutlined />} onClick={() => setDebugVisible(true)}>
              调试
            </Button>
          </Space>
        </div>

        {/* React Flow 画布 */}
        <div style={{ flex: 1 }}>
          <ReactFlowProvider>
            <ReactFlow
              nodes={nodes}
              edges={edges}
              onNodesChange={onNodesChange}
              onEdgesChange={onEdgesChange}
              onConnect={onConnect}
              onInit={setReactFlowInstance}
              onDrop={onDrop}
              onDragOver={onDragOver}
              nodeTypes={nodeTypes}
              fitView
            >
              <Background />
              <Controls />
              <MiniMap />
            </ReactFlow>
          </ReactFlowProvider>
        </div>
      </div>

      {/* 调试抽屉 */}
      <DebugDrawer 
        visible={debugVisible} 
        onClose={() => setDebugVisible(false)}
        nodes={nodes}
        edges={edges}
      />
    </div>
  );
};

export default WorkflowEditor;
