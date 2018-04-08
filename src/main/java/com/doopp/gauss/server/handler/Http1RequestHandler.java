package com.doopp.gauss.server.handler;

import com.doopp.gauss.server.dispatcher.RequestProcessor;
import com.google.inject.Inject;
import com.google.inject.Injector;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslHandler;
import org.slf4j.LoggerFactory;

import java.util.logging.Logger;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class Http1RequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(Http1RequestHandler.class);

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
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {  // (2)
        System.out.println("\nClient:"+ctx.channel().remoteAddress() +"加入");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest httpRequest) throws Exception {
        if (httpRequest.uri().equals(wsUri)) {
            ctx.fireChannelRead(httpRequest.retain());
        }
        else {

            logger.info(">>" + httpRequest.uri());
            if (HttpUtil.is100ContinueExpected(httpRequest)) {
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
                ctx.writeAndFlush(response);
            }

            FullHttpResponse httpResponse = new DefaultFullHttpResponse(httpRequest.protocolVersion(), HttpResponseStatus.OK);
            httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");

            injector.getInstance(RequestProcessor.class).processor(ctx, httpRequest, httpResponse);
            httpResponse.headers().set(CONTENT_LENGTH, httpResponse.content().readableBytes());

            if (HttpUtil.isKeepAlive(httpRequest)) {
                httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }

            //ctx.write(httpResponse);

            //ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            ChannelFuture future = ctx.writeAndFlush(httpResponse);
            if (!HttpUtil.isKeepAlive(httpRequest)) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }
}
