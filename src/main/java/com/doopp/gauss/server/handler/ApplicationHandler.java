package com.doopp.gauss.server.handler;

import com.google.inject.Injector;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;

public class ApplicationHandler  extends SimpleChannelInboundHandler<FullHttpRequest> {

    private Injector injector;

    private String websocketPath;

    public ApplicationHandler(Injector injector, String websocketPath) {
        this.injector = injector;
        this.websocketPath = websocketPath;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest httpRequest) throws Exception {

        // uri
        String uri = httpRequest.uri();

        // pipeline
        ChannelPipeline pipeline = ctx.channel().pipeline();

        // web socket
        if (uri.equals(websocketPath)) {
            pipeline.addLast(new WebSocketServerCompressionHandler());
            pipeline.addLast(new WebSocketServerProtocolHandler("/game-socket", null, true));
            pipeline.addLast(new WebSocketFrameHandler());
            ctx.fireChannelRead(httpRequest.retain());
            return;
        }

        // 根目录加上 index.html
        uri = uri.equals("/") ? uri + "index.html" : uri;

        // 读取资源
        java.io.InputStream ins = getClass().getResourceAsStream("/public" + uri);
        // static file
        if (ins!=null) {
            pipeline.addLast(new StaticFileResourceHandler());
            ctx.fireChannelRead(httpRequest.retain());
        }
        // request
        else {
            pipeline.addLast(new Http1RequestHandler(this.injector));
            ctx.fireChannelRead(httpRequest.retain());
        }
    }
}
