package com.example.examplemod.advanced.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public final class DevWebEndpoint implements AutoCloseable {
    private final EventLoopGroup boss = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
    private final EventLoopGroup workers = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
    private final RequestHandler handler;
    private Channel channel;

    public DevWebEndpoint(RequestHandler handler) {
        this.handler = handler;
    }

    public synchronized int start(int port) throws InterruptedException {
        if (channel != null) {
            throw new IllegalStateException("Development web endpoint is already running");
        }
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(boss, workers)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socket) {
                        socket.pipeline().addLast(new HttpServerCodec());
                        socket.pipeline().addLast(new HttpObjectAggregator(1024 * 1024));
                        socket.pipeline().addLast(new RequestChannelHandler(handler));
                    }
                });
        InetSocketAddress bindAddress = new InetSocketAddress(InetAddress.getLoopbackAddress(), port);
        channel = bootstrap.bind(bindAddress).sync().channel();
        return ((InetSocketAddress) channel.localAddress()).getPort();
    }

    @Override
    public synchronized void close() {
        if (channel != null) {
            channel.close();
            channel = null;
        }
        boss.shutdownGracefully();
        workers.shutdownGracefully();
    }

    @FunctionalInterface
    public interface RequestHandler {
        Response handle(String method, String uri, byte[] body);
    }

    public record Response(int status, String contentType, byte[] body) {
        public static Response text(int status, String body) {
            return new Response(status, "text/plain; charset=utf-8", body.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static final class RequestChannelHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
        private final RequestHandler handler;

        private RequestChannelHandler(RequestHandler handler) {
            this.handler = handler;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext context, FullHttpRequest request) {
            byte[] body = new byte[request.content().readableBytes()];
            request.content().getBytes(request.content().readerIndex(), body);
            Response response = handler.handle(request.method().name(), request.uri(), body);
            var payload = Unpooled.wrappedBuffer(response.body());
            var http = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.valueOf(response.status()),
                    payload);
            http.headers().set(HttpHeaderNames.CONTENT_TYPE, response.contentType());
            http.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, payload.readableBytes());
            context.writeAndFlush(http);
        }
    }
}
