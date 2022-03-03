package com.lite.wechat.domain.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MyFriendsVO{

    private String friendUserId;
    private String friendFaceImage;
    private String friendNickname;
    private String friendUsername;

}
