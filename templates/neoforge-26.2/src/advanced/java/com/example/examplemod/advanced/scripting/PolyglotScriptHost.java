package com.example.examplemod.advanced.scripting;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

public final class PolyglotScriptHost implements AutoCloseable {
    private final Map<String, Context> contexts = new HashMap<>();

    public synchronized Value reload(String moduleId, String language, Path script) throws IOException {
        Context next = Context.newBuilder(language)
                .allowHostAccess(HostAccess.NONE)
                .allowHostClassLookup(name -> false)
                .build();
        Source source = Source.newBuilder(language, script.toFile()).name(moduleId).build();
        try {
            Value result = next.eval(source);
            Context previous = contexts.put(moduleId, next);
            if (previous != null) {
                previous.close(true);
            }
            return result;
        } catch (RuntimeException exception) {
            next.close(true);
            throw exception;
        }
    }

    public synchronized void unload(String moduleId) {
        Context context = contexts.remove(moduleId);
        if (context != null) {
            context.close(true);
        }
    }

    @Override
    public synchronized void close() {
        for (Context context : contexts.values()) {
            context.close(true);
        }
        contexts.clear();
    }
}
