package com.example.examplemod.advanced.clientdev;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

public final class EmbeddedIdeConsole {
    private static final int MAX_HISTORY = 512;

    private final DirectJavaCompiler compiler;
    private final Deque<Entry> history = new ArrayDeque<>();

    public EmbeddedIdeConsole(DirectJavaCompiler compiler) {
        this.compiler = Objects.requireNonNull(compiler);
    }

    public synchronized DirectJavaCompiler.CompilationResult compile(String className, String source) {
        DirectJavaCompiler.CompilationResult result = compiler.compile(className, source);
        append(new Entry(Instant.now(), "compile", className, result.success()));
        return result;
    }

    public synchronized void log(String channel, String message) {
        append(new Entry(Instant.now(), channel, message, true));
    }

    public synchronized List<Entry> history() {
        return List.copyOf(new ArrayList<>(history));
    }

    private void append(Entry entry) {
        while (history.size() >= MAX_HISTORY) {
            history.removeFirst();
        }
        history.addLast(entry);
    }

    public record Entry(Instant time, String channel, String message, boolean success) {
    }
}
