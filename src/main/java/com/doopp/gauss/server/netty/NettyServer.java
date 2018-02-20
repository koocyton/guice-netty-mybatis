package com.doopp.gauss.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class NettyServer {

	private static final Logger logger = Logger.getLogger(NettyServer.class.getName());

	@Inject
	private Provider<ChannelInitializer<SocketChannel>> channelInitialer;

	@Inject
	private EventLoopGroup bossGroup;

	@Inject
	private EventLoopGroup workerGroup;

	@Inject
	private SocketAddress socketAddress;
	
	public void run() throws Exception {
		int port = ((InetSocketAddress)socketAddress).getPort();
		String host = ((InetSocketAddress)socketAddress).getHostString();
		logger.info("Running ServerBootstrap on "+host+":"+port);
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
			 .channel(NioServerSocketChannel.class)
					.childHandler(channelInitialer.get())
				.option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true);

			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		}
		finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
    }
	
}
