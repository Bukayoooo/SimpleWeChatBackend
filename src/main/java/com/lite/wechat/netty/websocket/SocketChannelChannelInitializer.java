package com.lite.wechat.netty.websocket;

import com.lite.wechat.netty.handler.Chathandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

class SocketChannelInitializer extends ChannelInitializer<SocketChannel>{
    @Override
    protected void initChannel(SocketChannel ch) throws Exception{
        // 添加Http编解码器
        ch.pipeline().addLast(new HttpServerCodec());
        // 添加大数据流的支持
        ch.pipeline().addLast(new ChunkedWriteHandler());
        // 添加HttpMessage聚合器，聚合Response，Request等对象
        ch.pipeline().addLast(new HttpObjectAggregator(1024 * 64));

        // 添加websocket处理协议
        ch.pipeline().addLast(new WebSocketServerProtocolHandler("/ws"));

        // 添加自定义处理器
        ch.pipeline().addLast(new Chathandler());

    }
}
