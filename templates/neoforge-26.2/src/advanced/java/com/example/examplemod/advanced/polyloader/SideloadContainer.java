package com.example.examplemod.advanced.polyloader;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SideloadContainer implements AutoCloseable {
    private final ModArtifactAnalyzer analyzer;
    private final AdapterRegistry adapters;
    private final UnifiedAbstractionLayer abstractionLayer;
    private final RuntimeEnvironment environment;
    private final ClassLoader parent;
    private final Map<UUID, Session> sessions = new ConcurrentHashMap<>();

    public SideloadContainer(
            ModArtifactAnalyzer analyzer,
            AdapterRegistry adapters,
            UnifiedAbstractionLayer abstractionLayer,
            RuntimeEnvironment environment,
            ClassLoader parent) {
        this.analyzer = analyzer;
        this.adapters = adapters;
        this.abstractionLayer = abstractionLayer;
        this.environment = environment;
        this.parent = parent;
    }

    public Session stage(Path jar) throws IOException {
        ModArtifactProfile profile = analyzer.analyze(jar);
        UUID id = UUID.randomUUID();
        if (profile.recommendedMode() == SideloadMode.PRELAUNCH_REQUIRED
                || profile.recommendedMode() == SideloadMode.UNSUPPORTED) {
            Session session = new Session(id, profile, null, State.PRELAUNCH_REQUIRED, null);
            sessions.put(id, session);
            return session;
        }
        URL url = profile.jar().toUri().toURL();
        IsolatedModClassLoader loader = new IsolatedModClassLoader(url, parent);
        Session session = new Session(id, profile, loader, State.STAGED, null);
        sessions.put(id, session);
        return session;
    }

    public Session activate(UUID id, LoaderFamily sourceLoader) {
        Session current = requireSession(id);
        if (current.classLoader() == null) {
            return current;
        }
        LoaderAdapter adapter = adapters.adapter(sourceLoader).orElse(null);
        if (adapter == null) {
            Session failed = current.with(State.FAILED, "No loader adapter registered for " + sourceLoader);
            sessions.put(id, failed);
            return failed;
        }
        SideloadMode decision = adapter.assess(current.profile());
        if (decision == SideloadMode.PRELAUNCH_REQUIRED || decision == SideloadMode.UNSUPPORTED) {
            Session blocked = current.with(State.PRELAUNCH_REQUIRED, "Adapter requires prelaunch activation");
            sessions.put(id, blocked);
            return blocked;
        }
        try {
            adapter.activate(new SideloadContext(
                    current.profile().jar(),
                    current.profile(),
                    current.classLoader(),
                    abstractionLayer,
                    environment));
            Session active = current.with(State.ACTIVE, null);
            sessions.put(id, active);
            return active;
        } catch (Exception | LinkageError exception) {
            Session failed = current.with(State.FAILED, exception.toString());
            sessions.put(id, failed);
            return failed;
        }
    }

    public void detach(UUID id) throws IOException {
        Session session = sessions.remove(id);
        if (session != null && session.classLoader() != null) {
            session.classLoader().close();
        }
    }

    private Session requireSession(UUID id) {
        Session session = sessions.get(id);
        if (session == null) {
            throw new IllegalArgumentException("Unknown sideload session: " + id);
        }
        return session;
    }

    @Override
    public void close() throws IOException {
        IOException failure = null;
        for (UUID id : sessions.keySet()) {
            try {
                detach(id);
            } catch (IOException exception) {
                failure = exception;
            }
        }
        if (failure != null) {
            throw failure;
        }
    }

    public enum State {
        STAGED,
        ACTIVE,
        PRELAUNCH_REQUIRED,
        FAILED
    }

    public record Session(
            UUID id,
            ModArtifactProfile profile,
            IsolatedModClassLoader classLoader,
            State state,
            String message) {
        private Session with(State newState, String newMessage) {
            return new Session(id, profile, classLoader, newState, newMessage);
        }
    }
}
