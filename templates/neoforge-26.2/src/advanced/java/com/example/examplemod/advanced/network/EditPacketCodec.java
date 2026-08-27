package com.example.examplemod.advanced.network;

import io.netty.buffer.ByteBuf;

public final class EditPacketCodec {
    private static final int MAGIC = 0x4d41444b;
    private static final int VERSION = 1;
    private static final int MAX_PAYLOAD = 4 * 1024 * 1024;

    public void encode(ByteBuf buffer, EditPacket packet) {
        byte[] payload = packet.payload();
        if (payload.length > MAX_PAYLOAD) {
            throw new IllegalArgumentException("Edit packet payload exceeds limit");
        }
        buffer.writeInt(MAGIC);
        buffer.writeInt(VERSION);
        buffer.writeLong(packet.transactionId());
        buffer.writeInt(packet.chunkX());
        buffer.writeInt(packet.sectionY());
        buffer.writeInt(packet.chunkZ());
        buffer.writeLong(packet.baseRevision());
        buffer.writeLong(packet.newRevision());
        buffer.writeInt(payload.length);
        buffer.writeBytes(payload);
    }

    public EditPacket decode(ByteBuf buffer) {
        if (buffer.readableBytes() < 48 || buffer.readInt() != MAGIC) {
            throw new IllegalArgumentException("Invalid edit packet header");
        }
        int version = buffer.readInt();
        if (version != VERSION) {
            throw new IllegalArgumentException("Unsupported edit packet version: " + version);
        }
        long transactionId = buffer.readLong();
        int chunkX = buffer.readInt();
        int sectionY = buffer.readInt();
        int chunkZ = buffer.readInt();
        long baseRevision = buffer.readLong();
        long newRevision = buffer.readLong();
        int payloadLength = buffer.readInt();
        if (payloadLength < 0 || payloadLength > MAX_PAYLOAD || payloadLength > buffer.readableBytes()) {
            throw new IllegalArgumentException("Invalid edit payload length");
        }
        byte[] payload = new byte[payloadLength];
        buffer.readBytes(payload);
        return new EditPacket(transactionId, chunkX, sectionY, chunkZ, baseRevision, newRevision, payload);
    }
}
