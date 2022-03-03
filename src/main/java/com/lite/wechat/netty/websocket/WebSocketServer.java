package com.lite.wechat.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

/**
 * 将WebSocketServer设计成一个单例的服务器
 * SpringBoot启动时，即启动netty
 */
@Component
public class WebSocketServer{

    // 采用饿汉模式，创建单例服务器
    private static WebSocketServer webSocketServer = new WebSocketServer();

    public static WebSocketServer getInstace(){
        return webSocketServer;
    }

    private NioEventLoopGroup boss;
    private NioEventLoopGroup worker;
    private ServerBootstrap serverBootstrap;


    // 禁止new出对象
    private WebSocketServer(){
        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap()
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new SocketChannelInitializer());
    }

    // 启动服务器
    public void start(){
        try {
            ChannelFuture channelFuture = serverBootstrap.bind(8080);
            System.err.println("Netty服务器已经启动!");
            Channel channel = channelFuture.sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }

}
