package com.example.examplemod.advanced.polyloader;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class UnifiedAbstractionLayer {
    private final Map<Domain, OperationHandler> handlers = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public void register(Domain domain, OperationHandler handler) {
        handlers.put(Objects.requireNonNull(domain), Objects.requireNonNull(handler));
    }

    public OperationResult dispatch(
            Domain domain,
            String action,
            LoaderFamily source,
            Map<String, String> arguments,
            byte[] payload) {
        OperationHandler handler = handlers.get(domain);
        if (handler == null) {
            return new OperationResult(false, "No UAL handler registered for " + domain, new byte[0]);
        }
        UnifiedOperation operation = new UnifiedOperation(
                sequence.incrementAndGet(),
                domain,
                action,
                source,
                arguments,
                payload);
        try {
            return Objects.requireNonNull(handler.handle(operation));
        } catch (Exception | LinkageError exception) {
            return new OperationResult(false, exception.toString(), new byte[0]);
        }
    }

    public enum Domain {
        REGISTRY,
        EVENT,
        NETWORK,
        RESOURCE,
        RENDER,
        WORLD,
        INPUT,
        LIFECYCLE
    }

    @FunctionalInterface
    public interface OperationHandler {
        OperationResult handle(UnifiedOperation operation) throws Exception;
    }

    public record UnifiedOperation(
            long sequence,
            Domain domain,
            String action,
            LoaderFamily source,
            Map<String, String> arguments,
            byte[] payload) {
        public UnifiedOperation {
            action = Objects.requireNonNull(action);
            source = Objects.requireNonNull(source);
            arguments = Map.copyOf(arguments);
            payload = payload.clone();
        }

        @Override
        public byte[] payload() {
            return payload.clone();
        }
    }

    public record OperationResult(boolean success, String message, byte[] payload) {
        public OperationResult {
            message = message == null ? "" : message;
            payload = payload.clone();
        }

        @Override
        public byte[] payload() {
            return payload.clone();
        }
    }
}
