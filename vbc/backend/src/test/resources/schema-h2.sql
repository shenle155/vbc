CREATE TABLE IF NOT EXISTS vbc_device (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_name VARCHAR(255) NOT NULL,
    device_code VARCHAR(100) NOT NULL UNIQUE,
    ip_address VARCHAR(45),
    stream_url VARCHAR(500),
    location VARCHAR(255),
    status VARCHAR(20) DEFAULT 'OFFLINE',
    last_heartbeat TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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
    status VARCHAR(20) DEFAULT 'UPLOADING',
    thumbnail_path VARCHAR(500),
    device_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (device_id) REFERENCES vbc_device(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS vbc_zone (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    zone_name VARCHAR(255) NOT NULL,
    video_id BIGINT NOT NULL,
    zone_color VARCHAR(20) DEFAULT '#FF0000',
    polygon_points CLOB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (video_id) REFERENCES vbc_video(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS vbc_alarm_rule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_name VARCHAR(255) NOT NULL,
    rule_type VARCHAR(30) NOT NULL,
    video_id BIGINT,
    zone_id BIGINT,
    threshold_value INT,
    duration_seconds INT,
    enabled TINYINT DEFAULT 1,
    alarm_level VARCHAR(20) DEFAULT 'WARNING',
    notify_methods CLOB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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
    handled TINYINT DEFAULT 0,
    handled_by VARCHAR(100),
    handled_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS vbc_frame (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    video_id BIGINT NOT NULL,
    frame_index INT NOT NULL,
    timestamp_seconds DOUBLE NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    processed TINYINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS vbc_detection_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    video_id BIGINT NOT NULL,
    frame_index INT NOT NULL,
    timestamp_seconds DOUBLE NOT NULL,
    detected_objects CLOB NOT NULL,
    person_count INT DEFAULT 0,
    vehicle_count INT DEFAULT 0,
    total_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS vbc_crowd_heatmap_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    video_id BIGINT NOT NULL,
    timestamp_seconds DOUBLE NOT NULL,
    grid_x INT NOT NULL,
    grid_y INT NOT NULL,
    density_value DOUBLE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Seed data
INSERT INTO vbc_device (device_name, device_code, ip_address, location, status) VALUES
('Gate Camera', 'CAM-001', '192.168.1.1', 'Main Gate', 'ONLINE'),
('Parking Camera', 'CAM-002', '192.168.1.2', 'Parking Lot', 'OFFLINE');

INSERT INTO vbc_video (title, file_name, file_path, status) VALUES
('Test Video 1', 'test1.mp4', '/uploads/videos/test1.mp4', 'READY');
