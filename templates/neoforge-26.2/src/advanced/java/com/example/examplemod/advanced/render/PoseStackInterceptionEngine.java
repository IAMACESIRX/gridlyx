package com.example.examplemod.advanced.render;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class PoseStackInterceptionEngine {
    private final List<PoseInterceptor> interceptors = new CopyOnWriteArrayList<>();

    public void add(PoseInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    public void intercept(PoseStack stack, String stage) {
        for (PoseInterceptor interceptor : interceptors) {
            interceptor.apply(stack, stage);
        }
    }

    @FunctionalInterface
    public interface PoseInterceptor {
        void apply(PoseStack stack, String stage);
    }
}
