package com.doopp.gauss.server.application;

import com.doopp.gauss.rpc.service.HelloService;
import com.doopp.gauss.rpc.service.impl.HelloServiceImpl;
import com.google.inject.*;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;

public class ApplicationModule extends AbstractModule {

	@Override
	public void configure() {
	    bind(HelloService.class).to(HelloServiceImpl.class);
	}

	@Singleton
	@Provides
	public ApplicationProperties applicationProperties() {
		return new ApplicationProperties();
	}

	@Provides
	public EventLoopGroup providesEventLoopGroup() {
		return new NioEventLoopGroup();
	}

}
