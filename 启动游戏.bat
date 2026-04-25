@echo off
setlocal enabledelayedexpansion
title Super Mario - Java Version

cd /d "%~dp0"

echo ========================================
echo   Super Mario Game Launcher
echo ========================================
echo.
echo Working Directory: %cd%
echo.

echo [INFO] Checking Java installation...
java -version 2>&1
if errorlevel 1 (
    echo.
    echo [ERROR] Java not found!
    echo.
    echo Please install Java JDK or JRE (version 8 or higher)
    echo Download: https://www.oracle.com/java/technologies/downloads/
    echo.
    echo Press any key to exit...
    pause >nul
    exit /b 1
)
echo [INFO] Java detected successfully!
echo.

echo [INFO] Compiling game code...
javac SuperMarioGame.java
if errorlevel 1 (
    echo.
    echo [ERROR] Compilation failed!
    echo.
    echo Press any key to exit...
    pause >nul
    exit /b 1
)
echo [INFO] Compilation successful!
echo.

echo [INFO] Checking compiled files...
if not exist "SuperMarioGame.class" (
    echo [ERROR] SuperMarioGame.class not found!
    echo.
    echo Press any key to exit...
    pause >nul
    exit /b 1
)
echo [INFO] All class files ready!
echo.

echo [INFO] Starting game...
echo [INFO] Game window should open in a few seconds
echo [INFO] Controls:
echo        Arrow Keys / WASD - Move
echo        Spacebar - Jump
echo        R Key - Restart
echo.

start "" javaw -cp . SuperMarioGame

echo [INFO] Game launch command sent!
echo.
echo ========================================
echo   Launcher will close in 3 seconds...
echo ========================================
timeout /t 3 /nobreak >nul
