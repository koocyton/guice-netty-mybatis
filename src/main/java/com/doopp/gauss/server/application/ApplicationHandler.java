package com.doopp.gauss.server.application;

import com.doopp.gauss.rpc.controller.AccountController;
import com.doopp.gauss.server.freemarker.ModelMap;
import com.doopp.gauss.server.netty.NettyServer;
import com.google.inject.Inject;
import com.google.inject.Injector;
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.Buffer;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Sharable
public class ApplicationHandler extends ChannelInboundHandlerAdapter {

    @Inject
    private Configuration tplconfiguration;

    @Inject
    private Injector injector;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof FullHttpRequest) {

            ModelMap modelMap = injector.getBinding(ModelMap.class).getProvider().get();
            // injector.getInstance(ModelMap.class);
            // System.out.print(modelMap);
            AccountController accountController = injector.getBinding(AccountController.class).getProvider().get();
            String tplName = accountController.hello(modelMap);
            tplconfiguration.setClassForTemplateLoading(this.getClass(), "/template/account");
            Template template = tplconfiguration.getTemplate(tplName + ".html");

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            template.process(modelMap, new OutputStreamWriter(outputStream));
            String templateContent = outputStream.toString("UTF-8");

            // FullHttpRequest httpRequest = (FullHttpRequest) msg;
            FullHttpResponse httpResponse = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(templateContent, CharsetUtil.UTF_8));
            httpResponse.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
            httpResponse.headers().set(CONTENT_LENGTH, httpResponse.content().readableBytes());

            //if (HttpUtil.isKeepAlive(httpRequest)) {
            //    httpResponse.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            //}
            ctx.writeAndFlush(httpResponse);
        }
        else if (msg instanceof WebSocketFrame) {
            // ctx.pipeline().addLast(myWebsocketHandler);
        }
    }
}
