import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// 工作流相关API
export const workflowApi = {
  // 获取所有工作流
  listWorkflows: () => api.get('/workflows'),
  
  // 获取单个工作流
  getWorkflow: (id: number) => api.get(`/workflows/${id}`),
  
  // 创建工作流
  createWorkflow: (workflow: any) => api.post('/workflows', workflow),
  
  // 更新工作流
  updateWorkflow: (id: number, workflow: any) => api.put(`/workflows/${id}`, workflow),
  
  // 删除工作流
  deleteWorkflow: (id: number) => api.delete(`/workflows/${id}`),
};

// 执行记录相关API
export const executionApi = {
  // 获取执行记录列表
  listExecutions: (workflowId: number) => api.get(`/executions/workflow/${workflowId}`),
  
  // 获取执行记录详情
  getExecution: (id: number) => api.get(`/executions/${id}`),
  
  // 创建执行记录
  createExecution: (record: any) => api.post('/executions', record),
  
  // 更新执行记录
  updateExecution: (id: number, record: any) => api.put(`/executions/${id}`, record),
};

export default api;
