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

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.slf4j.LoggerFactory;

public class NettyServer {

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private final Provider<ChannelInitializer<SocketChannel>> channelInitialer;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final SocketAddress socketAddress;

    @Inject
    public NettyServer(Provider<ChannelInitializer<SocketChannel>> channelInitialer, EventLoopGroup bossgroup, EventLoopGroup workergroup, SocketAddress sa) throws Exception {
        this.channelInitialer = channelInitialer;
        this.bossGroup = bossgroup;
        this.workerGroup = workergroup;
        this.socketAddress = sa;
    }

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

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(socketAddress).sync();

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            System.out.println("Finally shutdown gracefully");
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
