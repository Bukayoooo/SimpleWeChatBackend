package com.lite.wechat.netty.handler;

import com.lite.wechat.enums.MessageActionEnum;
import com.lite.wechat.netty.UserChannelMap;
import com.lite.wechat.netty.entity.ChatMessage;
import com.lite.wechat.netty.entity.DataContent;
import com.lite.wechat.service.UserService;
import com.lite.wechat.service.impl.UserServiceImpl;
import com.lite.wechat.utils.JsonUtils;
import com.lite.wechat.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.n3r.idworker.Id;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Chathandler extends SimpleChannelInboundHandler<TextWebSocketFrame>{

    private static ChannelGroup users = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception{
        // 获取从客户端获得的信息
        String content = msg.text();
        log.debug("获得的数据:" + content);
        System.out.println(new TextWebSocketFrame(LocalDateTime.now() + " 收到消息:" + content));
        Channel channel = ctx.channel();
        // 1. 获取客户端发送的消息
        DataContent dataContent = JsonUtils.jsonToPojo(content, DataContent.class);
        // 2. 获取消息类型
        Integer action = dataContent.getAction();
        DataContent dataContentRet = new DataContent();
        // 2.1 客户端链接信息
        if(action == MessageActionEnum.CONNECT.type){
            // 获取客户端用户id, 将用户和channel存储到map中
            String senderId = dataContent.getChatMessage().getSenderId();
            UserChannelMap.put(senderId, channel);
        }
        // 2.2 用户聊天信息
        else if(action == MessageActionEnum.CHAT.type){
            // 获取聊天信息，及聊天内容
            ChatMessage chatMessage = dataContent.getChatMessage();
            String senderId = chatMessage.getSenderId();
            String recieverId = chatMessage.getRecieverId();
            String message = chatMessage.getMessage();

            UserService userService = (UserService) SpringUtil.getBean("userServiceImpl");
//            UserService userService = new UserServiceImpl();
            System.out.println(userService);
            // 将消息保存到数据库中，并标记为未签收
            String messageId = userService.saveMessage(chatMessage);
            chatMessage.setMessageId(messageId);

            dataContentRet.setChatMessage(chatMessage);
            dataContentRet.setAction(dataContent.getAction());

            // 根据id查询好友的链接通道，将消息发送到好友客户端
            Channel recieverChannel = UserChannelMap.get(recieverId);
            if(recieverChannel == null){
                // TODO 用户离线，推送消息
            }else {
                // 从users中再次查询channel
                Channel findChannel = users.find(recieverChannel.id());
                if (findChannel != null){
                    // 用户在线
                    findChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(dataContentRet)));
                }else {
                    // TODO 用户离线，推送消息
                }
            }
            
        }
        // 2.3 用户接收到信息，签收消息
        else if(action == MessageActionEnum.SIGNED.type){
            UserService userService = (UserService) SpringUtil.getBean("userServiceImpl");
            // 从扩展字段中获取签收消息，代表要签收的消息id
            String messageIdsStr = dataContent.getExtend();
            String[] messageId = messageIdsStr.split(",");
            // 将messageId存入到list中，批量签收消息
            List<String> idList = new ArrayList<>();
            for(String id : messageId){
                idList.add(id);
            }

            if(idList != null && idList.size() > 0){
                // 批量签收
                userService.updateMessageIdSigned(idList);
            }
        }


    }

    // 客户端与服务端链接后触发该方法, 添加所有的channel
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception{
        users.add(ctx.channel());
    }

    // 客户端与服务端断开链接后触发该方法，并自定移除channelgroup中的channel
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception{
        // 此段代码可以不用写，ChannelGroup会自定删除channel
        users.remove(ctx.channel());
        log.debug("客户端以断开， channel id为 {}", ctx.channel().id().asLongText());
    }

    // 链接发生异常，断开链接
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
        cause.printStackTrace();
        // 发生异常，关闭连接
        ctx.channel().close();
        users.remove(ctx.channel());
    }
}
