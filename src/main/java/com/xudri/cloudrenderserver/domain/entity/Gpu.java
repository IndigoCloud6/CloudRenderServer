package com.xudri.cloudrenderserver.domain.entity;

import lombok.Data;

@Data
public class Gpu {
    /**
     * GPU设备ID（通常对应物理插槽位置）
     */
    private int deviceId;

    /**
     * GPU设备的全局唯一标识符
     */
    private String uuid;

    /**
     * GPU利用率（百分比，0-100范围）
     */
    private float gpuUtil;

    /**
     * 显存总容量（单位：MB/GB，具体取决于数据来源）
     */
    private float memTotal;

    /**
     * 已使用显存容量（单位：MB/GB，具体取决于数据来源）
     */
    private float memUsed;

    /**
     * 剩余可用显存容量（单位：MB/GB，具体取决于数据来源）
     */
    private float memFree;

    /**
     * GPU驱动版本信息
     */
    private String driver;

    /**
     * GPU产品名称（例如：NVIDIA GeForce RTX 3080）
     */
    private String name;

    /**
     * GPU硬件序列号
     */
    private String serial;

    /**
     * 显示模式状态
     */
    private String displayMode;

    /**
     * 显示活动状态
     */
    private String displayActive;

    /**
     * GPU温度（摄氏度）
     */
    private float tempGpu;

    public Gpu(int deviceId, String uuid, float gpuUtil, float memTotal, float memUsed, float memFree, String driver, String name, String serial,
               String displayMode, String displayActive, float tempGpu) {
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