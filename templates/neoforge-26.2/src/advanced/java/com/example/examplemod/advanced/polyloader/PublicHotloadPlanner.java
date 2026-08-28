package com.example.examplemod.advanced.polyloader;

import java.util.Objects;

public final class PublicHotloadPlanner {
    public ActivationPlan plan(ModArtifactProfile profile) {
        Objects.requireNonNull(profile, "profile");

        if (profile.nativeLibraries()) {
            return new ActivationPlan(
                    HotloadBand.H6_EPOCH,
                    ActivationStrategy.RUNTIME_EPOCH_HANDOFF,
                    "Native libraries may be process-rooted; stage the new runtime graph in a successor epoch.");
        }
        if (profile.transformationService() || profile.mixins() || profile.accessWideners()) {
            return new ActivationPlan(
                    HotloadBand.H6_EPOCH,
                    ActivationStrategy.RUNTIME_EPOCH_HANDOFF,
                    "Early bytecode/lifecycle history must be rebuilt or satisfied by a validated engine patch.");
        }
        return switch (profile.recommendedMode()) {
            case LIVE_SAFE -> new ActivationPlan(
                    HotloadBand.H3_MODULE,
                    ActivationStrategy.CLASSLOADER_EPOCH,
                    "Replace module implementation behind Gridelyx-owned handles.");
            case EMULATED -> new ActivationPlan(
                    HotloadBand.H5_LIFECYCLE,
                    ActivationStrategy.LIFECYCLE_REPLAY,
                    "Replay translated loader lifecycle into owned UAL operations.");
            case PRELAUNCH_REQUIRED -> new ActivationPlan(
                    HotloadBand.H6_EPOCH,
                    ActivationStrategy.RUNTIME_EPOCH_HANDOFF,
                    "Build a successor runtime epoch with the required prelaunch history.");
            case UNSUPPORTED -> new ActivationPlan(
                    HotloadBand.H6_EPOCH,
                    ActivationStrategy.ISOLATED_PROCESS,
                    "No validated in-process adapter exists; contain the artifact while an adapter or epoch path is developed.");
        };
    }

    public record ActivationPlan(HotloadBand band, ActivationStrategy strategy, String rationale) {
        public ActivationPlan {
            Objects.requireNonNull(band, "band");
            Objects.requireNonNull(strategy, "strategy");
            Objects.requireNonNull(rationale, "rationale");
        }
    }
}
