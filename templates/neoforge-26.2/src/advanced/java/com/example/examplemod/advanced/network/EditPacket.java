package com.example.examplemod.advanced.network;

import java.util.Arrays;

public record EditPacket(
        long transactionId,
        int chunkX,
        int sectionY,
        int chunkZ,
        long baseRevision,
        long newRevision,
        byte[] payload) {
    public EditPacket {
        if (newRevision <= baseRevision) {
            throw new IllegalArgumentException("Invalid edit revision range");
        }
        payload = Arrays.copyOf(payload, payload.length);
    }

    @Override
    public byte[] payload() {
        return Arrays.copyOf(payload, payload.length);
    }
}
