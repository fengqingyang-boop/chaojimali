@echo off
title Super Mario - Java Version
echo ========================================
echo   Super Mario Game Launcher
echo ========================================
echo.

REM Check Java installation
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java not found!
    echo.
    echo Please install Java JDK or JRE (version 8 or higher)
    echo Download: https://www.oracle.com/java/technologies/downloads/
    echo.
    pause
    exit /b 1
)

echo [INFO] Java detected successfully
echo.

REM Compile Java files
echo [INFO] Compiling game code...
javac SuperMarioGame.java
if errorlevel 1 (
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)
echo [INFO] Compilation successful!
echo.

REM Run game using start to open in new window
echo [INFO] Starting game...
echo [INFO] Game window should open in a few seconds
echo [INFO] Controls:
echo        Arrow Keys / WASD - Move
echo        Spacebar - Jump
echo        R Key - Restart
echo.

start "Super Mario" java SuperMarioGame

REM Wait a moment for the game to start
timeout /t 2 /nobreak >nul

REM Clean up class files after game starts
del /Q *.class 2>nul

echo ========================================
echo   Game launched!
echo   This window will close automatically.
echo ========================================
timeout /t 3 /nobreak >nul
