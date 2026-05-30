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

    /**
     * Extract video metadata: duration (seconds), fps, width, height.
     */
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
            return new VideoMeta();
        }
    }

    /**
     * Grab a specific frame at given second offset and save as JPEG.
     */
    public static String grabFrame(String videoPath, double secondOffset, String outputPath) throws IOException {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoPath)) {
            grabber.start();
            double fps = grabber.getFrameRate();
            long targetFrame = (long) (secondOffset * fps);
            grabber.setFrameNumber((int) Math.min(targetFrame, grabber.getLengthInFrames() - 1));

            Frame frame = grabber.grabImage();
            if (frame != null) {
                try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
                    BufferedImage image = converter.convert(frame);
                    if (image != null) {
                        FileUtil.ensureDirectory(new File(outputPath).getParent());
                        ImageIO.write(image, "jpg", new File(outputPath));
                        return outputPath;
                    }
                }
            }
            grabber.stop();
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
