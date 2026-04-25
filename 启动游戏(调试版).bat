@echo off
setlocal enabledelayedexpansion
title Super Mario - Debug Mode

cd /d "%~dp0"

echo ========================================
echo   Super Mario Game Launcher (DEBUG)
echo ========================================
echo.
echo Batch File Location: %~dp0
echo Working Directory: %cd%
echo.

echo [STEP 1] Checking Java installation...
echo ----------------------------------------
java -version 2>&1
if errorlevel 1 (
    echo.
    echo [ERROR] Java NOT found!
    echo.
    echo Please install Java JDK or JRE (version 8 or higher)
    echo Download: https://www.oracle.com/java/technologies/downloads/
    echo.
    echo Press any key to exit...
    pause >nul
    exit /b 1
)
echo [OK] Java is installed!
echo.

echo [STEP 2] Checking source file...
echo ----------------------------------------
if exist "SuperMarioGame.java" (
    echo [OK] SuperMarioGame.java found!
) else (
    echo [ERROR] SuperMarioGame.java NOT found!
    echo Current directory: %cd%
    echo.
    echo Press any key to exit...
    pause >nul
    exit /b 1
)
echo.

echo [STEP 3] Compiling Java code...
echo ----------------------------------------
echo Running: javac SuperMarioGame.java
echo.
javac SuperMarioGame.java
if errorlevel 1 (
    echo.
    echo [ERROR] Compilation FAILED!
    echo.
    echo Press any key to exit...
    pause >nul
    exit /b 1
)
echo [OK] Compilation successful!
echo.

echo [STEP 4] Checking compiled class files...
echo ----------------------------------------
dir /b *.class 2>nul
if errorlevel 1 (
    echo [ERROR] No .class files generated!
    echo.
    echo Press any key to exit...
    pause >nul
    exit /b 1
)
echo.
echo [OK] Class files generated!
echo.

echo [STEP 5] Starting game...
echo ----------------------------------------
echo Running: javaw -cp . SuperMarioGame
echo.
echo Game window should open shortly...
echo.

start "" javaw -cp . SuperMarioGame

echo [OK] Game launch command sent!
echo.
echo ========================================
echo   Debug Info Summary
echo ========================================
echo Java Status:      OK
echo Compilation:      OK
echo Class Files:      OK
echo Game Launch:      Sent
echo.
echo If the game window doesn't open, please check:
echo 1. Java is correctly installed
echo 2. No antivirus is blocking javaw.exe
echo 3. Try running "java SuperMarioGame" in command prompt
echo.
echo This window will close in 10 seconds...
echo ========================================
timeout /t 10 /nobreak >nul
