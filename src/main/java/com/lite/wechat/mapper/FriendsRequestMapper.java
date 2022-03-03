package com.lite.wechat.mapper;

import com.lite.wechat.domain.entity.FriendsRequest;
import com.lite.wechat.domain.vo.FriendsRequestVO;
import com.lite.wechat.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FriendsRequestMapper {

    void insert(FriendsRequest request);

    FriendsRequest queryFriendRequestByTwoId(@Param("myUserId") String myUserId, @Param("friendUserId") String friendUserId);

    List<FriendsRequestVO> queryFriendRequestsById(@Param("acceptUserId") String acceptUserId);

    void deleteFriendRequest(@Param("myid") String myid, @Param("senderUserId") String senderUserId);
}