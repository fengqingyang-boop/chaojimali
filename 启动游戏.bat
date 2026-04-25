@echo off
chcp 65001 >nul
title 超级玛丽 - Java版
echo ========================================
echo   超级玛丽 Java版 游戏启动器
echo ========================================
echo.

REM 检查 Java 是否安装
java -version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到 Java 环境！
    echo.
    echo 请先安装 Java JDK 或 JRE (版本 8 或更高)
    echo 下载地址: https://www.oracle.com/java/technologies/downloads/
    echo.
    pause
    exit /b 1
)

echo [信息] Java 环境检测通过
echo.

REM 编译 Java 文件
echo [信息] 正在编译游戏代码...
javac SuperMarioGame.java
if errorlevel 1 (
    echo [错误] 编译失败！
    pause
    exit /b 1
)
echo [信息] 编译成功！
echo.

REM 运行游戏
echo [信息] 正在启动游戏...
echo [提示] 游戏窗口将在几秒钟后打开
echo [提示] 控制方式:
echo        方向键 / WASD - 移动
echo        空格键 - 跳跃
echo        R键 - 重新开始
echo.
java SuperMarioGame

REM 清理 class 文件
del /Q *.class 2>nul

echo.
echo ========================================
echo   游戏已退出
echo ========================================
pause
