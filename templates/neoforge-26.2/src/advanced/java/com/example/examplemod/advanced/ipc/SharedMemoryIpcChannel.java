package com.example.examplemod.advanced.ipc;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.CRC32;

public final class SharedMemoryIpcChannel implements AutoCloseable {
    private static final int HEADER_BYTES = Integer.BYTES * 2;
    private final FileChannel channel;
    private final MappedByteBuffer memory;
    private final int capacity;

    public SharedMemoryIpcChannel(Path file, int payloadCapacity) throws IOException {
        if (payloadCapacity < 1) {
            throw new IllegalArgumentException("payloadCapacity must be positive");
        }
        capacity = payloadCapacity;
        channel = FileChannel.open(file, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
        channel.truncate((long) HEADER_BYTES + payloadCapacity);
        memory = channel.map(FileChannel.MapMode.READ_WRITE, 0, (long) HEADER_BYTES + payloadCapacity);
    }

    public synchronized void write(byte[] payload) {
        if (payload.length > capacity) {
            throw new IllegalArgumentException("payload exceeds shared-memory capacity");
        }
        CRC32 crc = new CRC32();
        crc.update(payload);
        memory.position(HEADER_BYTES);
        memory.put(payload);
        memory.putInt(4, (int) crc.getValue());
        memory.putInt(0, payload.length);
    }

    public synchronized byte[] read() {
        int length = memory.getInt(0);
        if (length < 0 || length > capacity) {
            throw new IllegalStateException("corrupt shared-memory length");
        }
        byte[] payload = new byte[length];
        ByteBuffer view = memory.duplicate();
        view.position(HEADER_BYTES);
        view.get(payload);
        CRC32 crc = new CRC32();
        crc.update(payload);
        if ((int) crc.getValue() != memory.getInt(4)) {
            throw new IllegalStateException("shared-memory checksum mismatch");
        }
        return payload;
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
