package com.doopp.gauss.server.handler;

import com.doopp.gauss.server.dispatcher.RequestDispatcher;
import com.google.inject.Injector;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;

public class Http1RequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private Injector injector;

    private String websocketPath;

    public Http1RequestHandler(Injector injector, String websocketPath) {
        this.injector = injector;
        this.websocketPath = websocketPath;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest httpRequest) throws Exception {
        if (httpRequest.uri().equals(websocketPath)) {
            // ctx.fireChannelRead(httpRequest.retain());
        }
        else {

            if (HttpUtil.is100ContinueExpected(httpRequest)) {
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
                ctx.writeAndFlush(response);
            }

            FullHttpResponse httpResponse = new DefaultFullHttpResponse(httpRequest.protocolVersion(), HttpResponseStatus.OK);
            // httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");

            injector.getInstance(RequestDispatcher.class).processor(ctx, httpRequest, httpResponse);
            httpResponse.headers().set(CONTENT_LENGTH, httpResponse.content().readableBytes());

            if (HttpUtil.isKeepAlive(httpRequest)) {
                httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }

            ctx.write(httpResponse);
            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            if (!HttpUtil.isKeepAlive(httpRequest)) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }
}
