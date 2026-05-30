package com.vbc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vbc.dto.DeviceSaveDTO;
import com.vbc.entity.Device;
import com.vbc.repository.DeviceMapper;
import com.vbc.service.DeviceService;
import com.vbc.vo.DeviceVO;
import com.vbc.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final DeviceMapper deviceMapper;

    @Override
    public PageResult<DeviceVO> listDevices(Integer page, Integer pageSize, String keyword, String status) {
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Device::getDeviceName, keyword)
                    .or().like(Device::getDeviceCode, keyword)
                    .or().like(Device::getLocation, keyword));
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Device::getStatus, status);
        }
        wrapper.orderByDesc(Device::getCreatedAt);

        Page<Device> result = deviceMapper.selectPage(new Page<>(page, pageSize), wrapper);

        List<DeviceVO> records = result.getRecords().stream().map(d -> {
            DeviceVO vo = new DeviceVO();
            BeanUtils.copyProperties(d, vo);
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(records, result.getTotal(), result.getCurrent(), result.getSize(), result.getPages());
    }

    @Override
    public DeviceVO getDevice(Long id) {
        Device device = deviceMapper.selectById(id);
        if (device == null) {
            throw new IllegalArgumentException("设备不存在");
        }
        DeviceVO vo = new DeviceVO();
        BeanUtils.copyProperties(device, vo);
        return vo;
    }

    @Override
    public DeviceVO createDevice(DeviceSaveDTO dto) {
        // Check unique code
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Device::getDeviceCode, dto.getDeviceCode());
        if (deviceMapper.selectCount(wrapper) > 0) {
            throw new IllegalArgumentException("设备编码已存在");
        }

        Device device = new Device();
        BeanUtils.copyProperties(dto, device);
        device.setStatus("OFFLINE");
        deviceMapper.insert(device);

        DeviceVO vo = new DeviceVO();
        BeanUtils.copyProperties(device, vo);
        return vo;
    }

    @Override
    public DeviceVO updateDevice(Long id, DeviceSaveDTO dto) {
        Device device = deviceMapper.selectById(id);
        if (device == null) {
            throw new IllegalArgumentException("设备不存在");
        }
        // Check unique code if changing
        if (!device.getDeviceCode().equals(dto.getDeviceCode())) {
            LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Device::getDeviceCode, dto.getDeviceCode());
            if (deviceMapper.selectCount(wrapper) > 0) {
                throw new IllegalArgumentException("设备编码已存在");
            }
        }
        BeanUtils.copyProperties(dto, device);
        deviceMapper.updateById(device);

        DeviceVO vo = new DeviceVO();
        BeanUtils.copyProperties(device, vo);
        return vo;
    }

    @Override
    public void deleteDevice(Long id) {
        deviceMapper.deleteById(id);
    }

    @Override
    public void heartbeat(Long id, String status) {
        Device device = deviceMapper.selectById(id);
        if (device == null) {
            throw new IllegalArgumentException("设备不存在");
        }
        device.setStatus(status != null ? status : "ONLINE");
        device.setLastHeartbeat(LocalDateTime.now());
        deviceMapper.updateById(device);
    }
}
