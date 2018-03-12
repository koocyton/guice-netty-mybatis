package com.doopp.gauss.server.handler;

import com.doopp.gauss.server.dispatcher.RequestProcessor;
import com.google.inject.Inject;
import com.google.inject.Injector;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Sharable
public class Http1RequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Inject
    private Injector injector;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest httpRequest) throws Exception {
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer("hello", CharsetUtil.UTF_8));
        injector.getInstance(RequestProcessor.class).processor(httpRequest, httpResponse);
        httpResponse.headers().set(CONTENT_LENGTH, httpResponse.content().readableBytes());
        ctx.writeAndFlush(httpResponse);
    }
}
