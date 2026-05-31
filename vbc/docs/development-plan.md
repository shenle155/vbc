# 智慧园区 AI 视觉分析 Demo 平台 — 开发文档

> 版本：v1.0 | 日期：2026-05-30

---

## 一、项目背景

本项目专注于 **AI 视觉分析**，集中于人工智能、机器视觉、深度学习方向，致力于视频检测、视频分析类产品研发。具备从底层核心视频算法开发、嵌入式软硬件设计到应用系统开发的完整能力链，服务行业覆盖：视频结构化输出、城市交通、工厂生产、智慧校园、智慧平安乡村、智慧园区/街道、智慧农场等。

本项目是一个 **智慧园区 AI 视觉分析 Demo 平台**，可在浏览器中运行，具备实时视频分析、目标检测、告警联动和数据大屏等核心功能。项目旨在展示全栈工程能力以及对公司核心业务方向的深度理解。

---

## 二、技术选型

| 层 | 技术 | 版本 | 选型理由 |
|----|------|------|----------|
| 前端框架 | Vue 3 + TypeScript + Vite | Vue 3.4 / Vite 5 | 响应式系统天然适合数据大屏，国内社区成熟 |
| UI 组件库 | Element Plus | 2.7 | 企业级中后台生态最完善，大屏/表格/表单开箱即用 |
| 可视化 | ECharts + vue-echarts | ECharts 5 | 国产图表库事实标准，热力图/折线图/饼图覆盖全部需求 |
| AI 推理 | TensorFlow.js + COCO-SSD | TF.js 4.20 | 浏览器端推理，零 GPU 成本，检测 person/car/bus/truck 等 80 类 |
| 后端框架 | Spring Boot 3 | 3.2 | Java 生态标准，WebSocket/REST/异步任务一体化 |
| ORM | MyBatis-Plus | 3.5 | Lambda 类型安全查询，中文文档友好 |
| 视频处理 | JavaCV (FFmpegFrameGrabber) | 1.5 | 支持所有 FFmpeg 视频格式，帧提取稳定可靠 |
| 实时通信 | STOMP over WebSocket + SockJS | — | 发布/订阅语义天然匹配业务，Spring 原生支持 |
| 数据库 | MySQL | 8.0 | JSON 列支持，性能稳定 |

### 关键决策说明

**为什么前端 AI 推理而不是后端 GPU？**
浏览器端 COCO-SSD 模型约 11MB，WebGL 加速下可达 15 FPS。对于 Demo 平台，精度足以展示效果，且运维成本为零。架构预留了后端推理服务的扩展点。

**为什么帧检测模式而不是视频流？**
浏览器无法直接解码 RTSP 流，WebRTC 网关引入巨大复杂度。帧模式（1 FPS）对园区监控场景够用，且支持任意 FFmpeg 兼容格式。

**为什么 Element Plus 而不是 Naive UI？**
Element Plus 的 el-table、el-form、el-dialog、el-menu 在企业后台场景经过充分验证，中文文档质量高，第三方教程丰富。

---

## 三、项目结构

