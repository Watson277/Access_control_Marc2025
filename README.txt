访问控制系统 (Access Control System) - 安装和运行指南
====================================================

项目团队成员：
- [团队成员1姓名]
- [团队成员2姓名]
- [团队成员3姓名]

系统要求：
---------
- Java JDK 8 或更高版本
- MySQL 5.7 或更高版本
- 操作系统：Windows/Linux/MacOS

安装步骤：
---------
1. 数据库设置：
   - 启动MySQL服务器
   - 创建数据库：CREATE DATABASE bigcomp_access;
   - 导入数据库脚本：mysql -u root -p bigcomp_access < db.sql
   - 或者使用MySQL Workbench等工具导入db.sql文件

2. 配置数据库连接：
   - 编辑 src/main/resources/config/database.properties 文件
   - 修改数据库连接信息（用户名、密码、URL等）

3. 准备资源文件：
   - 确保 images/ 文件夹中包含 site-layout.png 和 office-layout.png
   - 确保 files/ 文件夹存在（用于存储配置文件和日志）

4. 编译项目：
   - 如果使用Maven：mvn clean compile
   - 如果使用Gradle：gradle build
   - 或者使用IDE（如IntelliJ IDEA或Eclipse）导入项目并编译

5. 运行项目：
   - 方式1：运行可执行jar文件
     java -jar access-control-system.jar
   
   - 方式2：从IDE运行
     运行 com.bigcomp.accesscontrol.Main 类的main方法

功能说明：
---------
1. 用户管理：注册、删除用户，支持不同类型（员工、承包商、实习生、访客等）
2. 徽章管理：创建、分配、注销徽章
3. 资源管理：管理门、电梯、打印机等资源
4. 配置文件管理：创建和管理访问权限配置文件
5. 访问控制：实时处理访问请求并记录日志
6. 日志查询：搜索和查看访问日志
7. 实时监控：可视化站点和楼层，实时显示访问尝试

注意事项：
---------
- 首次运行前必须完成数据库设置
- 日志文件会自动创建在 logs/ 目录下，按年/月/日组织
- 配置文件保存在 files/ 目录下
- 确保有足够的磁盘空间存储日志文件

故障排除：
---------
- 如果无法连接数据库，检查 database.properties 配置
- 如果图片无法显示，检查 images/ 文件夹路径
- 如果日志无法写入，检查 logs/ 文件夹权限

联系信息：
---------
如有问题，请联系团队成员。

