package com.doopp.gauss.server.handler;

import com.doopp.gauss.server.dispatcher.RequestProcessor;
import com.google.inject.Inject;
import com.google.inject.Injector;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class Http1RequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private Injector injector;

    private String wsUri;

    public Http1RequestHandler(Injector injector, String wsUri) {
        this.injector = injector;
        this.wsUri = wsUri;
    }

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        return super.acceptInboundMessage(msg);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest httpRequest) throws Exception {
        if (httpRequest.uri().equals(wsUri)) {
            ctx.fireChannelRead(httpRequest.setUri(wsUri).retain());
        }
        else {
            FullHttpResponse httpResponse = new DefaultFullHttpResponse(HTTP_1_1, OK);
            injector.getInstance(RequestProcessor.class).processor(ctx, httpRequest, httpResponse);
            httpResponse.headers().set(CONTENT_LENGTH, httpResponse.content().readableBytes());
            ChannelFuture future = ctx.writeAndFlush(httpResponse);
            // future.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
