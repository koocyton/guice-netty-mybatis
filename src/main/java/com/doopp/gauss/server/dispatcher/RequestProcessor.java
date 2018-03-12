package com.doopp.gauss.server.dispatcher;

import com.doopp.gauss.backend.controller.AccountController;
import com.doopp.gauss.server.filter.SessionFilter;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import com.doopp.gauss.server.freemarker.ModelMap;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
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
        sessionFilter.doFilter(httpRequest, httpResponse);
    }

    public void triggerAction(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {

        String uri = httpRequest.uri();
        String[] uriSplit = uri.split("\\\\/");
        System.out.print("\n >>> " + uriSplit.length + " - " + uri + "\n" + uriSplit[0] + uriSplit.length);
        uriSplit[0] = (uriSplit.length==1) ? "portal" : uriSplit[0];
        uriSplit[1] = (uriSplit[1]==null) ? "index" : uriSplit[1];

        AccountController accountController = injector.getInstance(AccountController.class);
        ModelMap modelMap = new ModelMap();
        String actionResult = accountController.hello(modelMap);
        viewConfiguration.setClassForTemplateLoading(this.getClass(), "/template/account");

        try {
            Template template = viewConfiguration.getTemplate("hello.html");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            template.process(modelMap, new OutputStreamWriter(outputStream));
            String templateContent = outputStream.toString("UTF-8");

            // httpResponse.replace(Unpooled.copiedBuffer(templateContent, CharsetUtil.UTF_8));
            httpResponse.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
            httpResponse.setStatus(HttpResponseStatus.OK);
        }
        catch(Exception e) {
            System.out.print("\n" + uriSplit[0] + "/" + uriSplit[0] + " : \n" + e.getMessage());
        }
    }
}
