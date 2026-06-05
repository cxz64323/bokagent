Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "BokAgent - 项目启动和验证脚本" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

# 检查.env文件是否存在
if (-not (Test-Path ".env")) {
    Write-Host "⚠️  警告: .env文件不存在" -ForegroundColor Yellow
    Write-Host "正在从.env.example创建.env文件..." -ForegroundColor Yellow
    Copy-Item ".env.example" ".env"
    Write-Host "✅ 已创建.env文件，请编辑该文件配置你的API密钥" -ForegroundColor Green
    Write-Host ""
}

# 启动Docker服务
Write-Host "🚀 正在启动Docker服务..." -ForegroundColor Green
docker-compose up -d

Write-Host ""
Write-Host "⏳ 等待服务启动（约30秒）..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "验证UTF-8配置" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan

# 验证PostgreSQL编码
Write-Host ""
Write-Host "📊 PostgreSQL编码检查:" -ForegroundColor Green
docker-compose exec postgres psql -U bokagent -d workflow_db -c "SHOW server_encoding;" 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ PostgreSQL未就绪" -ForegroundColor Red
}

# 验证MySQL编码
Write-Host ""
Write-Host "📊 MySQL编码检查:" -ForegroundColor Green
docker-compose exec mysql mysql -u bokagent -psecret business_db -e "SHOW VARIABLES LIKE 'character_set_database';" 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ MySQL未就绪" -ForegroundColor Red
}

# 测试中文存储
Write-Host ""
Write-Host "📝 测试中文和Emoji存储:" -ForegroundColor Green
docker-compose exec postgres psql -U bokagent -d workflow_db -c "INSERT INTO workflows (name, description, graph_data) VALUES ('测试工作流 🎉', '描述测试 ✨🚀', '{}') ON CONFLICT DO NOTHING;" 2>$null
docker-compose exec postgres psql -U bokagent -d workflow_db -c "SELECT name, description FROM workflows WHERE name LIKE '%测试%' LIMIT 1;" 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ 测试失败" -ForegroundColor Red
}

Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "服务访问地址" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "🌐 前端应用: http://localhost" -ForegroundColor White
Write-Host "🔧 后端API:  http://localhost:8080" -ForegroundColor White
Write-Host "💾 MinIO控制台: http://localhost:9001" -ForegroundColor White
Write-Host ""
Write-Host "查看日志: docker-compose logs -f backend" -ForegroundColor Gray
Write-Host "停止服务: docker-compose down" -ForegroundColor Gray
Write-Host ""
Write-Host "✅ 项目启动完成！" -ForegroundColor Green
