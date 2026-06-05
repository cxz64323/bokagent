CREATE TABLE execution_records (
    id BIGSERIAL PRIMARY KEY,
    workflow_id BIGINT REFERENCES workflows(id),
    status VARCHAR(20) NOT NULL,  -- RUNNING, SUCCESS, FAILED
    input_data JSONB,
    output_data JSONB,
    audio_url TEXT,  -- MinIO存储的音频URL
    error_message TEXT,  -- 错误信息支持中文
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    duration_ms BIGINT  -- 执行耗时（毫秒）
);

COMMENT ON TABLE execution_records IS '工作流执行记录表';
COMMENT ON COLUMN execution_records.error_message IS '错误信息，支持中文描述';

CREATE INDEX idx_execution_records_workflow_id ON execution_records(workflow_id);
CREATE INDEX idx_execution_records_started_at ON execution_records(started_at DESC);
