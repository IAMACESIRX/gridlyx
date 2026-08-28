package com.example.examplemod.advanced.runtime;

import com.example.examplemod.advanced.polyloader.ActivationStrategy;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Target-specific callbacks used by the neutral reload orchestrator.
 *
 * <p>The runtime layer owns classification, ordering, escalation and evidence. Minecraft/loader-specific
 * integration is supplied through these bindings so target API drift does not contaminate the neutral core.
 */
public final class ReloadTargetBindings {
    private final ReloadAction dataReload;
    private final ReloadAction assetReload;
    private final ModuleReloadAction moduleReload;
    private final ReloadAction otherReload;
    private final EpochHandoffAction epochHandoff;

    private ReloadTargetBindings(Builder builder) {
        dataReload = builder.dataReload;
        assetReload = builder.assetReload;
        moduleReload = builder.moduleReload;
        otherReload = builder.otherReload;
        epochHandoff = builder.epochHandoff;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean hasDataReload() {
        return dataReload != null;
    }

    public boolean hasAssetReload() {
        return assetReload != null;
    }

    public boolean hasModuleReload() {
        return moduleReload != null;
    }

    public boolean hasOtherReload() {
        return otherReload != null;
    }

    public boolean hasEpochHandoff() {
        return epochHandoff != null;
    }

    public void reloadData(Path path, ChangeKind change) throws Exception {
        require(dataReload, "data reload").apply(path, change);
    }

    public void reloadAsset(Path path, ChangeKind change) throws Exception {
        require(assetReload, "asset reload").apply(path, change);
    }

    public ActivationStrategy reloadModule(Path path, ChangeKind change) throws Exception {
        if (moduleReload == null) {
            throw new UnsupportedOperationException("No module reload target binding is installed");
        }
        return Objects.requireNonNull(moduleReload.apply(path, change), "module activation strategy");
    }

    public void reloadOther(Path path, ChangeKind change) throws Exception {
        require(otherReload, "other reload").apply(path, change);
    }

    public void runtimeEpochHandoff(ExternalHotloadCore.ReloadEvent event, String reason) throws Exception {
        if (epochHandoff == null) {
            throw new UnsupportedOperationException("No runtime epoch handoff target binding is installed");
        }
        epochHandoff.handoff(event, reason);
    }

    private static ReloadAction require(ReloadAction action, String name) {
        if (action == null) {
            throw new UnsupportedOperationException("No " + name + " target binding is installed");
        }
        return action;
    }

    public enum ChangeKind {
        CREATE,
        MODIFY,
        DELETE,
        UNKNOWN
    }

    @FunctionalInterface
    public interface ReloadAction {
        void apply(Path path, ChangeKind change) throws Exception;
    }

    @FunctionalInterface
    public interface ModuleReloadAction {
        ActivationStrategy apply(Path path, ChangeKind change) throws Exception;
    }

    @FunctionalInterface
    public interface EpochHandoffAction {
        void handoff(ExternalHotloadCore.ReloadEvent event, String reason) throws Exception;
    }

    public static final class Builder {
        private ReloadAction dataReload;
        private ReloadAction assetReload;
        private ModuleReloadAction moduleReload;
        private ReloadAction otherReload;
        private EpochHandoffAction epochHandoff;

        private Builder() {}

        public Builder dataReload(ReloadAction action) {
            dataReload = Objects.requireNonNull(action);
            return this;
        }

        public Builder assetReload(ReloadAction action) {
            assetReload = Objects.requireNonNull(action);
            return this;
        }

        public Builder moduleReload(ModuleReloadAction action) {
            moduleReload = Objects.requireNonNull(action);
            return this;
        }

        public Builder otherReload(ReloadAction action) {
            otherReload = Objects.requireNonNull(action);
            return this;
        }

        public Builder runtimeEpochHandoff(EpochHandoffAction action) {
            epochHandoff = Objects.requireNonNull(action);
            return this;
        }

        public ReloadTargetBindings build() {
            return new ReloadTargetBindings(this);
        }
    }
}
