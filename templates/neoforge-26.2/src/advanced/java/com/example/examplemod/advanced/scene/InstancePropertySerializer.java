package com.example.examplemod.advanced.scene;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public final class InstancePropertySerializer {
    private static final int MAX_PROPERTIES = 4096;

    public byte[] encode(Map<String, Object> properties) throws IOException {
        if (properties.size() > MAX_PROPERTIES) {
            throw new IllegalArgumentException("Too many properties");
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream data = new DataOutputStream(bytes)) {
            data.writeInt(properties.size());
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                data.writeUTF(entry.getKey());
                writeValue(data, entry.getValue());
            }
        }
        return bytes.toByteArray();
    }

    public Map<String, Object> decode(byte[] bytes) throws IOException {
        try (DataInputStream data = new DataInputStream(new ByteArrayInputStream(bytes))) {
            int size = data.readInt();
            if (size < 0 || size > MAX_PROPERTIES) {
                throw new IOException("Invalid property count");
            }
            Map<String, Object> properties = new LinkedHashMap<>();
            for (int i = 0; i < size; i++) {
                properties.put(data.readUTF(), readValue(data));
            }
            return Map.copyOf(properties);
        }
    }

    private static void writeValue(DataOutputStream data, Object value) throws IOException {
        if (value instanceof String text) {
            data.writeByte(1);
            data.writeUTF(text);
        } else if (value instanceof Integer integer) {
            data.writeByte(2);
            data.writeInt(integer);
        } else if (value instanceof Long longValue) {
            data.writeByte(3);
            data.writeLong(longValue);
        } else if (value instanceof Double doubleValue) {
            data.writeByte(4);
            data.writeDouble(doubleValue);
        } else if (value instanceof Boolean booleanValue) {
            data.writeByte(5);
            data.writeBoolean(booleanValue);
        } else {
            throw new IOException("Unsupported property type: " + value.getClass().getName());
        }
    }

    private static Object readValue(DataInputStream data) throws IOException {
        return switch (data.readUnsignedByte()) {
            case 1 -> data.readUTF();
            case 2 -> data.readInt();
            case 3 -> data.readLong();
            case 4 -> data.readDouble();
            case 5 -> data.readBoolean();
            default -> throw new IOException("Unsupported property tag");
        };
    }
}
