package com.vbc.vo;

import lombok.Data;
import java.util.List;

@Data
public class AlarmStatsVO {
    private List<TypeCount> byType;
    private List<LevelCount> byLevel;
    private List<TrendItem> trend;

    @Data
    public static class TypeCount {
        private String type;
        private long count;
    }

    @Data
    public static class LevelCount {
        private String level;
        private long count;
    }

    @Data
    public static class TrendItem {
        private String date;
        private long count;
    }
}