```
vbc/
├── frontend/                          # Vue 3 + TypeScript + Vite
│   ├── public/
│   │   └── models/                    # TF.js 模型本地缓存（可选）
│   ├── src/
│   │   ├── api/                       # HTTP + WebSocket 客户端层
│   │   │   ├── request.ts             # Axios 实例、拦截器
│   │   │   ├── video.ts
│   │   │   ├── device.ts
│   │   │   ├── detection.ts
│   │   │   ├── alarm.ts
│   │   │   ├── dashboard.ts
│   │   │   └── zone.ts
│   │   ├── assets/
│   │   │   ├── images/
│   │   │   └── styles/
│   │   │       ├── variables.scss     # Element Plus 变量覆写
│   │   │       ├── global.scss
│   │   │       └── transitions.scss
│   │   ├── components/
│   │   │   ├── common/                # 通用组件
│   │   │   │   ├── StatCard.vue       # 统计卡片（数字滚动动画）
│   │   │   │   ├── PageHeader.vue     # 页面标题栏
│   │   │   │   ├── EmptyState.vue     # 空数据占位
│   │   │   │   └── ConfirmDialog.vue  # 确认弹窗
│   │   │   ├── video/                 # 视频域组件
│   │   │   │   ├── VideoUploadDialog.vue   # 上传弹窗
│   │   │   │   ├── VideoCard.vue           # 视频卡片
│   │   │   │   ├── VideoPlayer.vue         # HTML5 播放器封装
│   │   │   │   ├── DetectionCanvas.vue     # Canvas 检测框叠加层
│   │   │   │   └── DetectionControls.vue   # 控制面板
│   │   │   ├── charts/                # ECharts 封装
│   │   │   │   ├── PersonTrendChart.vue
│   │   │   │   ├── AlarmTypePieChart.vue
│   │   │   │   ├── DeviceStatusBarChart.vue
│   │   │   │   └── CrowdHeatmapChart.vue
│   │   │   ├── alarm/
│   │   │   │   ├── AlarmBadge.vue          # 告警徽标
│   │   │   │   ├── AlarmTable.vue          # 告警表格
│   │   │   │   ├── AlarmRuleForm.vue       # 规则表单
│   │   │   │   └── ZoneDrawer.vue          # ROI 多边形绘制器
│   │   │   └── layout/
│   │   │       ├── AppSidebar.vue
│   │   │       ├── AppHeader.vue
│   │   │       └── MainLayout.vue
│   │   ├── composables/               # Composition API 逻辑
│   │   │   ├── useWebSocket.ts        # STOMP 连接/订阅/重连
│   │   │   ├── useTensorFlow.ts       # TF.js 模型加载/推理
│   │   │   ├── useDetection.ts        # 检测循环编排器
│   │   │   ├── useVideoPlayer.ts      # 视频播放状态
│   │   │   └── usePolling.ts          # 通用轮询
│   │   ├── router/
│   │   │   └── index.ts               # 路由定义
│   │   ├── store/                     # Pinia 状态管理
│   │   │   ├── app.ts
│   │   │   ├── video.ts
│   │   │   ├── alarm.ts
│   │   │   ├── dashboard.ts
│   │   │   └── websocket.ts
│   │   ├── types/                     # TypeScript 接口
│   │   │   ├── video.ts
│   │   │   ├── device.ts
│   │   │   ├── detection.ts
│   │   │   ├── alarm.ts
│   │   │   ├── dashboard.ts
│   │   │   └── websocket.ts
│   │   ├── utils/
│   │   │   ├── tf/
│   │   │   │   ├── modelLoader.ts     # 加载 COCO-SSD + IndexedDB 缓存
│   │   │   │   └── postProcess.ts     # NMS / 置信度过滤 / 坐标缩放
│   │   │   ├── format.ts
│   │   │   └── validate.ts
│   │   ├── views/                     # 路由页面
│   │   │   ├── DashboardView.vue
│   │   │   ├── VideoListView.vue
│   │   │   ├── VideoDetailView.vue    # 核心页面：实时检测分析
│   │   │   ├── AlarmListView.vue
│   │   │   ├── AlarmConfigView.vue
│   │   │   ├── DeviceListView.vue
│   │   │   └── HeatmapView.vue
│   │   ├── App.vue
│   │   └── main.ts
│   ├── .env
│   ├── .env.development
│   ├── .env.production
│   ├── index.html
│   ├── package.json
│   ├── tsconfig.json
│   └── vite.config.ts                 # 代理 /api → localhost:8080
│
├── backend/
│   ├── src/main/java/com/vbc/
│   │   ├── VbcApplication.java
│   │   ├── config/
│   │   │   ├── CorsConfig.java            # CORS 允许前端 5173
│   │   │   ├── WebSocketConfig.java        # STOMP Broker + Endpoint
│   │   │   ├── MybatisPlusConfig.java      # 分页插件
│   │   │   ├── JacksonConfig.java
│   │   │   └── FileUploadConfig.java
│   │   ├── controller/
│   │   │   ├── VideoController.java
│   │   │   ├── DeviceController.java
│   │   │   ├── DetectionController.java
│   │   │   ├── AlarmController.java
│   │   │   ├── ZoneController.java
│   │   │   ├── DashboardController.java
│   │   │   └── FileController.java
│   │   ├── service/
│   │   │   ├── VideoService.java
│   │   │   ├── DeviceService.java
│   │   │   ├── DetectionService.java
│   │   │   ├── AlarmService.java
│   │   │   ├── ZoneService.java
│   │   │   ├── DashboardService.java
│   │   │   ├── FrameExtractionService.java  # JavaCV 帧提取
│   │   │   ├── AlarmEvaluationService.java  # 告警规则引擎
│   │   │   └── NotificationService.java     # WebSocket 广播
│   │   ├── service/impl/
│   │   │   └── (上述接口的实现类)
│   │   ├── repository/                    # MyBatis-Plus Mapper
│   │   │   ├── VideoMapper.java
│   │   │   ├── DeviceMapper.java
│   │   │   ├── DetectionRecordMapper.java
│   │   │   ├── AlarmRuleMapper.java
│   │   │   ├── AlarmRecordMapper.java
│   │   │   ├── ZoneMapper.java
│   │   │   ├── FrameMapper.java
│   │   │   └── CrowdHeatmapDataMapper.java
│   │   ├── entity/
│   │   │   ├── Video.java
│   │   │   ├── Device.java
│   │   │   ├── DetectionRecord.java
│   │   │   ├── AlarmRule.java
│   │   │   ├── AlarmRecord.java
│   │   │   ├── Zone.java
│   │   │   ├── Frame.java
│   │   │   └── CrowdHeatmapData.java
│   │   ├── dto/                            # 请求体
│   │   │   ├── VideoUploadDTO.java
│   │   │   ├── AlarmRuleSaveDTO.java
│   │   │   ├── ZoneSaveDTO.java
│   │   │   ├── DetectionReportDTO.java
│   │   │   ├── AlarmHandleDTO.java
│   │   │   └── PageQueryDTO.java
│   │   ├── vo/                             # 响应体
│   │   │   ├── VideoVO.java
│   │   │   ├── DeviceVO.java
│   │   │   ├── DetectionVO.java
│   │   │   ├── AlarmRecordVO.java
│   │   │   ├── AlarmRuleVO.java
│   │   │   ├── ZoneVO.java
│   │   │   ├── DashboardOverviewVO.java
│   │   │   ├── PersonTrendVO.java
│   │   │   ├── AlarmStatsVO.java
│   │   │   ├── DeviceStatusVO.java
│   │   │   ├── HeatmapDataVO.java
│   │   │   └── PageResult.java
│   │   ├── websocket/
│   │   │   ├── WebSocketAuthInterceptor.java
│   │   │   ├── DetectionReportHandler.java
│   │   │   └── WebSocketSessionManager.java
│   │   ├── handler/
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── util/
│   │   │   ├── FileUtil.java
│   │   │   ├── VideoUtil.java              # 视频元数据提取
│   │   │   └── PolygonUtil.java            # 射线法点面判断
│   │   └── job/
│   │       └── DashboardStatsJob.java      # 定时推送仪表盘数据
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   ├── application-dev.yml
│   │   └── mapper/
│   ├── sql/
│   │   ├── V1__schema.sql
│   │   └── V2__seed_data.sql
│   ├── uploads/                           # 运行时上传目录（.gitignored）
│   │   ├── videos/
│   │   ├── frames/
│   │   ├── thumbnails/
│   │   └── snapshots/
│   └── pom.xml
│
├── docs/
│   └── development-plan.md               # 本文档
├── .gitignore
└── README.md
```

