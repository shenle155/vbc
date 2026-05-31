package com.vbc.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VideoUtil {

    public static VideoMeta extractMeta(String videoPath) {
        log.warn("JavaCV not loaded, returning stub metadata for: {}", videoPath);
        VideoMeta meta = new VideoMeta();
        meta.durationSeconds = 60;
        meta.fps = 25.0;
        meta.width = 640;
        meta.height = 480;
        return meta;
    }

    public static String grabFrame(String videoPath, double secondOffset, String outputPath) {
        log.warn("JavaCV not loaded, frame grab is unavailable");
        return null;
    }

    public static class VideoMeta {
        public int durationSeconds;
        public double fps;
        public int width;
        public int height;
    }
}
