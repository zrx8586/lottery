#!/bin/bash

# �������ݿ�������Ϣ
DB_USER="lottery2025"
DB_NAME="lottery"
DB_PASSWORD="123456"
MYSQL_CMD="mysql -u${DB_USER} -p"

# ��ʾ�û���������
echo "������MySQL����:"
read -s DB_PASSWORD
MYSQL_CMD="mysql -u${DB_USER} -p${DB_PASSWORD}"

# �������ݿ⣨��������ڣ�
echo "�������ݿ� ${DB_NAME}..."
${MYSQL_CMD} -e "CREATE DATABASE IF NOT EXISTS ${DB_NAME} CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# �л���Ŀ�����ݿ�
echo "�л������ݿ� ${DB_NAME}..."
${MYSQL_CMD} -e "USE ${DB_NAME};"

# ��ձ�
echo "��ձ�..."
${MYSQL_CMD} ${DB_NAME} < clear_tables_lottery.sql

# ������ṹ
echo "������ṹ..."
${MYSQL_CMD} ${DB_NAME} < create_table_lottery.sql

# ��ʼ����������
echo "��ʼ����������..."
${MYSQL_CMD} ${DB_NAME} < init_data_lottery.sql

# ��ʼ�����û�������������
echo "��ʼ�����û�������������..."
${MYSQL_CMD} ${DB_NAME} < test_data_single_user_concurrent_draw.sql

# ��ʼ�����û�������������
echo "��ʼ�����û�������������..."
${MYSQL_CMD} ${DB_NAME} < test_data_multiple_user_concurrent_draw.sql

# ��֤����
echo "��֤����..."
${MYSQL_CMD} ${DB_NAME} -e "
SELECT 'lottery_activity' as table_name, COUNT(*) as count FROM lottery_activity;
SELECT 'lottery_prize' as table_name, COUNT(*) as count FROM lottery_prize;
SELECT 'lottery_activity_prize' as table_name, COUNT(*) as count FROM lottery_activity_prize;
SELECT 'lottery_record' as table_name, COUNT(*) as count FROM lottery_record;
SELECT 'lottery_activity_user' as table_name, COUNT(*) as count FROM lottery_activity_user;
SELECT 'users' as table_name, COUNT(*) as count FROM users;
"

echo "���ݿ��ʼ����ɣ�"
echo "Ԥ����������"
echo "- lottery_activity: 10 ����¼���������Ի��"
echo "- lottery_prize: 11 ����¼���������Խ�Ʒ��"
echo "- lottery_activity_prize: 50 ����¼���������Ի��Ʒ������"
echo "- lottery_record: 25 ����¼"
echo "- lottery_activity_user: 35 ����¼�����������û������¼��"
echo "- users: ��鿴 init_data_users.sql �еļ�¼��"
