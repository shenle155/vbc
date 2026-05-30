-- V2: Demo seed data

-- Insert demo devices
INSERT INTO vbc_device (device_name, device_code, ip_address, stream_url, location, status, last_heartbeat) VALUES
('校门摄像头 A1', 'CAM-GATE-001', '192.168.1.101', 'rtsp://192.168.1.101:554/stream', '学校正门', 'ONLINE', NOW()),
('操场摄像头 B1', 'CAM-PLAY-001', '192.168.1.102', 'rtsp://192.168.1.102:554/stream', '操场东侧', 'ONLINE', NOW()),
('教学楼摄像头 C1', 'CAM-TEACH-001', '192.168.1.103', 'rtsp://192.168.1.103:554/stream', '教学楼一楼大厅', 'ONLINE', NOW()),
('停车场摄像头 D1', 'CAM-PARK-001', '192.168.1.104', 'rtsp://192.168.1.104:554/stream', '地下停车场入口', 'OFFLINE', NOW()),
('食堂摄像头 E1', 'CAM-CANT-001', '192.168.1.105', 'rtsp://192.168.1.105:554/stream', '食堂正门', 'MAINTENANCE', NOW()),
('图书馆摄像头 F1', 'CAM-LIB-001', '192.168.1.106', 'rtsp://192.168.1.106:554/stream', '图书馆二楼', 'ONLINE', NOW()),
('宿舍楼摄像头 G1', 'CAM-DORM-001', '192.168.1.107', 'rtsp://192.168.1.107:554/stream', '宿舍楼A栋入口', 'ONLINE', NOW()),
('实验楼摄像头 H1', 'CAM-LAB-001', '192.168.1.108', 'rtsp://192.168.1.108:554/stream', '实验楼一楼', 'ONLINE', NOW()),
('体育馆摄像头 I1', 'CAM-GYM-001', '192.168.1.109', 'rtsp://192.168.1.109:554/stream', '体育馆入口', 'OFFLINE', NOW()),
('行政楼摄像头 J1', 'CAM-ADMIN-001', '192.168.1.110', 'rtsp://192.168.1.110:554/stream', '行政楼大厅', 'ONLINE', NOW()),
('道路摄像头 K1', 'CAM-ROAD-001', '192.168.1.111', 'rtsp://192.168.1.111:554/stream', '主干道十字路口', 'ONLINE', NOW()),
('围墙摄像头 L1', 'CAM-WALL-001', '192.168.1.112', 'rtsp://192.168.1.112:554/stream', '西侧围墙', 'ONLINE', NOW());
