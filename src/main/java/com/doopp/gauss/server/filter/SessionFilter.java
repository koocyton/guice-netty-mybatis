package com.doopp.gauss.server.filter;

import com.doopp.gauss.api.service.AccountService;
import com.doopp.gauss.common.entity.User;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.net.URI;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

public class SessionFilter {

    private static Logger logger = LoggerFactory.getLogger(SessionFilter.class);

    @Inject
    private AccountService accountService;

    public boolean doFilter(ChannelHandlerContext ctx, FullHttpRequest httpRequest, FullHttpResponse httpResponse) {

        String uri = URI.create(httpRequest.uri()).getPath();

        // 不过滤的uri
        String[] notFilters = new String[]{
                "/login",
                "/api/login",
                "/api/register",
        };

        // 是否过滤
        boolean doFilter = true;

        // 如果uri中包含不过滤的uri，则不进行过滤
        for (String notFilter : notFilters) {
            if (uri.contains(notFilter)) {
                doFilter = false;
                break;
            }
        }

        logger.info((doFilter) ? "{} [filter]" : "{} [unFilter]", uri);

        // 执行过滤 验证通过的会话
        try {
            if (doFilter) {
                // 从 header 里拿到 access token
                String sessionToken = httpRequest.headers().get("session-token");
                // 如果 token 存在，反解 token
                if (sessionToken != null) {
                    User user = accountService.userByToken(sessionToken);
                    // 如果能找到用户
                    if (user != null) {
                        ctx.channel().attr(AttributeKey.valueOf("currentUser")).set(user);
                        return true;
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
                return true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            writeErrorResponse(HttpResponseStatus.BAD_GATEWAY, httpResponse, e.getMessage());
        }
        return false;
    }

    private static void writeErrorResponse(HttpResponseStatus responseStatus, FullHttpResponse httpResponse, String message) {
        String json = "{\"status\":" + responseStatus.code() + ", \"message\":\"" + message + "\"}";
        httpResponse.setStatus(responseStatus);
        httpResponse.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
        httpResponse.content().writeBytes(Unpooled.copiedBuffer(json, CharsetUtil.UTF_8));
    }
}
