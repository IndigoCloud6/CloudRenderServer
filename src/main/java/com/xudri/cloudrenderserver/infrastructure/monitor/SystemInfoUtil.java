package com.xudri.cloudrenderserver.infrastructure.monitor;

import cn.hutool.core.util.NumberUtil;
import com.xudri.cloudrenderserver.domain.entity.Gpu;
import com.xudri.cloudrenderserver.domain.entity.HostInfo;
import lombok.extern.log4j.Log4j2;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.OperatingSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Log4j2
public class SystemInfoUtil {
    private static final SystemInfo systemInfo = new SystemInfo();
    private static final HardwareAbstractionLayer hardware = systemInfo.getHardware();
    private static final OperatingSystem operatingSystem = systemInfo.getOperatingSystem();

    public static HostInfo getSystemInfo() {
        double gb = 1024 * 1024 * 1024 * 1.0;
        HostInfo hostInfo = new HostInfo();

        // 获取主机名和操作系统信息
        hostInfo.setHostName(operatingSystem.getNetworkParams().getHostName());
        hostInfo.setOsName(operatingSystem.toString());

        // 获取IPv4地址
        LinkedHashSet<String> ipv4s = new LinkedHashSet<>();
        for (NetworkIF net : hardware.getNetworkIFs()) {
            for (String ip : net.getIPv4addr()) {
                if (!ip.startsWith("127.") && !ip.equals("0.0.0.0")) {
                    ipv4s.add(ip);
                }
            }
        }
        hostInfo.setIpv4s(ipv4s);

        // 获取CPU信息
        CentralProcessor processor = hardware.getProcessor();
        String cpuInfoStr = String.format("%s (%d cores, %d threads)",
                processor.getProcessorIdentifier().getName(),
                processor.getPhysicalProcessorCount(),
                processor.getLogicalProcessorCount());
        hostInfo.setCpuInfo(cpuInfoStr);

        // 获取CPU使用率
        double cpuUsage = processor.getSystemCpuLoad(1000) * 100;
        hostInfo.setCpuUsage(NumberUtil.round(cpuUsage, 2).doubleValue());

        // 获取内存信息
        GlobalMemory memory = hardware.getMemory();
        double totalMemorySize = memory.getTotal() / gb;
        double availableMemorySize = memory.getAvailable() / gb;
        double usedMemorySize = (memory.getTotal() - memory.getAvailable()) / gb;

        hostInfo.setTotalMemorySize(NumberUtil.round(totalMemorySize, 2).doubleValue());
        hostInfo.setFreeMemorySize(NumberUtil.round(availableMemorySize, 2).doubleValue());
        hostInfo.setUsedMemorySize(NumberUtil.round(usedMemorySize, 2).doubleValue());

        // 获取GPU信息
        List<Gpu> gpuList = getGPUs();
        hostInfo.setGpuList(gpuList);

        return hostInfo;
    }

    public static List<Gpu> getGPUs() {
        List<Gpu> gpus = new ArrayList<>();

        // 方法2: 如果OSHI无法获取GPU信息，则回退到nvidia-smi命令
        String nvidiaSmi = "nvidia-smi";
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    nvidiaSmi,
                    "--query-gpu=index,uuid,utilization.gpu,memory.total,memory.used,memory.free,driver_version,name,gpu_serial,display_active,display_mode,temperature.gpu",
                    "--format=csv,noheader,nounits");
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] vals = line.split(", ");
                if (vals.length == 12) {
                    int deviceIds = Integer.parseInt(vals[0]);
                    String uuid = vals[1];
                    float gpuUtil = safeFloatCast(vals[2]) / 100;
                    float memTotal = NumberUtil.round(safeFloatCast(vals[3]) / 1024, 2).floatValue();
                    float memUsed = NumberUtil.round(safeFloatCast(vals[4]) / 1024, 2).floatValue();
                    float memFree = NumberUtil.round(safeFloatCast(vals[5]) / 1024, 2).floatValue();
                    String driver = vals[6];
                    String gpu_name = vals[7];
                    String serial = vals[8];
                    String display_active = vals[9];
                    String display_mode = vals[10];
                    float temp_gpu = safeFloatCast(vals[11]);

                    Gpu gpu = new Gpu(
                            deviceIds,
                            uuid,
                            gpuUtil,
                            memTotal,
                            memUsed,
                            memFree,
                            driver,
                            gpu_name,
                            serial,
                            display_mode,
                            display_active,
                            temp_gpu);

                    gpus.add(gpu);
                }
            }
            reader.close();
        } catch (IOException e) {
            log.error("获取GPU信息失败", e);
        }

        return gpus;
    }

    private static float safeFloatCast(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static int findFreePortInRange(int startPort, int endPort) {
        for (int port = startPort; port <= endPort; port++) {
            if (isPortAvailable(port)) {
                return port;
            }
        }
        return -1;
    }

    public static boolean isPortAvailable(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println(getSystemInfo());
    }
}