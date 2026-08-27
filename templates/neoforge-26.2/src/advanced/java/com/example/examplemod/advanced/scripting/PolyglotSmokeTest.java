package com.example.examplemod.advanced.scripting;

import org.graalvm.polyglot.Context;

public final class PolyglotSmokeTest {
    private PolyglotSmokeTest() {}

    public static void main(String[] arguments) {
        try (Context context = Context.create("js")) {
            int answer = context.eval("js", "40 + 2").asInt();
            if (answer != 42) {
                throw new IllegalStateException("Unexpected GraalJS result: " + answer);
            }
        }
        System.out.println("PASS: GraalVM polyglot smoke test");
    }
}
