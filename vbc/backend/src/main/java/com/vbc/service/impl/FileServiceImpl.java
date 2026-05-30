package com.vbc.service.impl;

import com.vbc.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileServiceImpl implements FileService {

    private static final String BASE_PATH = "./uploads";

    @Override
    public Resource loadFile(String type, String filename) {
        try {
            Path filePath = Paths.get(BASE_PATH).resolve(type).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new IllegalArgumentException("文件不存在: " + filename);
        } catch (MalformedURLException e) {
            throw new RuntimeException("文件加载失败", e);
        }
    }
}
