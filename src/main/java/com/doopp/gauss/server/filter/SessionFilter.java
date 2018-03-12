package com.doopp.gauss.server.filter;

import com.doopp.gauss.backend.service.AccountService;
import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.server.dispatcher.RequestProcessor;
import com.google.inject.Inject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

public class SessionFilter {

    @Inject
    private AccountService accountService;

    @Inject
    private RequestProcessor requestProcessor;

    public void doFilter(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {

        String uri = httpRequest.uri();

        // 不过滤的uri
        String[] notFilters = new String[]{

            // session request
            "/login-form",
            "/register",
            "/login",
            "/logout",

            // static
//            "/js",
//            "/image",
//            "/css",
//            "/favicon.ico",
//            "/webjars",

            // helper
            "/hello",
            "/druid"
        };

        // 是否过滤
        boolean doFilter = true;

        // 如果uri中包含不过滤的uri，则不进行过滤
        for (String notFilter : notFilters) {
            if (uri.contains(notFilter) || uri.equals("/api")) {
                doFilter = false;
                break;
            }
        }

        // 执行过滤 验证通过的会话
        try {
            if (doFilter) {
                // 从 header 里拿到 access token
                String sessionToken = httpRequest.headers().get("session-token");

                // 如果 token 存在，反解 token
                if (sessionToken != null) {
                    User user = accountService.getUserByToken(sessionToken);
                    // 如果能找到用户
                    if (user != null) {
                        requestProcessor.triggerAction(httpRequest, httpResponse);
                    }
                    // 如果不能找到用户
                    else {
                        writeErrorResponse(HttpResponseStatus.NOT_FOUND, httpResponse, "not found user");
                    }
                }
                // 如果 token 不对
                else {
                    writeErrorResponse(HttpResponseStatus.NOT_ACCEPTABLE, httpResponse, "token failed");
                }
            }
            // 不用校验
            else {
                requestProcessor.triggerAction(httpRequest, httpResponse);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            writeErrorResponse(HttpResponseStatus.BAD_GATEWAY, httpResponse, e.getMessage());
        }
    }

    private static void writeErrorResponse(HttpResponseStatus responseStatus, FullHttpResponse httpResponse, String message) {
        String json = "{\"err_code\":" + responseStatus + ", \"err_message\":\"" + message + "\"}";
        httpResponse.setStatus(responseStatus);
        httpResponse.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
        httpResponse.content().writeBytes(Unpooled.copiedBuffer(json, CharsetUtil.UTF_8));
    }
}
