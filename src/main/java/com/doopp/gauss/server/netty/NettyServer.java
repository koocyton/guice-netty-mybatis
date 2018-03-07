package com.doopp.gauss.server.netty;

import com.doopp.gauss.server.application.ApplicationHandler;
import com.doopp.gauss.server.application.ApplicationProperties;
import com.doopp.gauss.server.application.WebSocketFrameHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import com.google.inject.Inject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtensionHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class NettyServer {

	private final Logger logger = LoggerFactory.getLogger(NettyServer.class);

	@Inject
	private EventLoopGroup bossGroup;

	@Inject
	private EventLoopGroup workerGroup;

	@Inject
	private ApplicationProperties applicationProperties;

	@Inject
	private ApplicationHandler applicationHandler;

	public void run() throws Exception {
		String host = applicationProperties.s("server.host");
		int port = applicationProperties.i("server.port");

		try {
			ServerBootstrap b = new ServerBootstrap();

			b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(channelInitializer())
				.option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true);
			ChannelFuture f = b.bind(host, port).sync();

			logger.info("Running ServerBootstrap on " + host +":" + port);

			f.channel().closeFuture().sync();
		}
		finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
    }

    // http://blog.csdn.net/kkkloveyou/article/details/44656325
	// http://blog.csdn.net/joeyon1985/article/details/53586004
	private ChannelInitializer channelInitializer() {
		return new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {

				ChannelPipeline pipeline = ch.pipeline();

				// 设置30秒没有读到数据，则触发一个READER_IDLE事件。
				// pipeline.addLast(new IdleStateHandler(30, 0, 0));

				// HttpServerCodec：将请求和应答消息解码为HTTP消息
				pipeline.addLast(new HttpServerCodec());

				// HttpObjectAggregator：将HTTP消息的多个部分合成一条完整的HTTP消息
				pipeline.addLast(new HttpObjectAggregator(65536));

				// pipeline.addLast(new ChunkedWriteHandler());

				// http
				pipeline.addLast(applicationHandler);

				// webSocket connect
				pipeline.addLast(new WebSocketServerProtocolHandler("/abc"));

				// 在管道中添加我们自己的接收数据实现方法
				pipeline.addLast(new WebSocketFrameHandler());
			}
		};
	}
}
