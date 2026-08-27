package com.example.examplemod.advanced.profiling;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import jdk.jfr.Recording;

public final class JfrProfiler implements AutoCloseable {
    private Recording recording;

    public synchronized void start(String name) {
        if (recording != null) {
            throw new IllegalStateException("A JFR recording is already active");
        }
        recording = new Recording();
        recording.setName(name);
        recording.enable("jdk.CPULoad").withPeriod(Duration.ofSeconds(1));
        recording.enable("jdk.GarbageCollection");
        recording.enable("jdk.JavaMonitorWait");
        recording.start();
    }

    public synchronized void dump(Path destination) throws IOException {
        if (recording == null) {
            throw new IllegalStateException("No active JFR recording");
        }
        recording.dump(destination);
    }

    public synchronized void stop(Path destination) throws IOException {
        if (recording == null) {
            return;
        }
        recording.stop();
        recording.dump(destination);
        recording.close();
        recording = null;
    }

    @Override
    public synchronized void close() {
        if (recording != null) {
            recording.close();
            recording = null;
        }
    }
}
