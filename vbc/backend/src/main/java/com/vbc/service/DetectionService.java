package com.vbc.service;

import com.vbc.dto.DetectionReportDTO;
import com.vbc.vo.DetectionVO;
import com.vbc.vo.PageResult;

public interface DetectionService {
    void saveDetection(DetectionReportDTO report);
    PageResult<DetectionVO> listDetections(Long videoId, Integer page, Integer pageSize,
                                           String startTime, String endTime);
}
