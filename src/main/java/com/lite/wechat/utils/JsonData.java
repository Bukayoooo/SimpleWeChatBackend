package com.lite.wechat.utils;

import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class JsonData {
    // 状态码
    private int code;
    // 接口返回数据
    private Object data;
    // 接口返回信息
    private String msg;

    public JsonData(){}

    public JsonData(int code, Object data){
        this.code = code;
        this.data = data;
    }

    public JsonData(int code, Object data, String msg){
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public static JsonData buildSuccess(Object data){
        return new JsonData(200, data, "success");
    }

    public static JsonData buildSuccess(Object data, String msg){
        return new JsonData(200, data, msg);
    }

    public static JsonData buildFailure(String msg) {
        return new JsonData(500, null, msg);
    }

    public static JsonData buildFailure(Object data, String msg) {
        return new JsonData(500, data, msg);
    }

    public static JsonData buildSuccess(int code, Object data, String msg) {
        return new JsonData(code, data, msg);
    }

}
