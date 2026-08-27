package com.example.examplemod.advanced.runtime;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class ExternalHotloadCore implements AutoCloseable {
    private static final long DEFAULT_MAX_FILE_SIZE = 32L * 1024L * 1024L;

    private final WatchService watchService;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Map<WatchKey, Path> watchedDirectories = new ConcurrentHashMap<>();
    private final List<Path> roots = new ArrayList<>();
    private final List<Consumer<ReloadEvent>> listeners = new ArrayList<>();
    private final Map<Path, Long> lastEventNanos = new ConcurrentHashMap<>();
    private final AtomicBoolean running = new AtomicBoolean();
    private final long maxFileSize;

    public ExternalHotloadCore() throws IOException {
        this(DEFAULT_MAX_FILE_SIZE);
    }

    public ExternalHotloadCore(long maxFileSize) throws IOException {
        if (maxFileSize <= 0) {
            throw new IllegalArgumentException("Maximum hotload file size must be positive");
        }
        this.maxFileSize = maxFileSize;
        watchService = FileSystems.getDefault().newWatchService();
    }

    public synchronized void addRoot(Path root) throws IOException {
        Path canonical = root.toAbsolutePath().normalize();
        Files.createDirectories(canonical);
        if (!roots.contains(canonical)) {
            roots.add(canonical);
            registerRecursively(canonical);
        }
    }

    public synchronized void addListener(Consumer<ReloadEvent> listener) {
        listeners.add(listener);
    }

    public void start() {
        if (running.compareAndSet(false, true)) {
            executor.submit(this::watchLoop);
        }
    }

    private void registerRecursively(Path root) throws IOException {
        try (var stream = Files.walk(root)) {
            for (Path directory : stream.filter(Files::isDirectory).toList()) {
                registerDirectory(directory);
            }
        }
    }

    private void registerDirectory(Path directory) throws IOException {
        WatchKey key = directory.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE);
        watchedDirectories.put(key, directory);
    }

    private void watchLoop() {
        while (running.get()) {
            try {
                WatchKey key = watchService.take();
                Path directory = watchedDirectories.get(key);
                if (directory != null) {
                    processKey(directory, key);
                }
                if (!key.reset()) {
                    watchedDirectories.remove(key);
                }
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                return;
            } catch (IOException exception) {
                emit(new ReloadEvent(null, ReloadKind.ERROR, Instant.now(), exception.getMessage()));
            }
        }
    }

    private void processKey(Path directory, WatchKey key) throws IOException {
        for (WatchEvent<?> rawEvent : key.pollEvents()) {
            if (rawEvent.kind() == StandardWatchEventKinds.OVERFLOW) {
                continue;
            }
            Path changed = directory.resolve((Path) rawEvent.context()).toAbsolutePath().normalize();
            if (!isUnderApprovedRoot(changed) || shouldDebounce(changed)) {
                continue;
            }
            if (rawEvent.kind() == StandardWatchEventKinds.ENTRY_CREATE && Files.isDirectory(changed)) {
                registerRecursively(changed);
            }
            if (Files.isRegularFile(changed) && Files.size(changed) > maxFileSize) {
                emit(new ReloadEvent(changed, ReloadKind.REJECTED, Instant.now(), "File exceeds hotload limit"));
                continue;
            }
            emit(new ReloadEvent(changed, classify(changed), Instant.now(), rawEvent.kind().name()));
        }
    }

    private boolean shouldDebounce(Path path) {
        long now = System.nanoTime();
        Long previous = lastEventNanos.put(path, now);
        return previous != null && now - previous < 50_000_000L;
    }

    private synchronized boolean isUnderApprovedRoot(Path path) {
        return roots.stream().anyMatch(path::startsWith);
    }

    private synchronized void emit(ReloadEvent event) {
        for (Consumer<ReloadEvent> listener : List.copyOf(listeners)) {
            listener.accept(event);
        }
    }

    private static ReloadKind classify(Path path) {
        if (path == null) {
            return ReloadKind.ERROR;
        }
        String file = path.getFileName().toString().toLowerCase();
        if (file.endsWith(".class") || file.endsWith(".jar")) {
            return ReloadKind.JAVA_BYTECODE;
        }
        if (file.endsWith(".js") || file.endsWith(".mjs") || file.endsWith(".py")) {
            return ReloadKind.SCRIPT;
        }
        if (file.endsWith(".json") || file.endsWith(".toml") || file.endsWith(".mcmeta")) {
            return ReloadKind.DATA;
        }
        if (file.endsWith(".png") || file.endsWith(".ogg") || file.endsWith(".bbmodel")) {
            return ReloadKind.ASSET;
        }
        return ReloadKind.OTHER;
    }

    @Override
    public void close() throws IOException {
        running.set(false);
        executor.shutdownNow();
        watchService.close();
    }

    public enum ReloadKind {
        JAVA_BYTECODE,
        SCRIPT,
        DATA,
        ASSET,
        OTHER,
        REJECTED,
        ERROR
    }

    public record ReloadEvent(Path path, ReloadKind kind, Instant time, String detail) {}
}
