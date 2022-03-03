package com.lite.wechat.domain.bo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserBO{

    private String id;

    private String username;

    private String faceImage;

    private String faceImageBig;

    private String nickname;

    private String qrcode;

}
