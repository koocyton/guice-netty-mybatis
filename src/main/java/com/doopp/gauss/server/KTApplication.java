package com.doopp.gauss.server;

import com.doopp.gauss.server.netty.BootstrapServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KTApplication {

//    public static void main(String[] args) throws Exception {
//        // String hostname = args[0];
//        // int port = Integer.valueOf(args[1]);
//        String hostname = "127.0.0.1";
//        int port = 8090;
//        Injector injector = Guice.createInjector(new BootstrapNettyModule(hostname,port));
//        final BootstrapNettyServer server = injector.getInstance(BootstrapNettyServer.class);
//        server.run();
//    }


    private static Logger logger = LoggerFactory.getLogger(KTApplication.class);

    private void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        // server端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
                        ch.pipeline().addLast(new HttpResponseEncoder());
                        // server端接收到的是httpRequest，所以要使用HttpRequestDecoder进行解码
                        ch.pipeline().addLast(new HttpRequestDecoder());
                        ch.pipeline().addLast(new BootstrapServerHandler());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(8808).sync();

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        KTApplication server = new KTApplication();
        server.start();
    }
}
