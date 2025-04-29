@echo off
setlocal enabledelayedexpansion

:: �������ݿ�������Ϣ
set DB_USER=root
set DB_NAME=lottery

:: ��ʾ�û���������
set /p DB_PASS=������MySQL����: 

:: ������ݿ��Ƿ���ڣ����������ɾ��
echo ���ڼ�����ݿ��Ƿ����...
mysql -u %DB_USER% -p%DB_PASS% -e "DROP DATABASE IF EXISTS %DB_NAME%;"
if errorlevel 1 (
    echo ����ɾ�����ݿ�ʧ��
    pause
    exit /b 1
)

:: �������ݿ�
echo ���ڴ������ݿ�...
mysql -u %DB_USER% -p%DB_PASS% -e "CREATE DATABASE %DB_NAME% CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
if errorlevel 1 (
    echo ���󣺴������ݿ�ʧ��
    pause
    exit /b 1
)

:: �л���Ŀ�����ݿⲢ�����ַ���
echo �����л������ݿⲢ�����ַ���...
mysql -u %DB_USER% -p%DB_PASS% %DB_NAME% -e "USE %DB_NAME%; SET NAMES utf8mb4; SET CHARACTER SET utf8mb4;"
if errorlevel 1 (
    echo �����л����ݿ�ʧ��
    pause
    exit /b 1
)

:: ������ʱ�ļ�
set TEMP_SQL=temp.sql

:: �����û���
echo ���ڴ����û���...
echo SET NAMES utf8mb4; > %TEMP_SQL%
echo SET CHARACTER SET utf8mb4; >> %TEMP_SQL%
type create_table_users.sql >> %TEMP_SQL%
mysql -u %DB_USER% -p%DB_PASS% %DB_NAME% < %TEMP_SQL%
if errorlevel 1 (
    echo ���󣺴����û���ʧ��
    del %TEMP_SQL%
    pause
    exit /b 1
)

:: ������Ϸ��
echo ���ڴ�����Ϸ��...
echo SET NAMES utf8mb4; > %TEMP_SQL%
echo SET CHARACTER SET utf8mb4; >> %TEMP_SQL%
type create_table_game.sql >> %TEMP_SQL%
mysql -u %DB_USER% -p%DB_PASS% %DB_NAME% < %TEMP_SQL%
if errorlevel 1 (
    echo ���󣺴�����Ϸ��ʧ��
    del %TEMP_SQL%
    pause
    exit /b 1
)

:: ����֧����
echo ���ڴ���֧����...
echo SET NAMES utf8mb4; > %TEMP_SQL%
echo SET CHARACTER SET utf8mb4; >> %TEMP_SQL%
type create_table_payment.sql >> %TEMP_SQL%
mysql -u %DB_USER% -p%DB_PASS% %DB_NAME% < %TEMP_SQL%
if errorlevel 1 (
    echo ���󣺴���֧����ʧ��
    del %TEMP_SQL%
    pause
    exit /b 1
)

:: �����齱��ر�
echo ���ڴ����齱��ر�...
echo SET NAMES utf8mb4; > %TEMP_SQL%
echo SET CHARACTER SET utf8mb4; >> %TEMP_SQL%
type create_table_lottery.sql >> %TEMP_SQL%
mysql -u %DB_USER% -p%DB_PASS% %DB_NAME% < %TEMP_SQL%
if errorlevel 1 (
    echo ���󣺴����齱��ʧ��
    del %TEMP_SQL%
    pause
    exit /b 1
)

:: ��ʼ���û�����
echo ���ڳ�ʼ���û�����...
echo SET NAMES utf8mb4; > %TEMP_SQL%
echo SET CHARACTER SET utf8mb4; >> %TEMP_SQL%
type init_data_users.sql >> %TEMP_SQL%
mysql -u %DB_USER% -p%DB_PASS% %DB_NAME% < %TEMP_SQL%
if errorlevel 1 (
    echo ���󣺳�ʼ���û�����ʧ��
    del %TEMP_SQL%
    pause
    exit /b 1
)

:: ��ʼ����Ϸ����
echo ���ڳ�ʼ����Ϸ����...
echo SET NAMES utf8mb4; > %TEMP_SQL%
echo SET CHARACTER SET utf8mb4; >> %TEMP_SQL%
type init_data_game.sql >> %TEMP_SQL%
mysql -u %DB_USER% -p%DB_PASS% %DB_NAME% < %TEMP_SQL%
if errorlevel 1 (
    echo ���󣺳�ʼ����Ϸ����ʧ��
    del %TEMP_SQL%
    pause
    exit /b 1
)

:: ��ʼ���齱����
echo ���ڳ�ʼ���齱����...
echo SET NAMES utf8mb4; > %TEMP_SQL%
echo SET CHARACTER SET utf8mb4; >> %TEMP_SQL%
type init_data_lottery.sql >> %TEMP_SQL%
mysql -u %DB_USER% -p%DB_PASS% %DB_NAME% < %TEMP_SQL%
if errorlevel 1 (
    echo ���󣺳�ʼ���齱����ʧ��
    del %TEMP_SQL%
    pause
    exit /b 1
)

:: ��֤����
echo ������֤����...
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
    echo ������֤����ʧ��
    del %TEMP_SQL%
    pause
    exit /b 1
)

:: ɾ����ʱ�ļ�
del %TEMP_SQL%

echo ���ݿ��ʼ����ɣ�
echo Ԥ����������
echo - lottery_activity: 8 ����¼
echo - lottery_prize: 6 ����¼
echo - lottery_activity_prize: 40 ����¼
echo - lottery_record: 25 ����¼
echo - lottery_activity_user: 25 ����¼
echo - users: ��鿴 init_data_users.sql �еļ�¼��
echo - season: ��鿴 init_data_game.sql �еļ�¼��
echo - game_user: ��鿴 init_data_game.sql �еļ�¼��
echo - season_leaderboard_record: ��鿴 init_data_game.sql �еļ�¼��
echo - idempotent_record: ��鿴 init_data_payment.sql �еļ�¼��
pause 