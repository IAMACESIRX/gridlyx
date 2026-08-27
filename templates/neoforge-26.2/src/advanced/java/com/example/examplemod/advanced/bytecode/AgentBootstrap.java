package com.example.examplemod.advanced.bytecode;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;

public final class AgentBootstrap {
    private static final BytecodeTransformEngine ENGINE = new BytecodeTransformEngine();
    private static volatile Instrumentation instrumentation;

    private AgentBootstrap() {}

    public static void premain(String arguments, Instrumentation inst) {
        install(inst);
    }

    public static void agentmain(String arguments, Instrumentation inst) {
        install(inst);
    }

    private static synchronized void install(Instrumentation inst) {
        if (instrumentation == null) {
            instrumentation = inst;
            inst.addTransformer(ENGINE, inst.isRetransformClassesSupported());
        }
    }

    public static BytecodeTransformEngine engine() {
        return ENGINE;
    }

    public static boolean isInstalled() {
        return instrumentation != null;
    }

    public static void redefine(Class<?> target, byte[] classBytes) throws Exception {
        Instrumentation inst = instrumentation;
        if (inst == null || !inst.isRedefineClassesSupported()) {
            throw new IllegalStateException("Instrumentation redefine support is unavailable");
        }
        inst.redefineClasses(new ClassDefinition(target, classBytes));
    }
}
