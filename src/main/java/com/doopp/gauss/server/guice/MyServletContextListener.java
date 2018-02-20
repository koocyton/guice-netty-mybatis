package com.doopp.gauss.server.guice;

import com.doopp.gauss.api.controller.AccountController;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

import javax.servlet.*;
import java.util.EnumSet;


public class MyServletContextListener extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new ServletModule() {
            protected void configureServlets() {
                bind();
                serve("/test").with(AccountController.class.getMethod("test").getR);
            }
        });
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        final ServletContext ctx = servletContextEvent.getServletContext();

        FilterRegistration.Dynamic encodingFilter = ctx.addFilter("", "");
        encodingFilter.setInitParameter("encoding", "UTF-8");
        encodingFilter.setInitParameter("forceEncoding", "true");
        encodingFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");

        // root web application context
        ApplicationCon rootWebAppContext = new Guice();
        rootWebAppContext.register(ApplicationConfiguration.class, WebMvcConfigurer.class);
        ctx.addListener(this);

        ctx.add

        // set spring mvc dispatcher
        DispatcherServlet dispatcherServlet = new DispatcherServlet(rootWebAppContext);
        ServletRegistration.Dynamic dispatcher = ctx.addServlet("mvc-dispatcher", dispatcherServlet);
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");

        // session filter
        FilterRegistration.Dynamic sessionFilter = ctx.addFilter("sessionFilter", SessionFilter.class);
        sessionFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");

        super.contextInitialized(servletContextEvent);
    }
}
