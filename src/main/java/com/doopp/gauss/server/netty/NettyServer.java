package com.doopp.gauss.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Properties;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.LoggerFactory;

public class NettyServer {

	private final org.slf4j.Logger logger = LoggerFactory.getLogger(NettyServer.class);

	@Inject
	private EventLoopGroup bossGroup;

	@Inject
	private EventLoopGroup workerGroup;

	@Inject
	private Properties applicationProperties;

	public void run() throws Exception {
		int port = Integer.valueOf(applicationProperties.getProperty("server.port"));
		String host = applicationProperties.getProperty("server.host");
		logger.info("Running ServerBootstrap on "+host+":"+port);
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(applicationChannelInitializer())
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

    private ChannelInitializer applicationChannelInitializer() {
		return new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				// 设置30秒没有读到数据，则触发一个READER_IDLE事件。
				// pipeline.addLast(new IdleStateHandler(30, 0, 0));
				// HttpServerCodec：将请求和应答消息解码为HTTP消息
				ch.pipeline().addLast("http-codec",new HttpServerCodec());
				// HttpObjectAggregator：将HTTP消息的多个部分合成一条完整的HTTP消息
				ch.pipeline().addLast("aggregator",new HttpObjectAggregator(65536));
				// ChunkedWriteHandler：向客户端发送HTML5文件
				ch.pipeline().addLast("http-chunked",new ChunkedWriteHandler());
				// 在管道中添加我们自己的接收数据实现方法
				ch.pipeline().addLast("handler",new ApplicationChannelInboundHandler());
			}
		};
	}
}
