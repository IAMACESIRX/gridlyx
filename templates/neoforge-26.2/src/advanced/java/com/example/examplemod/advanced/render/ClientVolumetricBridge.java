package com.example.examplemod.advanced.render;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public final class ClientVolumetricBridge {
    private final AtomicReference<VolumetricMatrixStream.Frame> pending = new AtomicReference<>();

    public void accept(VolumetricMatrixStream.Frame frame) {
        pending.set(Objects.requireNonNull(frame));
    }

    public VolumetricMatrixStream.Frame consumeLatest() {
        return pending.getAndSet(null);
    }
}
