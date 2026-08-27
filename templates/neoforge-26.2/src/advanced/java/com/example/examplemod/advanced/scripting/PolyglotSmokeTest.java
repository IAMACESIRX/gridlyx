package com.example.examplemod.advanced.scripting;

import com.example.examplemod.advanced.validation.BedrockBridgeSmokeTest;
import com.example.examplemod.advanced.validation.ProductionSmokeTest;
import com.example.examplemod.advanced.validation.GridelyxSmokeTest;
import org.graalvm.polyglot.Context;

public final class PolyglotSmokeTest {
    private PolyglotSmokeTest() {}

    public static void main(String[] arguments) throws Exception {
        GridelyxSmokeTest.run();
        BedrockBridgeSmokeTest.run();
        ProductionSmokeTest.run();
        try (Context context = Context.create("js")) {
            int answer = context.eval("js", "40 + 2").asInt();
            if (answer != 42) {
                throw new IllegalStateException("Unexpected GraalJS result: " + answer);
            }
        }
        System.out.println("PASS: GraalVM, Gridelyx Bedrock bridge and production timeline smoke tests");
    }
}
