package com.doopp.gauss.server.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class NettyModule extends AbstractModule {

	private String propertiesConfig;

	public NettyModule(String propertiesConfig) {
		this.propertiesConfig = propertiesConfig;
	}

	@Override
	protected void configure() {
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
