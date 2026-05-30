package com.vbc.service;

import com.vbc.dto.FrameExtractDTO;

public interface FrameExtractionService {
    void extractFrames(Long videoId, FrameExtractDTO dto);
}
