package com.example.examplemod.advanced.runtime;

import com.example.examplemod.advanced.bytecode.AgentBootstrap;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ClassHotSwapService {
    public RedefinitionResult redefine(Path classesRoot, Path classFile, ClassLoader loader) {
        try {
            if (!AgentBootstrap.isInstalled()) {
                return new RedefinitionResult(false, null, true, "Instrumentation agent is not installed");
            }
            String className = className(classesRoot, classFile);
            Class<?> target = Class.forName(className, false, loader);
            if (!AgentBootstrap.instrumentation().isModifiableClass(target)) {
                return new RedefinitionResult(false, className, true, "Loaded class is not modifiable");
            }
            byte[] bytecode = Files.readAllBytes(classFile);
            AgentBootstrap.redefine(target, bytecode);
            return new RedefinitionResult(true, className, false, null);
        } catch (UnsupportedOperationException | LinkageError exception) {
            return new RedefinitionResult(false, null, true, exception.toString());
        } catch (Exception exception) {
            return new RedefinitionResult(false, null, requiresEpochHandoff(exception), exception.toString());
        }
    }

    private static boolean requiresEpochHandoff(Exception exception) {
        return exception instanceof java.lang.instrument.UnmodifiableClassException
                || exception instanceof ClassNotFoundException
                || exception instanceof IllegalStateException;
    }

    private static String className(Path classesRoot, Path classFile) throws IOException {
        Path root = classesRoot.toRealPath();
        Path file = classFile.toRealPath();
        if (!file.startsWith(root) || !file.getFileName().toString().endsWith(".class")) {
            throw new IOException("Class file is outside the approved classes root");
        }
        String relative = root.relativize(file).toString();
        return relative.substring(0, relative.length() - ".class".length())
                .replace('/', '.')
                .replace('\\', '.');
    }

    public record RedefinitionResult(
            boolean success,
            String className,
            boolean requiresEpochHandoff,
            String error) {}
}
