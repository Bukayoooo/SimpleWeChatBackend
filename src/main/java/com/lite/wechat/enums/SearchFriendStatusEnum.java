package com.lite.wechat.enums;


public enum SearchFriendStatusEnum{

    SUCCESS(0, "OK"),
    USER_NOT_EXIST(1, "用户不存在"),
    NOT_YOURSELF(2, "不能添加自己为好友"),
    ALREADY_FRIENDS(3, "该用户已经是您的好友");

    public final Integer status;
    public final String msg;

    SearchFriendStatusEnum(Integer status, String msg){
        this.status = status;
        this.msg = msg;
    }

    public Integer getStatus(){
        return status;
    }

    /**
     * 根据状态获得返回信息
     */
    public static String getMsgByStatus(Integer status){

        for(SearchFriendStatusEnum enums : SearchFriendStatusEnum.values()){
            if(enums.getStatus() == status){
                return enums.msg;
            }
        }
        return null;
    }

}
