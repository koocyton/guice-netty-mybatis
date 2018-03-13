package com.doopp.gauss.server.dispatcher;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONCreator;
import com.doopp.gauss.server.filter.SessionFilter;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;

import com.doopp.gauss.server.freemarker.ModelMap;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

@Singleton
public class RequestProcessor {

    @Inject
    private Injector injector;

    @Inject
    private SessionFilter sessionFilter;

    @Inject
    private Configuration viewConfiguration;

    public void processor(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
        // filter
        sessionFilter.doFilter(httpRequest, httpResponse);
    }

    public void triggerAction(FullHttpRequest httpRequest, FullHttpResponse httpResponse) throws Exception {

        // request dispatcher
        String uri = httpRequest.uri();
        String[] uriSplit = uri.split("/");
        String ctrlName = (uri.equals("/") || uriSplit.length<2) ? "account" : uriSplit[1];
        String methodName = (uri.equals("/") || uriSplit.length<3) ? "hello" : uriSplit[2];

        // call controller
        String ctrlClass = "com.doopp.gauss.backend.controller." + ctrlName.substring(0, 1).toUpperCase() + ctrlName.substring(1) + "Controller";
        Object ctrlObject = injector.getInstance(Class.forName(ctrlClass));
        ModelMap modelMap = new ModelMap();
        Method method = ctrlObject.getClass().getMethod(methodName, new Class[]{ModelMap.class});
        Object[] arg = new Object[]{modelMap};

        String content;
        if (method.isAnnotationPresent(JSONCreator.class)) {
            // JSON
            content = JSON.toJSONString(method.invoke(ctrlObject, arg));
            httpResponse.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
        }
        else {
            // 模板
            String actionResult = (String) method.invoke(ctrlObject, arg);
            // get template
            viewConfiguration.setClassForTemplateLoading(this.getClass(), "/template/" + ctrlName);
            Template template = viewConfiguration.getTemplate(actionResult + ".html");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            template.process(modelMap, new OutputStreamWriter(outputStream));
            content = outputStream.toString("UTF-8");
            httpResponse.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
        }

        // write response
        httpResponse.content().writeBytes(Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));
        httpResponse.setStatus(HttpResponseStatus.OK);
    }
}
