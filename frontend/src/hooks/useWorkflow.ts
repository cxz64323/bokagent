import { useState, useEffect } from 'react';
import { workflowApi } from '../services/workflowApi';

export const useWorkflow = () => {
  const [workflowId, setWorkflowId] = useState<number | null>(null);
  const [loading, setLoading] = useState(false);

  // 保存工作流
  const saveWorkflow = async (nodes: any[], edges: any[]) => {
    setLoading(true);
    try {
      const workflowData = {
        name: '未命名工作流',
        description: '工作流描述',
        graphData: {
          nodes,
          edges,
          viewport: { x: 0, y: 0, zoom: 1 },
        },
      };

      let response;
      if (workflowId) {
        // 更新已有工作流
        response = await workflowApi.updateWorkflow(workflowId, workflowData);
      } else {
        // 创建新工作流
        response = await workflowApi.createWorkflow(workflowData);
        setWorkflowId(response.data.data.id);
      }

      return response.data.data;
    } catch (error) {
      console.error('保存工作流失败:', error);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  // 加载工作流
  const loadWorkflow = async (id: number) => {
    setLoading(true);
    try {
      const response = await workflowApi.getWorkflow(id);
      setWorkflowId(id);
      return response.data.data;
    } catch (error) {
      console.error('加载工作流失败:', error);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  // 重置
  const reset = () => {
    setWorkflowId(null);
  };

  return {
    workflowId,
    loading,
    saveWorkflow,
    loadWorkflow,
    reset,
  };
};
