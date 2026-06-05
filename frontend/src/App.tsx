import { Layout, Typography } from 'antd';
import WorkflowEditor from './components/WorkflowEditor';

const { Header } = Layout;
const { Title } = Typography;

function App() {
  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header style={{ background: '#fff', padding: '0 24px', borderBottom: '1px solid #e8e8e8' }}>
        <Title level={3} style={{ margin: '16px 0', display: 'inline-block' }}>
          AI Agent工作流编排系统 🤖
        </Title>
      </Header>
      <WorkflowEditor />
    </Layout>
  );
}

export default App;
