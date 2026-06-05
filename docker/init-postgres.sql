-- 确保数据库使用UTF-8编码
CREATE DATABASE workflow_db
    WITH 
    OWNER = bokagent
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TEMPLATE = template0;

-- 连接到workflow_db
\c workflow_db

-- 创建扩展
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- 验证编码
SHOW server_encoding;
SHOW client_encoding;
