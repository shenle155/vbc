package com.vbc.service;

import com.vbc.dto.ZoneSaveDTO;
import com.vbc.vo.ZoneVO;
import java.util.List;

public interface ZoneService {
    List<ZoneVO> listZones(Long videoId);
    ZoneVO createZone(Long videoId, ZoneSaveDTO dto);
    ZoneVO updateZone(Long id, ZoneSaveDTO dto);
    void deleteZone(Long id);
}
