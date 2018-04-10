package com.doopp.gauss.server.dispatcher;

import com.doopp.gauss.server.annotation.JsonResponse;
import com.doopp.gauss.server.filter.SessionFilter;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.doopp.gauss.server.freemarker.ModelMap;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

@Singleton
public class RequestDispatcher {

    @Inject
    private Injector injector;

    @Inject
    private SessionFilter sessionFilter;

    @Inject
    private Configuration viewConfiguration;

    public void processor(ChannelHandlerContext ctx, FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
        // filter
        sessionFilter.doFilter(ctx, httpRequest, httpResponse);
    }

    public void triggerAction(FullHttpRequest httpRequest, FullHttpResponse httpResponse) throws Exception {

        String dispatchIndex = httpRequest.method().name() + " " + httpRequest.uri();
        String dispatchValue = DispatchRule.rules.get(dispatchIndex);
        if (dispatchValue==null) {
            httpResponse.setStatus(HttpResponseStatus.NOT_FOUND);
            return;
        }
        String controllerName = dispatchValue.substring(0, dispatchValue.lastIndexOf("."));
        String methodName = dispatchValue.substring(dispatchValue.lastIndexOf(".")+1);

        // call controller
        String ctrlClass = "com.doopp.gauss.backend.controller." + controllerName.substring(0, 1).toUpperCase() + controllerName.substring(1) + "Controller";
        try {
            Class.forName(ctrlClass);
        }
        catch(ClassNotFoundException e) {
            httpResponse.setStatus(HttpResponseStatus.NOT_FOUND);
            return;
        }

        Class[]  classes = new Class[]{};
        Object[] objects = new Object[]{};

        Object ctrlObject = injector.getInstance(Class.forName(ctrlClass));
        Method[] methods = ctrlObject.getClass().getMethods();
        for(Method method : methods) {
            if (method.getName().equals(methodName)) {
                for (Parameter parameter : method.getParameters()) {
                    classes
                }
            }
            System.out.print("\n" + method.getName());
        }
        System.out.print(ctrlObject.getClass().getMethods()[0].getParameters()[0].getType().getTypeName());
        ModelMap modelMap = new ModelMap();
        Method method = ctrlObject.getClass().getMethod(methodName, new Class[]{ModelMap.class});
        Object[] arg = new Object[]{modelMap};

        String content;
        if (method.isAnnotationPresent(JsonResponse.class)) {
            // JSON
            Gson gson = new Gson();
            content = gson.toJson(method.invoke(ctrlObject, arg));
            httpResponse.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
        }
        else {
            // 模板
            String actionResult = (String) method.invoke(ctrlObject, arg);
            // get template
            viewConfiguration.setClassForTemplateLoading(this.getClass(), "/template/" + controllerName);
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

