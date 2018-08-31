package com.doopp.gauss.server.handler;

import com.doopp.gauss.server.application.ApplicationProperties;
import com.doopp.gauss.server.dispatcher.RequestDispatcher;
import com.doopp.gauss.server.filter.SessionFilter;
import com.google.inject.Inject;
import com.google.inject.Injector;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;

public class Http1RequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static Logger logger = LoggerFactory.getLogger(StaticFileResourceHandler.class);

    @Inject
    private Injector injector;

    @Inject
    private ApplicationProperties applicationProperties;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest httpRequest) throws Exception {

        // 取出 request uri 对应调用的 controller 和 method
        URI uri = URI.create(httpRequest.uri());
        // uri path
        String requirePath = uri.getPath();
        // web socket 地址的话
        if (requirePath.equals(applicationProperties.s("server.webSocket"))) {
            ctx.fireChannelRead(httpRequest.retain());
            return;
        }

        if (HttpUtil.is100ContinueExpected(httpRequest)) {
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
            ctx.writeAndFlush(response);
        }

        FullHttpResponse httpResponse = new DefaultFullHttpResponse(httpRequest.protocolVersion(), HttpResponseStatus.OK);
        // httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");

        // do filter
        if (injector.getInstance(SessionFilter.class).doFilter(ctx, httpRequest, httpResponse)) {
            // RequestDispatcher
            injector.getInstance(RequestDispatcher.class).dispatcher(ctx, httpRequest, httpResponse);
        }

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

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel incoming = ctx.channel();
        logger.info("Client: {} 异常", incoming.remoteAddress());
        cause.printStackTrace();
        ctx.close();
    }
}
