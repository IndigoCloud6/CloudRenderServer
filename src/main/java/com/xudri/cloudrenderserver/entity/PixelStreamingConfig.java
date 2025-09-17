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

    /**
     * 指定运行信令和Web服务器的计算机的IP地址或域名
     * 必须与-PixelStreamingPort一起使用，或者使用-PixelStreamingURL替代
     */
    private String pixelStreamingIP;

    /**
     * 指定信令和Web服务器侦听来自虚幻引擎应用程序的传入通信的端口
     * 信令和Web服务器使用默认值8888
     * 必须与-PixelStreamingIP一起使用，或者使用-PixelStreamingURL替代
     */
    private Integer pixelStreamingPort;

    /**
     * 同时替换-PixelStreamingIP和-PixelStreamingPort的替代参数
     * 必须包含WebSocket协议，例如：ws://127.0.0.1:8888 或 wss://127.0.0.1:8888
     * 仅当-PixelStreamingIP和-PixelStreamingPort不存在时才需要
     */
    private String pixelStreamingURL;

    // 虚幻引擎启动参数

    /**
     * 自动运行虚幻引擎应用程序，在本地计算机上完全没有可见的渲染
     * 该应用程序不会显示窗口，也不会全屏渲染
     * 建议始终包含此参数，除非需要在运行时在同一台计算机上本地查看虚幻引擎应用程序的渲染输出
     */
    private Boolean renderOffscreen = false;

    /**
     * 当与-ResX和-ResY结合使用时，这将强制虚幻引擎达到指定的分辨率
     * 这在通常没有显示分辨率的云部署中很有用
     */
    private Boolean forceRes = false;

    /**
     * 设置虚幻引擎应用程序启动时分辨率的宽度分量
     * 需要与-ForceRes和-ResY一起使用
     */
    private Integer resX;

    /**
     * 设置虚幻引擎应用程序启动时分辨率的高度分量
     * 需要与-ForceRes和-ResX一起使用
     */
    private Integer resY;

    /**
     * 强制虚幻引擎将软件混合用于音频
     * 如果没有音频设备，这可能是必需操作
     */
    private Boolean audioMixer = false;

    /**
     * 在遇到错误时抑制生成对话框
     * 这在屏幕外或容器中运行虚幻引擎时很有用，因为在这些场景中消息框可能会无限期挂起
     */
    private Boolean unattended = false;

    /**
     * 启用标准输出日志
     */
    private Boolean stdOut = false;

    /**
     * 与-StdOut组合会产生最大的日志输出
     * 这在SSH终端中调试或查看实时日志时非常有用
     */
    private Boolean fullStdOutLogOutput = false;

    // 像素流插件配置

    /**
     * 是否在游戏内HUD上显示像素流统计数据
     */
    private Boolean pixelStreamingHudStats = false;

    /**
     * 将能够通过像素流插件管线的延迟测试的触发功能禁用
     */
    private Boolean pixelStreamingDisableLatencyTester = false;

    /**
     * 一系列要从流客户端忽略的按键，各个按键用逗号分隔
     * 例如，"W,A,S,D"可用于过滤掉默认移动键
     */
    private List<String> pixelStreamingKeyFilter = new ArrayList<>();

    /**
     * 使用MediaIOFramework的媒体捕获，而不是像素流送内部的后备缓冲区源来捕获帧
     * 这在某些情况下可以作为首选项
     */
    private Boolean pixelStreamingUseMediaCapture = false;

    /**
     * 用户是否应该能够通过emitConsoleCommand javascript发送控制台命令
     */
    private Boolean allowPixelStreamingCommands = false;

    /**
     * 是否隐藏UE应用程序光标
     */
    private Boolean pixelStreamingHideCursor = false;

    // 编码器配置

    /**
     * 用于像素流送的指定编码器
     * 支持的编码器包括："H264"、"AV1"、"VP8"、"VP9"
     * 默认值: "H264"
     */
    private String pixelStreamingEncoderCodec = "H264";

    /**
     * 目标比特率（bps）
     * 设置此项会忽略WebRTC想要的比特率（不推荐）
     * 设置为-1时禁用
     * 默认值: -1
     */
    private Integer pixelStreamingEncoderTargetBitrate = -1;

    /**
     * 最大比特率（bps）
     * 在带有NVENC的CBR速率控制模式下不起作用
     * 默认值: 20000000
     */
    private Integer pixelStreamingEncoderMaxBitrate = 20000000;

    /**
     * 将帧从编码器转储到磁盘上的文件，以便进行调试
     */
    private Boolean pixelStreamingDebugDumpFrame = false;

    /**
     * 编码质量下限，0-100，值越高，质量越高，但比特率越高
     * 设置为-1时禁用
     * 默认值: -1
     */
    private Integer pixelStreamingEncoderMinQuality = -1;

    /**
     * 编码质量上限，0-100，值越高，质量越高，但比特率越高
     * 设置为-1时禁用
     * 默认值: -1
     */
    private Integer pixelStreamingEncoderMaxQuality = -1;

    /**
     * PixelStreaming视频编码器RateControl模式
     * 支持的模式有"ConstQP"、"VBR"、"CBR"
     * 注意：CBR是我们唯一推荐的模式
     * 默认值: "CBR"
     */
    private String pixelStreamingEncoderRateControl = "CBR";

    /**
     * 通过填充垃圾数据来维持恒定的比特率
     * 注意：CBR和MinQP = -1时不需要
     */
    private Boolean pixelStreamingEnableFillerData = false;

    /**
     * 编码器每帧要传递多少次
     * 更多细节，请参阅NVENC文档
     * 支持的模式有 "DISABLED"、"QUARTER"、"FULL"
     * 默认值: "FULL"
     */
    private String pixelStreamingEncoderMultipass = "FULL";

    /**
     * 设置像素流送并发硬件编码器会话的最大数量
     * -1意味着无数量限制
     * 默认值: -1
     */
    private Integer pixelStreamingEncoderMaxSessions = -1;

    /**
     * 编码器使用的H264配置文件
     * 支持的模式有 "AUTO" 、 "BASELINE" 、 "MAIN" 、 "HIGH" 、 "HIGH444" 、 STEREO 、 SVC_TEMPORAL_SCALABILITY 、 PROGRESSIVE_HIGH 、 CONSTRAINED_HIGH
     * 基线（Baseline）是唯一保证能受到支持的配置文件，前提是接收方的设备支持WebRTC
     * 默认值: "BASELINE"
     */
    private String pixelStreamingH264Profile = "BASELINE";

    // WebRTC配置

    /**
     * 指定WebRTC的日志级别
     * 对于调试WebRTC非常实用
     * 一些有帮助的Log级别包括：Log、Verbose、VeryVerbose
     * 默认值: "Log"
     */
    private String logCmds = "Log";

    /**
     * 降级偏好是WebRTC的策略，即在更改编码器比特率/QP不足时，破坏性地调整比特率（更改分辨率/丢帧）
     * 默认值: "MAINTAIN_FRAMERATE"
     */
    private String pixelStreamingWebRTCDegradationPreference = "MAINTAIN_FRAMERATE";

    /**
     * 最大FPS WebRTC将尝试捕获/编码/传输
     * 默认值: 60
     */
    private Integer pixelStreamingWebRTCMaxFps = 60;

    /**
     * WebRTC将尝试开始流送的起始比特率（bps）
     * 值必须介于最小和最大比特率之间
     * 默认值: 10000000
     */
    private Integer pixelStreamingWebRTCStartBitrate = 10000000;

    /**
     * 最小比特率（bps），WebRTC的请求不会低于该值
     * 注意不要将此值设置得太高，否则WebRTC会丢帧
     * 默认值: 100000
     */
    private Integer pixelStreamingWebRTCMinBitrate = 100000;

    /**
     * 最大比特率（bps），WebRTC的请求不会超出该值
     * 注意，不要将此值设置得太高，因为本地（理想）网络实际上会尝试达到此值
     * 默认值: 100000000
     */
    private Integer pixelStreamingWebRTCMaxBitrate = 100000000;

    /**
     * 禁用从浏览器接收音频到UE
     * 如果不需要音频，在某些情况下可以改善延迟
     */
    private Boolean pixelStreamingWebRTCDisableReceiveAudio = false;

    /**
     * 禁止将UE音频传输到浏览器
     * 如果不需要音频，在某些情况下可以改善延迟
     */
    private Boolean pixelStreamingWebRTCDisableTransmitAudio = false;

    /**
     * 在WebRTC中禁用音频和视频轨道的同步
     * 如果不需要同步，这会改善延迟
     * 默认值: true
     */
    private Boolean pixelStreamingWebRTCDisableAudioSync = true;

    /**
     * 设置WebRTC端口分配器的最小可用媒体端口数量
     * 默认值: 49152
     */
    private Integer pixelStreamingWebRTCMinPort = 49152;

    /**
     * 设置WebRTC端口分配器的最大可用媒体端口数量
     * 默认值: 65535
     */
    private Integer pixelStreamingWebRTCMaxPort = 65535;

    /**
     * 设置WebRTC端口分配器的标记
     * 标记用逗号分隔
     * 支持的值有：DISABLE_UDP, DISABLE_STUN, DISABLE_RELAY, DISABLE_TCP, ENABLE_IPV6, ENABLE_SHARED_SOCKET, ENABLE_STUN_RETRANSMIT_ATTRIBUTE, DISABLE_ADAPTER_ENUMERATION, DISABLE_DEFAULT_LOCAL_CANDIDATE, DISABLE_UDP_RELAY, ENABLE_IPV6_ON_WIFI, ENABLE_ANY_ADDRESS_PORTS, DISABLE_LINK_LOCAL_NETWORKS
     */
    private List<String> pixelStreamingWebRTCPortAllocatorFlags = new ArrayList<>();

    /**
     * 禁用WebRTC内部的帧丢弃机制
     * 如果在诸如LAN流送这类场景中出现帧丢失的情况，该机制很有用
     */
    private Boolean pixelStreamingWebRTCDisableFrameDropper = false;

    /**
     * 启用WebRTC视频速率控制试验，并设置最大延迟（毫秒）参数
     * 值小于零的将被丢弃
     * 默认值: -1.0
     */
    private Double pixelStreamingWebRTCVideoPacingMaxDelay = -1.0;

    /**
     * 启用WebRTC视频速率控制试验，并设置视频速率调整因子参数
     * 值越大，在较大比特率下就越宽松
     * 默认值: -1.0
     */
    private Double pixelStreamingWebRTCVideoPacingFactor = -1.0;

    /**
     * 设置WebRTC试验字符串
     * 格式："TRIAL1/VALUE1/TRIAL2/VALUE2/"
     */
    private String pixelStreamingWebRTCFieldTrials = "";

    /**
     * 启用解耦（Decoupled）模式，允许WebRTC和应用程序FPS以不同帧率流送
     * 像素流送会试图以固定帧率流送
     * 如果你的应用程序无法以目标帧率流送，它会发送重复的帧
     */
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
        if (!Integer.valueOf(-1).equals(pixelStreamingEncoderTargetBitrate)) args.add("-PixelStreamingEncoderTargetBitrate=" + pixelStreamingEncoderTargetBitrate);
        if (!Integer.valueOf(20000000).equals(pixelStreamingEncoderMaxBitrate)) args.add("-PixelStreamingEncoderMaxBitrate=" + pixelStreamingEncoderMaxBitrate);
        if (Boolean.TRUE.equals(pixelStreamingDebugDumpFrame)) args.add("-PixelStreamingDebugDumpFrame=true");
        if (!Integer.valueOf(-1).equals(pixelStreamingEncoderMinQuality)) args.add("-PixelStreamingEncoderMinQuality=" + pixelStreamingEncoderMinQuality);
        if (!Integer.valueOf(-1).equals(pixelStreamingEncoderMaxQuality)) args.add("-PixelStreamingEncoderMaxQuality=" + pixelStreamingEncoderMaxQuality);
        if (!"CBR".equals(pixelStreamingEncoderRateControl)) args.add("-PixelStreamingEncoderRateControl=" + pixelStreamingEncoderRateControl);
        if (Boolean.TRUE.equals(pixelStreamingEnableFillerData)) args.add("-PixelStreamingEnableFillerData=true");
        if (!"FULL".equals(pixelStreamingEncoderMultipass)) args.add("-PixelStreamingEncoderMultipass=" + pixelStreamingEncoderMultipass);
        if (!Integer.valueOf(-1).equals(pixelStreamingEncoderMaxSessions)) args.add("-PixelStreamingEncoderMaxSessions=" + pixelStreamingEncoderMaxSessions);
        if (!"BASELINE".equals(pixelStreamingH264Profile)) args.add("-PixelStreamingH264Profile=" + pixelStreamingH264Profile);

        // WebRTC配置 - 只添加与默认值不同的参数
        if (!"Log".equals(logCmds)) args.add("-LogCmds=\"LogPixelStreamingWebRTC " + logCmds + "\"");
        if (!"MAINTAIN_FRAMERATE".equals(pixelStreamingWebRTCDegradationPreference)) args.add("-PixelStreamingWebRTCDegradationPreference=" + pixelStreamingWebRTCDegradationPreference);
        if (!Integer.valueOf(60).equals(pixelStreamingWebRTCMaxFps)) args.add("-PixelStreamingWebRTCMaxFps=" + pixelStreamingWebRTCMaxFps);
        if (!Integer.valueOf(10000000).equals(pixelStreamingWebRTCStartBitrate)) args.add("-PixelStreamingWebRTCStartBitrate=" + pixelStreamingWebRTCStartBitrate);
        if (!Integer.valueOf(100000).equals(pixelStreamingWebRTCMinBitrate)) args.add("-PixelStreamingWebRTCMinBitrate=" + pixelStreamingWebRTCMinBitrate);
        if (!Integer.valueOf(100000000).equals(pixelStreamingWebRTCMaxBitrate)) args.add("-PixelStreamingWebRTCMaxBitrate=" + pixelStreamingWebRTCMaxBitrate);
        if (Boolean.TRUE.equals(pixelStreamingWebRTCDisableReceiveAudio)) args.add("-PixelStreamingWebRTCDisableReceiveAudio=true");
        if (Boolean.TRUE.equals(pixelStreamingWebRTCDisableTransmitAudio)) args.add("-PixelStreamingWebRTCDisableTransmitAudio=true");
        if (!Boolean.TRUE.equals(pixelStreamingWebRTCDisableAudioSync)) args.add("-PixelStreamingWebRTCDisableAudioSync=false");
        if (!Integer.valueOf(49152).equals(pixelStreamingWebRTCMinPort)) args.add("-PixelStreamingWebRTCMinPort=" + pixelStreamingWebRTCMinPort);
        if (!Integer.valueOf(65535).equals(pixelStreamingWebRTCMaxPort)) args.add("-PixelStreamingWebRTCMaxPort=" + pixelStreamingWebRTCMaxPort);
        if (pixelStreamingWebRTCPortAllocatorFlags != null && !pixelStreamingWebRTCPortAllocatorFlags.isEmpty()) {
            args.add("-PixelStreamingWebRTCPortAllocatorFlags=" + String.join(",", pixelStreamingWebRTCPortAllocatorFlags));
        }
        if (Boolean.TRUE.equals(pixelStreamingWebRTCDisableFrameDropper)) args.add("-PixelStreamingWebRTCDisableFrameDropper=true");
        if (!Double.valueOf(-1.0).equals(pixelStreamingWebRTCVideoPacingMaxDelay)) args.add("-PixelStreamingWebRTCVideoPacingMaxDelay=" + pixelStreamingWebRTCVideoPacingMaxDelay);
        if (!Double.valueOf(-1.0).equals(pixelStreamingWebRTCVideoPacingFactor)) args.add("-PixelStreamingWebRTCVideoPacingFactor=" + pixelStreamingWebRTCVideoPacingFactor);
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
}