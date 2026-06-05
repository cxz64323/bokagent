-- 创建数据库并设置UTF-8MB4编码
CREATE DATABASE IF NOT EXISTS business_db
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE business_db;

-- 验证编码
SHOW VARIABLES LIKE 'character_set_database';
SHOW VARIABLES LIKE 'collation_database';
