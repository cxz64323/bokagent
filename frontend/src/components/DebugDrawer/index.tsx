import React, { useState } from 'react';
import { Drawer, Form, Input, Button, Space, message, Card } from 'antd';
import { executionApi } from '../../services/workflowApi';

interface DebugDrawerProps {
  visible: boolean;
  onClose: () => void;
  nodes: any[];
  edges: any[];
}

const DebugDrawer: React.FC<DebugDrawerProps> = ({ visible, onClose, nodes, edges }) => {
  const [testData, setTestData] = useState('{}');
  const [output, setOutput] = useState('');
  const [loading, setLoading] = useState(false);

  const handleExecute = async () => {
    if (nodes.length === 0) {
      message.warning('请先添加节点');
      return;
    }

    setLoading(true);
    try {
      const input = JSON.parse(testData);
      
      // TODO: 使用真实的工作流ID（从useWorkflow获取）
      const workflowId = 1;
      
      // 调用后端执行工作流
      const response = await fetch(`/api/workflow/${workflowId}/execute`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(input),
      });

      const result = await response.json();
      
      if (result.code === 200) {
        const executionRecord = result.data;
        setOutput(JSON.stringify({
          status: executionRecord.status,
          message: executionRecord.status === 'SUCCESS' ? '工作流执行成功 ✨' : '执行失败',
          output: executionRecord.outputData,
          error: executionRecord.error,
          startTime: executionRecord.startTime,
          endTime: executionRecord.endTime,
        }, null, 2));
        
        message.success(executionRecord.status === 'SUCCESS' ? '执行完成！🎉' : '执行失败');
      } else {
        throw new Error(result.message || '执行失败');
      }

    } catch (error: any) {
      console.error('执行失败:', error);
      setOutput(JSON.stringify({
        status: 'FAILED',
        error: error.message || '执行失败',
      }, null, 2));
      message.error('执行失败');
    } finally {
      setLoading(false);
    }
  };

  const handleTestDataChange = (value: string) => {
    try {
      JSON.parse(value);
      setTestData(value);
    } catch (e) {
      // JSON格式错误，但仍允许输入
      setTestData(value);
    }
  };

  return (
    <Drawer
      title="调试面板"
      placement="right"
      width={450}
      open={visible}
      onClose={onClose}
    >
      <Form layout="vertical">
        <Form.Item label="测试输入数据（JSON格式）">
          <Input.TextArea
            value={testData}
            onChange={(e) => handleTestDataChange(e.target.value)}
            rows={8}
            placeholder='{"input": "测试数据"}'
          />
        </Form.Item>

        <Form.Item>
          <Space>
            <Button 
              type="primary" 
              onClick={handleExecute} 
              loading={loading}
              disabled={nodes.length === 0}
            >
              执行工作流
            </Button>
            <Button onClick={() => setOutput('')}>清空输出</Button>
          </Space>
        </Form.Item>
      </Form>

      {output && (
        <Card 
          title="执行结果" 
          size="small" 
          style={{ marginTop: 16 }}
          bodyStyle={{ maxHeight: 400, overflow: 'auto' }}
        >
          <pre style={{ 
            whiteSpace: 'pre-wrap', 
            wordBreak: 'break-word',
            fontSize: 12,
            lineHeight: 1.5,
          }}>
            {output}
          </pre>
        </Card>
      )}

      <div style={{ marginTop: 16, padding: 12, background: '#f5f5f5', borderRadius: 4 }}>
        <div style={{ fontSize: 12, color: '#666' }}>
          <div><strong>节点数量:</strong> {nodes.length}</div>
          <div><strong>连接数量:</strong> {edges.length}</div>
        </div>
      </div>
    </Drawer>
  );
};

export default DebugDrawer;
