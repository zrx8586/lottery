@echo off
echo 正在切换到开发环境...
echo.

REM 修改主配置文件中的环境设置
powershell -Command "(Get-Content 'src/main/resources/application.properties') -replace 'spring.profiles.active=prod', 'spring.profiles.active=dev' | Set-Content 'src/main/resources/application.properties'"

echo 环境已切换到: 开发环境 (dev)
echo 配置文件: application-dev.properties
echo.
echo 请重新启动应用程序以应用新配置
echo.
pause
