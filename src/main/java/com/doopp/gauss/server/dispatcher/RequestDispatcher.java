package com.doopp.gauss.server.dispatcher;

import com.doopp.gauss.server.annotation.JsonResponse;
import com.doopp.gauss.server.filter.SessionFilter;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;

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

        // 取出 request uri 对应调用的 controller 和 method
        String dispatchIndex = httpRequest.method().name() + " " + httpRequest.uri();
        String dispatchValue = DispatchRule.rules.get(dispatchIndex);
        if (dispatchValue==null) {
            dispatchValue = DispatchRule.rules.get(httpRequest.uri());
        }
        if (dispatchValue==null) {
            httpResponse.setStatus(HttpResponseStatus.NOT_FOUND);
            return;
        }

        String controllerName = dispatchValue.substring(0, dispatchValue.lastIndexOf("."));
        String methodName = dispatchValue.substring(dispatchValue.lastIndexOf(".")+1);

        // 拿到 Controller Class
        String ctrlClass = "com.doopp.gauss.backend.controller." + controllerName.substring(0, 1).toUpperCase() + controllerName.substring(1) + "Controller";
        try {
            Class.forName(ctrlClass);
        }
        catch(ClassNotFoundException e) {
            httpResponse.setStatus(HttpResponseStatus.NOT_FOUND);
            return;
        }

        // 用户模板的 modelMap
        ModelMap modelMap = new ModelMap();

        // 将方法的参数找出来，用于注入对应类型的 object
        ArrayList<Class> classList = new ArrayList<>();
        ArrayList<Object> objectList = new ArrayList<>();

        // 拿到 controller 的注入方法
        Object ctrlObject = injector.getInstance(Class.forName(ctrlClass));

        // 获取所有的方法
        Method[] methods = ctrlObject.getClass().getMethods();
        for(Method method : methods) {
            if (method.getName().equals(methodName)) {
                for (Parameter parameter : method.getParameters()) {
                    Class parameterClass = Class.forName(parameter.getType().getTypeName());
                    classList.add(parameterClass);
                    // modelMap 另处理
                    if (parameterClass==modelMap.getClass()) {
                        objectList.add(modelMap);
                    }
                    // httpRequest
                    else if (parameterClass==FullHttpRequest.class) {
                        objectList.add(httpRequest);
                    }
                    // httpResponse
                    else if (parameterClass==FullHttpResponse.class) {
                        objectList.add(httpResponse);
                    }
                    else {
                        objectList.add(parameterClass.newInstance());
                    }
                }
                break;
            }
        }

        // 转类型
        Class[] classes = classList.toArray(new Class[classList.size()]);
        Object[] objects = objectList.toArray();
        Method method = ctrlObject.getClass().getMethod(methodName, classes);

        // content
        String content;

        // if json
        if (method.isAnnotationPresent(JsonResponse.class)) {
            Gson gson = new Gson();
            content = gson.toJson(method.invoke(ctrlObject, objects));
            httpResponse.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
        }
        // if template
        else {
            String actionResult = (String) method.invoke(ctrlObject, objects);
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

