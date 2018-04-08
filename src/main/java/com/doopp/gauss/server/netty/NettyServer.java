package com.doopp.gauss.server.netty;

import com.doopp.gauss.server.handler.Http1RequestHandler;
import com.doopp.gauss.server.application.ApplicationProperties;
import com.doopp.gauss.server.handler.HttpFileResourceHandler;
import com.doopp.gauss.server.handler.WebSocketFrameHandler;
import com.google.inject.Injector;
import com.google.inject.spi.StaticInjectionRequest;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import com.google.inject.Inject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyServer {

	@Inject
	private ApplicationProperties applicationProperties;

	@Inject
	private Injector injector;

	@Inject
	private EventLoopGroup bossGroup;

	@Inject
	private EventLoopGroup workerGroup;

	public void run() throws Exception {
		String host = applicationProperties.s("server.host");
		int port = applicationProperties.i("server.port");
		System.out.print("\n" + workerGroup);
		try {
			ServerBootstrap b = new ServerBootstrap();

			b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(channelInitializer())
				.option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true);

			System.out.print("\n Running ServerBootstrap on " + host +":" + port);

			ChannelFuture f = b.bind(host, port).sync();
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

				// 打印日志,可以看到websocket帧数据
				// pipeline.addFirst(new LoggingHandler(LogLevel.INFO));
				// HttpServerCodec：将请求和应答消息解码为HTTP消息
				pipeline.addLast(new HttpServerCodec());
				// HttpObjectAggregator：将HTTP消息的多个部分合成一条完整的HTTP消息
				pipeline.addLast(new HttpObjectAggregator(65536));
				// pipeline.addLast(new ChunkedWriteHandler());

                // http
                pipeline.addLast(new HttpFileResourceHandler());

                // http
                pipeline.addLast(new Http1RequestHandler(injector, "/game-socket"));

				// webSocket connect
				// pipeline.addLast(new WebSocketServerCompressionHandler());
				//pipeline.addLast(new WebSocketServerProtocolHandler("/game-socket", null, true));
				//pipeline.addLast(new WebSocketFrameHandler());
			}
		};
	}
}



