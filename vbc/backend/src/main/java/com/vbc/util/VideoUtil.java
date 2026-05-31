package com.vbc.util;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Slf4j
public class VideoUtil {

    public static VideoMeta extractMeta(String videoPath) {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoPath)) {
            grabber.start();
            VideoMeta meta = new VideoMeta();
            meta.durationSeconds = (int) (grabber.getLengthInTime() / 1_000_000.0);
            meta.fps = grabber.getFrameRate();
            meta.width = grabber.getImageWidth();
            meta.height = grabber.getImageHeight();
            grabber.stop();
            return meta;
        } catch (Exception e) {
            log.error("Failed to extract video metadata: {}", videoPath, e);
            VideoMeta meta = new VideoMeta();
            meta.durationSeconds = 60;
            meta.fps = 25.0;
            meta.width = 640;
            meta.height = 480;
            return meta;
        }
    }

    public static String grabFrame(String videoPath, double secondOffset, String outputPath) {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoPath)) {
            grabber.start();
            double fps = grabber.getFrameRate();
            if (fps <= 0) fps = 25.0;
            long targetFrame = (long) (secondOffset * fps);
            long totalFrames = grabber.getLengthInFrames();
            if (totalFrames > 0) {
                grabber.setFrameNumber((int) Math.min(targetFrame, totalFrames - 1));
            }
            Frame frame = grabber.grabImage();
            if (frame != null) {
                try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
                    BufferedImage image = converter.convert(frame);
                    if (image != null) {
                        File outputFile = new File(outputPath);
                        FileUtil.ensureDirectory(outputFile.getParent());
                        ImageIO.write(image, "jpg", outputFile);
                        grabber.stop();
                        return outputPath;
                    }
                }
            }
            grabber.stop();
        } catch (Exception e) {
            log.error("Failed to grab frame at {}s from {}: {}", secondOffset, videoPath, e.getMessage());
        }
        return null;
    }

    public static class VideoMeta {
        public int durationSeconds;
        public double fps;
        public int width;
        public int height;
    }
}
