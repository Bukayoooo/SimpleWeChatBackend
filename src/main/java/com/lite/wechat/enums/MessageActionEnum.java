package com.lite.wechat.enums;

public enum MessageActionEnum{

    CONNECT(1, "客户端链接服务端消息"),
    CHAT(2, "客户端发送的聊天消息"),
    SIGNED(3, "消息签收"),
    KEEPALIVE(4, "客户端保持心跳"),
    PULL_FRIEND(5, "重新拉取好友");


    public final Integer type;
    public final String content;

    MessageActionEnum(Integer type, String content){
        this.type = type;
        this.content = content;
    }

    public Integer getType(){
        return type;
    }

}
