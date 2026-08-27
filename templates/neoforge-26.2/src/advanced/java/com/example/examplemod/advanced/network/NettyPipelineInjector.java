package com.example.examplemod.advanced.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import java.util.concurrent.CompletableFuture;

public final class NettyPipelineInjector {
    private NettyPipelineInjector() {}

    public static CompletableFuture<Void> addBefore(Channel channel, String anchor, String id, ChannelHandler handler) {
        validateId(id);
        CompletableFuture<Void> result = new CompletableFuture<>();
        channel.eventLoop().execute(() -> {
            try {
                ChannelPipeline pipeline = channel.pipeline();
                if (pipeline.get(id) != null) {
                    throw new IllegalStateException("pipeline handler already exists: " + id);
                }
                if (anchor != null && pipeline.get(anchor) != null) {
                    pipeline.addBefore(anchor, id, handler);
                } else {
                    pipeline.addLast(id, handler);
                }
                result.complete(null);
            } catch (Throwable throwable) {
                result.completeExceptionally(throwable);
            }
        });
        return result;
    }

    public static CompletableFuture<Void> remove(Channel channel, String id) {
        validateId(id);
        CompletableFuture<Void> result = new CompletableFuture<>();
        channel.eventLoop().execute(() -> {
            try {
                if (channel.pipeline().get(id) != null) {
                    channel.pipeline().remove(id);
                }
                result.complete(null);
            } catch (Throwable throwable) {
                result.completeExceptionally(throwable);
            }
        });
        return result;
    }

    private static void validateId(String id) {
        if (id == null || !id.matches("[A-Za-z0-9_.-]{1,80}")) {
            throw new IllegalArgumentException("invalid pipeline handler id");
        }
    }
}
