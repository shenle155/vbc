package com.vbc.service;

import org.springframework.core.io.Resource;

public interface FileService {
    Resource loadFile(String type, String filename);
}
