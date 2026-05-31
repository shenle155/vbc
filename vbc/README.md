# 智慧园区 AI 视觉分析 Demo 平台

基于浏览器端的实时视频 AI 分析平台，覆盖视频管理、目标检测、告警系统和数据大屏。

## 技术栈

| 层 | 技术 |
|----|------|
| 前端 | Vue 3 + TypeScript + Vite + Element Plus + ECharts + TensorFlow.js |
| 后端 | Spring Boot 3.2 + MyBatis-Plus + JavaCV + STOMP WebSocket |
| 数据库 | MySQL 8.0 |

## 功能模块

- 数据大屏：实时统计卡片 + 人数趋势 + 告警分布
- 视频管理：上传 / 存储 / 帧提取（JavaCV） / 删除
- AI 检测：浏览器端 COCO-SSD 目标检测，Canvas 实时绘制
- 告警系统：区域入侵 / 人群聚集 / 人数超限，WebSocket 实时通知
- 设备管理：摄像头注册 / 心跳 / 状态监控
- 热力图：人群密度网格分析

## 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 18+
- MySQL 8.0+

## 快速启动

```bash
# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE vbc CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 2. 启动后端 (port 8080)
cd vbc/backend
mvn spring-boot:run

# 3. 启动前端 (port 5173)
cd vbc/frontend
npm install
npm run dev
```

访问 http://localhost:5173
