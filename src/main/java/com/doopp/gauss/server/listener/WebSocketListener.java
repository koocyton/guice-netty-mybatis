package com.doopp.gauss.server.listener;

import io.netty.channel.Channel;

public abstract class WebSocketListener {

    public void onConnect(Channel channel) {
    }

    public void onDisconnect(Channel channel) {
    }

    public void onTextMessage(Channel channel, String message) {
    }

    public void onJsonMessage(Channel channel, Object jsonClass) {
    }

    public void onBinaryMessage(Channel channel, byte[] message) {
    }

    public void onPingMessage(Channel channel) {
    }

    public void onPongMessage(Channel channel) {
    }
}
