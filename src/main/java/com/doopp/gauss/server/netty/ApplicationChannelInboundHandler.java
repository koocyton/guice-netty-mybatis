package com.doopp.gauss.server.netty;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

public class ApplicationChannelInboundHandler extends SimpleChannelInboundHandler<Object> {

    private final static Logger logger = LoggerFactory.getLogger(ApplicationChannelInboundHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        // 传统的HTTP接入
        if (msg instanceof FullHttpRequest) {
            ctx.write(httpRequestDispatcher((FullHttpRequest) msg));
            ctx.flush();
        }

        // WebSocket接入
        else if (msg instanceof WebSocketFrame) {

        }
    }

    private static FullHttpResponse httpRequestDispatcher(FullHttpRequest httpRequest) throws Exception {

        // 编码
        String charsetName = "UTF-8";

        // Map url
        String uri = httpRequest.uri();

        // Request Method
        HttpMethod httpMethod = httpRequest.method();

        // request uri
        switch(uri) {
            case
        }

        // FullHttpResponse httpResponse
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(HTTP_1_1, OK);
        httpResponse.replace(Unpooled.wrappedBuffer("你好，Netty".getBytes("UTF-8")));

        httpResponse.headers().set(CONTENT_TYPE, "text/plain;charset=UTF-8");
        httpResponse.headers().set(CONTENT_LENGTH, httpResponse.content().readableBytes());
        if (HttpUtil.isKeepAlive(httpRequest)) {
            httpResponse.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        return httpResponse;
    }
}