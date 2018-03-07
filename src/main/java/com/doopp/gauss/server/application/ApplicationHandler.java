package com.doopp.gauss.server.application;

import com.doopp.gauss.backend.controller.AccountController;
import com.doopp.gauss.server.freemarker.ModelMap;
import com.google.inject.Inject;
import com.google.inject.Injector;
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Sharable
public class ApplicationHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(ApplicationHandler.class);

    @Inject
    private Configuration tplconfiguration;

    @Inject
    private Injector injector;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        FullHttpRequest httpRequest = (FullHttpRequest) msg;
        // if webSocket
        if (httpRequest.headers().get("Connection")!=null && httpRequest.headers().get("Upgrade")!=null) {
            ctx.fireChannelRead(httpRequest.retain());
        }
        // if http request
        else {
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
            httpResponse.headers().set(CONTENT_TYPE, "text/html;charset=UTF-8");
            httpResponse.headers().set(CONTENT_LENGTH, httpResponse.content().readableBytes());

            //if (HttpUtil.isKeepAlive(httpRequest)) {
            //    httpResponse.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            //}
            ctx.writeAndFlush(httpResponse);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("Client:" + incoming.remoteAddress() + " is exception");
        cause.printStackTrace();
        ctx.close();
    }
}