---

## 四、路由设计

| 路径 | 组件 | 名称 | 说明 |
|------|------|------|------|
| `/dashboard` | DashboardView | 数据大屏 | 统计卡片 + 趋势图 + 告警分布 + 设备状态 |
| `/videos` | VideoListView | 视频管理 | 视频上传、搜索、卡片列表 |
| `/videos/:id` | VideoDetailView | 实时分析 | **核心页面**：播放器 + 检测叠加层 + 结果面板 |
| `/alarms` | AlarmListView | 告警记录 | 告警筛选、列表、处理操作 |
| `/alarms/config` | AlarmConfigView | 告警规则 | 规则列表 + 新增/编辑表单 |
| `/devices` | DeviceListView | 设备管理 | 摄像头注册、状态监控 |
| `/heatmap` | HeatmapView | 热力图 | 视频选择 + 时间范围 + 密度热力图 |

---

## 五、数据库设计

### ER 关系

```
vbc_device ──(1:N)── vbc_video
vbc_video  ──(1:N)── vbc_frame
vbc_video  ──(1:N)── vbc_zone
vbc_video  ──(1:N)── vbc_detection_record
vbc_video  ──(1:N)── vbc_alarm_record
vbc_video  ──(1:N)── vbc_crowd_heatmap_data
vbc_zone   ──(1:N)── vbc_alarm_rule
vbc_alarm_rule ──(1:N)── vbc_alarm_record
```

### 表结构

#### vbc_video（视频）

