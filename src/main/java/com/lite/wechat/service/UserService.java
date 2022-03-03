package com.lite.wechat.service;


import com.lite.wechat.domain.entity.FriendsRequest;
import com.lite.wechat.domain.entity.User;
import com.lite.wechat.domain.vo.FriendsRequestVO;
import com.lite.wechat.domain.vo.MyFriendsVO;
import com.lite.wechat.netty.entity.ChatMessage;

import java.util.List;

public interface UserService {

    boolean queryUserNameIsExist(String username);

    User queryUserForLogin(User user);

    User saveUser(User user);

    User setNickname(String id, String nickname);

    User queryUserById(String id);

    User updateUserById(User user);

    Integer getStatusByIdAndFriendname(String myid, String friendUsername);

    User queryFriendByName(String friendUsername);

    void addFriendRequest(String myUserId, String friendUserId);

    List<FriendsRequestVO> queryFriendRequestsById(String myUserId);

    void removeFriendRequest(String myid, String senderUserId);

    void addMyFriends(String myid, String senderUserId);

    List<MyFriendsVO> queryMyFriendsById(String myUserId);

    String saveMessage(ChatMessage chatMessage);

    void updateMessageIdSigned(List<String> idList);
}
