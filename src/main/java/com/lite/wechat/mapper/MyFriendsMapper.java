package com.lite.wechat.mapper;

import com.lite.wechat.domain.entity.MyFriends;
import com.lite.wechat.domain.vo.MyFriendsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MyFriendsMapper {
    MyFriends searchUserByMyIdAndFriendId(@Param("myUserId") String myUserId, @Param("myFriendUserId") String myFriendUserId);

    void insert(MyFriends myFriends);

    List<MyFriendsVO> searchMyFriendsById(@Param("myUserId") String myUserId);
}