| 列 | 类型 | 说明 |
|----|------|------|
| id | BIGINT PK AUTO_INCREMENT | 视频 ID |
| title | VARCHAR(255) NOT NULL | 用户自定义标题 |
| file_name | VARCHAR(255) NOT NULL | 原始文件名 |
| file_path | VARCHAR(500) NOT NULL | 服务端存储路径 |
| file_size | BIGINT | 文件大小（字节） |
| duration_seconds | INT | 视频时长（秒） |
| width | INT | 帧宽度 |
| height | INT | 帧高度 |
| fps | DOUBLE | 帧率 |
| status | VARCHAR(20) DEFAULT 'UPLOADING' | UPLOADING / PROCESSING / READY / ERROR |
| thumbnail_path | VARCHAR(500) | 缩略图路径 |
| device_id | BIGINT FK → vbc_device.id | 关联设备（可选） |
| created_at | DATETIME(3) | 创建时间 |
| updated_at | DATETIME(3) | 更新时间 |

#### vbc_device（设备）

| 列 | 类型 | 说明 |
|----|------|------|
| id | BIGINT PK AUTO_INCREMENT | 设备 ID |
| device_name | VARCHAR(255) NOT NULL | 设备名称 |
| device_code | VARCHAR(100) UNIQUE NOT NULL | 设备唯一编码 |
| ip_address | VARCHAR(45) | IP 地址 |
| stream_url | VARCHAR(500) | RTSP/RTMP 流地址 |
| location | VARCHAR(255) | 安装位置 |
| status | VARCHAR(20) DEFAULT 'OFFLINE' | ONLINE / OFFLINE / MAINTENANCE |
| last_heartbeat | DATETIME(3) | 最后心跳时间 |
| created_at | DATETIME(3) | 创建时间 |
| updated_at | DATETIME(3) | 更新时间 |

#### vbc_zone（ROI 区域）

| 列 | 类型 | 说明 |
|----|------|------|
| id | BIGINT PK AUTO_INCREMENT | 区域 ID |
| zone_name | VARCHAR(255) NOT NULL | 区域名称 |
| video_id | BIGINT FK → vbc_video.id | 所属视频 |
| zone_color | VARCHAR(20) DEFAULT '#FF0000' | 显示颜色 |
| polygon_points | JSON NOT NULL | `[{"x":0.1,"y":0.2},...]` 归一化坐标(0-1) |
| created_at | DATETIME(3) | 创建时间 |
| updated_at | DATETIME(3) | 更新时间 |

#### vbc_alarm_rule（告警规则）

| 列 | 类型 | 说明 |
|----|------|------|
| id | BIGINT PK AUTO_INCREMENT | 规则 ID |
| rule_name | VARCHAR(255) NOT NULL | 规则名称 |
| rule_type | VARCHAR(30) NOT NULL | ZONE_INTRUSION / CROWD_GATHERING / PEOPLE_EXCEED / LOITERING |
| video_id | BIGINT FK → vbc_video.id | 适用视频（NULL=全局） |
| zone_id | BIGINT FK → vbc_zone.id | 适用区域（NULL=整帧） |
| threshold_value | INT | 人数阈值 |
| duration_seconds | INT | 滞留时长阈值 |
| enabled | TINYINT(1) DEFAULT 1 | 是否启用 |
| alarm_level | VARCHAR(20) DEFAULT 'WARNING' | INFO / WARNING / CRITICAL |
| created_at | DATETIME(3) | 创建时间 |
| updated_at | DATETIME(3) | 更新时间 |

#### vbc_alarm_record（告警记录）

| 列 | 类型 | 说明 |
|----|------|------|
| id | BIGINT PK AUTO_INCREMENT | 告警 ID |
| rule_id | BIGINT FK → vbc_alarm_rule.id | 触发规则 |
| video_id | BIGINT FK → vbc_video.id | 所属视频 |
| alarm_type | VARCHAR(30) NOT NULL | 告警类型（冗余） |
| alarm_level | VARCHAR(20) NOT NULL | 告警级别（冗余） |
| alarm_message | TEXT | 告警描述 |
| snapshot_path | VARCHAR(500) | 触发帧截图路径 |
| frame_index | INT | 触发帧序号 |
| timestamp_seconds | DOUBLE | 视频时间偏移 |
| handled | TINYINT(1) DEFAULT 0 | 是否已处理 |
| handled_by | VARCHAR(100) | 处理人 |
| handled_at | DATETIME(3) | 处理时间 |
| created_at | DATETIME(3) | 创建时间 |

#### vbc_frame（视频帧）

