package com.doopp.gauss.server.dispatcher;

import com.doopp.gauss.common.exception.GaussException;
import com.doopp.gauss.server.annotation.*;
import com.doopp.gauss.server.filter.SessionFilter;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.util.*;

import com.doopp.gauss.server.freemarker.ModelMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

public class RequestDispatcher {

    private final static Logger logger = LoggerFactory.getLogger(RequestDispatcher.class);

    @Inject
    private Injector injector;

    @Inject
    private Configuration viewConfiguration;

    public void dispatcher(ChannelHandlerContext context, FullHttpRequest request, FullHttpResponse response) throws Exception {

        // 取出 request uri 对应调用的 controller 和 method
        URI uri = URI.create(request.uri());
        // uri path
        String dispatchUri = uri.getPath();
        logger.info(">>> RequestDispatcher " + dispatchUri);
        // controller method index
        String dispatchIndex = request.method().name() + " " + dispatchUri;
        String dispatchValue = DispatchRule.rules.get(dispatchIndex);
        if (dispatchValue == null) {
            dispatchValue = DispatchRule.rules.get(dispatchUri);
        }
        if (dispatchValue == null) {
            response.setStatus(HttpResponseStatus.NOT_FOUND);
            return;
        }

        String controllerName = dispatchValue.substring(0, dispatchValue.lastIndexOf("."));
        String methodName = dispatchValue.substring(dispatchValue.lastIndexOf(".") + 1);

        // 拿到 Controller Class
        String ctrlClass = "com.doopp.gauss.api.controller." + controllerName.substring(0, 1).toUpperCase() + controllerName.substring(1) + "Controller";
        try {
            Class.forName(ctrlClass);
        } catch (ClassNotFoundException e) {
            response.setStatus(HttpResponseStatus.NOT_FOUND);
            return;
        }

        // 用户模板的 modelMap
        // ModelMap modelMap = new ModelMap();

        // 将方法的参数找出来，用于注入对应类型的 object
        ArrayList<Class> classList = new ArrayList<>();
        ArrayList<Object> objectList = new ArrayList<>();

        // 拿到 controller 的注入方法
        Object ctrlObject = injector.getInstance(Class.forName(ctrlClass));

        // ModelMap
        ModelMap modelMap = new ModelMap();
        // POST GET 参数
        Map<String, String> requestParams = this.getRequestParams(request);
        // Headers
        HttpHeaders httpHeaders = request.headers();
        // Cookies
        Set<Cookie> httpCookies = httpHeaders.get("Cookie") == null
                ? null
                : ServerCookieDecoder.STRICT.decode(httpHeaders.get("Cookie"));
        // 获取所有的方法
        Method[] methods = ctrlObject.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                for (Parameter parameter : method.getParameters()) {
                    Class parameterClass = Class.forName(parameter.getType().getTypeName());
                    // modelMap 另处理
                    if (parameterClass == ModelMap.class) {
                        objectList.add(modelMap);
                        classList.add(parameterClass);
                    }
                    // httpRequest
                    else if (parameterClass == FullHttpRequest.class) {
                        objectList.add(request);
                        classList.add(parameterClass);
                    }
                    // httpResponse
                    else if (parameterClass == FullHttpResponse.class) {
                        objectList.add(response);
                        classList.add(parameterClass);
                    }
                    // RequestParam
                    else if (parameter.getAnnotation(RequestParam.class) != null) {
                        String requestParamKey = parameter.getAnnotation(RequestParam.class).value();
                        String requestParamVal = requestParams.get(requestParamKey);
                        if (parameterClass == Long.class) {
                            objectList.add(Long.valueOf(requestParamVal));
                            classList.add(Long.class);
                        } else if (parameterClass == Integer.class) {
                            objectList.add(Integer.valueOf(requestParamVal));
                            classList.add(Integer.class);
                        } else if (parameterClass == Boolean.class) {
                            objectList.add(Boolean.valueOf(requestParamVal));
                            classList.add(Boolean.class);
                        } else {
                            objectList.add(String.valueOf(requestParamVal));
                            classList.add(String.class);
                        }
                    }
                    // RequestHeader
                    else if (parameter.getAnnotation(RequestHeader.class) != null) {
                        String requestParamKey = parameter.getAnnotation(RequestHeader.class).value();
                        objectList.add(httpHeaders.get(requestParamKey));
                        classList.add(String.class);
                    }
                    // RequestCookie
                    else if (parameter.getAnnotation(RequestCookie.class) != null) {
                        String requestParamKey = parameter.getAnnotation(RequestCookie.class).value();
                        String requestParamVal = null;
                        if (httpCookies != null) {
                            for (Cookie cookie : httpCookies) {
                                if (cookie.name().equals(requestParamKey)) {
                                    requestParamVal = cookie.value();
                                }
                            }
                        }
                        objectList.add(requestParamVal);
                        classList.add(parameterClass);
                    }
                    // RequestSession
                    else if (parameter.getAnnotation(RequestSession.class) != null) {
                        String requestParamKey = parameter.getAnnotation(RequestSession.class).value();
                        objectList.add(context.channel().attr(AttributeKey.valueOf(requestParamKey)).get());
                        classList.add(parameterClass);
                    }
                    // RequestBody
                    else if (parameter.getAnnotation(RequestBody.class) != null) {
                        ByteBuf bf = request.content();
                        byte[] byteArray = new byte[bf.capacity()];
                        bf.readBytes(byteArray);
                        objectList.add((new Gson()).fromJson(new String(byteArray), parameterClass));
                        classList.add(parameterClass);
                    }
                    // null object
                    else {
                        objectList.add(null);
                        classList.add(Object.class);
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
        String content = "";

        // if json
        if (method.isAnnotationPresent(ResponseBody.class)) {
            // new Gson();
            Gson gson = new GsonBuilder().serializeNulls().create();
            try {
                content = gson.toJson(method.invoke(ctrlObject, objects));
                response.setStatus(HttpResponseStatus.OK);
            } catch (InvocationTargetException e) {
                if (e.getCause().getClass() == GaussException.class) {
                    GaussException ge = (GaussException) e.getCause();
                    content = gson.toJson(ge);
                } else {
                    content = gson.toJson(new GaussException((short) 500, "System Error"));
                }
                response.setStatus(HttpResponseStatus.BAD_GATEWAY);
            }
            response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
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
            response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
        }

        // write response
        response.content().writeBytes(Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));
    }

    // 处理 Get Post 请求
    private Map<String, String> getRequestParams(HttpRequest req) {

        Map<String, String> requestParams = new HashMap<>();
        // 处理get请求
        if (req.method() == HttpMethod.GET || req.method() == HttpMethod.POST) {
            QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
            Map<String, List<String>> params = decoder.parameters();
            for (Map.Entry<String, List<String>> next : params.entrySet()) {
                requestParams.put(next.getKey(), next.getValue().get(0));
            }
        }
        // 处理POST请求
        if (req.method() == HttpMethod.POST) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), req);
            List<InterfaceHttpData> postData = decoder.getBodyHttpDatas();
            for (InterfaceHttpData data : postData) {
                if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                    MemoryAttribute attribute = (MemoryAttribute) data;
                    requestParams.put(attribute.getName(), attribute.getValue());
                }
            }
        }
        return requestParams;
    }
}

