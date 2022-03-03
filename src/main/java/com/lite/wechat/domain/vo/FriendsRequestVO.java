package com.lite.wechat.domain.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FriendsRequestVO{

    private String sendUserId;
    private String senderUsername;
    private String senderNickname;
    private String senderFaceImage;
}
