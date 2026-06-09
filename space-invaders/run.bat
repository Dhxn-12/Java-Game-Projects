@echo off
REM ─────────────────────────────────────────────────────────────────
REM  Space Invaders — Build & Run Script (Windows)
REM ─────────────────────────────────────────────────────────────────
REM  Requirements: Java 8+ JDK installed and on PATH
REM  Double-click this file or run from cmd/PowerShell
REM ─────────────────────────────────────────────────────────────────

echo ───────────────────────────────────────────────
echo   SPACE INVADERS — Build and Run
echo ───────────────────────────────────────────────

REM Check javac
where javac >nul 2>nul
if errorlevel 1 (
    echo ERROR: javac not found on PATH.
    echo Please install a Java JDK 8+ and add it to your PATH.
    echo Download: https://adoptium.net/
    pause
    exit /b 1
)

javac -version

REM Compile
echo   Compiling...
if not exist out mkdir out

REM Collect all .java files
dir /s /b src\*.java > sources.txt

javac -d out -sourcepath src @sources.txt
if errorlevel 1 (
    echo BUILD FAILED.
    pause
    exit /b 1
)

del sources.txt
echo   Build successful!
echo ───────────────────────────────────────────────
echo   Launching game...
echo.

java -cp out main.Game

pause
