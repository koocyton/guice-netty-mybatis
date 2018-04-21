package com.doopp.gauss.server.handler;

import com.doopp.gauss.server.listener.WebSocketListener;
import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;

public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private WebSocketListener webSocketListener;

    WebSocketFrameHandler(WebSocketListener webSocketListener) {
        this.webSocketListener = webSocketListener;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame socketFrame) throws Exception {

        if (socketFrame instanceof TextWebSocketFrame) {
            handleText(ctx, (TextWebSocketFrame) socketFrame);
        }
        else if (socketFrame instanceof BinaryWebSocketFrame) {
            handleBinary(ctx, (BinaryWebSocketFrame) socketFrame);
        }
        else if (socketFrame instanceof PingWebSocketFrame) {
            handlePing(ctx);
        }
        else if (socketFrame instanceof PongWebSocketFrame) {
            handlePong(ctx);
        }
        else {
            String message = "unsupported frame type: " + socketFrame.getClass().getName();
            throw new UnsupportedOperationException(message);
        }
    }

    private void handleText(ChannelHandlerContext ctx, TextWebSocketFrame textFrame) {
        ByteBuf buf = textFrame.content();
        byte[] byteArray = new byte[buf.capacity()];
        buf.readBytes(byteArray);
        String content = new String(byteArray);
        if (content.length()>1) {
            this.webSocketListener.onTextMessage(ctx.channel(), content);
            this.webSocketListener.onJsonMessage(ctx.channel(), new Gson().fromJson(content, Object.class));
        }
    }

    private void handleBinary(ChannelHandlerContext ctx, BinaryWebSocketFrame binaryFrame) {
        ByteBuf buf = binaryFrame.content();
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        this.webSocketListener.onBinaryMessage(ctx.channel(), data);
    }

    private void handlePing(ChannelHandlerContext ctx) {
        this.webSocketListener.onPingMessage(ctx.channel());
    }

    private void handlePong(ChannelHandlerContext ctx) {
        this.webSocketListener.onPongMessage(ctx.channel());
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        this.webSocketListener.onConnect(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        this.webSocketListener.onDisconnect(ctx.channel());
    }
}
