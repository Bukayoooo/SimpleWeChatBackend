package com.lite.wechat;

import com.lite.wechat.netty.websocket.WebSocketServer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class NettyBootloader implements ApplicationListener<ContextRefreshedEvent>{
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event){
        // 监听springboot启动事件, springboot容器加载完毕，将触发该事件
        if(event.getApplicationContext().getParent() == null){
            WebSocketServer.getInstace().start();
        }
    }
}
