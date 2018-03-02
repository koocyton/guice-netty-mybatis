package com.doopp.gauss.server.application;

import com.doopp.gauss.rpc.controller.AccountController;
import com.google.inject.Inject;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Sharable
public class ApplicationHandler extends ChannelInboundHandlerAdapter {

    @Inject
    private AccountController accountController;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest httpRequest = (FullHttpRequest) msg;
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(accountController.hello(), CharsetUtil.UTF_8));
        httpResponse.headers().set(CONTENT_TYPE, "text/plain;charset=UTF-8");
        httpResponse.headers().set(CONTENT_LENGTH, httpResponse.content().readableBytes());

        if (HttpUtil.isKeepAlive(httpRequest)) {
            httpResponse.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        ctx.writeAndFlush(httpResponse);
    }
}
