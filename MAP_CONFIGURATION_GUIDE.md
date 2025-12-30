# 地图配置指南
## Map Configuration Guide

本指南说明如何配置资源在 `Map.png` 图片上的位置坐标。

---

## 📍 当前实现的功能

✅ **已完成**:
1. 加载 `Map.png` 作为监控面板的背景
2. 根据访问结果在对应位置显示：
   - 🟢 **绿色圆点** = 访问成功（Granted）
   - 🔴 **红色圆点** = 访问被拒绝（Denied）
3. 标记会在访问后显示3秒，然后自动消失
4. 图片会自动缩放以适应面板大小

---

## ⚙️ 配置资源位置

### 方法 1: 修改代码中的坐标（推荐用于测试）

编辑文件：`src/main/java/com/bigcomp/accesscontrol/util/ResourceLocationMapper.java`

在 `initializeDefaultLocations()` 方法中，修改资源坐标：

```java
private void initializeDefaultLocations() {
    // 坐标基于图片尺寸（假设默认图片为 1200x800）
    // 格式：resourceLocations.put("资源ID", new Point(x坐标, y坐标));
    
    // RES001 - 主入口门
    resourceLocations.put("RES001", new Point(150, 400));
    
    // RES002 - 办公室门 201
    resourceLocations.put("RES002", new Point(600, 200));
    
    // RES003 - 电梯 1
    resourceLocations.put("RES003", new Point(600, 700));
    
    // 添加更多资源...
    // resourceLocations.put("RES004", new Point(x, y));
}
```

### 方法 2: 使用图片编辑工具确定坐标

1. 使用图片查看器或编辑器打开 `Map.png`
2. 找到每个访问控制点的位置
3. 记录该位置的像素坐标（x, y）
4. 将坐标填入 `ResourceLocationMapper.java`

**坐标系统**:
- **X轴**: 从左到右（0 = 最左边）
- **Y轴**: 从上到下（0 = 最上面）
- 坐标单位：像素

---

## 🗺️ 根据图片描述识别房间和访问点

根据您提供的图片描述，以下是主要区域和访问控制点：

### 主要区域：
1. **办公室 (OFFICE)**: 1-8号办公室
2. **开放工作区 (OPEN WORKSTATIONS)**: 顶部中央
3. **会议室 (CONFERENCE)**: 中央
4. **接待区 (RECEPTION)**: 中央和底部中央
5. **服务器室 (SERVER RM)**: 右上角（有红色标记）
6. **打印机 (PRINTER)**: PRINTER 1, PRINTER 2
7. **电梯**:
   - 货运电梯 (FREIGHT ELEVATORS): 最右侧
   - 乘客电梯 (PASSENGER ELEVATORS): 底部中央（有红色标记）
8. **洗手间**: M (男), W (女)
9. **楼梯**: 底部左侧和右侧

### 访问控制点（根据描述）：
- **红色点 (2个)**:
  1. 服务器室入口
  2. 乘客电梯中央门
  
- **绿色点 (4个)**:
  1. 办公室4和5入口的走廊
  2. 办公室7入口的走廊
  3. 底部左侧楼梯，办公室1入口
  4. 底部右侧，通往货运电梯和第二个楼梯

---

## 🔧 如何确定精确坐标

### 步骤 1: 查看图片尺寸
```java
// 在 ResourceLocationMapper.java 中添加调试代码
System.out.println("Image width: " + imageWidth);
System.out.println("Image height: " + imageHeight);
```

### 步骤 2: 使用图片坐标工具
推荐工具：
- **Windows**: Paint, GIMP, Photoshop
- **在线工具**: https://www.image-map.net/
- **Java工具**: 可以创建一个简单的坐标选择工具

### 步骤 3: 测试和调整
1. 编译并运行应用
2. 进行访问测试
3. 观察标记是否出现在正确位置
4. 如果位置不对，调整坐标并重新编译

---

## 📝 示例配置

假设您的 `Map.png` 尺寸为 1200x800 像素，以下是一个示例配置：

```java
private void initializeDefaultLocations() {
    // 假设的坐标（需要根据实际图片调整）
    
    // RES001 - 主入口（假设在左侧中央）
    resourceLocations.put("RES001", new Point(100, 400));
    
    // RES002 - 办公室门 201（假设在顶部中央）
    resourceLocations.put("RES002", new Point(600, 150));
    
    // RES003 - 电梯 1（假设在底部中央）
    resourceLocations.put("RES003", new Point(600, 700));
    
    // 如果数据库中有更多资源，添加它们：
    // resourceLocations.put("RES004", new Point(800, 300)); // 服务器室
    // resourceLocations.put("RES005", new Point(200, 600)); // 办公室1
    // resourceLocations.put("RES006", new Point(1000, 400)); // 货运电梯
}
```

---

## 🎯 快速测试方法

1. **临时测试坐标**:
   - 在代码中设置一个资源的坐标为图片中心
   - 进行访问测试
   - 观察标记是否出现在中心
   - 如果出现，说明系统工作正常，只需调整坐标

2. **使用网格**:
   - 将图片分成网格（例如 10x10）
   - 估算每个资源在哪个网格
   - 计算网格中心坐标
   - 填入代码并测试

3. **逐步调整**:
   - 从一个大概的位置开始
   - 每次测试后微调坐标
   - 直到标记出现在正确位置

---

## ⚠️ 注意事项

1. **坐标系统**: 
   - Java的坐标系统：原点(0,0)在左上角
   - X轴向右递增，Y轴向下递增

2. **图片缩放**:
   - 系统会自动缩放图片以适应面板
   - 坐标也会相应缩放
   - 确保坐标基于原始图片尺寸

3. **资源ID匹配**:
   - 确保代码中的资源ID与数据库中的资源ID一致
   - 检查数据库中的 `resources` 表

4. **重新编译**:
   - 修改坐标后需要重新编译：
   ```bash
   mvn clean package
   ```

---

## 🔍 调试技巧

### 添加调试输出
在 `MonitoringPanel.java` 的 `paintComponent` 方法中添加：

```java
System.out.println("Drawing marker for " + resourceId + " at (" + markerX + ", " + markerY + ")");
```

### 显示所有资源位置
临时添加代码显示所有已配置的资源：

```java
for (String resourceId : locationMapper.getMappedResourceIds()) {
    Point loc = locationMapper.getLocation(resourceId, imgWidth, imgHeight);
    System.out.println(resourceId + " -> (" + loc.x + ", " + loc.y + ")");
}
```

---

## 📋 检查清单

- [ ] `Map.png` 已复制到 `src/main/resources/` 目录
- [ ] 已识别图片中的所有访问控制点
- [ ] 已确定每个资源ID对应的位置坐标
- [ ] 已在 `ResourceLocationMapper.java` 中配置坐标
- [ ] 已重新编译项目
- [ ] 已测试访问功能，标记出现在正确位置

---

## 🆘 需要帮助？

如果您无法确定精确坐标，可以提供以下信息：

1. **图片尺寸**: `Map.png` 的实际宽度和高度（像素）
2. **资源列表**: 数据库中所有资源的ID和名称
3. **位置描述**: 每个资源在图片中的大概位置（例如："左上角"、"中央"、"右下角"）

我可以帮您估算坐标或创建一个坐标配置工具。

---

**配置完成后，重新编译并运行应用即可看到实时访问状态显示在地图上！**

