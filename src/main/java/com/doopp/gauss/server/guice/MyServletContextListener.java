package com.doopp.gauss.server.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

import javax.servlet.*;
import java.util.EnumSet;


public class MyServletContextListener extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        System.out.print("\n -- \n");
        return Guice.createInjector(new ServletModule() {
        });
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        final ServletContext ctx = servletContextEvent.getServletContext();

        FilterRegistration.Dynamic encodingFilter = ctx.addFilter("", "");
        encodingFilter.setInitParameter("encoding", "UTF-8");
        encodingFilter.setInitParameter("forceEncoding", "true");
        encodingFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");

        // session filter
        //FilterRegistration.Dynamic sessionFilter = ctx.addFilter("sessionFilter", SessionFilter.class);
        //sessionFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");

        super.contextInitialized(servletContextEvent);
    }
}
