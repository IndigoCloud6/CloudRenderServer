package com.xudri.cloudrenderserver.entity;

import lombok.Data;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * @ClassName HostInfo
 * @Description 主机信息实体类
 * @Author MaxYun
 * @Date 2023/3/30 14:44
 * @Version 1.0
 */
@Data
public class HostInfo {

    /**
     * 主机名
     */
    private String hostName;

    /**
     * 操作系统名称
     */
    private String osName;

    /**
     * IPv4地址集合
     */
    private LinkedHashSet<String> ipv4s;

    /**
     * CPU信息
     */
    private String cpuInfo;

    /**
     * 总内存大小（GB）
     */
    private double totalMemorySize;

    /**
     * 空闲内存大小（GB）
     */
    private double freeMemorySize;

    /**
     * 已使用内存大小（GB）
     */
    private double usedMemorySize;

    /**
     * GPU列表
     */
    private List<Gpu> gpuList;

    /**
     * CPU使用率（百分比）
     */
    private double cpuUsage;

    @Override
    public String toString() {
        return "HostInfo{" +
                "hostName='" + hostName + '\'' +
                ", osName='" + osName + '\'' +
                ", ipv4s=" + ipv4s +
                ", cpuInfo='" + cpuInfo + '\'' +
                ", totalMemorySize=" + totalMemorySize +
                ", freeMemorySize=" + freeMemorySize +
                ", usedMemorySize=" + usedMemorySize +
                ", gpuList=" + gpuList +
                ", cpuUsage=" + cpuUsage +
                '}';
    }
}