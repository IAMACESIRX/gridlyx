package com.example.examplemod.advanced.validation;

// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/bedrock/BedrockBridgeCodec.java
import com.example.examplemod.advanced.bedrock.BedrockBridgeCodec;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/bedrock/BedrockBridgeFrame.java
import com.example.examplemod.advanced.bedrock.BedrockBridgeFrame;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class BedrockBridgeSmokeTest {
    private BedrockBridgeSmokeTest() {}

    public static void run() {
        BedrockBridgeCodec codec = new BedrockBridgeCodec();
        byte[] payload = "gridelyx-bedrock".getBytes(StandardCharsets.UTF_8);
        BedrockBridgeFrame source = new BedrockBridgeFrame(42L, BedrockBridgeFrame.Type.MESH, payload);
        byte[] encoded = codec.encode(source);
        BedrockBridgeFrame decoded = codec.decode(encoded);
        require(decoded.sequence() == 42L, "Bedrock bridge sequence changed during codec round-trip");
        require(decoded.type() == BedrockBridgeFrame.Type.MESH, "Bedrock bridge type changed during codec round-trip");
        require(Arrays.equals(decoded.payload(), payload), "Bedrock bridge payload changed during codec round-trip");

        encoded[encoded.length - 1] ^= 0x01;
        boolean checksumRejected = false;
        try {
            codec.decode(encoded);
        } catch (IllegalArgumentException expected) {
            checksumRejected = true;
        }
        require(checksumRejected, "Bedrock bridge accepted a corrupted payload");
        System.out.println("PASS: Gridelyx Bedrock bridge smoke test");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
