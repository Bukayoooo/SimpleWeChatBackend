package com.lite.wechat.controller;

import com.lite.wechat.domain.bo.UserBO;
import com.lite.wechat.domain.entity.FriendsRequest;
import com.lite.wechat.domain.vo.FriendsRequestVO;
import com.lite.wechat.domain.vo.MyFriendsVO;
import com.lite.wechat.domain.vo.UserVO;
import com.lite.wechat.enums.SearchFriendStatusEnum;
import com.lite.wechat.service.UserService;
import com.lite.wechat.utils.CommonUtils;
import com.lite.wechat.utils.FastDFSUploadUtils;
import org.apache.commons.lang3.StringUtils;
import com.lite.wechat.domain.entity.User;
import com.lite.wechat.utils.JsonData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserService userService;

    @Autowired
    private FastDFSUploadUtils fastDFSUploadUtils;

    /**
     * 用户注册或者登录
     */
    @PostMapping("/registerOrLogin")
    public JsonData registerOrLogin(@RequestBody User user) {

        String username = user.getUsername();
        String password = user.getPassword();

        // 1. 判断用户名或密码是否为空
        if(StringUtils.isBlank(username)
                        || StringUtils.isBlank(password)) {
            return JsonData.buildFailure("用户名或密码为空...");
        }
        // 2. 查询数据库，判断用户是否已经存在，不存在直接注册
        boolean userNameIsExist = userService.queryUserNameIsExist(username);
        User userResult = null;
        if(userNameIsExist){
            // 2.1 用户名存在，登录客户端
            user.setPassword(CommonUtils.MD5(password));
            userResult = userService.queryUserForLogin(user);
            // 登录失败
            if(userResult == null){
                return JsonData.buildFailure("用户名或密码错误...");
            }
        }else{
            /**
             * TODO 用户注册页面
             */
            // 2. 用户名不存在，注册账户
            user.setUsername(username);
            user.setNickname(username);
            user.setFaceImage("");
            user.setFaceImageBig("");
            user.setPassword(CommonUtils.MD5(password));
            userResult = userService.saveUser(user);
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userResult, userVO);

        return JsonData.buildSuccess(userVO, "登录成功!");
    }

    /**
     * 更改昵称
     */
    @PostMapping("/setNickname")
    public JsonData setNickname(@RequestBody UserBO user){
        User userResult = userService.setNickname(user.getId(), user.getNickname());
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userResult, userVO);
        return JsonData.buildSuccess(userVO);
    }

    /**
     * 上传头像
     */
    @PostMapping(value = "/uploadFaceImage", headers = "content-type=multipart/form-data")
    public JsonData uploadFaceImage(MultipartFile file, String id){
        String imgURL = "";
        try {
            imgURL = fastDFSUploadUtils.upload(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 获取缩略图
        String thump = "_80x80.";
        String[] split = imgURL.split("\\.");
        String thumpImgUrl = split[0] + thump + split[1];

        User user = new User();
        user.setId(id);
        user.setFaceImageBig(imgURL);
        user.setFaceImage(thumpImgUrl);

        User userResult = userService.updateUserById(user);

        return JsonData.buildSuccess(userResult);
    }

    /**
     * 搜索用户
     */
    @PostMapping("/searchFriend")
    public JsonData searchFriend(String myid, String friendUsername){

        if(StringUtils.isBlank(myid) || StringUtils.isBlank(friendUsername)){
            return JsonData.buildFailure("用户名为空...");
        }

        Integer status = userService.getStatusByIdAndFriendname(myid, friendUsername);

        // 1. 如果搜索的用户是自己，返回搜索的用户是自己
        // 2. 如果搜索的用户已经是自己的好友，返回搜索的用户已经是自己的好友
        // 3. 如果搜索的用户不存在，则返回不存在该用户

        if(status == SearchFriendStatusEnum.SUCCESS.status){
            User friend = userService.queryFriendByName(friendUsername);
            UserVO friendVO = new UserVO();
            BeanUtils.copyProperties(friend, friendVO);
            return JsonData.buildSuccess(friendVO);
        }else{
            return JsonData.buildFailure(SearchFriendStatusEnum.getMsgByStatus(status));
        }
    }

    /**
     * 发送好友请求
     */
    @PostMapping("/sendFriendRequest")
    public JsonData sendFriendRequest(String myUserId, String friendUserId){

        userService.addFriendRequest(myUserId, friendUserId);

        return JsonData.buildSuccess("");
    }

    /**
     * 查询好友请求表
     */
    @PostMapping("/queryFriendRequests")
    public JsonData queryFriendRequests(String myUserId){
        if(StringUtils.isBlank(myUserId)){
            return JsonData.buildFailure("用户id不可为空");
        }
        List<FriendsRequestVO> friendsRequests = userService.queryFriendRequestsById(myUserId);
        return JsonData.buildSuccess(friendsRequests);
    }

    /**
     * 忽略好友请求
     */
    @PostMapping("/ignoreFriendRequest")
    public JsonData ignoreFriendRequest(String myid, String senderUserId){
        if(StringUtils.isBlank(myid) || StringUtils.isBlank(senderUserId)){
            return JsonData.buildFailure("用户id不可为空");
        }
        userService.removeFriendRequest(myid, senderUserId);
        return JsonData.buildSuccess("");
    }

    /**
     * 通过好友请求
     */
    @PostMapping("/passFriendRequest")
    public JsonData passFriendRequest(String myid, String senderUserId){
        if(StringUtils.isBlank(myid) || StringUtils.isBlank(senderUserId)){
            return JsonData.buildFailure("用户id不可为空");
        }
        userService.addMyFriends(myid, senderUserId);
        // 返回我的所有好友，用于更新好友列表
        List<MyFriendsVO> myFriendsVO = userService.queryMyFriendsById(myid);
        return JsonData.buildSuccess(myFriendsVO);
    }

    /**
     * 获取通讯录列表
     */
    @PostMapping("/getContactList")
    public JsonData getContactList(String myUserId){
        List<MyFriendsVO> myFriendsList = userService.queryMyFriendsById(myUserId);
        return JsonData.buildSuccess(myFriendsList);
    }

//    public

}
