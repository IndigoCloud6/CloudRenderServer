package com.xudri.cloudrenderserver.core.signaling;

import com.xudri.cloudrenderserver.core.signaling.SignallingChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName SignallingChannelInitializer @Description TODO @Author MaxYun @Date 2024/3/6
 * 19:43 @Version 1.0
 */
@Component
public class SignallingChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Resource
    private SignallingChannelHandler signallingChannelHandler;

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        socketChannel
                .pipeline()
                .addLast(new IdleStateHandler(0, 30, 0, TimeUnit.SECONDS))
                .addLast(new HttpServerCodec())
                .addLast(new StringEncoder())
                .addLast(new HttpObjectAggregator(512 * 1024))
                .addLast(signallingChannelHandler)
                .addLast(new ChunkedWriteHandler());
    }
}
