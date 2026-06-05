-- 确保表使用UTF-8编码（PostgreSQL默认就是UTF-8）
CREATE TABLE workflows (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,  -- 支持中文和Emoji
    graph_data JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- 添加注释（支持中文）
COMMENT ON TABLE workflows IS '工作流定义表';
COMMENT ON COLUMN workflows.name IS '工作流名称';
COMMENT ON COLUMN workflows.description IS '工作流描述，支持中文和Emoji 🎉';

CREATE INDEX idx_workflows_created_at ON workflows(created_at DESC);
