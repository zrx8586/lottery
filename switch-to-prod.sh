#!/bin/bash

echo "正在切换到生产环境..."
echo

# 修改主配置文件中的环境设置
sed -i 's/spring.profiles.active=dev/spring.profiles.active=prod/g' src/main/resources/application.properties

echo "环境已切换到: 生产环境 (prod)"
echo "配置文件: application-prod.properties"
echo
echo "请重新启动应用程序以应用新配置"
echo
