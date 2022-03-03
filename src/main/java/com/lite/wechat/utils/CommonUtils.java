package com.lite.wechat.utils;


import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;


public class CommonUtils{

    public static String MD5(String data){

        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();

            for(byte item : array){
                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }

            return sb.toString().toUpperCase();
        }catch (Exception e){
            return null;
        }
    }

    @Test
    public void test(){
        String md = MD5("hz19981004");
        System.out.println(md);
        String s = MD5(md);
        System.out.println(s);
    }
}
