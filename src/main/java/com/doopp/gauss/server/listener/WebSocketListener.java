package com.doopp.gauss.server.listener;

import io.netty.channel.Channel;

public interface WebSocketListener {

    public void onConnect(Channel channel);

    public void onDisconnect(Channel channel);

    public String onTextMessage(Channel channel, String message);

    public String onBinaryMessage(Channel channel, byte[] message);

    public String onPingMessage(Channel channel);

    public String onPongMessage(Channel channel);
}
