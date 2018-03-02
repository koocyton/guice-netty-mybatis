package com.doopp.gauss.server.application;

import com.doopp.gauss.rpc.service.HelloService;
import com.doopp.gauss.rpc.service.impl.HelloServiceImpl;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ApplicationModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(HelloService.class).to(HelloServiceImpl.class);
	}

	@Provides
	public ApplicationProperties applicationProperties() {
		return new ApplicationProperties();
	}

	@Provides
	public EventLoopGroup providesEventLoopGroup() {
		return new NioEventLoopGroup();
	}
}
