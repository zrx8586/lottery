#!/bin/bash

# �������ݿ�������Ϣ
DB_USER="root"
DB_NAME="lottery"
MYSQL_CMD="mysql -u${DB_USER} -p"

# ��ʾ�û���������
echo "������MySQL����:"
read -s DB_PASSWORD
MYSQL_CMD="mysql -u${DB_USER} -p${DB_PASSWORD}"

# �������ݿ⣨��������ڣ�
echo "�������ݿ� ${DB_NAME}..."
${MYSQL_CMD} -e "CREATE DATABASE IF NOT EXISTS ${DB_NAME};"

# �л���Ŀ�����ݿ�
echo "�л������ݿ� ${DB_NAME}..."
${MYSQL_CMD} -e "USE ${DB_NAME};"

# ������б�
echo "������б�..."
${MYSQL_CMD} ${DB_NAME} < clear_tables_lottery.sql

# ������ṹ
echo "������ṹ..."
${MYSQL_CMD} ${DB_NAME} < create_table_lottery.sql

# ��ʼ������
echo "��ʼ������..."
${MYSQL_CMD} ${DB_NAME} < init_data_lottery.sql

echo "���ݿ��ʼ����ɣ�" 