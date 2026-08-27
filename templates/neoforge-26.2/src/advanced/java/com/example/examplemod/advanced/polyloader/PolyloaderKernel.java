package com.example.examplemod.advanced.polyloader;

import com.example.examplemod.advanced.bytecode.BytecodeTransformEngine;
import java.lang.instrument.Instrumentation;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class PolyloaderKernel {
    private static volatile PolyloaderKernel current;

    private final Instrumentation instrumentation;
    private final RuntimeEnvironmentScanner environmentScanner = new RuntimeEnvironmentScanner();
    private final UnifiedAbstractionLayer abstractionLayer = new UnifiedAbstractionLayer();
    private final AdapterRegistry adapters = new AdapterRegistry();
    private final DynamicHandleScanner handleScanner = new DynamicHandleScanner();
    private final ModArtifactAnalyzer artifactAnalyzer = new ModArtifactAnalyzer();
    private final AsmInvocationTranslator invocationTranslator = new AsmInvocationTranslator();
    private final AtomicBoolean transformerInstalled = new AtomicBoolean();
    private final SideloadContainer sideloadContainer;
    private volatile RuntimeEnvironment environment;

    private PolyloaderKernel(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
        environment = environmentScanner.scan(instrumentation);
        sideloadContainer = new SideloadContainer(
                artifactAnalyzer,
                adapters,
                abstractionLayer,
                environment,
                ClassLoader.getSystemClassLoader());
    }

    public static synchronized PolyloaderKernel install(
            Instrumentation instrumentation,
            BytecodeTransformEngine transformEngine) {
        if (current == null) {
            current = new PolyloaderKernel(instrumentation);
        }
        current.installTransformer(transformEngine);
        return current;
    }

    public static PolyloaderKernel current() {
        PolyloaderKernel kernel = current;
        if (kernel == null) {
            throw new IllegalStateException("Polyloader kernel is not installed");
        }
        return kernel;
    }

    private void installTransformer(BytecodeTransformEngine transformEngine) {
        if (transformerInstalled.compareAndSet(false, true)) {
            transformEngine.addRule(PolyloaderKernel::shouldTransform, this::translateClass);
        }
    }

    private byte[] translateClass(String className, byte[] classBytes) {
        try {
            List<CallTranslationRule> rules = adapters.translationRules(environment);
            return invocationTranslator.translate(classBytes, rules);
        } catch (RuntimeException | LinkageError ignored) {
            return classBytes;
        }
    }

    private static boolean shouldTransform(String className) {
        return !className.startsWith("java/")
                && !className.startsWith("javax/")
                && !className.startsWith("jdk/")
                && !className.startsWith("sun/")
                && !className.startsWith("org/objectweb/asm/")
                && !className.startsWith("com/example/examplemod/advanced/polyloader/");
    }

    public RuntimeEnvironment refreshEnvironment() {
        environment = environmentScanner.scan(instrumentation);
        return environment;
    }

    public RuntimeEnvironment environment() {
        return environment;
    }

    public UnifiedAbstractionLayer abstractionLayer() {
        return abstractionLayer;
    }

    public AdapterRegistry adapters() {
        return adapters;
    }

    public DynamicHandleScanner handleScanner() {
        return handleScanner;
    }

    public SideloadContainer sideloadContainer() {
        return sideloadContainer;
    }
}
