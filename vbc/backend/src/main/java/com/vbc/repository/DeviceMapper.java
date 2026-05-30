package com.vbc.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vbc.entity.Device;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeviceMapper extends BaseMapper<Device> {
}
