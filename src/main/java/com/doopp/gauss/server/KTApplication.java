package com.doopp.gauss.server;

import com.doopp.gauss.server.netty.NettyServer;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class KTApplication {

    public static void main(String[] args) throws Exception {

        String hostname = args[0];
        int port = Integer.valueOf(args[1]);
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {

            }
        });
        final NettyServer server = injector.getInstance(NettyServer.class);
        server.run();
    }
}
