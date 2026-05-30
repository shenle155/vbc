package com.vbc.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vbc.entity.Video;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VideoMapper extends BaseMapper<Video> {
}
