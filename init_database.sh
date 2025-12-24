#!/bin/bash

# 车辆租贷管理系统 - PostgreSQL 初始化脚本
# 使用方法: bash init_database.sh

set -e

echo "=========================================="
echo "车辆租贷管理系统 - 数据库初始化"
echo "=========================================="
echo ""

# 配置信息
DB_HOST="localhost"
DB_PORT="5432"
DB_USER="postgres"
DB_NAME="vehicle_rental"
SCRIPT_PATH="src/main/resources/schema.sql"

# 检查PostgreSQL连接
echo "[1/3] 检查PostgreSQL连接..."
if ! command -v psql &> /dev/null; then
    echo "❌ 错误: 未找到psql命令，请先安装PostgreSQL"
    exit 1
fi

# 创建数据库
echo "[2/3] 检查数据库是否存在..."
if psql -h "$DB_HOST" -U "$DB_USER" -lqt | cut -d \| -f 1 | grep -qw "$DB_NAME"; then
    echo "✓ 数据库 '$DB_NAME' 已存在"
else
    echo "创建数据库 '$DB_NAME'..."
    createdb -h "$DB_HOST" -U "$DB_USER" "$DB_NAME"
    echo "✓ 数据库创建成功"
fi

# 执行SQL脚本
echo "[3/3] 执行初始化SQL脚本..."
if [ ! -f "$SCRIPT_PATH" ]; then
    echo "❌ 错误: 找不到SQL脚本文件 $SCRIPT_PATH"
    exit 1
fi

psql -h "$DB_HOST" -U "$DB_USER" -d "$DB_NAME" -f "$SCRIPT_PATH"

echo ""
echo "=========================================="
echo "✓ 数据库初始化完成！"
echo "=========================================="
echo ""
echo "数据库信息："
echo "  主机: $DB_HOST"
echo "  端口: $DB_PORT"
echo "  用户: $DB_USER"
echo "  数据库: $DB_NAME"
echo ""
echo "下一步: 修改 application.properties 中的数据库密码，然后启动应用"
echo ""
