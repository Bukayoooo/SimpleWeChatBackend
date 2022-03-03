package com.lite.wechat.service.impl;

import com.lite.wechat.domain.entity.ChatMsg;
import com.lite.wechat.domain.entity.FriendsRequest;
import com.lite.wechat.domain.entity.MyFriends;
import com.lite.wechat.domain.entity.User;
import com.lite.wechat.domain.vo.FriendsRequestVO;
import com.lite.wechat.domain.vo.MyFriendsVO;
import com.lite.wechat.enums.MessageActionEnum;
import com.lite.wechat.enums.MessageSignEnum;
import com.lite.wechat.enums.SearchFriendStatusEnum;
import com.lite.wechat.mapper.ChatMsgMapper;
import com.lite.wechat.mapper.FriendsRequestMapper;
import com.lite.wechat.mapper.MyFriendsMapper;
import com.lite.wechat.mapper.UserMapper;
import com.lite.wechat.netty.UserChannelMap;
import com.lite.wechat.netty.entity.ChatMessage;
import com.lite.wechat.netty.entity.DataContent;
import com.lite.wechat.service.UserService;
import com.lite.wechat.utils.*;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MyFriendsMapper myFriendsMapper;

    @Autowired
    private FriendsRequestMapper friendsRequestMapper;

    @Autowired
    private ChatMsgMapper chatMsgMapper;

    @Autowired
    private FastDFSClient fastDFSClient;


    /**
     * 根据用户名查询用户，判断用户是否存在
     */
    @Override
    public boolean queryUserNameIsExist(String username){
        User userByUsername = userMapper.findUserByUsername(username);
        return userByUsername != null ? true : false;
    }

    /**
     * 根据用户名和密码查询用户，用于用户登录
     */
    @Override
    public User queryUserForLogin(User user){
        User userResult = userMapper.findUserByUsernameAndPassWord(user);
        return userResult;
    }

    /**
     * 根据用户id查询用户
     */
    @Override
    public User queryUserById(String id){
        User userResult = userMapper.findUserById(id);
        return userResult;
    }

    /**
     * 根据用户id增加用户数据，用于用户注册
     */
    @Override
    public User updateUserById(User user){
        userMapper.updateUserById(user);
        return queryUserById(user.getId());
    }

    /**
     * 根据用户id和好友用户名来判断当前用户和好友的关系
     */
    @Override
    public Integer getStatusByIdAndFriendname(String myid, String friendUsername){
        User friend = queryFriendByName(friendUsername);
        // 1. 用户不存在
        if(friend == null){
            return SearchFriendStatusEnum.USER_NOT_EXIST.getStatus();
        }
        // 2. 查询的用户是自己
        if(friend.getId().equals(myid)){
            return SearchFriendStatusEnum.NOT_YOURSELF.getStatus();
        }
        // 3. 查询的用户已经是自己的好友
        MyFriends myFriends = searchUserByMyIdAndFriendId(myid, friend.getId());
        if(myFriends != null){
            return SearchFriendStatusEnum.ALREADY_FRIENDS.getStatus();
        }

        return SearchFriendStatusEnum.SUCCESS.getStatus();
    }

    /**
     * 根据用户id和好友id来查询好友关系数据表my_friends,用于判断用户与搜索好友是否为好友关系
     */
    private MyFriends searchUserByMyIdAndFriendId(String myId, String friendId){
        return  myFriendsMapper.searchUserByMyIdAndFriendId(myId, friendId);
    }

    /**
     * 根据用户名查询用户，用于查询好友用户信息
     */
    @Override
    public User queryFriendByName(String friendUsername){
        return userMapper.findUserByUsername(friendUsername);
    }

    /**
     * 根据用户id和好友id添加好友请求记录
     */
    @Override
    public void addFriendRequest(String myUserId, String friendUserId){

        // 查询好友请求记录表中是否已经存在请求
        FriendsRequest friendsRequest = friendsRequestMapper.queryFriendRequestByTwoId(myUserId, friendUserId);

        // 好友请求记录不出在，创建好友请求
        if(friendsRequest == null){
            FriendsRequest request = new FriendsRequest();
            Sid sid = new Sid();
            String id = sid.nextShort();

            request.setId(id);
            request.setSendUserId(myUserId);
            request.setAcceptUserId(friendUserId);
            request.setRequestDateTime(new Date());
            friendsRequestMapper.insert(request);
        }
    }

    /**
     * 根据接收用户id查询自身的好友请求记录
     */
    @Override
    public List<FriendsRequestVO> queryFriendRequestsById(String acceptUserId){
        List<FriendsRequestVO> requests = friendsRequestMapper.queryFriendRequestsById(acceptUserId);
        return requests;
    }

    /**
     * 删除好友请求记录
     */
    @Override
    public void removeFriendRequest(String myid, String senderUserId){
        friendsRequestMapper.deleteFriendRequest(myid, senderUserId);
    }

    /**
     * 添加好友关系
     */
    @Override
    public void addMyFriends(String myid, String senderUserId){
        saveFriends(myid, senderUserId);
        saveFriends(senderUserId, myid);
        removeFriendRequest(myid, senderUserId);
        // 使用websocket向好友请求发送方发送更新好友列表更新信息
        Channel channel = UserChannelMap.get(senderUserId);

        if(channel != null){
            // 发送好友列表消息
            DataContent dataContent = new DataContent();
            dataContent.setAction(MessageActionEnum.PULL_FRIEND.type);

            channel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(dataContent)));
        }

    }

    /**
     * 根据用户id获取所有好友，用于渲染通讯录
     */
    @Override
    public List<MyFriendsVO> queryMyFriendsById(String myUserId){
        return myFriendsMapper.searchMyFriendsById(myUserId);
    }

    @Override
    public String saveMessage(ChatMessage chatMessage){
        ChatMsg chatMsg = new ChatMsg();
        Sid sid = new Sid();
        String messageId = sid.nextShort();
        chatMsg.setId(messageId);
        chatMsg.setSendUserId(chatMessage.getSenderId());
        chatMsg.setAcceptUserId(chatMessage.getRecieverId());
        chatMsg.setMsg(chatMessage.getMessage());
        chatMsg.setSignFlag(MessageSignEnum.unsign.type);
        chatMsg.setCreateTime(new Date());

        chatMsgMapper.insert(chatMsg);
        return messageId;
    }

    /**
     * 批量签收消息
     */
    @Override
    public void updateMessageIdSigned(List<String> messageIdList){
        chatMsgMapper.batchSignMessage(messageIdList);
    }

    /**
     * 添加朋友
     */
    public void saveFriends(String myid, String senderUserId){
        Sid sid = new Sid();
        String id = sid.nextShort();

        MyFriends myFriends = new MyFriends();
        myFriends.setId(id);
        myFriends.setMyUserId(myid);
        myFriends.setMyFriendUserId(senderUserId);
        myFriendsMapper.insert(myFriends);
    }

    @Override
    public User saveUser(User user){
        // 为用户随机生成id
        Sid sid = new Sid();
        String id = sid.nextShort();
        // 为用户随机生成二维码
        String qrCodePath = "O:\\Project\\wechat\\LiteWeChat\\QRCode\\" + id +"_qrcode.png";
        // 使用工具生成二维码
        QRCodeUtils.createQRCode(qrCodePath, "This is qrcode of wetchat user:" + user.getUsername());
        MultipartFile qrCodeImgae = FileUtils.fileToMultipart(qrCodePath);
        // 将二维码上传至图片服务器
        String qrCodeURL = "";
        try {
            qrCodeURL = fastDFSClient.uploadQRCode(qrCodeImgae);
            System.out.println(qrCodeURL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        user.setId(id);
        user.setQrcode(qrCodeURL);

        userMapper.insert(user);
        return user;
    }

    @Override
    public User setNickname(String id, String nickname){
        userMapper.updateNickname(id, nickname);
        return queryUserById(id);
    }
}
