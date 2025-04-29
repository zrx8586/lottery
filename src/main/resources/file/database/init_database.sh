#!/bin/bash

# 设置数据库连接信息
DB_USER="root"
DB_NAME="lottery"
MYSQL_CMD="mysql -u${DB_USER} -p"

# 提示用户输入密码
echo "请输入MySQL密码:"
read -s DB_PASSWORD
MYSQL_CMD="mysql -u${DB_USER} -p${DB_PASSWORD}"

# 创建数据库（如果不存在）
echo "创建数据库 ${DB_NAME}..."
${MYSQL_CMD} -e "CREATE DATABASE IF NOT EXISTS ${DB_NAME};"

# 切换到目标数据库
echo "切换到数据库 ${DB_NAME}..."
${MYSQL_CMD} -e "USE ${DB_NAME};"

# 清空所有表
echo "清空所有表..."
${MYSQL_CMD} ${DB_NAME} < clear_tables_lottery.sql

# 创建表结构
echo "创建表结构..."
${MYSQL_CMD} ${DB_NAME} < create_table_lottery.sql

# 初始化数据
echo "初始化数据..."
${MYSQL_CMD} ${DB_NAME} < init_data_lottery.sql

echo "数据库初始化完成！" 