| 列 | 类型 | 说明 |
|----|------|------|
| id | BIGINT PK AUTO_INCREMENT | 帧 ID |
| video_id | BIGINT FK → vbc_video.id | 所属视频 |
| frame_index | INT NOT NULL | 帧序号 |
| timestamp_seconds | DOUBLE NOT NULL | 时间偏移 |
| file_path | VARCHAR(500) NOT NULL | JPEG 文件路径 |
| processed | TINYINT(1) DEFAULT 0 | 是否已检测 |
| created_at | DATETIME(3) | 创建时间 |

#### vbc_detection_record（检测记录）

| 列 | 类型 | 说明 |
|----|------|------|
| id | BIGINT PK AUTO_INCREMENT | 记录 ID |
| video_id | BIGINT FK → vbc_video.id | 所属视频 |
| frame_index | INT NOT NULL | 帧序号 |
| timestamp_seconds | DOUBLE NOT NULL | 时间偏移 |
| detected_objects | JSON NOT NULL | `[{"class":"person","confidence":0.94,"bbox":[x,y,w,h]},...]` |
| person_count | INT DEFAULT 0 | 人数（预计算） |
| vehicle_count | INT DEFAULT 0 | 车辆数（预计算） |
| total_count | INT DEFAULT 0 | 总数（预计算） |
| created_at | DATETIME(3) | 创建时间 |

#### vbc_crowd_heatmap_data（热力图数据）

| 列 | 类型 | 说明 |
|----|------|------|
| id | BIGINT PK AUTO_INCREMENT | 数据 ID |
| video_id | BIGINT FK → vbc_video.id | 所属视频 |
| timestamp_seconds | DOUBLE NOT NULL | 时间偏移 |
| grid_x | INT NOT NULL | 网格列号 |
| grid_y | INT NOT NULL | 网格行号 |
| density_value | DOUBLE NOT NULL | 密度值(0.0-1.0) |
| created_at | DATETIME(3) | 创建时间 |

---

## 六、API 设计

### 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { ... },
  "timestamp": 1717027200000
}
```

分页响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [ ... ],
    "total": 156,
    "page": 1,
    "pageSize": 10,
    "pages": 16
  }
}
```

### REST API

#### 视频管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/videos?page=&pageSize=&keyword=&status=` | 视频列表 |
| GET | `/api/v1/videos/{id}` | 视频详情 |
| POST | `/api/v1/videos/upload` | 上传视频（multipart/form-data） |
| DELETE | `/api/v1/videos/{id}` | 删除视频（级联删除帧、检测、区域、热力图） |
| GET | `/api/v1/videos/{videoId}/frames?startIndex=&endIndex=` | 获取帧列表 |
| POST | `/api/v1/videos/{videoId}/extract` | 触发异步帧提取 |

#### 设备管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/devices?page=&pageSize=&keyword=&status=` | 设备列表 |
| GET | `/api/v1/devices/{id}` | 设备详情 |
| POST | `/api/v1/devices` | 注册设备 |
| PUT | `/api/v1/devices/{id}` | 更新设备 |
| DELETE | `/api/v1/devices/{id}` | 删除设备 |
| POST | `/api/v1/devices/{id}/heartbeat` | 设备心跳 |

#### 检测结果

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/videos/{videoId}/detections?page=&pageSize=&startTime=&endTime=` | 检测记录列表 |
| GET | `/api/v1/videos/{videoId}/detections/stats` | 检测统计 |

#### 告警管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/alarms?page=&alarmType=&alarmLevel=&handled=&startDate=&endDate=` | 告警记录列表 |
| GET | `/api/v1/alarms/{id}` | 告警详情（含截图） |
| PUT | `/api/v1/alarms/{id}/handle` | 处理告警 |
| GET | `/api/v1/alarms/rules?page=&enabled=` | 规则列表 |
| POST | `/api/v1/alarms/rules` | 创建规则 |
| PUT | `/api/v1/alarms/rules/{id}` | 更新规则 |
| DELETE | `/api/v1/alarms/rules/{id}` | 删除规则 |

#### ROI 区域

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/videos/{videoId}/zones` | 区域列表 |
| POST | `/api/v1/videos/{videoId}/zones` | 创建区域 |
| PUT | `/api/v1/zones/{id}` | 更新区域 |
| DELETE | `/api/v1/zones/{id}` | 删除区域 |

#### 数据大屏

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/dashboard/overview` | 概览统计 |
| GET | `/api/v1/dashboard/person-trend?period=HOURLY` | 人数趋势 |
| GET | `/api/v1/dashboard/alarm-stats` | 告警统计 |
| GET | `/api/v1/dashboard/device-status` | 设备状态列表 |
| GET | `/api/v1/dashboard/heatmap-data/{videoId}?startTime=&endTime=` | 热力图数据 |

