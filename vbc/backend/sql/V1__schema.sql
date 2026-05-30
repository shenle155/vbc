-- V1: Initial schema for Smart Campus AI Vision Analysis Platform

CREATE TABLE IF NOT EXISTS vbc_device (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_name VARCHAR(255) NOT NULL,
    device_code VARCHAR(100) NOT NULL UNIQUE,
    ip_address VARCHAR(45),
    stream_url VARCHAR(500),
    location VARCHAR(255),
    status VARCHAR(20) DEFAULT 'OFFLINE' COMMENT 'ONLINE / OFFLINE / MAINTENANCE',
    last_heartbeat DATETIME(3),
    created_at DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    INDEX idx_device_status (status),
    UNIQUE INDEX uk_device_code (device_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Camera device table';

CREATE TABLE IF NOT EXISTS vbc_video (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    duration_seconds INT,
    width INT,
    height INT,
    fps DOUBLE,
    status VARCHAR(20) DEFAULT 'UPLOADING' COMMENT 'UPLOADING / PROCESSING / READY / ERROR',
    thumbnail_path VARCHAR(500),
    device_id BIGINT,
    created_at DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    INDEX idx_vbc_video_status (status),
    INDEX idx_vbc_video_device_id (device_id),
    FOREIGN KEY (device_id) REFERENCES vbc_device(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Uploaded video table';

CREATE TABLE IF NOT EXISTS vbc_zone (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    zone_name VARCHAR(255) NOT NULL,
    video_id BIGINT NOT NULL,
    zone_color VARCHAR(20) DEFAULT '#FF0000',
    polygon_points JSON NOT NULL COMMENT '[{"x":0.1,"y":0.2},...] normalized coordinates 0-1',
    created_at DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    INDEX idx_zone_video_id (video_id),
    FOREIGN KEY (video_id) REFERENCES vbc_video(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ROI zone table';

CREATE TABLE IF NOT EXISTS vbc_alarm_rule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_name VARCHAR(255) NOT NULL,
    rule_type VARCHAR(30) NOT NULL COMMENT 'ZONE_INTRUSION / CROWD_GATHERING / PEOPLE_EXCEED / LOITERING',
    video_id BIGINT,
    zone_id BIGINT,
    threshold_value INT,
    duration_seconds INT,
    enabled TINYINT(1) DEFAULT 1,
    alarm_level VARCHAR(20) DEFAULT 'WARNING' COMMENT 'INFO / WARNING / CRITICAL',
    notify_methods JSON COMMENT '["websocket"]',
    created_at DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    INDEX idx_alarm_rule_video_id (video_id),
    INDEX idx_alarm_rule_enabled (enabled),
    FOREIGN KEY (video_id) REFERENCES vbc_video(id) ON DELETE CASCADE,
    FOREIGN KEY (zone_id) REFERENCES vbc_zone(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Alarm rule table';

CREATE TABLE IF NOT EXISTS vbc_alarm_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_id BIGINT NOT NULL,
    video_id BIGINT NOT NULL,
    alarm_type VARCHAR(30) NOT NULL,
    alarm_level VARCHAR(20) NOT NULL,
    alarm_message TEXT,
    snapshot_path VARCHAR(500),
    frame_index INT,
    timestamp_seconds DOUBLE,
    handled TINYINT(1) DEFAULT 0,
    handled_by VARCHAR(100),
    handled_at DATETIME(3),
    created_at DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3),
    INDEX idx_alarm_record_video_id (video_id),
    INDEX idx_alarm_record_created_at (created_at),
    INDEX idx_alarm_record_handled (handled),
    FOREIGN KEY (rule_id) REFERENCES vbc_alarm_rule(id) ON DELETE CASCADE,
    FOREIGN KEY (video_id) REFERENCES vbc_video(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Alarm record table';

CREATE TABLE IF NOT EXISTS vbc_frame (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    video_id BIGINT NOT NULL,
    frame_index INT NOT NULL,
    timestamp_seconds DOUBLE NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    processed TINYINT(1) DEFAULT 0,
    created_at DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3),
    UNIQUE INDEX idx_frame_video_id_index (video_id, frame_index),
    INDEX idx_frame_processed (processed),
    FOREIGN KEY (video_id) REFERENCES vbc_video(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Extracted frame table';

CREATE TABLE IF NOT EXISTS vbc_detection_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    video_id BIGINT NOT NULL,
    frame_index INT NOT NULL,
    timestamp_seconds DOUBLE NOT NULL,
    detected_objects JSON NOT NULL COMMENT '[{"class":"person","confidence":0.94,"bbox":[x,y,w,h]},...]',
    person_count INT DEFAULT 0,
    vehicle_count INT DEFAULT 0,
    total_count INT DEFAULT 0,
    created_at DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3),
    INDEX idx_detection_video_timestamp (video_id, timestamp_seconds),
    INDEX idx_detection_created_at (created_at),
    FOREIGN KEY (video_id) REFERENCES vbc_video(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Detection record table';

CREATE TABLE IF NOT EXISTS vbc_crowd_heatmap_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    video_id BIGINT NOT NULL,
    timestamp_seconds DOUBLE NOT NULL,
    grid_x INT NOT NULL,
    grid_y INT NOT NULL,
    density_value DOUBLE NOT NULL COMMENT '0.0 (empty) to 1.0 (max crowd)',
    created_at DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3),
    INDEX idx_heatmap_video_time (video_id, timestamp_seconds),
    UNIQUE INDEX uk_heatmap_cell (video_id, timestamp_seconds, grid_x, grid_y),
    FOREIGN KEY (video_id) REFERENCES vbc_video(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Crowd heatmap data table';
