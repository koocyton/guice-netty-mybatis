package com.doopp.gauss.server.handler;

import com.google.inject.Injector;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import java.io.InputStream;

import java.net.URI;

public class ApplicationHandler  extends SimpleChannelInboundHandler<FullHttpRequest> {

    private Injector injector;

    private String websocketPath;

    public ApplicationHandler(Injector injector, String websocketPath) {
        this.injector = injector;
        this.websocketPath = websocketPath;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest httpRequest) throws Exception {

        // requestUri
        URI requestUri= URI.create(httpRequest.uri());
        String requestPath = requestUri.getPath();

        // pipeline
        ChannelPipeline pipeline = ctx.channel().pipeline();

        // web socket
        // if (requestPath.equals(websocketPath)) {
        //    pipeline.addLast(new WebSocketServerCompressionHandler());
        //    pipeline.addLast(new WebSocketServerProtocolHandler("/game-socket", null, true));
        //    pipeline.addLast(new WebSocketFrameHandler(this.injector.getInstance(GameSocketListener.class)));
        //    ctx.fireChannelRead(httpRequest.retain());
        //    return;
        // }

        // 根目录加上 index.html
        requestPath = requestPath.equals("/") ? requestPath + "index.html" : requestPath;
        // 读取资源
        InputStream ins = getClass().getResourceAsStream("/public" + requestPath);
        // static file
        if (ins!=null) {
            pipeline.addLast(new StaticFileResourceHandler(requestPath));
            ctx.fireChannelRead(httpRequest.retain());
        }
        // request
        else {
            pipeline.addLast(new Http1RequestHandler(this.injector));
            ctx.fireChannelRead(httpRequest.retain());
        }
    }
}
