package com.doopp.gauss.server.netty;

import com.doopp.gauss.api.controller.AccountController;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class NettyModule extends AbstractModule {

	private final String hostname;
	private final int port;
	
	public NettyModule(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}

	@Override
	protected void configure() {
		// bind(AccountController.class);
		// bind(NettyServer.class);
	}

	@Provides
	public AccountController accountController() {
		return new AccountController();
	}

	@Provides
	public SocketAddress provideSocketAddress() {
		return new InetSocketAddress(hostname, port);
	}

	@Provides
	public EventLoopGroup providesEventLoopGroup() {
		return new NioEventLoopGroup();
	}

	@Provides
	public ChannelInitializer<SocketChannel> provideChannelInit(final HttpServerInboundHandler serverHandler) {
		return new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				// server端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
				ch.pipeline().addLast(new HttpResponseEncoder());
				// server端接收到的是httpRequest，所以要使用HttpRequestDecoder进行解码
				ch.pipeline().addLast(new HttpRequestDecoder());
				//
				ch.pipeline().addLast(serverHandler);
			}
		};
	}
}
