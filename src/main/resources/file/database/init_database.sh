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
${MYSQL_CMD} -e "CREATE DATABASE IF NOT EXISTS ${DB_NAME} CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 切换到目标数据库
echo "切换到数据库 ${DB_NAME}..."
${MYSQL_CMD} -e "USE ${DB_NAME};"

# 清空表
echo "清空表..."
${MYSQL_CMD} ${DB_NAME} < clear_tables_lottery.sql

# 创建表结构
echo "创建表结构..."
${MYSQL_CMD} ${DB_NAME} < create_table_lottery.sql

# 初始化基础数据
echo "初始化基础数据..."
${MYSQL_CMD} ${DB_NAME} < init_data_lottery.sql

# 初始化单用户并发测试数据
echo "初始化单用户并发测试数据..."
${MYSQL_CMD} ${DB_NAME} < test_data_single_user_concurrent_draw.sql

# 初始化多用户并发测试数据
echo "初始化多用户并发测试数据..."
${MYSQL_CMD} ${DB_NAME} < test_data_multiple_user_concurrent_draw.sql

# 验证数据
echo "验证数据..."
${MYSQL_CMD} ${DB_NAME} -e "
SELECT 'lottery_activity' as table_name, COUNT(*) as count FROM lottery_activity;
SELECT 'lottery_prize' as table_name, COUNT(*) as count FROM lottery_prize;
SELECT 'lottery_activity_prize' as table_name, COUNT(*) as count FROM lottery_activity_prize;
SELECT 'lottery_record' as table_name, COUNT(*) as count FROM lottery_record;
SELECT 'lottery_activity_user' as table_name, COUNT(*) as count FROM lottery_activity_user;
SELECT 'users' as table_name, COUNT(*) as count FROM users;
"

echo "数据库初始化完成！"
echo "预期数据量："
echo "- lottery_activity: 10 条记录（包含测试活动）"
echo "- lottery_prize: 11 条记录（包含测试奖品）"
echo "- lottery_activity_prize: 50 条记录（包含测试活动奖品关联）"
echo "- lottery_record: 25 条记录"
echo "- lottery_activity_user: 35 条记录（包含测试用户参与记录）"
echo "- users: 请查看 init_data_users.sql 中的记录数" 