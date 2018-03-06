package com.doopp.gauss.server.application;

import com.google.inject.AbstractModule;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;

import com.google.inject.Provides;

public class ApplicationModule extends AbstractModule {

	@Override
	protected void configure() {
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
