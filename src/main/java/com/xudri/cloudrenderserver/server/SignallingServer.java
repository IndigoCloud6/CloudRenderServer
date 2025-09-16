package com.xudri.cloudrenderserver.server;

import com.xudri.cloudrenderserver.initializer.SignallingChannelInitializer;
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
            log.info("Signalling server started on port {}", servicePort);
            isRunning = true;
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Signalling server was interrupted.", e);
        } catch (Exception e) {
            log.error("Exception occurred while starting the signalling server.", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            isRunning = false;
            log.info("Signalling server has been shut down.");
        }
    }

    public boolean stop() {
        if (channel != null) {
            channel.close();
            channel = null;
            isRunning = false;
            log.info("Signalling server stopped.");
            return true;
        }
        log.warn("Signalling server stop attempt failed; server was not running.");
        return false;
    }
}
