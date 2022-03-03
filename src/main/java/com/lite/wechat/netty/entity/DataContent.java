package com.lite.wechat.netty.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DataContent{

    private Integer action;             // 发送消息类型
    private ChatMessage chatMessage;    // 用户聊天信息
    private String extend;              // 扩展字段

}
