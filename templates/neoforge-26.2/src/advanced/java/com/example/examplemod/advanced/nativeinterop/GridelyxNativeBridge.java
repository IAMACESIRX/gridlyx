package com.example.examplemod.advanced.nativeinterop;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Objects;
import java.util.zip.CRC32;

public final class GridelyxNativeBridge implements AutoCloseable {
    public static final int ABI_VERSION = 2;
    public static final int PROTOCOL_VERSION = 2;

    private final Arena arena = Arena.ofShared();
    private final NativeExtensionAbi abi;
    private final MethodHandle abiVersion;
    private final MethodHandle protocolVersion;
    private final MethodHandle createSharedMemory;
    private final MethodHandle openSharedMemory;
    private final MethodHandle payloadAddress;
    private final MethodHandle payloadCapacity;
    private final MethodHandle sharedSequence;
    private final MethodHandle publish;
    private final MethodHandle closeSharedMemory;
    private final MethodHandle unlinkSharedMemory;

    public GridelyxNativeBridge(Path library) {
        abi = new NativeExtensionAbi(Objects.requireNonNull(library, "library"));
        abiVersion = abi.bind("gridelyx_abi_version", FunctionDescriptor.of(ValueLayout.JAVA_INT));
        protocolVersion = abi.bind("gridelyx_protocol_version", FunctionDescriptor.of(ValueLayout.JAVA_INT));
        createSharedMemory = abi.bind(
                "gridelyx_shm_create",
                FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT));
        openSharedMemory = abi.bind(
                "gridelyx_shm_open",
                FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS));
        payloadAddress = abi.bind(
                "gridelyx_shm_payload",
                FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS));
        payloadCapacity = abi.bind(
                "gridelyx_shm_capacity",
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS));
        sharedSequence = abi.bind(
                "gridelyx_shm_sequence",
                FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS));
        publish = abi.bind(
                "gridelyx_shm_publish",
                FunctionDescriptor.of(
                        ValueLayout.JAVA_INT,
                        ValueLayout.ADDRESS,
                        ValueLayout.JAVA_INT,
                        ValueLayout.JAVA_INT,
                        ValueLayout.JAVA_INT));
        closeSharedMemory = abi.bind(
                "gridelyx_shm_close",
                FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));
        unlinkSharedMemory = abi.bind(
                "gridelyx_shm_unlink",
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS));

        int nativeAbi = invokeInt(abiVersion);
        int nativeProtocol = invokeInt(protocolVersion);
        if (nativeAbi != ABI_VERSION || nativeProtocol != PROTOCOL_VERSION) {
            close();
            throw new IllegalStateException(
                    "Gridelyx native ABI/protocol mismatch: abi=" + nativeAbi + ", protocol=" + nativeProtocol);
        }
    }

    public SharedMemorySession create(String name, int payloadCapacityBytes) {
        if (payloadCapacityBytes < 1) {
            throw new IllegalArgumentException("payloadCapacityBytes must be positive");
        }
        MemorySegment nativeName = arena.allocateFrom(name);
        MemorySegment handle = invokeAddress(createSharedMemory, nativeName, payloadCapacityBytes);
        return session(handle, name);
    }

    public SharedMemorySession open(String name) {
        MemorySegment nativeName = arena.allocateFrom(name);
        MemorySegment handle = invokeAddress(openSharedMemory, nativeName);
        return session(handle, name);
    }

    public boolean unlink(String name) {
        MemorySegment nativeName = arena.allocateFrom(name);
        return invokeInt(unlinkSharedMemory, nativeName) == 1;
    }

    private SharedMemorySession session(MemorySegment handle, String name) {
        if (handle.address() == 0L) {
            throw new IllegalStateException("Unable to open Gridelyx shared memory: " + name);
        }
        int capacity = invokeInt(payloadCapacity, handle);
        MemorySegment address = invokeAddress(payloadAddress, handle);
        if (capacity < 1 || address.address() == 0L) {
            invokeVoid(closeSharedMemory, handle);
            throw new IllegalStateException("Gridelyx shared-memory handle has no payload region");
        }
        return new SharedMemorySession(handle, address.reinterpret(capacity), capacity);
    }

    @Override
    public void close() {
        abi.close();
        arena.close();
    }

    public final class SharedMemorySession implements AutoCloseable {
        private MemorySegment handle;
        private final MemorySegment payload;
        private final int capacity;

        private SharedMemorySession(MemorySegment handle, MemorySegment payload, int capacity) {
            this.handle = handle;
            this.payload = payload;
            this.capacity = capacity;
        }

        public int capacity() {
            return capacity;
        }

        public long sequence() {
            ensureOpen();
            return invokeLong(sharedSequence, handle);
        }

        public long publish(int type, byte[] frame) {
            ensureOpen();
            Objects.requireNonNull(frame, "frame");
            if (frame.length > capacity) {
                throw new IllegalArgumentException("frame exceeds Gridelyx shared-memory capacity");
            }

            ByteBuffer view = payload.asByteBuffer();
            view.clear();
            view.put(frame);
            CRC32 crc = new CRC32();
            crc.update(frame);
            int accepted = invokeInt(publish, handle, type, frame.length, (int) crc.getValue());
            if (accepted != 1) {
                throw new IllegalStateException("Native Gridelyx bridge rejected frame publication");
            }
            return sequence();
        }

        private void ensureOpen() {
            if (handle.address() == 0L) {
                throw new IllegalStateException("Gridelyx shared-memory session is closed");
            }
        }

        @Override
        public void close() {
            if (handle.address() != 0L) {
                invokeVoid(closeSharedMemory, handle);
                handle = MemorySegment.NULL;
            }
        }
    }

    private static int invokeInt(MethodHandle handle, Object... arguments) {
        return ((Number) invoke(handle, arguments)).intValue();
    }

    private static long invokeLong(MethodHandle handle, Object... arguments) {
        return ((Number) invoke(handle, arguments)).longValue();
    }

    private static MemorySegment invokeAddress(MethodHandle handle, Object... arguments) {
        return (MemorySegment) invoke(handle, arguments);
    }

    private static void invokeVoid(MethodHandle handle, Object... arguments) {
        invoke(handle, arguments);
    }

    private static Object invoke(MethodHandle handle, Object... arguments) {
        try {
            return handle.invokeWithArguments(arguments);
        } catch (Throwable failure) {
            throw new IllegalStateException("Gridelyx native call failed", failure);
        }
    }
}
