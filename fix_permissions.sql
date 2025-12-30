-- 修复权限关联的SQL脚本
-- 如果数据库中已有数据，运行此脚本建立权限链
-- 团队成员：[团队成员1], [团队成员2], [团队成员3]

USE bigcomp_access;

-- 插入配置文件（定义访问权限模板）
INSERT INTO profiles (profile_name, file_path, description) VALUES
('Employee Profile', 'files/profiles/employee_profile.json', 'Standard employee profile with access to office areas'),
('Contractor Profile', 'files/profiles/contractor_profile.json', 'Contractor profile with limited access permissions'),
('Intern Profile', 'files/profiles/intern_profile.json', 'Intern profile with basic access permissions')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- 插入资源组（将资源分组管理）
INSERT INTO resource_groups (group_name, security_level, file_path, description) VALUES
('office_access', 1, 'files/resource_groups/office_access.json', 'Office area access group, including office doors and elevators'),
('building_access', 0, 'files/resource_groups/building_access.json', 'Building access group, including main entrance')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- 建立权限链：徽章 → 配置文件
-- 为所有已存在的徽章分配配置文件
INSERT INTO badge_profiles (badge_id, profile_name) VALUES
('BX76Z541', 'Employee Profile'),  -- John的员工徽章
('BR59KA87', 'Employee Profile'),  -- Jane的员工徽章
('R7U39PL2', 'Contractor Profile'), -- Bob的承包商徽章
('B3746028', 'Intern Profile')     -- Alice的实习生徽章
ON DUPLICATE KEY UPDATE profile_name = VALUES(profile_name);

-- 建立权限链：配置文件 → 资源组
INSERT INTO profile_resource_groups (profile_name, group_name) VALUES
('Employee Profile', 'office_access'),    -- 员工可以访问办公区域
('Employee Profile', 'building_access'),   -- 员工可以访问大楼
('Contractor Profile', 'building_access'), -- 承包商只能访问大楼入口
('Intern Profile', 'office_access'),       -- 实习生可以访问办公区域
('Intern Profile', 'building_access')      -- 实习生可以访问大楼
ON DUPLICATE KEY UPDATE profile_name = VALUES(profile_name);

-- 建立权限链：资源组 → 资源
INSERT INTO resource_group_members (resource_id, group_name) VALUES
('RES001', 'building_access'),  -- 主入口属于大楼访问组
('RES002', 'office_access'),    -- 办公室门属于办公区域组
('RES003', 'office_access')     -- 电梯属于办公区域组
ON DUPLICATE KEY UPDATE resource_id = VALUES(resource_id);

-- 验证权限链
SELECT 
    b.badge_id AS '徽章ID',
    u.first_name AS '用户',
    p.profile_name AS '配置文件',
    rg.group_name AS '资源组',
    r.resource_id AS '资源ID',
    r.resource_name AS '资源名称'
FROM badges b
JOIN badge_profiles bp ON b.badge_id = bp.badge_id
JOIN profiles p ON bp.profile_name = p.profile_name
JOIN profile_resource_groups prg ON p.profile_name = prg.profile_name
JOIN resource_groups rg ON prg.group_name = rg.group_name
JOIN resource_group_members rgm ON rg.group_name = rgm.group_name
JOIN resources r ON rgm.resource_id = r.resource_id
LEFT JOIN users u ON b.user_id = u.user_id
WHERE b.is_active = TRUE
ORDER BY b.badge_id, r.resource_id;

