package com.lite.wechat.mapper;

import com.lite.wechat.domain.entity.User;
import com.lite.wechat.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User findUserByUsername(@Param("username") String username);

    User findUserByUsernameAndPassWord(User user);

    void insert(User user);

    void updateNickname(@Param("id") String id, @Param("nickname") String nickname);

    User findUserById(@Param("id") String id);

    void updateUserById(User user);
}