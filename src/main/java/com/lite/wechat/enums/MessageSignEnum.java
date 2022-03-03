package com.lite.wechat.enums;

public enum MessageSignEnum{

    unsign(0, "未签收"),
    signed(1, "已签收");


    public final Integer type;
    public final String content;

    MessageSignEnum(Integer type, String content){
        this.type = type;
        this.content = content;
    }

    public Integer getType(){
        return type;
    }
}
