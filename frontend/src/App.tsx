import { Layout, Typography } from 'antd';
import { BrowserRouter as Router } from 'react-router-dom';

const { Header, Content, Footer } = Layout;
const { Title } = Typography;

function App() {
  return (
    <Router>
      <Layout style={{ minHeight: '100vh' }}>
        <Header style={{ background: '#fff', padding: '0 24px' }}>
          <Title level={3} style={{ margin: '16px 0' }}>
            AI Agent工作流编排系统 🤖
          </Title>
        </Header>
        <Content style={{ padding: '24px', minHeight: 'calc(100vh - 134px)' }}>
          <div style={{ background: '#fff', padding: 24, minHeight: 360 }}>
            <Title level={4}>欢迎使用 BokAgent</Title>
            <p>支持中文和Emoji测试: 你好世界 🎉🚀✨</p>
          </div>
        </Content>
        <Footer style={{ textAlign: 'center' }}>
          BokAgent ©{new Date().getFullYear()} - AI Agent Workflow Orchestration System
        </Footer>
      </Layout>
    </Router>
  );
}

export default App;
