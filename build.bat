@echo off
echo Building Access Control System...
echo.

REM 清理旧的构建
if exist target rmdir /s /q target

REM 编译项目
call mvn clean compile
if %errorlevel% neq 0 (
    echo Build failed!
    pause
    exit /b %errorlevel%
)

REM 打包
call mvn package
if %errorlevel% neq 0 (
    echo Package failed!
    pause
    exit /b %errorlevel%
)

REM 复制jar文件到根目录
if exist target\access-control-system-1.0.0.jar (
    copy target\access-control-system-1.0.0.jar access-control-system.jar
    echo.
    echo Build successful! JAR file: access-control-system.jar
) else (
    echo JAR file not found!
)

pause

