package com.xudri.cloudrenderserver.infrastructure.network;

import com.xudri.cloudrenderserver.core.signaling.SignallingChannelInitializer;
import com.xudri.cloudrenderserver.common.util.LoggerUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @ClassName SignallingServer
 * @Description 信令服务
 * @Author MaxYun
 * @Since 2025/9/16 11:09
 * @Version 1.0
 */

@Component
@Log4j2
public class SignallingServer {

    @Resource
    private SignallingChannelInitializer signallingChannelInitializer;

    @Setter
    @Getter
    private int servicePort = 9999;

    private Channel channel;

    @Getter
    private volatile boolean isRunning = false;

    @Async
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(512 * 1024, 1024 * 1024))
                    .childHandler(signallingChannelInitializer);

            channel = bootstrap.bind(servicePort).sync().channel();
            LoggerUtil.logServiceStartup("信令服务器", "启动成功", "端口：" + servicePort);
            log.info("信令服务器已在端口 {} 上启动", servicePort);
            isRunning = true;
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LoggerUtil.logError(log, "信令服务器启动", "信令服务器启动过程被中断", e);
        } catch (Exception e) {
            LoggerUtil.logError(log, "信令服务器启动", "启动信令服务器时发生异常", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            isRunning = false;
            LoggerUtil.logServiceStartup("信令服务器", "已关闭", "正常关闭");
            log.info("信令服务器已关闭");
        }
    }

    public boolean stop() {
        if (channel != null) {
            channel.close();
            channel = null;
            isRunning = false;
            LoggerUtil.logServiceStartup("信令服务器", "停止成功", "");
            log.info("信令服务器已停止");
            return true;
        }
        LoggerUtil.logWarning(log, "信令服务器", "服务器停止失败，服务器未在运行");
        log.warn("信令服务器停止失败，服务器未在运行");
        return false;
    }
}
