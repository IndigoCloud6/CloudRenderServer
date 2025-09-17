package com.xudri.cloudrenderserver.domain.entity;

public class Gpu {
    private int deviceId;
    private String uuid;
    private float gpuUtil;
    private float memTotal;
    private float memUsed;
    private float memFree;
    private String driver;
    private String name;
    private String serial;
    private String displayMode;
    private String displayActive;
    private float tempGpu;

    public Gpu(int deviceId, String uuid, float gpuUtil, float memTotal, float memUsed, float memFree, String driver, String name, String serial, String displayMode, String displayActive, float tempGpu) {
        this.deviceId = deviceId;
        this.uuid = uuid;
        this.gpuUtil = gpuUtil;
        this.memTotal = memTotal;
        this.memUsed = memUsed;
        this.memFree = memFree;
        this.driver = driver;
        this.name = name;
        this.serial = serial;
        this.displayMode = displayMode;
        this.displayActive = displayActive;
        this.tempGpu = tempGpu;
    }

    // Getters
    public int getDeviceId() {
        return deviceId;
    }

    public String getUuid() {
        return uuid;
    }

    public float getGpuUtil() {
        return gpuUtil;
    }

    public float getMemTotal() {
        return memTotal;
    }

    public float getMemUsed() {
        return memUsed;
    }

    public float getMemFree() {
        return memFree;
    }

    public String getDriver() {
        return driver;
    }

    public String getName() {
        return name;
    }

    public String getSerial() {
        return serial;
    }

    public String getDisplayMode() {
        return displayMode;
    }

    public String getDisplayActive() {
        return displayActive;
    }

    public float getTempGpu() {
        return tempGpu;
    }

    // toString Method
    @Override
    public String toString() {
        return "GPU{" +
                "deviceId=" + deviceId +
                ", uuid='" + uuid + '\'' +
                ", gpuUtil=" + gpuUtil +
                ", memTotal=" + memTotal +
                ", memUsed=" + memUsed +
                ", memFree=" + memFree +
                ", driver='" + driver + '\'' +
                ", name='" + name + '\'' +
                ", serial='" + serial + '\'' +
                ", displayMode='" + displayMode + '\'' +
                ", displayActive='" + displayActive + '\'' +
                ", tempGpu=" + tempGpu +
                '}';
    }
}