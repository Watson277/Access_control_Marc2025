-- 访问控制系统数据库脚本
-- 创建时间：2025-12-25
-- 团队成员：[团队成员1], [团队成员2], [团队成员3]

-- 创建数据库
CREATE DATABASE IF NOT EXISTS bigcomp_access CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bigcomp_access;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    user_id VARCHAR(50) PRIMARY KEY,
    gender ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    user_type ENUM('EMPLOYEE', 'CONTRACTOR', 'INTERN', 'VISITOR') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 徽章表
CREATE TABLE IF NOT EXISTS badges (
    badge_id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50),
    created_date DATE NOT NULL,
    expiration_date DATE,
    last_update_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 资源表
CREATE TABLE IF NOT EXISTS resources (
    resource_id VARCHAR(50) PRIMARY KEY,
    badge_reader_id VARCHAR(50),
    resource_name VARCHAR(200) NOT NULL,
    location VARCHAR(500),
    resource_type ENUM('DOOR', 'GATE', 'ELEVATOR', 'STAIRWAY', 'PRINTER', 'BEVERAGE_DISPENSER', 'OTHER') NOT NULL,
    state ENUM('CONTROLLED', 'UNCONTROLLED') DEFAULT 'CONTROLLED',
    building VARCHAR(100),
    floor INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 徽章读取器表
CREATE TABLE IF NOT EXISTS badge_readers (
    reader_id VARCHAR(50) PRIMARY KEY,
    resource_id VARCHAR(50) NOT NULL,
    reader_name VARCHAR(200),
    location VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (resource_id) REFERENCES resources(resource_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 更新资源表的外键约束
ALTER TABLE resources ADD FOREIGN KEY (badge_reader_id) REFERENCES badge_readers(reader_id) ON DELETE SET NULL;

-- 配置文件表
CREATE TABLE IF NOT EXISTS profiles (
    profile_name VARCHAR(100) PRIMARY KEY,
    file_path VARCHAR(500) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 资源组表
CREATE TABLE IF NOT EXISTS resource_groups (
    group_name VARCHAR(100) PRIMARY KEY,
    security_level INT DEFAULT 0,
    file_path VARCHAR(500) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 徽章-配置文件关联表（多对多）
CREATE TABLE IF NOT EXISTS badge_profiles (
    badge_id VARCHAR(50),
    profile_name VARCHAR(100),
    assigned_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (badge_id, profile_name),
    FOREIGN KEY (badge_id) REFERENCES badges(badge_id) ON DELETE CASCADE,
    FOREIGN KEY (profile_name) REFERENCES profiles(profile_name) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 配置文件-资源组关联表（多对多）
CREATE TABLE IF NOT EXISTS profile_resource_groups (
    profile_name VARCHAR(100),
    group_name VARCHAR(100),
    PRIMARY KEY (profile_name, group_name),
    FOREIGN KEY (profile_name) REFERENCES profiles(profile_name) ON DELETE CASCADE,
    FOREIGN KEY (group_name) REFERENCES resource_groups(group_name) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 资源-资源组关联表（多对多）
CREATE TABLE IF NOT EXISTS resource_group_members (
    resource_id VARCHAR(50),
    group_name VARCHAR(100),
    PRIMARY KEY (resource_id, group_name),
    FOREIGN KEY (resource_id) REFERENCES resources(resource_id) ON DELETE CASCADE,
    FOREIGN KEY (group_name) REFERENCES resource_groups(group_name) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 访问日志表（用于快速查询，实际日志存储在CSV文件中）
CREATE TABLE IF NOT EXISTS access_logs_summary (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    access_date DATE NOT NULL,
    access_time TIME NOT NULL,
    badge_id VARCHAR(50),
    reader_id VARCHAR(50),
    resource_id VARCHAR(50),
    user_id VARCHAR(50),
    user_name VARCHAR(200),
    status ENUM('GRANTED', 'DENIED') NOT NULL,
    reason TEXT,
    INDEX idx_date (access_date),
    INDEX idx_badge (badge_id),
    INDEX idx_resource (resource_id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 删除示例数据（如果存在，以便可以重复执行脚本）
DELETE FROM badge_profiles WHERE badge_id IN ('BX76Z541', 'BR59KA87', 'R7U39PL2', 'B3746028');
DELETE FROM badges WHERE badge_id IN ('BX76Z541', 'BR59KA87', 'R7U39PL2', 'B3746028');
DELETE FROM users WHERE user_id IN ('U001', 'U002', 'U003', 'U004');
DELETE FROM resource_group_members WHERE resource_id IN ('RES001', 'RES002', 'RES003');
DELETE FROM badge_readers WHERE reader_id IN ('BR001', 'BR002', 'BR003');
DELETE FROM resources WHERE resource_id IN ('RES001', 'RES002', 'RES003');

-- 插入示例数据
INSERT INTO users (user_id, gender, first_name, last_name, user_type) VALUES
('U001', 'MALE', 'John', 'Doe', 'EMPLOYEE'),
('U002', 'FEMALE', 'Jane', 'Smith', 'EMPLOYEE'),
('U003', 'MALE', 'Bob', 'Johnson', 'CONTRACTOR'),
('U004', 'FEMALE', 'Alice', 'Williams', 'INTERN');

INSERT INTO badges (badge_id, user_id, created_date, expiration_date, last_update_date, is_active) VALUES
('BX76Z541', 'U001', '2025-01-01', '2026-12-31', '2025-12-25', TRUE),
('BR59KA87', 'U002', '2025-01-15', '2026-12-31', '2025-12-25', TRUE),
('R7U39PL2', 'U003', '2025-02-01', '2025-12-31', '2025-12-25', TRUE),
('B3746028', 'U004', '2025-03-01', '2025-12-31', '2025-12-25', TRUE);

-- 先插入资源（badge_reader_id 暂时为 NULL，因为此时 badge_readers 还不存在）
INSERT INTO resources (resource_id, badge_reader_id, resource_name, location, resource_type, state, building, floor) VALUES
('RES001', NULL, 'Main Entrance Door', 'Building A, Floor 1', 'DOOR', 'CONTROLLED', 'Building A', 1),
('RES002', NULL, 'Office Door 201', 'Building A, Floor 2, Room 201', 'DOOR', 'CONTROLLED', 'Building A', 2),
('RES003', NULL, 'Elevator 1', 'Building A', 'ELEVATOR', 'CONTROLLED', 'Building A', 0);

-- 然后插入徽章读取器（引用已存在的 resource_id）
INSERT INTO badge_readers (reader_id, resource_id, reader_name, location, is_active) VALUES
('BR001', 'RES001', 'Main Entrance Reader', 'Building A, Floor 1', TRUE),
('BR002', 'RES002', 'Office Door Reader', 'Building A, Floor 2, Room 201', TRUE),
('BR003', 'RES003', 'Elevator Reader', 'Building A, Elevator 1', TRUE);

-- 最后更新资源表的 badge_reader_id（现在 badge_readers 已存在）
UPDATE resources SET badge_reader_id = 'BR001' WHERE resource_id = 'RES001';
UPDATE resources SET badge_reader_id = 'BR002' WHERE resource_id = 'RES002';
UPDATE resources SET badge_reader_id = 'BR003' WHERE resource_id = 'RES003';

