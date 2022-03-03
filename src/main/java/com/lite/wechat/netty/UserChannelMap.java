package com.lite.wechat.netty;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

public class UserChannelMap{

    public static HashMap<String, Channel> userChannelMap = new HashMap<>();

    public static void put(String senderId, Channel channel){
        userChannelMap.put(senderId, channel);
    }

    public static Channel get(String senderId){
        return userChannelMap.get(senderId);
    }

    public static void getMap(){
        for(Map.Entry<String, Channel> entry : userChannelMap.entrySet()){
            System.out.println("userId:" + entry.getKey() + ","
                                         + "channel:" + entry.getValue());
        }
    }

}
