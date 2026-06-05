#!/bin/bash

echo "========================================="
echo "BokAgent - 项目启动和验证脚本"
echo "========================================="
echo ""

# 检查.env文件是否存在
if [ ! -f .env ]; then
    echo "⚠️  警告: .env文件不存在"
    echo "正在从.env.example创建.env文件..."
    cp .env.example .env
    echo "✅ 已创建.env文件，请编辑该文件配置你的API密钥"
    echo ""
fi

# 启动Docker服务
echo "🚀 正在启动Docker服务..."
docker-compose up -d

echo ""
echo "⏳ 等待服务启动（约30秒）..."
sleep 30

echo ""
echo "========================================="
echo "验证UTF-8配置"
echo "========================================="

# 验证PostgreSQL编码
echo ""
echo "📊 PostgreSQL编码检查:"
docker-compose exec postgres psql -U bokagent -d workflow_db -c "SHOW server_encoding;" 2>/dev/null || echo "❌ PostgreSQL未就绪"

# 验证MySQL编码
echo ""
echo "📊 MySQL编码检查:"
docker-compose exec mysql mysql -u bokagent -psecret business_db -e "SHOW VARIABLES LIKE 'character_set_database';" 2>/dev/null || echo "❌ MySQL未就绪"

# 测试中文存储
echo ""
echo "📝 测试中文和Emoji存储:"
docker-compose exec postgres psql -U bokagent -d workflow_db -c "INSERT INTO workflows (name, description, graph_data) VALUES ('测试工作流 🎉', '描述测试 ✨🚀', '{}') ON CONFLICT DO NOTHING;" 2>/dev/null
docker-compose exec postgres psql -U bokagent -d workflow_db -c "SELECT name, description FROM workflows WHERE name LIKE '%测试%' LIMIT 1;" 2>/dev/null || echo "❌ 测试失败"

echo ""
echo "========================================="
echo "服务访问地址"
echo "========================================="
echo "🌐 前端应用: http://localhost"
echo "🔧 后端API:  http://localhost:8080"
echo "💾 MinIO控制台: http://localhost:9001"
echo ""
echo "查看日志: docker-compose logs -f backend"
echo "停止服务: docker-compose down"
echo ""
echo "✅ 项目启动完成！"
