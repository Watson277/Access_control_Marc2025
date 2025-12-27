#!/bin/bash

echo "Building Access Control System..."
echo

# 清理旧的构建
rm -rf target

# 编译项目
mvn clean compile
if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

# 打包
mvn package
if [ $? -ne 0 ]; then
    echo "Package failed!"
    exit 1
fi

# 复制jar文件到根目录
if [ -f target/access-control-system-1.0.0.jar ]; then
    cp target/access-control-system-1.0.0.jar access-control-system.jar
    echo
    echo "Build successful! JAR file: access-control-system.jar"
else
    echo "JAR file not found!"
    exit 1
fi

