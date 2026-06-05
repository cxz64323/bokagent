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
      
      // 创建执行记录
      const record = {
        workflowId: 1, // TODO: 使用实际的工作流ID
        inputData: input,
      };

      const createResponse = await executionApi.createExecution(record);
      const executionId = createResponse.data.data.id;

      // TODO: 调用后端执行工作流
      // 目前先模拟执行结果
      setTimeout(() => {
        setOutput(JSON.stringify({
          status: 'SUCCESS',
          message: '工作流执行成功 ✨',
          output: '这是模拟的输出结果',
        }, null, 2));
        
        // 更新执行记录状态
        executionApi.updateExecution(executionId, {
          status: 'SUCCESS',
          outputData: { result: '模拟结果' },
        });
        
        message.success('执行完成！🎉');
      }, 1000);

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
