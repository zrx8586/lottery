@echo off
setlocal enabledelayedexpansion

:: 设置数据库连接信息
set DB_USER=root
set DB_NAME=lottery

:: 提示用户输入密码
set /p DB_PASS=请输入MySQL密码: 

:: 检查数据库是否存在，如果存在则删除
echo 正在检查数据库是否存在...
mysql -u %DB_USER% -p%DB_PASS% -e "DROP DATABASE IF EXISTS %DB_NAME%;"
if errorlevel 1 (
    echo 错误：删除数据库失败
    pause
    exit /b 1
)

:: 创建数据库
echo 正在创建数据库...
mysql -u %DB_USER% -p%DB_PASS% -e "CREATE DATABASE %DB_NAME% CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
if errorlevel 1 (
    echo 错误：创建数据库失败
    pause
    exit /b 1
)

:: 切换到目标数据库并设置字符集
echo 正在切换到数据库并设置字符集...
mysql -u %DB_USER% -p%DB_PASS% %DB_NAME% -e "USE %DB_NAME%; SET NAMES utf8mb4; SET CHARACTER SET utf8mb4;"
if errorlevel 1 (
    echo 错误：切换数据库失败
    pause
    exit /b 1
)

:: 创建临时文件
set TEMP_SQL=temp.sql

:: 创建用户表
echo 正在创建用户表...
echo SET NAMES utf8mb4; > %TEMP_SQL%
echo SET CHARACTER SET utf8mb4; >> %TEMP_SQL%
type create_table_users.sql >> %TEMP_SQL%
mysql -u %DB_USER% -p%DB_PASS% %DB_NAME% < %TEMP_SQL%
if errorlevel 1 (
    echo 错误：创建用户表失败
    del %TEMP_SQL%
    pause
    exit /b 1
)

:: 创建游戏表
echo 正在创建游戏表...
echo SET NAMES utf8mb4; > %TEMP_SQL%
echo SET CHARACTER SET utf8mb4; >> %TEMP_SQL%
type create_table_game.sql >> %TEMP_SQL%
mysql -u %DB_USER% -p%DB_PASS% %DB_NAME% < %TEMP_SQL%
if errorlevel 1 (
    echo 错误：创建游戏表失败
    del %TEMP_SQL%
    pause
    exit /b 1
)

:: 创建支付表
echo 正在创建支付表...
echo SET NAMES utf8mb4; > %TEMP_SQL%
echo SET CHARACTER SET utf8mb4; >> %TEMP_SQL%
type create_table_payment.sql >> %TEMP_SQL%
mysql -u %DB_USER% -p%DB_PASS% %DB_NAME% < %TEMP_SQL%
if errorlevel 1 (
    echo 错误：创建支付表失败
    del %TEMP_SQL%
    pause
    exit /b 1
)

:: 创建抽奖相关表
echo 正在创建抽奖相关表...
echo SET NAMES utf8mb4; > %TEMP_SQL%
echo SET CHARACTER SET utf8mb4; >> %TEMP_SQL%
type create_table_lottery.sql >> %TEMP_SQL%
mysql -u %DB_USER% -p%DB_PASS% %DB_NAME% < %TEMP_SQL%
if errorlevel 1 (
    echo 错误：创建抽奖表失败
    del %TEMP_SQL%
    pause
    exit /b 1
)

:: 初始化用户数据
echo 正在初始化用户数据...
echo SET NAMES utf8mb4; > %TEMP_SQL%
echo SET CHARACTER SET utf8mb4; >> %TEMP_SQL%
type init_data_users.sql >> %TEMP_SQL%
mysql -u %DB_USER% -p%DB_PASS% %DB_NAME% < %TEMP_SQL%
if errorlevel 1 (
    echo 错误：初始化用户数据失败
    del %TEMP_SQL%
    pause
    exit /b 1
)

:: 初始化游戏数据
echo 正在初始化游戏数据...
echo SET NAMES utf8mb4; > %TEMP_SQL%
echo SET CHARACTER SET utf8mb4; >> %TEMP_SQL%
type init_data_game.sql >> %TEMP_SQL%
mysql -u %DB_USER% -p%DB_PASS% %DB_NAME% < %TEMP_SQL%
if errorlevel 1 (
    echo 错误：初始化游戏数据失败
    del %TEMP_SQL%
    pause
    exit /b 1
)

:: 初始化抽奖数据
echo 正在初始化抽奖数据...
echo SET NAMES utf8mb4; > %TEMP_SQL%
echo SET CHARACTER SET utf8mb4; >> %TEMP_SQL%
type init_data_lottery.sql >> %TEMP_SQL%
mysql -u %DB_USER% -p%DB_PASS% %DB_NAME% < %TEMP_SQL%
if errorlevel 1 (
    echo 错误：初始化抽奖数据失败
    del %TEMP_SQL%
    pause
    exit /b 1
)

:: 验证数据
echo 正在验证数据...
echo SET NAMES utf8mb4; > %TEMP_SQL%
echo SET CHARACTER SET utf8mb4; >> %TEMP_SQL%
echo SELECT 'lottery_activity' as table_name, COUNT(*) as count FROM lottery_activity; >> %TEMP_SQL%
echo SELECT 'lottery_prize' as table_name, COUNT(*) as count FROM lottery_prize; >> %TEMP_SQL%
echo SELECT 'lottery_activity_prize' as table_name, COUNT(*) as count FROM lottery_activity_prize; >> %TEMP_SQL%
echo SELECT 'lottery_record' as table_name, COUNT(*) as count FROM lottery_record; >> %TEMP_SQL%
echo SELECT 'lottery_activity_user' as table_name, COUNT(*) as count FROM lottery_activity_user; >> %TEMP_SQL%
echo SELECT 'users' as table_name, COUNT(*) as count FROM users; >> %TEMP_SQL%
echo SELECT 'season' as table_name, COUNT(*) as count FROM season; >> %TEMP_SQL%
echo SELECT 'game_user' as table_name, COUNT(*) as count FROM game_user; >> %TEMP_SQL%
echo SELECT 'season_leaderboard_record' as table_name, COUNT(*) as count FROM season_leaderboard_record; >> %TEMP_SQL%
echo SELECT 'idempotent_record' as table_name, COUNT(*) as count FROM idempotent_record; >> %TEMP_SQL%
mysql -u %DB_USER% -p%DB_PASS% %DB_NAME% < %TEMP_SQL%
if errorlevel 1 (
    echo 错误：验证数据失败
    del %TEMP_SQL%
    pause
    exit /b 1
)

:: 删除临时文件
del %TEMP_SQL%

echo 数据库初始化完成！
echo 预期数据量：
echo - lottery_activity: 8 条记录
echo - lottery_prize: 6 条记录
echo - lottery_activity_prize: 40 条记录
echo - lottery_record: 25 条记录
echo - lottery_activity_user: 25 条记录
echo - users: 请查看 init_data_users.sql 中的记录数
echo - season: 请查看 init_data_game.sql 中的记录数
echo - game_user: 请查看 init_data_game.sql 中的记录数
echo - season_leaderboard_record: 请查看 init_data_game.sql 中的记录数
echo - idempotent_record: 请查看 init_data_payment.sql 中的记录数
pause 