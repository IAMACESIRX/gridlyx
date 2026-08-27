package com.example.examplemod.advanced.clientdev;

import com.example.examplemod.advanced.bytecode.AgentBootstrap;
import java.util.Map;
import java.util.Objects;

public final class LiveCompilationGateway {
    private final DirectJavaCompiler compiler;

    public LiveCompilationGateway(DirectJavaCompiler compiler) {
        this.compiler = Objects.requireNonNull(compiler);
    }

    public LiveResult compileAndActivate(String className, String source, ClassLoader parent) {
        DirectJavaCompiler.CompilationResult compilation = compiler.compile(className, source);
        if (!compilation.success()) {
            return new LiveResult(false, null, compilation.diagnostics(), Activation.NONE);
        }
        byte[] primary = compilation.classes().get(className);
        if (primary == null) {
            return new LiveResult(false, null, ListSupport.single("Primary class was not emitted"), Activation.NONE);
        }
        try {
            Class<?> existing = Class.forName(className, false, parent);
            if (!AgentBootstrap.isInstalled()) {
                return new LiveResult(false, existing, ListSupport.single("Instrumentation agent is not installed"),
                        Activation.NONE);
            }
            AgentBootstrap.redefine(existing, primary);
            return new LiveResult(true, existing, compilation.diagnostics(), Activation.REDEFINED);
        } catch (ClassNotFoundException ignored) {
            try {
                MemoryClassLoader loader = new MemoryClassLoader(parent, compilation.classes());
                Class<?> loaded = loader.loadClass(className);
                return new LiveResult(true, loaded, compilation.diagnostics(), Activation.ISOLATED_CLASSLOADER);
            } catch (ClassNotFoundException exception) {
                return new LiveResult(false, null, ListSupport.single(exception.toString()), Activation.NONE);
            }
        } catch (Exception exception) {
            return new LiveResult(false, null, ListSupport.single(exception.toString()), Activation.NONE);
        }
    }

    public enum Activation {
        NONE,
        REDEFINED,
        ISOLATED_CLASSLOADER
    }

    public record LiveResult(
            boolean success,
            Class<?> activatedClass,
            java.util.List<String> diagnostics,
            Activation activation) {
        public LiveResult {
            diagnostics = java.util.List.copyOf(diagnostics);
        }
    }

    private static final class MemoryClassLoader extends ClassLoader {
        private final Map<String, byte[]> classes;

        private MemoryClassLoader(ClassLoader parent, Map<String, byte[]> classes) {
            super(parent);
            this.classes = Map.copyOf(classes);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            byte[] bytes = classes.get(name);
            if (bytes == null) {
                throw new ClassNotFoundException(name);
            }
            return defineClass(name, bytes, 0, bytes.length);
        }
    }

    private static final class ListSupport {
        private ListSupport() {
        }

        private static java.util.List<String> single(String value) {
            return java.util.List.of(value);
        }
    }
}
