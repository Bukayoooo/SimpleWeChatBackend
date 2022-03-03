package com.lite.wechat.mapper;

import com.lite.wechat.domain.entity.ChatMsg;
import com.lite.wechat.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatMsgMapper{
    void insert(ChatMsg chatMsg);

    void batchSignMessage(List<String> messageIdList);
}