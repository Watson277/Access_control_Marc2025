# 项目结构说明

## 目录结构

```
project/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── bigcomp/
│       │           └── accesscontrol/
│       │               ├── Main.java                    # 主入口类
│       │               ├── model/                        # 实体类包
│       │               │   ├── User.java                 # 用户实体
│       │               │   ├── Badge.java                # 徽章实体
│       │               │   ├── Resource.java            # 资源实体
│       │               │   ├── BadgeReader.java          # 徽章读取器实体
│       │               │   ├── Profile.java              # 配置文件实体
│       │               │   ├── ResourceGroup.java        # 资源组实体
│       │               │   ├── AccessRequest.java        # 访问请求实体
│       │               │   └── AccessRule.java           # 访问规则实体
│       │               ├── dao/                          # 数据访问层
│       │               │   ├── UserDAO.java
│       │               │   ├── UserDAOImpl.java
│       │               │   ├── BadgeDAO.java
│       │               │   ├── BadgeDAOImpl.java
│       │               │   ├── ResourceDAO.java
│       │               │   ├── ResourceDAOImpl.java
│       │               │   ├── ProfileDAO.java
│       │               │   ├── ProfileDAOImpl.java
│       │               │   ├── AccessRuleDAO.java
│       │               │   └── AccessRuleDAOImpl.java
│       │               ├── service/                      # 业务逻辑层
│       │               │   ├── AccessControlService.java
│       │               │   └── AccessControlServiceImpl.java
│       │               ├── gui/                          # 图形用户界面
│       │               │   ├── MainWindow.java           # 主窗口
│       │               │   ├── UserManagementPanel.java   # 用户管理面板
│       │               │   ├── BadgeManagementPanel.java   # 徽章管理面板
│       │               │   ├── ResourceManagementPanel.java # 资源管理面板
│       │               │   ├── ProfileManagementPanel.java # 配置文件管理面板
│       │               │   ├── LogSearchPanel.java        # 日志查询面板
│       │               │   └── MonitoringPanel.java      # 实时监控面板
│       │               └── util/                         # 工具类
│       │                   ├── DatabaseConnection.java    # 数据库连接工具
│       │                   └── LogManager.java            # 日志管理器
│       └── resources/
│           └── config/
│               └── database.properties                   # 数据库配置文件
├── database/
│   └── db.sql                                            # 数据库脚本
├── files/                                                 # 配置文件和资源文件
│   ├── profiles/                                         # 配置文件目录
│   │   └── employee_profile.json
│   └── resource_groups/                                  # 资源组配置目录
│       ├── office_access.json
│       ├── printer_access.json
│       └── beverage_access.json
├── logs/                                                 # 日志文件目录（运行时生成）
├── README.txt                                            # 项目说明文件
├── pom.xml                                               # Maven配置文件
├── build.bat                                             # Windows构建脚本
├── build.sh                                              # Linux/Mac构建脚本
└── .gitignore                                            # Git忽略文件

```

## 核心功能模块

### 1. 模型层 (model)
- 定义了所有业务实体类
- 使用枚举类型定义状态和类型
- 实现了实体之间的关联关系

### 2. 数据访问层 (dao)
- 实现了DAO模式
- 提供数据库CRUD操作
- 处理实体之间的关联关系

### 3. 业务逻辑层 (service)
- 实现访问控制核心逻辑
- 验证徽章有效性
- 检查访问权限
- 处理访问请求

### 4. 用户界面层 (gui)
- 使用Java Swing构建GUI
- 提供6个主要功能面板
- 实现实时监控功能

### 5. 工具层 (util)
- 数据库连接管理
- 日志文件管理
- CSV格式日志记录

## 数据库设计

主要数据表：
- users: 用户表
- badges: 徽章表
- resources: 资源表
- badge_readers: 徽章读取器表
- profiles: 配置文件表
- resource_groups: 资源组表
- badge_profiles: 徽章-配置文件关联表
- profile_resource_groups: 配置文件-资源组关联表
- resource_group_members: 资源-资源组关联表
- access_logs_summary: 访问日志摘要表

## 日志系统

- 日志文件格式：CSV
- 存储结构：logs/年/月/YYYY-MM-DD.csv
- 日志内容：日期、时间、徽章ID、读取器ID、资源ID、用户ID、用户名、状态

## 配置文件系统

- 配置文件格式：JSON
- 配置文件存储：files/profiles/
- 资源组配置存储：files/resource_groups/

## 构建和运行

### 构建项目
```bash
# Windows
build.bat

# Linux/Mac
chmod +x build.sh
./build.sh

# 或使用Maven
mvn clean package
```

### 运行项目
```bash
java -jar access-control-system.jar
```

## 注意事项

1. 首次运行前需要：
   - 安装MySQL数据库
   - 创建数据库并导入db.sql
   - 配置database.properties文件

2. 日志目录会自动创建

3. 配置文件需要手动创建或通过GUI生成

