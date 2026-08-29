package com.example.examplemod.advanced.polyloader;

// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/bytecode/BytecodeTransformEngine.java
import com.example.examplemod.advanced.bytecode.BytecodeTransformEngine;
import java.lang.instrument.Instrumentation;

public final class PolyloaderBootstrap {
    private static volatile State state = State.UNINITIALISED;
    private static volatile String failureMessage;

    private PolyloaderBootstrap() {}

    public static synchronized void install(
            Instrumentation instrumentation,
            BytecodeTransformEngine transformEngine) {
        if (state == State.ACTIVE) {
            return;
        }
        if (!asmAvailable()) {
            state = State.ASM_MISSING;
            failureMessage = "ASM is not visible to the prelaunch agent classloader";
            return;
        }
        try {
            PolyloaderKernel.install(instrumentation, transformEngine);
            state = State.ACTIVE;
            failureMessage = null;
        } catch (RuntimeException | LinkageError exception) {
            state = State.FAILED;
            failureMessage = exception.toString();
        }
    }

    public static State state() {
        return state;
    }

    public static String failureMessage() {
        return failureMessage;
    }

    private static boolean asmAvailable() {
        try {
            Class.forName("org.objectweb.asm.ClassReader", false, PolyloaderBootstrap.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
    }

    public enum State {
        UNINITIALISED,
        ACTIVE,
        ASM_MISSING,
        FAILED
    }
}
