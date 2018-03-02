package com.doopp.gauss.server;

import com.doopp.gauss.rpc.controller.AccountController;
import com.doopp.gauss.rpc.service.HelloService;
import com.doopp.gauss.rpc.service.impl.HelloServiceImpl;
import com.doopp.gauss.server.netty.ApplicationChannelInboundHandler;
import com.google.inject.name.Names;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ApplicationModule extends AbstractModule {

	private String propertiesConfig;

	ApplicationModule(String propertiesConfig) {
		this.propertiesConfig = propertiesConfig;
	}

	@Override
	protected void configure() {
		// bind(HelloService.class).to(HelloServiceImpl.class);
	}

	@Provides
	public Properties applicationProperties() {
		Properties properties = new Properties();
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(propertiesConfig));
			properties.load(bufferedReader);
			return properties;
		}
		catch(IOException e) {
			return null;
		}
	}

	@Provides
	public EventLoopGroup providesEventLoopGroup() {
		return new NioEventLoopGroup();
	}
}
