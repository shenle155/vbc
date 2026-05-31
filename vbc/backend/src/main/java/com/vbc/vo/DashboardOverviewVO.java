package com.vbc.vo;

import lombok.Data;

@Data
public class DashboardOverviewVO {
    private int onlineDevices;
    private int totalDevices;
    private int todayAlarms;
    private int unhandledAlarms;
    private int todayPersonCount;
    private int todayVehicleCount;
    private int activeVideos;
}