#### 文件服务

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/files/{type}/{filename}` | 静态文件访问（videos/frames/thumbnails/snapshots） |

---

## 七、WebSocket 设计

### 连接

```
ws://localhost:8080/ws（SockJS 降级: http://localhost:8080/ws）
```

### 服务端 → 客户端（客户端订阅）

| 频道 | 推送内容 | 消费方 |
|------|---------|--------|
| `/topic/detection/{videoId}` | 检测结果（类型/置信度/坐标/计数/时间） | VideoDetailView |
| `/topic/alarm/{videoId}` | 触发的告警（类型/级别/消息/截图 URL/时间） | AppHeader AlarmBadge + 页面通知 |
| `/topic/dashboard` | 仪表盘统计更新（在线设备/今日告警/人数/车数） | DashboardView StatCard |
| `/topic/video/{videoId}/status` | 视频状态变更（READY/PROCESSING/ERROR） | VideoDetailView |
| `/topic/device/{deviceId}/status` | 设备状态变更 | DeviceListView |

### 客户端 → 服务端（客户端发送）

| 目的地 | 发送内容 | 说明 |
|--------|---------|------|
| `/app/detection/report` | 检测结果 JSON | 浏览器 TF.js 检测结果回传，服务端持久化 |
| `/app/device/heartbeat` | 设备心跳 | 替代 REST 心跳接口 |

---

## 八、核心数据流

### 8.1 视频分析全流程

```
┌──────────┐    上传视频     ┌──────────────┐   异步帧提取    ┌──────────────┐
│  用户浏览器 │ ──────────────→ │ VideoController│ ──────────────→ │FrameExtract  │
│  上传 .mp4 │ ←── VideoVO ── │ 保存视频元数据  │                │Service (@Async)│
└──────────┘                 └──────────────┘                └──────┬───────┘
                                                                    │
                                          JavaCV FFmpegFrameGrabber
                                          .grabImage() @ 1 FPS
                                          .saveAsJPEG(framePath)
                                          vbc_frame INSERT
                                                                    │
                                          video.status → READY     │
                                          WS /topic/video/{id}/status
                                                                    │
┌──────────┐   加载帧图片      ┌──────────────┐                  ◄──┘
│  用户浏览器 │ ←────────────── │ FileController│
│            │   GET /frames   │ 提供 JPEG 文件 │
│   TF.js   │                 └──────────────┘
│  COCO-SSD │
│  模型推理  │─── Canvas 绘制检测框（本地即时显示）
│            │
│            │─── STOMP SEND /app/detection/report → 后端
│            │                                          │
│            │                                   ┌──────▼───────┐
│            │                                   │ DetectionService│
│            │                                   │ 保存检测记录    │
│            │                                   │                │
│            │                                   │ AlarmEval      │
│            │                                   │ Service        │
│            │                                   │ 规则匹配→告警   │
│            │                                   │                │
│            │    WS /topic/detection/{id}       │ Notification   │
│            │ ←─────────────────────────────────│ Service.broadcast│
│            │    WS /topic/alarm/{id}           │                │
│            │ ←─────────────────────────────────┘                │
└──────────┘
```

### 8.2 告警评估引擎

```
AlarmEvaluationService.evaluate(DetectionRecord record)

  1. 加载激活的告警规则
     WHERE enabled = 1 AND (video_id = :vid OR video_id IS NULL)

  2. FOR EACH 规则:
     ├── PEOPLE_EXCEED:    person_count > threshold → 触发
     ├── CROWD_GATHERING:  区域内 person_count > threshold → 触发
     ├── ZONE_INTRUSION:   PolygonUtil.rayCasting(bbox中心点, zone多边形) → 触发
     └── LOITERING:        连续N帧 区域内有人 → 触发

  3. 触发告警:
     ├── 生成 AlarmRecord + 截取当前帧快照
     ├── alarmRecordMapper.insert()
     ├── WS /topic/alarm/{videoId} 推送
     └── WS /topic/dashboard 更新统计
```

### 8.3 仪表盘实时更新

```
来源 1：定时任务（每 30 秒）
  DashboardStatsJob (@Scheduled 30s)
    → DashboardService.getOverview() 聚合查询
    → WS /topic/dashboard 推送全量统计

