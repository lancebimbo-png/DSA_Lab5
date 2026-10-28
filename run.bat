@echo off
setlocal

cd /d "%~dp0"

where java >nul 2>nul
if errorlevel 1 (
    echo ERROR: Java is not available on this computer.
    echo Install a JDK and make sure java is on the PATH.
    pause
    exit /b 1
)

where javac >nul 2>nul
if errorlevel 1 (
    echo ERROR: The Java compiler javac is not available.
    echo Install a JDK and make sure javac is on the PATH.
    pause
    exit /b 1
)

echo Compiling Lab 5 files...
javac ArrayStack.java ArrayQueue.java Main.java
if errorlevel 1 (
    echo.
    echo ERROR: Compilation failed. Read the messages above and try again.
    pause
    exit /b 1
)

echo Starting Lab 5 program...
echo.
java Main
if errorlevel 1 (
    echo.
    echo ERROR: The program stopped with an error.
    pause
    exit /b 1
)

endlocal
