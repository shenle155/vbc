package com.vbc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vbc.dto.DeviceSaveDTO;
import com.vbc.vo.DeviceVO;
import com.vbc.vo.PageResult;

public interface DeviceService {
    PageResult<DeviceVO> listDevices(Integer page, Integer pageSize, String keyword, String status);
    DeviceVO getDevice(Long id);
    DeviceVO createDevice(DeviceSaveDTO dto);
    DeviceVO updateDevice(Long id, DeviceSaveDTO dto);
    void deleteDevice(Long id);
    void heartbeat(Long id, String status);
}
