# Git 推送到远程仓库指南
## Git Push to Remote Repository Guide

本指南说明如何将本地代码推送到远程Git仓库。

---

## 📋 当前状态

**远程仓库**: `git@github.com:Watson277/Access_control_Marc2025.git`  
**当前分支**: `master`  
**远程分支**: `origin/master`

---

## 🚀 推送步骤

### 步骤 1: 查看更改状态
```bash
git status
```

### 步骤 2: 添加所有更改到暂存区
```bash
# 添加所有更改（包括新文件和修改的文件）
git add .

# 或者只添加特定文件
git add <文件名>
```

### 步骤 3: 提交更改
```bash
git commit -m "提交信息描述"
```

**提交信息示例**:
- `"Add real-time simulation and map visualization features"`
- `"Implement cached access control service"`
- `"Add floor plan map with access status indicators"`

### 步骤 4: 推送到远程仓库
```bash
# 推送到远程 master 分支
git push origin master

# 或者如果当前分支已设置上游
git push
```

---

## 📝 完整推送流程示例

```bash
# 1. 查看状态
git status

# 2. 添加所有更改
git add .

# 3. 提交更改
git commit -m "Add real-time simulation, map visualization, and cached access control"

# 4. 推送到远程
git push origin master
```

---

## ⚠️ 常见问题

### 问题 1: 远程有新的提交
**错误信息**: `Updates were rejected because the remote contains work...`

**解决方案**:
```bash
# 先拉取远程更改
git pull origin master

# 如果有冲突，解决冲突后再次提交
git add .
git commit -m "Merge remote changes"
git push origin master
```

### 问题 2: 需要强制推送（不推荐）
**警告**: 强制推送会覆盖远程更改，请谨慎使用！

```bash
git push origin master --force
```

### 问题 3: SSH 密钥未配置
**错误信息**: `Permission denied (publickey)`

**解决方案**:
1. 生成SSH密钥（如果还没有）:
   ```bash
   ssh-keygen -t ed25519 -C "your_email@example.com"
   ```
2. 将公钥添加到GitHub:
   - 复制 `~/.ssh/id_ed25519.pub` 的内容
   - 在GitHub: Settings → SSH and GPG keys → New SSH key

### 问题 4: 使用 HTTPS 而不是 SSH
如果SSH有问题，可以切换到HTTPS:
```bash
# 查看当前远程URL
git remote -v

# 更改为HTTPS
git remote set-url origin https://github.com/Watson277/Access_control_Marc2025.git

# 推送（会提示输入用户名和密码/Token）
git push origin master
```

---

## 🔐 GitHub 认证

### 使用 Personal Access Token (推荐)
1. GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
2. 生成新token，选择权限（至少需要 `repo`）
3. 使用token作为密码推送

### 使用 SSH 密钥（推荐）
- 更安全，无需每次输入密码
- 配置一次后永久使用

---

## 📌 最佳实践

1. **提交前检查**:
   ```bash
   git status
   git diff  # 查看具体更改
   ```

2. **有意义的提交信息**:
   - 使用清晰、描述性的提交信息
   - 说明做了什么更改，为什么更改

3. **频繁提交**:
   - 完成一个功能就提交一次
   - 不要积累太多更改

4. **推送前测试**:
   - 确保代码可以编译
   - 运行基本测试

5. **不要提交敏感信息**:
   - 检查 `.gitignore` 是否正确配置
   - 不要提交密码、API密钥等

---

## 🎯 快速推送命令

```bash
# 一键推送（添加、提交、推送）
git add . && git commit -m "Your commit message" && git push origin master
```

---

## 📋 当前需要推送的文件

根据 `git status`，以下文件需要提交：

### 修改的文件:
- `files/profiles/employee_profile.json`
- `files/resource_groups/*.json`
- `fix_permissions.sql`
- 多个Java源文件
- `src/main/resources/config/database.properties`

### 新文件:
- `COMPILE_GUIDE.md`
- `MAP_CONFIGURATION_GUIDE.md`
- `REALTIME_TESTING_GUIDE.md`
- `Map.png`
- `CachedAccessControlService.java`
- `AccessControlCache.java`
- `ResourceLocationMapper.java`

---

## ✅ 推送检查清单

- [ ] 检查 `.gitignore` 是否正确（排除 `target/`, `logs/` 等）
- [ ] 检查是否有敏感信息（密码、密钥等）
- [ ] 添加所有需要提交的文件
- [ ] 编写有意义的提交信息
- [ ] 提交更改
- [ ] 推送到远程仓库
- [ ] 在GitHub上验证更改已上传

---

**准备好后，运行推送命令即可！**

