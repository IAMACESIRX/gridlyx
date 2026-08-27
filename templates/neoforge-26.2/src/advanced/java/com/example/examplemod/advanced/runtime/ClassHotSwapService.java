package com.example.examplemod.advanced.runtime;

import com.example.examplemod.advanced.bytecode.AgentBootstrap;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ClassHotSwapService {
    public RedefinitionResult redefine(Path classesRoot, Path classFile, ClassLoader loader) {
        try {
            String className = className(classesRoot, classFile);
            Class<?> target = Class.forName(className, false, loader);
            byte[] bytecode = Files.readAllBytes(classFile);
            AgentBootstrap.redefine(target, bytecode);
            return new RedefinitionResult(true, className, null);
        } catch (Exception exception) {
            return new RedefinitionResult(false, null, exception.toString());
        }
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

    public record RedefinitionResult(boolean success, String className, String error) {}
}
