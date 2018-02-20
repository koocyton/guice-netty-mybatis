package com.doopp.gauss.server.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;


public class MyServletContextListener extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {

        return Guice.createInjector(new ServletModule() {
            protected void configureServlets() {
                bind();
                serve("/helloworld").with(HelloWorldServlet.class);
            }
        });
    }
}
