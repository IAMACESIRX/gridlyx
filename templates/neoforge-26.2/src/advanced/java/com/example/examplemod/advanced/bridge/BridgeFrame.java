package com.example.examplemod.advanced.bridge;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public record BridgeFrame(int version, long requestId, String operation, byte[] payload) {
    public static final int CURRENT_VERSION = 1;
    public static final int MAX_OPERATION_BYTES = 256;
    public static final int MAX_PAYLOAD_BYTES = 16 * 1024 * 1024;

    public BridgeFrame {
        payload = Arrays.copyOf(payload, payload.length);
    }

    public void writeTo(DataOutput output) throws IOException {
        byte[] operationBytes = operation.getBytes(StandardCharsets.UTF_8);
        if (operationBytes.length > MAX_OPERATION_BYTES || payload.length > MAX_PAYLOAD_BYTES) {
            throw new IOException("Bridge frame exceeds protocol limits");
        }
        output.writeInt(version);
        output.writeLong(requestId);
        output.writeInt(operationBytes.length);
        output.write(operationBytes);
        output.writeInt(payload.length);
        output.write(payload);
    }

    public static BridgeFrame readFrom(DataInput input) throws IOException {
        int version = input.readInt();
        long requestId = input.readLong();
        int operationLength = checkedLength(input.readInt(), MAX_OPERATION_BYTES);
        byte[] operationBytes = new byte[operationLength];
        input.readFully(operationBytes);
        int payloadLength = checkedLength(input.readInt(), MAX_PAYLOAD_BYTES);
        byte[] payload = new byte[payloadLength];
        input.readFully(payload);
        return new BridgeFrame(
                version,
                requestId,
                new String(operationBytes, StandardCharsets.UTF_8),
                payload);
    }

    private static int checkedLength(int value, int maximum) throws IOException {
        if (value < 0 || value > maximum) {
            throw new IOException("Invalid bridge frame length: " + value);
        }
        return value;
    }
}
