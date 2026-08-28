package com.example.examplemod.advanced.bedrock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.CRC32;

public final class BedrockBridgeCodec {
    public static final int MAGIC = 0x474C5842; // GLXB
    public static final short VERSION = 2;
    private static final int HEADER_BYTES = Integer.BYTES
            + Short.BYTES
            + Short.BYTES
            + Long.BYTES
            + Integer.BYTES
            + Integer.BYTES;

    public byte[] encode(BedrockBridgeFrame frame) {
        byte[] payload = frame.payload();
        CRC32 crc = new CRC32();
        crc.update(payload);
        ByteBuffer output = ByteBuffer.allocate(HEADER_BYTES + payload.length).order(ByteOrder.BIG_ENDIAN);
        output.putInt(MAGIC);
        output.putShort(VERSION);
        output.putShort((short) frame.type().code());
        output.putLong(frame.sequence());
        output.putInt(payload.length);
        output.putInt((int) crc.getValue());
        output.put(payload);
        return output.array();
    }

    public BedrockBridgeFrame decode(byte[] encoded) {
        if (encoded.length < HEADER_BYTES) {
            throw new IllegalArgumentException("Gridelyx Bedrock frame is shorter than its header");
        }
        ByteBuffer input = ByteBuffer.wrap(encoded).order(ByteOrder.BIG_ENDIAN);
        int magic = input.getInt();
        short version = input.getShort();
        int typeCode = Short.toUnsignedInt(input.getShort());
        long sequence = input.getLong();
        int payloadLength = input.getInt();
        int expectedCrc = input.getInt();
        if (magic != MAGIC || version != VERSION) {
            throw new IllegalArgumentException("Unsupported Gridelyx Bedrock bridge envelope");
        }
        if (payloadLength < 0 || payloadLength != input.remaining()) {
            throw new IllegalArgumentException("Invalid Gridelyx Bedrock payload length");
        }

        byte[] payload = new byte[payloadLength];
        input.get(payload);
        CRC32 crc = new CRC32();
        crc.update(payload);
        if ((int) crc.getValue() != expectedCrc) {
            throw new IllegalArgumentException("Gridelyx Bedrock payload checksum mismatch");
        }
        return new BedrockBridgeFrame(sequence, BedrockBridgeFrame.Type.fromCode(typeCode), payload);
    }
}
