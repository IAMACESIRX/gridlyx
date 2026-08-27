package com.example.examplemod.advanced.bedrock;

import java.util.Arrays;

public record BedrockBridgeFrame(long sequence, Type type, byte[] payload) {
    public BedrockBridgeFrame {
        if (sequence < 0L) {
            throw new IllegalArgumentException("sequence must not be negative");
        }
        payload = Arrays.copyOf(payload, payload.length);
    }

    @Override
    public byte[] payload() {
        return Arrays.copyOf(payload, payload.length);
    }

    public enum Type {
        CONTROL(1),
        UAL_OPERATION(2),
        MESH(3),
        TEXTURE_PATCH(4),
        WORLD_DELTA(5),
        TELEMETRY(6),
        SCRIPT_RESULT(7);

        private final int code;

        Type(int code) {
            this.code = code;
        }

        public int code() {
            return code;
        }

        public static Type fromCode(int code) {
            for (Type type : values()) {
                if (type.code == code) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown Gridelyx Bedrock frame type: " + code);
        }
    }
}
