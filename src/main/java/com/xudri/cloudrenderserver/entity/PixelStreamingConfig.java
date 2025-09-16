package com.xudri.cloudrenderserver.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName PixelStreamingConfig
 * @Description 像素流控制台命令和启动参数
 * @Author MaxYun
 * @Since 2025/9/15 11:43
 * @Version 1.0
 */

@Data
public class PixelStreamingConfig {

    // 必需的启动参数
    private String pixelStreamingIP;
    private Integer pixelStreamingPort;
    private String pixelStreamingURL;

    // 虚幻引擎启动参数
    private Boolean renderOffscreen = false;
    private Boolean forceRes = false;
    private Integer resX;
    private Integer resY;
    private Boolean audioMixer = false;
    private Boolean unattended = false;
    private Boolean stdOut = false;
    private Boolean fullStdOutLogOutput = false;

    // 像素流插件配置
    private Boolean pixelStreamingHudStats = false;
    private Boolean pixelStreamingDisableLatencyTester = false;
    private List<String> pixelStreamingKeyFilter = new ArrayList<>();
    private Boolean pixelStreamingUseMediaCapture = false;
    private Boolean allowPixelStreamingCommands = false;
    private Boolean pixelStreamingHideCursor = false;

    // 编码器配置
    private String pixelStreamingEncoderCodec = "H264";
    private Integer pixelStreamingEncoderTargetBitrate = -1;
    private Integer pixelStreamingEncoderMaxBitrate = 20000000;
    private Boolean pixelStreamingDebugDumpFrame = false;
    private Integer pixelStreamingEncoderMinQuality = -1;
    private Integer pixelStreamingEncoderMaxQuality = -1;
    private String pixelStreamingEncoderRateControl = "CBR";
    private Boolean pixelStreamingEnableFillerData = false;
    private String pixelStreamingEncoderMultipass = "FULL";
    private Integer pixelStreamingEncoderMaxSessions = -1;
    private String pixelStreamingH264Profile = "BASELINE";

    // WebRTC配置
    private String logCmds = "Log";
    private String pixelStreamingWebRTCDegradationPreference = "MAINTAIN_FRAMERATE";
    private Integer pixelStreamingWebRTCMaxFps = 60;
    private Integer pixelStreamingWebRTCStartBitrate = 10000000;
    private Integer pixelStreamingWebRTCMinBitrate = 100000;
    private Integer pixelStreamingWebRTCMaxBitrate = 100000000;
    private Boolean pixelStreamingWebRTCDisableReceiveAudio = false;
    private Boolean pixelStreamingWebRTCDisableTransmitAudio = false;
    private Boolean pixelStreamingWebRTCDisableAudioSync = true;
    private Integer pixelStreamingWebRTCMinPort = 49152;
    private Integer pixelStreamingWebRTCMaxPort = 65535;
    private List<String> pixelStreamingWebRTCPortAllocatorFlags = new ArrayList<>();
    private Boolean pixelStreamingWebRTCDisableFrameDropper = false;
    private Double pixelStreamingWebRTCVideoPacingMaxDelay = -1.0;
    private Double pixelStreamingWebRTCVideoPacingFactor = -1.0;
    private String pixelStreamingWebRTCFieldTrials = "";
    private String pixelStreamingDecoupleFramerate = "";

    // 构造方法
    public PixelStreamingConfig() {
    }

    // 必需参数的构造方法
    public PixelStreamingConfig(String pixelStreamingIP, Integer pixelStreamingPort) {
        this.pixelStreamingIP = pixelStreamingIP;
        this.pixelStreamingPort = pixelStreamingPort;
    }

    public PixelStreamingConfig(String pixelStreamingURL) {
        this.pixelStreamingURL = pixelStreamingURL;
    }

