package com.example.examplemod.advanced.scripting;

// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/validation/BedrockBridgeSmokeTest.java
import com.example.examplemod.advanced.validation.BedrockBridgeSmokeTest;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/validation/GridelyxSmokeTest.java
import com.example.examplemod.advanced.validation.GridelyxSmokeTest;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/validation/ProductionSmokeTest.java
import com.example.examplemod.advanced.validation.ProductionSmokeTest;
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
