#!/bin/bash

# 设置数据库连接信息
DB_USER="lottery2025"
DB_NAME="lottery"
DB_PASSWORD="123456"
MYSQL_CMD="mysql -u${DB_USER} -p${DB_PASSWORD}"

echo "开始初始化数据库..."

# 检查MySQL连接
echo "检查MySQL连接..."
if ! ${MYSQL_CMD} -e "SELECT 1;" > /dev/null 2>&1; then
    echo "错误：无法连接到MySQL数据库，请检查用户名、密码和MySQL服务状态"
    exit 1
fi

# 创建数据库（如果不存在）
echo "创建数据库 ${DB_NAME}..."
${MYSQL_CMD} -e "CREATE DATABASE IF NOT EXISTS ${DB_NAME} CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 切换到目标数据库
echo "切换到目标数据库 ${DB_NAME}..."
${MYSQL_CMD} -e "USE ${DB_NAME};"

# 设置字符集
echo "设置字符集..."
${MYSQL_CMD} ${DB_NAME} -e "SET NAMES utf8mb4; SET CHARACTER SET utf8mb4;"

# 检查SQL文件是否存在
echo "检查SQL文件..."
REQUIRED_FILES=(
    "create_table_users.sql"
    "create_table_game.sql"
    "create_table_payment.sql"
    "create_table_lottery.sql"
    "clear_tables_lottery.sql"
    "init_data_users.sql"
    "init_data_game.sql"
    "init_data_lottery.sql"
    "test_data_single_user_concurrent_draw.sql"
    "test_data_multiple_user_concurrent_draw.sql"
)

for file in "${REQUIRED_FILES[@]}"; do
    if [ ! -f "$file" ]; then
        echo "错误：缺少SQL文件 $file"
        exit 1
    fi
done

# 创建表结构 - 按照依赖关系顺序
echo "创建用户表..."
${MYSQL_CMD} ${DB_NAME} < create_table_users.sql
if [ $? -ne 0 ]; then
    echo "错误：创建用户表失败"
    exit 1
fi

echo "创建游戏表..."
${MYSQL_CMD} ${DB_NAME} < create_table_game.sql
if [ $? -ne 0 ]; then
    echo "错误：创建游戏表失败"
    exit 1
fi

echo "创建支付表..."
${MYSQL_CMD} ${DB_NAME} < create_table_payment.sql
if [ $? -ne 0 ]; then
    echo "错误：创建支付表失败"
    exit 1
fi

echo "创建抽奖表..."
${MYSQL_CMD} ${DB_NAME} < create_table_lottery.sql
if [ $? -ne 0 ]; then
    echo "错误：创建抽奖表失败"
    exit 1
fi

# 验证表是否创建成功
echo "验证表结构..."
TABLES=(
    "users"
    "season"
    "game_user"
    "season_leaderboard_record"
    "lottery_activity"
    "lottery_prize"
    "lottery_activity_prize"
    "lottery_record"
    "lottery_activity_user"
    "idempotent_record"
)

for table in "${TABLES[@]}"; do
    if ! ${MYSQL_CMD} ${DB_NAME} -e "DESCRIBE $table;" > /dev/null 2>&1; then
        echo "错误：表 $table 创建失败"
        exit 1
    else
        echo "✓ 表 $table 创建成功"
    fi
done

# 清空表数据
echo "清空表数据..."
${MYSQL_CMD} ${DB_NAME} < clear_tables_lottery.sql

# 初始化数据 - 按照依赖关系顺序
echo "初始化用户数据..."
${MYSQL_CMD} ${DB_NAME} < init_data_users.sql

echo "初始化游戏数据..."
${MYSQL_CMD} ${DB_NAME} < init_data_game.sql

echo "初始化抽奖数据..."
${MYSQL_CMD} ${DB_NAME} < init_data_lottery.sql

echo "初始化单用户并发抽奖测试数据..."
${MYSQL_CMD} ${DB_NAME} < test_data_single_user_concurrent_draw.sql

echo "初始化多用户并发抽奖测试数据..."
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
SELECT 'season' as table_name, COUNT(*) as count FROM season;
SELECT 'game_user' as table_name, COUNT(*) as count FROM game_user;
SELECT 'season_leaderboard_record' as table_name, COUNT(*) as count FROM season_leaderboard_record;
SELECT 'idempotent_record' as table_name, COUNT(*) as count FROM idempotent_record;
"

echo "数据库初始化完成！"
echo "预期数据量："
echo "- lottery_activity: 10 条记录（抽奖活动）"
echo "- lottery_prize: 11 条记录（抽奖奖品）"
echo "- lottery_activity_prize: 50 条记录（抽奖活动奖品关系）"
echo "- lottery_record: 25 条记录"
echo "- lottery_activity_user: 35 条记录（抽奖活动用户记录）"
echo "- users: 查看 init_data_users.sql 中的记录数"
echo "- season: 查看 init_data_game.sql 中的记录数"
echo "- game_user: 查看 init_data_game.sql 中的记录数"
echo "- season_leaderboard_record: 查看 init_data_game.sql 中的记录数"
echo "- idempotent_record: 查看 init_data_payment.sql 中的记录数"
