package com.lite.wechat.netty.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ChatMessage{

    private String senderId;
    private String recieverId;
    private String message;
    private String messageId;

}
