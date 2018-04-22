package com.doopp.gauss.common.listener;

import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.server.listener.WebSocketListener;
import com.google.gson.Gson;
import com.google.inject.Singleton;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class GameSocketListener extends WebSocketListener {

    private Map<Long, Channel> channelGroup = new HashMap<>();

    @Override
    public void onTextMessage(Channel channel, String message) {
        channel.writeAndFlush(new TextWebSocketFrame("[you say]" + message));
    }

    @Override
    public void onJsonMessage(Channel channel, Object json) {
        channel.writeAndFlush(new TextWebSocketFrame("[you say]" + (new Gson()).toJson(json)));
    }

    private Long getChannelUserId(Channel channel) {
        return channel.getUserId();
    }

    private void sendMessage(Long userId, String message) {
        Channel channel = channelGroup.get(userId);
        if (channel!=null) {
            channel.writeAndFlush(new TextWebSocketFrame(message));
        }
    }

    @Override
    public void onConnect(Channel channel) {
        Long userId = 123L;
        channelGroup.put(userId, channel);
        channelGroup.get(userId).close();
    }

    @Override
    public void onDisconnect(Channel channel) {
        Long userId = 123L;
        channelGroup.remove(userId);
    }
}
