package com.example.examplemod.advanced.bedrock;

import com.example.examplemod.advanced.nativeinterop.GridelyxNativeBridge;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public final class BedrockNativeSession implements AutoCloseable {
    private final BedrockBridgeCodec codec = new BedrockBridgeCodec();
    private final GridelyxNativeBridge.SharedMemorySession sharedMemory;
    private final AtomicLong sequence = new AtomicLong();

    public BedrockNativeSession(GridelyxNativeBridge.SharedMemorySession sharedMemory) {
        this.sharedMemory = Objects.requireNonNull(sharedMemory, "sharedMemory");
    }

    public PublishedFrame publish(BedrockBridgeFrame.Type type, byte[] payload) {
        Objects.requireNonNull(type, "type");
        long logicalSequence = sequence.incrementAndGet();
        BedrockBridgeFrame frame = new BedrockBridgeFrame(logicalSequence, type, payload);
        byte[] encoded = codec.encode(frame);
        long transportSequence = sharedMemory.publish(type.code(), encoded);
        return new PublishedFrame(logicalSequence, transportSequence, encoded.length);
    }

    public int capacity() {
        return sharedMemory.capacity();
    }

    @Override
    public void close() {
        sharedMemory.close();
    }

    public record PublishedFrame(long logicalSequence, long transportSequence, int encodedBytes) {}
}