    // 生成命令行参数列表
    public List<String> toCommandLineArgs() {
        List<String> args = new ArrayList<>();

        // 必需参数 - 总是添加
        if (pixelStreamingURL != null && !pixelStreamingURL.isEmpty()) {
            args.add("-PixelStreamingURL=" + pixelStreamingURL);
        } else if (pixelStreamingIP != null && pixelStreamingPort != null) {
            args.add("-PixelStreamingIP=" + pixelStreamingIP);
            args.add("-PixelStreamingPort=" + pixelStreamingPort);
        }

        // 虚幻引擎参数 - 只添加与默认值不同的参数
        if (Boolean.TRUE.equals(renderOffscreen)) args.add("-RenderOffscreen");
        if (Boolean.TRUE.equals(forceRes)) args.add("-ForceRes");
        if (resX != null) args.add("-ResX=" + resX);
        if (resY != null) args.add("-ResY=" + resY);
        if (Boolean.TRUE.equals(audioMixer)) args.add("-AudioMixer");
        if (Boolean.TRUE.equals(unattended)) args.add("-Unattended");
        if (Boolean.TRUE.equals(stdOut)) args.add("-StdOut");
        if (Boolean.TRUE.equals(fullStdOutLogOutput)) args.add("-FullStdOutLogOutput");

        // 像素流插件配置 - 只添加与默认值不同的参数
        if (Boolean.TRUE.equals(pixelStreamingHudStats)) args.add("-PixelStreamingHudStats=true");
        if (Boolean.TRUE.equals(pixelStreamingDisableLatencyTester)) args.add("-PixelStreamingDisableLatencyTester=true");
        if (pixelStreamingKeyFilter != null && !pixelStreamingKeyFilter.isEmpty()) {
            args.add("-PixelStreamingKeyFilter=" + String.join(",", pixelStreamingKeyFilter));
        }
        if (Boolean.TRUE.equals(pixelStreamingUseMediaCapture)) args.add("-PixelStreamingUseMediaCapture=true");
        if (Boolean.TRUE.equals(allowPixelStreamingCommands)) args.add("-AllowPixelStreamingCommands=true");
        if (Boolean.TRUE.equals(pixelStreamingHideCursor)) args.add("-PixelStreamingHideCursor=true");

        // 编码器配置 - 只添加与默认值不同的参数
        if (!"H264".equals(pixelStreamingEncoderCodec)) args.add("-PixelStreamingEncoderCodec=" + pixelStreamingEncoderCodec);
        if (!Integer.valueOf(-1).equals(pixelStreamingEncoderTargetBitrate))
            args.add("-PixelStreamingEncoderTargetBitrate=" + pixelStreamingEncoderTargetBitrate);
        if (!Integer.valueOf(20000000).equals(pixelStreamingEncoderMaxBitrate))
            args.add("-PixelStreamingEncoderMaxBitrate=" + pixelStreamingEncoderMaxBitrate);
        if (Boolean.TRUE.equals(pixelStreamingDebugDumpFrame)) args.add("-PixelStreamingDebugDumpFrame=true");
        if (!Integer.valueOf(-1).equals(pixelStreamingEncoderMinQuality))
            args.add("-PixelStreamingEncoderMinQuality=" + pixelStreamingEncoderMinQuality);
        if (!Integer.valueOf(-1).equals(pixelStreamingEncoderMaxQuality))
            args.add("-PixelStreamingEncoderMaxQuality=" + pixelStreamingEncoderMaxQuality);
        if (!"CBR".equals(pixelStreamingEncoderRateControl)) args.add("-PixelStreamingEncoderRateControl=" + pixelStreamingEncoderRateControl);
        if (Boolean.TRUE.equals(pixelStreamingEnableFillerData)) args.add("-PixelStreamingEnableFillerData=true");
        if (!"FULL".equals(pixelStreamingEncoderMultipass)) args.add("-PixelStreamingEncoderMultipass=" + pixelStreamingEncoderMultipass);
        if (!Integer.valueOf(-1).equals(pixelStreamingEncoderMaxSessions))
            args.add("-PixelStreamingEncoderMaxSessions=" + pixelStreamingEncoderMaxSessions);
        if (!"BASELINE".equals(pixelStreamingH264Profile)) args.add("-PixelStreamingH264Profile=" + pixelStreamingH264Profile);

        // WebRTC配置 - 只添加与默认值不同的参数
        if (!"Log".equals(logCmds)) args.add("-LogCmds=\"LogPixelStreamingWebRTC " + logCmds + "\"");
        if (!"MAINTAIN_FRAMERATE".equals(pixelStreamingWebRTCDegradationPreference))
            args.add("-PixelStreamingWebRTCDegradationPreference=" + pixelStreamingWebRTCDegradationPreference);
        if (!Integer.valueOf(60).equals(pixelStreamingWebRTCMaxFps)) args.add("-PixelStreamingWebRTCMaxFps=" + pixelStreamingWebRTCMaxFps);
        if (!Integer.valueOf(10000000).equals(pixelStreamingWebRTCStartBitrate))
            args.add("-PixelStreamingWebRTCStartBitrate=" + pixelStreamingWebRTCStartBitrate);
        if (!Integer.valueOf(100000).equals(pixelStreamingWebRTCMinBitrate))
            args.add("-PixelStreamingWebRTCMinBitrate=" + pixelStreamingWebRTCMinBitrate);
        if (!Integer.valueOf(100000000).equals(pixelStreamingWebRTCMaxBitrate))
            args.add("-PixelStreamingWebRTCMaxBitrate=" + pixelStreamingWebRTCMaxBitrate);
        if (Boolean.TRUE.equals(pixelStreamingWebRTCDisableReceiveAudio)) args.add("-PixelStreamingWebRTCDisableReceiveAudio=true");
        if (Boolean.TRUE.equals(pixelStreamingWebRTCDisableTransmitAudio)) args.add("-PixelStreamingWebRTCDisableTransmitAudio=true");
        if (!Boolean.TRUE.equals(pixelStreamingWebRTCDisableAudioSync)) args.add("-PixelStreamingWebRTCDisableAudioSync=false");
        if (!Integer.valueOf(49152).equals(pixelStreamingWebRTCMinPort)) args.add("-PixelStreamingWebRTCMinPort=" + pixelStreamingWebRTCMinPort);
        if (!Integer.valueOf(65535).equals(pixelStreamingWebRTCMaxPort)) args.add("-PixelStreamingWebRTCMaxPort=" + pixelStreamingWebRTCMaxPort);
        if (pixelStreamingWebRTCPortAllocatorFlags != null && !pixelStreamingWebRTCPortAllocatorFlags.isEmpty()) {
            args.add("-PixelStreamingWebRTCPortAllocatorFlags=" + String.join(",", pixelStreamingWebRTCPortAllocatorFlags));
        }
        if (Boolean.TRUE.equals(pixelStreamingWebRTCDisableFrameDropper)) args.add("-PixelStreamingWebRTCDisableFrameDropper=true");
        if (!Double.valueOf(-1.0).equals(pixelStreamingWebRTCVideoPacingMaxDelay))
            args.add("-PixelStreamingWebRTCVideoPacingMaxDelay=" + pixelStreamingWebRTCVideoPacingMaxDelay);
        if (!Double.valueOf(-1.0).equals(pixelStreamingWebRTCVideoPacingFactor))
            args.add("-PixelStreamingWebRTCVideoPacingFactor=" + pixelStreamingWebRTCVideoPacingFactor);
        if (pixelStreamingWebRTCFieldTrials != null && !pixelStreamingWebRTCFieldTrials.isEmpty()) {
            args.add("-PixelStreamingWebRTCFieldTrials=" + pixelStreamingWebRTCFieldTrials);
        }
        if (pixelStreamingDecoupleFramerate != null && !pixelStreamingDecoupleFramerate.isEmpty()) {
            args.add("-PixelStreamingDecoupleFramerate=" + pixelStreamingDecoupleFramerate);
        }

        return args;
    }

    // 将命令行参数列表转换为字符串
    public String toCommandLineString() {
        List<String> args = toCommandLineArgs();
        return String.join(" ", args);
    }

    // 重写toString方法，返回命令行字符串
    @Override
    public String toString() {
        return toCommandLineString();
    }

    public static void main(String[] args) {
        // 创建配置对象
        PixelStreamingConfig config = new PixelStreamingConfig("127.0.0.1", 8888);
        config.setRenderOffscreen(true);
        config.setPixelStreamingWebRTCMaxFps(30);
        System.out.println(config);
    }
}