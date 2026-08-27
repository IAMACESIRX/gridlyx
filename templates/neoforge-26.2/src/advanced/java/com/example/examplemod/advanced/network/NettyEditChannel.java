package com.example.examplemod.advanced.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.Objects;
import java.util.function.Consumer;

public final class NettyEditChannel {
    public static final String HANDLER_NAME = "madk-world-edit";

    private final EditPacketCodec codec = new EditPacketCodec();

    public void install(Channel channel, Consumer<EditPacket> consumer) {
        Objects.requireNonNull(channel);
        Objects.requireNonNull(consumer);
        channel.eventLoop().execute(() -> {
            if (channel.pipeline().get(HANDLER_NAME) == null) {
                channel.pipeline().addLast(HANDLER_NAME, new Handler(codec, consumer));
            }
        });
    }

    public void uninstall(Channel channel) {
        channel.eventLoop().execute(() -> {
            if (channel.pipeline().get(HANDLER_NAME) != null) {
                channel.pipeline().remove(HANDLER_NAME);
            }
        });
    }

    private static final class Handler extends SimpleChannelInboundHandler<ByteBuf> {
        private final EditPacketCodec codec;
        private final Consumer<EditPacket> consumer;

        private Handler(EditPacketCodec codec, Consumer<EditPacket> consumer) {
            this.codec = codec;
            this.consumer = consumer;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext context, ByteBuf message) {
            message.markReaderIndex();
            try {
                consumer.accept(codec.decode(message));
            } catch (IllegalArgumentException exception) {
                message.resetReaderIndex();
                context.fireChannelRead(message.retain());
            }
        }
    }
}