来源 2：事件驱动
  检测记录入库 → WS /topic/detection/{videoId} 推送
  告警触发     → WS /topic/alarm/{videoId} 推送 + 通知

前端 DashboardView:
  subscribe /topic/dashboard → 更新 StatCard（数字滚动动画）
  subscribe /topic/alarm/*   → 更新告警徽标 + 弹窗通知
```

---

## 九、开发计划

### Phase 1 — 项目骨架搭建

| 步骤 | 任务 | 产出 |
|------|------|------|
| 1.1 | Spring Boot 3 项目初始化（Maven） | 后端可启动，端口 8080 |
| 1.2 | Vue 3 + Vite 项目初始化 | 前端可启动，端口 5173 |
| 1.3 | CORS 配置 + Vite 代理 | 前后端联调通 |
| 1.4 | MySQL 连接 + MyBatis-Plus 配置 | 后端连接数据库 |
| 1.5 | 执行建表 SQL | 8 张表创建完成 |
| 1.6 | MainLayout + 路由 + 侧边栏 | 7 个页面可导航 |

### Phase 2 — 视频 + 设备 CRUD

| 步骤 | 任务 |
|------|------|
| 2.1 | Device 实体 + Mapper + Service + Controller |
| 2.2 | Video 实体 + Mapper + Service + 上传接口 |
| 2.3 | FileController 静态文件服务 |
| 2.4 | DeviceListView（表格 + 新增/编辑弹窗） |
| 2.5 | VideoListView（卡片网格 + 上传弹窗） |

### Phase 3 — 帧提取

| 步骤 | 任务 |
|------|------|
| 3.1 | JavaCV 依赖 + VideoUtil（提取时长/FPS/分辨率） |
| 3.2 | FrameExtractionService（异步抽帧 + 存 JPEG + 写 DB） |
| 3.3 | WebSocketConfig + NotificationService |
| 3.4 | useWebSocket.ts（连接/订阅/断开） |
| 3.5 | VideoDetailView 基础版（播放器 + 提取按钮） |

### Phase 4 — WebSocket 层完善

| 步骤 | 任务 |
|------|------|
| 4.1 | WebSocketAuthInterceptor |
| 4.2 | WS Token 签发接口 |
| 4.3 | DetectionReportHandler（接收检测报告） |
| 4.4 | useWebSocket 完善（自动重连、心跳） |

### Phase 5 — 浏览器 AI 检测（核心）

| 步骤 | 任务 |
|------|------|
| 5.1 | modelLoader.ts（加载 COCO-SSD + IndexedDB 缓存） |
| 5.2 | useTensorFlow.ts（loadModel / detect / dispose） |
| 5.3 | DetectionCanvas.vue（Canvas 叠加层，绘制检测框） |
| 5.4 | useDetection.ts（检测循环：加载帧 → 推理 → 绘制 → WS 上报） |
| 5.5 | DetectionControls.vue（启停/置信度阈值/模型选择/FPS） |
| 5.6 | 后端 DetectionService 保存检测记录 |

### Phase 6 — 告警系统

| 步骤 | 任务 |
|------|------|
| 6.1 | Zone 实体 + CRUD |
| 6.2 | AlarmRule 实体 + CRUD |
| 6.3 | AlarmRecord 实体 + 查询/处理 |
| 6.4 | PolygonUtil（射线法点面判断） |
| 6.5 | AlarmEvaluationService（规则评估引擎） |
| 6.6 | ZoneDrawer.vue（Canvas 绘制多边形区域） |
| 6.7 | AlarmConfigView + AlarmRuleForm |
| 6.8 | AlarmListView + AlarmBadge（通知 + 徽标） |

### Phase 7 — 数据大屏

| 步骤 | 任务 |
|------|------|
| 7.1 | DashboardService 聚合查询（概览/趋势/告警统计） |
| 7.2 | DashboardController |
| 7.3 | DashboardStatsJob（@Scheduled 30s） |
| 7.4 | StatCard（数字滚动动画） |
| 7.5 | PersonTrendChart + AlarmTypePieChart + DeviceStatusBarChart |
| 7.6 | DashboardView 组装 |

### Phase 8 — 热力图

| 步骤 | 任务 |
|------|------|
| 8.1 | CrowdHeatmapData 实体 + Mapper |
| 8.2 | 网格密度计算（检测框中心点离散化） |
| 8.3 | 热力图数据查询接口 |
| 8.4 | CrowdHeatmapChart 渲染 |

### Phase 9 — 打磨收尾

| 步骤 | 任务 |
|------|------|
| 9.1 | V2__seed_data.sql（Demo 种子数据） |
| 9.2 | Loading 状态（骨架屏/进度条） |
| 9.3 | Error 处理（全局异常映射 + 前端提示） |
| 9.4 | Empty 状态（空数据占位组件） |
| 9.5 | 响应式适配（侧边栏折叠/仪表盘断点） |
| 9.6 | README.md（项目介绍/技术栈/运行说明） |

---

## 十、关键技术要点

### 10.1 TF.js 内存管理

```typescript
// useDetection.ts 中每帧推理后必须释放张量
const tensor = tf.browser.fromPixels(imageElement)
const predictions = await model.detect(tensor)
tf.dispose(tensor) // 关键！否则 WebGL 内存泄漏

// 组件卸载时释放全部资源
onUnmounted(() => {
  model.dispose()
  tf.disposeVariables()
})
```

### 10.2 JavaCV 帧提取并发控制

帧提取使用 `@Async` 异步执行，线程池大小 2。同一视频只允许一个提取任务运行（检查 `status == PROCESSING`）。

### 10.3 Zone 归一化坐标

多边形顶点存储为 0-1 范围的比例值，与视频分辨率解耦。前端绘制和后端点面判断时乘以实际宽高即可。

```java
// PolygonUtil 射线法
public static boolean isPointInPolygon(double px, double py, List<Point> polygon) {
    int count = 0;
    for (int i = 0; i < polygon.size(); i++) {
        Point a = polygon.get(i);
        Point b = polygon.get((i + 1) % polygon.size());
        if ((a.y > py) != (b.y > py)
            && px < (b.x - a.x) * (py - a.y) / (b.y - a.y) + a.x) {
            count++;
        }
    }
    return count % 2 == 1;
}
```

### 10.4 WebSocket 重连策略

前端指数退避重连：1s → 2s → 4s → 8s → 最大 30s。重连成功后自动重新订阅之前活跃的频道。

---

## 十一、Maven 依赖

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.5</version>
</parent>

<properties>
    <java.version>17</java.version>
    <mybatis-plus.version>3.5.6</mybatis-plus.version>
    <javacv.version>1.5.10</javacv.version>
</properties>

<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>spring-boot-starter-web</dependency>
    <dependency>spring-boot-starter-websocket</dependency>
    <dependency>spring-boot-starter-validation</dependency>

    <!-- Database -->
    <dependency>mysql-connector-j</dependency>
    <dependency>com.baomidou:mybatis-plus-spring-boot3-starter:${mybatis-plus.version}</dependency>

    <!-- Video Processing -->
    <dependency>org.bytedeco:javacv-platform:${javacv.version}</dependency>

    <!-- Utilities -->
    <dependency>org.projectlombok:lombok</dependency>
    <dependency>cn.hutool:hutool-all:5.8.27</dependency>
</dependencies>
```

## 十二、前端依赖

```json
{
  "dependencies": {
    "vue": "^3.4.0",
    "vue-router": "^4.3.0",
    "pinia": "^2.1.0",
    "axios": "^1.7.0",
    "element-plus": "^2.7.0",
    "@element-plus/icons-vue": "^2.3.0",
    "echarts": "^5.5.0",
    "vue-echarts": "^6.7.0",
    "@stomp/stompjs": "^7.0.0",
    "sockjs-client": "^1.6.0",
    "@tensorflow/tfjs": "^4.20.0",
    "@tensorflow-models/coco-ssd": "^2.2.0",
    "dayjs": "^1.11.0"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.0",
    "vite": "^5.2.0",
    "typescript": "^5.4.0",
    "unplugin-vue-components": "^0.27.0",
    "unplugin-auto-import": "^0.18.0",
    "sass": "^1.77.0"
  }
}
```

---

## 十三、环境要求

| 工具 | 版本要求 |
|------|---------|
| JDK | 17+ |
| Maven | 3.8+ |
| Node.js | 18+ |
| npm | 9+ |
| MySQL | 8.0+ |
| FFmpeg | JavaCV 内置，无需单独安装 |

---

## 十四、快速启动

```bash
# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE vbc CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 2. 启动后端
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
# 访问 http://localhost:8080

# 3. 启动前端
cd frontend
npm install
npm run dev
# 访问 http://localhost:5173
```
